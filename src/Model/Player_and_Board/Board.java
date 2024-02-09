package Model.Player_and_Board;

import Model.game_characters.*;
import Model.game_characters.ImmovableObjects.Forbidden_Zone.ForbiddenZone;
import Model.game_characters.ImmovableObjects.ImmovableMonsters.*;
import Model.game_characters.MoveableObjects.Regular_Monsters.*;
import Model.game_characters.MoveableObjects.SpecialMoveableObjects.Special_Characters.*;
import Server.Game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

class Board {
    private final GameCharacters[][] piecesOnBoard;
    private int firstPlayersTotalAttacks;
    private int firstPlayersSuccessfulAttacks;
    private int secondPlayersTotalAttacks;
    private int secondPlayersSuccessfulAttacks;
    private final int gameMode; // Συγκρατεί το mode του παιχνιδιού.
    int[] total = new int[12];
    int[][] check = new int[2][12]; // TODO: Maybe remove.

    Board(int mode) {
        piecesOnBoard = new GameCharacters[8][10];
        gameMode = mode;

        firstPlayersTotalAttacks = 0;
        firstPlayersSuccessfulAttacks = 0;

        secondPlayersTotalAttacks = 0;
        secondPlayersSuccessfulAttacks = 0;

        referenceMonsters();
    }

    // Based on the game mode, it sets the total number of monsters each player controls.
    private void referenceMonsters() {
        boolean regularArmy = ((gameMode != 2) && (gameMode != 3));

        // Κάθε στήλη υποδεικνύει το πλήθος του κάθε τέρατος που επιτρέπεται.
        total = (regularArmy) ? new int[]{1, 1, 4, 5, 2, 2, 2, 3, 2, 1, 1, 6} :
                new int[]{1, 1, 2, 2, 1, 1, 1, 1, 1, 1, 1, 3};

        check[0] = Arrays.copyOf(total, total.length);
        check[1] = Arrays.copyOf(total, total.length);
    }


    // Στήνω το ταμπλό για την εκκίνηση του παιχνιδιού. Τυχαία κατανομή.
    public List<Integer> randomBoardSetup(int ID) {
        ArrayList<Integer> monsters = new ArrayList<>();
        int startingRow, endingRow, startingCol, endingCol;

        if (gameMode == 0 || gameMode == 1) {
            startingRow = (ID == 1) ? 0 : 5;
            endingRow = (ID == 1) ? 3 : 8;
            startingCol = 0;
            endingCol = 10;
        } else {
            startingRow = (ID == 1) ? 0 : 6;
            endingRow = (ID == 1) ? 2 : 8;
            startingCol = 1;
            endingCol = 9;
        }
        Random rand = new Random();
        int randomMonster;

        for (int i = startingRow; i < endingRow; ++i) {
            for (int j = startingCol; j < endingCol; ++j) {
                if (piecesOnBoard[i][j] == null) {
                    // Επαναλαμβάνεται, ώσπου να επιλεχτεί τέρας που μπορεί να τοποθετηθεί στο ταμπλό.
                    monsters.add(i);
                    monsters.add(j);
                    do {
                        randomMonster = rand.nextInt(12);
                    } while (check[ID - 1][randomMonster] == 0);
                    monsters.add(randomMonster);
                    // Τοποθετήθηκε το συγκεκριμένο τέρας, συνεπώς μειώνω το πλήθος.
                    --check[ID - 1][randomMonster];
                    this.addMonsterOnBoard(i, j, randomMonster, ID);
                }
            }
        }

        return monsters;
    }

    // Μόλις πραγματοποιηθεί το τυχαίο setup να χρησιμοποιείται αυτό για να ανανεώνεται η λίστα
    // ζωντανών τεράτων του Player.
    void getPlayersAliveMonsters(List<GameCharacters> monstersList, int ID) {
        // Αρχή και τέλος της for.
        int start;
        int end;

        boolean regularArmy = (gameMode != 2 && gameMode != 3);

        if (ID == 1) {
            start = (regularArmy) ? 2 : 1;
            end = 0;
        } else {
            start = 7;
            end = (regularArmy) ? 5 : 6;
        }

        int startLine = (regularArmy) ? 0 : 1;
        int endLine = (regularArmy) ? 10 : 9;

        // Ξεκινάω από τη κορυφαία γραμμή, επειδή αυτά είναι περισσότερο πιθανό πως μπορούν να μετακινηθούν
        // και συνεπώς καθιστά ταχύτερο τον έλεγχο κίνησης των τεράτων.
        for (int i = start; i >= end; --i)
            monstersList.addAll(Arrays.asList(piecesOnBoard[i]).subList(startLine, endLine));

    }

