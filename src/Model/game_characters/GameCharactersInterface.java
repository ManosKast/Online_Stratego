package Model.game_characters;

import javax.swing.*;

/**
 * This interface contains actions a monster from Stratego can take.
 */
interface GameCharactersInterface {

    /**
     * The monster must be alive, otherwise it has no position on the board.
     * @return the monster's current row in the board. If it's dead -1 is returned, instead.
     */
    int getRow();

    /**
     * The monster must be alive, otherwise it has no position on the board.
     * @return the monster's current line in the board. If it's dead -1 is returned, instead.
     */
    int getLine();

    /**
     * @return the monster's ranking.
     */
    int getPower();

    /**
     * @return the monster's image.
     */
    Icon getFrontPicture();

    /**
     * @return the player's ID that owns the card.
     */
    int getPlayersID();

    /**
     * @return true if the monster's alive; otherwise, it returns false.
     */
    boolean isAlive();

    /**
     * Moves the specified monster to another position.
     * @param nextRow has to be an integer.
     * @param nextLine has to be an integer.
     * These parameters modify the monster's current position.
     * All integers are valid, as the game's rules are defined by the programmer.
     * It's a void method, therefore it returns nothing.
     */
    void move(int nextRow, int nextLine);

    /**
     * @param defender must be of type GameCharacters, other than null.
     * This method simulates a battle between the method's monster and the passed monster.
     * If the attacker wins the battle, this method moves its position to the defender's monster.
     * Again, the game's rules are defined by the programmer, so there's no validity check.
     * This method speculates that the defender's monster is indeed a valid option.
     */
    void attack(GameCharacters defender);

    /**
     * Revives the monster on the position (line, row).
     * @param line must be a valid board position.
     * @param row must be a valid board position.
     */
    public void reviveMonster(int line, int row);
}
