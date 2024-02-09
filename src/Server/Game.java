package Server;

import Controller.Pair;
import Packet.*;
import Protocol.Protocol;
import Protocol.Flag;

import Model.Player_and_Board.Player;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import Model.game_characters.GameCharacters;


public class Game {
    private final int[] positionBuffer = new int[2];
    private int gameMode;
    private Player attacker;
    private Player defender;
    private boolean revive;
    private String packet1;
    private String packet2;
    private int reviveMonster;
    private GameCharacters reviver;
    private GameCharacters winner;
    private Packet packet = new Packet();
    private final Client player1;
    private final Client player2;
    private final Pair<String> packetBuffer = new Pair<String>();
    private PrintWriter attackerOut;
    private PrintWriter defenderOut;
    private ExecutorService executor;
    private boolean gameOver = false;
    private final int[] selectedMonster;
    private final boolean[] ready = new boolean[2];
    Player invertedPlayer;

    public Game(int gameMode, Client player1, Client player2) throws IOException {
        this.gameMode = gameMode;
        this.revive = false;
        this.winner = null;
        this.reviveMonster = 0;
        this.player1 = player1;
        this.player2 = player2;
        selectedMonster = new int[]{-1, -1};

        initialiseGame();
        this.attackerOut = new PrintWriter(player1.getSocket().getOutputStream(), true);
        this.defenderOut = new PrintWriter(player2.getSocket().getOutputStream(), true);
        this.executor = Executors.newFixedThreadPool(2);
    }

    // Ξεκινάει το παιχνίδι, μόλις επιλεχτεί gameMode από το αρχικό UI.
    private void initialiseGame() {
        // TODO: Πρόσθεσε μήνυμα και username στους Player. Πρόσθεσε τα στον constructor.
        // Δημιουργεί τους δύο παίκτες. Ξεκινάει πρώτος ο μπλε, αν δεν απατώμαι.
        this.attacker = new Player(gameMode, player1.getID());
        this.defender = new Player(gameMode, player2.getID());
        this.invertedPlayer = attacker;
        System.out.println("Attacker: " + attacker.getID() + " Defender: " + defender.getID());
        positionBuffer[0] = -1;
        positionBuffer[1] = -1;
    }

    public String startGame(){
        GameCharacters[][] board = attacker.getBoard();
        int[][] icons = new int[board.length][board[0].length];
        for(int i = 0; i < board.length; ++i)
            for(int j = 0; j < board[i].length; ++j)
                if (board[i][j] != null)
                    icons[i][j] = board[i][j].getPower();
        return packet.generatePacket(Protocol.START_GAME, true, icons);
    }

    // TODO: Remove and use only the one with 3 arguments.
    public String getPlayersBoard(String player, byte flag){
        return getPlayersBoard(player, flag, Protocol.START_GAME);
    }

    // TODO: Add this on board. No access to board directly.
    public String getPlayersBoard(String player, byte flag, byte protocol){
        GameCharacters[][] board = attacker.getBoard();
        int[][] icons = new int[board.length][board[0].length];
        Player invertedPlayer = (attacker.getPlayerID().equals(player)) ? attacker : defender;
        // TODO: Remove comments and make it prettier.
        // TODO: Invert attacker's board.
        /*
        if (invertedPlayer.getID() == 1) {
            icons = invertedPlayer.revertBoard();
            return packet.generatePacket(Protocol.START_GAME, flag, true, icons);
        }

         */
        if (attacker.getPlayerID().equals(player)){
            for (int i = 0; i < board.length / 2; ++i) {
                for (int j = 0; j < board[i].length; ++j) {
                    if (board[i][j] != null) icons[i][j] = board[i][j].getPower();
                    else icons[i][j] = -2;
                }
            }

            for (int i = board.length / 2 - 1; i < board.length; ++i){
                for (int j = 0; j < board[i].length; ++j) {
                    if (board[i][j] != null && board[i][j].getPower() != -1) icons[i][j] = 12;
                    else if (board[i][j] == null) icons[i][j] = -2;
                    else icons[i][j] = -1;
                }
            }

        } else {
            for (int i = 0; i < board.length / 2; ++i) {
                for (int j = 0; j < board[i].length; ++j) {
                    if (board[i][j] != null && board[i][j].getPower() != -1) icons[i][j] = 12;
                    else if(board[i][j] == null) icons[i][j] = -1;
                    else icons[i][j] = -2;
                }
            }

            for (int i = board.length / 2; i < board.length; ++i){
                for (int j = 0; j < board[i].length; ++j) {
                    if (board[i][j] != null) icons[i][j] = board[i][j].getPower();
                    else icons[i][j] = -2;
                }
            }
        }

        return packet.generatePacket(protocol, flag, true, icons);
    }

