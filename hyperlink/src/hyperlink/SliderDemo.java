package hyperlink;

/*
 * Copyright (c) 1995, 2008, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */ 


import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import javax.swing.*;
import javax.swing.event.*;

/*
 * SliderDemo.java requires all the files in the images/doggy
 * directory.
 */
public class SliderDemo extends JPanel
                        implements ActionListener,
                                   WindowListener,
                                   ChangeListener {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//Set up animation parameters.ç
    static final int FPS_MIN = 1;
    static final int FPS_MAX = 29;
    static final int FPS_INIT = 0;    //initial frames per second
	static int width = 352;
	static int height = 288;
    int frameNumber = 0;
    int NUM_FRAMES = 30;
    Image[] images = new Image[NUM_FRAMES]; // changed from ImageIcon to Image
    //ImageIcon[] images = new ImageIcon[NUM_FRAMES];
    int delay;
    Timer timer;
    boolean frozen = false;
    String video1Path;
    //This label uses ImageIcon to show the doggy pictures.
    static JLabel picture;
    //JLabel picture;
    
    /////
    private BufferedImage background;
    private Point startPt = null;
    private Point endPt = null;
    private Point currentPt = null;
    /////

    public SliderDemo(String[] paths) {
    	video1Path = paths[0];
        loadPicture();

        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

       //delay = 1000 / FPS_INIT;

        //Create the label.
        JLabel sliderLabel = new JLabel("Frames Number", JLabel.CENTER);
        sliderLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        //Create the slider.
        JSlider framesPerSecond = new JSlider(JSlider.HORIZONTAL,
                                              FPS_MIN, NUM_FRAMES, FPS_MIN);
        

        framesPerSecond.addChangeListener(this);
        
        
        ///////
        // Add button to open up new JFrame canvas for drawing bounding box
        JButton drawButton = new JButton("Draw");
        drawButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        drawButton.addActionListener(this);
        ///////

        //Turn on labels at major tick marks.

        framesPerSecond.setMajorTickSpacing(10);
        framesPerSecond.setMinorTickSpacing(1);
        framesPerSecond.setPaintTicks(true);
        framesPerSecond.setPaintLabels(true);
        framesPerSecond.setBorder(
                BorderFactory.createEmptyBorder(0,0,10,0));
        Font font = new Font("Serif", Font.ITALIC, 15);
        framesPerSecond.setFont(font);

        //Create the label that displays the animation.
        picture = new JLabel();
        picture.setHorizontalAlignment(JLabel.CENTER);
        picture.setAlignmentX(Component.CENTER_ALIGNMENT);
        picture.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLoweredBevelBorder(),
                BorderFactory.createEmptyBorder(10,10,10,10)));
        updatePicture(1); //display first frame
        
        //Put everything together.
        add(sliderLabel);
        add(framesPerSecond);
        add(picture);
        setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        
        //////
        add(drawButton);    // Add button for drawing on canvas JFrame
        //////

        //Set up a timer that calls this object's action handler.
        timer = new Timer(delay, this);
        timer.setInitialDelay(delay * 7); //We pause animation twice per cycle
                                          //by restarting the timer
        timer.setCoalesce(true);
     
    }

    ///////
    // Open new JFrame canvas to draw onto BufferedImage and store/replace this image into
    // current JFrame picture -- In progress
    public void actionPerformed(ActionEvent e) {
       JFrame canvas = new JFrame("Edit");
       canvas.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       
       //Add content to the window.
       canvas.add(picture, BorderLayout.CENTER);

       //Display the window.
       canvas.pack();
       canvas.setLocationByPlatform(true); 
       canvas.setVisible(true); 
    }
    ///////
    
    /** Add a listener for window events. */
    void addWindowListener(Window w) {
        w.addWindowListener(this);
    }

    //React to window events.
    public void windowIconified(WindowEvent e) {
        //stopAnimation();
    }
    public void windowDeiconified(WindowEvent e) {
        //startAnimation();
    }
    public void windowOpened(WindowEvent e) {}
    public void windowClosing(WindowEvent e) {}
    public void windowClosed(WindowEvent e) {}
    public void windowActivated(WindowEvent e) {}
    public void windowDeactivated(WindowEvent e) {}

    /** Listen to the slider. */
    public void stateChanged(ChangeEvent e) {
        JSlider source = (JSlider)e.getSource();
//        if (!source.getValueIsAdjusting()) {
//            int fps = (int)source.getValue();
//            if (fps == 0) {
//                if (!frozen) stopAnimation();
//            } else {
//                delay = 1000 / fps;
//                timer.setDelay(delay);
//                timer.setInitialDelay(delay * 10);
//                if (frozen) startAnimation();
//            }
//        }
        if (!source.getValueIsAdjusting()) {
        	frameNumber = (int)source.getValue()-1;
        }
        updatePicture(frameNumber);
    }
   
    public void startAnimation() {
        //Start (or restart) animating!
        timer.start();
        frozen = false;
    }

    public void stopAnimation() {
        //Stop the animating thread.
        timer.stop();
        frozen = true;
    }

    /*
    //Called when the Timer fires.
    public void actionPerformed(ActionEvent e) {
        //Advance the animation frame.
//        if (frameNumber == (NUM_FRAMES )) {
//            frameNumber = 1;
//        } else {
//            frameNumber++;
//        }
//
//        updatePicture(frameNumber); //display the next picture
//
//        if ( frameNumber==(NUM_FRAMES )
//          || frameNumber==(NUM_FRAMES/2) ) {
//            timer.restart();
//        }
    }
*/


    @Override
    protected void paintComponent(Graphics g) {
       super.paintComponent(g);
       if (background != null) {
          g.drawImage(background, 0, 0, null);
       }

       if (startPt != null && currentPt != null) {
          g.setColor(new Color(255, 100, 200));
          int x = Math.min(startPt.x, currentPt.x);
          int y = Math.min(startPt.y, currentPt.y);
          int width = Math.abs(startPt.x - currentPt.x);
          int height = Math.abs(startPt.y - currentPt.y);
          g.drawRect(x, y, width, height);
       }
    }
    
    ///////
    private class ModifiedMouseAdapter extends MouseAdapter {
       @Override
       public void mouseDragged(MouseEvent mEvt) {
          currentPt = mEvt.getPoint();
          SliderDemo.this.repaint();
       }

       @Override
       public void mouseReleased(MouseEvent mEvt) {
          endPt = mEvt.getPoint();
          currentPt = null;
          
          Graphics g = background.getGraphics();
          
          g.setColor(new Color(255, 0, 0));
          int x = Math.min(startPt.x, endPt.x);
          int y = Math.min(startPt.y, endPt.y);
          int width = Math.abs(startPt.x - endPt.x);
          int height = Math.abs(startPt.y - endPt.y);
          g.drawRect(x, y, width, height);
          g.dispose();

          startPt = mEvt.getPoint();
          repaint();
       }

       @Override
       public void mousePressed(MouseEvent mEvt) {
          startPt = mEvt.getPoint();
       }
    }
    
    
    /** Update the label to display the image for the current frame. */
    protected void updatePicture(int frameNumber) {
        //Set the image.
        if (images[frameNumber] != null) {
           
           //////
           // Allow for drawing on top of BufferedImage
           background = (BufferedImage) images[frameNumber];
           Graphics g = background.getGraphics();
           g.drawImage(background, 0, 0, this);
           g.dispose();
           
           ModifiedMouseAdapter myMouseAdapter = new ModifiedMouseAdapter();
           addMouseMotionListener(myMouseAdapter);
           addMouseListener(myMouseAdapter);
           /////
           
           picture.setIcon(new ImageIcon(background)); 
           
        } else { //image not found
            picture.setText("image #" + frameNumber + " not found");
        }
    }

    private void loadPicture(){
    	//Get the image if we haven't already.
    	for(int i = 0; i < NUM_FRAMES; i++) {
    		String formatted = String.format("%04d", i + 1);
    		System.out.println(formatted);
    		images[i] = createImageIcon(video1Path + formatted + ".rgb");

    	}
        
        
    }
    /** Returns an ImageIcon, or null if the path was invalid. */
    // Changed to Image instead of ImageIcon to allow drawing capabilities
    protected static Image createImageIcon(String path) {           
       //java.net.URL imgURL = SliderDemo.class.getResource(path);
    	
       // Read in the specified image
		BufferedImage imgOne = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		readImageRGB(width, height, path, imgOne);
		
		return imgOne;
    }

	/** Read Image RGB
	 *  Reads the image of given width and height at the given imgPath into the provided BufferedImage.
	 */
	private static void readImageRGB(int width, int height, String imgPath, BufferedImage img)
	{
		try
		{
			int frameLength = width*height*3;

			File file = new File(imgPath);
			RandomAccessFile raf = new RandomAccessFile(file, "r");
			raf.seek(0);

			long len = frameLength;
			byte[] bytes = new byte[(int) len];

			raf.read(bytes);

			int ind = 0;
			for(int y = 0; y < height; y++)
			{
				for(int x = 0; x < width; x++)
				{
					byte a = 0;
					byte r = bytes[ind];
					byte g = bytes[ind+height*width];
					byte b = bytes[ind+height*width*2]; 
					int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
					//int pix = ((a << 24) + (r << 16) + (g << 8) + b);
					img.setRGB(x,y, pix);
					ind++;
				}
			}
		}
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     * @param args 
     */
    private static void createAndShowGUI(String[] args) {
        //Create and set up the window.
        JFrame frame = new JFrame("SliderDemo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //take in the first file path 
        SliderDemo animator = new SliderDemo(args);
                
        //Add content to the window.
        frame.add(animator, BorderLayout.CENTER);

        //Display the window.
        frame.pack();
        frame.setLocationByPlatform(true); /////// added
        frame.setVisible(true);
        //animator.startAnimation(); 


    }

    
    public static void main(String[] args) {
        /* Turn off metal's use of bold fonts */
        UIManager.put("swing.boldMetal", Boolean.FALSE);
        
        
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI(args);
                
            }
        });
    }
    
    
    
}
