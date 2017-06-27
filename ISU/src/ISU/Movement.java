package ISU;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
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
//import static ISU.Calculations.trigAngle;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.geom.Ellipse2D;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;

class Movement extends JPanel implements ActionListener, MouseWheelListener, MouseListener, MouseMotionListener {

    int delay = 10; //10 Milliseconds (1/100 seconds)
    int tDelay = 1000 / delay;
    Timer timer;
    Timer timer2;
    Timer mmTimer;
    int accuracy = 1000;
    long pRadius = 1 * 600000, pScale = pRadius * 10000 * 2;
//    double c.xPos = 0;                        //used to be int/long
//    double c.yPos = pRadius * accuracy;            //used to be int/long
//    double c.angle = 0;
    long pXpos = 0, pYpos = 0;
//    double c.pAHL = 70000;        //This is the atmospheric height limit where there would be no more atmosphere 
    double backColour;

    double[][] stars = new double[50][3];
//    double c.altitudeToPlanetCenter;
//    double rXSpeed, rYSpeed;
    boolean isPaused = false;
    boolean blowUp = false;
    int blowUpCount = 0;
    boolean showMiniMap;
    boolean isMiniMapUp;
    boolean timeWarpOn;
    int timeWarpSpeed = 1;

    double[] controlPassThrough = new double[10];
    int[][] mouseDrag = new int[3][3];
    int[] pPosMM;

//    boolean startBlowUp = false;
    Calculations c = new Calculations();
    PauseMenu pm = new PauseMenu();
//    double[] passThroughI = new double[10];
//    double[] passThroughF = new double[6];

    Random rand = new Random(); //Random number generator
    BufferedImage rPic = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
    BufferedImage e1 = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
    BufferedImage e2 = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
    BufferedImage e3 = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
    BufferedImage e4 = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
    BufferedImage e5 = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
    BufferedImage arrow = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
    BufferedImage arrow2 = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
    BufferedImage throttleAngle = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
    BufferedImage angles = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
    BufferedImage miniShip = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
    BufferedImage stable = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
    BufferedImage prograde = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
    BufferedImage retrograde = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
    BufferedImage rIn = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
    BufferedImage rOut = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
    BufferedImage warp = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
    BufferedImage aScale = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
    BufferedImage aPointer = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
    BufferedImage fScale = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
    double zScale = 0.3;
    private javax.swing.JTextField altitude;
    private javax.swing.JTextField speedometer;

