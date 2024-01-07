package Model.game_characters.ImmovableObjects.ImmovableMonsters;

import Model.game_characters.ImmovableObjects.ImmovableObject;

public class Flag extends ImmovableObject {
    // power 0, επειδή το καταστρέφουν όλοι οι κινούμενοι χαρακτήρες.
    public Flag(int x, int y, int playersID){
        super(x, y, 0, playersID);
    }
}
