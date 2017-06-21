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
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.image.ImageFilter;
import java.util.ArrayList;
import java.util.List;

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

    Calculations c = new Calculations();
    double[] passThroughI = new double[8];
    double[] passThroughF = new double[6];

    Random rand = new Random(); //Random number generator
    BufferedImage rPic = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
    BufferedImage explosion1 = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
    BufferedImage explosion2 = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
    BufferedImage explosion3 = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
    BufferedImage explosion4 = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
    BufferedImage explosion5 = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
    BufferedImage starBkg = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
    double zScale = 0.3;

    public Movement(int width, int height) {

        try {

            rPic = ImageIO.read(new File("Rocket.png"));
            starBkg = ImageIO.read(new File("Stars.jpeg"));
            explosion1 = ImageIO.read(new File("Explosion.gif"));
            explosion2 = ImageIO.read(new File("e2.png"));
            explosion3 = ImageIO.read(new File("e3.png"));
            explosion4 = ImageIO.read(new File("e4.jpg"));
            explosion5 = ImageIO.read(new File("e5.jpg"));

        } catch (IOException e) {
            System.out.println(e);
        }

        addKeyListener(new TAdapter());
        setFocusable(true);
        addMouseWheelListener(this);

        for (int i = 0; i < stars.length; i++) {
            stars[i][0] = rand.nextDouble() * 800;
            stars[i][1] = rand.nextDouble() * 800;
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

            if (blowUp == false) {
                System.out.println("Switching timers");
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
        } else {
            System.out.println("Display Image");
            g2d.drawImage(rPic, 200, 200, 200, 200, this);
        }
//        dispExplosion(g);

    }

    private int getExpPos(char x) {     //Fix explosion positioning
        if (x == 'w') {
//            return 0;
            return ((-200) + (rand.nextInt(100) - 50) + 700 / 2);
//            return ((-200) + (rand.nextInt(100) - 50) + getWidth() / 2);
        } else if (x == 'h') {
//            return ((-200) + (rand.nextInt(100) - 50) + getHeight() / 2);
            return ((-200) + (rand.nextInt(100) - 50) + 700 / 2);
        }
        return 0;

    }

    private void dispExplosion(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        for (int i = 0; i < rand.nextInt(3) + 1; i++) {
            switch (rand.nextInt(5) + 1) {
                case 1:
                    g2d.drawImage(explosion1, 200, 200, 200, 200, this);
//                        g2d.drawImage(e1, i, i, this)
                    break;
                case 2:
                    g2d.drawImage(explosion2, getExpPos('w'), getExpPos('h'), 200, 200, this);
                    break;
                case 3:
                    g2d.drawImage(explosion3, getExpPos('w'), getExpPos('h'), 200, 200, this);
                    break;
                case 4:
                    g2d.drawImage(explosion4, getExpPos('w'), getExpPos('h'), 200, 200, this);
                    break;
                case 5:
                    g2d.drawImage(explosion5, getExpPos('w'), getExpPos('h'), 200, 200, this);
                    break;
            }

        }
    }

    private void dispScenery(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        if (altitudeToPlanetCenter > (pRadius + pAHL)) {
            backColour = 0;
        } else {
            backColour = .7 - ((altitudeToPlanetCenter - pRadius) / pAHL) * 0.7;
        }

        //########Background atmosphere/space
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
    public void mouseWheelMoved(MouseWheelEvent e
    ) {
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
        System.out.println("zScale: " + zScale);
//        System.out.println((getHeight() / 2 + ((rPic.getHeight() / 2) + (altitudeToPlanetCenter - pRadius) * accuracy) * zScale));
//        System.out.println("3: " + ((altitudeToPlanetCenter - pRadius) * accuracy) * zScale);
//        System.out.println("4: " + (int) pXpos + ", " + (int) pYpos + ", " + (int) Math.abs(pScale) + ", " + (int) Math.abs(pScale));
    }

    private class TAdapter extends KeyAdapter {

        @Override
        public void keyReleased(KeyEvent e) {
            int key = e.getKeyCode();

            if (key == KeyEvent.VK_W) {
                System.out.println("Released Up");
                passThroughI[4] = 0;
            }

            if (key == KeyEvent.VK_S) {
                System.out.println("Released Down");
                passThroughI[5] = 0;
            }
            if (key == KeyEvent.VK_A) {
                System.out.println("Released Left");
                passThroughI[6] = 0;
            }

            if (key == KeyEvent.VK_D) {
                System.out.println("Released Right");
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
//                System.out.println("Up");
                passThroughI[4] = -1;
            }

            if (key == KeyEvent.VK_X) {
//                System.out.println("Down");
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

        }

    }

//##########################################GARBAGE CODE#########################################
//    @Override
//    public void keyPressed(KeyEvent e) {
//        System.out.println("Key pressed code=" + e.getKeyCode() + ", char=" + e.getKeyChar());
//        int key = e.getKeyCode();
//
//        if (key == KeyEvent.VK_LEFT) {
//            System.out.println("Left");
//            rSpeed += -1;
//        }
//
//        if (key == KeyEvent.VK_RIGHT) {
//            System.out.println("Right");
//            rSpeed += 1;
//        }
//
//        if (key == KeyEvent.VK_UP) {
//            System.out.println("Up");
//            dy += -1;
//        }
//
//        if (key == KeyEvent.VK_DOWN) {
//            System.out.println("Down");
//            dy += 1;
//        }
//    }
//
//    @Override
//    public void keyReleased(KeyEvent e
//    ) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
}

//                double pythagsTest = Math.sqrt(Math.pow(xPos - xPos[k], 2) + Math.pow(yPos - yPos[k], 2));    //Calculates distance between two balls. 
/**
 * Trashed drag profile
 */
////        if (dragXc > Math.pow(dragX, 2) dragX * Math.sqrt(dragX * 10) && rSpeed != 0) {
//        if (dragXc > dragX * Math.sqrt(dragX * 10) && rSpeed != 0) {
//            dragX++;
//            dragXc = 0;
////            System.out.println("Moving xPos");
//            xPos += rSpeed;
//
//        } else if (rSpeed != 0) {
//
//            if (dragXc > 30000) {
////                dragXc += dragXc);
//                rSpeed--;
//                if (rSpeed > 0) {
//                    rSpeed--;
//                } else if (rSpeed < 0) {
//                    rSpeed++;
//                } else if (rSpeed == 0) {
//                    dragXc = 1;
//                    dragX = 0;
//                }
//            } else {
//                dragXc += 1000;
//            }
////            System.out.println(dragXc * 1000 + ", " + Math.pow(dragX, 2) * Math.sqrt(dragX * 10) + ", " + dragX);
//
//        }
//
//        if (dragYc > dragY * Math.sqrt(dragY * 10) && dy != 0) {
//            dragY++;
//            dragYc = 0;
////            System.out.println("Moving yPos");
//            yPos += dy;
//
//        } else if (dy != 0) {
//
//            if (dragYc > 30000) {
////                dragXc += dragXc);
//                dy--;
//                if (dy > 0) {
//                    dy--;
//                } else if (dy < 0) {
//                    dy++;
//                } else if (dy == 0) {
//                    dragYc = 1;
//                    dragY = 0;
//                }
//            } else {
//                dragYc += 1000;
//            }
////            System.out.println(dragYc * 1000 + ", " + Math.pow(dragY, 2) * Math.sqrt(dragY * 10) + ", " + dragY);
//
//        }
//#############Linear drag
//        if (dy > 0) {
//            dy += -(drag);
//        } else if (dy < 0) {
//            dy += (drag);
//        }
//#################Drawing a square instead
//        g.setColor(Color.red);
//        Rectangle myShape = new Rectangle(-radius, -radius, radius * 2, radius * 2);
//        g2d.translate(xPos / 1000, yPos / 1000);
//        g2d.rotate(Math.toRadians(aThrottle));
//        g2d.draw();
//        g2d.fill(myShape);
//######################Other garbage tests
////        double locationX = scale * rPic.getWidth() / 2;
////        double locationY = scale * rPic.getHeight() / 2;
//        double locationX = getWidth() / 2;
//        double locationY = getHeight() / 2;
//
//        AffineTransform tx = AffineTransform.getRotateInstance(Math.toRadians(aThrottle), locationY, locationY);
////        tx.scale(scale, scale);
//        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
////        g2d.drawImage(op.filter(rPic, null), getWidth() - rPic.getHeight(), getHeight() - rPic.getHeight(), null);
//        g2d.drawImage(op.filter(rPic, null), getWidth() - rPic.getHeight(), getHeight() /2 - rPic.getHeight(), null);
//###############For square window 
//        if (xPos < 00000) {                                             //Detects if the ball tries to cross the left perimeter
////            rSpeed = Math.abs(rSpeed);                                        //absolute value 
//            xPos = (getWidth() - 00000) * 1000;
//        }
//        if (xPos > (getWidth() - 00000) * 1000) { //Detects if the ball tries to cross the right perimeter
//            //            rSpeed = -Math.abs(rSpeed);
//            xPos = 0;
//        }
//
//        if (yPos < 00000) {                                             //Detects if the ball tries to cross the top perimeter
////            dy = Math.abs(dy);
//            yPos = (getHeight() - 00000) * 1000;
//
//        }
//        if (yPos > (getHeight() - 00000) * 1000) {                               //Detects if the ball tries to cross the bottom perimeter
////            dy = -Math.abs(dy);
//            yPos = 0;
//        }
//#####################Old Controls
//            if (key == KeyEvent.VK_LEFT) {
//                System.out.println("Left");
////                    dragX = 1;
//
//                if (aSpeed > -10) {
//                    aSpeed--;
//
//                }
//
//            }
//
//            if (key == KeyEvent.VK_RIGHT) {
//                System.out.println("Right");
//
//                if (aSpeed < 10) {
//                    aSpeed++;
//                }
//            }
//
//            if (key == KeyEvent.VK_UP) {
//                System.out.println("Up");
////                rSpeed += -1000;
//
//                sThrottle += 0.1;
//                if (sThrottle > 1) {
//                    sThrottle = 1;
//                }
//            }
//
//            if (key == KeyEvent.VK_DOWN) {
//                System.out.println("Down");
////                rSpeed = 000;
//
//                sThrottle -= 0.1;
//                if (sThrottle < 0) {
//                    sThrottle = 0;
//                }
//
//            }
//###########Planet Display
//        pYpos = (long) ((xPos) / 1000 * 1 + (getHeight() + (rPic.getHeight() * zScale)) / 2);
//        pYpos = (long) (((altitudeToPlanetCenter) * 1 - pScale) / 100000 + (getHeight() + (rPic.getHeight() * zScale)) / 2);
//        pYpos = (long) ((((altitudeToPlanetCenter) * 1 - pScale) / 100000 + (getHeight() + (rPic.getHeight())) / 2) * zScale);
//        g.fillOval(pXpos, pYpos, pScale, pScale);
