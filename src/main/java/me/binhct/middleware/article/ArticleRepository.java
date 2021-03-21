package me.binhct.middleware.article;

import java.util.List;

public interface ArticleRepository {
    public long addArticle(Article article);
    public long addArticles(List<Article> articles);
    public Article getArticle(String id);
    public List<Article> getArticles(List<String> id);
    public List<Article> getLatestId(int count);
    public List<Article> getByPublisherLatest(String publisher, int count);
    public List<String> getAllPublisher();
}
