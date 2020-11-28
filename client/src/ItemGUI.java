import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.text.DecimalFormat;

public class ItemGUI {
    Client client;
    Item item;
    static DecimalFormat df = new DecimalFormat("0.00");
    TextField highestBidder;
    TextField currentPrice;
    TextField timerCounter;

    public ItemGUI(Item currentItem, Client client) {
        this.item = currentItem;
        this.client = client;
    }


    public GridPane init() {
        GridPane currentItemPane = new GridPane();

        Label itemInformationTab = new Label("Item Information:");
        Label itemTabLabel = new Label("Name: " + item.getName());
        Label startPrice = new Label("Starting Price: $" + df.format(item.getMinimumBid()));
        Label buyNowPrice = new Label("Buy Now Price: $" +  df.format(item.getBuyNow()));
        Label separator = new Label("------------------------------------------------------------");
        Label currentInformationTab = new Label("Current Information:");

        Label mostRecentBidder = new Label("Highest Bidder: ");
        highestBidder = new TextField(item.getHighestBidder());
        highestBidder.setEditable(false);

        Label currentPriceLabel = new Label("Current Bid: ");
        currentPrice = new TextField("$" + df.format(item.getCurrentPrice()));
        highestBidder.setEditable(false);

        Label timerLabel = new Label("Time left: ");
        timerCounter = new TextField("" + item.getTimeLeft() + "s");
        timerCounter.setEditable(false);

        Label separator2 = new Label("------------------------------------------------------------");
        Label customerActionTab = new Label("Customer Action:");
        Button bid = new Button("Bid");
        Button buyNow = new Button("Buy Now: $" +  df.format(item.getBuyNow()));
        TextField bidValue = new TextField("$" + df.format(item.getCurrentPrice() + 0.01));

        Button exit = new Button("Exit");

        bid.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String output = client.processBid(bidValue.getText(), item);
                Stage bidOutputStage = new Stage();
                bidOutputStage.setX(500);
                bidOutputStage.setY(500);
                BorderPane bidBP = new BorderPane();
                bidBP.setCenter(new Label(output));
                bidOutputStage.setScene(new Scene(bidBP, 200, 200));
                bidOutputStage.show();
            }
        });

        buyNow.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.exit(0);
            }
        });

        exit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.exit(0);
            }
        });


        currentItemPane.add(itemInformationTab, 0, 0, 1, 1);
        currentItemPane.add(itemTabLabel, 0, 1, 3, 1);
        currentItemPane.add(startPrice, 0, 2, 3, 1);
        currentItemPane.add(buyNowPrice, 0, 3, 3, 1);

        currentItemPane.add(separator, 0, 4, 3, 1);

        currentItemPane.add(currentInformationTab, 0, 5, 1, 1);
        currentItemPane.add(timerLabel, 0, 6, 1, 1);
        currentItemPane.add(timerCounter, 1, 6, 2, 1);
        currentItemPane.add(mostRecentBidder, 0, 7, 1, 1);
        currentItemPane.add(highestBidder, 1, 7, 2, 1);
        currentItemPane.add(currentPriceLabel, 0, 8, 1, 1);
        currentItemPane.add(currentPrice, 1, 8, 2, 1);

        currentItemPane.add(separator2, 0, 9, 3, 1);

        currentItemPane.add(customerActionTab, 0, 10, 1, 1);
        currentItemPane.add(bidValue, 0, 11, 2, 1);
        currentItemPane.add(bid, 2, 11, 1, 1);
        currentItemPane.add(buyNow, 0, 12, 2, 1);
        currentItemPane.add(exit, 2, 12);

        return currentItemPane;
    }

    public void updateLatestItemInformation() {
        highestBidder.setText(item.getHighestBidder());
        currentPrice.setText("$" + df.format(item.getCurrentPrice()));
        timerCounter.setText("" + item.getTimeLeft() + "s");
    }
}