    private boolean gameContinues() {
        return (attacker.matchContinues() && defender.matchContinues());
    }

    public String selectMonster(int row, int column, String player){
        if (revive) return packet.generatePacket(Protocol.SELECT, false, 0);
        if (isValidMonster(row, column) && isPlayersTurn(player)){
            positionBuffer[0] = row;
            positionBuffer[1] = column;
            return packet.generatePacket(Protocol.SELECT, Flag.HIGHLIGHT, true, attacker.getMoveablePath(row, column));
        }
        return packet.generatePacket(Protocol.SELECT, false, 0);
    }

    // TODO: Change names and add in Timer first calls of continueMoveMonster.
    // TODO: Change to package, after reformatting the code.
    // TODO: Should return void.
    public void moveMonster(int nextRow, int nextCol, String player) {
        boolean battle = false;
        // TODO: Add check if players turn.
        if(!isValidMovingPosition(nextRow, nextCol)) {
            packet1 = packet.generatePacket(Protocol.MOVE, false, 0);
            packetBuffer.setFirst(packet1); packetBuffer.setSecond(null);
            attackerOut.println(packet1);
            return;
        }
        // TODO: Change packet to packet2 or defenderPacket.
        if (!isPlayersTurn(player)) {
            packet1 = packet.generatePacket(Protocol.MOVE, false, 0);
            packetBuffer.setFirst(packet1); packetBuffer.setSecond(null);
            defenderOut.println(packet1);
            return;
        }

        if(revive) {
            packet1 = packet.generatePacket(Protocol.MOVE, false, 0);
            packetBuffer.setFirst(packet1); packetBuffer.setSecond(null);
            attackerOut.println(packet1);
            return;
        }

        GameCharacters monster = defender.getMonster(nextRow, nextCol);
        int mIndex = (monster != null) ? ((monster.getPower() == Integer.MAX_VALUE) ? 11 : monster.getPower()) : -2;
        int[] moveTo = {nextRow, nextCol, mIndex};
        int[] move = {positionBuffer[0], positionBuffer[1], nextRow, nextCol, attacker.getMonster(positionBuffer[0], positionBuffer[1]).getPower()};
        // TODO: Add message. Change moveCharacter so there is no battle boolean.
        if (attacker.isAttacking(nextRow, nextCol)) battle = true;

        winner = attacker.moveCharacter(positionBuffer[0], nextRow, positionBuffer[1], nextCol, defender);

        // Αν τελείωσε το παιχνίδι.
        // TODO: Maybe get it smoother. Change flags and protocols to include everything in one packet and view the battle.
        // TODO: Add checks to see who won.
        if (!gameContinues()) {
            if (winner != null) {
                packet1 = packet.generatePacket(Protocol.GAME_OVER, Flag.WON, true, 0);
                packet2 = packet.generatePacket(Protocol.GAME_OVER, Flag.LOST, true, 0);
            }
            else {
                packet1 = packet.generatePacket(Protocol.GAME_OVER, Flag.LOST, true, 0);
                packet2 = packet.generatePacket(Protocol.GAME_OVER, Flag.WON, true, 0);
            }

            gameOver = true;
            executor.submit(new SendMessage(attackerOut, packet1));
            executor.submit(new SendMessage(defenderOut, packet2));
            return;
        }

        if(battle){
            // Collect statistics.
            int[] attackerData = new int[moveTo.length + 1];
            System.arraycopy(moveTo, 0, attackerData, 0, moveTo.length);
            attackerData[attackerData.length - 1] = attacker.getSuccessfulAttackRatio();

            if(winner == null){
                packet1 = packet.generatePacket(Protocol.MOVE, Flag.COMBAT_TIE, true, attackerData);
                packet2 = packet.generatePacket(Protocol.ENEMY_MOVE, Flag.COMBAT_TIE, true, move);
            }
            else if (winner.getPlayersID() == attacker.getID()){
                packet1 = packet.generatePacket(Protocol.MOVE, Flag.COMBAT_VICTORIOUS, true, attackerData);
                packet2 = packet.generatePacket(Protocol.ENEMY_MOVE, Flag.COMBAT_DEFEATED, true, move);
            }
            else {
                packet1 = packet.generatePacket(Protocol.MOVE, Flag.COMBAT_DEFEATED, true, attackerData);
                packet2 = packet.generatePacket(Protocol.ENEMY_MOVE, Flag.COMBAT_VICTORIOUS, true, move);
            }
        } else {
            packet1 = packet.generatePacket(Protocol.MOVE, Flag.NO_COMBAT, true, moveTo);
            packet2 = packet.generatePacket(Protocol.ENEMY_MOVE, Flag.NO_COMBAT, true, move);
        }

        executor.submit(new SendMessage(attackerOut, packet1));
        executor.submit(new SendMessage(defenderOut, packet2));

        if (attacker.canRevive(winner, defender)) {
            int[] capturedMonsters = attacker.getCapturedMonsters();
            reviver = winner;
            packet1 = packet.generatePacket(Protocol.REVIVE, Flag.REVIVE_PANEL, true, capturedMonsters);
            packet2 = packet.generatePacket(Protocol.ENEMY_REVIVE, Flag.WAIT, true, 0);

            // TODO: Create function send messages instead of having executor.submits.
            executor.submit(new SendMessage(attackerOut, packet1));
            executor.submit(new SendMessage(defenderOut, packet2));
        } else nextRound();
    }

