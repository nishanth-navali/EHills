import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class ClientHandler implements Runnable {

    private final Socket client;
    private final ObjectInputStream reader;
    private final ClientObserver writer;
    private final Server server;
    public ClientHandler(Server server, Socket clientSocket, ClientObserver writer, ObjectInputStream reader) {
        this.server = server;
        this.client = clientSocket;
        this.writer = writer;
        this.reader = reader;
    }

    @Override
    public void run() {
        synchronized (this) {
            Object obj;
            try {
                while(true) {
                    if((obj = reader.readObject()) != null) {
                        System.out.println("Received from client: " + obj);
                        if(obj instanceof Login) {
                            boolean send = false;
                            for(Login currentLogin : server.getLogins()) {
                                if(currentLogin.compareTo(obj) == 0) {
                                    send = true;
                                }
                            }
                            writer.update(server, new Boolean(send));
                            if(send) {
                                writer.update(server, server.getItemsDs());
                            }
                        }
                        else if(obj instanceof ArrayList<?> && ((ArrayList<?>) obj).get(0) instanceof Item) {
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

