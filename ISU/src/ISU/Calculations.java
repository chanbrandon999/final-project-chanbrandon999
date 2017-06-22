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
    long rThrust = 1500 * 1000 * 1;
    double xForce = 0, yForce = 0;
    double xAccel = 0, yAccel = 0;
//    boolean[] isPressed = new boolean[4];
    double[] passThrough1 = new double[10];
    int i = 0;
    int tDelay = 1000 / 10;
    boolean resetAngle;
    double holdAngle = angle;
    double intermediateAngle;
    boolean firstSlowDone;
    int foo;
    boolean firstCheck = true;
    double[] paSpeed = new double[2];

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

        if (false) {
            xPos = 0;
            yPos = (pRadius + 100000) * accuracy;
//        rXSpeed = 2427 * 2 * 100;
            rXSpeed = 2427 * 3 * 1 - 177.25;
//        rXSpeed = 2428 * 2;
            rYSpeed = 0;
        }

        if (false) {
            yPos = (pRadius + 300) * accuracy;
            angle = 180;
            rYSpeed = -1000;
        }

//        frame2.toOutputWindow(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
    }

//    public double[] positionUpdate() {
    public double[] positionUpdate(double[] passThrough) {

        passThrough1 = passThrough;

        controls();

        drag = 0;  //temporary measure 

//        calcD();
        xForce = (calcT() * trigAngle("s", angle)) - (calcG() * trigAngle("s", xPos, yPos));
        yForce = (calcT() * trigAngle("c", angle)) - (calcG() * trigAngle("c", xPos, yPos));

        xAccel = (xForce / rMass);
        yAccel = (yForce / rMass);

        if (moveRocket() == true || passThrough1[9] == 0.8103) {
            return new double[]{-1.9182736465, -1.192837465};
        }

        altitudeToPlanetCenter = (Math.sqrt(Math.pow(xPos / 1000, 2) + Math.pow(yPos / 1000, 2)));

        frame2.toOutputWindow(calcT(), calcG(), trigAngle("s", xPos, yPos), xForce, yForce, xAccel, yAccel, altitudeToPlanetCenter, aSpeed, xPos, yPos, angle, trigAngle("c", angle), rXSpeed, rYSpeed);
        return new double[]{xPos, yPos, angle, altitudeToPlanetCenter, rXSpeed, rYSpeed};
    }

    private boolean moveRocket() {

        rXSpeed += xAccel / tDelay;     //The position increments each 10 milliseconds 
        rYSpeed += yAccel / tDelay;

        xPos += rXSpeed;                //The position updates each 10 miliseconds by the speed each milisecond 
        yPos += rYSpeed;

        if ((Math.sqrt(Math.pow(xPos / accuracy, 2) + Math.pow(yPos / accuracy, 2))) < pRadius) {

            if ((Math.sqrt(Math.pow(rXSpeed / tDelay, 2) + Math.pow(rYSpeed / tDelay, 2))) > 6) {
//                System.out.println("Blow up!");
                return true;
            }

            xPos = pRadius * trigAngle("s", xPos, yPos) * accuracy;
            yPos = pRadius * trigAngle("c", xPos, yPos) * accuracy;
        }

        angle += aSpeed;
        angle = angle % 360;

        rotationControl((int) passThrough1[8]);
        return false;

    }

    public static double trigAngle(String sinORcos, double xPos, double yPos) {

        if (xPos == 0 && sinORcos.equals("s")) {
            return 0;
        } else if (xPos == 0 && sinORcos.equals("c")) {
            return 1;
        }

        if (sinORcos.equals("s")) {
            return Math.sin((Math.atan(xPos / yPos)));

        } else if (sinORcos.equals("c")) {
            return Math.cos((Math.atan(xPos / yPos)));
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
        return 0;
    }

    private double calcT() {
        return sThrottle * rThrust; //The total thrust would be limited by the throttle as a percentage of the thrust.  
    }

//    public void controls(double[] passThrough) {
    private void rotationControl(int choice) {
        if (firstCheck == true && choice != 0) { //goes off when clearing choices pressed 
            holdAngle = angle;
            intermediateAngle = (holdAngle - angle) / 2 + angle;
            firstSlowDone = false;
            foo = 0;
        }

        if (choice == 1) {
            System.out.println(Math.signum(paSpeed[0]) + ", " + Math.signum(paSpeed[1]));
            if (Math.signum(paSpeed[0]) * Math.signum(paSpeed[1]) == -1) {
                System.out.println("########Change angle##########");
                intermediateAngle = (holdAngle - angle) / 2 + angle;
                System.out.println("Intermediate: " + intermediateAngle + ", angle: " + angle);
                firstSlowDone = true;

//                foo++;
//                if (foo == 5) {
//                    firstSlowDone = true;
//                    foo = 0;
//                } else {
//                    firstSlowDone = false;
//                }
                System.out.println("########Change angle##########");
            }
            if (firstSlowDone == false) {
                if ((angle + 180) % 360 < holdAngle) {
//                if ((angle - 180) % 360 > holdAngle) {
                    if (intermediateAngle < angle) {
//                        System.out.println("Slowing1");
                        aSpeed -= 0.01;
//                        System.out.println("s32");
                    } else {
                        aSpeed += 0.01;
//                        System.out.println("sef");
                    }
//                tempDir = 1;
                }
                if ((angle - 180) % 360 > holdAngle) {
//                if ((angle + 180) % 360 < holdAngle) {

                    if (intermediateAngle > angle) {
//                        System.out.println("Slowing2");
                        aSpeed += 0.01;
//                        System.out.println("sdf");
                    } else {
                        aSpeed -= 0.01;
//                        System.out.println("sdf");
                    }
//                tempDir = -1;
                }
//                System.out.println("Do stuff there");
            } else {
//                if (angle < holdAngle) {
//                    aSpeed += 0.0001;
//                } else if (angle > holdAngle) {
//                    aSpeed -= 0.0001;
//                }
                aSpeed = 0;
//                System.out.println("Slow Part, desired angle: " + holdAngle + ", current: " + angle);
            }

            paSpeed[0] = paSpeed[1];
            paSpeed[1] = aSpeed;
            firstCheck = false;

        } else if (choice == 0) {
            firstSlowDone = false;
            foo = 0;
            firstCheck = true;
        }
    }
    int tempAngle = 0;
    int tempAngle2 = 0;

    private static int angleCheck(double tempAngle, double tempAngle2) {
        if ((tempAngle + 180) % 360 < tempAngle2) {

        } else if ((tempAngle - 180) % 360 > tempAngle2) {

        }

        return 0;
    }

    public void controls() {

        if (passThrough1[6] == 1) {
//            System.out.println("Left");
//            passThrough1[8] = 0; //Controlls = 0

            if (aSpeed > -10) {
                aSpeed -= 0.01;

            }

        }

        if (passThrough1[7] == 1) {
//            System.out.println("Right");
//            passThrough1[8] = 0;
            foo = 0;
            if (aSpeed < 10) {
                aSpeed += 0.01;
            }

        }

        if (passThrough1[7] == 10) {
//            System.out.println("HOME");
            xPos = 0;
            yPos = (pRadius + 100000) * accuracy;
//        rXSpeed = 2427 * 2 * 100;
            rXSpeed = 2427 * 3 * 1 - 177.25;
//        rXSpeed = 2428 * 2;
            rYSpeed = 0;
        }

        if (passThrough1[4] == 1) {
//            System.out.println("1Up");
            sThrottle += 0.005;
            if (sThrottle > 1) {
                sThrottle = 1;
            }
        } else if (passThrough1[4] == -1) {
//            System.out.println("Cancel Throttle");
            sThrottle = 0;
        }

        if (passThrough1[5] == 1) {
//            System.out.println("1Down");
            sThrottle -= 0.005;
            if (sThrottle < 0) {
                sThrottle = 0;
            }

        } else if (passThrough1[5] == 10) {
//            System.out.println("Full Throttle");
            sThrottle = 1;
        }

//        return new double[]{aSpeed, sThrottle};
    }
}

//##########################NEW GARBAGE CODE###################################
//        System.out.println("Thrust Force: " + calcT() + ", \tGravity force " + calcG() + ", \t Gravity cos: " + trigAngle("c", xPos, yPos));
//        System.out.println("X component = " + xForce + ", \tY component = " + yForce);
//        System.out.println("xAccel: " + xAccel + ",  \t\tyAccel: " + yAccel);
//        System.out.println("Altitude = " + altitudeToPlanetCenter);
//        System.out.println("Rocket Position: " + xPos / 1000 + ", " + yPos / 1000 + "\tshipAngle" + angle + ", \t rCosValue" + trigAngle("c", angle));