    // 0 : Flag , 1 : Trap, 2 : Slayer , 3 : Scout , 4 : Dwarf , 5 : Elf , 6 : Lava Beast / Yeti ,
    // 7 : Sorceress , 8 : Beast Rider , 9 : Knight , 10 : Mage , 11 : Dragon.
    // Δημιουργεί νέο τέρας και το στήνει στο ταμπλό στη τοποθεσία που δίνεται από τις for του randomBoardSetup.
    private void addMonsterOnBoard(int line, int row, int monster, int player) {
        switch (monster) {
            case 0 -> piecesOnBoard[line][row] = new Flag(row, line, player);
            case 1 -> piecesOnBoard[line][row] = new Slayer(row, line, player);
            case 2 -> piecesOnBoard[line][row] = new Scout(row, line, player);
            case 3 -> piecesOnBoard[line][row] = new Dwarf(row, line, player);
            case 4 -> piecesOnBoard[line][row] = new Elf(row, line, player);
            case 5 -> {
                if (player == 1)
                    piecesOnBoard[line][row] = new Yeti(row, line, player);
                else
                    piecesOnBoard[line][row] = new LavaBeast(row, line, player);
            }
            case 6 -> piecesOnBoard[line][row] = new Sorceress(row, line, player);
            case 7 -> piecesOnBoard[line][row] = new BeastRider(row, line, player);
            case 8 -> piecesOnBoard[line][row] = new Knight(row, line, player);
            case 9 -> piecesOnBoard[line][row] = new Mage(row, line, player);
            case 10 -> piecesOnBoard[line][row] = new Dragon(row, line, player);
            case 11 -> piecesOnBoard[line][row] = new Trap(row, line, player);
        }
    }

    // Τοποθετεί στο ταμπλό τις απαγορευμένες ζώνες.
    private void addForbiddenZones() {
        piecesOnBoard[3][2] = new ForbiddenZone(3, 2, -1);
        piecesOnBoard[3][3] = new ForbiddenZone(3, 3, -1);
        piecesOnBoard[3][6] = new ForbiddenZone(3, 6, -1);
        piecesOnBoard[3][7] = new ForbiddenZone(3, 7, -1);
        piecesOnBoard[4][2] = new ForbiddenZone(4, 2, -1);
        piecesOnBoard[4][3] = new ForbiddenZone(4, 3, -1);
        piecesOnBoard[4][6] = new ForbiddenZone(4, 6, -1);
        piecesOnBoard[4][7] = new ForbiddenZone(4, 7, -1);
    }

    public boolean canDeployMonster(int monster, int player) {
        return check[player-1][monster] > 0;
    }

    public boolean canPositionMonster(int row, int col, int player, int monster) {
        // TODO: Add descriptive methods.
        if (isOutOfBounds(row, col) || player < 1 || player > 2 || monster < 0 || monster > 11)
            return false;
        int startingRow, endingRow, startingCol, endingCol;
        if (gameMode == 0 || gameMode == 1) {
            startingRow = (player == 1) ? 0 : 5;
            endingRow = (player == 1) ? 3 : 8;
            startingCol = 0;
            endingCol = 10;
        } else {
            startingRow = (player == 1) ? 0 : 6;
            endingRow = (player == 1) ? 2 : 8;
            startingCol = 1;
            endingCol = 9;
        }

        boolean legalPosition = (row >= startingRow && row < endingRow) && (col >= startingCol && col < endingCol);
        if (legalPosition && piecesOnBoard[row][col] == null && check[player-1][monster] > 0) {
            addMonsterOnBoard(row, col, monster, player);
            --check[player-1][monster];
            return true;
        }
        return false;
    }
    public String toString() {
        StringBuilder print = new StringBuilder();

        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 10; ++j)
                print.append(piecesOnBoard[i][j].toString());

