package Model.game_characters.MoveableObjects.SpecialMoveableObjects.Special_Characters;

import Model.game_characters.GameCharacters;
import Model.game_characters.MoveableObjects.Regular_Monsters.Dragon;
import Model.game_characters.MoveableObjects.SpecialMoveableObjects.SpecialMoveableCharacters;

public class Slayer extends SpecialMoveableCharacters {

    public Slayer(int row, int line, int playersID){super(row, line, 1, playersID);}

    public void attack(GameCharacters defender) {
        if(defender == null) {
            System.out.println("No monster has been passed.");
            return;
        }
        if(!this.isEnemy(defender)){
            System.out.println("No enemy monster has been passed.");
            return;
        }

        if (defender instanceof Dragon) {
            this.move(defender.getRow(), defender.getLine());
            this.kills(defender);
        }
        else
            super.attack(defender);
    }

}
