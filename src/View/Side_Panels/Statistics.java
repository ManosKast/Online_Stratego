package View.Side_Panels;

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

    public Statistics(int width, int height){
        this.setSize(width, height);
        this.setLayout(new BorderLayout());
        initialisePanels();
    }

    private void initialisePanels(){

        statistics = new JLabel("Statistics");
        statistics.setHorizontalAlignment(SwingConstants.CENTER);

        statisticsPanel = new JPanel();
        statisticsPanel.setLayout(new BorderLayout());
        statisticsPanel.setBackground(Color.GRAY);

        playersTurn = new JLabel("Player 1 turn");
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

    // Αλλάζει το πάνελ των στατιστικών.
    public void changeTurn(int ID, int ratio, int revivals){
        ++this.round;

        this.playersTurn.setText("Player " + ID + " turn");
        this.attackingRate.setText("  Successful attacking rate: " + ratio + "%");

        this.totalRevivals.setText("  Revivals: " + revivals);

        this.noRound.setText("Round: " + this.round + "  ");

    }

    private void scaleFonts(){
        int size = this.getHeight() / 15;

        statistics.setFont(new Font("Verdana", Font.BOLD + Font.ITALIC, 2 * size));

        playersTurn.setFont(new Font("Verdana", Font.BOLD + Font.ITALIC, size));

        attackingRate.setFont(new Font("Verdana", Font.BOLD + Font.ITALIC, size));

        totalRevivals.setFont(new Font("Verdana", Font.BOLD + Font.ITALIC, size));

        noRound.setFont(new Font("Verdana", Font.BOLD + Font.ITALIC, size));
    }

    public void restartGame(){this.round = 0;}
    public void scalePanels(int width, int height){
        this.setSize(width, height);
        this.scaleFonts();
    }

}
