/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ISU;

import java.util.Random;
import javax.swing.JFrame;

class Calculations {

//    Movement m = new Movement();
    int accuracy = 1000;
    long pRadius = 1 * 600000, pScale = pRadius * 10000 * 2;
    public double xPos = 0;                        //used to be int/long
    public double yPos = pRadius * accuracy;            //used to be int/long
    public double aThrottle = 0, aSpeed = 0, angle = 0;
    public double rXSpeed = 0, rYSpeed = 0, sThrottle = 0;
    long rThrust = 1500 * 1000 * 1;
    long pXpos = 0, pYpos = 0;
    double pGravity = 9.81;
    double pAHL = 670000;        //This is the atmospheric height limit where there would be no more atmosphere 
    double[][] stars = new double[50][3];
    public double altitudeToPlanetCenter;
    double rFuel = 16000, initFuel = rFuel;
    double rMass = 12.75 * 1000 + rFuel;

    double xForce, yForce;
    double xAccel, yAccel;
//    boolean[] isPressed = new boolean[4];
    double[] controls = new double[10];
    int selectHeading;
    int delay = 10;
    int tDelay = 1000 / delay;

    boolean startBlowUp;
    boolean timeWarpOn;

    boolean resetAngle;
    double hAngle[] = new double[4];
    double midAngle;
    boolean slowSpin = true;
    double[] previousASpeed = new double[2];

    Random rand = new Random(); //Random number generator
    double zScale = 0.3;
    OutputWindow frame2 = new OutputWindow();
    boolean dispTelemetry = false;
//    Movement m = new Movement();

    /**
     * Initializes set variables
     */
    public Calculations() {

        for (int i = 0; i < stars.length; i++) {
            stars[i][0] = rand.nextDouble() * 800;
            stars[i][1] = rand.nextDouble() * 800;
            stars[i][2] = (rand.nextInt(4) + 2);
        }

        if (dispTelemetry == true) {
            frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame2.setVisible(true);
            frame2.setResizable(true);
            frame2.setSize(600, 300);
            frame2.setLocation(1250, 50);
        }

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
     * @param controlsPassThrough An array of values with the positioning and
     * User Input
     * @return A second array with the resulting forces acting on the rocket.
     */
    public void positionUpdate(double[] controlsPassThrough) {

        if (timeWarpOn == true) { //Skips 
            xForce = (-calcG() * trigAngle('s', xPos, yPos));
            yForce = (-calcG() * trigAngle('c', xPos, yPos));
        } else {
            controls = controlsPassThrough;
            controls();
            xForce = (calcT() * trigAngle('s', fix(angle + 0 * travelAngle(xPos, yPos)))) - (calcG() * trigAngle('s', xPos, yPos) + calcD() * trigAngle('s', rXSpeed, rYSpeed));
            yForce = (calcT() * trigAngle('c', fix(angle + 0 * travelAngle(xPos, yPos)))) - (calcG() * trigAngle('c', xPos, yPos) + calcD() * trigAngle('c', rXSpeed, rYSpeed));
            //Adds components of the X forces and Y forces together. Includes Thrust, Gravity and Drag. 
        }

        xAccel = (xForce / rMass);
        yAccel = (yForce / rMass);
        //Adds the forces together to find the acceleration components according to Newton's law: F = mass * acceleration   

        applyMovement();

        altitudeToPlanetCenter = (Math.sqrt(Math.pow(xPos / 1000, 2) + Math.pow(yPos / 1000, 2)));

        if (dispTelemetry == true) {
            frame2.toOutputWindow(calcT(), calcG(), trigAngle('s', xPos, yPos), xForce, yForce, xAccel, yAccel, altitudeToPlanetCenter, aSpeed, xPos * accuracy, yPos * accuracy, angle, trigAngle('c', angle), rXSpeed, rYSpeed, calcD() * trigAngle('c', rXSpeed, rYSpeed), calcD() * trigAngle('s', rXSpeed, rYSpeed));
        }

    }

    /**
     * Moves the rocket given the acceleration of the rocket in X and Y
     * directions
     */
    public void applyMovement() {

        rXSpeed += xAccel / tDelay;     //The speed increments each 10 milliseconds 
        rYSpeed += yAccel / tDelay;

        xPos += rXSpeed * delay;                //The position updates each 10 miliseconds by the speed each milisecond 
        yPos += rYSpeed * delay;

        if ((Math.sqrt(Math.pow(xPos / accuracy, 2) + Math.pow(yPos / accuracy, 2))) < pRadius) {   //Checks if the rocket hits the ground 

//            System.out.println("crashSpeed: " + (Math.sqrt(Math.pow(rXSpeed * delay, 2) + Math.pow(rYSpeed * delay, 2))));
            if ((Math.sqrt(Math.pow(rXSpeed * delay, 2) + Math.pow(rYSpeed * delay, 2))) > 6) {   //Checks if it hits it hard enough to blow up 
//                System.out.println("Blow up!");
                startBlowUp = true;
            }

            double crashAngleX = trigAngle('s', xPos, yPos);
            double crashAngleY = trigAngle('c', xPos, yPos);

            xPos = pRadius * crashAngleX * accuracy;
            yPos = pRadius * crashAngleY * accuracy;
        }

        if (timeWarpOn == false) {
            rotationControl(selectHeading);
            angle += aSpeed;
            angle = fix(angle);
        }

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
            if (Math.signum(xPos) == -1) {
                return -Math.abs(Math.sin((Math.atan(xPos / yPos))));
            } else {
                return Math.abs(Math.sin((Math.atan(xPos / yPos))));
            }

        } else if (sinORcos == 'c') {
            if (Math.signum(yPos) == -1) {
                return -Math.abs(Math.cos((Math.atan(xPos / yPos))));
            } else {
                return Math.abs(Math.cos((Math.atan(xPos / yPos))));
            }
        }

        return 0;
    }

