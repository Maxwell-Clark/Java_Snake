import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {

    static final int SCREEN_WIDTH = 600;
    static final int SCREEN_HEIGHT = 600;
    static final int UNIT_SIZE = 25;
    static int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT)/UNIT_SIZE;
    static final int DELAY = 50;
    int x[] = new int[GAME_UNITS];
    int y[] = new int[GAME_UNITS];
    int bodyParts = 6;
    int applesEaten;
    int appleX;
    int appleY;
    char direction = 'R';
    boolean running = false;
    boolean resetGame = false;
    Timer gameTimer;
    Timer gameOverTimer; // to be used to automatically restart
    Random random;


    GamePanel() {
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.black);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        startGame();
    }

    public void startGame() {
        levelManager();
        running = true;
        gameTimer = new Timer(DELAY, this);
        gameTimer.start();
    }

    public void paintComponent(Graphics graphic) {
        super.paintComponent(graphic);
        if(!resetGame) draw(graphic);
        else resetGame();
    }

    public void draw(Graphics graphic) {

        if(running) {
            if((applesEaten % 100) == 0) {
                graphic.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
            }
            else graphic.setColor(Color.green);

            graphic.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);

            for (int i = 0; i < bodyParts; i++) {
                if ((i % 2) == 0) {
                    graphic.setColor(Color.red);
                    graphic.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
                    graphic.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);

                } else {
                    graphic.setColor(new Color(random.nextInt(100), random.nextInt(255), random.nextInt(150)));
                    graphic.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                }
            }
            graphic.setColor(Color.red);
            graphic.setFont(new Font("Ink Free", Font.BOLD, 40));
            FontMetrics metrics = getFontMetrics(graphic.getFont());
            graphic.drawString("Score: " + applesEaten, (SCREEN_WIDTH - metrics.stringWidth("Score: " + applesEaten))/2, graphic.getFont().getSize());
        }
        else {
            gameOver(graphic);
        }

    }

    public void newApple() {
        levelManager();
        appleX = random.nextInt((int)SCREEN_WIDTH/UNIT_SIZE)* UNIT_SIZE;
        appleY = random.nextInt((int)SCREEN_HEIGHT/UNIT_SIZE)* UNIT_SIZE;
    }

    public void levelManager() {
        appleX = random.nextInt((int)SCREEN_WIDTH/UNIT_SIZE)* UNIT_SIZE;
        appleY = random.nextInt((int)SCREEN_HEIGHT/UNIT_SIZE)* UNIT_SIZE;
    }

    public void move() {
        for(int i = bodyParts; i>0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }

        switch(direction) {
            case 'U':
                y[0] = y[0] - UNIT_SIZE;
                break;
            case 'D':
                y[0] = y[0] + UNIT_SIZE;
                break;
            case 'L':
                x[0] = x[0] - UNIT_SIZE;
                break;
            case 'R':
                x[0] = x[0] + UNIT_SIZE;
        }
    }

    public void checkApple() {
        if((x[0] == appleX) && (y[0] == appleY)) {
            bodyParts++;
            applesEaten++;
            levelManager();
        }
    }

    public void checkCollisions() {
        //checks if the head hits the body
        for(int i=bodyParts; i>0;i--) {
            if((x[0] == x[i]) && (y[0] == y[i])) {
                running = false;
            }
        }
        //checks if the head hits the left border
        if(x[0] < 0) {
            running = false;
        }
        //checks if the head hits the right border
        if(x[0] > SCREEN_WIDTH) {
            running = false;
        }
        //checks if the head hits the top border
        if(y[0] < 0) {
            running = false;
        }
        if(y[0]> SCREEN_HEIGHT){
            running = false;
        }
        if(running == false) {
            gameTimer.stop();
        }
    }

    public void gameOver(Graphics graphic) {
        //Game Over Text
        graphic.setColor(Color.red);
        graphic.setFont(new Font("Ink Free", Font.BOLD, 75));
        FontMetrics gameOverMetrics = getFontMetrics(graphic.getFont());
        graphic.drawString("Game Over", (SCREEN_WIDTH - gameOverMetrics.stringWidth("Game Over"))/2, SCREEN_HEIGHT/2);
        //Reset Text
        graphic.setColor(Color.green);
        graphic.setFont(new Font("Ink Free", Font.BOLD, 30));
        FontMetrics restartMetrics = getFontMetrics(graphic.getFont());
        graphic.drawString("Press R to Restart", (SCREEN_WIDTH - restartMetrics.stringWidth("Pres R to Restart"))/2, SCREEN_HEIGHT/3);
        //Score Text
        graphic.setColor(Color.red);
        graphic.setFont(new Font("Ink Free", Font.BOLD, 40));
        FontMetrics scoreMetrics = getFontMetrics(graphic.getFont());
        graphic.drawString("Score: " + applesEaten, (SCREEN_WIDTH - scoreMetrics.stringWidth("Score: " + applesEaten))/2, graphic.getFont().getSize());

    }

    public void resetGame() {
        running = true;
        applesEaten = 0;
        bodyParts = 6;
        x = new int[GAME_UNITS];
        y = new int[GAME_UNITS];
        direction = 'R';
        startGame();
        resetGame = false;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(running) {
            move();
            checkApple();
            checkCollisions();
        }
        repaint();
    }

    public class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent event) {
            switch(event.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    if(direction != 'R') {
                        direction = 'L';
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    if(direction != 'L') {
                        direction = 'R';
                    }
                    break;
                case KeyEvent.VK_UP:
                    if(direction != 'D') {
                        direction = 'U';
                    }
                    break;
                case KeyEvent.VK_DOWN:
                    if(direction != 'U') {
                        direction = 'D';
                    }
                    break;
            }
            if(!running && (event.getKeyCode() == KeyEvent.VK_R)) {
                resetGame();
            }
        }
    }
}
