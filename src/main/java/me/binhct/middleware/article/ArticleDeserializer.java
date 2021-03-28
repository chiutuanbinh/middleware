package me.binhct.middleware.article;

import org.apache.kafka.common.serialization.Deserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.binhct.middleware.proto.Article.PArticle;;

public class ArticleDeserializer implements Deserializer<Article> {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ArticleDeserializer.class);
    public static Article fromProto(PArticle pArticle) {
        Article article = new Article();
        article.setOriCategory(pArticle.getOriCategory());
        article.setOriKeywords(pArticle.getOriKeywordsList());
        article.setOriUrl(pArticle.getOriUrl());
        article.setPublisher(pArticle.getPublisher());
        article.setTitle(pArticle.getTitle());
        article.setParagraph(pArticle.getParagraphList());
        article.setTimestamp(pArticle.getTimestamp());
        article.setAid(pArticle.getId());
        article.setMediaURLs(pArticle.getMediaUrlList());
        article.setDescription(pArticle.getDescription());
        return article;
    }
    @Override
    public Article deserialize(String topic, byte[] data) {
        try {
            PArticle pArticle = PArticle.parseFrom(data);
            Article article = fromProto(pArticle);
            return article;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }

    
    
}