    /**
     * Initializes the game and game components
     */
    public Movement() {

        try {

            rPic = ImageIO.read(new File("Rocket.png"));
            e1 = ImageIO.read(new File("e1.png"));
            e2 = ImageIO.read(new File("e2.png"));
            e3 = ImageIO.read(new File("e3.png"));
            e4 = ImageIO.read(new File("e4.png"));
            e5 = ImageIO.read(new File("e5.png"));
            arrow = ImageIO.read(new File("Arrow.png"));
            arrow2 = ImageIO.read(new File("Arrow2.png"));
            throttleAngle = ImageIO.read(new File("ThrottleArrow.png"));
            angles = ImageIO.read(new File("Angles.png"));
            miniShip = ImageIO.read(new File("MiniShip.png"));
            stable = ImageIO.read(new File("stable.png"));
            prograde = ImageIO.read(new File("prograde.png"));
            retrograde = ImageIO.read(new File("retrograde.png"));
            rIn = ImageIO.read(new File("rIn.png"));
            rOut = ImageIO.read(new File("rOut.png"));
            warp = ImageIO.read(new File("warp.png"));
            aScale = ImageIO.read(new File("aScale.png"));
            aPointer = ImageIO.read(new File("aPointer.png"));
            fScale = ImageIO.read(new File("fScale.png"));

        } catch (IOException e) {
            System.out.println(e);
        }

        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        int setX = gd.getDisplayMode().getWidth() - 700;
        int setY = gd.getDisplayMode().getHeight() - 100;

        addKeyListener(new TAdapter());
        setFocusable(true);
        addMouseWheelListener(this);

        altitude = new javax.swing.JTextField(20);
        altitude.setEditable(false);
        altitude.setFont(new java.awt.Font("Impact", 1, 18)); // NOI18N
        altitude.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        speedometer = new javax.swing.JTextField(10);
        speedometer.setEditable(false);
        speedometer.setFont(new java.awt.Font("Impact", 1, 11));
        speedometer.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        speedometer.setText("Speed: 0");

        for (int i = 0; i < stars.length; i++) {
            stars[i][0] = rand.nextInt(gd.getDisplayMode().getWidth() - 700) * 1;
            stars[i][1] = rand.nextInt(gd.getDisplayMode().getHeight() - 100) * 1;
            stars[i][2] = (rand.nextInt(4) + 2);
        }

        pm.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        pm.addKeyListener(new TAdapter());
        pm.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                isPaused = false;
                timer.start();
            }
        });
        timer = new Timer(10, this);
        timer.start();

    }

    @Override
    public void actionPerformed(ActionEvent e) {

        pm.setLocation(getWidth() / 2, getHeight() / 2);

        altitude.setLocation(getWidth() / 2 - 142, 4);
        speedometer.setLocation(getWidth() / 2 - 46, getHeight() - 200 - 15);

        if (showMiniMap == true && isMiniMapUp == false) { //Before the minimap opens, run these commands
            timer.stop();   //Stop running timer
            pPosMM = new int[]{getWidth() / 2, getHeight() / 2};    //Initialize planet position
            mmTimer = new Timer(100, this);
            mmTimer.start();    //Switch to less-intensive timer
            isMiniMapUp = true; //Sets this variable to check later 
            remove(altitude);   //Removes temporarily
            remove(speedometer);
        } else if (showMiniMap == false && isMiniMapUp == true) { //When closing minimap, start the timer again
            mmTimer.stop();
            isMiniMapUp = false;
            timer.start();
//            System.out.println("Adding Spedometer");
            add(altitude);
            add(speedometer);

        }

        if (blowUp == false && showMiniMap == false) {

            for (int i = 0; i < timeWarpSpeed; i++) {

                c.positionUpdate(controlPassThrough);
                if (c.altitudeToPlanetCenter < c.pAHL) {
                    timeWarpSpeed = 1;
                    timeWarpOn = false;
                    break;
                }
            }
            add(altitude);
            add(speedometer);
        }

        if (c.startBlowUp == true) {
            remove(altitude);   //Removes temporarily
            remove(speedometer);
            timer.stop();

            if (blowUp == false) {  //Only if it hasn't blown up yet
                timer2 = new Timer(300, this);
                timer2.start();
                blowUp = true;
            }
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

        if (blowUp == false && showMiniMap == false) {
            dispScenery(g);
            AffineTransform at = new AffineTransform();
            at.translate(getWidth() / 2 - rPic.getWidth() * zScale / 2, getHeight() / 2 - rPic.getHeight() * zScale / 2);  //Don't touch. Puts the rocket at the center
            at.scale(zScale, zScale);
            at.rotate(Math.toRadians(c.angle), rPic.getWidth() / 2, rPic.getHeight() / 2);
            g2d.drawImage(rPic, at, this);
            dispControls(g);
            altitude.setText("Altitude: " + (int) (c.altitudeToPlanetCenter - pRadius) + " m");
            speedometer.setText((int) ((Math.sqrt(Math.pow(c.rYSpeed, 2) + Math.pow(c.rXSpeed, 2)))) + " m/s");
        } else if (blowUp == true) {
            dispScenery(g);
            if (blowUpCount < 15) {
                dispExplosion(g);
                blowUpCount++;
            } else {
                timer2.stop();
            }
        } else if (showMiniMap == true) {
            addMouseMotionListener(this);
            addMouseListener(this);
            dispMiniMap(g);
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
        if (c.altitudeToPlanetCenter > (c.pAHL)) {
            backColour = 0;
        } else {
            backColour = .7 - ((c.altitudeToPlanetCenter - pRadius) / (c.pAHL - pRadius)) * 0.7;
        }
        g2d.setPaint(Color.getHSBColor((float) .55, (float) .71, (float) backColour));  //Last number for darkness 
        g2d.fillRect(0, 0, getWidth(), getHeight());

        //########Stars
        g.setColor(Color.white);
        for (int i = 0; i < stars.length; i++) {
            if (blowUp == false) {
                stars[i][0] += c.rYSpeed / 1000;
                stars[i][1] -= c.rXSpeed / 1000;

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
        pYpos = (long) (getHeight() / 2 + ((rPic.getHeight() / 2) + 1 * (c.altitudeToPlanetCenter - pRadius) * accuracy) * zScale);           //#########################FIX ME

        g.setColor(Color.green);
        Ellipse2D.Double shape = new Ellipse2D.Double(pXpos, pYpos, pScale, pScale);
        g2d.draw(shape);
        g2d.fill(shape);

    }

    private void dispControls(Graphics g) {
//        JTextField altitude = new JTextField(15);

        altitude.setText("Altitude: " + (int) c.altitudeToPlanetCenter);

        Graphics2D g2d = (Graphics2D) g;

        //Draws the blue border (really a full circle but that doesn't matter)
        g.setColor(Color.blue);
        g.fillOval((getWidth()) / 2 - 100, getHeight() - 200, 200, 200);

        //Draws the smaller White circle
        g.setColor(Color.white);
        g.fillOval((getWidth()) / 2 - 100 + 5, getHeight() - 200 + 5, 190, 190);

        //Draws the c.angles
        g.drawImage(angles, getWidth() / 2 - 126, getHeight() - 195, 222, 190, this);

        //Draws throttle meter
        AffineTransform at1 = new AffineTransform();
        at1.translate(getWidth() / 2 - 125, getHeight() - 195);  //Don't touch, draws the spinning arrow
        at1.scale(0.317, 0.31766);
        at1.rotate(Math.toRadians(c.sThrottle * 60), 400, 300); //Don't touch this, this spins the other arrow 
        g2d.drawImage(throttleAngle, at1, this);

        //Draws the SAS heading logos
        switch (c.selectHeading) {
            case 1:
                g.drawImage(stable, getWidth() / 2 - 10, getHeight() - 195 / 2 - 50, 20, 20, this);
                break;
            case 2:
                g.drawImage(prograde, getWidth() / 2 - 10, getHeight() - 195 / 2 - 50, 20, 20, this);
                break;
            case 3:
                g.drawImage(rIn, getWidth() / 2 - 10, getHeight() - 195 / 2 - 50, 20, 20, this);
                break;
            case 4:
                g.drawImage(retrograde, getWidth() / 2 - 10, getHeight() - 195 / 2 - 50, 20, 20, this);
                break;
            case 5:
                g.drawImage(rOut, getWidth() / 2 - 10, getHeight() - 195 / 2 - 50, 20, 20, this);
                break;
        }

        //Displays time warping triangles
        if (timeWarpSpeed >= 1) {
            for (int i = 1; Math.pow(2, i) <= Math.pow(timeWarpSpeed, 2); i++) {
                g.drawImage(warp, (int) (15 + i * 20), 15, 15, 15, this);
            }
        }

        //Draws heading arrow
        AffineTransform at = new AffineTransform();
        at.translate(((getWidth() / 2) - 128 + 1), (getHeight() - 256 + 57.5 / 2) * 1);  //Don't touch, draws the spinning arrow
        at.rotate(Math.toRadians(c.angle), arrow.getWidth() / 2, arrow.getHeight() / 2); //Don't touch this, this spins the arrow 
        g2d.drawImage(arrow, at, this);
        //Draws travel arrow
        AffineTransform at2 = new AffineTransform();
        at2.translate(((getWidth() / 2) - arrow2.getWidth() / 2), (getHeight() - arrow2.getHeight()) + 57.5 / 2);  //Don't touch, draws the other spinning arrow
        at2.rotate(Math.toRadians(c.travelAngle(c.rXSpeed, c.rYSpeed)), arrow2.getWidth() / 2, arrow2.getHeight() / 2); //Don't touch this, this spins the other arrow 
        g2d.drawImage(arrow2, at2, this);

        //Displays altitude Scale
        g.drawImage(aScale, (getWidth() - 284) / 2, 0, this);
        if (((c.altitudeToPlanetCenter - pRadius) / (c.pAHL - pRadius)) > 1) {
            g.drawImage(aPointer, (int) ((getWidth() + 284 - 30) / 2 - 284), 61, this);
        } else {
            g.drawImage(aPointer, (int) ((getWidth() + 284 - 30) / 2 - 284 + 284 * ((((100.13 * Math.pow(Math.E, -(c.altitudeToPlanetCenter - c.pRadius) / 5600)) / (100.13))))), 61, this);
        }

        //Displays fuel gague
        g.drawImage(fScale, (getWidth() - 284), 0, this);
        if (c.rFuel < 0) {
            g.drawImage(aPointer, (int) ((getWidth() - 284 - 15)), 61, this);
        } else {
            g.drawImage(aPointer, (int) ((getWidth()) - 284 * ((c.initFuel - c.rFuel) / c.initFuel) - 15), 61, this);
        }

    }

    private void dispMiniMap(Graphics g) {
        g.setColor(Color.black);
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(Color.green);
        g.fillOval((int) (- 600 / 2 * zScale * 5 + (pPosMM[0])), (int) (- 600 / 2 * zScale * 5 + pPosMM[1]), (int) (600 * (zScale * 5)), (int) (600 * (zScale * 5)));
        g.drawImage(miniShip, (int) (- 600 / 2 * zScale * 5 + (pPosMM[0]) + 600 * (zScale * 5) / 2 - 15 + c.xPos / 2000000 * (zScale * 5)), (int) (- 600 / 2 * (zScale * 5) + pPosMM[1] + 600 * (zScale * 5) / 2 - 15 - c.yPos / 2000000 * (zScale * 5)), 30, 30, this);
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
    }

    @Override
    public void mousePressed(MouseEvent e) {
        mouseDrag[0][0] = e.getX();
        mouseDrag[0][1] = e.getY();
        mouseDrag[1][0] = e.getX();
        mouseDrag[1][1] = e.getY();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        mouseDrag[0][0] = mouseDrag[1][0];
        mouseDrag[0][1] = mouseDrag[1][1];
        mouseDrag[1][0] = e.getX();
        mouseDrag[1][1] = e.getY();
        mouseDrag[2][0] = mouseDrag[1][0] - mouseDrag[0][0];
        mouseDrag[2][1] = mouseDrag[1][1] - mouseDrag[0][1];
        pPosMM[0] += mouseDrag[2][0];
        pPosMM[1] += mouseDrag[2][1];
    }

    private class TAdapter extends KeyAdapter {

        @Override
        public void keyReleased(KeyEvent e) {
            int key = e.getKeyCode();

            if (key == KeyEvent.VK_W) {
//                System.out.println("Released Up");
                controlPassThrough[0] = 0;

            }

            if (key == KeyEvent.VK_S) {
//                System.out.println("Released Down");
                controlPassThrough[1] = 0;
            }
            if (key == KeyEvent.VK_A) {
//                System.out.println("Released Left");
                controlPassThrough[2] = 0;
            }

            if (key == KeyEvent.VK_D) {
//                System.out.println("Released Right");
                controlPassThrough[3] = 0;
            }

            if (key == KeyEvent.VK_Z) {
                controlPassThrough[0] = 0;
            }

            if (key == KeyEvent.VK_X) {
                controlPassThrough[1] = 0;
            }
            if (key == KeyEvent.VK_HOME) {
//                System.out.println("Right");
                controlPassThrough[7] = 0;
            }
        }

        @Override
        public void keyPressed(KeyEvent e) {
//            System.out.println("Key pressed code=" + e.getKeyCode() + ", char=" + e.getKeyChar());
            int key = e.getKeyCode();
            if (timeWarpOn == false) {

                if (key == KeyEvent.VK_W) {
//                System.out.println("Up");
                    controlPassThrough[0] = 1;

                }

                if (key == KeyEvent.VK_S) {
//                System.out.println("Down");
                    controlPassThrough[1] = 1;

                }
                if (key == KeyEvent.VK_A) {
//                System.out.println("Left");
                    controlPassThrough[2] = 1;
                }

                if (key == KeyEvent.VK_D) {
//                System.out.println("Right");
                    controlPassThrough[3] = 1;
                }

                if (key == KeyEvent.VK_HOME) {
//                System.out.println("Right");
                    controlPassThrough[7] = 10;
                }
                if (key == KeyEvent.VK_END) {
//                System.out.println("Right");
                    controlPassThrough[8] = 10;
                }

                if (key == KeyEvent.VK_Z) {
//                System.out.println("minThrottle");
                    controlPassThrough[0] = -1;
                }

                if (key == KeyEvent.VK_X) {
//                System.out.println("maxThrottle");
                    controlPassThrough[1] = 10;
                }

                if (key == KeyEvent.VK_1) { //Stable Heading
                    if (c.selectHeading != 1) {
                        c.selectHeading = 1;
                    } else {
                        c.selectHeading = 0;
                    }
                }
                if (key == KeyEvent.VK_2) {//Prograde
                    if (c.selectHeading != 2) {
                        c.selectHeading = 2;
                    } else {
                        c.selectHeading = 0;
                    }
                }
                if (key == KeyEvent.VK_3) {//Right
                    if (c.selectHeading != 3) {
                        c.selectHeading = 3;
                    } else {
                        c.selectHeading = 0;
                    }
                }
                if (key == KeyEvent.VK_4) {//Retrograde
                    if (c.selectHeading != 4) {
                        c.selectHeading = 4;
                    } else {
                        c.selectHeading = 0;
                    }
                }
                if (key == KeyEvent.VK_5) {//Left
                    if (c.selectHeading != 5) {
                        c.selectHeading = 5;
                    } else {
                        c.selectHeading = 0;
                    }
                }

                if (key == KeyEvent.VK_F3) {
//                System.out.println("Explode on demand!");
                    c.startBlowUp = true;
                }

            }

            if (key == KeyEvent.VK_ESCAPE) {

                if (isPaused == false) {
                    timer.stop();
                    isPaused = true;

                    pm.setVisible(true);

                } else {
                    isPaused = false;
                    timer.start();

                }

            }

            if (key == KeyEvent.VK_M) { //Intended for minimap menu

                if (showMiniMap == false) {
                    showMiniMap = true;
                } else {
                    showMiniMap = false;
                }
//                System.out.println("showMiniMap: " + showMiniMap);
            }

            if (key == KeyEvent.VK_PERIOD) {
                if (c.altitudeToPlanetCenter >= c.pAHL) {
                    timeWarpOn = true;
                    controlPassThrough[0] = 0;
                    controlPassThrough[1] = 0;
                    c.sThrottle = 0;
                    if (timeWarpSpeed < 256) {
                        timeWarpSpeed *= 2;
                        controlPassThrough[0] = 0;
                        c.selectHeading = 1;
                    } else {
                        timeWarpSpeed = 256;
                    }
                }

            }
            if (key == KeyEvent.VK_COMMA) {
                if (timeWarpSpeed > 1) {
                    timeWarpSpeed /= 2;
                } else {
//                    System.out.println("Time warp off!");
                    timeWarpSpeed = 1;
                    timeWarpOn = false;
                }
            }

        }

    }

    @Override
    public void mouseEntered(MouseEvent e) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mouseExited(MouseEvent e) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mouseClicked(MouseEvent e) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
