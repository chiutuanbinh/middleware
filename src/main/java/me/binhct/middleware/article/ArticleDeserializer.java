package me.binhct.middleware.article;

import org.apache.kafka.common.serialization.Deserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.binhct.middleware.proto.Article.PArticle;;

public class ArticleDeserializer implements Deserializer<Article> {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ArticleDeserializer.class);

    @Override
    public Article deserialize(String topic, byte[] data) {
        try {
            PArticle pArticle = PArticle.parseFrom(data);
            Article article = Article.fromProto(pArticle);
            return article;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }

    
    
}
