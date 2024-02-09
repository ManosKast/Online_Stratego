package Server;

import java.net.Socket;

public class Client {
    static final byte NO_OPERATION = -1;
    static final byte EXIT = 0;
    static final byte RESTART = 1;
    private final Socket socket;
    private final String ID;
    private byte restart;

    public Client(Socket socket, String ID) {
        this.socket = socket;
        this.ID = ID;
        this.restart = NO_OPERATION;
    }

    public Socket getSocket() {return socket;}
    public String getID() {return ID;}
    public boolean restart() {return restart == RESTART;}
    public boolean exited() {return restart == EXIT;}
    public void restartGame() {this.restart = RESTART;}
    public void exitGame() {this.restart = EXIT;}
    public void noOperation() {this.restart = NO_OPERATION;}
}
