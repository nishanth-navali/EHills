/*
 * EE422C Final Project submission by
 * Nishanth Navali
 * nan686
 * 16160
 * Fall 2020
 * Slip days used: 1
 */

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

public class ClientHandler implements Runnable {

    private final Socket client;
    private final ObjectInputStream reader;
    private final ClientObserver writer;
    private final Server server;

    // Constructor passing in all the I/O streams and server
    public ClientHandler(Server server, Socket clientSocket, ClientObserver writer, ObjectInputStream reader) {
        this.server = server;
        this.client = clientSocket;
        this.writer = writer;
        this.reader = reader;
    }

    /**
     * Thread to check for inputs from each client and process them accordingly
     */
    @Override
    public void run() {
        synchronized (this) {
            Object obj;
            try {
                while (true) {
                    if ((obj = reader.readObject()) != null) {
                        System.out.println("Received from client: " + obj);
                        if (obj instanceof Login) {
                            boolean send = false;
                            ((Login) obj).decrypt();
                            for (Login currentLogin : server.getLogins()) {
                                if (currentLogin.compareTo(obj) == 0) {
                                    send = true;
                                }
                            }
                            writer.update(server, new Boolean(send));
                            if (send) {
                                writer.update(server, server.getItemsDs());
                            }
                        } else if (obj instanceof ArrayList<?> && ((ArrayList<?>) obj).get(0) instanceof Item) {
                            server.processRequest((ArrayList<Item>) obj);
                        }
                    }
                }
            } catch (SocketException e) {
                System.out.println("Client has been disconnected");
                server.removeObserver(writer);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}

