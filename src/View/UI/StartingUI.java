package View.UI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

// Πάνελ για το αρχικό μενού.
public class StartingUI extends JPanel {

    MouseListener hover = new HoverMouse();
    private JPanel buttons;
    private final JButton[] button = new JButton[3];
    private JLabel chooseMode;

    public StartingUI(int width, int height, MouseListener handler){
        this.setSize(width, height);
        this.addComponentListener(new Resize());

        this.setBackground(Color.BLACK);

        this.setupPanels(handler);

        this.setVisible(true);
    }

    // Αρχικοποιεί τα panels, κουμπιά.
    private void setupPanels(MouseListener handler){

        chooseMode = new JLabel("Choose Mode");
        chooseMode.setOpaque(true);
        chooseMode.setBackground(Color.BLACK);
        chooseMode.setForeground(Color.WHITE);
        chooseMode.setHorizontalAlignment(SwingConstants.CENTER);

        buttons = new JPanel();
        buttons.setLayout(new GridLayout(2, 1));

        button[0] = new JButton();
        button[0].setOpaque(true);
        button[0].setBackground(Color.BLACK);
        button[0].setText("No Step Back");
        button[0].setForeground(Color.WHITE);
        button[0].setHorizontalAlignment(SwingConstants.CENTER);
        button[0].addMouseListener(hover);
        button[0].addMouseListener(handler);
        button[0].setFocusPainted(false);
        button[0].setBorderPainted(false);
        button[0].setName("1");
        buttons.add(button[0]);

        button[1] = new JButton();
        button[1].setOpaque(true);
        button[1].setBackground(Color.BLACK);
        button[1].setText("Reduced Army");
        button[1].setForeground(Color.WHITE);
        button[1].setHorizontalAlignment(SwingConstants.CENTER);
        button[1].addMouseListener(hover);
        button[1].setBorderPainted(false);
        button[1].setFocusPainted(false);
        button[1].addMouseListener(handler);
        button[1].setName("2");
        buttons.add(button[1]);

        button[2] = new JButton();
        button[2].setOpaque(true);
        button[2].setBackground(Color.BLACK);
        button[2].setText("Proceed to Game");
        button[2].setForeground(Color.WHITE);
        button[2].setBorderPainted(false);
        button[2].addMouseListener(hover);
        button[2].addMouseListener(handler);
        button[2].setFocusPainted(false);
        button[2].setHorizontalAlignment(SwingConstants.CENTER);
        button[2].setName("3");

        this.scalePanels();

        this.add(chooseMode, BorderLayout.NORTH);
        this.add(buttons, BorderLayout.CENTER);
        this.add(button[2], BorderLayout.SOUTH);
    }

    // Scale τα panels.
    private void scalePanels(){
        int size = this.getHeight() / 15;

        chooseMode.setFont(new Font("Verdana", Font.BOLD + Font.ITALIC, size));

        buttons.setPreferredSize(new Dimension(getWidth(), size * 11));

        button[0].setFont(new Font("Verdana", Font.BOLD + Font.ITALIC, size));
        button[1].setFont(new Font("Verdana", Font.BOLD + Font.ITALIC, size));

        button[2].setPreferredSize(new Dimension(this.getWidth(), 3 * size));
        button[2].setFont(new Font("Verdana", Font.BOLD + Font.ITALIC, size));
    }

    //Scale το panel.
    private void scalePanel(int width, int height){
        this.setSize(width, height);

        this.scalePanels();
    }

    public void disableUI(){
        this.setVisible(false);
        this.setEnabled(false);
    }


    // Αλλάζει χρώμα στο mode που το mouse περιφέρεται κάποια στιγμή.
    private static class HoverMouse implements MouseListener {

        // Αλλαγή χρώματος του κουμπιού κατόπιν επιλογής.
        @Override
        public void mouseClicked(MouseEvent e) {
            JButton button = (JButton) e.getSource();

            button.setBackground(Color.GRAY);
            button.setEnabled(false);
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

    private class Resize implements ComponentListener{

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
