package View.Side_Panels;

import javax.swing.*;
import java.awt.*;

public class ActiveRules extends JPanel {

    private JLabel appliedRules;
    private JPanel appliedRulesPanels;
    private JLabel reducedArmy;
    private JLabel noRetreat;
    private JButton[] buttons = new JButton[2];
    private JPanel[] lines = new JPanel[2];
    private JPanel firstLine;
    private JPanel secondLine;

    public ActiveRules(int width, int height){
        this.setSize(width, height);

        this.setLayout(new BorderLayout());

        this.square();
    }

    private void square(){

        firstLine = new JPanel();
        firstLine.setBackground(Color.GRAY);

        secondLine = new JPanel();
        secondLine.setBackground(Color.GRAY);

        appliedRulesPanels = new JPanel();
        appliedRulesPanels.setLayout(new GridLayout(2, 1));
        appliedRulesPanels.setBackground(Color.GRAY);

        lines[0] = new JPanel();
        lines[0].setLayout(new BorderLayout());
        lines[0].setBackground(Color.GRAY);

        lines[1] = new JPanel();
        lines[1].setLayout(new BorderLayout());
        lines[1].setBackground(Color.GRAY);

        appliedRules = new JLabel("Applied Rules");
        appliedRules.setHorizontalAlignment(SwingConstants.CENTER);

        buttons[0] = new JButton();
        buttons[0].setBorderPainted(false);
        buttons[0].setOpaque(true);

        buttons[1] = new JButton();
        buttons[1].setBorderPainted(false);
        buttons[1].setOpaque(true);

        reducedArmy = new JLabel("No Retreat");
        reducedArmy.setForeground(Color.WHITE);
        reducedArmy.setHorizontalAlignment(SwingConstants.LEFT);

        noRetreat = new JLabel("Reduced Army");
        noRetreat.setForeground(Color.WHITE);
        noRetreat.setHorizontalAlignment(SwingConstants.LEFT);

        this.scalePanels();

        firstLine.add(reducedArmy);
        firstLine.add(buttons[0]);

        secondLine.add(noRetreat);
        secondLine.add(buttons[1]);

        lines[0].add(firstLine, BorderLayout.WEST);
        lines[1].add(secondLine, BorderLayout.WEST);

        appliedRulesPanels.add(lines[0]);
        appliedRulesPanels.add(lines[1]);

        this.add(appliedRules, BorderLayout.NORTH);
        this.add(appliedRulesPanels, BorderLayout.CENTER);
    }


    private void scalePanels(){
        int size = this.getHeight() / 15;
        int x;

        appliedRules.setFont(new Font("Verdana", Font.BOLD + Font.ITALIC, 2 * size));

        buttons[0].setPreferredSize(new Dimension(size, size));
        buttons[1].setPreferredSize(new Dimension(size, size));

        x = buttons[0].getPreferredSize().height - 5;

        buttons[0].setFont(new Font("Verdana", Font.BOLD, x));
        buttons[1].setFont(new Font("Verdana", Font.BOLD, x));

        reducedArmy.setFont(new Font("Verdana", Font.BOLD + Font.ITALIC, size));

        noRetreat.setFont(new Font("Verdana", Font.BOLD + Font.ITALIC, size));
    }

    public void scalePanel(int width, int height){
        this.setSize(width, height);
        this.scalePanels();
    }

    public void tickRules(int gameMode){
        if(gameMode == 1 || gameMode == 3){
            buttons[0].setMargin(new Insets(0, 0, 0, 0));
            buttons[0].setText("x");
        }
        if(gameMode == 2 || gameMode == 3){
            buttons[1].setMargin(new Insets(0, 0, 0, 0));
            buttons[1].setText("x");
        }
    }

}
