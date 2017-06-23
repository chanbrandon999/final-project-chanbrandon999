package ISU;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.Timer;
import static ISU.Calculations.trigAngle;
import java.awt.AlphaComposite;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.image.ImageFilter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import javax.swing.JTextField;

class Movement extends JPanel implements ActionListener, MouseWheelListener {

    int delay = 10; //10 Milliseconds (1/100 seconds)
    int tDelay = 1000 / delay;
    Timer timer;
    Timer timer2;

    int accuracy = 1000;
    long pRadius = 1 * 600000, pScale = pRadius * 10000 * 2;
    double xPos = 0;                        //used to be int/long
    double yPos = pRadius * accuracy;            //used to be int/long
    double angle = 0;
    long pXpos = 0, pYpos = 0;
    double pAHL = 70000;        //This is the atmospheric height limit where there would be no more atmosphere 
    double backColour;

    double[][] stars = new double[50][3];
    double altitudeToPlanetCenter;
    double rXSpeed, rYSpeed;
    boolean isPaused = false;
    boolean blowUp = false;
    int blowUpCount = 0;

    Calculations c = new Calculations();
    double[] passThroughI = new double[10];
    double[] passThroughF = new double[6];

    Random rand = new Random(); //Random number generator
    BufferedImage rPic = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
    BufferedImage e1 = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
    BufferedImage e2 = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
    BufferedImage e3 = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
    BufferedImage e4 = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
    BufferedImage e5 = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
    BufferedImage arrow = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
    BufferedImage arrow2 = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
    BufferedImage angles = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
//    BufferedImage starBkg = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
    double zScale = 0.3;
    private javax.swing.JTextField altitude;

