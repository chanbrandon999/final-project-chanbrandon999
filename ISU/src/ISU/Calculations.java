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
    long drag = 10;
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

    int tDelayI = 10;
    int tDelay = 1000 / tDelayI;

    boolean resetAngle;
    double hAngle[] = new double[4];
    double midAngle;
    boolean slowSpin = true;
    double[] paSpeed = new double[2];
    int aSpeedChange;

    Random rand = new Random(); //Random number generator
    double zScale = 0.3;
    OutputWindow frame2 = new OutputWindow();

    /**
     *
     */
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
        frame2.setLocation(1250, 50);

        if (false) {
            xPos = 0;
            yPos = (pRadius + 100000) * accuracy;
            rXSpeed = 2243 * 1;
            rYSpeed = 0;
        }

        if (false) {
            yPos = (pRadius + 300) * accuracy;
            angle = 180;
            rYSpeed = -1000;
        }

    }

    /**
     * Updates the position of the rocket based off the forces acting on the
     * rocket
     *
     * @param passThrough An array of values with the positioning and User Input
     * @return A second array with the resulting forces acting on the rocket.
     */
    public double[] positionUpdate(double[] passThrough) {

        passThrough1 = passThrough;

        controls();

        drag = 0;  //temporary measure 

//        calcD();
        xForce = (calcT() * trigAngle('s', angle)) - (calcG() * trigAngle('s', xPos, yPos));
        yForce = (calcT() * trigAngle('c', angle)) - (calcG() * trigAngle('c', xPos, yPos));

        xAccel = (xForce / rMass);
        yAccel = (yForce / rMass);

        if (moveRocket() == true || passThrough1[9] == 0.8103) {
            return new double[]{-1.9182736465, -1.192837465};
        }

        altitudeToPlanetCenter = (Math.sqrt(Math.pow(xPos / 1000, 2) + Math.pow(yPos / 1000, 2)));

        frame2.toOutputWindow(calcT(), calcG(), trigAngle('s', xPos, yPos), xForce, yForce, xAccel, yAccel, altitudeToPlanetCenter, aSpeed, xPos * accuracy, yPos * accuracy, angle, trigAngle('c', angle), rXSpeed, rYSpeed);
        return new double[]{xPos, yPos, angle, altitudeToPlanetCenter, rXSpeed, rYSpeed, sThrottle};
    }

    /**
     * Sets the positioning of the rocket
     *
     * @return
     */
    private boolean moveRocket() {

        rXSpeed += xAccel / tDelay;     //The speed increments each 10 milliseconds 
        rYSpeed += yAccel / tDelay;

        xPos += rXSpeed * tDelayI;                //The position updates each 10 miliseconds by the speed each milisecond 
        yPos += rYSpeed * tDelayI;

        if ((Math.sqrt(Math.pow(xPos / accuracy, 2) + Math.pow(yPos / accuracy, 2))) < pRadius) {   //Checks if the rocket hits the ground 

            if ((Math.sqrt(Math.pow(rXSpeed * tDelayI, 2) + Math.pow(rYSpeed * tDelayI, 2))) > 6) {   //Checks if it hits it hard enough to blow up 
//                System.out.println("Blow up!");
                return true;
            }

            xPos = pRadius * trigAngle('s', xPos, yPos) * accuracy;
            yPos = pRadius * trigAngle('c', xPos, yPos) * accuracy;
        }

        angle += aSpeed;
        angle = fix(angle);

//        System.out.println("Fandangled Angle: \t\t\t\t\t" + travelAngle(rXSpeed, rYSpeed));
        rotationControl((int) passThrough1[8]);
        return false;

    }

    /**
     * Calculates the angle of the rocket to the center of the planet
     *
     * @param sinORcos Specifies which math equation to use. 's' for Sine, 'c'
     * for Cosine
     * @param xPos The xPosition of the rocket to find the angle
     * @param yPos The yPosition of the rocket to find the angle
     * @return Returns the cosine or sine value of the angle to the center of
     * the planet
     */
    public static double trigAngle(char sinORcos, double xPos, double yPos) {

        if (xPos == 0 && sinORcos == 's') {
            return 0;
        } else if (xPos == 0 && sinORcos == 'c') {
            return 1;
        }

        if (sinORcos == 's') {
            return Math.sin((Math.atan(xPos / yPos)));

        } else if (sinORcos == 'c') {
            return Math.cos((Math.atan(xPos / yPos)));
        }

        return 0;
    }

    /**
     * Finds the Sine or Cosine value of a specified angle
     *
     * @param sinORcos Specifies which math equation to use. 's' for Sine, 'c'
     * for Cosine
     * @param angle The angle of the rocket to find the trigonometric value for
     * @return The Sine or Cosine value of a specified angle
     */
    public static double trigAngle(char sinORcos, double angle) {

        if ((sinORcos == 's')) {
            return Math.sin(Math.toRadians(angle));
        } else if (sinORcos == 'c') {
            return Math.cos(Math.toRadians(angle));
        }
        return 0;
    }

    /**
     * Calculates the force of gravity given a certain radius
     *
     * @return The force of gravity in Newtons
     */
    private double calcG() {

        if (altitudeToPlanetCenter <= pRadius) {
            return 0;
        } else {
            return rMass * (pGravity / Math.pow(altitudeToPlanetCenter / pRadius, 2));
        }

    }

    private double calcD() { //###################################################################################################################
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

    /**
     * Calculates the thrust of the rocket given the max thrust and the current
     * throttle setting.
     *
     * @return Force of thrust in Newtons.
     */
    private double calcT() {
        return sThrottle * rThrust;
        //The total thrust would be limited by the throttle as a percentage of the thrust.  
    }

    private void rotationControl(int choice) {

        if ((Math.sqrt(Math.pow(rYSpeed, 2) + Math.pow(rXSpeed, 2))) == 0 && choice != 1) {
            return;
        }

        hAngle[0] = fix(travelAngle(rXSpeed, rYSpeed));
        hAngle[1] = fix(hAngle[0] + 90);
        hAngle[2] = fix(hAngle[1] + 90);
        hAngle[3] = fix(hAngle[2] + 90);

        if (choice != 0 && resetAngle == true) {
//            System.out.println("RAN ONCE ######$%");
            resetAngle = false;
            slowSpin = false;
            midAngle = 0;
        }

        if (choice == 1) { //Holds a stable heading 
            if (aSpeed > 0.01) {
                aSpeed -= 0.01;
            } else if (aSpeed < -0.01) {
                aSpeed += 0.01;
            } else {
                aSpeed = 0;
            }
        } else if (2 <= choice && choice <= 5) {
            rotateTo(choice - 2);
        } else if (resetAngle != true) {
            resetAngle = true;
        }
    }

    /**
     * Rotates towards specified location 0, 1, 2 or 3. These locations are
     * north/east/south/west of the direction of movement.
     *
     * @param SASpos The selection of location to rotate towards
     */
    private void rotateTo(int SASpos) {
//        System.out.println("is opposite: " + ((angle) > fix(hAngle[SASpos] + 178) && (angle) < fix(hAngle[SASpos] - 178)) + ", " + fix(hAngle[SASpos] - 178));
        if (((Math.signum(paSpeed[0]) * Math.signum(paSpeed[1]) == -1) && true)
                || ((angle) > fix(hAngle[SASpos] + 178) && (angle) < fix(hAngle[SASpos] - 178))) {
//            System.out.println("#####################################################################");

            if ((angle) > hAngle[SASpos]) {
                if ((angle) - hAngle[SASpos] > 180) {
                    midAngle = fix((((angle) - 360) + hAngle[SASpos]) / 2);
                } else  {
                    midAngle = fix((((angle)) + hAngle[SASpos]) / 2);
                }
            } else {
                if (hAngle[SASpos] - (angle) > 180) {
                    midAngle = fix(((hAngle[SASpos] - 360) + (angle)) / 2);
                } else  {
                    midAngle = fix(((hAngle[SASpos]) + (angle)) / 2);
                }
            }
            
//            midAngle = ((hAngle[SASpos] + (angle)) % 360) / 2;
//            System.out.println("New intermediate: " + midAngle);
            slowSpin = true;
        }

        if (aSpeed > 1.5) {
            aSpeed -= 0.01;
        } else if (aSpeed < -1.5) {
            aSpeed += 0.01;
        } else //
        //            
        if (leftORright((angle), midAngle) == 1 && slowSpin == true && leftORright((angle), hAngle[SASpos]) == -1) {
//            System.out.print("33");
            aSpeed += 0.01;
        } else if (leftORright((angle), midAngle) == -1 && slowSpin == true && leftORright((angle), hAngle[SASpos]) == 1) {
//            System.out.print("44");
            aSpeed -= 0.01;
//            
        } else if (leftORright((angle), hAngle[SASpos]) == -1) {
//            System.out.print("11");
            aSpeed -= 0.01;
        } else if (leftORright((angle), hAngle[SASpos]) == 1) {
//            System.out.print("22");
            aSpeed += 0.01;
        } else {
            if (aSpeed > hAngle[SASpos]) {
                aSpeed -= 0.01;
            } else if (aSpeed < hAngle[SASpos]) {
                aSpeed += 0.01;
            } else {
                aSpeed = 0;
            }
//            System.out.println("Here012471204712047120712044104");
        }
//        System.out.println(" Target: " + hAngle[SASpos] + ", current: " + (angle) + ", direction of travel: " + leftORright((angle), hAngle[SASpos]) + ", slowDirection: " + leftORright((angle), midAngle));

        paSpeed[0] = paSpeed[1];
        paSpeed[1] = aSpeed;
    }

    /**
     * Changes negative angles to positive angles
     *
     * @param angle The angle to see if it needs fixing
     * @return The fixed angle from 0 < to < 360
     */
    public double fix(double angle) {
        if (Math.signum(angle) == -1) {
            return (360 + angle);
        } else if (angle > 360) {
            return angle % 360;
        }
        return angle;
    }

    /**
     * Returns which direction the rocket should turn for the fastest arrival to
     * angle2
     *
     * @param angle The angle the rocket is pointing at
     * @param angle2 The desired pointed angle
     * @return The direction the rocket should turn
     */
    public int leftORright(double angle, double angle2) {
        double angleL = fix((angle - angle2) % 360);
        double other = fix(360 - angleL);
//        System.out.println("angleL: " + angleL + ", other: " + other);
        if (angleL < other) {
            return -1;
        } else if (angleL > other) {
            return 1;
        }
        return 0;
    }

    public static double travelAngle(double xSpeed, double ySpeed) {
        if (Math.signum(xSpeed) == 1 && Math.signum(ySpeed) == 1) {
            return (((Math.toDegrees(Math.atan(xSpeed / ySpeed)))) + 0);
        } else if (Math.signum(xSpeed) == 1 && Math.signum(ySpeed) == -1) {
            return (((Math.toDegrees(Math.atan(xSpeed / ySpeed)))) + 180);
        } else if (Math.signum(xSpeed) == -1 && Math.signum(ySpeed) == -1) {
            return (((Math.toDegrees(Math.atan(xSpeed / ySpeed)))) + 180);
        } else if (Math.signum(xSpeed) == -1 && Math.signum(ySpeed) == 1) {
            return (360 - (Math.abs(Math.toDegrees(Math.atan(xSpeed / ySpeed)))));
//##############################################################################
        } else if (Math.signum(xSpeed) == 0 && Math.signum(ySpeed) == 1) {
            return 0;
        } else if (Math.signum(xSpeed) == 1 && Math.signum(ySpeed) == 0) {
            return 90;
        } else if (Math.signum(xSpeed) == 0 && Math.signum(ySpeed) == -1) {
            return 180;
        } else if (Math.signum(xSpeed) == -1 && Math.signum(ySpeed) == 0) {
            return 270;
        }

        return 0;

    }

    /**
     * Takes the passThrough array to process the user input from the Movement
     * class
     */
    public void controls() {

        if (passThrough1[6] == 1) {
//            System.out.println("Left");
            if (aSpeed > -7) {
                aSpeed -= 0.01;
            }

//            passThrough1[8] = 0;
        }

        if (passThrough1[7] == 1) {
//            System.out.println("Right");
            if (aSpeed < 7) {
                aSpeed += 0.01;
            }
//            passThrough1[8] = 0;
        }

        if (passThrough1[7] == 10) {        //Puts rocket in preset orbit
//            System.out.println("HOME");
            xPos = 0;
            yPos = (pRadius + 100000) * accuracy;
//        rXSpeed = 2427 * 2 * 100;
//            rXSpeed = 2427 * 3 * 1 - 177.25;
            rXSpeed = 2243 * 1;
            rYSpeed = 0;
        }

        if (passThrough1[4] == 1) {
//            System.out.println("1Up");
            sThrottle += 0.005;
            if (sThrottle > 1) {
                sThrottle = 1;
            }
        } else if (passThrough1[4] == -1) {     //The 'Z' key
//            System.out.println("Cancel Throttle");
            sThrottle = 0;
        }

        if (passThrough1[5] == 1) {
//            System.out.println("1Down");
            sThrottle -= 0.005;
            if (sThrottle < 0) {
                sThrottle = 0;
            }

        } else if (passThrough1[5] == 10) {     //The 'X' key
//            System.out.println("Full Throttle");
            sThrottle = 1;
        }

    }

    //Setters
    public void setxPos(double xPos) {
        this.xPos = xPos;
    }

    public void setyPos(double yPos) {
        this.yPos = yPos;
    }

    public void setaThrottle(double aThrottle) {
        this.aThrottle = aThrottle;
    }

    public void setaSpeed(double aSpeed) {
        this.aSpeed = aSpeed;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }

    public void setrXSpeed(double rXSpeed) {
        this.rXSpeed = rXSpeed;
    }

    public void setrYSpeed(double rYSpeed) {
        this.rYSpeed = rYSpeed;
    }

    public void setsThrottle(double sThrottle) {
        this.sThrottle = sThrottle;
    }

    public void setDrag(long drag) {
        this.drag = drag;
    }

    public void setrMass(double rMass) {
        this.rMass = rMass;
    }

    public void setrThrust(long rThrust) {
        this.rThrust = rThrust;
    }

    public void setzScale(double zScale) {
        this.zScale = zScale;
    }

}

//##########################NEW GARBAGE CODE###################################
//        System.out.println("Thrust Force: " + calcT() + ", \tGravity force " + calcG() + ", \t Gravity cos: " + trigAngle('c', xPos, yPos));
//        System.out.println("X component = " + xForce + ", \tY component = " + yForce);
//        System.out.println("xAccel: " + xAccel + ",  \t\tyAccel: " + yAccel);
//        System.out.println("Altitude = " + altitudeToPlanetCenter);
//        System.out.println("Rocket Position: " + xPos / 1000 + ", " + yPos / 1000 + "\tshipAngle" + angle + ", \t rCosValue" + trigAngle('c', angle));
