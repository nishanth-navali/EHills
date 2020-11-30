/*
 * EE422C Final Project submission by
 * Nishanth Navali
 * nan686
 * 16160
 * Fall 2020
 * Slip days used: 1
 */

import java.io.*;
import java.util.ArrayList;

public class IncomingReader implements Runnable {

    // Object input form server
    ObjectInputStream reader;

    // client to handle inputs
    Client client;

    // CONSTRUCTOR
    public IncomingReader(ObjectInputStream reader, Client c) {
        this.reader = reader;
        this.client = c;
    }

    @Override
    public void run() {
        Object fromServer;
        try {
            // check for object
            while ((fromServer = reader.readObject()) != null) {
                // Print object to console
                System.out.println("From Server: " + fromServer);

                // Boolean input means login status
                if (fromServer instanceof Boolean) {
                    client.setLogin((Boolean) fromServer);
                }
                // Arraylist of Items input means a itemsDs update
                else if (fromServer instanceof ArrayList<?> && ((ArrayList<?>) fromServer).get(0) instanceof Item) {
                    client.updateItems((ArrayList<Item>) fromServer);
                }
            }
        } catch (IOException e) {
            System.out.println("Disconnected from server");
        } catch (ClassNotFoundException ex) {
            System.out.println("Input cannot be mapped to a class");
        }
    }
}
