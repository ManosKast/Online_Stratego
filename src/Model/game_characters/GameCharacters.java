package Model.game_characters;

import Model.game_characters.ImmovableObjects.Forbidden_Zone.ForbiddenZone;
import Model.game_characters.ImmovableObjects.ImmovableMonsters.*;
import Model.game_characters.MoveableObjects.Regular_Monsters.*;
import Model.game_characters.MoveableObjects.SpecialMoveableObjects.Special_Characters.*;

import javax.swing.*;

/**
 * This class contains all actions a monster can take.
 * The line must be in range of 0 to 7.
 * The row must be in range of 0 to 9.
 * If the monster is not alive, then its row and line must equal to -1.
 */
abstract public class GameCharacters implements GameCharactersInterface {
    private int row;
    private int line;
    private int power;
    private boolean alive;
    private ImageIcon monstersPicture;
    private int ID;


    // Ένας απλός constructor που αρχικοποιεί ένα στοιχείο, προσδίνοντας τα απαραίτητα χαρακτηριστικά.
    protected GameCharacters(int initialRow, int initialLine, int charactersPower, int playerID){
        if(playerID != 1 && playerID != 2 && playerID != -1){
            System.out.println("ID is not valid");
            return;
        }
        if(!(this instanceof ForbiddenZone)) {
            // Εάν δε μπορεί να αρχικοποιηθεί στη ζητούμενη θέση το τέρας, εκτύπωσε μήνυμα λάθους.
            if (!this.isValidStartingPosition(initialLine, initialRow, playerID)) {
                System.out.println("Cannot initiate the monster in this position.");
                System.out.println("SLine = " + initialLine + ", SRow" + initialRow + "and ID = " + playerID);
                return;
            }
        }

        this.row = initialRow;
        this.line = initialLine;
        this.power = charactersPower;
        this.alive = true;
        this.ID = playerID;
        updateCardsPictures();

    }

    // Ανάλογα τον παίχτη, επιλέγει το μπλε ή κόκκινο πισινό μέρος της κάρτας.
    private void updateCardsPictures(){

        if(this instanceof Flag)
            this.monstersPicture = (this.ID == 1) ? new ImageIcon("src/images/bluePieces/flagB.png") :
                                                new ImageIcon("src/images/RedPieces/flagR.png");

        else if(this instanceof Trap)
            this.monstersPicture = (this.ID == 1) ? new ImageIcon("src/images/bluePieces/trapB.png") :
                    new ImageIcon("src/images/RedPieces/trapR.png");

        else if(this instanceof BeastRider)
            this.monstersPicture = (this.ID == 1) ? new ImageIcon("src/images/bluePieces/beastRiderB.png") :
                    new ImageIcon("src/images/RedPieces/beastRiderR.png");

        else if(this instanceof Dragon)
            this.monstersPicture = (this.ID == 1) ? new ImageIcon("src/images/bluePieces/dragonB.png") :
                    new ImageIcon("src/images/RedPieces/dragonR.png");

        else if(this instanceof Elf)
            this.monstersPicture = (this.ID == 1) ? new ImageIcon("src/images/bluePieces/elfB.png") :
                    new ImageIcon("src/images/RedPieces/elfR.png");

        else if(this instanceof Knight)
            this.monstersPicture = (this.ID == 1) ? new ImageIcon("src/images/bluePieces/knightB.png") :
                    new ImageIcon("src/images/RedPieces/knightR.png");

        else if(this instanceof LavaBeast)
            this.monstersPicture = new ImageIcon("src/images/RedPieces/lavaBeast.png");

        else if(this instanceof Mage)
            this.monstersPicture = (this.ID == 1) ? new ImageIcon("src/images/bluePieces/mageB.png") :
                    new ImageIcon("src/images/RedPieces/mageR.png");

        else if(this instanceof Sorceress)
            this.monstersPicture = (this.ID == 1) ? new ImageIcon("src/images/bluePieces/sorceressB.png") :
                    new ImageIcon("src/images/RedPieces/sorceressR.png");

        else if(this instanceof Yeti)
            this.monstersPicture = new ImageIcon("src/images/bluePieces/yeti.png");

        else if(this instanceof Dwarf)
            this.monstersPicture = (this.ID == 1) ? new ImageIcon("src/images/bluePieces/dwarfB.png") :
                    new ImageIcon("src/images/RedPieces/dwarfR.png");

        else if(this instanceof Scout)
            this.monstersPicture = (this.ID == 1) ? new ImageIcon("src/images/bluePieces/scoutB.png") :
                    new ImageIcon("src/images/RedPieces/scoutR.png");
        else if(this instanceof Slayer)
            this.monstersPicture = (this.ID == 1) ? new ImageIcon("src/images/bluePieces/slayerB.png") :
                    new ImageIcon("src/images/RedPieces/slayerR.png");
    }