    public void canReviveMonster(String player, int monster) {
        if (!isPlayersTurn(player)) {
            packet2 = packet.generatePacket(Protocol.REVIVE, Flag.REVIVE_MONSTER, false, 0);
            defenderOut.println(packet2);
        }
        else if (attacker.isCaptured(monster)) {
            revive = true;
            reviveMonster = monster;
            List<Integer> highlightPositions = attacker.getValidRevivalPositions();
            packet1 = packet.generatePacket(Protocol.REVIVE, Flag.REVIVE_MONSTER, true, highlightPositions);
            attackerOut.println(packet1);
        }
    }

    public void reviveMonster(String player, int[] position) {
        if (!isPlayersTurn(player)) {
            defenderOut.println(packet.generatePacket(Protocol.REVIVE, Flag.REVIVE_POSITION, false, 0));
        } else if (attacker.reviveMonster(reviver, position[0], position[1], defender, reviveMonster)) {
            int[] data = {position[0], position[1], reviveMonster};
            packet1 = packet.generatePacket(Protocol.REVIVE, Flag.REVIVE_POSITION, true, position);
            packet2 = packet.generatePacket(Protocol.ENEMY_REVIVE, Flag.REVIVED, true, data);
            executor.submit(new SendMessage(attackerOut, packet1));
            executor.submit(new SendMessage(defenderOut, packet2));
            revive = false;
            this.nextRound();
        }
    }
    private void restartPosition() {
        this.positionBuffer[0] = -1;
        this.positionBuffer[1] = -1;
    }

    // Ελέγχει εάν έχει επιλεχτεί σωστό τέρας.
    private boolean isValidMonster(int line, int row) {
        return attacker.isMovingMonster(line, row);
    }

    private boolean isValidMovingPosition(int nextLine, int nextRow) {
        return attacker.canMove(positionBuffer[0], positionBuffer[1], nextLine, nextRow);
    }

    private boolean monsterHasBeenSelected(int line, int row) {
        return (this.positionBuffer[0] != -1 && !attacker.isThisPlayersMonster(line, row));
    }

    // Αλλάζει τον γύρο.
    private void nextRound() {
        Player temp = attacker;
        attacker = defender;
        defender = temp;

        PrintWriter tempOut = attackerOut;
        attackerOut = defenderOut;
        defenderOut = tempOut;

        this.restartPosition();
    }

    public void restartGame() {
        // Ξεκινάει πάντα ο μπλε.
        // Δεν αλλάζει το mode παιχνιδιού, το
        if (attacker.getID() != 1)
            nextRound();

        player1.noOperation();
        player2.noOperation();

        attacker.restartGame(defender);
        packet1 = getPlayersBoard(attacker.getPlayerID(), Flag.FIRST, Protocol.REPLAY);
        packet2 = getPlayersBoard(defender.getPlayerID(), Flag.SECOND, Protocol.REPLAY);

        executor.submit(new SendMessage(attackerOut, packet1));
        executor.submit(new SendMessage(defenderOut, packet2));
    }

