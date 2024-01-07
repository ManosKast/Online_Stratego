package View.Side_Panels;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class CapturedMonsters extends JPanel{
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
    private final ArrayList<Image> firstPlayersCaptives;
    private final ArrayList<Image> secondPlayersCaptives;

    public CapturedMonsters(int width, int height,  int gameMode){
        this.initialMonsters = ((gameMode != 2) && (gameMode != 3)) ? new int[]{1, 6, 1, 4, 5, 2, 2, 2, 3, 2, 1, 1} :
                                                                      new int[]{1, 3, 1, 2, 2, 1, 1, 1, 1, 1, 1, 1};

        this.firstPlayersCaptives = new ArrayList<>();
        this.secondPlayersCaptives = new ArrayList<>();

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

        this.getPictures();
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

        ImageIcon monstersImage;

        Font font = new Font("Verdana", Font.BOLD + Font.ITALIC, size);

        ArrayList<Image> images = (this.ID == 1) ? secondPlayersCaptives : firstPlayersCaptives;

        for(int i = 0; i < 4; ++i){
            for(int j = 0; j < 3; ++j){
                monstersPictures[i][j].setFont(font);
                monstersPictures[i][j].setText(Integer.toString(captives[monster]));
                monstersPictures[i][j].setIcon(new ImageIcon(images.get(monster).getScaledInstance(width,
                                                                            height, Image.SCALE_SMOOTH)));
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

    // Προσθέτει τις εικόνες των τεράτων που έκαστος παίκτης μπορεί να αιχμαλωτίσει.
    private void getPictures(){

        this.firstPlayersCaptives.add(new ImageIcon("src/images/bluePieces/flagB.png").getImage());
        this.secondPlayersCaptives.add(new ImageIcon("src/images/RedPieces/flagR.png").getImage());

        this.firstPlayersCaptives.add(new ImageIcon("src/images/bluePieces/trapB.png").getImage());
        this.secondPlayersCaptives.add(new ImageIcon("src/images/RedPieces/trapR.png").getImage());

        this.firstPlayersCaptives.add(new ImageIcon("src/images/bluePieces/slayerB.png").getImage());
        this.secondPlayersCaptives.add(new ImageIcon("src/images/RedPieces/slayerR.png").getImage());

        this.firstPlayersCaptives.add(new ImageIcon("src/images/bluePieces/scoutB.png").getImage());
        this.secondPlayersCaptives.add(new ImageIcon("src/images/RedPieces/scoutR.png").getImage());

        this.firstPlayersCaptives.add(new ImageIcon("src/images/bluePieces/dwarfB.png").getImage());
        this.secondPlayersCaptives.add(new ImageIcon("src/images/RedPieces/dwarfR.png").getImage());

        this.firstPlayersCaptives.add(new ImageIcon("src/images/bluePieces/elfB.png").getImage());
        this.secondPlayersCaptives.add(new ImageIcon("src/images/RedPieces/elfR.png").getImage());

        this.firstPlayersCaptives.add(new ImageIcon("src/images/bluePieces/yeti.png").getImage());
        this.secondPlayersCaptives.add(new ImageIcon("src/images/RedPieces/lavaBeast.png").getImage());

        this.firstPlayersCaptives.add(new ImageIcon("src/images/bluePieces/sorceressB.png").getImage());
        this.secondPlayersCaptives.add(new ImageIcon("src/images/RedPieces/sorceressR.png").getImage());

        this.firstPlayersCaptives.add(new ImageIcon("src/images/bluePieces/beastRiderB.png").getImage());
        this.secondPlayersCaptives.add(new ImageIcon("src/images/RedPieces/beastRiderR.png").getImage());

        this.firstPlayersCaptives.add(new ImageIcon("src/images/bluePieces/knightB.png").getImage());
        this.secondPlayersCaptives.add(new ImageIcon("src/images/RedPieces/knightR.png").getImage());

        this.firstPlayersCaptives.add(new ImageIcon("src/images/bluePieces/mageB.png").getImage());
        this.secondPlayersCaptives.add(new ImageIcon("src/images/RedPieces/mageR.png").getImage());

        this.firstPlayersCaptives.add(new ImageIcon("src/images/bluePieces/dragonB.png").getImage());
        this.secondPlayersCaptives.add(new ImageIcon("src/images/RedPieces/dragonR.png").getImage());
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
}