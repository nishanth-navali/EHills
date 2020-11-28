/*
 * EE422C Final Project submission by
 * Nishanth Navali
 * nan686
 * 16160
 * Fall 2020
 */


import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Observable;
import com.google.gson.Gson;

public class Server extends Observable {

    private ArrayList<Item> itemsDs;
    private ArrayList<Login> loginsDs;

    public static void main (String [] args) {
        Server server = new Server();
        server.populateItems();
        server.populateLogins();
        server.SetupNetworking();
    }

    private void populateLogins() {
        loginsDs = new ArrayList<Login>();
        Gson gson = new Gson();
        File directoryPath = new File("users");
        String[] filenames = directoryPath.list();
        for(int i = 0; i < filenames.length; i++) {
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

    private void populateItems() {
        // init items from file
        itemsDs = new ArrayList<Item>();
        Gson gson = new Gson();
        File directoryPath = new File("items");
        String[] filenames = directoryPath.list();
        for(int i = 0; i < filenames.length; i++) {
            String filename = "items/" + filenames[i];
            try (Reader reader = new FileReader(filename)) {
                Item newItem = gson.fromJson(reader, Item.class);
                newItem.init();
                itemsDs.add(newItem);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println(itemsDs);
    }

    private void SetupNetworking() {
        int port = 5000;
        try {
            ServerSocket ss = new ServerSocket(port);
            System.out.println("Server started at IP:" + ss.getInetAddress());
            while (true) {
                Socket clientSocket = ss.accept();
                ClientObserver writer = new ClientObserver(clientSocket.getOutputStream());
                InputStream is = clientSocket.getInputStream();
                ObjectInputStream ois = new ObjectInputStream(is);
                Thread t = new Thread(new ClientHandler(this, clientSocket, writer, ois));
                t.start();
                addObserver(writer);
                System.out.println("Got a connection");
            }
        } catch (IOException e) {}
    }

    public synchronized void processRequest(ArrayList<Item> obj) {
        System.out.println(this.itemsDs); // testing
        this.setChanged();
        this.notifyObservers(this.itemsDs);
    }

    public ArrayList<Item> getItemsDs() {
        return itemsDs;
    }

    public ArrayList<Login> getLogins() {return loginsDs;}

    public void setItemsDs(ArrayList<Item> itemsDs) {
        this.itemsDs = itemsDs;
    }
}