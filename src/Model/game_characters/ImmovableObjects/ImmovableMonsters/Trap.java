package Model.game_characters.ImmovableObjects.ImmovableMonsters;

import Model.game_characters.ImmovableObjects.ImmovableObject;

public class Trap extends ImmovableObject {
    // power 0, επειδή το καταστρέφουν όλοι οι κινούμενοι χαρακτήρες.
    public Trap(int x, int y, int playersID){

        super(x, y, Integer.MAX_VALUE, playersID);
    }
}