package me.binhct.middleware.cluster;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClusterModel {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClusterModel.class);
    public static final ClusterModel INSTANCE = new ClusterModel();
    private ClusterRepository repository;
    private ClusterModel() {
        super();
    }

    public void setRepository(ClusterRepository repository){
        this.repository = repository;
    }

    public List<Cluster> getLatest(int limit) {
        return repository.getLatest(limit);
    }

    public long addCluster(Cluster cluster){
        return repository.addCluster(cluster);
    }

    
}
