package me.binhct.middleware.article;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import org.bson.types.ObjectId;

import me.binhct.middleware.proto.Article.PArticle;
@JsonInclude(Include.NON_NULL)
public final class Article {

    public static Article fromProto(PArticle pArticle) {
        Article article = new Article();
        article.oriCategory = pArticle.getOriCategory();
        article.oriKeywords = pArticle.getOriKeywordsList();
        article.oriUrl = pArticle.getOriUrl();
        article.publisher = pArticle.getPublisher();
        article.title = pArticle.getTitle();
        article.paragraphs = pArticle.getParagraphList();
        article.timestamp = pArticle.getTimestamp();
        article.aid = pArticle.getId();
        return article;
    }

    private ObjectId id;
    private String publisher;
    private String title;
    private String oriCategory;
    private List<String> oriKeywords;
    private String oriUrl;
    private long timestamp;
    private List<String> paragraphs;
    private String aid;
    private List<String> mediaURLs;

    public Article() {
    }
    public Article(ObjectId id, String publisher, String title, String oriCategory, List<String> oriKeywords, String oriUrl, long timestamp, List<String> paragraphs, String aid, List<String> mediaURLs) {
        this.id = id;
        this.publisher = publisher;
        this.title = title;
        this.oriCategory = oriCategory;
        this.oriKeywords = oriKeywords;
        this.oriUrl = oriUrl;
        this.timestamp = timestamp;
        this.paragraphs = paragraphs;
        this.aid = aid;
        this.mediaURLs = mediaURLs;
    }

    public ObjectId getId() {
        return id;
    }
    public void setId(ObjectId id) {
        this.id = id;
    }
    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOriCategory() {
        return oriCategory;
    }

    public void setOriCategory(String oriCategory) {
        this.oriCategory = oriCategory;
    }

    public List<String> getOriKeywords() {
        return oriKeywords;
    }

    public void setOriKeywords(List<String> oriKeywords) {
        this.oriKeywords = oriKeywords;
    }

    public String getOriUrl() {
        return oriUrl;
    }

    public void setOriUrl(String oriUrl) {
        this.oriUrl = oriUrl;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public List<String> getParagraphs() {
        return paragraphs;
    }

    public void setParagraphs(List<String> paragraphs) {
        this.paragraphs = paragraphs;
    }

    public String getAid() {
        return aid;
    }

    public void setAid(String aid) {
        this.aid = aid;
    }

    public List<String> getMediaURLs() {
        return mediaURLs;
    }

    public void setMediaURLs(List<String> mediaURLs) {
        this.mediaURLs = mediaURLs;
    }

    @Override
    public String toString() {
        String sValue = String.format("%s %s %d %s", aid, title, timestamp, paragraphs.get(0));
        return sValue;
    }
}
