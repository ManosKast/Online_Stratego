package View.Side_Panels;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;

import static Util.Util.scaleImage;

public class CapturedMonsters extends JPanel{
    private JLabel top = new JLabel("Captures");
    private JPanel capturedPanel = new JPanel();
    private JLabel bottom = new JLabel("Captured: 0");
    private final JLabel[][] monstersPictures = new JLabel[4][3]; // Όλες οι εικόνες και τα texts.
    private JPanel images; // Πάνελ που περιέχει όλες τις εικόνες.
    private int picturesWidth;
    private int picturesHeight;
    private final int[] captives = new int[12];
    private int ID;
    private final BufferedImage[] enemyMonsters;
    private int[] capturedMonsters;

    public CapturedMonsters(int width, int height, BufferedImage[] enemyMonsters){
        this.enemyMonsters = enemyMonsters;
        this.capturedMonsters = new int[enemyMonsters.length];
        for (int i = 0; i < enemyMonsters.length; i++) this.capturedMonsters[i] = 0;

        // Το Resize του View, επικαλείται πάντα στην αρχή.
        // Εφόσον δε δημιούργησα μέθοδο αρχικοποίησης των πάνελς
        // και επικαλούμαι τα nextRound.
        this.ID = -1;

        this.setSize(new Dimension(width, height));
        this.setLayout(new BorderLayout());

        this.setUpBottomHalf();

        this.setVisible(true);
    }

    private void setUpBottomHalf(){
        JPanel bottomHalf = new JPanel();
        bottomHalf.setLayout(new BorderLayout());
        bottomHalf.setPreferredSize(new Dimension(this.getWidth(), this.getHeight()));

        setUpLabels();

        this.capturedPanel.setBackground(Color.GRAY);
        this.addCaptiveMonsters();

        bottomHalf.add(this.top, BorderLayout.NORTH);
        bottomHalf.add(this.capturedPanel, BorderLayout.CENTER);

        this.add(bottomHalf);
    }

    private void setUpLabels(){
        this.top = new JLabel("Captures");

        this.top.setHorizontalAlignment(SwingConstants.CENTER);

        this.capturedPanel = new JPanel();
        this.capturedPanel.setLayout(new BorderLayout());

        this.bottom = new JLabel("Captured: 0");
        this.bottom.setForeground(Color.WHITE);

        this.images = new JPanel();
        this.images.setLayout(new GridLayout(4, 3));
        this.images.setBackground(Color.GRAY);

        this.scaleCapturedPanel();
        this.setUpImages();
    }


    private void setUpImages(){
        for(int i = 0; i < 4; ++i){
            for(int j = 0; j < 3; ++j){
                monstersPictures[i][j] = new JLabel("", null, JLabel.LEADING);
                images.add(monstersPictures[i][j]);
            }
        }

        this.getCaptiveMonsters();
    }

    private void addCaptiveMonsters(){
        this.picturesWidth = this.getWidth();
        this.picturesHeight = this.getHeight();

        this.capturedPanel.add(images, BorderLayout.CENTER);
        this.capturedPanel.add(bottom, BorderLayout.SOUTH);
        this.add(this.capturedPanel);
    }

    public void nextRound(int[] referenceToMonsters){
        this.ID = (this. ID == 1) ? 2 : 1;
        this.getTotalOfCaptives(referenceToMonsters);
        this.getCaptiveMonsters();
    }

    private void getCaptiveMonsters(){
        int monster = 0;
        int size = this.getHeight() / 23;
        int height = this.getHeight() / 5;
        int width = this.getWidth() / 6;

        Font font = new Font("Verdana", Font.BOLD + Font.ITALIC, size);

        for(int i = 0; i < 4; ++i){
            for(int j = 0; j < 3; ++j){
                monstersPictures[i][j].setFont(font);
                monstersPictures[i][j].setText(Integer.toString(capturedMonsters[monster]));
                monstersPictures[i][j].setIcon(scaleImage(enemyMonsters[monster], width, height));
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
            captives[i] = capturedMonsters[i];
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
    public void restartGame(){
        Arrays.fill(capturedMonsters, 0);

        for(JLabel[] row : monstersPictures)
            for(JLabel monster : row)
                monster.setText(Integer.toString(0));

        setTotalCaptives(0);
    }

    private void scaleCapturedPanel(){
        int size = this.getHeight() / 30;

        this.top.setFont(new Font("Verdana", Font.BOLD + Font.ITALIC, 2 * size));
        this.bottom.setFont(new Font("Verdana", Font.BOLD + Font.ITALIC, size));
    }

    public void captureMonster(int index) {
        ++this.capturedMonsters[index];
        updateCapturedMonsters(index);
    }

    public void rescueMonster(int index) {
        --this.capturedMonsters[index];
        updateCapturedMonsters(index);
    }

    private void updateCapturedMonsters(int index) {
        int row = index / 3;
        int col = index % 3;
        int size = this.getHeight() / 23;
        // TODO: Font class's field.
        Font font = new Font("Verdana", Font.BOLD + Font.ITALIC, size);

        monstersPictures[row][col].setFont(font);
        monstersPictures[row][col].setText(Integer.toString(capturedMonsters[index]));

        int sum = 0;
        for(int captives : capturedMonsters) sum += captives;

        setTotalCaptives(sum);
    }
}
