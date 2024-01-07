package Model.game_characters.MoveableObjects;

import Model.game_characters.GameCharacters;

abstract public class MoveableCharacters extends GameCharacters {
    protected MoveableCharacters(int initialRow, int initialLine, int power, int playersID){
        super(initialRow, initialLine, power, playersID);
    }

    // Ο χαρακτήρας μετακινείται σε νέα κατεύθυνση.
    public void move(int nextRow, int nextLine){
        if(!this.isValidPosition(nextLine, nextRow)){
            System.out.println("Position passed is illegal.");
            return;
        }

        this.setRow(nextRow);
        this.setLine(nextLine);
    }

    // Ο δράκος θα μπορούσε να κάνει override αυτή τη μέθοδο, επειδή το wins είναι περιττό.
    // Πάντα σκοτώνει οτιδήποτε επιτεθεί.
    public void attack(GameCharacters defender){
        if(defender == null) {
            System.out.println("No monster has been passed.");
            return;
        }
        if(!this.isEnemy(defender)){
            System.out.println("No enemy monster has been passed.");
            return;
        }

        // Υποδεικνύει τον νικητή.
        byte wins = this.winsBattleAgainst(defender);

        // Διαφορετικά αποτελέσματα μαχών.
        if(wins == 1) {
            move(defender.getRow(), defender.getLine());
            this.kills(defender);
        }
        else if (wins == 2)
            this.kills(this);
        else{
            this.kills(defender);
            this.kills(this);
        }

    }

}
