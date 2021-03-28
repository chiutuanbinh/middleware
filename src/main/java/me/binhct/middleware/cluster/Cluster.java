package me.binhct.middleware.cluster;

import java.util.List;

import org.bson.types.ObjectId;

public class Cluster {
    public static final String CID = "cid";
    public static final String MEMBER = "member";

    private ObjectId id;
    private String cid;
    private List<String> member;

    public Cluster() {
        super();
    }

    public Cluster(ObjectId id, String clusterId, List<String> member) {
        super();
        this.id = id;
        this.cid = clusterId;
        this.member = member;
    }

    public String getCid() {
        return cid;
    }

    public ObjectId getId() {
        return id;
    }

    public List<String> getMember() {
        return member;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public void setMember(List<String> memberArticleIds) {
        this.member = memberArticleIds;
    }
}
