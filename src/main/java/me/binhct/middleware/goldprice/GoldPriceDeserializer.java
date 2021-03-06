package me.binhct.middleware.goldprice;

import org.apache.kafka.common.serialization.Deserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.binhct.middleware.proto.Goldprice.PGoldprice;;

public class GoldPriceDeserializer implements Deserializer<GoldPrice> {
    private static final Logger LOGGER = LoggerFactory.getLogger(GoldPriceDeserializer.class);

    @Override
    public GoldPrice deserialize(String topic, byte[] data) {
        try {
            PGoldprice price = PGoldprice.parseFrom(data);
            GoldPrice gp = new GoldPrice(price.getGType(), price.getBuy(), price.getSell(), price.getTimestamp());
            return gp;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }

        return null;
    }

}
