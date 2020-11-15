/*
 * EE422C Final Project submission by
 * Nishanth Navali
 * nan686
 * 16160
 * Fall 2020
 */

package server;

import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable {

    static ArrayList<Socket> clientsDS = new ArrayList<Socket>();
    private ObjectInputStream reader;
    private  ClientObserver writer; // See Canvas. Extends ObjectOutputStream, implements Observer
    Socket clientSocket;

    public ClientHandler(Socket clientSocket, ClientObserver writer) {
        // TODO
    }

    @Override
    public void run() {
        // TODO
    }
}