    /**
     * Finds the Sine or Cosine value of two specified "sides."
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
    public double calcG() {

        if (altitudeToPlanetCenter <= pRadius) { //Checks if the rocket is underneath the planet
            return 0;
        } else {
            return rMass * (pGravity / Math.pow(altitudeToPlanetCenter / pRadius, 2));
            //Returns force of gravity according to diminishing gravity laws
            //See link for equations: https://www.mansfieldct.org/Schools/MMS/staff/hand/lawsgravaltitude.htm
        }

    }

    public double calcD() { //###################################################################################################################
        if (altitudeToPlanetCenter < pAHL) {
            double atmPressure = 100.13 * Math.pow(Math.E, -(altitudeToPlanetCenter - pRadius) / 5600); //Calculates atmospheric pressure of the atmosphere.
            return 0.5 * atmPressure * Math.pow((Math.sqrt(Math.pow(rXSpeed / 100, 2) + Math.pow(rYSpeed / 100, 2))), 2) * 0.2 * 2; //Calculates atmospheric drag
            //See link for details: http://wiki.kerbalspaceprogram.com/wiki/Atmosphere
        } else {
            return 0;
        }
    }

    /**
     * Calculates the thrust of the rocket given the max thrust and the current
     * throttle setting.
     *
     * @return Force of thrust in Newtons.
     */
    public double calcT() {
        if (rFuel > 0) {
            if (sThrottle > 0) {
                rFuel -= 492.74 / 1000 * sThrottle;
                rMass = 12.75 * 1000 + rFuel;
                return sThrottle * rThrust;
            }
        }
        return 0;
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
        if (((Math.signum(previousASpeed[0]) * Math.signum(previousASpeed[1]) == -1) && true)
                || ((angle) > fix(hAngle[SASpos] + 178) && (angle) < fix(hAngle[SASpos] - 178))) {

            if ((angle) > hAngle[SASpos]) {
                if ((angle) - hAngle[SASpos] > 180) {
                    midAngle = fix((((angle) - 360) + hAngle[SASpos]) / 2);
                } else {
                    midAngle = fix((((angle)) + hAngle[SASpos]) / 2);
                }
            } else {
                if (hAngle[SASpos] - (angle) > 180) {
                    midAngle = fix(((hAngle[SASpos] - 360) + (angle)) / 2);
                } else {
                    midAngle = fix(((hAngle[SASpos]) + (angle)) / 2);
                }
            }

            slowSpin = true;
        }

        if (aSpeed > 1.5) {
            aSpeed -= 0.01;
        } else if (aSpeed < -1.5) {
            aSpeed += 0.01;
            //This part slows down the ship if it is going too fast 
        } else if (leftORright((angle), midAngle) == 1 && slowSpin == true && leftORright((angle), hAngle[SASpos]) == -1) {
            aSpeed += 0.01;
            //Made for when the ship rotation needs to slow down when travelling to the left 
        } else if (leftORright((angle), midAngle) == -1 && slowSpin == true && leftORright((angle), hAngle[SASpos]) == 1) {
            aSpeed -= 0.01;
            //Made for when the ship rotation needs to slow down when travelling to the right 
        } else if (leftORright((angle), hAngle[SASpos]) == -1) {
            aSpeed -= 0.01;
            //Made for when the ship rotation needs to rotate to the left 
        } else if (leftORright((angle), hAngle[SASpos]) == 1) {
            aSpeed += 0.01;
            //Made for when the ship rotation needs to rotate to the right
        } else {
            //Resorts to slowing the rotation if all else does not meet the conditions
            if (aSpeed > hAngle[SASpos]) {
                aSpeed -= 0.01;
            } else if (aSpeed < hAngle[SASpos]) {
                aSpeed += 0.01;
            } else {
                aSpeed = 0;
            }
        }
//        System.out.println(" Target: " + hAngle[SASpos] + ", current: " + (angle) + ", direction of travel: " + leftORright((angle), hAngle[SASpos]) + ", slowDirection: " + leftORright((angle), midAngle));

        previousASpeed[0] = previousASpeed[1];
        previousASpeed[1] = aSpeed;
    }

