/*
 * EE422C Final Project submission by
 * Nishanth Navali
 * nan686
 * 16160
 * Fall 2020
 */

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Array;
import java.net.Socket;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;

public class Client {

    ClientController controller;
    ObjectInputStream reader;
    ObjectOutputStream writer;
    Socket socket;
    String customerName = "Unknown Customer";
    ArrayList<Item> itemsDs;
    ClientGUI clientGUI;
    private Boolean login = null;

    static DecimalFormat df = new DecimalFormat("0.00");


    public Client(String name, Socket socket, ObjectOutputStream toServer, ObjectInputStream fromServer, ClientGUI clientGUI) {
        this.socket = socket;
        this.writer = toServer;
        this.reader = fromServer;
        itemsDs = new ArrayList<Item>();
        this.clientGUI = clientGUI;
    }

    ObjectOutputStream getObjectOutputStream() {return writer;}

    ObjectInputStream getObjectInputStream() {return reader;}

    ClientController getController () { return controller; }

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

    public void updateItems(ArrayList<Item> fromServer) {
        if(itemsDs.size() == 0) {
            itemsDs = fromServer;
        }
        else {
            for(int i = 0; i < itemsDs.size(); i++) {
                itemsDs.get(i).update(fromServer.get(i));
            }

            clientGUI.updateItems();
        }
    }

    public void sendLogin(Login login) {
        try {
            writer.writeObject(login);
            writer.flush();
            writer.reset();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void sendItems() throws IOException {
        System.out.println("To server: " + itemsDs);
        writer.writeObject(itemsDs);
        writer.flush();
        writer.reset();
    }

    public void resetLogin() {
        login = null;
    }

    public String processBid(String text, Item item) {
        if(item.isSold()) {
            return "Unsuccessful bid: Item is already sold";
        }
        try {
            double bidValue = Double.parseDouble(text.replaceAll("[$]", ""));
            if(bidValue <= item.getCurrentPrice()) {
                return "Unsuccessful bid: Proposed bid value is not greater than the current highest bid";
            }
            else if(bidValue >= item.getBuyNow()) {
                return "Unsuccessful bid: Proposed bid value is not less than buy now price";
            }
            else {
                item.setHighestBidder(this.customerName);
                item.setCurrentPrice(bidValue);
                this.sendItems();
                return "Successful bid: " +  text + " on " + item.getName();
            }
        }
        catch (NumberFormatException e) {
            return "Unsuccessful bid: Unrecognized number format";
        } catch (IOException e) {
            return "Unsuccessful bid: Unable to send items back to server";
        }
    }

    public String processBuyNow(Item item) {
        if(item.isSold()) {
            return "Unsuccessful buy now: Item is already sold";
        }
        try {
                item.setHighestBidder(this.customerName);
                item.setCurrentPrice(item.getBuyNow());
                item.setSold();
                this.sendItems();
                return "Successful purchase: $" +  df.format(item.getBuyNow()) + " on " + item.getName();

        }
        catch (IOException e) {
            return "Unsuccessful buy now: Unable to send items back to server";
        }
    }
}