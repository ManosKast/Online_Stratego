package View.UI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import static Util.Util.scaleImage;

public class ReviveMonster extends JPanel {

    private JLabel chooseMonster;
    private JPanel revivalPanel;
    private final JButton[] buttons = new JButton[10];
    private final int[] capturedMonsters;
    private final BufferedImage[] monsters;

    public ReviveMonster(int width, int height, int[] capturedMonsters, BufferedImage[] monsters, MouseListener handler){
        this.setSize(width, height);
        this.setLayout(new BorderLayout());
        this.setBackground(Color.BLACK);
        this.addComponentListener(new Resize());

        this.monsters = monsters;
        this.capturedMonsters = capturedMonsters;

        this.revivePanel(handler);

        this.setVisible(true);
    }

    // Δημιουργεί το πάνελ αναγέννησης.
    private void revivePanel(MouseListener handler){

        chooseMonster = new JLabel("Choose monster to revive");
        chooseMonster.setOpaque(true);
        chooseMonster.setBackground(Color.BLACK);
        chooseMonster.setForeground(Color.WHITE);
        chooseMonster.setHorizontalAlignment(SwingConstants.CENTER);

        revivalPanel = new JPanel();
        revivalPanel.setBackground(Color.BLACK);
        revivalPanel.setLayout(new GridLayout(2, 5));

        this.addButtons(handler);
        this.scalePanels();

        this.add(chooseMonster, BorderLayout.NORTH);
        this.add(revivalPanel, BorderLayout.CENTER);

    }

    // Πρόσθεσε το scaleButtons.
    private void scalePanels(){
        int size = this.getHeight() / 30;

        chooseMonster.setFont(new Font("Verdana", Font.BOLD + Font.ITALIC, size));

        this.scaleButtons();
    }

    // Προσθέτει τα κουμπιά στο πάνελ.
    private void addButtons(MouseListener handler){
        JButton but;

        for(int i = 0; i < 10; ++i){
            buttons[i] = new JButton();

            but = buttons[i];

            but.addMouseListener(handler);
            but.setName(String.valueOf(i));
            revivalPanel.add(but);
        }
    }

    // Κάνει scale τις εικόνες για να εφαρμόζονται σχεδόν ιδανικά στη κουμπί.
    private void scaleButtons(){
        int width = this.getWidth() * 29 / 150;
        int height = this.getHeight() * 29 / 60;

        for(int i = 0; i < 10; ++i){
            ImageIcon monstersIcon = scaleImage(monsters[i + 1], width, height);
            this.buttons[i].setIcon(monstersIcon);
            this.buttons[i].setDisabledIcon(monstersIcon);

            // Εάν δεν έχει αιχμαλωτηθεί τέρας του εν λόγω είδους, τότε δε γίνεται να αναγεννηθεί.
            if(!this.canRevive(i + 2)) {
                this.buttons[i].setIcon(null);
                this.buttons[i].setBackground(Color.BLACK);
                this.buttons[i].setEnabled(false);
            }
        }
    }

    // Ελέγχει εάν το τέρας monster μπορεί να αναγεννηθεί.
    // Το initialMonsters συγκρατεί τα τέρατα που ανήκουν στον εν λόγω παίκτη στην αρχή του παιχνιδιού.
    // Το currentMonsters συγκρατεί τα τέρατα που διαθέτει ο εν λόγω παίκτης.
    // monster + 2, επειδή οι δείκτες 0 και 1 περιέχουν τη σημαία και τις παγίδες.
    private boolean canRevive(int monster){
        return (this.capturedMonsters[monster] != 0);
    }

    // Αφαιρεί το πάνελ από το φρέιμ του View.
    public void disablePanel(){
        this.setVisible(false);
        this.setEnabled(false);
    }

    // Για scaling.
    private class Resize implements ComponentListener {

        @Override
        public void componentResized(ComponentEvent e) {
            scalePanels();
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
