package View;

import Model.Player_and_Board.Player;
import Model.game_characters.GameCharacters;
import Model.game_characters.ImmovableObjects.ImmovableMonsters.Flag;
import Model.game_characters.ImmovableObjects.ImmovableMonsters.Trap;
import View.Side_Panels.ActiveRules;
import View.Side_Panels.CapturedMonsters;
import View.Side_Panels.Statistics;
import View.UI.EndGameUI;
import View.UI.ReviveMonster;
import View.UI.StartingUI;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class View extends JFrame implements ViewInterface {

    private int buttonWidth;
    private int buttonHeight;
    private int gameMode;
    private final JButton[][] boardSquares = new JButton[8][10];
    private final JPanel squares = new JPanel();
    private final JPanel roundPanel = new JPanel();
    private final JPanel topHalfRoundPanel = new JPanel();
    private final Resize window = new Resize();
    private final List<JButton> borderedSquares = new ArrayList<>();
    private ActiveRules rules;
    private CapturedMonsters monsters;
    private Statistics stats;
    private final StartingUI startingUI;
    private ReviveMonster reviveMonster;
    private EndGameUI endGameUI;
    private BufferedImage[] voidMonstersPictures = new BufferedImage[13];
    private BufferedImage[] lightMonstersPictures = new BufferedImage[13];
    private ImageIcon[] cardsBack = new ImageIcon[2];
    private final BufferedImage[][] currentButtonsImages = new BufferedImage[8][10];
    private BufferedImage[] userMonsters;
    private BufferedImage[] opponentMonsters;

    public View(MouseListener handler){
        super("Stratego");

        this.initialiseVoidMonstersPictures();
        this.initialiseLightMonstersPictures();

        this.setSize(800,600);

        this.setLayout(new BorderLayout());

        startingUI = new StartingUI(this.getWidth(), this.getHeight(), handler);

        this.add(startingUI);

        this.setVisible(true);

        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

    }
    // 0 : Flag , 1 : Trap, 2 : Slayer , 3 : Scout , 4 : Dwarf , 5 : Elf , 6 : Lava Beast / Yeti ,
    // 7 : Sorceress , 8 : Beast Rider , 9 : Knight , 10 : Mage , 11 : Dragon .
    private void initialiseVoidMonstersPictures() {
        String[] voidMonsterPaths = {
                "src/images/RedPieces/flagR.png",
                "src/images/RedPieces/slayerR.png",
                "src/images/RedPieces/scoutR.png",
                "src/images/RedPieces/dwarfR.png",
                "src/images/RedPieces/elfR.png",
                "src/images/RedPieces/lavaBeast.png",
                "src/images/RedPieces/sorceressR.png",
                "src/images/RedPieces/beastRiderR.png",
                "src/images/RedPieces/knightR.png",
                "src/images/RedPieces/mageR.png",
                "src/images/RedPieces/dragonR.png",
                "src/images/RedPieces/trapR.png",
                "src/images/bluePieces/blueHidden.png"
        };

        for (int i = 0; i < voidMonsterPaths.length; i++) {
            voidMonstersPictures[i] = loadImage(voidMonsterPaths[i]);
        }
    }

    private void initialiseLightMonstersPictures() {
        String[] lightMonsterPaths = {
                "src/images/bluePieces/flagB.png",
                "src/images/bluePieces/slayerB.png",
                "src/images/bluePieces/scoutB.png",
                "src/images/bluePieces/dwarfB.png",
                "src/images/bluePieces/elfB.png",
                "src/images/bluePieces/yeti.png",
                "src/images/bluePieces/sorceressB.png",
                "src/images/bluePieces/beastRiderB.png",
                "src/images/bluePieces/knightB.png",
                "src/images/bluePieces/mageB.png",
                "src/images/bluePieces/dragonB.png",
                "src/images/bluePieces/trapB.png",
                "src/images/RedPieces/redHidden.png"
        };

        for (int i = 0; i < lightMonsterPaths.length; i++) {
            lightMonstersPictures[i] = loadImage(lightMonsterPaths[i]);
        }
    }

    // Utility method to load image from path
    private BufferedImage loadImage(String path) {
        try {
            return ImageIO.read(new File(path));
        } catch (IOException e) {
            e.printStackTrace(); // Consider a more robust error handling strategy
            return null;
        }
    }
    private void initialiseBoard(MouseListener buttonHandler){
        JButton newButton;

        int height = this.getHeight() - 35;
        int width = this.getWidth() * 2/3;
        
        GridLayout layout = new GridLayout(8, 10);

        this.squares.setLayout(layout);
        this.squares.setSize(width, height);

        for(int i = 0; i < 8; ++i){
            for(int j = 0; j < 10; ++j){
                boardSquares[i][j] = new JButton();
                newButton = boardSquares[i][j];

                newButton.setName("" + i + j);
                newButton.addMouseListener(buttonHandler);
                newButton.setRolloverEnabled(false);

                squares.add(newButton);
            }
        }

        this.addForbiddenZone();

        this.add(squares, FlowLayout.LEFT);
    }

    // Προσθέτει την απαγορευμένη ζώνη.
    private void addForbiddenZone(){
        for(int i = 3; i <= 4; ++i){
            boardSquares[i][2].setBackground(Color.BLACK);
            this.boardSquares[i][2].setBorderPainted(false);
            boardSquares[i][3].setBackground(Color.BLACK);
            this.boardSquares[i][3].setBorderPainted(false);
            boardSquares[i][6].setBackground(Color.BLACK);
            this.boardSquares[i][6].setBorderPainted(false);
            boardSquares[i][7].setBackground(Color.BLACK);
            this.boardSquares[i][7].setBorderPainted(false);

        }
    }

    private void initialiseRoundPanel(){
        int width = this.getWidth() - this.squares.getWidth();
        int height = this.getHeight() - 35;

        this.roundPanel.setSize(new Dimension(width, height));
        this.roundPanel.setLayout(new GridLayout(2, 1));

        this.topHalfRoundPanel.setLayout(new GridLayout(2, 1));
        this.topHalfRoundPanel.setSize(new Dimension(width, height / 2));

        this.rules = new ActiveRules(width, height / 4);
        this.rules.tickRules(gameMode);

        this.stats = new Statistics(width, height / 4);
        this.monsters = new CapturedMonsters(width, height / 2, gameMode);

        this.topHalfRoundPanel.add(this.rules);
        this.topHalfRoundPanel.add(this.stats);

        this.roundPanel.add(this.topHalfRoundPanel);
        this.roundPanel.add(this.monsters);

        this.add(roundPanel, BorderLayout.EAST);
    }

    public void startGame(MouseListener buttonHandler, int gameMode){
        this.gameMode = gameMode;

        this.startingUI.disableUI();

        this.initialiseBoard(buttonHandler);
        this.initialiseRoundPanel();

        this.setVisible(true);
        this.addComponentListener(window);
    }

    @Override
    public void nextRound(Player attacker, Player defender) {

    }


    private BufferedImage scaleImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        BufferedImage scaledImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = scaledImage.createGraphics();

        // Improve the scaling quality
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
        g2d.dispose();

        return scaledImage;
    }

    // Refactor newImage method to return an ImageIcon
    private ImageIcon newIcon(BufferedImage originalImage) {
        BufferedImage scaledImage = scaleImage(originalImage, buttonWidth, buttonHeight);
        return new ImageIcon(scaledImage);
    }

    // TODO: Bug with resizing.
    void resizeImage(int row, int column) {
        System.out.println("Resizing image");
        boardSquares[row][column].setIcon(newIcon(currentButtonsImages[row][column]));
    }

    public void highlightPositions(List<Integer> validMoveablePositions){
        int line;
        int row;

        this.removeBorders();

        for(int i = 0; i < validMoveablePositions.size(); ++i){
            line = validMoveablePositions.get(i);
            row = validMoveablePositions.get(++i);

            boardSquares[line][row].setBorder(BorderFactory.createLineBorder(Color.GREEN, buttonWidth / 10));
            borderedSquares.add(boardSquares[line][row]);
        }

        repaint();
        setVisible(true);

    }


    public void updateBoard(GameCharacters winner, int cLine, int cRow, int nLine, int nRow){

        // Αν η μάχη ήταν ισόπαλη τότε αφαίρεσε τη κάρτα του αμυνόμενου από το ταμπλό.
        if(winner == null) {
            boardSquares[nLine][nRow].setIcon(null);
            currentButtonsImages[nLine][nRow] = null;
        }

        // Το τέρας του επιτιθέμενου δε γίνεται να παραμείνει στην ίδια θέση, συνεπώς
        // αφαιρώ την εικόνα του από το κουμπί στο οποίο βρισκόταν.
        boardSquares[cLine][cRow].setIcon(null);
        boardSquares[cLine][cRow].setRolloverEnabled(false);
        currentButtonsImages[cLine][cRow] = null;

    }

    public void moveCharacter(int[] previousPosition, int[] nextPosition){
        int pRow = previousPosition[0], pColumn = previousPosition[1];
        int nRow = nextPosition[0], nColumn = nextPosition[1];

        currentButtonsImages[nRow][nColumn] = currentButtonsImages[pRow][pColumn];
        currentButtonsImages[pRow][pColumn] = null;
        boardSquares[nRow][nColumn].setIcon(newIcon(currentButtonsImages[nRow][nColumn]));
        boardSquares[pRow][pColumn].setIcon(null);

        this.removeBorders();
    }

    // Αφαιρεί τα borders που ζωγραφίσαμε στα κουμπιά.
    private void removeBorders(){
        for(JButton button : this.borderedSquares)
            button.setBorder(UIManager.getLookAndFeel().getDefaults().getBorder("Button.border"));

        borderedSquares.clear();
    }


    public void selectMonsterToRevive(MouseListener handler, Player attacker){
        this.concealGame();

        reviveMonster = new ReviveMonster(this.getWidth(), this.getHeight(), attacker.getInitialReferenceToMonsters(),
                                                        attacker.getReferenceToMonsters(), attacker.getID(), handler);

        this.add(reviveMonster);
        this.repaint();
        this.setVisible(true);
    }

    public void selectPositionToRevive(List<Integer> positions){
        if(positions == null)
            return;
        if(positions.size() == 0)
            return;

        reviveMonster.disablePanel();

        this.squares.setVisible(true);
        this.roundPanel.setVisible(true);

        this.highlightPositions(positions);
    }

    // Κρύβει το επιτραπέζιο παιχνίδι, για να εμφανίσει το πάνελ αναγέννησης.
    private void concealGame(){
        this.squares.setVisible(false);
        this.roundPanel.setVisible(false);
    }

    public void endGame(Player attacker, MouseListener handler){
        this.concealGame();

        endGameUI = new EndGameUI(this.getWidth(), this.getHeight(), attacker.getID(), handler);

        this.add(endGameUI);
    }


    public void restartGame(Player attacker, Player defender){
        endGameUI.disableUI();

        this.squares.setVisible(true);
        this.roundPanel.setVisible(true);

        this.emptyBoard();
        this.stats.restartGame();
        this.monsters.restartGame();

        this.nextRound(attacker, defender);
    }

    // Αφαιρεί όλα τα στοιχεία απ΄το ταμπλό.
    private void emptyBoard(){
        for(int i = 0; i < 8; ++i)
            for(int j = 0; j < 10; ++j)
                boardSquares[i][j].setIcon(null);
    }

    public void displayBattlingMonsters(int[] attackerPosition, int[] defenderPosition, int enemyMonster){
        int dRow = defenderPosition[0], dCol = defenderPosition[1];
        int aRow = attackerPosition[0], aCol = attackerPosition[1];
        removeBorders();
        currentButtonsImages[dRow][dCol] = opponentMonsters[enemyMonster];
        resizeImage(dRow, dCol);
        boardSquares[dRow][dCol].setBorder(BorderFactory.createLineBorder(Color.RED, buttonWidth / 10));
        boardSquares[aRow][aCol].setBorder(BorderFactory.createLineBorder(Color.RED, buttonWidth / 10));
        // TODO: I think upon resizing it gets green, due to resizal.
        borderedSquares.add(boardSquares[dRow][dCol]);
        borderedSquares.add(boardSquares[aRow][aCol]);

        repaint();
    }

    public void test(int[][] board, byte turn){
        BufferedImage[] pictures = (turn == 1) ? lightMonstersPictures : voidMonstersPictures;
        userMonsters = (turn == 1) ? lightMonstersPictures : voidMonstersPictures;
        opponentMonsters = (turn == 1) ? voidMonstersPictures : lightMonstersPictures;

        BufferedImage image;
        this.removeBorders();
        this.buttonHeight = boardSquares[0][0].getHeight();
        this.buttonWidth = boardSquares[0][0].getWidth();
        for(int i = 0; i < 8; ++i) {
            for (int j = 0; j < 10; ++j) {
                if (board[i][j] >= 0) {
                    if (board[i][j] == Integer.MAX_VALUE) image = pictures[11];
                    else image = pictures[board[i][j]];
                    boardSquares[i][j].setIcon(newIcon(image));
                    currentButtonsImages[i][j] = image;
                }
            }
        }
    }

    public void killMonster(int[] position){
        int row = position[0], col = position[1];
        boardSquares[row][col].setIcon(null);
        currentButtonsImages[row][col] = null;
        removeBorders();
    }

    public void killBothMonsters(int[] fPosition, int[] sPosition){
        killMonster(fPosition); killMonster(sPosition);
    }

    public void concealMonster(int[] conceal) {
        int row = conceal[0], col = conceal[1];
        currentButtonsImages[row][col] = userMonsters[12];
        boardSquares[row][col].setIcon(newIcon(userMonsters[12]));
        removeBorders();
    }

    // Για scaling.
    private class Resize implements ComponentListener{

        // Εάν αλλάξει το μέγεθος του παραθύρου του παιχνιδιού, άλλαξε τα μεγέθη των πανελς.
        @Override
        public void componentResized(ComponentEvent e) {
            // TODO: Instead of creating an executor always, just have one ready.
            // TODO: Can add try-catch if not bored.Also try to play with executor, so we don't have to always initialise.
            ExecutorService executor = Executors.newFixedThreadPool(2);
            executor.submit(this::resizePanels);
            executor.submit(this::resizeRoundPanel);
            executor.shutdown();

            resizeImagesOnButtons();
            repaint();
            setVisible(true);
        }

        @Override
        public void componentMoved(ComponentEvent e) {}

        @Override
        public void componentShown(ComponentEvent e) {}

        @Override
        public void componentHidden(ComponentEvent e) {}

        // Μεγέθυνε τα κουμπιά αναλογικά με το μέγεθος του παραθύρου.
        private void resizePanels(){
            int width = getWidth() * 2/3;
            int height = getHeight() - 35;

            squares.setPreferredSize(new Dimension(width, height));
            roundPanel.setPreferredSize(new Dimension(getWidth() / 3, height));

            buttonWidth = width / 10;
            buttonHeight = height / 8;
        }

        // Μεγεθύνει τις εικόνες, ώστε να χωράνε τέλεια στα κουμπιά.
        private void resizeImagesOnButtons(){
            // Εάν είναι bordered κάποιο κουμπί, πραγματοποίησε κατάλληλο scale.
            if(borderedSquares.size() > 0)
                for(JButton borderedButton : borderedSquares)
                    borderedButton.setBorder(BorderFactory.createLineBorder(Color.GREEN, buttonWidth / 10));

            IntStream.range(0, boardSquares.length)
                    .parallel()
                    .forEach(i -> IntStream.range(0, boardSquares[i].length).
                                filter(j -> currentButtonsImages[i][j] != null).
                                forEach(j -> resizeImage(i, j))
                            );
            /*
            borderedSquares.forEach(
                    button -> button.setBorder(BorderFactory.createLineBorder(Color.GREEN, buttonWidth / 10))
            );*/
        }

        private void resizeRoundPanel(){
            Dimension store = roundPanel.getPreferredSize();
            int height = store.height / 4;
            int width = store.width;
            monsters.scalePanel(width, 2 * height);
            stats.scalePanels(width, height);
            rules.scalePanel(width, height);
        }
    }
}
