package Server;

import Controller.Pair;
import Packet.*;
import Protocol.Protocol;
import Protocol.Flag;

import Model.Player_and_Board.Player;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.Executor;
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
    private GameCharacters winner;
    private Packet packet = new Packet();
    private final Client player1;
    private final Client player2;
    private final Pair<String> packetBuffer = new Pair<String>();
    private PrintWriter attackerOut;
    private PrintWriter defenderOut;
    private ExecutorService executor;

    public Game(int gameMode, Client player1, Client player2) throws IOException {
        this.gameMode = gameMode;
        this.revive = false;
        this.winner = null;
        this.reviveMonster = 0;
        this.player1 = player1;
        this.player2 = player2;

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


    // TODO: Add this on board. No access to board directly.
    public String getPlayersBoard(String player, byte flag){
        GameCharacters[][] board = attacker.getBoard();
        int[][] icons = new int[board.length][board[0].length];
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
        return packet.generatePacket(Protocol.START_GAME, flag, true, icons);
    }

    private boolean gameContinues() {
        return (attacker.matchContinues() && defender.matchContinues());
    }

    public String selectMonster(int row, int column, String player){
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
    public Pair<String> moveMonster(int nextLine, int nextRow, String player) {
        boolean battle = false;
        // TODO: Add check if players turn.
        if(!isValidMovingPosition(nextLine, nextRow)) {
            packet1 = packet.generatePacket(Protocol.MOVE, false, 0);
            packetBuffer.setFirst(packet1); packetBuffer.setSecond(null);
            attackerOut.println(packet1);
            return packetBuffer;
        }
        // TODO: Change packet to packet2 or defenderPacket.
        if (!isPlayersTurn(player)) {
            packet1 = packet.generatePacket(Protocol.MOVE, false, 0);
            packetBuffer.setFirst(packet1); packetBuffer.setSecond(null);
            defenderOut.println(packet1);
            return packetBuffer;
        }

        int i = 0;
        GameCharacters monster = defender.getMonster(nextLine, nextRow);
        int mIndex = (monster != null) ? ((monster.getPower() == Integer.MAX_VALUE) ? 11 : monster.getPower()) : -2;
        int[] moveTo = {nextLine, nextRow, mIndex};
        int[] move = {positionBuffer[0], positionBuffer[1], nextLine, nextRow, attacker.getMonster(positionBuffer[0], positionBuffer[1]).getPower()};
        // TODO: Add message. Change moveCharacter so there is no battle boolean.
        if (attacker.isAttacking(nextLine, nextRow)) battle = true;

        winner = attacker.moveCharacter(positionBuffer[0], nextLine, positionBuffer[1], nextRow, defender);

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

            executor.submit(new SendMessage(attackerOut, packet1));
            executor.submit(new SendMessage(defenderOut, packet2));
            return packetBuffer;
        }

        // Εάν η μάχη δεν ήταν ισόπαλη.
        // Η δεύτερη μέθοδος αποκαλύπτει εάν το επιτιθέμενο τέρας κέρδισε τη μάχη ή όχι.
        // Επειδή αν επιστρέψει true, τότε το επιτιθέμενο τέρας βρίσκεται στην επόμενη θέση.
        if (attacker.canRevive(winner, defender))
            // TODO: Πρόσθεσε μήνυμα
            i = 0; // Πρόσθεσε μήνυμα.

        if(battle){
            if(winner == null){
                packet1 = packet.generatePacket(Protocol.MOVE, Flag.COMBAT_TIE, true, moveTo);
                packet2 = packet.generatePacket(Protocol.ENEMY_MOVE, Flag.COMBAT_TIE, true, move);
            }
            else if (winner.getPlayersID() == attacker.getID()){
                packet1 = packet.generatePacket(Protocol.MOVE, Flag.COMBAT_VICTORIOUS, true, moveTo);
                packet2 = packet.generatePacket(Protocol.ENEMY_MOVE, Flag.COMBAT_DEFEATED, true, move);
            }
            else {
                packet1 = packet.generatePacket(Protocol.MOVE, Flag.COMBAT_DEFEATED, true, moveTo);
                packet2 = packet.generatePacket(Protocol.ENEMY_MOVE, Flag.COMBAT_VICTORIOUS, true, move);
            }
        } else {
            packet1 = packet.generatePacket(Protocol.MOVE, Flag.NO_COMBAT, true, moveTo);
            packet2 = packet.generatePacket(Protocol.ENEMY_MOVE, Flag.NO_COMBAT, true, move);
        }

        executor.submit(new SendMessage(attackerOut, packet1));
        executor.submit(new SendMessage(defenderOut, packet2));
        // Τέλος μάχης. Ανανέωσε τη τοποθεσία του τέρατος, τα αιχμαλωτισμένα τέρατα,
        // δεδομένου ότι η επίθεση ήταν επιτυχής, άλλαξε γύρο και κρύψε/εμφάνισε κάρτες.
        this.nextRound();
        return packetBuffer;
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
        return;
    }

    private void restartGame() {
        // Ξεκινάει πάντα ο μπλε.
        // Δεν αλλάζει το mode παιχνιδιού, το
        if (attacker.getID() != 1) {
            Player temp = attacker;
            attacker = defender;
            defender = temp;
        }

        attacker.restartGame(defender);
        // TODO: Πρόσθεσε μήνυμα για επανάληψη.
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

    static class SendMessage implements Runnable {
        private final PrintWriter out;
        private final String message;

        SendMessage(PrintWriter out, String message){
            this.out = out;
            this.message = message;
        }

        @Override
        public void run() {
            out.println(message);
        }
    }
}