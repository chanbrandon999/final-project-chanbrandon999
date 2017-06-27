/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ISU;

import static ISU.Calculations.travelAngle;
import static ISU.Calculations.trigAngle;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Someone
 */
public class CalculationsTest {
    Calculations c = new Calculations();

    public CalculationsTest() {
    }

    @Test
    public void testPositionUpdate() {
//        Calculations c = new Calculations();
//        c.setRPos(100, 100);
//        c.positionUpdate(new double[10]);
//        assertEquals(true, c.xForce > 9.7 && c.xForce < 9.81);
//        assertEquals(true, true);
//        assertEquals(true, true);
        
        
    }

 

    @Test
    public void testControls() {
    }

    @Test
    public void testleftORright() {
        double angle1 = 200, angle2 = 10;
                                    //angle, hAngle
        assertEquals(1, c.leftORright(200, 10)); //Should turn to the right
        assertEquals(-1, c.leftORright(10, 200)); //Should turn left
        assertEquals(1, c.leftORright(10, 40)); //Should also turn right 
        assertEquals(-1, c.leftORright(10, -10)); //Should also turn right 
        assertEquals(1, c.leftORright(-10, 10)); //Should also turn right 
        assertEquals(1, c.leftORright(355, 30)); //Should also turn right 
    }
    
    @Test
    public void testTravelAngle()   {
        assertEquals(0,(int) travelAngle(0, 1));
        assertEquals(90,(int) travelAngle(1, 0));
        assertEquals(180,(int) travelAngle(0, -1));
        assertEquals(270,(int) travelAngle(-1, 0));
        assertEquals(45,(int) travelAngle(1, 1));
        assertEquals(45 + 90,(int) travelAngle(1, -1));
        assertEquals(45 + 2 * 90,(int) travelAngle(-1, -1));
        assertEquals(45 + 3 * 90,(int) travelAngle(-1, 1));
    }
    
        @Test
    public void testFixAngle() {
        assertEquals(350,(int) c.fix(-10));
        assertEquals(10,(int) c.fix(10));
        assertEquals(180,(int) c.fix(180));
    }

    @Test
    public void testTrigAngle_char_double() {
        assertEquals(Math.sin(-45), trigAngle('s',-1,1), 1);
    }

    @Test
    public void testFix() {
    }

    @Test
    public void testLeftORright() {
    }



}
