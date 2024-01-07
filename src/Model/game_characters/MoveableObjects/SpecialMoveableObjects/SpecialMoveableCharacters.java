package Model.game_characters.MoveableObjects.SpecialMoveableObjects;

import Model.game_characters.GameCharacters;
import Model.game_characters.MoveableObjects.MoveableCharacters;

abstract public class SpecialMoveableCharacters extends MoveableCharacters {

    public SpecialMoveableCharacters(int row, int line, int power, int playersID){
        super(row, line, power, playersID);
    }

    // Κομμάτι της attack της MoveableCharacters. Τα στοιχεία επίθεσης είναι ειδικότερα στα
    // παιδιά της SpecialMoveableCharacters.
    public void attack(GameCharacters defender){
        if(defender == null) {
            System.out.println("No monster has been passed.");
            return;
        }
        if(!this.isEnemy(defender)){
            System.out.println("No enemy monster has been passed.");
            return;
        }

        byte wins = this.winsBattleAgainst(defender);

        if(wins == 1) {
            this.move(defender.getRow(), defender.getLine());
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
