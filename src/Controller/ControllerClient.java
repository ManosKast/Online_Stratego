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
import java.lang.reflect.Type;
import java.net.*;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class ControllerClient {
    private static final int[] positionBuffer = new int[2];
    private static View view = null;
    private static int gameMode;
    private static Player attacker;
    private static Player defender;
    private boolean revive;
    private boolean deployment;
    private int reviveMonster;
    private static int selectedMonster;
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
    static int playerID;
    static final int cardsBack = 12;

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
        // TODO: Maybe add in view.
        // TODO: ongoingAttack global var.
        view.displayBattlingMonsters(userPos, concealedPos, concealedMonster);
        Timer timer = new Timer(500, e->{view.concealMonster(concealedPos);});
        timer.setRepeats(false); // Ensure the timer only runs once
        timer.start();
        try{Thread.sleep(510);} catch (InterruptedException e){e.printStackTrace();}
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
        System.out.println("Next line: " + nextLine + " Next row: " + nextRow);
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
        this.restartPosition();
        this.revive = false;
        this.reviveMonster = 0;
    }

    class ServerThread extends Thread {
        private BufferedReader in;
        private String serverInput;
        private boolean running = false;

        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                while(awaitingServerInput()){
                    // TODO: Act accordingly on View package.
                    // TODO: Maybe add enemy buffer.
                    System.out.println("Echo: " + serverInput);
                    Packet server = Packet.convertMessageToPacket(serverInput);

                    switch (server.getProtocol()) {
                        case Protocol.WELCOME -> {
                            if (server.assignedID()) {
                                String[] data = server.extractData(String[].class);
                                playerID = Integer.parseInt(data[0]);
                                packet.setPlayerID(data[1]);
                            }
                        }

                        case Protocol.START_GAME -> {
                            if (!running) {
                                board = server.extractData(int[][].class);
                                view.startGame(gameHandler, new BoardSetUp(), gameMode, server.getFlag());
                                view.test(board);
                                running = true;
                                deployment = true;
                            }
                        }
                        case Protocol.SELECT -> {
                            if (server.getFlag() == Flag.HIGHLIGHT && server.approved()) {
                                Type type = new TypeToken<List<Integer>>(){}.getType();
                                List<Integer> positions = server.extractData(type);
                                view.highlightPositions(positions);
                                ButtonsPressed.selected = true;
                            }
                        }

                        case Protocol.REVIVE -> {if(server.approved()) handleReviveFlag(server);}
                        case Protocol.ENEMY_REVIVE -> handleEnemyReviveFlag(server);

                        // TODO: Synchronise board's elements.
                        case Protocol.MOVE -> { if(server.approved()) handleMoveFlag(server);}
                        //TODO: Create a new method in view that takes argument one int[] (move).
                        case Protocol.ENEMY_MOVE -> handleEnemyMoveFlag(server);

                        case Protocol.GAME_OVER -> {
                            if(server.getFlag() == Flag.OPPONENT_EXITED) {
                                view.opponentExited();
                                continue;
                            }
                            String outcome = (server.getFlag() == Flag.WON) ? "You won" : "You lost";
                            view.endGame(new RevivePanelPressed(), outcome);
                        }

                        case Protocol.REPLAY -> {
                            if(server.approved()) {
                                board = server.extractData(int[][].class);
                                view.restartGame(board, server.getFlag());
                            }
                        }

                        case Protocol.BOARD_SETUP -> {
                            if(server.approved()) handleBoardSetUpFlags(server);
                            else view.clearBorders();
                        }

                        case Protocol.ENEMY_BOARD_SETUP -> handleEnemyBoardSetUpFlags(server);
                    }
                    waitingResponse.set(false);
                }
            } catch (IOException e) {
                System.err.println("Error reading server input: " + e.getMessage());
                System.exit(1);
            }
        }

        // TODO: Handle them properly.
        private void handleMoveFlag(Packet server){
            int[] move = server.extractData(int[].class);
            byte flag = server.getFlag();
            if (flag == Flag.NO_COMBAT){
                view.moveCharacter(positionBuffer, move);
                ButtonsPressed.selected = false;
                board[move[0]][move[1]] = board[positionBuffer[0]][positionBuffer[1]];
                board[positionBuffer[0]][positionBuffer[1]] = -2;
                view.nextRound();
            }
            else {
                System.out.println(Arrays.toString(move));
                int enemyMonster = move[2];
                int ratio = move[3];
                System.out.println("Ratio: " + ratio);
                displayBattle(positionBuffer, move, enemyMonster);
                switch (flag) {
                    case Flag.COMBAT_VICTORIOUS -> {
                        view.moveCharacter(positionBuffer, move);
                        board[move[0]][move[1]] = board[positionBuffer[0]][positionBuffer[1]];
                        board[positionBuffer[0]][positionBuffer[1]] = -2;
                        view.updateCaptivePanel(enemyMonster);
                    }
                    case Flag.COMBAT_DEFEATED -> {
                        view.killMonster(positionBuffer);
                        board[positionBuffer[0]][positionBuffer[1]] = -2;
                    }
                    case Flag.COMBAT_TIE -> {
                        view.killBothMonsters(positionBuffer, move);
                        board[positionBuffer[0]][positionBuffer[1]] = -2;
                        board[move[0]][move[1]] = -2;
                        view.updateCaptivePanel(enemyMonster);
                    }
                }
                view.nextRound(ratio);
            }
        }

        // TODO: Add variable names that are more descriptive.
        private void handleEnemyMoveFlag(Packet server){
            int[] move = server.extractData(int[].class);
            byte flag = server.getFlag();
            if(flag == Flag.NO_COMBAT){
                view.moveCharacter(move, new int[]{move[2], move[3]});
                board[move[2]][move[3]] = board[move[0]][move[1]];
                board[move[0]][move[1]] = -2;
            }
            else {
                int enemyMonster = move[4];
                displayBattle(new int[]{move[2], move[3]}, move, enemyMonster);
                switch (flag) {
                    case Flag.COMBAT_DEFEATED -> {
                        view.killMonster(new int[] {move[2], move[3]});
                        view.moveCharacter(move, new int[]{move[2], move[3]});
                        board[move[2]][move[3]] = board[move[0]][move[1]];
                        board[move[0]][move[1]] = -2;
                    }
                    case Flag.COMBAT_VICTORIOUS -> {
                        view.killMonster(move);
                        board[move[0]][move[1]] = -2;
                        view.updateCaptivePanel(enemyMonster);
                    }
                    case Flag.COMBAT_TIE -> {
                        view.killBothMonsters(move, new int[]{move[2], move[3]});
                        board[move[0]][move[1]] = -2;
                        board[move[2]][move[3]] = -2;
                        view.updateCaptivePanel(enemyMonster);
                    }
                }
            }
            view.nextRound();
        }

        private void handleReviveFlag(Packet server) {
            byte flag = server.getFlag();

            switch(flag) {
                case Flag.REVIVE_PANEL -> {
                    int[] capturedMonsters = server.extractData(int[].class);
                    view.selectMonsterToRevive(new RevivePanelPressed(), playerID, capturedMonsters);
                }

                case Flag.REVIVE_MONSTER -> {
                    Type type = new TypeToken<List<Integer>>(){}.getType();
                    List<Integer> positions = server.extractData(type);
                    view.selectPositionToRevive(positions);
                    revive = true;
                }

                case Flag.REVIVE_POSITION -> {
                    int[] position = server.extractData(int[].class);
                    view.reviveMonster(reviveMonster + 2, position);
                    int row = position[0], column = position[1];
                    board[row][column] = reviveMonster;
                    view.nextRound();
                    resumeGame();
                }
            }
        }

        private void handleEnemyReviveFlag(Packet server) {
            byte flag = server.getFlag();

            switch (flag) {
                case Flag.REVIVED -> {
                    int[] revivalPosition = server.extractData(int[].class);
                    int enemyMonster = revivalPosition[2];
                    int row = revivalPosition[0], column = revivalPosition[1];
                    view.enemyRevivedMonster(enemyMonster, revivalPosition);
                    board[row][column] = cardsBack;
                    view.nextRound();
                }

                case Flag.WAIT -> view.enemyReviving();
            }
        }

        private void handleBoardSetUpFlags(Packet server) {
            byte flag = server.getFlag();

            switch (flag) {
                case Flag.FINALISE_BOARD -> {
                    board = server.extractData(int[][].class);
                    view.startGame(gameHandler, new BoardSetUp(), gameMode, flag);
                    restartPosition();
                }

                case Flag.SELECT_MONSTER -> {
                    Type type = new TypeToken<List<Integer>>(){}.getType();
                    List<Integer> positions = server.extractData(type);
                    view.highlightPositions(positions);
                }

                case Flag.POSITION_MONSTER -> {
                    int[] position = server.extractData(int[].class);
                    selectedMonster = position[2];
                    view.positionMonster(selectedMonster, position);
                    board[position[0]][position[1]] = selectedMonster;
                    selectedMonster = -1;
                }

                case Flag.CLEAR_POSITION -> {
                    int[] position = server.extractData(int[].class);
                    int monster = position[2];
                    view.clear(position, monster);
                    board[position[0]][position[1]] = -2;
                }

                case Flag.RANDOMISE_BOARD -> {
                    Type type = new TypeToken<List<Integer>>(){}.getType();
                    List<Integer> positions = server.extractData(type);
                    for(int i = 0; i < positions.size(); ++i){
                        int row = positions.get(i);
                        int column = positions.get(++i);
                        int monster = positions.get(++i);
                        board[row][column] = monster;
                        // TODO: Don't create new array.
                        view.positionMonster(monster, new int[]{row, column});
                    }
                    selectedMonster = -1;
                }

                case Flag.WAITING_OPPONENT -> {
                    byte turn = server.extractData(byte.class);
                    view.startGame(Flag.WAITING_OPPONENT, turn);
                    deployment = false;
                }

                case Flag.OPPONENT_READY -> {
                    byte turn = server.extractData(byte.class);
                    view.opponentReady(Flag.OPPONENT_READY, turn);
                }

                case Flag.START_GAME -> {
                    byte turn = server.extractData(byte.class);
                    view.startGame(Flag.START_GAME, turn);
                    deployment = false;
                }
            }
        }

        private void handleEnemyBoardSetUpFlags(Packet packet) {
            byte flag = packet.getFlag();
            // TODO: position arraylist, since enemy can randomise.
            int[] position = packet.extractData(int[].class);
            if (flag == Flag.POSITION_MONSTER) view.positionMonster(cardsBack, position);
            else if (flag == Flag.CLEAR_POSITION) view.killMonster(position);
            else if (flag == Flag.RANDOMISE_BOARD) {
                for (int i = 0; i < position.length; ++i) {
                    int row = position[i];
                    int column = position[++i];
                    board[row][column] = cardsBack;
                    view.positionMonster(cardsBack, new int[]{row, column});
                }
            }
        }

        private boolean awaitingServerInput() throws IOException {return (serverInput = in.readLine()) != null;}

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
                int row = pressedButton.getName().charAt(0) - '0';
                int column = pressedButton.getName().charAt(1) - '0';

                // Υποδεικνύει ότι τέρας πρέπει να αναγεννηθεί.
                if(revive){
                    int[] position = new int[]{row, column};
                    message = packet.generatePacket(Protocol.REVIVE, Flag.REVIVE_POSITION, false, position);
                    out.println(message);
                } else if (deployment) {
                    positionBuffer[0] = row;
                    positionBuffer[1] = column;
                    if (board[row][column] >= 0) {
                        message = packet.generatePacket(Protocol.BOARD_SETUP, Flag.CLEAR_POSITION, false, positionBuffer);
                        out.println(message);
                    }
                    if (selectedMonster >= 0) {
                        message = packet.generatePacket(Protocol.BOARD_SETUP, Flag.POSITION_MONSTER, false, positionBuffer);
                        out.println(message);
                    }
                } else {
                    if (isValidMonster(row, column)) {
                        // TODO: Remove isValidMonster, server should dictate Position Buffer.
                        // TODO: Update position buffer if Protocol.SELECT is approved.
                        positionBuffer[0] = row;
                        positionBuffer[1] = column;
                        message = packet.generatePacket(Protocol.SELECT, false, positionBuffer);
                        out.println(message);
                    } else if (selected) {
                        // Εάν η τοποθεσία που ο παίκτης θέλει να μετακινήσει το τέρας είναι μη-έγκυρη.
                        isValidMovingPosition(row, column);
                        // TODO: If is valid moving position, move monster.
                        //moveMonster(positionBuffer[0], positionBuffer[1], row, column);
                    }
                }
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

    // TODO: Change name
    class RevivePanelPressed implements MouseListener{

        @Override
        public void mouseClicked(MouseEvent e) {
            JButton button = (JButton) e.getSource();
            String operation = button.getName().toLowerCase();
            String query;

            switch (operation) {
                // TODO: Fix exit.
                case "exit" -> {
                    query = packet.generatePacket(Protocol.EXIT, false, null);
                    out.println(query);
                    System.exit(0);
                }
                case "replay" -> query = packet.generatePacket(Protocol.REPLAY, false, null);
                default -> {
                    int monster = button.getName().charAt(0) - '0';
                    reviveMonster = monster - 1;
                    query = packet.generatePacket(Protocol.REVIVE, Flag.REVIVE_MONSTER, false, monster);
                }
            }

            out.println(query);
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


    static class BoardSetUp implements MouseListener {

        @Override
        public void mouseClicked(MouseEvent e) {
            JButton pressedButton = ((JButton) e.getSource());

            if (SwingUtilities.isLeftMouseButton(e)) {
                String operation = pressedButton.getName();
                String query;
                switch(operation) {
                    case "finalise" -> query = packet.generatePacket(Protocol.BOARD_SETUP, Flag.FINALISE_BOARD, false, null);

                    case "randomise" -> query = packet.generatePacket(Protocol.BOARD_SETUP, Flag.RANDOMISE_BOARD, false, null);

                    default -> {
                        selectedMonster = Integer.parseInt(operation);
                        String monster = pressedButton.getName();
                        query = packet.generatePacket(Protocol.BOARD_SETUP, Flag.SELECT_MONSTER, false, monster);
                    }
                }

                out.println(query);
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
}

