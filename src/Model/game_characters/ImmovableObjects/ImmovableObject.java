package Model.game_characters.ImmovableObjects;

import Model.game_characters.GameCharacters;

abstract public class ImmovableObject extends GameCharacters {

    public ImmovableObject(int row, int line, int power, int playersID){super(row, line, power, playersID);}

    // Τα immovable objects δε μπορούν ούτε να κινηθούν και ούτε να επιτεθούν.
    public void move(int x, int y){
        System.out.println("Cannot move this monster.");
    }
    public void attack(GameCharacters defender){
        System.out.println("This monster cannot attack.");
    }
}
