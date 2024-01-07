package Controller;

import Model.Player_and_Board.Player;
import Model.game_characters.GameCharacters;
import Protocol.Protocol;
import Protocol.Flag;
import View.View;

import Packet.Packet;
import com.google.gson.reflect.TypeToken;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class ControllerClient {
    private static final int[] positionBuffer = new int[2];
    private static View view = null;
    private static int gameMode;
    private static Player attacker;
    private static Player defender;
    private boolean revive;
    private int reviveMonster;
    private GameCharacters winner;
    private String message;
    private static final Packet packet = new Packet();
    static AtomicBoolean waitingResponse = new AtomicBoolean(false);
    static Socket socket;
    static PrintWriter out;
    ServerThread serverThread;
    UserThread userThread;
    static ButtonsPressed gameHandler;
    static int[][] board;

    // TODO: Classes that extend Thread should implement Runnable. Extending Thread not good practice.
    // TODO: Add all listeners in different file and give necessary variables, through constructor.
    public static void main(String[] args) { new ControllerClient(); }

    public ControllerClient(){
        gameMode = 0;
        this.revive = false;
        this.winner = null;
        this.reviveMonster = 0;
        gameHandler = new ButtonsPressed();
        view = new View(new UIPressed());
    }

    private void establishConnection(String address){
        try {
            socket = new Socket(address, 1234);
            out = new PrintWriter(socket.getOutputStream(), true);
            this.serverThread = new ServerThread();
            this.userThread = new UserThread(gameHandler);
            this.userThread.start();
            this.serverThread.start();
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + e.getMessage());
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " + e.getMessage());
            System.exit(1);
        }

    }
    // Ξεκινάει το παιχνίδι, μόλις επιλεχτεί gameMode από το αρχικό UI.
    private void search_game(){
        String address = "127.0.0.1";
        if (socket == null || socket.isClosed()) establishConnection(address);
        message = packet.generatePacket(Protocol.FIND_MATCH, false, (Integer) gameMode);
        out.println(message);
        // TODO: SEND message to server.
    }

    private boolean gameContinues() {
        return ( attacker.matchContinues() && defender.matchContinues() );
    }

    // TODO: Change names and add in Timer first calls of continueMoveMonster.
    static void displayBattle(int[] userPos, int[] concealedPos, int concealedMonster) {
        // TODO: If attack or defense. Maybe in flag, we'll see.
        // TODO: Reformat displayBattlingMonsters to take as argument two int[].
        // TODO: Fix timer.
        view.displayBattlingMonsters(userPos, concealedPos, concealedMonster);
        Timer timer = new Timer(500, null);
        timer.setRepeats(false); // Ensure the timer only runs once
        timer.start();
        try{Thread.sleep(500);}catch (InterruptedException e){e.printStackTrace();}
    }

    private void restartPosition(){
        positionBuffer[0] = -1;
        positionBuffer[1] = -1;
    }

    // Ελέγχει εάν έχει επιλεχτεί σωστό τέρας.
    private boolean isValidMonster(int line, int row){
        return board[line][row] >= 1 && board[line][row] <= 10;
    }

    private void isValidMovingPosition(int nextLine, int nextRow){
        message = packet.generatePacket(Protocol.MOVE, false, new int[]{nextLine, nextRow});
        out.println(message);
        // TODO: Send message to server. Server must hold monster player has chosen.
    }


    // Αλλάζει τον γύρο.
    private void nextRound(){
        Player temp = attacker;
        attacker = defender;
        defender = temp;

        view.nextRound(attacker, defender);
        restartPosition();
    }

    private void restartGame(){
        // Ξεκινάει πάντα ο μπλε.
        // Δεν αλλάζει το mode παιχνιδιού, το
        if(attacker.getID() != 1){
            Player temp = attacker;
            attacker = defender;
            defender = temp;
        }

        attacker.restartGame(defender);

        view.restartGame(attacker, defender);
    }

    // Μετά την αναγέννηση, επανεμφανίζει το παιχνίδι, αλλάζει γύρο και καθιστά  false τη μεταβλητή revive.
    private void resumeGame(){
        this.nextRound();
        this.revive = false;
    }

    static class ServerThread extends Thread {
        private BufferedReader in;
        private String serverInput;
        private boolean running = false;

        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(ControllerClient.socket.getInputStream()));
                while(waitingServerInput()){
                    // TODO: Act accordingly on View package.
                    // TODO: Maybe add enemy buffer.
                    System.out.println("Echo: " + serverInput);
                    Packet server = Packet.convertMessageToPacket(serverInput);

                    switch (server.getProtocol()) {
                        case Protocol.WELCOME -> { if (server.assignedID()) packet.setPlayerID(server); }

                        case Protocol.START_GAME -> {
                            if (!running) {
                                board = server.extractData(int[][].class);
                                view.startGame(gameHandler, gameMode);
                                view.test(board, server.getFlag());
                                running = true;
                            }
                        }
                        case Protocol.SELECT -> {
                            if (server.getFlag() == Flag.HIGHLIGHT && server.approved()) {
                                List<Integer> positions = server.extractData(new TypeToken<List<Integer>>() {
                                }.getType());
                                view.highlightPositions(positions);
                                ButtonsPressed.selected = true;
                            }
                        }
                        // TODO: Synchronise board's elements.
                        case Protocol.MOVE -> { if(server.approved()) handleMoveFlag(server);}
                        //TODO: Create a new method in view that takes argument one int[] (move).
                        case Protocol.ENEMY_MOVE -> handleEnemyMoveFlag(server);
                    }
                    ControllerClient.waitingResponse.set(false);
                }
            } catch (IOException e) {
                System.err.println("Error reading server input: " + e.getMessage());
                System.exit(1);
            }
        }
        // TODO: Handle them properly.
        private void handleMoveFlag(Packet server){
            int[] move = server.extractData(int[].class);
            if (server.getFlag() == Flag.NO_COMBAT){
                view.moveCharacter(positionBuffer, move);
                ButtonsPressed.selected = false;
                board[move[0]][move[1]] = board[positionBuffer[0]][positionBuffer[1]];
                board[positionBuffer[0]][positionBuffer[1]] = -2;
            }
            else {
                displayBattle(positionBuffer, move, move[2]);
                switch (server.getFlag()) {
                    case Flag.COMBAT_VICTORIOUS -> {
                        view.moveCharacter(positionBuffer, move);
                        board[move[0]][move[1]] = board[positionBuffer[0]][positionBuffer[1]];
                        board[positionBuffer[0]][positionBuffer[1]] = -2;
                    }
                    case Flag.COMBAT_DEFEATED -> {
                        view.killMonster(positionBuffer);
                        board[positionBuffer[0]][positionBuffer[1]] = -2;
                        view.concealMonster(move);
                    }
                    case Flag.COMBAT_TIE -> {
                        view.killBothMonsters(positionBuffer, move);
                        board[positionBuffer[0]][positionBuffer[1]] = -2;
                        board[move[0]][move[1]] = -2;
                    }
                }
            }
        }

        private void handleEnemyMoveFlag(Packet server){
            int[] move = server.extractData(int[].class);
            if(server.getFlag() == Flag.NO_COMBAT){
                view.moveCharacter(move, new int[]{move[2], move[3]});
                board[move[2]][move[3]] = board[move[0]][move[1]];
                board[move[0]][move[1]] = -2;
            }
            else {
                displayBattle(new int[]{move[2], move[3]}, move, move[4]);
                switch (server.getFlag()) {
                    case Flag.COMBAT_DEFEATED -> {
                        view.killMonster(new int[] {move[2], move[3]});
                        view.moveCharacter(move, new int[]{move[2], move[3]});
                        board[move[2]][move[3]] = board[move[0]][move[1]];
                        board[move[0]][move[1]] = -2;
                    }
                    case Flag.COMBAT_VICTORIOUS -> {
                        view.killMonster(move);
                        board[move[0]][move[1]] = -2;
                    }
                    case Flag.COMBAT_TIE -> {
                        view.killBothMonsters(move, new int[]{move[2], move[3]});
                        board[move[0]][move[1]] = -2;
                        board[move[2]][move[3]] = -2;
                    }
                }
            }
        }

        private boolean waitingServerInput() throws IOException {return (serverInput = in.readLine()) != null;}

    }

    // TODO: Instantiate view game here. Create the board here and then let the other thread place pictures.
    static class UserThread extends Thread {
        MouseListener gameHandler;

        UserThread(MouseListener gameHandler) {
            super();
            this.gameHandler = gameHandler;
        }

        public void run() {
            positionBuffer[0] = -1;
            positionBuffer[1] = -1;
        }

        //private boolean canRequest() {return !Client.waitingResponse.get();}

    }

    // Για τα κλικ του πίνακα παιχνιδιού.
    class ButtonsPressed implements MouseListener {
        static boolean selected = false;
        @Override
        public void mouseClicked(MouseEvent e) {
            JButton pressedButton = ((JButton) e.getSource());

            if (SwingUtilities.isLeftMouseButton(e)) {

                // Αν έχει πατηθεί το αριστερό κλικ. Τα κουμπιά έχουν ονομασίες του τύπο 18,
                // όπου το 1 αποτελεί τη γραμμή του τέρατος και το 8 αποτελεί τη στήλη του τέρατος.
                int monstersLine = pressedButton.getName().charAt(0) - '0';
                int monstersRow = pressedButton.getName().charAt(1) - '0';

                // Υποδεικνύει ότι τέρας πρέπει να αναγεννηθεί.
                if(revive){
                    if(attacker.isValidPositionForRevival(monstersLine, monstersRow)){
                        if(!attacker.reviveMonster(winner, monstersLine, monstersRow, defender, reviveMonster))
                            view.selectMonsterToRevive(new revivePanelPressed(), attacker);
                        else
                            resumeGame();
                    }
                }
                else {
                    if (isValidMonster(monstersLine, monstersRow)) {
                        positionBuffer[0] = monstersLine; positionBuffer[1] = monstersRow;
                        message = packet.generatePacket(Protocol.SELECT, false, positionBuffer);
                        out.println(message);
                    }

                    else if (selected) {
                        // Εάν η τοποθεσία που ο παίκτης θέλει να μετακινήσει το τέρας είναι μη-έγκυρη.
                        isValidMovingPosition(monstersLine, monstersRow);
                        // TODO: If is valid moving position, move monster.
                        //moveMonster(positionBuffer[0], positionBuffer[1], monstersLine, monstersRow);
                    }
                }
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
        }

        @Override
        public void mouseReleased(MouseEvent e) {
        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent e) {
        }
    }

    // Για τα κλικ του UI.
    class UIPressed implements MouseListener{

        @Override
        public void mouseClicked(MouseEvent e) {
            JButton click = (JButton) e.getSource();

            int mode = click.getName().charAt(0) - '0';

            if(gameMode != 1 && gameMode != 3 && mode == 1) gameMode += 1;
            else if (gameMode != 2 && gameMode != 3 && mode == 2) gameMode += 2;
            else if(mode == 3) {
                search_game();
            }
        }


        @Override
        public void mousePressed(MouseEvent e) {}
        @Override
        public void mouseReleased(MouseEvent e) {}
        @Override
        public void mouseEntered(MouseEvent e) {}
        @Override
        public void mouseExited(MouseEvent e) {}
    }


    class revivePanelPressed implements MouseListener{

        @Override
        public void mouseClicked(MouseEvent e) {
            JButton button = (JButton) e.getSource();
            // TODO: Switch case.
            if(button.getName().equalsIgnoreCase("exit")){
                System.exit(0);
            }
            else if(button.getName().equalsIgnoreCase("replay")){
                restartGame();
            }
            else {
                int monster = button.getName().charAt(0) - '0';

                // Αν επιλέχτηκε σωστό τέρας για να αναγεννηθεί, τότε επέλεξε τοποθεσία αναγέννησης.
                if (attacker.monsterCanRevive(monster)) {
                    reviveMonster = monster;
                    view.selectPositionToRevive(attacker.getValidRevivalPositions());
                    revive = true;
                }
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {

        }

        @Override
        public void mouseReleased(MouseEvent e) {

        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }
    }

}