    /**
     * Changes negative angles to positive angles, and reduces angles higher
     * than 360.
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
     * Returns which direction the rocket should turn for the shortest distance
     * to angle2
     *
     * @param angle The angle the rocket is pointing at
     * @param angle2 The desired pointed angle
     * @return The direction the rocket should turn
     */
    public int leftORright(double angle, double angle2) {
        double angleL = fix((angle - angle2) % 360);
        double other = fix((360 - angleL) % 360);
//        System.out.println("angleL: " + angleL + ", other: " + other);
        if (angleL < other) {
            return -1;
        } else if (angleL > other) {
            return 1;
        }
        return 0;
    }

    public static double travelAngle(double x, double y) {
        if (Math.signum(x) == 1 && Math.signum(y) == 1) {
            return (((Math.toDegrees(Math.atan(x / y)))) + 0);
        } else if (Math.signum(x) == 1 && Math.signum(y) == -1) {
            return (((Math.toDegrees(Math.atan(x / y)))) + 180);
        } else if (Math.signum(x) == -1 && Math.signum(y) == -1) {
            return (((Math.toDegrees(Math.atan(x / y)))) + 180);
        } else if (Math.signum(x) == -1 && Math.signum(y) == 1) {
            return (360 - (Math.abs(Math.toDegrees(Math.atan(x / y)))));
//##############################################################################
        } else if (Math.signum(x) == 0 && Math.signum(y) == 1) {
            return 0;
        } else if (Math.signum(x) == 1 && Math.signum(y) == 0) {
            return 90;
        } else if (Math.signum(x) == 0 && Math.signum(y) == -1) {
            return 180;
        } else if (Math.signum(x) == -1 && Math.signum(y) == 0) {
            return 270;
        }

        return 0;

    }

    /**
     * Takes the passThrough array to process the user input from the Movement
     * class
     */
    public void controls() {

        if (controls[0] == 1) {             //W key
            sThrottle += 0.005;
            if (sThrottle > 1) {
                sThrottle = 1;
            }
        } else if (controls[0] == -1) {     //The 'Z' key
            sThrottle = 0;
        }

        if (controls[1] == 1) { //S key
            sThrottle -= 0.005;
            if (sThrottle < 0) {
                sThrottle = 0;
            }
        } else if (controls[1] == 10) {     //The 'X' key
//            System.out.println("Full Throttle");
            sThrottle = 1;
        }

        if (controls[2] == 1) {     //A key
            if (aSpeed > -7) {
                aSpeed -= 0.01;
            }
        }

        if (controls[3] == 1) { //D key
            if (aSpeed < 7) {
                aSpeed += 0.01;
            }
        }

        if (controls[7] == 10) {        //Puts rocket in preset orbit //Home key
            xPos = 0;
            yPos = (pRadius + 100000) * accuracy;
            rXSpeed = 2243 * 1;
            rYSpeed = 0;
        }

        if (controls[8] == 10) {        //End key, fills fuel
            rFuel = 16000;
        }

    }

    //Setters
    public void setRPos(double xPos, double yPos) {
        this.xPos = xPos;
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
