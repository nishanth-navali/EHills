/*
 * EE422C Final Project submission by
 * Nishanth Navali
 * nan686
 * 16160
 * Fall 2020
 * Slip days used: 1
 */

import java.io.Serializable;
import java.util.Timer;
import java.util.TimerTask;

public class Item implements Serializable {
    private static final long serialVersionUID = -72947298263569244L;

    // Item name
    private String name;

    // Current bid price
    private double currentPrice;

    // Starting bid price
    private double minimumBid;

    // Buy Now price
    private double buyNow;

    // Boolean to check if item has been sold or not
    private boolean sold;

    // Timer
    private Integer timeLeft;

    // Name of the highest bidder
    private String highestBidder;

    // CONSTRUCTOR
    public Item(String name, double minimumBid, double buyNow, int timeLeft) {
        this.name = name;
        this.currentPrice = minimumBid;
        this.minimumBid = minimumBid;
        this.buyNow = buyNow;
        this.timeLeft = timeLeft;
        this.sold = false;
        highestBidder = "NA";
    }

    /**
     * toString for console debugging
     *
     * @return
     */
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

    // Server initialization called after GSON parsing
    public void init() {
        this.currentPrice = this.minimumBid - 0.01;
        this.sold = false;
        this.highestBidder = "NA";
    }

    public void startTimer() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (timeLeft > 0) {
                    timeLeft--;
                } else {
                    setSold();
                }
            }
        }, 1000, 1000);
    }

    // Client update item without changing reference
    public void update(Item item) {
        this.highestBidder = item.getHighestBidder();
        this.currentPrice = item.getCurrentPrice();
        this.timeLeft = item.getTimeLeft();
        this.sold = item.isSold();
    }

    // Assorted getters/setters

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

    public Integer getTimeLeft() {
        return timeLeft;
    }

    public void setTimeLeft(int timeLeft) {
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

    public void setSold() {
        this.sold = true;
    }
}
