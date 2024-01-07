package Controller;

import Model.Player_and_Board.Player;
import Protocol.Protocol;
import Protocol.Flag;
import Packet.Packet;
import Server.Client;
import Server.Game;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

// TODO: Encipher IDs. Create Class Client that will contain client's details.
// TODO: Create Class Game that will contain the game.
public class ControllerServer {
    private String serverID = "Server";
    static Hashtable<String, Game> games = new Hashtable<>();
    static LinkedList<Client> queued_players = new LinkedList<>();

    public static void main(String[] args) throws IOException {
        int portNumber = 1234; // Example port number

        try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
            System.out.println("Server is listening on port " + portNumber);

            while (true) {
                Socket clientSocket = serverSocket.accept(); // Accept new client
                System.out.println("Accepted new client " + clientSocket.getRemoteSocketAddress());
                // TODO: Create method to generate random ID based on IP.
                queued_players.add(new Client(clientSocket, "Player" + queued_players.size())); // Add client to queue
                // Create and start a new thread for the client
                new ClientHandler(clientSocket).start();
            }
        } catch (IOException e) {
            System.out.println("Exception caught when trying to listen on port " + portNumber);
            System.out.println(e.getMessage());
        }
    }

    static class ClientHandler extends Thread {
        private final Packet messageHandler = new Packet();
        private final Socket clientSocket;
        private Packet message;
        private String response;
        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        // TODO: Server should return its ID, so clients can check if they are connected to the same server.
        // TODO: Add source and destination (names) in packet.
        // TODO: Only one thread can access the game at a time.
        public void run() {
            try {
                String header = "";
                System.out.println(this.clientSocket);
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    //System.out.println("Received from client: " + inputLine);
                    Packet query = Packet.convertMessageToPacket(inputLine);
                    //System.out.println("Protocol: " + query.getProtocol());
                    header = inputLine;
                    switch (query.getProtocol()){
                        case Protocol.FIND_MATCH:
                            if (!query.hasName()) response = queued_players.getLast().getID();
                            header = query.generatePacket(Protocol.WELCOME, true, response);
                            out.println(header);

                            if(queued_players.size() % 2 == 0) {
                                header = query.generatePacket(Protocol.START_GAME, true, response);
                                // TODO: Make this check with a new thread. Also, ID in Player must be ID returned
                                // TODO: by server. Pair must contain socket and Player.
                                // TODO: Alternatively hash table with 2 keys for player or socket.
                                // TODO: Make Pair iterable. Make it prettier too. Add functions too,
                                Client player2 = queued_players.remove(), player1 = queued_players.remove();
                                Game newGame = new Game(0, player1, player2);
                                System.out.println(player1.getID());
                                games.put(player1.getID(), newGame);
                                games.put(player2.getID(), newGame);
                                PrintWriter out1 = new PrintWriter(player1.getSocket().getOutputStream(), true);
                                PrintWriter out2 = new PrintWriter(player2.getSocket().getOutputStream(), true);
                                String start1 = newGame.getPlayersBoard(player1.getID(), Flag.FIRST);
                                String start2 = newGame.getPlayersBoard(player2.getID(), Flag.SECOND);
                                out1.println(start1);
                                out2.println(start2);
                            }
                            break;

                        case Protocol.MOVE:
                            System.out.println(query.getID());
                            Game game = games.get(query.getID());
                            if (game != null) {
                                int[] nextPosition = query.extractData(int[].class);
                                game.moveMonster(nextPosition[0], nextPosition[1], query.getID());
                            } else System.out.println("Game not found");

                        case Protocol.SELECT:
                            Game game1 = games.get(query.getID());
                            if (game1 != null) {
                                int[] nextPosition = query.extractData(int[].class);
                                response = game1.selectMonster(nextPosition[0], nextPosition[1], query.getID());
                                out.println(response);
                            } else System.out.println("Game not found");
                            break;
                    }
                }

                System.out.println("Terminated");
                in.close();
                out.close();
                clientSocket.close();

            } catch (IOException e) {
                System.out.println("Exception caught when trying to listen on a socket or listen for a connection");
                System.out.println(e.getMessage());
            }
        }
    }
}
