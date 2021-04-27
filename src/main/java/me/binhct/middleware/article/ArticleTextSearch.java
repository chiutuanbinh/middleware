package me.binhct.middleware.article;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.SortedNumericDocValuesField;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortedNumericSortField;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ArticleTextSearch {
    private static final Logger LOGGER = LoggerFactory.getLogger(ArticleTextSearch.class);
    private static AtomicLong initCounter = new AtomicLong(0l);
    public static ArticleTextSearch INSTANCE = new ArticleTextSearch();
    private StandardAnalyzer analyzer;
    private IndexWriter indexWriter;
    private SearcherManager searcherManager;
    private Directory indexDirectory;
    

    private ArticleTextSearch() {
        LOGGER.info(Long.toString(initCounter.addAndGet(1)));
        analyzer = new StandardAnalyzer();
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        try {
            Path indexPath = new File("lu").toPath();
            indexDirectory = FSDirectory.open(indexPath);
            indexWriter = new IndexWriter(indexDirectory, config);
            indexWriter.commit();
            searcherManager = new SearcherManager(indexWriter, null);
            LOGGER.info("ATS INIT");
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }

    }

    public boolean indexArticle(Article article) {
        try {
            Document doc = parse(article);
            // indexWriter.addDocument(doc);
            indexWriter.updateDocument(new Term(Article.AID, article.getAid()), doc);
            indexWriter.commit();
            searcherManager.maybeRefresh();
            return true;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }

        return false;
    }

    private static Document parse(Article article) {
        try {
            Document document = new Document();
            document.add(new Field(Article.AID, article.getAid(), TextField.TYPE_STORED));
            document.add(new Field(Article.TITLE, article.getTitle(), TextField.TYPE_STORED));
            document.add(new Field(Article.DESCRIPTION, article.getDescription(), TextField.TYPE_STORED));
            for (String para : article.getParagraph()){
                document.add(new TextField(Article.PARAGRAPH, para, Field.Store.YES));
            }
            
            document.add(new SortedNumericDocValuesField(Article.TIME_STAMP, article.getTimestamp()));
            return document;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return null;
        }
    }

    public List<Article> searchArticle(String queryString){
        List<Article> res = new ArrayList<>();
        for (Document doc : searchDocument(queryString)){
            res.add(ArticleMongoRepository.INSTANCE.getArticle(doc.get(Article.AID)));
        }
        return res;
    }

    private List<Document> searchDocument(String field, String queryString) {
        List<Document> res = new ArrayList<>();
        try {
            Query query = new TermQuery(new Term(field, queryString));
            IndexSearcher indexSearcher = searcherManager.acquire();
            TopDocs topdocs = indexSearcher.search(query, 100);
            for (ScoreDoc scoreDoc : topdocs.scoreDocs) {
                res.add(indexSearcher.doc(scoreDoc.doc));
            }
            searcherManager.release(indexSearcher);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return res;
    }

    private List<Document> searchDocument(String queryString) {
        List<Document> res = new ArrayList<>();
        try {
            res = searchWithQueryParser(queryString);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return res;
    }

    private List<Document> searchAllFieldManual(String queryString) {
        List<Document> res = new ArrayList<>();
        try {
            BooleanQuery.Builder builder = new BooleanQuery.Builder();
            TermQuery descriptionQuery = new TermQuery(new Term(Article.DESCRIPTION, queryString));
            TermQuery paragraphQuery = new TermQuery(new Term(Article.PARAGRAPH, queryString));
            builder.add(descriptionQuery, Occur.SHOULD).add(paragraphQuery, Occur.SHOULD);
            LOGGER.info(Boolean.toString(searcherManager == null));
            IndexSearcher indexSearcher = searcherManager.acquire();
            TopDocs topdocs = indexSearcher.search(builder.build(), 10);
            for (ScoreDoc scoreDoc : topdocs.scoreDocs) {
                res.add(indexSearcher.doc(scoreDoc.doc));
            }
            searcherManager.release(indexSearcher);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return res;
    }

    private List<Document> searchWithQueryParser(String queryString) {
        List<Document> res = new ArrayList<>();
        try {
            MultiFieldQueryParser queryParser = new MultiFieldQueryParser(
                    new String[] { Article.DESCRIPTION, Article.PARAGRAPH }, analyzer);
            Sort sort = new Sort(new SortedNumericSortField(Article.TIME_STAMP, SortField.Type.LONG, true));
            IndexSearcher indexSearcher = searcherManager.acquire();
            TopDocs topdocs = indexSearcher.search(queryParser.parse(queryString), 10, sort);
            for (ScoreDoc scoreDoc : topdocs.scoreDocs) {
                res.add(indexSearcher.doc(scoreDoc.doc));
            }
            searcherManager.release(indexSearcher);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return res;
    }

    private List<Document> searchWithQueryLanguage(String queryString) {
        List<Document> res = new ArrayList<>();
        try {
            IndexSearcher indexSearcher = searcherManager.acquire();
            QueryParser parser = new QueryParser(Article.PARAGRAPH, analyzer);
            String query = String.format("%s:%s OR %s:%s",
                Article.PARAGRAPH, queryString, Article.DESCRIPTION, queryString);
            TopDocs topdocs = indexSearcher.search(parser.parse(query), 10);
            for (ScoreDoc scoreDoc : topdocs.scoreDocs) {
                res.add(indexSearcher.doc(scoreDoc.doc));
            }
            searcherManager.release(indexSearcher);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return res;
    }

    public static void main(String[] args) throws Exception {
        cloneMongo();
    }

    private static void search(){
        List<Document> docs = INSTANCE.searchDocument("tuấn anh");
        for (Document doc : docs){
            LOGGER.info(doc.get(Article.AID));
        }
    }
    

    private static void cloneMongo() {
        ArticleMongoRepository repository = ArticleMongoRepository.INSTANCE;
        List<Article> oneKLatest = repository.getLatestArticles(0, 1000);
        oneKLatest.parallelStream().forEach(x -> {
            if (INSTANCE.indexArticle(x)){
                LOGGER.info("SUCCESS");
            }
        });
    }

    private static void bar() {
        ArticleTextSearch ats = ArticleTextSearch.INSTANCE;
        Article article = new Article();
        article.setTitle("YOLO");
        article.setTimestamp(1);
        List<String> para = new ArrayList<>();
        para.add("EC EC");
        article.setParagraph(para);
        ats.indexArticle(article);
        List<String> res = ats.searchDocument(Article.TITLE, "YOLO").stream().map(x->x.get(Article.AID)).collect(Collectors.toList());
        for (String doc : res) {
            LOGGER.info(doc.toString());
        }
    }

    private static void foo() throws Exception {
        Analyzer analyzer = new StandardAnalyzer();

        Path indexPath = Files.createTempDirectory("tempIndex");
        Directory directory = FSDirectory.open(indexPath);
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter iwriter = new IndexWriter(directory, config);
        Document doc = new Document();
        String text = "Một hai ba.";
        doc.add(new Field("fieldname", text, TextField.TYPE_STORED));
        Document doc2 = new Document();
        String text2 = "bốn năm sáu.";
        doc2.add(new Field("fieldname", text2, TextField.TYPE_STORED));
        Document doc3 = new Document();
        String textx = "bảy ba bốn.";
        doc3.add(new Field("fieldname", textx, TextField.TYPE_STORED));
        iwriter.addDocument(doc);
        iwriter.addDocument(doc2);
        iwriter.addDocument(doc3);
        iwriter.close();

        // Now search the index:
        DirectoryReader ireader = DirectoryReader.open(directory);
        IndexSearcher isearcher = new IndexSearcher(ireader);
        // Parse a simple query that searches for "text":
        QueryParser parser = new QueryParser("fieldname", analyzer);
        Query query = parser.parse("bốn");
        ScoreDoc[] hits = isearcher.search(query, 10).scoreDocs;
        LOGGER.info(Integer.toString(hits.length));
        // Iterate through the results:
        for (int i = 0; i < hits.length; i++) {
            Document hitDoc = isearcher.doc(hits[i].doc);
            LOGGER.info(hitDoc.get("fieldname"));
            LOGGER.info(Float.toString(hits[i].score));
        }
        ireader.close();
        directory.close();
        IOUtils.rm(indexPath);

    }
}
