package View.Side_Panels;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import static Util.Util.scaleImage;

public class SelectionUI extends JPanel {

    private final MouseListener listener;
    private JButton[][] boardSquares = new JButton[3][4];
    private JLabel top = new JLabel("Captures");
    private JPanel capturedPanel = new JPanel();
    private JLabel bottom = new JLabel("Captured: 0");
    private final JLabel[][] monstersPictures = new JLabel[4][3]; // Όλες οι εικόνες και τα texts.
    private JPanel images; // Πάνελ που περιέχει όλες τις εικόνες.
    private int picturesWidth;
    private int picturesHeight;
    private final int[] initialMonsters; // Υπάρχει κυρίως για το scaling.
    private final int[] captives = new int[12];
    private int ID;
    private JButton randomise;
    private JButton confirm;
    private JPanel buttons;
    private JPanel monstersPanel;
    private JPanel[][] panels = new JPanel[3][4];
    private JLabel[][] labels = new JLabel[3][4];
    private BufferedImage[] monsters;

    public SelectionUI(int width, int height, int gameMode, MouseListener mouseHandler, BufferedImage[] monsters){
        setPreferredSize(new Dimension(width, height));
        setLayout(new BorderLayout());
        this.initialMonsters = ((gameMode != 2) && (gameMode != 3)) ? new int[]{1, 1, 4, 5, 2, 2, 2, 3, 2, 1, 1, 6} :
                new int[]{1, 1, 2, 2, 1, 1, 1, 1, 1, 1, 1, 3};

        this.monsters = monsters;

        // Το Resize του View, επικαλείται πάντα στην αρχή.
        // Εφόσον δε δημιούργησα μέθοδο αρχικοποίησης των πάνελς
        // και επικαλούμαι τα nextRound.
        this.ID = -1;
        this.listener = mouseHandler;

        this.setSize(new Dimension(width, height));
        this.setLayout(new BorderLayout());

        // TODO: Make it field and scale everything.
        JLabel top = new JLabel("Set-up board");
        top.setFont(new Font("Verdana", Font.BOLD + Font.ITALIC, height/20));
        top.setHorizontalAlignment(SwingConstants.CENTER);
        this.add(top, BorderLayout.NORTH);

        this.setUpButtons();

        this.initialiseBoard();
        this.setVisible(true);
    }

    // TODO: Maybe add different colors on buttons.
    private void setUpButtons() {
        this.randomise = new JButton("Randomise");
        this.randomise.setName("randomise");
        this.randomise.setBackground(Color.RED);
        this.randomise.setFocusPainted(false);
        this.randomise.addMouseListener(listener);

        this.confirm = new JButton("Confirm");
        this.confirm.setName("finalise");
        this.confirm.setBackground(Color.GREEN);
        this.confirm.setFocusPainted(false);
        this.confirm.addMouseListener(listener);

        this.buttons = new JPanel();
        this.buttons.setLayout(new GridLayout(1, 2));

        this.buttons.add(this.randomise);
        this.buttons.add(this.confirm);

        this.add(this.buttons, BorderLayout.SOUTH);
    }



    private void initialiseBoard(){
        monstersPanel = new JPanel();
        monstersPanel.setLayout(new GridLayout(3, 4));

        JButton newButton;

        int height = this.getHeight();
        int width = this.getWidth();


        for(int i = 0; i < 3; ++i){
            for(int j = 0; j < 4; ++j){
                boardSquares[i][j] = new JButton();
                newButton = boardSquares[i][j];

                // TODO: Simplify this.
                JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
                panels[i][j] = panel;
                newButton.setName("" + (j + 4*i));
                newButton.addMouseListener(listener);
                newButton.setIcon(scaleImage(monsters[j + 4*i], width/6, height/5));
                JLabel label = new JLabel("Total: " + initialMonsters[j + 4*i]);
                Font font = new Font("Verdana", Font.BOLD + Font.ITALIC, height/40);
                label.setFont(font);
                labels[i][j] = label;
                panel.add(label);
                panel.add(newButton);
                monstersPanel.add(panel);
            }
        }

        this.add(monstersPanel, BorderLayout.CENTER);
    }

    private void setRemainingMonsters() {

    }
    private void getCaptiveMonsters(){
        int monster = 0;
        int size = this.getHeight() / 23;
        int height = this.getHeight() / 5;
        int width = this.getWidth() / 6;

        ImageIcon monstersImage;

        Font font = new Font("Verdana", Font.BOLD + Font.ITALIC, size);

        for(int i = 0; i < 4; ++i){
            for(int j = 0; j < 3; ++j){
                monstersPictures[i][j].setFont(font);
                monstersPictures[i][j].setText(Integer.toString(captives[monster]));
                monstersPictures[i][j].setIcon(scaleImage(monsters[monster], width, height));
                ++monster;
            }
        }
    }

    private void getTotalOfCaptives(int[] referenceToMonsters){
        if(referenceToMonsters == null)
            return;

        int size = referenceToMonsters.length;
        int sum = 0;

        for(int i = 0; i < size; ++i) {
            captives[i] = this.initialMonsters[i] - referenceToMonsters[i];
            sum += captives[i];
        }

        this.setTotalCaptives(sum);

    }

    private void setTotalCaptives(int sum){
        this.bottom.setText("Captured: " + sum);
    }


    public void scalePanel(int width, int height){
        if(this.ID == -1){
            this.ID = 2;
            return;
        }
        this.setSize(width, height);

        this.picturesWidth = this.getWidth();
        this.picturesHeight = this.getHeight();

        this.getCaptiveMonsters();
        this.scaleCapturedPanel();
    }

    // Επειδή, αλλάζει μόνο του rounds.
    public void restartGame(){this.ID = 2;}

    private void scaleCapturedPanel(){
        int size = this.getHeight() / 30;

        this.top.setFont(new Font("Verdana", Font.BOLD + Font.ITALIC, 2 * size));
        this.bottom.setFont(new Font("Verdana", Font.BOLD + Font.ITALIC, size));
    }

    public void decrementMonster(int selectedMonster) {
        --this.initialMonsters[selectedMonster];
        int row = selectedMonster / 4;
        int col = selectedMonster % 4;

        this.labels[row][col].setText("Total: " + this.initialMonsters[selectedMonster]);
    }

    public void increment(int monster) {
        ++initialMonsters[monster];
        int row = monster / 4;
        int col = monster % 4;

        labels[row][col].setText("Total: " + initialMonsters[monster]);
    }

    // Αλλάζει χρώμα στο mode που το mouse περιφέρεται κάποια στιγμή.
    private static class HoverMouse implements MouseListener {

        // Αλλαγή χρώματος του κουμπιού κατόπιν επιλογής.
        @Override
        public void mouseClicked(MouseEvent e) {
            JButton button = (JButton) e.getSource();

            System.out.println("Button " + button.getName() + " was clicked.");
        }

        @Override
        public void mousePressed(MouseEvent e) {}
        @Override
        public void mouseReleased(MouseEvent e) {}
        @Override
        public void mouseEntered(MouseEvent e) {}
        @Override
        public void mouseExited(MouseEvent e) {}
    }

    // TODO: Resize
    // TODO: Hover mouse red borders indicating cell clearance, during deployment phase.
}