            print.append("\n");
        }

        print.append("\n\n");

        for (int i = 5; i < 8; ++i) {
            for (int j = 0; j < 10; ++j) {
                print.append(piecesOnBoard[i][j].toString());
            }
            print.append("\n");
        }

        return print.toString();
    }

    // Έλεγχος για μετακίνηση τέρατος. Μπορεί να μετακινηθεί εάν η επόμενη θέση είναι κενή,
    // δεν είναι απαγορευμένη ζώνη ή περιέχει τέρας του αντιπάλου.
    // Επίσης κάθε τέρας -πλην scout- μπορεί να μετακινηθεί μόνο ένα βήμα προς τα μπρος, πίσω, αριστερά, δεξιά.
    boolean canMove(int currentLine, int nextLine, int currentRow, int nextRow, int playersID) {
        if (this.isOutOfBounds(currentLine, currentRow) || this.isOutOfBounds(nextLine, nextRow))
            return false;
        if (playersID != 1 && playersID != 2)
            return false;

        GameCharacters movingMonster = piecesOnBoard[currentLine][currentRow];
        if (movingMonster == null || movingMonster instanceof ForbiddenZone)
            return false;

        boolean move;

        // Αν mode == 1 ή mode == 3, τότε δεν επιτρέπεται να κινηθεί προς τα πίσω. Άρα δεν υπολογίζω την απόλυτη τιμή.
        int moveHorizontally = ((gameMode != 1) && (gameMode != 3)) ? Math.abs(currentLine - nextLine) :
                (nextLine - currentLine);
        int moveVertically = Math.abs(currentRow - nextRow);


        // Εάν ο χαρακτήρας είναι flag ή trap, προφανώς δε μετακινείται.
        if ((movingMonster instanceof Flag) || (movingMonster instanceof Trap))
            move = false;
            // Ένας scout μπορεί να κινηθεί πάνω, κάτω, αριστερά, δεξιά, αλλά με όσα βήματα θέλει.
        else if (movingMonster instanceof Scout)
            move = canScoutMove(currentLine, nextLine, currentRow, nextRow, playersID);
        else {
            // Εάν gameMode == 1 ή gameMode == 3, τότε δεν επιτρέπεται να μετακινηθεί προς τα κάτω.
            if (gameMode == 1 || gameMode == 3) {
                if (playersID == 1) {
                    move = ((moveHorizontally == 1) && (moveVertically == 0)) ||
                            ((moveHorizontally == 0) && (moveVertically == 1));
                } else {
                    move = ((moveHorizontally == -1) && (moveVertically == 0)) ||
                            ((moveHorizontally == 0) && (moveVertically == 1));
                }
            } else
                // Μπορεί να μετακινηθεί μόνο ένα βήμα και όχι πλάγια.
                move = ((moveHorizontally == 1) && (moveVertically == 0)) ||
                        ((moveHorizontally == 0) && (moveVertically == 1));
        }

        // Μόνο αν nextLine, nextRow είναι κενή θέση ή αντίπαλος, μπορεί να κινηθεί.
        return move && (piecesOnBoard[nextLine][nextRow] == null ||
                this.isEnemy(nextLine, nextRow, playersID));
    }

    // TODO: Simplified version. Check it out.
    /*
    boolean canMove(int currentLine, int nextLine, int currentRow, int nextRow, int playersID) {
    if (isOutOfBounds(currentLine, currentRow) || isOutOfBounds(nextLine, nextRow) || playersID < 1 || playersID > 2)
        return false;

    GameCharacters movingMonster = piecesOnBoard[currentLine][currentRow];
    if (movingMonster == null || movingMonster instanceof ForbiddenZone)
        return false;

    int moveHorizontally = Math.abs(currentLine - nextLine);
    int moveVertically = Math.abs(currentRow - nextRow);

    boolean move = false;
    if (movingMonster instanceof Flag || movingMonster instanceof Trap) {
        move = false;
    } else if (movingMonster instanceof Scout) {
        move = canScoutMove(currentLine, nextLine, currentRow, nextRow, playersID);
    } else {
        move = ((moveHorizontally == 1) && (moveVertically == 0)) || ((moveHorizontally == 0) && (moveVertically == 1));
        if (gameMode == 1 || gameMode == 3) {
            move = playersID == 1 ? move : move && (nextLine - currentLine) >= 0;
        }
    }

    return move && (piecesOnBoard[nextLine][nextRow] == null || isEnemy(nextLine, nextRow, playersID));
}
     */

    // TODO: isEnemy with argument a GameCharacter instead.
    // Επιστρέφει true εάν το τέρας είναι του αντιπάλου.
    boolean isEnemy(int line, int row, int playersID) {
        if (this.isOutOfBounds(line, row) || piecesOnBoard[line][row] == null ||
                                piecesOnBoard[line][row] instanceof ForbiddenZone) return false;

        return (playersID != piecesOnBoard[line][row].getPlayersID());
    }

    // Ελέγχει εάν ο Scout μπορεί να κινηθεί.
    private boolean canScoutMove(int currentLine, int nextLine, int currentRow, int nextRow, int playersID) {
        boolean move = true;
        int moveHorizontally = currentRow - nextRow;
        int moveVertically = currentLine - nextLine;

        // Εάν παραμένει στην ίδια θέση, τότε δεν είναι έγκυρη κίνηση.
        if ((moveVertically == 0) && (moveHorizontally == 0))
            move = false;
            // Εάν κινείται οριζόντια.
        else if (moveVertically == 0) {

            int i = currentRow;

            // Η for αυτή κινείται ώσπου συναντήσει κάποιο εμπόδιο.
            // Εάν currentLine - nextLine > 0, τότε κινείται προς τα κάτω.
            if (moveHorizontally > 0) {
                for (i = i - 1; ((i >= 0) && (i != nextRow) && (piecesOnBoard[nextLine][i] == null)); --i) ;
            } else {
                for (i = i + 1; ((i < 10) && (i != nextRow) && (piecesOnBoard[nextLine][i] == null)); ++i) ;
            }

            // Εάν i != currentLine, τότε σημαίνει ότι βρέθηκε εμπόδιο πριν το nextLine.
            // Συνεπώς, δε μπορεί να κινηθεί στη (nextLine, nextRow).
            // Εάν φτάνει στο nextLine, αλλά το αντικείμενο που βρίσκεται στο
            // piecesOnBoard[i][nextRow] δεν είναι εχθρός, τότε δε γίνεται να κινηθεί.
            if (i != nextRow || ((piecesOnBoard[nextLine][i] != null) && !this.isEnemy(currentLine, i, playersID)))
                move = false;

        }
        // Εάν κινείται οριζόντια.
        else if (moveHorizontally == 0) {

            int i = currentLine;

            // Εάν mode == 1 ή mode == 3, τότε δε επιτρέπεται να κινηθεί προς τα πάνω είναι μόνο ο 2.
            // Ο παίκτης 1 μπορεί να κινηθεί μόνο προς τα κάτω.
            // Εαν mode == 0 ή mode == 2, τότε μπορούν να κινηθούν και προς τα πίσω.
            if ((moveVertically > 0) &&
                    ((gameMode == 0 || gameMode == 2) || ((gameMode == 1) || (gameMode == 3)) && (playersID == 2))) {
                for (i = i - 1; ((i >= 0) && (i != nextLine) && (piecesOnBoard[i][nextRow] == null)); --i) ;
            } else if ((moveVertically < 0) &&
                    ((gameMode == 0 || gameMode == 2) || ((gameMode == 1) || (gameMode == 3)) && (playersID == 1))) {
                for (i = i + 1; ((i < 8) && (i != nextLine) && (piecesOnBoard[i][nextRow] == null)); ++i) ;
            }

            if (i != nextLine || ((piecesOnBoard[i][nextRow] != null) && !this.isEnemy(i, nextRow, playersID)))
                move = false;

        }
        // Διαφορετικά, εάν δε κινείται σύμφωνα με τους κανόνες.
        else
            move = false;

        return move;
    }

    // Μετακινεί το τέρας, αν επιτρέπεται.
    GameCharacters moveMonster(int initialLine, int nextLine, int initialRow,
                               int nextRow, Player attacker, Player defender) {

        if (this.isOutOfBounds(initialLine, initialRow) || this.isOutOfBounds(nextLine, nextRow))
            return null;

        // Διευκολύνει λίγο την ανάγνωση του κώδικα.
        int attackerID = attacker.getID();

        GameCharacters winner = null;

        // Διευκολύνει την ανάγνωση.
        GameCharacters attackingMonster = piecesOnBoard[initialLine][initialRow];
        GameCharacters defendingMonster = piecesOnBoard[nextLine][nextRow];

        // Αν στο σημείο μετακίνησης του τέρατος, βρίσκεται αντίπαλο τέρας, επιτίθεται.
        if (this.isEnemy(nextLine, nextRow, attackerID)) {

            this.attack(attackerID);
            attackingMonster.attack(defendingMonster);

            // Εάν είναι και τα δύο τέρατα νεκρά, τότε:
            if (!(attackingMonster.isAlive()) && !(defendingMonster.isAlive())) {
                piecesOnBoard[nextLine][nextRow] = null;
                attacker.lostMonster(attackingMonster, defender);
                defender.lostMonster(defendingMonster, attacker);
                this.successfulAttack(attackerID);
            }
            // Εξετάζει την έκβαση της μάχης. Εάν πέθαναν τα τέρατα, τότε τα αφαιρούμε από το ταμπλό.
            else if (!attackingMonster.isAlive()) {
                attacker.lostMonster(attackingMonster, defender);
                winner = defendingMonster;
            }
            // Αν το τέρας επίθεσης κερδίζει τη μάχη, ανανέωσε τις θέσεις.
            else {
                piecesOnBoard[nextLine][nextRow] = attackingMonster;
                defender.lostMonster(defendingMonster, attacker);
                winner = attackingMonster;
                this.successfulAttack(attackerID);
            }
        } else {
            attackingMonster.move(nextRow, nextLine);
            piecesOnBoard[nextLine][nextRow] = attackingMonster;
            winner = attackingMonster;
        }

        piecesOnBoard[initialLine][initialRow] = null;

        return winner;
    }

    // Αν πραγματοποιηθεί κάποια επίθεση, σημειώνεται στη κατάλληλη μεταβλητή.
    private void attack(int ID) {
        if (ID == 1)
            ++firstPlayersTotalAttacks;
        else
            ++secondPlayersTotalAttacks;
    }

    // Αν η επίθεση του επιτιθέμενου ήταν επιτυχής, τότε το σημειώνω.
    private void successfulAttack(int ID) {
        if (ID == 1)
            ++firstPlayersSuccessfulAttacks;
        else
            ++secondPlayersSuccessfulAttacks;
    }

    // Επιστρέφει το τέρας που βρίσκεται στη τοποθεσία (line, row).
    GameCharacters getMonster(int line, int row) {
        if (this.isOutOfBounds(line, row))
            return null;
        if (piecesOnBoard[line][row] instanceof ForbiddenZone)
            return null;

        return piecesOnBoard[line][row];
    }

    // Επιστρέφει το σύνολο των επιθέσεων.
    int getTotalAttacks(int ID) {
        return (ID == 1) ? firstPlayersTotalAttacks : secondPlayersTotalAttacks;
    }

    // Επιστρέφει το σύνολο των επιτυχών επιθέσεων.
    int getAttackersSuccessfulAttacks(int ID) {
        return (ID == 1) ? firstPlayersSuccessfulAttacks : secondPlayersSuccessfulAttacks;
    }

    // Μία από τις συνθήκες νίκης είναι τα τέρατα του αντιπάλου να μην είναι ικανά να κινηθούν.
    // Η παρακάτω μέθοδος ελέγχει αυτό ακριβώς.
    // Λειτουργεί και σαν έλεγχος ύπαρξης τουλάχιστον ενός μετακινήσιμου χαρακτήρα.
    boolean canMoveAtLeastAMonster(List<GameCharacters> monsters) {
        if (monsters == null)
            return false;
        if (monsters.size() == 0)
            return false;

        boolean can_move = false;
        int row, line;
        int ID = monsters.get(0).getPlayersID();

        for (GameCharacters monster : monsters) {
            // Εάν τα τέρατα είναι μετακινήσιμα, κάνε κατάλληλο έλεγχο.
            if (!(monster instanceof Trap) && !(monster instanceof Flag)) {
                // Απλα διευκολύνει τον κώδικα και δε χρειάζεται να επικαλούμαι συναρτήσεις στην if.
                row = monster.getRow();
                line = monster.getLine();

                // Έλεγχος αν μπορεί να κινηθεί. Βήμα μπρος, πίσω, δεξιά και αριστερά.
                if (this.monsterCanMoveToDirection(line, row, ID)) {
                    can_move = true;
                    break;
                }
            }
        }

        return can_move;
    }

    boolean hasSelectedMoveableMonster(int line, int row, int playersID) {
        if (this.isOutOfBounds(line, row))
            return false;

        GameCharacters monster = piecesOnBoard[line][row];

        if (monster instanceof ForbiddenZone || monster == null)
            return false;

        // Εάν έδωσε έγκυρη διεύθυνση και εάν η διεύθυνση αντιστοιχεί σε μη-κενή θέση, κάνε έλεγχο.
        return (monster.getPlayersID() == playersID) && monsterCanMoveToDirection(line, row, playersID) &&
                !(monster instanceof Trap) && !(monster instanceof Flag);
    }

    // Ελέγχει εάν είναι έγκυρη τοποθεσία για αναγέννηση.
    boolean isValidPositionForRevival(int line, int row, int ID) {
        if (this.isOutOfBounds(line, row))
            return false;

        int startingLine, endingLine, startingRow, endingRow;

        if (gameMode == 0 || gameMode == 1) {
            startingLine = (ID == 1) ? 0 : 5;
            endingLine = (ID == 1) ? 3 : 8;
            startingRow = 0;
            endingRow = 10;
        } else {
            startingLine = (ID == 1) ? 0 : 6;
            endingLine = (ID == 1) ? 2 : 8;
            startingRow = 1;
            endingRow = 9;
        }

        boolean valid = ((line >= startingLine) && (line < endingLine) && (row >= startingRow) && (row < endingRow));

        return (valid && piecesOnBoard[line][row] == null);
    }

    // Ελέγχει εάν μπορεί να κινηθεί προς κάποια κατεύθυνση.
    private boolean monsterCanMoveToDirection(int line, int row, int ID) {
        return canMove(line, line + 1, row, row, ID) || canMove(line, line - 1, row, row, ID) ||
                canMove(line, line, row, row + 1, ID) || canMove(line, line, row, row - 1, ID);
    }

    // Ελέγχει εάν η διεύθυνση βρίσκεται εντός του ταμπλού.
    private boolean isOutOfBounds(int line, int row) {
        return ((line < 0) || (line > 7) || (row < 0) || (row > 9));
    }


    // Αναγεννεί το τέρας και το επανατοποθετεί στον πίνακα.
    boolean reviveMonster(GameCharacters monster, int line, int row) {
        if (monster == null || this.isOutOfBounds(line, row) || piecesOnBoard[line][row] != null)
            return false;

        monster.reviveMonster(line, row);
        piecesOnBoard[line][row] = monster;
        return true;
    }


    void resetBoard(List<GameCharacters> first, List<GameCharacters> second) {
        this.emptyBoard();
        this.rearrangeBoard(first, second);

        firstPlayersTotalAttacks = 0;
        firstPlayersSuccessfulAttacks = 0;
        secondPlayersTotalAttacks = 0;
        secondPlayersSuccessfulAttacks = 0;
    }

    // Αδειάζει το ταμπλό από τέρατα.
    // Διατηρεί μόνο τις απαγορευμένες ζώνες.
    private void emptyBoard() {
        int start, end;

        // Αδείαζει το ταμπλό από τέρατα.
        for (int z = 0; z < 2; ++z) {

            start = (z == 0) ? 0 : 5;
            end = (z == 0) ? 3 : 8;

            for (int i = start; i < end; ++i) {
                for (int j = 0; j < 10; ++j)
                    piecesOnBoard[i][j] = null;
            }
        }

        int[] moveableZones = {0, 1, 4, 5, 8, 9};
        for (int i = 3; i <= 4; ++i) {
            for (int j : moveableZones) {
                piecesOnBoard[i][j] = null;
            }
        }
    }

    GameCharacters[][] getBoard() {return piecesOnBoard;}
    // Αρχικοποιεί εκ νέου το ταμπλό, για την αρχή νέου παιχνιδιού.
    private void rearrangeBoard(List<GameCharacters> first, List<GameCharacters> second) {
        List<GameCharacters> monsters, newList;
        List<GameCharacters> firstList = new ArrayList<>();
        List<GameCharacters> secondList = new ArrayList<>();

        GameCharacters temp;

        int totalMonsters, index;
        Random randomNumber = new Random();

        int startingLine, endingLine, startingRow, endingRow;

        for (int z = 0; z < 2; ++z) {

            if (gameMode == 0 || gameMode == 1) {
                startingLine = (z == 0) ? 0 : 5;
                endingLine = (z == 0) ? 3 : 8;
                startingRow = 0;
                endingRow = 10;
            } else {
                startingLine = (z == 0) ? 0 : 6;
                endingLine = (z == 0) ? 2 : 8;
                startingRow = 1;
                endingRow = 9;
            }

            monsters = (z == 0) ? first : second;
            newList = (z == 0) ? firstList : secondList;

            totalMonsters = monsters.size();

            // TODO: Maybe add queue or stack, instead of removing index. Faster and simpler.
            for (int i = startingLine; i < endingLine; ++i) {
                for (int j = startingRow; j < endingRow; ++j) {
                    index = randomNumber.nextInt(totalMonsters);

                    temp = monsters.get(index);
                    newList.add(temp);

                    monsters.remove(index);
                    --totalMonsters;

                    piecesOnBoard[i][j] = temp;
                    temp.initiate(i, j);
                }
            }
        }
    }

    public List<GameCharacters> testMove() {
        piecesOnBoard[2][0] = new Scout(0, 2, 1);
        piecesOnBoard[2][9] = new Dragon(9, 2, 1);

        List<GameCharacters> list = new ArrayList<>();
        list.add(piecesOnBoard[2][0]);
        list.add(piecesOnBoard[2][9]);

        return list;
    }

    void testAttack(Player attacker, GameCharacters attackingMonster, Player defender, GameCharacters defendingMonster) {
        int nextLine = defendingMonster.getLine(), nextRow = defendingMonster.getRow();
        int initialLine = attackingMonster.getLine(), initialRow = attackingMonster.getRow();

        attackingMonster.attack(defendingMonster);

        // Εάν είναι και τα δύο τέρατα νεκρά, τότε:
        if (!(attackingMonster.isAlive()) && !(defendingMonster.isAlive())) {
            piecesOnBoard[nextLine][nextRow] = null;
            attacker.lostMonster(attackingMonster, defender);
            defender.lostMonster(defendingMonster, attacker);
        }
        // Εξετάζει την έκβαση της μάχης. Εάν πέθαναν τα τέρατα, τότε τα αφαιρούμε από το ταμπλό.
        else if (!attackingMonster.isAlive()) {
            attacker.lostMonster(attackingMonster, defender);
        }
        // Αν το τέρας επίθεσης κερδίζει τη μάχη, ανανέωσε τις θέσεις.
        else {
            piecesOnBoard[nextLine][nextRow] = attackingMonster;
            defender.lostMonster(defendingMonster, attacker);
        }


        piecesOnBoard[initialLine][initialRow] = null;

    }

    public static void main(String[] args){
        Board myBoard = new Board(1);
        System.out.println(myBoard);
    }

    public int clearPosition(int[] position, int ID) {
        if(isOutOfBounds(position[0], position[1]) || piecesOnBoard[position[0]][position[1]] == null)
            return -1;

        int row = position[0];
        int col = position[1];
        int monster = -1;

        if (piecesOnBoard[row][col] != null && piecesOnBoard[row][col].getPlayersID() == ID) {
            monster = piecesOnBoard[row][col].getPower();
            if (monster == Integer.MAX_VALUE) monster = 11;
            piecesOnBoard[row][col] = null;
            ++check[ID-1][monster];
        }
        return monster;
    }

    public boolean isReady(int ID) {
        int startingLine, endingLine, startingRow, endingRow;
        if (gameMode == 0 || gameMode == 1) {
            startingLine = (ID == 1) ? 0 : 5;
            endingLine = (ID == 1) ? 3 : 8;
            startingRow = 0;
            endingRow = 10;
        } else {
            startingLine = (ID == 1) ? 0 : 6;
            endingLine = (ID == 1) ? 2 : 8;
            startingRow = 1;
            endingRow = 9;
        }

        for (int i = startingLine; i < endingLine; ++i)
            for (int j = startingRow; j < endingRow; ++j)
                if (piecesOnBoard[i][j] == null) return false;

        return true;
    }
}
