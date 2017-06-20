/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ISU;

/**
 *
 * @author Someone
 */
public class Calculations {

    public Calculations() {

    }

    public static double trigAngle(String sinORcos, double xPos, double yPos) {

        if (xPos == 0 && sinORcos.equals("s")) {
            return 0;
        } else if (xPos == 0 && sinORcos.equals("c")) {
            return 1;
        }

        if (sinORcos.equals("s")) {
            return Math.sin(Math.toRadians(Math.atan(yPos / xPos)));
        } else if (sinORcos.equals("c")) {
            return Math.cos(Math.toRadians(Math.atan(yPos / xPos)));
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

    private static double calcG() {

//        if (altitudeToPlanetCenter <= pDiameter) {
//            return 0;
//        } else {
////            System.out.println("altitude: " + altitudeToPlanetCenter + ", pDiameter = " + pDiameter);
//            return rMass * (pGravity / Math.pow(altitudeToPlanetCenter / pDiameter, 2));
//        }
        return 0;

    }

    private static double calcD() {
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
//        angle += aSpeed / 5;
        return 0;
    }

    private static double calcT() {

        return 0;
//        return sThrottle * rThrust; //The total thrust would be limited by the throttle as a percentage of the thrust. 

    }
}
