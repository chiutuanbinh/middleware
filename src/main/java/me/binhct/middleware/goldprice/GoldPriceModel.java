package me.binhct.middleware.goldprice;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import ch.qos.logback.classic.Level;

@Component
public class GoldPriceModel {
    public static final GoldPriceModel Instance = new GoldPriceModel();
    private static final String GOLD = "gold";
    private static final Logger LOGGER = LoggerFactory.getLogger(GoldPriceModel.class);

    private Map<String, GoldPrice> entries;
    private Properties kafkaProperties;
    private KafkaConsumer<String, GoldPrice> consumer;
    private Thread listener;

    private GoldPriceModel() {
        entries = new HashMap<>();
        initKafkaProperties();
        initKafkaConsumer();
        startKafkaListen();
    }

    private void initKafkaProperties() {
        kafkaProperties = new Properties();
        kafkaProperties.setProperty("bootstrap.servers", "localhost:9092");
        kafkaProperties.setProperty("group.id", "priceMW");
        kafkaProperties.setProperty("enable.auto.commit", "true");
        kafkaProperties.setProperty("auto.commit.interval.ms", "1000");
        kafkaProperties.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        kafkaProperties.setProperty("value.deserializer", "me.binhct.middleware.goldprice.GoldPriceDeserializer");
        // kafkaProperties.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
    }

    private void initKafkaConsumer() {
        consumer = new KafkaConsumer<>(kafkaProperties);
        consumer.subscribe(Arrays.asList(GOLD));
    }

    private void startKafkaListen() {
        listener = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    ConsumerRecords<String, GoldPrice> records = consumer.poll(Duration.ofMillis(100));
                    for (ConsumerRecord<String, GoldPrice> record : records){
                        LOGGER.info(record.key());
                        LOGGER.info(record.value().toString());
                        entries.put(record.key(), record.value());
                    }
                }
            }
        });
        listener.start();
    }

    public GoldPrice getPrice(String gType) {
        LOGGER.info(entries.toString());
        return entries.get(gType);
    }

    public static void main(String[] args) {
        ((ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME)).setLevel(Level.INFO);

        while (true) {
            ConsumerRecords<String, GoldPrice> records = Instance.consumer.poll(Duration.ofMillis(1000));
            for (ConsumerRecord<String, GoldPrice> record : records) {
                if (record == null || record.value() == null){
                    continue;
                }
                LOGGER.info(record.value().toString());
            }
        }

    }
}
