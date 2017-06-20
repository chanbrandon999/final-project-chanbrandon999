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

class Movement extends JPanel implements ActionListener, MouseWheelListener {

    int delay = 10; //10 Milliseconds (1/100 seconds)
    Timer timer;

    long pRadius = 1 * 500000, pScale = pRadius * 10000 * 2;    
    double xPos = 0;                        //used to be int/long
    double yPos = pRadius * 1000;            //used to be int/long
    double aThrottle = 0, aSpeed = 0, angle = 0;
    double rXSpeed = 0, rYSpeed = 0, sThrottle = 0;
    double dx = 0, dy = 0;
    long drag = 1000;
    long pXpos = 0, pYpos = 0;
    double pGravity = 9.81;
    double pAHL = 70000;        //This is the atmospheric height limit where there would be no more atmosphere 
    double[][] stars = new double[50][3];
    double altitudeToPlanetCenter;
    double rMass = 30000;
    long rThrust = 1500 * 1000;
    double tempXF = 0, tempYF = 0;
    double xAccel = 0, yAccel = 0;
    Calculations c = new Calculations();

    Random rand = new Random(); //Random number generator
    BufferedImage rPic = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
    BufferedImage starBkg = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
    double zScale = 0.3, scrollX = 0;

    public Movement(int width, int height) {
//        BufferedImage rPic = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);

        try {

            rPic = ImageIO.read(new File("Rocket.png"));
            starBkg = ImageIO.read(new File("tile.jpeg"));

        } catch (IOException e) {
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

    }

    @Override
    public void actionPerformed(ActionEvent e) {

//        drag = yPos / 500000;
        drag = 0;  //temporary measure 

        calcD();

        tempXF = (calcT() * trigAngle("s", angle)) - (calcG() * trigAngle("s", xPos, yPos));

//            System.out.println("That Y");
        tempYF = (calcT() * trigAngle("c", angle)) - (calcG() * trigAngle("c", xPos, yPos));
//            System.out.println("angle cos multiplier for calcG: " + Math.sin(Math.toRadians(Math.atan(yPos / xPos))));

        xAccel = (tempXF / rMass);
        yAccel = (tempYF / rMass);

        altitudeToPlanetCenter = (Math.sqrt(Math.pow(xPos / 1000, 2) + Math.pow(yPos / 1000, 2)));

        if (altitudeToPlanetCenter < pRadius) {
            if (xPos != 0) {
                tempXF = pRadius * Math.sin(Math.toRadians(Math.atan(yPos / xPos)));
//                tempYF = pRadius * Math.cos(Math.toRadians(Math.atan(yPos / xPos)));
                tempYF = pRadius;
            } else {
                tempXF = 0;
                tempYF = calcG();
            }
//            System.out.println("Under planet tempY = " + tempYF);
//            System.out.println(1 * Math.cos(Math.toRadians(Math.atan(yPos / xPos))));
            xPos += (tempXF);
            yPos += (tempYF);

        } else {
            rXSpeed += xAccel;
            rYSpeed += yAccel;

            xPos += rXSpeed / 100;
            yPos += rYSpeed / 100;
        }

        System.out.println("Thrust Force: " + calcT() + ", \tGravity force " + calcG() + ", \t Gravity cos: " + trigAngle("c", xPos, yPos));
        System.out.println("X component = " + tempXF + ", \tY component = " + tempYF);
        System.out.println("xAccel: " + xAccel + ",  \t\tyAccel: " + yAccel);
        System.out.println("Altitude = " + altitudeToPlanetCenter);
        System.out.println("Rocket Position: " + xPos / 200000 + ", " + yPos / 200000 + "\tshipAngle" + angle + ", \t rCosValue" + trigAngle("c", angle));

        repaint();
    }

    private double calcG() {

        if (altitudeToPlanetCenter <= pRadius) {
            return 0;
        } else {
            return rMass * (pGravity / Math.pow(altitudeToPlanetCenter / pRadius, 2));
        }

    }

    private void calcD() {
//        if (rSpeed > 0) {
//            rSpeed += -(drag);
//        } else if (rSpeed < 0) {
//            rSpeed += (drag);
//        }

//        aSpeed = aThrottle / 1;
//        System.out.println(aSpeed);
        if (aSpeed > 0) {
            aSpeed += (drag / 5);                           //Fix angular drag model 
        } else if (aSpeed < 0) {
            aSpeed -= (drag / 5);
        }
        angle += aSpeed / 5;
    }

    private double calcT() {
        return sThrottle * rThrust; //The total thrust would be limited by the throttle as a percentage of the thrust. 
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        pScale = (long) (pRadius * zScale);
        dispScenery(g);

        AffineTransform at = new AffineTransform();
        at.translate(getWidth() / 2 - rPic.getWidth() * zScale / 2, getHeight() / 2 - rPic.getHeight() * zScale / 2);  //Don't touch. Puts the rocket at the center
        at.scale(zScale, zScale);
        at.rotate(Math.toRadians(angle), rPic.getWidth() / 2, rPic.getHeight() / 2);
        g2d.drawImage(rPic, at, this);

    }

    private void dispScenery(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        //########Black Background
        g2d.setPaint(Color.getHSBColor((float) .55, (float) .71, (float) .70));
        g2d.fillRect(0, 0, getWidth(), getHeight());

        //########Stars
        g.setColor(Color.white);
        for (int i = 0; i < stars.length; i++) {
            stars[i][0] -= dx / 1000000000;
            stars[i][1] -= dy / 1000000000;
            g.fillOval((int) stars[i][1], (int) stars[i][0], (int) stars[i][2], (int) stars[i][2]);
        }

        //#######Planet
        pXpos = (getWidth() - pScale) / 2;
//        pYpos = (long) ((xPos) / 1000 * 1 + (getHeight() + (rPic.getHeight() * zScale)) / 2);
//        pYpos = (long) (((altitudeToPlanetCenter) * 1 - pScale) / 100000 + (getHeight() + (rPic.getHeight() * zScale)) / 2);
//        pYpos = (long) ((((altitudeToPlanetCenter) * 1 - pScale) / 100000 + (getHeight() + (rPic.getHeight())) / 2) * zScale);
        pYpos = (long) (rPic.getHeight() + altitudeToPlanetCenter / 1000000);  //Fix This

//        System.out.println("pYpos = " + pYpos);
//        System.out.println("yPos - pRadius = " + (yPos - pRadius) / 10000);
//        System.out.println((yPos - pRadius) / 1000 + (getHeight() + (rPic.getHeight() * zScale)) / 2);
        g.setColor(Color.green);
        g.fillOval((int) pXpos, (int) pYpos, (int) pScale, (int) pScale);
//        System.out.println(yPos);

    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (zScale >= 0.00005 && zScale <= 1) {
            if (e.getWheelRotation() > 0) {
                zScale -= 0.3 * zScale;
            } else if (e.getWheelRotation() < 0) {
                zScale += 0.3 * zScale;
            }

        }

        if (zScale < 0.00005) {
            zScale = 0.00005;
        } else if (zScale > 1) {
            zScale = 1;
        }
    }

    private class TAdapter extends KeyAdapter {

        @Override
        public void keyReleased(KeyEvent e) {
            int key = e.getKeyCode();

//            if (key == KeyEvent.VK_UP) {
//                System.out.println("Released Up");
////                rSpeed += -1000;
//                sThrottle += 0;
//
//            }
        }

        @Override
        public void keyPressed(KeyEvent e) {
//            System.out.println("Key pressed code=" + e.getKeyCode() + ", char=" + e.getKeyChar());
            int key = e.getKeyCode();

            aThrottle = 0;

            if (key == KeyEvent.VK_LEFT) {
                System.out.println("Left");
//                    dragX = 1;

                if (aSpeed > -10) {
                    aSpeed--;

                }

            }

            if (key == KeyEvent.VK_RIGHT) {
                System.out.println("Right");

                if (aSpeed < 10) {
                    aSpeed++;
                }
            }

            if (key == KeyEvent.VK_UP) {
                System.out.println("Up");
//                rSpeed += -1000;

                sThrottle += 0.1;
                if (sThrottle > 1) {
                    sThrottle = 1;
                }
            }

            if (key == KeyEvent.VK_DOWN) {
                System.out.println("Down");
//                rSpeed = 000;

                sThrottle -= 0.1;
                if (sThrottle < 0) {
                    sThrottle = 0;
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
