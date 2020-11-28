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
import java.util.ArrayList;
import java.util.Arrays;

public class Client {

    ClientController controller;
    ObjectInputStream reader;
    ObjectOutputStream writer;
    Socket socket;
    Customer customer;
    ArrayList<Item> itemsDs;
    ClientGUI clientGUI;
    private Boolean login = null;


    public Client(String name, Socket socket, ObjectOutputStream toServer, ObjectInputStream fromServer, ClientGUI clientGUI) {
        customer = new Customer(name);
        this.socket = socket;
        this.writer = toServer;
        this.reader = fromServer;
        itemsDs = new ArrayList<Item>();
        this.clientGUI = clientGUI;
    }


//    @Override
//    public void start(Stage primaryStage) throws Exception{
////        FXMLLoader fxmlLoader = new FXMLLoader();
////        Parent root = fxmlLoader.load(getClass().getResource("client.fxml").openStream());
////        controller = fxmlLoader.getController();
////        primaryStage.setTitle("Customer");
////        primaryStage.setScene(new Scene(root, 700, 600));
////        primaryStage.show();
////        controller.myClient = this;
//
////        connectToServer();
//        writer.writeObject(new String("Bill"));
//        writer.flush();
//        writer.reset();
//        writer.writeObject(new Item("Ball", 10, 50, 100));
//        writer.flush();
//        writer.reset();
////        socket.close();
//    }

//    void connectToServer () {
//        int port = 5000;
//        try {
//            socket = new Socket("localhost", port);
//            writer = new ObjectOutputStream(socket.getOutputStream());
//            reader = new ObjectInputStream(socket.getInputStream());
//            System.out.println("networking established");
//            Thread readerThread = new Thread(new IncomingReader(reader)); // see Canvas's Chat for IncomingReader class
//            readerThread.start();
//
//        } catch (IOException e) {}
//    }

    ObjectOutputStream getObjectOutputStream() {return writer;}

    ObjectInputStream getObjectInputStream() {return reader;}

    ClientController getController () { return controller; }

    public ArrayList<Item> getItemsDs() {
        return itemsDs;
    }

    public void setName(String name) {
        customer.setName(name);
    }

    public Boolean checkLogin() {
        return login;
    }

    public void setLogin(Boolean fromServer) {
        this.login = fromServer;
    }

    public String getName() {
        return customer.name;
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
                item.setName(getName());
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
}