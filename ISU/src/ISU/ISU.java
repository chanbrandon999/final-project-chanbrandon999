/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ISU;

import javax.swing.JFrame;

/**
 *
 * @author cgallinaro
 */
public class ISU {

    /**
     * @param args the command line arguments
     */
    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Rocket Simulation");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        int setX = 700, setY = 700; //Flexible window dimensions for balls 
        Movement mvmnt = new Movement(setX, setY);
        frame.add(mvmnt);
        frame.setSize(setX, setY);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

}
