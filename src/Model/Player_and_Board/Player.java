package Model.Player_and_Board;

import GamesException.IllegalPlayerException;
import Model.game_characters.*;
import Model.game_characters.ImmovableObjects.Forbidden_Zone.ForbiddenZone;
import Model.game_characters.ImmovableObjects.ImmovableMonsters.*;
import Model.game_characters.MoveableObjects.Regular_Monsters.*;
import Model.game_characters.MoveableObjects.SpecialMoveableObjects.Special_Characters.*;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class Player implements PlayerInterface {
    // Η πρώτη γραμμή του πίνακα υποδεικνύει το σύνολο των τεράτων που περιέχει στο ταμπλό.
    // Η δεύτερη γραμμή του πίνακα υποδεικνύει το σύνολο των τεράτων που ξεκινάει κάθε παίκτης.
    // Κάθε στήλη υποδεικνύει ένα συγκεκριμένο τέρας.
    // Στήλη:
    // 0 : Flag , 1 : Trap, 2 : Slayer , 3 : Scout , 4 : Dwarf , 5 : Elf , 6 : Lava Beast / Yeti ,
    // 7 : Sorceress , 8 : Beast Rider , 9 : Knight , 10 : Mage , 11 : Dragon .

    // TODO: Add in referenceToMonsters third row that contains captured monsters.
    private int[][] referenceToMonsters;
    private static Board gameBoard; // Περιέχει ένα reference του ταμπλού.
    private int ID; // Για να ταυτοποιείται ο παίκτης.
    private int gameMode; // Διατηρεί το mode παιχνιδιού.
    private String playerID = "";

    // Διατηρεί τα ζωντανά τέρατα. Καθιστά ευκολότερη την εύρεση τους στο ταμπλό.
    private List<GameCharacters> aliveMonsters;
    // Διατηρεί τα τέρατα που έχει αιχμαλωτίσει ο παίκτης.
    private List<GameCharacters> capturedMonsters;

    // Πίνακας δύο αντικειμένων τύπου τεράτων. Μόνο δύο φορές μπορεί να αναγεννήσει
    // τέρας κάθε παίκτης και πρέπει να είναι διαφορετικά τέρατα.
    private final GameCharacters[] monstersThatCanRevive = new GameCharacters[2];
    private static int playerCount = 0; // Συγκρατεί το πλήθος των παικτών που παίζουν.
    private ImageIcon cardsBackImage; // Πίσω μέρος της κάρτας.
    private int revivals; // Σύνολο αναγεννήσεων.



    /**
     * Creates a player who plays Stratego.
     * Can be invoked at most twice; if it's invoked more times, an exception
     * will be thrown and caught within this class.
     * @param mode must be either 0 or 1 or 2 or 3.
     * mode == 0: Plays the game with regular rules.
     * mode == 1: 'No Step Back' mode.
     * mode == 2: Half monsters mode.
     * mode == 3: No Step Back mode and half monsters mode.
     */
    public Player(int mode){
        try {
            // mode πρέπει να είναι έιτε 0 ή 1 ή 2 ή 3.
            if (mode != 0 && mode != 1 && mode != 2 && mode != 3)
                throw new IllegalPlayerException("Illegal game mode.");

            // Πρέπει να υπάρχουν ακριβώς 2 παίκτες.
            if(playerCount > 20)
                throw new IllegalPlayerException("Too many players.");

        }catch(IllegalPlayerException e){
            System.out.println(e);
            return;
        }

        this.gameMode = mode;

        int id = (playerCount % 2 == 0) ? 1 : 2;

        // Static μεταβλητές, οπότε το αρχικοποιώ μονομιάς.
        if(playerCount % 2 == 0)
            gameBoard = new Board(mode);

        initialiseArray(mode);

        this.ID = id;
        this.revivals = 0;
        this.aliveMonsters = new ArrayList<>();
        this.capturedMonsters = new ArrayList<>();

        this.cardsBackImage = (this.ID == 1) ? new ImageIcon("src/images/bluePieces/blueHidden.png") :
                new ImageIcon("src/images/RedPieces/redHidden.png");

        ++playerCount;

    }

    public Player(int mode, String playerID) {
        try {
            // mode πρέπει να είναι έιτε 0 ή 1 ή 2 ή 3.
            if (mode != 0 && mode != 1 && mode != 2 && mode != 3)
                throw new IllegalPlayerException("Illegal game mode.");

            // Πρέπει να υπάρχουν ακριβώς 2 παίκτες.
            if(playerCount > 20)
                throw new IllegalPlayerException("Too many players.");

        }catch(IllegalPlayerException e){
            System.out.println(e);
            return;
        }

        this.gameMode = mode;

        int id = (playerCount % 2 == 0) ? 1 : 2;

        // Static μεταβλητές, οπότε το αρχικοποιώ μονομιάς.
        if(playerCount == 0)
            gameBoard = new Board(mode);

        initialiseArray(mode);

        this.ID = id;
        this.playerID = playerID;
        this.revivals = 0;
        this.aliveMonsters = new ArrayList<>();
        this.capturedMonsters = new ArrayList<>();

        this.cardsBackImage = (this.ID == 1) ? new ImageIcon("src/images/bluePieces/blueHidden.png") :
                new ImageIcon("src/images/RedPieces/redHidden.png");

        ++playerCount;

    }
    // Αρχικοποιεί τον πίνακα referenceToMonsters.
    private void initialiseArray(int mode) {
        // Εάν mode == 2 ή mode == 3, τότε μισά τέρατα.
        this.referenceToMonsters = new int[2][12];
        this.referenceToMonsters[0] = (mode != 2 && mode != 3) ? new int[]{1, 6, 1, 4, 5, 2, 2, 2, 3, 2, 1, 1} :
                new int[]{1, 3, 1, 2, 2, 1, 1, 1, 1, 1, 1, 1};
        this.referenceToMonsters[1] = (mode != 2 && mode != 3) ? new int[]{1, 6, 1, 4, 5, 2, 2, 2, 3, 2, 1, 1} :
                new int[]{1, 3, 1, 2, 2, 1, 1, 1, 1, 1, 1, 1};

    }

    // Εντολή που χρησιμοποιεί ο παίκτης για να μετακινήσει τέρας του.
    // Επιστρέφει true εάν κινήθηκε επιτυχώς το τέρας.
    // Επιστρέφει false εάν δε κινήθηκε επιτυχώς το τέρας.
    public GameCharacters moveCharacter(int initialLine, int nextLine, int initialRow,
                                                                                int nextRow, Player enemyPlayer){

        if(!gameBoard.hasSelectedMoveableMonster(initialLine, initialRow, this.ID))
            return null;

        return gameBoard.moveMonster(initialLine, nextLine, initialRow, nextRow, this, enemyPlayer);
    }

    // Αφαιρεί κάποιο τέρας που πέθανε από τη λίστα και το προσθέτει στη λίστα αιχμαλώτων του αντίπαλου παίκτη.
    void lostMonster(GameCharacters deadMonster, Player captor){
        captor.capturedMonsters.add(deadMonster);
        captor.capturedMonsters.sort(new sortMonsters());
        this.aliveMonsters.remove(deadMonster);
        this.updateMonsterReferences(deadMonster);
    }

    // GETTERS
    public int getID(){return this.ID;}
    public int[] getReferenceToMonsters(){return this.referenceToMonsters[0];}
    public int[] getInitialReferenceToMonsters(){return this.referenceToMonsters[1];}
    public int getSuccessfulAttackRatio(){
        int totalAttacks = gameBoard.getTotalAttacks(this.ID);
        int successfulAttacks = gameBoard.getAttackersSuccessfulAttacks(this.ID);
        if(totalAttacks == 0)
            return 0;

        return successfulAttacks * 100 / totalAttacks;
    }
    public ImageIcon getCardsBack(){return this.cardsBackImage;}
    public int getRevivals(){return this.revivals;}

    // Ελέγχει εάν ο παίκτης δεν έχει χάσει το παιχνίδι.
    public boolean matchContinues(){
        // Δεν έχει παρθεί η σημαία του, μπορεί τουλάχιστον ένα τέρας να κινηθεί.
        return (this.referenceToMonsters[0][0] > 0) && gameBoard.canMoveAtLeastAMonster(this.aliveMonsters);
    }

    public boolean isMovingMonster(int line, int row){
        // Αν δεν είναι ούτρε σημαία, ούτε παγίδα.
        return gameBoard.hasSelectedMoveableMonster(line, row, this.ID);
    }

    // Ελέγχει εάν μπορεί να αναγεννήσει κάποιο τέρας ο παίκτης.
    public boolean canRevive(GameCharacters monster, Player defender){
        if(monster == null)
            return false;
        if(defender == null)
            return false;

        boolean canRevive = false;

        // Εάν το τέρας δεν είναι τύπου Scout και το τέρας μπορεί να αναγεννήσει και υπάρχει τέρας
        // αιχμαλωτισμένο από τον αντίπαλο.
        if(this.monsterCanRevive(monster) && (defender.capturedMonsters.size() > 0)){

            int startingLine, endingLine, startingRow, endingRow;

            if(gameMode == 0 || gameMode == 1){
                startingLine = (ID == 1) ? 0 : 5;
                endingLine = (ID == 1) ? 3 : 8;
                startingRow = 0;
                endingRow = 10;
            } else{
                startingLine = (ID == 1) ? 0 : 6;
                endingLine = (ID == 1) ? 2 : 8;
                startingRow = 1;
                endingRow = 9;
            }

            // Στην εκφώνηση, μία από τις συνθήκες αναγέννησης είναι το τέρας να
            // βρεθεί στη πρώτη ή όγδοη σειρά --ανάλογα τον επιτιθέμενο.
            if(this.ID == 1 && monster.getLine() == 7)
                canRevive = true;
            else if(this.ID == 2 && monster.getLine() == 0)
                canRevive = true;

            System.out.println("First Can revive: " + canRevive + ", ID: " + this.ID + ", line: " + monster.getLine());

            // Εάν το τέρας βρίσκεται σε τοποθεσία ικανή για αναγέννηση και υπάρχει χώρος
            // για να πραγματοποιηθεί η αναγέννηση, τότε είναι ικανή η αναγέννηση.
            if(canRevive){
                canRevive = false;

                for(int i = startingLine; i < endingLine; ++i){
                    for(int j = startingRow; j < endingRow; ++j){
                        if(this.isValidPositionForRevival(i, j)){
                            canRevive = true;
                            break;
                        }
                    }
                }

            }
        }

        System.out.println("Can revive: " + canRevive);
        return canRevive;
    }

    public boolean isValidPositionForRevival(int line, int row){
        return gameBoard.isValidPositionForRevival(line, row, this.ID);
    }

    public boolean reviveMonster(GameCharacters monsterReviving,int newLine,int newRow, Player defender,int monster){
        if(defender == null || monster < 0 || monsterReviving == null || monsterReviving.getPlayersID() != this.ID)
            return false;

        boolean revive = false;

        // Εάν ο παίκτης έχει επιλέξει έγκυρη θέση για την αναγέννηση τέρατος και το τέρας που
        // αναγεννεί, δεν έχει αναγεννήσει άλλο τέρας παλιότερα.
        if(gameBoard.isValidPositionForRevival(newLine, newRow, this.ID)){

            int index = this.getRevivalIndex(monster);

            GameCharacters revivedMonster = defender.capturedMonsters.get(index);

            // Εάν δεν αναγεννηθεί, επιστρέφει λάθος.
            if(!gameBoard.reviveMonster(revivedMonster, newLine, newRow))
                return false;

            // Επανατοποθετώ το τέρας στα alivemonsters του επιτιθέμενου και το αφαιρώ από την
            // αιχμαλωσία του αμυνόμενου.
            defender.capturedMonsters.remove(index);
            this.aliveMonsters.add(revivedMonster);
            this.addToMonsterReferences(revivedMonster);

            if(this.monstersThatCanRevive[0] == null)
                this.monstersThatCanRevive[0] = monsterReviving;
            else if(this.monstersThatCanRevive[1] == null)
                this.monstersThatCanRevive[1] = monsterReviving;
            else
                return false;

            revive = true;
            ++revivals;
        }

        return revive;
    }

    public boolean isCaptured(int monster){
        if(monster < 0)
            return false;

        System.out.println("Captured: " + (this.referenceToMonsters[0][monster + 2] != this.referenceToMonsters[1][monster + 2]));
        // Εάν ισοδυναμεί η παρακάτω παράσταση, τότε δεν έχει αιχμαλωτιστεί κανένα τέρας monster.
        return (this.referenceToMonsters[0][monster + 2] != this.referenceToMonsters[1][monster + 2]);
    }

    public boolean canMove(int line, int row, int nextLine, int nextRow){
        return gameBoard.canMove(line, nextLine, row, nextRow, this.ID);
    }

    // Ελέγχει εάν το τέρας μπορεί να αναγεννήσει.
    private boolean monsterCanRevive(GameCharacters monster){
        if(monster == null)
            return false;

        boolean isPlayersMonster = (monster.getPlayersID() == this.ID);
        boolean notScout = !(monster instanceof Scout);
        boolean hasNeverRevived = (this.monstersThatCanRevive[0] != monster);
        boolean notReachedMaxRevivals = this.monstersThatCanRevive[1] == null;
        return (isPlayersMonster && notScout && hasNeverRevived && notReachedMaxRevivals);
    }

    // TODO: Change this.
    private void addToMonsterReferences(GameCharacters deadMonster){
        if(deadMonster instanceof BeastRider)
            ++this.referenceToMonsters[0][8];

        else if(deadMonster instanceof Dragon)
            ++this.referenceToMonsters[0][11];

        else if(deadMonster instanceof Elf)
            ++this.referenceToMonsters[0][5];

        else if(deadMonster instanceof Knight)
            ++this.referenceToMonsters[0][9];

        else if(deadMonster instanceof LavaBeast)
            ++this.referenceToMonsters[0][6];

        else if(deadMonster instanceof Mage)
            ++this.referenceToMonsters[0][10];

        else if(deadMonster instanceof Sorceress)
            ++this.referenceToMonsters[0][7];

        else if(deadMonster instanceof Yeti)
            ++this.referenceToMonsters[0][6];

        else if(deadMonster instanceof Dwarf)
            ++this.referenceToMonsters[0][4];

        else if(deadMonster instanceof Scout)
            ++this.referenceToMonsters[0][3];

        else
            ++this.referenceToMonsters[0][2];
    }

    // Επιστρέφει έναν πίνακα τύπου Icon με όλες τις εικόνες των αιχμαλωτισμένων τεράτων.
    // Αν ο παίκτης δεν έχει αιχμαλωτίσει κανένα τέρας, τότε επιστρέφει null.
    public int[] getCapturedMonsters(){
        if(!this.matchContinues())
            return null;

        int[] capturedMonsters = new int[referenceToMonsters[0].length];
        for (int[] referenceToMonster : referenceToMonsters) {
            for (int j = 0; j < referenceToMonster.length; ++j) {
                capturedMonsters[j] = referenceToMonsters[1][j] - referenceToMonsters[0][j];
            }
        }
       return capturedMonsters;
    }

    public List<GameCharacters> getPlayersAliveMonsters(){
        if(!this.matchContinues())
            return null;

        return this.aliveMonsters;
    }

    public boolean isThisPlayersMonster(int line, int row){
        GameCharacters monster = gameBoard.getMonster(line, row);

        if(monster == null)
            return false;

        return (monster.getPlayersID() == this.ID);
    }

    public List<Integer> getMoveablePath(int line, int row){
        if(!this.isMovingMonster(line, row))
            return null;

        GameCharacters monster = gameBoard.getMonster(line, row);
        List<Integer> positions = new ArrayList<>();

        // Διαφορετικά τέρατα μπορούν να ακολουθήσουν διαφορετικά μονοπάτια.
        if(monster instanceof Scout)
            scoutMoveablePath(positions, line, row);
        else {
            if (gameBoard.canMove(line, line + 1, row, row, this.ID)) {
                positions.add(line + 1);
                positions.add(row);
            }
            if (gameBoard.canMove(line, line - 1, row, row, this.ID)) {
                positions.add(line - 1);
                positions.add(row);
            }
            if (gameBoard.canMove(line, line, row, row + 1, this.ID)) {
                positions.add(line);
                positions.add(row + 1);
            }
            if (gameBoard.canMove(line, line, row, row - 1, this.ID)) {
                positions.add(line);
                positions.add(row - 1);
            }
        }

        return positions;
    }

    private void scoutMoveablePath(List<Integer> path, int line, int row){
        int i;

        // Όσο μπορεί να κινηθεί το τέρας, ενώ το τέρας δε βγαίνει εκτός ταμπλού.
        for(i = 1; ((line + i) < 8) && gameBoard.canMove(line, line + i, row, row, this.ID); ++i){
            path.add(line + i);
            path.add(row);
        }

        for(i = 1; ((line - i) >= 0) && gameBoard.canMove(line, line - i, row, row, this.ID); ++i){
            path.add(line - i);
            path.add(row);
        }

        for(i = 1; ((row + i) < 10) && gameBoard.canMove(line, line, row, row + i, this.ID); ++i){
            path.add(line);
            path.add(row + i);
        }

        for(i = 1; ((row - i) >= 0) && gameBoard.canMove(line, line, row, row - i, this.ID); ++i){
            path.add(line);
            path.add(row - i);
        }
    }

    // Ανανεώνει το referenceToMonsters, την επικαλούμαι όποτε πεθαίνει τέρας.
    private void updateMonsterReferences(GameCharacters deadMonster){
        if(deadMonster instanceof Flag)
            --this.referenceToMonsters[0][0];

        else if(deadMonster instanceof Trap)
            --this.referenceToMonsters[0][1];

        else if(deadMonster instanceof BeastRider)
            --this.referenceToMonsters[0][8];

        else if(deadMonster instanceof Dragon)
            --this.referenceToMonsters[0][11];

        else if(deadMonster instanceof Elf)
            --this.referenceToMonsters[0][5];

        else if(deadMonster instanceof Knight)
            --this.referenceToMonsters[0][9];

        else if(deadMonster instanceof LavaBeast)
            --this.referenceToMonsters[0][6];

        else if(deadMonster instanceof Mage)
            --this.referenceToMonsters[0][10];

        else if(deadMonster instanceof Sorceress)
            --this.referenceToMonsters[0][7];

        else if(deadMonster instanceof Yeti)
            --this.referenceToMonsters[0][6];

        else if(deadMonster instanceof Dwarf)
            --this.referenceToMonsters[0][4];

        else if(deadMonster instanceof Scout)
            --this.referenceToMonsters[0][3];

        else
        --this.referenceToMonsters[0][2];
    }

    private int getRevivalIndex(int monster){
        if(monster < 0 || monster > 9)
            return -1;

        int index = 0;

        // Το monster αποτελεί κώδικα του είδους του τέρατος που θέλουμε να αναγεννήσουμε.
        for(int i = 2; i <= monster + 2; ++i)
            index += this.referenceToMonsters[1][i] - this.referenceToMonsters[0][i];

        return (index - 1);
    }

    public String getPlayerID() {
        return playerID;
    }

    // TODO: Remove this.
    public GameCharacters[][] getBoard(){
        return gameBoard.getBoard();
    }
    public List<Integer> getValidRevivalPositions(){
        List<Integer> revivalPositions = new ArrayList<>();

        int startingLine, endingLine, startingRow, endingRow;

        // Θέσεις εκκινήσεως. Μόνο σε αυτές επιτρέπεται να αναγεννήσουμε.
        if(gameMode == 0 || gameMode == 1){
            startingLine = (ID == 1) ? 0 : 5;
            endingLine = (ID == 1) ? 3 : 8;
            startingRow = 0;
            endingRow = 10;
        }
        else{
            startingLine = (ID == 1) ? 0 : 6;
            endingLine = (ID == 1) ? 2 : 8;
            startingRow = 1;
            endingRow = 9;
        }

        for(int i = startingLine; i < endingLine; ++i){
            for(int j = startingRow; j < endingRow; ++j){
                if(isValidPositionForRevival(i, j)) {
                    revivalPositions.add(i);
                    revivalPositions.add(j);
                }
            }
        }

        return revivalPositions;
    }

    public boolean canPositionMonster(int row, int col, int monster) {
        return gameBoard.canPositionMonster(row, col, this.ID, monster);
    }

    // TODO: Rename getValidRevivalPositions.
    public List<Integer> isAvailableMonster(int monster) {
        if (monster < 0 || monster > 11) return null;
        if (gameBoard.canDeployMonster(monster, this.ID)) return getValidRevivalPositions();
        return null;
    }


    public void restartGame(Player otherPlayer){

        // Προσθέτει τα τέρατα που έχουν αιχμαλωτιστεί, στη λίστα ζωντανών τεράτων.
        for(int i = otherPlayer.capturedMonsters.size() - 1; i >= 0; --i){
            this.aliveMonsters.add(otherPlayer.capturedMonsters.get(i));
            otherPlayer.capturedMonsters.remove(i);
        }

        for(int i = this.capturedMonsters.size() - 1; i >= 0; --i){
            otherPlayer.aliveMonsters.add(this.capturedMonsters.get(i));
            this.capturedMonsters.remove(i);
        }

        Player delete;

        for(int i = 0; i < 2; ++i){
            delete = (i == 0) ? this : otherPlayer;

            delete.revivals = 0;

            System.arraycopy(this.referenceToMonsters[1], 0, delete.referenceToMonsters[0], 0, 12);

            for(int j = 0; j < monstersThatCanRevive.length; ++j)
               delete.monstersThatCanRevive[j] = null;

        }

        gameBoard.resetBoard(this.aliveMonsters, otherPlayer.aliveMonsters);

        List<GameCharacters> add;

        for(int i = 1; i <= 2; ++i) {
            add = (i == 1) ? this.aliveMonsters : otherPlayer.aliveMonsters;
            gameBoard.getPlayersAliveMonsters(add, i);
        }
    }

    public boolean isAttacking(int line, int row){
        return gameBoard.isEnemy(line, row, this.ID);
    }

    public GameCharacters getMonster(int line, int row){return gameBoard.getMonster(line, row);}
    public List<GameCharacters> testMove(){
        return gameBoard.testMove();
    }

    public void testAttack(GameCharacters attackingMonster, Player defender, GameCharacters defendingMonster) {
        gameBoard.testAttack(this, attackingMonster, defender, defendingMonster);
    }

    public int[][] invertBoard() {
        if(ID != 1) return null;

        GameCharacters[][] board = gameBoard.getBoard();
        int height = board.length;
        int width = board[0].length;

        int[][] invertedBoard = new int[height][width];
        for(int i = 0; i < height; ++i){
            for(int j = 0; j < width; ++j){
                GameCharacters monster = board[i][j];
                if (monster == null) invertedBoard[height - i - 1][width - j - 1] = -1;
                else if (monster instanceof ForbiddenZone) invertedBoard[height - i - 1][width - j - 1] = -1;
                else if (monster.getPlayersID() == ID) {
                    int label = (monster.getPower() != Integer.MAX_VALUE) ? monster.getPower() : 11;
                    invertedBoard[height - i - 1][width - j - 1] = label;
                } else invertedBoard[height - i - 1][width - j - 1] = 12;
            }
        }
        System.out.println(Arrays.deepToString(invertedBoard));
        return invertedBoard;
    }

    public int clearPosition(int[] position) {
        return gameBoard.clearPosition(position, this.ID);
    }

    public List<Integer> randomiseBoard() {
        return gameBoard.randomBoardSetup(this.ID);
    }

    public boolean isReady() {
        if (gameBoard.isReady(this.ID)) {
            gameBoard.getPlayersAliveMonsters(this.aliveMonsters, this.ID);
            return true;
        }
        return false;
    }

    // Για τη ταξινόμηση των τεράτων των captured τεράτων.
    private static class sortMonsters implements Comparator<GameCharacters>{

        @Override
        public int compare(GameCharacters monster1, GameCharacters monster2) {
            GameCharacters compare;
            int[] givenValue = new int[2];

            for(int i = 0; i < 2; ++i){
                compare = (i == 0) ? monster1 : monster2;

                if(compare instanceof Flag)
                    givenValue[i] = 12;

                else if(compare instanceof Trap)
                    givenValue[i] = 11;

                else if(compare instanceof Dragon)
                    givenValue[i] = 10;

                else if(compare instanceof Mage)
                    givenValue[i] = 9;

                else if(compare instanceof Knight)
                    givenValue[i] = 8;

                else if(compare instanceof BeastRider)
                    givenValue[i] = 7;

                else if(compare instanceof Sorceress)
                    givenValue[i] = 6;

                else if(compare instanceof LavaBeast || compare instanceof Yeti)
                    givenValue[i] = 5;

                else if(compare instanceof Elf)
                    givenValue[i] = 4;

                else if(compare instanceof Dwarf)
                    givenValue[i] = 3;

                else if(compare instanceof Scout)
                    givenValue[i] = 2;

                else
                    givenValue[i] = 1;


            }

            return (givenValue[0] > givenValue[1]) ? 1 : ((givenValue[1] == givenValue[0]) ? 0 : -1);
        }
    }
}
