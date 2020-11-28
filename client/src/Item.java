import java.io.Serializable;
import java.util.ArrayList;

public class Item implements Serializable {
    private static final long serialVersionUID = -72947298263569244L;
    String name;
    double currentPrice;
    double minimumBid;
    double buyNow;
    boolean sold;
    double timeLeft;
    String highestBidder;



    public Item(String name, double minimumBid, double buyNow, double timeLeft) {
        this.name = name;
        this.currentPrice = minimumBid;
        this.minimumBid = minimumBid;
        this.buyNow = buyNow;
        this.timeLeft = timeLeft;
        this.sold = false;
        highestBidder = "NA";
    }

    @Override
    public String toString() {
        return "Item{" +
                "name='" + name + '\'' +
                ", currentPrice=" + currentPrice +
                ", minimumBid=" + minimumBid +
                ", buyNow=" + buyNow +
                ", sold=" + sold +
                ", timeLeft=" + timeLeft +
                ", highestBidder='" + highestBidder + '\'' +
                '}';
    }

    public void init() {
        this.currentPrice = this.minimumBid;
        this.sold = false;
        this.highestBidder = "NA";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(double currentPrice) {
        this.currentPrice = currentPrice;
    }

    public double getMinimumBid() {
        return minimumBid;
    }

    public void setMinimumBid(double minimumBid) {
        this.minimumBid = minimumBid;
    }

    public double getBuyNow() {
        return buyNow;
    }

    public void setBuyNow(double buyNow) {
        this.buyNow = buyNow;
    }

    public double getTimeLeft() {
        return timeLeft;
    }

    public void setTimeLeft(double timeLeft) {
        this.timeLeft = timeLeft;
    }

    public String getHighestBidder() {
        return highestBidder;
    }

    public void setHighestBidder(String highestBidder) {
        this.highestBidder = highestBidder;
    }

    public boolean isSold() {
        return sold;
    }

    public void update(Item item) {
        this.highestBidder = item.getHighestBidder();
        this.currentPrice = item.getCurrentPrice();
        this.timeLeft = item.getTimeLeft();
        this.sold = item.isSold();
    }
}
