package Protocol;

public class Flag {
    // Flags for welcome protocol.
    public static final byte SET_ID = 1;

    // Flags for start game protocol.
    public static final byte SELECTION = 1;

    // Flags for move protocol.
    public static final byte NO_COMBAT = 1;
    public static final byte COMBAT_VICTORIOUS = 2;
    public static final byte COMBAT_DEFEATED = 3;
    public static final byte COMBAT_TIE = 4;

    // Flags regarding end of game.
    public static final byte WON = 1;
    public static final byte LOST = 2;
    public static final byte OPPONENT_EXITED = 3;
    public static final byte OPPONENT_REMATCH = 4;

    // Flags regarding first turn.
    public static final byte FIRST = 1;
    public static final byte SECOND = 2;

    // Flags regarding selection.
    public static final byte HIGHLIGHT = 1;

    // Flags regarding revival
    public static final byte REVIVE_PANEL = 1;
    public static final byte REVIVE_MONSTER = 2;
    public static final byte REVIVE_POSITION = 3;
    public static final byte ENEMY_REVIVE = 4;

    // Flags regarding opponent's revival.
    public static final byte WAIT = 1;
    public static final byte REVIVED = 2;

    // Flags regarding board setup.
    public static final byte SELECT_MONSTER = 1;
    public static final byte POSITION_MONSTER = 2;
    public static final byte CLEAR_POSITION = 3;
    public static final byte RANDOMISE_BOARD = 4;
    public static final byte FINALISE_BOARD = 5;
    public static final byte WAITING_OPPONENT = 6;
    public static final byte OPPONENT_READY = 8;
    public static final byte START_GAME = 7;

}
