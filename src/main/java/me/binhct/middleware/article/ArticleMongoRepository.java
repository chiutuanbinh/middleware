package me.binhct.middleware.article;

import java.util.ArrayList;
import java.util.List;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;

import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ArticleMongoRepository implements ArticleRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(ArticleMongoRepository.class);
    public static final ArticleMongoRepository INSTANCE = new ArticleMongoRepository();

    private final String URI = "mongodb://localhost:27017";
    private MongoClient mongoClient;
    private MongoDatabase mongoDb;
    private MongoCollection<Article> articleCollection;

    private ArticleMongoRepository() {
        super();
        CodecRegistry pojoCodecRegistry = CodecRegistries.fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build()));

        mongoClient = MongoClients.create(URI);
        mongoDb = mongoClient.getDatabase("mycollection").withCodecRegistry(pojoCodecRegistry);
        articleCollection = mongoDb.getCollection("articles", Article.class);
        articleCollection.withCodecRegistry(pojoCodecRegistry);

    }

    @Override
    public long addArticle(Article article) {
        Bson updateAll = Updates.combine(Updates.set(Article.AID, article.getAid()),
                Updates.set(Article.PUBLISHER, article.getPublisher()), Updates.set(Article.TITLE, article.getTitle()),
                Updates.set(Article.TIME_STAMP, article.getTimestamp()),
                Updates.set(Article.ORI_CATEGORY, article.getOriCategory()),
                Updates.set(Article.ORI_KEYWORDS, article.getOriKeywords()),
                Updates.set(Article.ORI_URL, article.getOriUrl()),
                Updates.set(Article.DESCRIPTION, article.getDescription()),
                Updates.set(Article.MEDIA_URLS, article.getMediaURLs()));
        UpdateResult res = articleCollection.updateOne(Filters.eq(Article.AID, article.getAid()), updateAll,
                new UpdateOptions().upsert(true));
        if (res.wasAcknowledged()) {
            // LOGGER.info("article added");
            return 1;
        }
        // LOGGER.info("article not added");
        return -1;
    }

    @Override
    public long addArticles(List<Article> articles) {
        long res = 1;
        for (Article article : articles) {
            res = res * addArticle(article);
        }
        return res;
    }

    @Override
    public Article getArticle(String id) {
        try {
            Article article = articleCollection.find(Filters.eq(Article.AID, id)).first();
            return article;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public List<Article> getArticles(List<String> ids) {
        List<Article> res = new ArrayList<>();
        try {
            for (String id : ids) {
                res.add(getArticle(id));
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return res;
    }

    @Override
    public List<Article> getLatestId(int count) {
        List<Article> res = new ArrayList<>();
        try {

            for (Article a : articleCollection.find().sort(Sorts.descending(Article.TIME_STAMP)).limit(count)
                    .projection(Projections.fields(Projections.include(Article.TITLE, Article.PUBLISHER, Article.TIME_STAMP, Article.DESCRIPTION, Article.MEDIA_URLS),
                            Projections.excludeId()))) {
                res.add(a);
            }
            

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return res;
    }

    @Override
    public List<Article> getByPublisherLatest(String publisher, int count) {
        List<Article> res = new ArrayList<>();
        try {
            Bson filter = Filters.eq(Article.PUBLISHER, publisher);
            Bson sort = Sorts.descending(Article.TIME_STAMP);
            Bson projection = Projections.fields(Projections.include(Article.TITLE, Article.TIME_STAMP, Article.DESCRIPTION, Article.PARAGRAPH, Article.ORI_KEYWORDS));
            for (Article a : articleCollection.find(filter).sort(sort).limit(count).projection(projection)){
                res.add(a);
            }
            return res;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public List<String> getAllPublisher() {
        List<String> result = new ArrayList<>();
        try {
            for (String publisher : articleCollection.distinct(Article.PUBLISHER, String.class)){
                result.add(publisher);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return result;
    }

}