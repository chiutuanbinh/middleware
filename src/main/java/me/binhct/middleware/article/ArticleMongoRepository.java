package me.binhct.middleware.article;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.result.InsertOneResult;

import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;

public class ArticleMongoRepository implements ArticleRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(ArticleMongoRepository.class);
    public static final ArticleMongoRepository INSTANCE = new ArticleMongoRepository();
    private static final String TIME_STAMP = "timestamp";
    private static final String AID = "aid";
    private static final String TITLE = "title";
    private static final String PUBLISHER = "publisher";
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
        ;
        articleCollection = mongoDb.getCollection("articles", Article.class);
        articleCollection.withCodecRegistry(pojoCodecRegistry);

    }

    @Override
    public long addArticle(Article article) {
        InsertOneResult res = articleCollection.insertOne(article);
        if (res.wasAcknowledged()) {
            LOGGER.info("article added");
            return 1;
        }
        LOGGER.info("article not added");
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
            Article article = articleCollection.find(Filters.eq(AID, id)).first();
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
    public List<Article> getLatestId(long count) {
        List<Article> res = new ArrayList<>();
        try {

            for (Article a : articleCollection.find().sort(Sorts.descending(TIME_STAMP)).limit(Math.toIntExact(count)).projection(
                    Projections.fields(Projections.include(TITLE, PUBLISHER, TIME_STAMP), Projections.excludeId()))){
                        res.add(a);
                    };

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return res;
    }

    @Override
    public List<Article> getByPublisherLatest(String publisher, long count) {
        // TODO Auto-generated method stub
        return null;
    }

}