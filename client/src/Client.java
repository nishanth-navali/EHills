/*
 * EE422C Final Project submission by
 * Nishanth Navali
 * nan686
 * 16160
 * Fall 2020
 * Slip days used: 1
 */

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class Client {

    // IO streams
    private final ObjectInputStream reader;
    private final ObjectOutputStream writer;
    private final Socket socket;

    // Customer name from login username
    private String customerName = "Unknown Customer";

    // Data structure of Items
    private ArrayList<Item> itemsDs;

    // Main GUI
    private final ClientGUI clientGUI;

    // Login boolean object to check
    private Boolean login = null;

    // decimal formatter
    static DecimalFormat df = new DecimalFormat("0.00");

    // CONSTRUCTOR
    public Client(String name, Socket socket, ObjectOutputStream toServer, ObjectInputStream fromServer, ClientGUI clientGUI) {
        this.socket = socket;
        this.writer = toServer;
        this.reader = fromServer;
        itemsDs = new ArrayList<Item>();
        this.clientGUI = clientGUI;
    }

    // Assorted getters/setters

    public ArrayList<Item> getItemsDs() {
        return itemsDs;
    }

    public void setName(String name) {
        this.customerName = name;
    }

    public Boolean checkLogin() {
        return login;
    }

    public void setLogin(Boolean fromServer) {
        this.login = fromServer;
    }

    public String getName() {
        return this.customerName;
    }

    /**
     * handle getting items arraylist from server
     *
     * @param fromServer - arraylist of items from the server
     */
    public void updateItems(ArrayList<Item> fromServer) {
        // if this is the first time the server is sending
        if (itemsDs.size() == 0) {
            itemsDs = fromServer;
        }

        // send to client to manually update each item, then update on GUI
        else {
            for (int i = 0; i < itemsDs.size(); i++) {
                itemsDs.get(i).update(fromServer.get(i));
            }
            clientGUI.updateItems();
        }
    }

    /**
     * Called whenever a successful bid or buy happens
     *
     * @throws IOException signifying no connection to server
     */
    public void sendItems() throws IOException {
        System.out.println("To server: " + itemsDs);
        writer.writeObject(itemsDs);
        writer.flush();
        writer.reset();
    }

    /**
     * Sends the login to the server
     *
     * @param login - username and password from the GUI
     */
    public void sendLogin(Login login) {
        try {
            writer.writeObject(login);
            writer.flush();
            writer.reset();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Once the login status is checked, set it back to null to avoid confusion
     */
    public void resetLogin() {
        login = null;
    }

    /**
     * Processes the bid (called by ClientGUI) and returns status message
     *
     * @param text - the bid value from TextField
     * @param item - the item being bid on
     * @return output message to go into a popup
     */
    public synchronized String processBid(String text, Item item) {
        // check if it is sold
        if (item.isSold()) {
            return "Unsuccessful bid: Item is already sold";
        }
        try {
            double bidValue = Double.parseDouble(text.replaceAll("[$]", ""));
            if (bidValue <= item.getCurrentPrice()) {
                return "Unsuccessful bid: Proposed bid value is not greater than the current highest bid";
            } else if (bidValue >= item.getBuyNow()) {
                return "Unsuccessful bid: Proposed bid value is not less than buy now price";
            } else {
                // valid bid, send to client
                item.setHighestBidder(this.customerName);
                item.setCurrentPrice(bidValue);
                this.sendItems();
                return "Successful bid: " + text + " on " + item.getName();
            }
        } catch (NumberFormatException e) {
            return "Unsuccessful bid: Unrecognized number format";
        } catch (IOException e) {
            return "Unsuccessful bid: Unable to send items back to server";
        }
    }

    /**
     * Processes the buy now call (called by ClientGUI) and returns status message
     *
     * @param item - the item being bought
     * @return output message to go into a popup
     */
    public synchronized String processBuyNow(Item item) {
        // check if it is sold
        if (item.isSold()) {
            return "Unsuccessful buy now: Item is already sold";
        }
        try {
            // valid buy, send to client
            item.setHighestBidder(this.customerName);
            item.setCurrentPrice(item.getBuyNow());
            item.setSold();
            this.sendItems();
            return "Successful purchase: $" + df.format(item.getBuyNow()) + " on " + item.getName();

        } catch (IOException e) {
            return "Unsuccessful buy now: Unable to send items back to server";
        }
    }
}