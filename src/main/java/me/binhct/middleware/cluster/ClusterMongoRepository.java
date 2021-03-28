package me.binhct.middleware.cluster;

import java.util.ArrayList;
import java.util.List;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
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

public class ClusterMongoRepository implements ClusterRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClusterMongoRepository.class);
    public static final ClusterMongoRepository INSTANCE = new ClusterMongoRepository();
    private final String URI = "mongodb://localhost:27017";
    private MongoClient mongoClient;
    private MongoDatabase mongoDb;
    private MongoCollection<Cluster> clusterCollection;

    private ClusterMongoRepository() {
        super();
        CodecRegistry pojoCodecRegistry = CodecRegistries.fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build()));

        mongoClient = MongoClients.create(URI);
        mongoDb = mongoClient.getDatabase("mycollection").withCodecRegistry(pojoCodecRegistry);
        clusterCollection = mongoDb.getCollection("clusters", Cluster.class);
        clusterCollection.withCodecRegistry(pojoCodecRegistry);
    }

    @Override
    public long addCluster(Cluster cluster) {
        Bson updateAll = Updates.combine(Updates.set(Cluster.CID, cluster.getCid()),
                Updates.set(Cluster.MEMBER, cluster.getMember()));
        UpdateResult res = clusterCollection.updateOne(Filters.eq(Cluster.CID, cluster.getId()), updateAll,
                new UpdateOptions().upsert(true));

        if (res.wasAcknowledged()) {
            // LOGGER.info("cluster added");
            return 1;
        }
        // LOGGER.info("cluster not added");
        return -1;
    }

    @Override
    public long addClusters(List<Cluster> clusters) {
        long res = 1;
        for (Cluster c : clusters) {
            res = res * addCluster(c);
        }
        return res;
    }

    @Override
    public Cluster getCluster(String clusterId) {
        try {
            Cluster cluster = clusterCollection.find(Filters.eq(Cluster.CID, clusterId)).first();
            return cluster;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            //TODO: handle exception
        }
        return null;
    }

    @Override
    public List<Cluster> getLatest(int count) {
        List<Cluster> res = new ArrayList<>();
        try {
            for (Cluster c : clusterCollection.find().sort(Sorts.ascending(Cluster.CID)).limit(count)){
                res.add(c);
            }

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        // TODO Auto-generated method stub
        return res;
    }

}
