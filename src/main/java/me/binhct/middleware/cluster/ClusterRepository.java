package me.binhct.middleware.cluster;

import java.util.List;


public interface ClusterRepository {
    public long addCluster(Cluster cluster);
    public long addClusters(List<Cluster> clusters);
    public Cluster getCluster(String clusterId); 
    public List<Cluster> getLatest(int count);


}
