/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ISU;

import java.util.Random;
import javax.swing.JFrame;

class Calculations {

    int accuracy = 1000;
    long pRadius = 1 * 600000, pScale = pRadius * 10000 * 2;
    double xPos = 0;                        //used to be int/long
    double yPos = pRadius * accuracy;            //used to be int/long
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
//    boolean[] isPressed = new boolean[4];
    double[] passThrough1 = new double[8];
    int i = 0;

    Random rand = new Random(); //Random number generator
    double zScale = 0.3;
    OutputWindow frame2 = new OutputWindow();
    

    public Calculations() {

        for (int i = 0; i < stars.length; i++) {
            stars[i][0] = rand.nextDouble() * 800;
            stars[i][1] = rand.nextDouble() * 800;
            stars[i][2] = (rand.nextInt(4) + 2);
        }

        frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame2.setVisible(true);
        frame2.setResizable(true);
        frame2.setSize(600, 300);
        frame2.setLocation(700, 50);

        xPos = 0;
        yPos = (pRadius + 100000) * accuracy;
//        rXSpeed = 2427 * 2 * 100;
        rXSpeed = 2427 * 3 * 100 - 18000;
        rYSpeed = 0;
        System.out.println(trigAngle("s", .1, 5));
        

//        frame2.toOutputWindow(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
    }

//    public double[] positionUpdate() {
    public double[] positionUpdate(double[] passThrough) {

        passThrough1 = passThrough;

        controls();

        drag = 0;  //temporary measure 

        calcD();

        tempXF = (calcT() * trigAngle("c", angle)) - (calcG() * trigAngle("c", xPos, yPos));
        tempYF = (calcT() * trigAngle("s", angle)) - (calcG() * trigAngle("s", xPos, yPos));

        xAccel = (tempXF / rMass);
        yAccel = (tempYF / rMass);

        if (i != 0) {
            moveRocket();
            
        }   else    {
            i++;
        }

        altitudeToPlanetCenter = (Math.sqrt(Math.pow(xPos / 1000, 2) + Math.pow(yPos / 1000, 2)));

        angle += aSpeed;

        frame2.toOutputWindow(calcT(), calcG(), trigAngle("c", xPos, yPos), tempXF, tempYF, xAccel, yAccel, altitudeToPlanetCenter, xPos, yPos, angle, trigAngle("c", angle), rXSpeed, rYSpeed);

        return new double[]{xPos, yPos, calcD(), altitudeToPlanetCenter};
    }

    private void moveRocket() {

        rXSpeed += xAccel;
        rYSpeed += yAccel;

//        System.out.println("Speed: " + rYSpeed / 100);
        xPos += rXSpeed / 100;
        yPos += rYSpeed / 100;

        if ((Math.sqrt(Math.pow(xPos / accuracy, 2) + Math.pow(yPos / accuracy, 2))) < pRadius) {

            if ((Math.sqrt(Math.pow(rXSpeed, 2) + Math.pow(rXSpeed, 2))) > 6) {
                System.out.println("Blow up!");
                System.exit(0);
            }

            xPos = pRadius * trigAngle("s", xPos, yPos) * accuracy;
            yPos = pRadius * trigAngle("c", xPos, yPos) * accuracy;
        }
    }

    public static double trigAngle(String sinORcos, double xPos, double yPos) {

        if (xPos == 0 && sinORcos.equals("s")) {
            return 0;
        } else if (xPos == 0 && sinORcos.equals("c")) {
            return 1;
        }

        if (sinORcos.equals("s")) {
            return Math.sin((Math.atan(yPos / xPos)));
            
        } else if (sinORcos.equals("c")) {
            return Math.cos((Math.atan(yPos / xPos)));
        }

        return 0;
    }

    public static double trigAngle(String sinORcos, double angle) {

        if ((sinORcos.equals("s"))) {
            return Math.sin(Math.toRadians(angle));
        } else if (sinORcos.equals("c")) {
            return Math.cos(Math.toRadians(angle));
        }
        return 0;
    }

    private double calcG() {

        if (altitudeToPlanetCenter <= pRadius) {
            return 0;
        } else {
//            System.out.println("altitude: " + altitudeToPlanetCenter + ", pDiameter = " + pDiameter);
            return rMass * (pGravity / Math.pow(altitudeToPlanetCenter / pRadius, 2));
        }

    }

    private double calcD() {
//        if (rSpeed > 0) {
//            rSpeed += -(drag);
//        } else if (rSpeed < 0) {
//            rSpeed += (drag);
//        }

//        aSpeed = aThrottle / 1;
//        System.out.println(aSpeed);
//        if (aSpeed > 0) {
//            aSpeed += (drag / 5);                           //Fix angular drag model 
//        } else if (aSpeed < 0) {
//            aSpeed -= (drag / 5);
//        }
//System.out.println("sdfdsf");
//        System.out.println(aSpeed);
        return angle;
    }

    private double calcT() {
        return sThrottle * rThrust; //The total thrust would be limited by the throttle as a percentage of the thrust.  
    }

//    public void controls(double[] passThrough) {
    public void controls() {

        if (passThrough1[6] == 1) {
            System.out.println("1Left");

            if (aSpeed > -10) {
                aSpeed -= 0.01;

            }
            System.out.println(aSpeed);

        }

        if (passThrough1[7] == 1) {
            System.out.println("1Right");

            if (aSpeed < 10) {
                aSpeed += 0.01;
            }

        }

        if (passThrough1[7] == 10) {
            System.out.println("HOME");

        }

        if (passThrough1[4] == 1) {
            System.out.println("1Up");
            sThrottle += 0.005;
            if (sThrottle > 1) {
                sThrottle = 1;
            }
        }

        if (passThrough1[5] == 1) {
            System.out.println("1Down");
            sThrottle -= 0.005;
            if (sThrottle < 0) {
                sThrottle = 0;
            }

        }

//        return new double[]{aSpeed, sThrottle};
    }
}

//##########################NEW GARBAGE CODE###################################
//        System.out.println("Thrust Force: " + calcT() + ", \tGravity force " + calcG() + ", \t Gravity cos: " + trigAngle("c", xPos, yPos));
//        System.out.println("X component = " + tempXF + ", \tY component = " + tempYF);
//        System.out.println("xAccel: " + xAccel + ",  \t\tyAccel: " + yAccel);
//        System.out.println("Altitude = " + altitudeToPlanetCenter);
//        System.out.println("Rocket Position: " + xPos / 1000 + ", " + yPos / 1000 + "\tshipAngle" + angle + ", \t rCosValue" + trigAngle("c", angle));
