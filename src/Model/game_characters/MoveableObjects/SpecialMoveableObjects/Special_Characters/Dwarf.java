package Model.game_characters.MoveableObjects.SpecialMoveableObjects.Special_Characters;

import Model.game_characters.GameCharacters;
import Model.game_characters.ImmovableObjects.ImmovableMonsters.Trap;
import Model.game_characters.MoveableObjects.SpecialMoveableObjects.SpecialMoveableCharacters;

public class Dwarf extends SpecialMoveableCharacters {

    public Dwarf(int row, int line, int playersID){super(row, line, 3, playersID);}

    // override την attack της movable.
    public void attack(GameCharacters defender){
        if(defender == null) {
            System.out.println("No monster has been passed.");
            return;
        }
        if(!this.isEnemy(defender)){
            System.out.println("No enemy monster has been passed.");
            return;
        }

        //  Εάν επιτεθεί σε παγίδα, τη καταστρέφει χωρίς να πεθάνει.
        if(defender instanceof Trap) {
            this.move(defender.getRow(), defender.getLine());
            this.kills(defender);
        }
        // Αν δεν επιτεθεί σε παγίδα, εκτελεί κομμάτι του κώδικα της attack της βασικής κλάσης.
        else
            super.attack(defender);
    }

}
