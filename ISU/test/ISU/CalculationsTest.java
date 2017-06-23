/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ISU;

import static ISU.Calculations.travelAngle;
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
    }

    @Test
    public void testTrigAngle_3args() {
    }

    @Test
    public void testTrigAngle_String_double() {
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
    }
    
    @Test
    public void testTravelAngle()   {
        assertEquals(0,(int) travelAngle(0, 1));
        assertEquals(90,(int) travelAngle(1, 0));
        assertEquals(180,(int) travelAngle(0, -1));
        assertEquals(270,(int) travelAngle(-1, 0));
    }

}
