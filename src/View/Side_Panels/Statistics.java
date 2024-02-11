package View.Side_Panels;

import Protocol.Flag;

import javax.swing.*;
import java.awt.*;

public class Statistics extends JPanel {

    private JLabel statistics;
    private JPanel statisticsPanel;
    private JLabel playersTurn;
    private JLabel attackingRate;
    private JPanel bottomText;
    private JLabel totalRevivals;
    private JLabel noRound;
    private int round;
    private int playerID;
    private final String yourTurn = "Your turn";
    private final static String opponentsTurn = "Opponent's turn";
    private final static String opponentReviving = "Opponent is reviving";
    private boolean myTurn;
    private int revivalCount = 0;


    public Statistics(int width, int height, int ID){
        this.setSize(width, height);
        this.setLayout(new BorderLayout());
        this.playerID = ID;
        initialisePanels();
    }

    public Statistics(int width, int height, int ID, byte flag, byte turn) {
        this.setSize(width, height);
        this.setLayout(new BorderLayout());
        this.playerID = ID;
        this.myTurn = (turn == Flag.FIRST);
        System.out.println(myTurn);
        initialisePanels();

        if (flag == Flag.WAITING_OPPONENT){
            this.waitingOpponent();
        } else if (flag == Flag.START_GAME) {
            String text = (myTurn) ? yourTurn : opponentsTurn;
            this.playersTurn.setText(text);
        }
    }

    private void initialisePanels(){

        statistics = new JLabel("Statistics");
        statistics.setHorizontalAlignment(SwingConstants.CENTER);

        statisticsPanel = new JPanel();
        statisticsPanel.setLayout(new BorderLayout());
        statisticsPanel.setBackground(Color.GRAY);

        playersTurn = new JLabel("");
        playersTurn.setHorizontalAlignment(SwingConstants.CENTER);
        playersTurn.setForeground(Color.WHITE);

        attackingRate = new JLabel("  Successful attacking rate: 0%");
        attackingRate.setForeground(Color.WHITE);

        bottomText = new JPanel();
        bottomText.setLayout(new BorderLayout());
        bottomText.setBackground(Color.GRAY);

        totalRevivals = new JLabel("  Revivals: 0");
        totalRevivals.setForeground(Color.WHITE);

        noRound = new JLabel("Round: 1  ");
        noRound.setForeground(Color.WHITE);

        this.scaleFonts();

        bottomText.add(totalRevivals, BorderLayout.WEST);
        bottomText.add(noRound, BorderLayout.EAST);

        statisticsPanel.add(playersTurn, BorderLayout.NORTH);
        statisticsPanel.add(attackingRate, BorderLayout.CENTER);
        statisticsPanel.add(bottomText, BorderLayout.SOUTH);

        this.add(statistics, BorderLayout.NORTH);
        this.add(statisticsPanel, BorderLayout.CENTER);
    }

    public void nextRound() {
        System.out.println(myTurn);
        ++this.round;
        myTurn = !myTurn;
        String text = (myTurn) ? yourTurn : opponentsTurn;
        this.playersTurn.setText(text);
        this.noRound.setText("Round: " + this.round + "  ");
    }

    public void nextRound(int ratio) {
        nextRound();
        attackingRate.setText("  Successful attacking rate: " + ratio + "%");
    }


    public void retractNextRound() {
        String text = (myTurn) ? yourTurn : opponentsTurn;
        myTurn = !myTurn;
        this.playersTurn.setText(text);
        this.noRound.setText("Round: " + --this.round + "  ");
    }

    public void decrementNextRound() {
        myTurn = !myTurn;
        this.noRound.setText("Round: " + --this.round + "  ");
    }

    public void enemyReviving() {
        // nextRound gets called after a successful move, so we need to undo its effects.
        myTurn = !myTurn;
        noRound.setText("Round: " + --round + "  ");
        this.playersTurn.setText("Enemy is reviving");
    }

    public void incrementRevivals() {
        this.totalRevivals.setText("  Revivals: " + ++this.revivalCount);
    }

    private void scaleFonts(){
        int size = this.getHeight() / 15;

        statistics.setFont(new Font("Verdana", Font.BOLD + Font.ITALIC, 2 * size));

        playersTurn.setFont(new Font("Verdana", Font.BOLD + Font.ITALIC, size));

        attackingRate.setFont(new Font("Verdana", Font.BOLD + Font.ITALIC, size));

        totalRevivals.setFont(new Font("Verdana", Font.BOLD + Font.ITALIC, size));

        noRound.setFont(new Font("Verdana", Font.BOLD + Font.ITALIC, size));
    }

    public void restartGame(byte flag){
        this.round = 0;
        this.revivalCount = 0;
        this.attackingRate.setText("  Successful attacking rate: 0%");
        this.totalRevivals.setText("  Revivals: 0");
        this.noRound.setText("Round: 1  ");
        if (flag == Flag.WAITING_OPPONENT)
            this.waitingOpponent();
    }

    public void scalePanels(int width, int height){
        this.setSize(width, height);
        this.scaleFonts();
    }

    public void opponentReady(byte turn) {
        myTurn = (turn == Flag.FIRST);
        String text = (myTurn) ? yourTurn : opponentsTurn;
        this.playersTurn.setText(text);
    }

    public void waitingOpponent() {
        this.playersTurn.setText("Waiting for opponent");
    }
}