    // Μετά την αναγέννηση, επανεμφανίζει το παιχνίδι, αλλάζει γύρο και καθιστά  false τη μεταβλητή revive.
    private void resumeGame() {
        this.nextRound();
        this.revive = false;
    }

    // TODO: Add string ID to Player and then remove comment.
    public boolean isPlayersTurn(String player){
        return attacker.getPlayerID().equals(player);
    }

    public boolean isOver() {
        return gameOver;
    }

    public int canRestart() {
        return (player1.exited() || player2.exited()) ? -1 : (player1.restart() && player2.restart()) ? 1 : 0;
    }

    // If a player exited and the other player wants to continue, find him another game.
    public Client findClientGame() {
        return (player1.exited()) ? player2 : ((player2.exited()) ? player1 : null);

    }

    public void isAvailableMonster(String player, int monster) {
        String response;
        Player p = (attacker.getPlayerID().equals(player)) ? attacker : defender;
        PrintWriter out = (attacker.getPlayerID().equals(player)) ? attackerOut : defenderOut;
        int i = p.getID() - 1;

        ArrayList<Integer> highlightPositions = (ArrayList<Integer>) p.isAvailableMonster(monster);
        if (highlightPositions == null) {
            selectedMonster[i] = -1;
            response = packet.generatePacket(Protocol.BOARD_SETUP, Flag.SELECT_MONSTER, false, -1);
        }
        else {
            selectedMonster[i] = monster;
            response = packet.generatePacket(Protocol.BOARD_SETUP, Flag.SELECT_MONSTER, true, highlightPositions);
        }

        out.println(response);

    }

    public void positionMonster(String player, int[] position) {
        String response;
        Player p = (attacker.getPlayerID().equals(player)) ? attacker : defender;
        PrintWriter aOut = (attacker.getPlayerID().equals(player)) ? attackerOut : defenderOut;
        PrintWriter dOut = (attacker.getPlayerID().equals(player)) ? defenderOut : attackerOut;
        int i = p.getID() - 1;

        if (p.canPositionMonster(position[0], position[1], selectedMonster[i])) {
            int[] data = {position[0], position[1], selectedMonster[i]};
            response = packet.generatePacket(Protocol.BOARD_SETUP, Flag.POSITION_MONSTER, true, data);
            aOut.println(response);
            response = packet.generatePacket(Protocol.ENEMY_BOARD_SETUP, Flag.POSITION_MONSTER, true, position);
            dOut.println(response);
        } else aOut.println(packet.generatePacket(Protocol.BOARD_SETUP, Flag.POSITION_MONSTER, false, 0));
    }

    private int[][] invertBoard() {
        GameCharacters[][] board = attacker.getBoard();
        int depth = board.length;
        int width = board[0].length;
        int[][] icons = new int[board.length][board[0].length];
        for(int i = 0; i < board.length; ++i) {
            for (int j = 0; j < board[i].length; ++j)
                if (board[i][j] == null) icons[i][j] = 0;
                else if (board[i][j].getPlayersID() == 1) icons[i][j] = board[depth - i][width - j].getPower();
                else icons[i][j] = 12;
        }
        return icons;
    }

    public void replay(String id) {
        PrintWriter opponentOut = (attacker.getPlayerID().equals(id)) ? defenderOut : attackerOut;
        packet1 = packet.generatePacket(Protocol.GAME_OVER, Flag.OPPONENT_REMATCH, true, 0);
        opponentOut.println(packet1);
        if (player1.getID().equals(id)) player1.restartGame();
        else player2.restartGame();
    }

    public Client exit(String id) {
        PrintWriter opponentOut = (attacker.getPlayerID().equals(id)) ? defenderOut : attackerOut;
        packet1 = packet.generatePacket(Protocol.GAME_OVER, Flag.OPPONENT_EXITED, true, 0);
        opponentOut.println(packet1);
        Client exitingClient = (player1.getID().equals(id)) ? player1 : player2;
        Client otherClient = (player1.getID().equals(id)) ? player2 : player1;

        exitingClient.exitGame();

        return (otherClient.exited()) ? null : otherClient;
    }

