package Model.Player_and_Board;

import Model.game_characters.GameCharacters;

import javax.swing.*;
import java.util.List;

/**
 * Must not be initialised more than twice.
 * Simulates the moves this game's player can make.
 */
public interface PlayerInterface {

    /**
     * It  attempts to move the monster that lies in (initialLine, initialRow) to the position
     * (nextLine, nextRow); if an enemy's monster lies in that position an attack is
     * initiated. If the attacker's monster wins it moves to that position, if not
     * it is captured by the enemy and is consequently removed from the board.
     * @param initialLine must be an integer between 0 and 7.
     * @param nextLine must be an integer between 0 and 7.
     * @param initialRow must be an integer between 0 and 9.
     * @param nextRow must be an integer between 0 and 9.
     * @param enemyPlayer must be of type Player and unequal to null.
     * (initialLine, initialRow) must point to an attacker's moveable monster.
     * (nextLine, nextRow) must correspond to either an empty position or to an enemy's monster.
     * @return if there's a battle, it returns the winner's battle and if it ends in a draw
     * it returns null.
     * Otherwise, if there is no attack and it simply moves, it returns the attacker's monster
     * that completed the said move.
     */
    GameCharacters moveCharacter(int initialLine, int nextLine, int initialRow, int nextRow, Player enemyPlayer);

    /**
     * @return The player's ID number.
     */
    int getID();

    /**
     * @return an array containing references to the player's monsters.
     */
    public int[] getReferenceToMonsters();

    /**
     * @return an array containing a reference to the player's starting monsters.
     */
    public int[] getInitialReferenceToMonsters();

    /**
     * @return the player's attack to capture ratio.
     */
    public int getSuccessfulAttackRatio();

    /**
     * @return the back of this player's card.
     */
    public ImageIcon getCardsBack();

    /**
     * @return the number of this player's completed revivals.
     */
    public int getRevivals();


    /**
     * @return a list containing valid positions for revival.
     */
    public List<Integer> getValidRevivalPositions();


    /**
     * Checks if any game-ending conditions are met.
     * @return true if no game-ending condition is met. Otherwise, it returns false.
     */
    boolean matchContinues();

    /**
     * Initialises the board anew.
     * @param otherPlayer a pointer to the second player.
     */
    void restartGame(Player otherPlayer);

    /**
     * Checks if the given position corresponds to a player's monster.
     * @param line must be an integer between 0 and 7.
     * @param row must be an integer between 0 and 9.
     * The Board class enforces the game's rules, therefore parameters line and row could
     * be any integer.
     * @return  true if the selected monster is owned by the player who evoked this method.
     * Otherwise, it returns false. If the game has ended it returns false.
     */
    boolean isMovingMonster(int line, int row);

    /**
     * Checks if the player can revive a monster.
     * @return true if the player can revive one of his captured monsters. Otherwise, it returns false.
     * It also returns false, if the game has ended.
     */
    boolean canRevive(GameCharacters monster, Player defender);

    /**
     * Tries to revive an attacker's monster.
     * @param monsterReviving must be of type GameCharacters unequal to null and must be
     * a reference to the attacker's monster that meets the requirements to revive an ally monster.
     * @param newLine must be an integer between 0 and 7.
     * @param newRow must be an integer between 0 and 9.
     * (newLine, newRow) must be an empty square on the board.
     * @param defender must be a reference to the current round's defender.
     * @param monster must be an integer >= 0. It serves as an index to
     * the  list.
     * @return true if the monster has been revived successfully.
     * Otherwise, it returns false.
     */
    boolean reviveMonster(GameCharacters monsterReviving, int newLine, int newRow, Player defender, int monster);

    /**
     * Getter.
     * @return An array containing pictures of all monsters this player has captured.
     * Otherwise, it returns null. If the game has ended it returns null.
     */
    int[] getCapturedMonsters();

    /**
     * Checks if the given position is valid to revive the attacker's monster.
     * @param line must be an integer between 0 and 7.
     * @param row must be an integer between 0 and 9.
     * @return true if it can be revived in the given position.
     * Otherwise, it returns false.
     */
    boolean isValidPositionForRevival(int line, int row);

    /**
     * Checks if the attacker's monster can move to the position (nextLine, nextRow).
     * A moveable attacker's monster must sit in the position (line, row).
     * @param line must be an integer between 0 and 7.
     * @param row must be an integer between 0 and 9.
     * @param nextLine must be an integer between 0 and 7.
     * @param nextRow must be an integer between 0 and 9.
     * @return true if the monster can move to the new position.
     * Otherwise, it returns false.
     */
    boolean canMove(int line, int row, int nextLine, int nextRow);

    /**
     * Returns a List containing the monsters' the attacker currently controls.
     * @return a List containing the monsters controlled by the player.
     */
    List<GameCharacters> getPlayersAliveMonsters();

    /**
     * Checks if the given monster is this player's.
     * @param line must be an integer that corresponds to valid lines within the board.
     * @param row must be an integer that corresponds to valid rows within the board.
     * @return true if the given monster belongs to the player. Otherwise, it returns false.
     */
    boolean isThisPlayersMonster(int line, int row);

    /**
     * Checks the paths the player's monster can move to (that corresponds to the position (line, row))
     * adds them in a List and returns it.
     * @param line must be an integer between 0 and 7.
     * @param row must be an integer between 0 and 9.
     * (line, row) must correspond to a moveable monster.
     * @return a List containing the potential paths this monster can move to.
     */
    List<Integer> getMoveablePath(int line, int row);

    /**
     * Checks if the given monster can revive.
     * @param monster must be an integer that ranges from 2 to 12.
     * @return true if the monster can revive, otherwise it returns false.
     */
    boolean isCaptured(int monster);

}
