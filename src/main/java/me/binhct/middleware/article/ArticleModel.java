package me.binhct.middleware.article;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ArticleModel {
    private static final Logger LOGGER = LoggerFactory.getLogger(ArticleModel.class);
    public static final ArticleModel INSTANCE = new ArticleModel();
    private static final String ARTICLE = "article";
    private ArticleRepository repository;
    private ArticleTextSearch textSearch=ArticleTextSearch.INSTANCE;
    private Properties kafkaProperties;
    private KafkaConsumer<String, Article> consumer;
    private Thread listener;

    private ArticleModel() {
        super();
        initKafkaProperties();
        initKafkaConsumer();
        startKafkaListen();
        
    }
    public void setRepository(ArticleRepository repository) {
        this.repository = repository;
    }

    private void initKafkaProperties(){
        kafkaProperties = new Properties();
        kafkaProperties.setProperty("bootstrap.servers", "localhost:9092");
        kafkaProperties.setProperty("group.id", "articleMW");
        kafkaProperties.setProperty("enable.auto.commit", "true");
        kafkaProperties.setProperty("auto.commit.interval.ms", "1000");
        kafkaProperties.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        kafkaProperties.setProperty("value.deserializer", "me.binhct.middleware.article.ArticleDeserializer");
    }

    private void initKafkaConsumer() {
        consumer = new KafkaConsumer<>(kafkaProperties);
        consumer.subscribe(Arrays.asList(ARTICLE));
    }

    private void startKafkaListen() {
        listener = new Thread(new Runnable() {
            @Override
            public void run() {
                LOGGER.info("article listener started");
                while (true) {
                    ConsumerRecords<String, Article> records = consumer.poll(Duration.ofMillis(1000));
                    for (ConsumerRecord<String, Article> record : records){
                        // LOGGER.info(record.value().getParagraphs().toString());
                        repository.addArticle(record.value());
                        textSearch.indexArticle(record.value());
                    }
                }
            }
        });
        listener.start();
    }

    public List<Article> getLatest(int count){
        return repository.getLatestArticles(count);
    }

    public List<Article> getByPubisher(String publisher, int count){
        return repository.getByPublisherLatest(publisher, count);
    }

    public List<Article> getByCategory(String category, int count){
        return repository.getByCategoryLatest(category, count);
    }

    public Article get(String id) {
        return repository.getArticle(id);
    }

    public List<String> getAllPublisher(){
        return repository.getAllPublisher();
    }

    public List<String> getAllCategory(){
        return repository.getAllCategory();
    }

    public List<Article> search(String searchTerm){
        return textSearch.searchArticle(searchTerm);
    }

}
