package cn.fan.pojo;

public class Trade {

    public String getBuyDate() {
        return buyDate;
    }

    public void setBuyDate(String buyDate) {
        this.buyDate = buyDate;
    }

    public String getSellDate() {
        return sellDate;
    }

    public void setSellDate(String sellDate) {
        this.sellDate = sellDate;
    }

    public float getBuyClosePoint() {
        return buyClosePoint;
    }

    public void setBuyClosePoint(float buyClosePoint) {
        this.buyClosePoint = buyClosePoint;
    }

    public float getSellClosePoint() {
        return sellClosePoint;
    }

    public void setSellClosePoint(float sellClosePoint) {
        this.sellClosePoint = sellClosePoint;
    }

    public float getRate() {
        return rate;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }

    private String buyDate;//购买日期
    private String sellDate;//卖出日期
    private float buyClosePoint;//买入金额
    private float sellClosePoint;//出售金额
    private float rate;//收益
}
