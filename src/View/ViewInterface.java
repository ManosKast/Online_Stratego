package View;

import Model.Player_and_Board.Player;
import Model.game_characters.GameCharacters;

import java.awt.event.MouseListener;
import java.util.List;

public interface ViewInterface {

    /**
     * Initialises and displays the requested game mode.
     * @param buttonHandler must be a valid MouseListener object, so that it processes the player's interactions.
     * @param gameMode must be an integer that ranges from 0 to 3.
     */
    public void startGame(MouseListener buttonHandler, int gameMode);


    /**
     * The method conceals the defender's cards and emerge the attacker's cards and update's
     * the round's panel.
     * @param attacker must be a pointer to the current attacker.
     * @param defender must be a pointer to the current defender.
     */
    void nextRound(Player attacker, Player defender);


    /**
     * Upon an attack, it updates the board by moving or removing a monster.
     * @param winner must be either a pointer to the attacker's monster, in case it wins the battle or there
     *               was no battle, or a null pointer in case the battle ends in a defeat or a draw.
     * @param cLine an integer that indicates the monsters current line on the board.
     * @param cRow an integer that indicates the monster's current row on the board.
     * @param nLine an integer that indicates the line the monster moved to.
     * @param nRow an integer that indicates the row the monster moved to.
     *
     * All integers must point to a valid position on the board.
     */
    public void updateBoard(GameCharacters winner, int cLine, int cRow, int nLine, int nRow);


    /**
     * It highlights the path the selected monster can follow.
     * @param validMoveablePositions must be a list of integers; its even indexes dictate the lines
     * the monster can move to and the odd indexes dictate the rows a monster can move to.
     * The lines must be an integer between 0 and 7 and the rows must be an integer between 0 and 9.
     * The first two elements contain the monster's current position.
     */
    void highlightPositions(List<Integer> validMoveablePositions);

    /**
     * Displays a panel containing monsters the attacker can revive.
     * @param handler must be a valid MouseListener object that interacts with the player.
     * @param attacker must be a pointer to the current attacker.
     */
    public void selectMonsterToRevive(MouseListener handler, Player attacker);

    /**
     * Highlights the positions the selected monster can be revived.
     * @param positions must be a list that holds Integer objects. The integers on even indexes correspond to
     *                  a line and the next index corresponds to the position's row.
     * All positions included must be a set of the attacker's empty starting positions.
     */
    public void selectPositionToRevive(List<Integer> positions);

    /**
     * Displays anew the board upon the player's request.
     * @param attacker must be a pointer to the current attacker.
     * @param defender must be a pointer to the current defender.
     */
    public void restartGame(Player attacker, Player defender);

    /**
     * Ends the game and displays a menu requesting the game to either end or restart.
     */
    void endGame(Player attacker, MouseListener handler);

}
