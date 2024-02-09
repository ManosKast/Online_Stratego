package View.UI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class EndGameUI extends JPanel {

    JLabel playerWins;
    JPanel options;
    JButton[] option = new JButton[2];
    String outcome;

    public EndGameUI(int width, int height, String ID, MouseListener handler){
        this.setSize(width, height);
        this.setBackground(Color.BLACK);
        this.setLayout(new BorderLayout());
        this.outcome = ID;

        this.initialisePanels(handler);

        this.setVisible(true);
        this.addComponentListener(new Resize());
    }

    private void initialisePanels(MouseListener handler){
        JButton button;

        playerWins = new JLabel(outcome);
        playerWins.setOpaque(true);
        playerWins.setBackground(Color.BLACK);
        playerWins.setForeground(Color.WHITE);
        playerWins.setHorizontalAlignment(SwingConstants.CENTER);

        options = new JPanel();
        options.setLayout(new GridLayout(2, 1));

        for(int i = 0; i < 2; ++i){
            option[i] = new JButton();
            button = option[i];

            button.setBackground(Color.BLACK);
            button.setOpaque(true);
            button.setRolloverEnabled(false);
            button.setForeground(Color.WHITE);
            button.setBorderPainted(false);
            button.setFocusPainted(false);

            if(i == 0) {
                button.setText("Replay");
                button.setName("Replay");
            }
            else {
                button.setText("Exit");
                button.setName("Exit");
            }

            button.addMouseListener(new HoverMouse());

            button.addMouseListener(handler);
            options.add(button);
        }

        this.scalePanel(this.getWidth(), this.getHeight());

        this.add(playerWins, BorderLayout.NORTH);
        this.add(options, BorderLayout.CENTER);
    }

    private void scalePanel(int width, int height){
        int size = height / 15;

        this.playerWins.setFont(new Font("Verdana", Font.BOLD + Font.ITALIC, size));

        for(int i = 0; i < 2; ++i)
            this.option[i].setFont(new Font("Verdana", Font.BOLD + Font.ITALIC, size));

        this.options.setSize(width, height - size);
    }


    public void disableUI(){
        this.setVisible(false);
        this.setEnabled(false);
    }

    public void opponentExited() {
        playerWins.setText("<html><center>" + playerWins.getText() + "<br>Opponent exited the game</center></html>");        playerWins.setHorizontalAlignment(SwingConstants.CENTER);
        option[0].setText("New Game");
    }

    // Αλλάζει χρώμα στο mode που το mouse περιφέρεται κάποια στιγμή.
    private static class HoverMouse implements MouseListener {

        @Override
        public void mouseClicked(MouseEvent e) {

        }

        @Override
        public void mousePressed(MouseEvent e) {

        }

        @Override
        public void mouseReleased(MouseEvent e) {

        }

        @Override
        public void mouseEntered(MouseEvent e) {
            JButton hover = (JButton) e.getSource();

            hover.setForeground(Color.RED);
        }

        @Override
        public void mouseExited(MouseEvent e) {
            JButton hover = (JButton) e.getSource();

            hover.setForeground(Color.WHITE);
        }
    }

    private class Resize implements ComponentListener {

        @Override
        public void componentResized(ComponentEvent e) {
            scalePanel(getWidth(), getHeight());
        }

        @Override
        public void componentMoved(ComponentEvent e) {

        }

        @Override
        public void componentShown(ComponentEvent e) {

        }

        @Override
        public void componentHidden(ComponentEvent e) {

        }
    }

}
