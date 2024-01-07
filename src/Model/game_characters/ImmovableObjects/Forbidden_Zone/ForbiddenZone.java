package Model.game_characters.ImmovableObjects.Forbidden_Zone;

import Model.game_characters.ImmovableObjects.ImmovableObject;

// Βοηθητικό για τους κανόνες του παιχνιδιού.
public class ForbiddenZone extends ImmovableObject {
    public ForbiddenZone(int x, int y, int ID){
        super(x, y, -1, ID);
    }
}
