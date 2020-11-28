import java.io.*;
import java.util.ArrayList;

public class IncomingReader implements Runnable {

    ObjectInputStream reader;
    Client client;

    public IncomingReader(ObjectInputStream reader, Client c) {
        this.reader = reader;
        this.client = c;
    }

    @Override
    public void run() {
        Object fromServer;
        try {
            while ((fromServer = reader.readObject()) != null) {
                System.out.println("From Server: " + fromServer);
                if(fromServer instanceof Boolean) {
                    client.setLogin((Boolean) fromServer);
                }
                else if(fromServer instanceof ArrayList<?> && ((ArrayList<?>) fromServer).get(0) instanceof Item) {
                    client.updateItems((ArrayList<Item>) fromServer);
                }

            }
        } catch (IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }
}
