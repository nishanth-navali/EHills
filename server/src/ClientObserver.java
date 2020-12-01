/*
 * EE422C Final Project submission by
 * Nishanth Navali
 * nan686
 * 16160
 * Fall 2020
 * Slip days used: 1
 */

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Observable;
import java.util.Observer;

public class ClientObserver extends ObjectOutputStream implements Observer {

    public ClientObserver(OutputStream out) throws IOException {
        super(out);
    }

    /**
     * Sends message to client
     *
     * @param o   - the server observable
     * @param arg - object to send
     */
    @Override
    public void update(Observable o, Object arg) {
        try {
            System.out.println("Sending to client: " + arg);
            writeObject(arg);
            this.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
