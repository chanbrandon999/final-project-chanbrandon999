/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ISU;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import javax.swing.JFrame;

/**
 *
 * @author cgallinaro
 */
public class ISU {

    /**
     * Creates and displays the Movement class Window
     *
     * @param args the command line arguments
     */
    private static void createAndShowGUI() {

        JFrame frame = new JFrame("Rocket Simulation");

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        int setX = gd.getDisplayMode().getWidth() - 700;
        int setY = gd.getDisplayMode().getHeight() - 100;

//        int setX = 700, setY = 700; //Flexible window dimensions
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
