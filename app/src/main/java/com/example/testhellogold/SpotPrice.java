package com.example.testhellogold;

public class SpotPrice {
    private float buy,sell,spot_price;
    private String timestamp;

    public SpotPrice(float buy, float sell, float spot_price, String timestamp) {
        this.buy = buy;
        this.sell = sell;
        this.spot_price = spot_price;
        this.timestamp = timestamp;
    }

    public float getBuy() {
        return buy;
    }

    public void setBuy(float buy) {
        this.buy = buy;
    }

    public float getSell() {
        return sell;
    }

    public void setSell(float sell) {
        this.sell = sell;
    }

    public float getSpot_price() {
        return spot_price;
    }

    public void setSpot_price(float spot_price) {
        this.spot_price = spot_price;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
