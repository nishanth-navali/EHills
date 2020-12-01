/*
 * EE422C Final Project submission by
 * Nishanth Navali
 * nan686
 * 16160
 * Fall 2020
 * Slip days used: 1
 */

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import com.google.gson.Gson;

public class Server extends Observable {

    // Data structures to hold items/logins on initialization
    private ArrayList<Item> itemsDs;
    private ArrayList<Login> loginsDs;

    public static void main(String[] args) {
        Server server = new Server();
        server.populateItems();
        server.populateLogins();
        server.SetupNetworking();
    }

    /**
     * Initialize possible valid logins from JSON
     */
    private void populateLogins() {
        loginsDs = new ArrayList<Login>();
        Gson gson = new Gson();
        File directoryPath = new File("users");
        String[] filenames = directoryPath.list();
        for (int i = 0; i < filenames.length; i++) {
            String filename = "users/" + filenames[i];
            try (Reader reader = new FileReader(filename)) {
                Login login = gson.fromJson(reader, Login.class);
                loginsDs.add(login);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println(loginsDs);
    }

    /**
     * Initialize Items from JSON
     */
    private void populateItems() {
        // init items from file
        itemsDs = new ArrayList<Item>();
        Gson gson = new Gson();
        File directoryPath = new File("items");
        String[] filenames = directoryPath.list();
        for (int i = 0; i < filenames.length; i++) {
            String filename = "items/" + filenames[i];
            try (Reader reader = new FileReader(filename)) {
                Item newItem = gson.fromJson(reader, Item.class);
                newItem.init();
                newItem.startTimer();
                itemsDs.add(newItem);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        System.out.println(itemsDs);
    }

    /**
     * Connect the socket and set up I/O
     */
    private void SetupNetworking() {
        final int port = 5000;
        try {
            ServerSocket ss = new ServerSocket(port, 100, InetAddress.getByName("0.0.0.0"));
            System.out.println("Server started at IP:" + ss.getInetAddress());
            while (true) {
                Socket clientSocket = ss.accept();
                ClientObserver writer = new ClientObserver(clientSocket.getOutputStream());
                InputStream is = clientSocket.getInputStream();
                ObjectInputStream ois = new ObjectInputStream(is);
                Thread t = new Thread(new ClientHandler(this, clientSocket, writer, ois));
                t.start();
                addObserver(writer);
                writer.update(this, "Hi! Welcome to EHills!");
                System.out.println("Got a connection");
            }
        } catch (IOException e) {
            System.out.println("Unable to set up networking");
        }
    }

    /**
     * This method updates the local data structure and sends it back to all the clients, also deals with concurrent bids
     *
     * @param obj - new ArrayList from a client
     */
    public synchronized void processRequest(ArrayList<Item> obj) {
        boolean different = false;
        for (int i = 0; i < itemsDs.size(); i++) {
            if (itemsDs.get(i).getCurrentPrice() <= obj.get(i).getCurrentPrice()) {
                different = true;
            }
        }
        if (different) {
            itemsDs = obj;
            for (Item item : itemsDs) {
                item.startTimer();
            }
            this.setChanged();
            this.notifyObservers(this.itemsDs);
        } else {
            System.out.println("Bid for the same price of an object was sent after another object sent, so bid was rejected");
        }
    }

    // Assorted getters/setters
    public ArrayList<Item> getItemsDs() {
        return itemsDs;
    }

    public ArrayList<Login> getLogins() {
        return loginsDs;
    }

    public void setItemsDs(ArrayList<Item> itemsDs) {
        this.itemsDs = itemsDs;
    }

    public void removeObserver(Observer writer) {
        this.deleteObserver(writer);
    }
}