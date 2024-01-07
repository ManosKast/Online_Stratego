package Server;

import java.net.Socket;

public class Client {
    private final Socket socket;
    private final String ID;

    public Client(Socket socket, String ID) {
        this.socket = socket;
        this.ID = ID;
    }

    public Socket getSocket() {return socket;}
    public String getID() {return ID;}
}
