package Controller;
import Packet.*;
import Protocol.Protocol;

import Model.Player_and_Board.Player;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.*;

import View.View;
import Model.game_characters.GameCharacters;


public class Controller {
    /*
    private final int[] positionBuffer = new int[2];
    private final View view;
    private int gameMode;
    private Player attacker;
    private Player defender;
    private boolean revive;
    private int reviveMonster;
    private GameCharacters winner;
    private Packet packet = new Packet();


    public static void main(String[] args) {
        new Controller();
    }

    public Controller(){
        this.gameMode = 0;
        this.revive = false;
        this.winner = null;
        this.reviveMonster = 0;

        view = new View(new UIPressed());
    }

    // Ξεκινάει το παιχνίδι, μόλις επιλεχτεί gameMode από το αρχικό UI.
    private void initialiseGame(){
        packet.generatePacket(Protocol.FIND_MATCH, false, (Integer) gameMode);
        // Δημιουργεί τους δύο παίκτες. Ξεκινάει πρώτος ο μπλε, αν δεν απατώμαι.
        this.attacker = new Player(gameMode);
        this.defender = new Player(gameMode);

        positionBuffer[0] = -1;
        positionBuffer[1] = -1;

        this.view.startGame(new ButtonsPressed(), this.gameMode);
        // Προσθέτει τις εικόνες της τρέχουσας κατάστασης του board στο view.
        this.view.nextRound(attacker, defender);
    }

    private boolean gameContinues() {
        return ( attacker.matchContinues() && defender.matchContinues() );
    }

    // TODO: Change names and add in Timer first calls of continueMoveMonster.
    private void moveMonster(int currentLine, int currentRow, int nextLine, int nextRow) {

        if(attacker.isAttacking(nextLine, nextRow)){
            view.displayBattlingMonsters(attacker.getMonster(currentLine, currentRow), attacker.getMonster(nextLine,  nextRow));
            Timer timer = new Timer(500, e ->
                continueMoveMonster(currentLine, currentRow, nextLine, nextRow)
            );
            timer.setRepeats(false); // Ensure the timer only runs once
            timer.start();
        } else continueMoveMonster(currentLine, currentRow, nextLine, nextRow);

    }

    private void continueMoveMonster(int currentLine, int currentRow, int nextLine, int nextRow){
        // Το moveCharacter επιστρέφει null, εάν η μάχη είναι ισόπαλη ή εάν δεν υπήρξε μάχη.
        winner = attacker.moveCharacter(currentLine, nextLine, currentRow, nextRow, defender);

        view.updateBoard(winner, currentLine, currentRow, nextLine, nextRow);

        // Αν τελείωσε το παιχνίδι.
        if (!gameContinues()) {
            view.endGame(attacker, new revivePanelPressed());
            return;
        }

        // Εάν η μάχη δεν ήταν ισόπαλη.
        // Η δεύτερη μέθοδος αποκαλύπτει εάν το επιτιθέμενο τέρας κέρδισε τη μάχη ή όχι.
        // Επειδή αν επιστρέψει true, τότε το επιτιθέμενο τέρας βρίσκεται στην επόμενη θέση.
        if (attacker.canRevive(winner, defender))
            view.selectMonsterToRevive(new revivePanelPressed(), attacker);
        else
            // Τέλος μάχης. Ανανέωσε τη τοποθεσία του τέρατος, τα αιχμαλωτισμένα τέρατα,
            // δεδομένου ότι η επίθεση ήταν επιτυχής, άλλαξε γύρο και κρύψε/εμφάνισε κάρτες.
            this.nextRound();
    }

    private void restartPosition(){
        this.positionBuffer[0] = -1;
        this.positionBuffer[1] = -1;
    }

    // Ελέγχει εάν έχει επιλεχτεί σωστό τέρας.
    private boolean isValidMonster(int line, int row){
        return attacker.isMovingMonster(line, row);
    }

    private boolean isValidMovingPosition(int[] position, int nextLine, int nextRow){
        return attacker.canMove(position[0], position[1], nextLine, nextRow);
    }

    private boolean monsterHasBeenSelected(int line, int row){
        return (this.positionBuffer[0] != -1 && !attacker.isThisPlayersMonster(line, row));
    }

    // Αλλάζει τον γύρο.
    private void nextRound(){
        Player temp = attacker;
        attacker = defender;
        defender = temp;

        view.nextRound(attacker, defender);
        this.restartPosition();
        return;
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

    // Για τα κλικ του πίνακα παιχνιδιού.
    private class ButtonsPressed implements MouseListener {

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
                    // Εάν ο παίκτης έχει επιλέξει ένα τέρας.
                    if (monsterHasBeenSelected(monstersLine, monstersRow)) {

                        // Εάν η τοποθεσία που ο παίκτης θέλει να μετακινήσει το τέρας είναι μη-έγκυρη.
                        if (isValidMovingPosition(positionBuffer, monstersLine, monstersRow))
                            moveMonster(positionBuffer[0], positionBuffer[1], monstersLine, monstersRow);
                    }
                    // Αν δεν έχει επιλεχτεί τέρας, έλεγξε αν επιλέχτηκε κάποιο έγκυρο τέρας.
                    else {

                        if (isValidMonster(monstersLine, monstersRow)) {

                            positionBuffer[0] = monstersLine;
                            positionBuffer[1] = monstersRow;

                            // Εάν το τέρας είναι έγκυρο, εμφάνισε τη διαδρομή που μπορεί να ακολουθήσει.
                            view.highlightPositions(attacker.getMoveablePath(monstersLine, monstersRow));
                        }
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
    private class UIPressed implements MouseListener{

        @Override
        public void mouseClicked(MouseEvent e) {
            JButton click = (JButton) e.getSource();

            int mode = click.getName().charAt(0) - '0';

            if(gameMode != 1 && gameMode != 3 && mode == 1){
                gameMode += 1;
            } else if (gameMode != 2 && gameMode != 3 && mode == 2){
                gameMode += 2;
            } else if(mode == 3) {
                initialiseGame();
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


    private class revivePanelPressed implements MouseListener{

        @Override
        public void mouseClicked(MouseEvent e) {
            JButton button = (JButton) e.getSource();

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
*/
}
