package hyperlink;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;

public class AuthoringApp {

	 public static void main(String[] args) {
	        /* Turn off metal's use of bold fonts */
	        UIManager.put("swing.boldMetal", Boolean.FALSE);
	        //Schedule a job for the event-dispatching thread:
	        //creating and showing this application's GUI.
	        javax.swing.SwingUtilities.invokeLater(new Runnable() {
	            public void run() {
	                createAndShowGUI();
	            }
	        });
	    }
	 /**
	     * Create the GUI and show it.  For thread safety,
	     * this method should be invoked from the
	     * event-dispatching thread.
	     * @param args 
	     */
	    private static void createAndShowGUI() {
	        //Create and set up the window.
	        Interface frame = new Interface();
	       
	    }
}