    public Client getClient(String id) {
        return (player1.getID().equals(id)) ? player1 : player2;
    }

    // TODO: Instead of creating new packets, modify existing ones.
    public void clearPosition(String player, int[] position) {
        Player p = (attacker.getPlayerID().equals(player)) ? attacker : defender;
        PrintWriter aOut = (attacker.getPlayerID().equals(player)) ? attackerOut : defenderOut;
        PrintWriter dOut = (attacker.getPlayerID().equals(player)) ? defenderOut : attackerOut;

        int monster = p.clearPosition(position);
        if(monster != -1) {
            int[] data = {position[0], position[1], monster};
            packet1 = packet.generatePacket(Protocol.BOARD_SETUP, Flag.CLEAR_POSITION, true, data);
            packet2 = packet.generatePacket(Protocol.ENEMY_BOARD_SETUP, Flag.CLEAR_POSITION, true, position);
            executor.submit(new SendMessage(aOut, packet1));
            executor.submit(new SendMessage(dOut, packet2));
        }
        else {
            packet1 = packet.generatePacket(Protocol.BOARD_SETUP, Flag.CLEAR_POSITION, false, 0);
            aOut.println(packet1);
        }
    }


    public void randomiseBoard(String player) {
        Player p = (attacker.getPlayerID().equals(player)) ? attacker : defender;
        PrintWriter aOut = (attacker.getPlayerID().equals(player)) ? attackerOut : defenderOut;
        PrintWriter dOut = (attacker.getPlayerID().equals(player)) ? defenderOut : attackerOut;

        List<Integer> data = p.randomiseBoard();
        int[] data2 = new int[data.size() - data.size()/3];
        int j = 0;
        for(int i = 0; i < data.size(); ++i) {
            data2[j++] = data.get(i++);
            data2[j++] = data.get(i++);
        }
        packet1 = packet.generatePacket(Protocol.BOARD_SETUP, Flag.RANDOMISE_BOARD, true, data);
        packet2 = packet.generatePacket(Protocol.ENEMY_BOARD_SETUP, Flag.RANDOMISE_BOARD, true, data2);
        executor.submit(new SendMessage(aOut, packet1));
        executor.submit(new SendMessage(dOut, packet2));
    }

    public synchronized void finaliseBoard(String player) {
        Player p = (attacker.getPlayerID().equals(player)) ? attacker : defender;
        PrintWriter out = (attacker.getPlayerID().equals(player)) ? attackerOut : defenderOut;

        // Check if the board is filled.
        if (!p.isReady()) {
            packet1 = packet.generatePacket(Protocol.BOARD_SETUP, Flag.FINALISE_BOARD, false, 0);
            out.println(packet1);
            return;
        }

        int i = p.getID() - 1;
        int other = (i == 0) ? 2 : 1;
        ready[i] = true;

        if (ready[0] && ready[1]) {
            if(attacker.getID() == other) {
                packet1 = packet.generatePacket(Protocol.BOARD_SETUP, Flag.OPPONENT_READY, true, Flag.FIRST);
                packet2 = packet.generatePacket(Protocol.BOARD_SETUP, Flag.START_GAME, true, Flag.SECOND);
            }
            else {
                packet1 = packet.generatePacket(Protocol.BOARD_SETUP, Flag.START_GAME, true, Flag.FIRST);
                packet2 = packet.generatePacket(Protocol.BOARD_SETUP, Flag.OPPONENT_READY, true, Flag.SECOND);
            }

            executor.submit(new SendMessage(attackerOut, packet1));
            executor.submit(new SendMessage(defenderOut, packet2));
        }
        else {
            ready[attacker.getID() - 1] = true;
            packet1 = packet.generatePacket(Protocol.BOARD_SETUP, Flag.WAITING_OPPONENT, true, 0);
            out.println(packet1);
        }

    }

    private int[] invertPosition(int[] position) {
        int[] newPosition = new int[2];
        newPosition[0] = position[0];
        newPosition[1] = position[1];
        return newPosition;
    }

    static class SendMessage implements Runnable {
        private final PrintWriter out;
        private final String message;

        SendMessage(PrintWriter out, String message) {
            this.out = out;
            this.message = message;
        }

        @Override
        public void run() {
            out.println(message);
        }
    }
}