    /**
     * Initializes the game and game components
     *
     * @param width The width of the window
     * @param height the Height of the window
     */
    public Movement(int width, int height) {

        try {

            rPic = ImageIO.read(new File("Rocket.png"));
//            starBkg = ImageIO.read(new File("Stars.jpeg"));
            e1 = ImageIO.read(new File("e1.png"));
            e2 = ImageIO.read(new File("e2.png"));
            e3 = ImageIO.read(new File("e3.png"));
            e4 = ImageIO.read(new File("e4.png"));
            e5 = ImageIO.read(new File("e5.png"));
            arrow = ImageIO.read(new File("Arrow.png"));
            arrow2 = ImageIO.read(new File("Arrow2.png"));
            angles = ImageIO.read(new File("Angles.png"));

        } catch (IOException e) {
            System.out.println(e);
        }

        addKeyListener(new TAdapter());
        setFocusable(true);
        addMouseWheelListener(this);

        altitude = new javax.swing.JTextField(20);
        
        altitude.setEditable(false);
        altitude.setFont(new java.awt.Font("Impact", 1, 18)); // NOI18N
        altitude.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        add(altitude);

        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        int setX = gd.getDisplayMode().getWidth() - 700;
        int setY = gd.getDisplayMode().getHeight() - 100;


        for (int i = 0; i < stars.length; i++) {
            stars[i][0] = rand.nextInt(gd.getDisplayMode().getWidth() - 700) * 1;
            stars[i][1] = rand.nextInt(gd.getDisplayMode().getHeight() - 100) * 1;
            stars[i][2] = (rand.nextInt(4) + 2);
        }

//        setBackground(Color.BLACK);
        timer = new Timer(delay, this);
        //the "this" is the actionPerformed method
        timer.start();

//        if (blowUp == true) {
//            System.out.println("Switching timers");
//            timer2 = new Timer(delay, this);
//            timer2.start();
//            blowUp = true;
//
//        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (blowUp == false) {
            passThroughF = c.positionUpdate(passThroughI);
        }
        if (passThroughF[1] == -1.192837465 && passThroughF[0] == -1.9182736465) {
            timer.stop();

            if (blowUp == false) {  //Only use if it hasn't blown up yet
//                System.out.println("Switching timers");
                timer2 = new Timer(300, this);
                timer2.start();
                blowUp = true;
            }

        } else {
            xPos = passThroughF[0];
            yPos = passThroughF[1];
            angle = passThroughF[2];
            altitudeToPlanetCenter = passThroughF[3];
            rXSpeed = passThroughF[4];
            rYSpeed = passThroughF[5];
        }
        repaint();
    }

    /**
     * Paints all the elements onto the screen
     *
     * @param g
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        pScale = (long) (pRadius * 2 * 10000 * zScale);

        dispScenery(g);

        if (blowUp == false) {
            AffineTransform at = new AffineTransform();
            at.translate(getWidth() / 2 - rPic.getWidth() * zScale / 2, getHeight() / 2 - rPic.getHeight() * zScale / 2);  //Don't touch. Puts the rocket at the center
            at.scale(zScale, zScale);
            at.rotate(Math.toRadians(angle), rPic.getWidth() / 2, rPic.getHeight() / 2);
            g2d.drawImage(rPic, at, this);
            dispControls(g, altitudeToPlanetCenter);
            altitude.setText("Altitude: " + (int) altitudeToPlanetCenter);
        } else {
//            System.out.println("Display Image");
//            g2d.drawImage(rPic, 200, 200, 200, 200, this);
            if (blowUpCount < 15) {
                dispExplosion(g);
                blowUpCount++;
            } else {
                timer2.stop();
            }

        }

    }

    /**
     * This method displays 2 or 3 explosion animations randomly around the
     * rocket.
     *
     * @param g
     */
    private void dispExplosion(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        int eSz = (int) (550 * zScale);

        for (int i = 0; i < rand.nextInt(2) + 2; i++) {
            switch (rand.nextInt(5) + 1) {
                case 1:
                    g2d.drawImage(e1, getExpPos('w', eSz), getExpPos('h', eSz), eSz, eSz, this);
                    break;
                case 2:
                    g2d.drawImage(e2, getExpPos('w', eSz), getExpPos('h', eSz), eSz, eSz, this);
                    break;
                case 3:
                    g2d.drawImage(e3, getExpPos('w', eSz), getExpPos('h', eSz), eSz, eSz, this);
                    break;
                case 4:
                    g2d.drawImage(e4, getExpPos('w', eSz), getExpPos('h', eSz), eSz, eSz, this);
                    break;
                case 5:
                    g2d.drawImage(e5, getExpPos('w', eSz), getExpPos('h', eSz), eSz, eSz, this);
                    break;
            }

        }
    }

    /**
     * Returns positions around the center where the rocket would blow up
     *
     * @param x The length or width of the explosion coordinate
     * @return Random coordinates around the center of the screen (where the
     * rocket would be)
     */
    private int getExpPos(char x, int eSz) {
        if (x == 'w') {

            return (int) (-(eSz / 2) + (rand.nextInt(100) - 50) * zScale + getWidth() / 2);
        } else if (x == 'h') {
            return (int) (-(eSz / 2) + (rand.nextInt(100) - 50) * zScale + getHeight() / 2);
        }
        return 0;

    }

    /**
     * Displays the scenery needed in the rocket program. Displays the
     * atmosphere, the stars and the planet.
     *
     * @param g The graphics object to help display the components
     */
    private void dispScenery(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        //########Background atmosphere/space
        if (altitudeToPlanetCenter > (pRadius + pAHL)) {
            backColour = 0;
        } else {
            backColour = .7 - ((altitudeToPlanetCenter - pRadius) / pAHL) * 0.7;
        }
        g2d.setPaint(Color.getHSBColor((float) .55, (float) .71, (float) backColour));  //Last number for darkness 
        g2d.fillRect(0, 0, getWidth(), getHeight());

        //########Stars
        g.setColor(Color.white);
        for (int i = 0; i < stars.length; i++) {
            if (blowUp == false) {
                stars[i][0] += rYSpeed / 1000;
                stars[i][1] -= rXSpeed / 1000;

                if (stars[i][0] > getHeight()) {
                    stars[i][0] -= getHeight();
                } else if (stars[i][0] < 0) {
                    stars[i][0] += getHeight();
                }

                if (stars[i][1] > getWidth()) {
                    stars[i][1] -= getWidth();
                } else if (stars[i][1] < 0) {
                    stars[i][1] += getWidth();
                }
            }

            g.fillOval((int) stars[i][1], (int) stars[i][0], (int) stars[i][2], (int) stars[i][2]);
        }

        //###################Planet###################
        pXpos = (getWidth() - pScale) / 2;
        pYpos = (long) (getHeight() / 2 + ((rPic.getHeight() / 2) + 1 * (altitudeToPlanetCenter - pRadius) * accuracy) * zScale);           //#########################FIX ME

        g.setColor(Color.green);
        Ellipse2D.Double shape = new Ellipse2D.Double(pXpos, pYpos, pScale, pScale);
        g2d.draw(shape);
        g2d.fill(shape);

    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (zScale >= 0.00005 && zScale <= 1) {
            if (e.getWheelRotation() > 0) {
                zScale -= 0.25 * zScale;
            } else if (e.getWheelRotation() < 0) {
                zScale += 0.25 * zScale;
            }
        }

        if (zScale < 0.00006) {
            zScale = 0.00005;
        } else if (zScale > 1.00001) {
            zScale = 1;
        }
//        System.out.println("zScale: " + zScale);
    }

    private void dispControls(Graphics g, double altitudeToPlanetCenter) {
//        JTextField altitude = new JTextField(15);
        
        altitude.setText("Altitude: " + (int) altitudeToPlanetCenter);
        

        Graphics2D g2d = (Graphics2D) g;
        g.setColor(Color.blue);
        g.fillOval((getWidth()) / 2 - 100, getHeight() - 200, 200, 200);
        g.setColor(Color.white);
        g.fillOval((getWidth()) / 2 - 100 + 5, getHeight() - 200 + 5, 190, 190);
        g.drawImage(angles, getWidth() / 2 - 100 + 5, getHeight() - 200 + 5, 190, 190, this);

        AffineTransform at = new AffineTransform();
        at.translate(((getWidth() / 2) - 128 + 1), (getHeight() - 256 + 57.5 / 2) * 1);  //Don't touch, draws the spinning arrow
        at.rotate(Math.toRadians(angle), arrow.getWidth() / 2, arrow.getHeight() / 2); //Don't touch this, this spins the arrow 
        g2d.drawImage(arrow, at, this);
        AffineTransform at2 = new AffineTransform();
        at2.translate(((getWidth() / 2) - arrow2.getWidth() / 2), (getHeight() - arrow2.getHeight()) + 57.5 / 2);  //Don't touch, draws the other spinning arrow
        at2.rotate(Math.toRadians(c.travelAngle(rXSpeed, rYSpeed)), arrow2.getWidth() / 2, arrow2.getHeight() / 2); //Don't touch this, this spins the other arrow 
        g2d.drawImage(arrow2, at2, this);
    }

    private class TAdapter extends KeyAdapter {

        @Override
        public void keyReleased(KeyEvent e) {
            int key = e.getKeyCode();

            if (key == KeyEvent.VK_W) {
//                System.out.println("Released Up");
                passThroughI[4] = 0;
            }

            if (key == KeyEvent.VK_S) {
//                System.out.println("Released Down");
                passThroughI[5] = 0;
            }
            if (key == KeyEvent.VK_A) {
//                System.out.println("Released Left");
                passThroughI[6] = 0;
            }

            if (key == KeyEvent.VK_D) {
//                System.out.println("Released Right");
                passThroughI[7] = 0;
            }

            if (key == KeyEvent.VK_Z) {
                passThroughI[4] = 0;
            }

            if (key == KeyEvent.VK_X) {
                passThroughI[5] = 0;
            }
        }

        @Override
        public void keyPressed(KeyEvent e) {
//            System.out.println("Key pressed code=" + e.getKeyCode() + ", char=" + e.getKeyChar());
            int key = e.getKeyCode();
//            c.controls(key);

            if (key == KeyEvent.VK_W) {
//                System.out.println("Up");
                passThroughI[4] = 1;
            }

            if (key == KeyEvent.VK_S) {
//                System.out.println("Down");
                passThroughI[5] = 1;
            }
            if (key == KeyEvent.VK_A) {
//                System.out.println("Left");
                passThroughI[6] = 1;
            }

            if (key == KeyEvent.VK_D) {
//                System.out.println("Right");
                passThroughI[7] = 1;
            }

            if (key == KeyEvent.VK_HOME) {
//                System.out.println("Right");
                passThroughI[7] = 10;
            }

            if (key == KeyEvent.VK_Z) {
//                System.out.println("minThrottle");
                passThroughI[4] = -1;
            }

            if (key == KeyEvent.VK_X) {
//                System.out.println("maxThrottle");
                passThroughI[5] = 10;
            }

            if (key == KeyEvent.VK_ESCAPE) {

                if (isPaused == false) {
                    timer.stop();
                    isPaused = true;
                } else {
                    timer.start();
                    isPaused = false;
                }

            }
            if (key == KeyEvent.VK_1) { //Stable Heading
                if (passThroughI[8] != 1) {
                    passThroughI[8] = 1;
                } else {
                    passThroughI[8] = 0;
                }
            }
            if (key == KeyEvent.VK_2) {//Prograde
                if (passThroughI[8] != 2) {
                    passThroughI[8] = 2;
                } else {
                    passThroughI[8] = 0;
                }
            }
            if (key == KeyEvent.VK_3) {//Right
                if (passThroughI[8] != 3) {
                    passThroughI[8] = 3;
                } else {
                    passThroughI[8] = 0;
                }
            }
            if (key == KeyEvent.VK_4) {//Retrograde
                if (passThroughI[8] != 4) {
                    passThroughI[8] = 4;
                } else {
                    passThroughI[8] = 0;
                }
            }
            if (key == KeyEvent.VK_5) {//Left
                if (passThroughI[8] != 5) {
                    passThroughI[8] = 5;
                } else {
                    passThroughI[8] = 0;
                }
            }

            if (key == KeyEvent.VK_F3) {
//                System.out.println("Explode on demand!");
                passThroughI[9] = 0.8103;
            }

            if (key == KeyEvent.VK_M) {//Left

//                if (passThroughI[8] != 5) {
//                    passThroughI[8] = 5;
//                } else {
//                    passThroughI[8] = 0;
//                }
            }

        }

    }

}
