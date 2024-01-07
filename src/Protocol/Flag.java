package Protocol;

public class Flag {
    // Flags for welcome protocol.
    public static final byte SET_ID = 1;

    // Flags for move protocol.
    public static final byte NO_COMBAT = 1;
    public static final byte COMBAT_VICTORIOUS = 2;
    public static final byte COMBAT_DEFEATED = 3;
    public static final byte COMBAT_TIE = 4;

    // Flags regarding end of game.
    public static final byte WON = 1;
    public static final byte LOST = 2;

    // Flags regarding first turn.
    public static final byte FIRST = 1;
    public static final byte SECOND = 2;

    // Flags regarding selection.
    public static final byte HIGHLIGHT = 1;
}
