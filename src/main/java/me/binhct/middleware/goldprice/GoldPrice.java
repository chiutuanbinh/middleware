package me.binhct.middleware.goldprice;

public class GoldPrice {
    private final String gType;
    private final int buy;
    private final int sell;
    private final long timestamp;

    public GoldPrice(String gType, int buy, int sell, long timestamp){
        this.gType = gType;
        this.buy = buy;
        this.sell = sell;
        this.timestamp = timestamp;
    }
    public String getgType() {
        return gType;
    }
    public int getBuy() {
        return buy;
    }
    public int getSell() {
        return sell;
    }
    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        String text = String.format("%s %d %d %d ", gType, buy, sell, timestamp);
        return text;
    }

}