    // Σκοτώνει τον χαρακτήρα που το επικαλέστηκε.
    protected void kills(GameCharacters dead){
        dead.alive = false;
        dead.setRow(-1);
        dead.setLine(-1);
    }

    // Αλλάζει τη τοποθεσία του χαρακτήρα στο ταμπλό.
    abstract public void move(int nextRow, int nextLine);

    // Επιτίθεται σε κάποιον αντίπαλο.
    abstract public void attack(GameCharacters defender);

    // Getters για τη γραμμή, τη σειρά και τη δύναμη του χαρακτήρα.
    public int getRow(){return row;}
    public int getLine(){return line;}
    public int getPower(){return power;}
    public ImageIcon getFrontPicture(){return this.monstersPicture;}
    public int getPlayersID(){return this.ID;}

    // Setters για τη νέα τοποθεσία του χαρακτήρα.
    protected void setRow(int newRow){this.row = newRow;}
    protected void setLine(int newLine){this.line = newLine;}
    public void reviveMonster(int line, int row){
        if(!this.isValidPosition(line, row))
            return;

        this.alive = true;
        this.line = line;
        this.row = row;
    }

    public void initiate(int line, int row){
        if(!this.isValidPosition(line, row))
            return;

        this.setLine(line);
        this.setRow(row);
    }

    // Observers

    // Ελέγχει εάν είναι ζωντανός ο χαρακτήρας/
    public boolean isAlive(){return this.alive;}

    // Προσομοιώνει τη μάχη.
    // Επιστρέφει 1, εάν ο επικαλεστής της μεθόδου κερδίζει τη μάχη.
    // Επιστρέφει 2, εάν ο αμυνόμενος κερδίζει τη μάχη.
    // Επιστρέφει 0, εάν είναι ισοδύναμοι οι χαρακτήρες και άρα πεθαίνουν μαζί.
    protected byte winsBattleAgainst(GameCharacters defender){
        byte returns;

        if(this.power > defender.power)
            returns = 1;
        else if(defender.power > this.power)
            returns = 2;
        else
            returns = 0;

        return returns;
    }

    // Ελέγχει τις αρχικές διευθύνσεις τοποθέτησης των τεράτων.
    protected boolean isValidStartingPosition(int line, int row, int playersID){
        boolean allowed = false;

        // Τέρατα των διαφορετικών παικτών μπορούν να διαταχτούν σε διαφορετικές γραμμές.
        int start = (playersID == 1) ? 0 : 5;
        int end = (playersID == 1) ? 2 : 7;

        if((line >= start) && (line <= end) && (row >= 0) && (row <= 9))
            allowed = true;

        return allowed;
    }

    // Αν ανήκουν σε διαφορετικούς παίκτες, θα έχουν και διαφορετικά αναγνωριστικά.
    protected boolean isEnemy(GameCharacters monster){
        if (monster == null) {
            System.out.println("No monster has been passed.");
            return false;
        }
        if(monster instanceof ForbiddenZone){
            System.out.println("Cannot attack a zone.");
            return false;
        }

        return (this.ID != monster.ID);
    }

    // Ελέγχει εάν μπορεί να τοποθετηθεί τέρας στη θέση αυτή.
    protected boolean isValidPosition(int line, int row){
        if((line == 3 || line == 4) && (row == 2 || row == 3 || row == 6 || row == 7)){
            System.out.println("This position corresponds to a prohibited zone");
            return false;
        }
        if( (line < 0) || (line > 7) || (row < 0) || (row > 9) ){
            System.out.println("Position passed is illegal.");
            return false;
        }

        return true;
    }

    public String toString(){
        String print = "";

        if(this instanceof Flag)
            print += "(" + line + ", " + row + "): " + "Flag";

        if(this instanceof Trap)
            print += "(" + line + ", " + row + "): " + "Trap";

        if(this instanceof BeastRider)
            print += "(" + line + ", " + row + "): " + "Beast Rider";

        if(this instanceof Dragon)
            print += "(" + line + ", " + row + "): " + "Dragon";

        if(this instanceof Elf)
            print += "(" + line + ", " + row + "): " + "Elf";

        if(this instanceof Knight)
            print += "(" + line + ", " + row + "): " + "Knight";

        if(this instanceof LavaBeast)
            print += "(" + line + ", " + row + "): " + "Lava Beast";

        if(this instanceof Mage)
            print += "(" + line + ", " + row + "): " + "Mage";

        if(this instanceof Sorceress)
            print += "(" + line + ", " + row + "): " + "Sorceress";

        if(this instanceof Yeti)
            print += "(" + line + ", " + row + "): " + "Yeti";

        if(this instanceof Dwarf)
            print += "(" + line + ", " + row + "): " + "Dwarf";

        if(this instanceof Scout)
            print += "(" + line + ", " + row + "): " + "Scout";

        if(this instanceof Slayer)
            print += "(" + line + ", " + row + "): " + "Slayer";

        return print + "\n";
    }
}
