package Model.game_characters.MoveableObjects.SpecialMoveableObjects.Special_Characters;

import Model.game_characters.GameCharacters;
import Model.game_characters.MoveableObjects.SpecialMoveableObjects.SpecialMoveableCharacters;

public class Scout extends SpecialMoveableCharacters {

    public Scout(int row, int line, int playersID){super(row, line, 2, playersID);}

    public void attack(GameCharacters defender){
        if(defender == null) {
            System.out.println("No monster has been passed.");
            return;
        }
        if(!this.isEnemy(defender)){
            System.out.println("No enemy monster has been passed.");
            return;
        }

        super.attack(defender);
    }

}
