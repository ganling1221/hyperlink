package hyperlink;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.*;
import javax.swing.event.*;

/*
 * SliderDemo.java requires all the files in the images/doggy
 * directory.
 */
public class VideoPlayer extends JPanel
                        implements ActionListener,
                                   WindowListener,
                                   ChangeListener,
                                   MouseListener{
    /**
	 * 
	 */
	private static final long serialVersionUID = 8466488705895603792L;
	/**
	 * 
	 */
	//Set up animation parameters.
    static final int FPS_MIN = 0;
    static final int FPS_MAX = 30;
    static final int FPS_INIT = 15;    //initial frames per second
    String framePath;
    static String videoPath;
    int frameNumber;
    int NUM_FRAMES = 90000;
	static int width = 352;
	static int height = 288;
    ImageIcon[] images = new ImageIcon[NUM_FRAMES];
    int delay;
    Timer timer;
    boolean frozen = false;
    Map<Integer, ArrayList<String[]>> hyperlinks;
    //This label uses ImageIcon to show the doggy pictures.
    JLabel picture;

    public VideoPlayer(String path, int num) {
    	framePath = path;
        setLayout(new BorderLayout());
        frameNumber = num;
        delay = 1000 / FPS_INIT;

        loadHyperlink();
        //Create the button.
        JButton play = new JButton("PLAY");
        play.addActionListener(this);
        play.addActionListener(new ActionListener() { 
        	  public void actionPerformed(ActionEvent e) { 
        	    startAnimation();
        	  } 
        	} );
      //Create the button.
        JButton stop = new JButton("STOP");
        stop.addActionListener(this);
        stop.addActionListener(new ActionListener() { 
        	  public void actionPerformed(ActionEvent e) { 
        	    stopAnimation();
        	  } 
        	} );
        //Create the label that displays the animation.
        picture = new JLabel();
        picture.setHorizontalAlignment(JLabel.CENTER);
        picture.setAlignmentX(Component.CENTER_ALIGNMENT);
        picture.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLoweredBevelBorder(),
                BorderFactory.createEmptyBorder(10,10,10,10)));
        updatePicture(0); //display first frame

        //Put everything together.
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new FlowLayout());
        buttonsPanel.add(play);
        buttonsPanel.add(stop);
        add(buttonsPanel,BorderLayout.NORTH);

        add(picture,BorderLayout.CENTER);
        picture.addMouseListener(this);
        setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        //Set up a timer that calls this object's action handler.
        timer = new Timer(delay, this);
        timer.setInitialDelay(delay * 7); //We pause animation twice per cycle
                                          //by restarting the timer
        timer.setCoalesce(true);
    }

    /** Add a listener for window events. */
    void addWindowListener(Window w) {
        w.addWindowListener(this);
    }

    //React to window events.
    public void windowIconified(WindowEvent e) {
        stopAnimation();
    }
    public void windowDeiconified(WindowEvent e) {
        startAnimation();
    }
    public void windowOpened(WindowEvent e) {}
    public void windowClosing(WindowEvent e) {}
    public void windowClosed(WindowEvent e) {}
    public void windowActivated(WindowEvent e) {}
    public void windowDeactivated(WindowEvent e) {}
    
    public void mousePressed(MouseEvent e) {
       
    }
    
    public void mouseReleased(MouseEvent e) {
        }
    
    public void mouseEntered(MouseEvent e) {
       
    }
    
    public void mouseExited(MouseEvent e) {
       
    }
    
    public void mouseClicked(MouseEvent e) {
    	String path = null;
    	int num = 1;
      int x=e.getX();
      int y=e.getY();
      if(hyperlinks.containsKey(frameNumber)) {
    	  String[] pathAndFrame = clickedOnTracedObject(x,y) ;
    	  if(pathAndFrame != null) {
    	      createAndShowGUI(pathAndFrame[0],Integer.valueOf(pathAndFrame[1]));
    	      stopAnimation();
    	  }
      }

    }

    public String[] clickedOnTracedObject(int x, int y) {
    	ArrayList<String[]> boxes = hyperlinks.get(frameNumber);
    	for(int i =0; i<boxes.size();i++) {
    		int startX = Integer.valueOf(boxes.get(i)[0]);
    		int startY = Integer.valueOf(boxes.get(i)[1]);
    		int w = Integer.valueOf(boxes.get(i)[2]);
    		int h = Integer.valueOf(boxes.get(i)[3]);

    		//for the overlapping bounding box, just take the first one created 
    		if(x >=startX && y >= startY && y <= startY+h  && x<=startX+w ) {    
    			return new String[] {boxes.get(i)[4],boxes.get(i)[5]};
    		}
    	}
    	return null;//if not found current pointer within any bouding box, return -1
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

    //Called when the Timer fires.
    public void actionPerformed(ActionEvent e) {
        //Advance the animation frame.
        if (frameNumber == (NUM_FRAMES+1)) {
            frameNumber = 1;
        } else {
            frameNumber++;
        }

        updatePicture(frameNumber); //display the next picture

        if ( frameNumber==(NUM_FRAMES + 1)
          || frameNumber==(NUM_FRAMES/2 + 1) ) {
            timer.restart();
        }
    }

    /** Update the label to display the image for the current frame. */
    protected void updatePicture(int frameNum) {
        //Get the image if we haven't already.
     
        String formatted = String.format("%04d", frameNumber);
        ImageIcon image =createImageIcon(framePath+ formatted + ".rgb");
        //Set the image.
        if (image!= null) {
            picture.setIcon(image);
        } else { //image not found
            picture.setText("image #" + frameNumber + " not found");
        }
    }

    /** Returns an ImageIcon, or null if the path was invalid. */
    protected static ImageIcon createImageIcon(String path) {
        //java.net.URL imgURL = SliderDemo.class.getResource(path);
    	// Read in the specified image
		BufferedImage imgOne = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		readImageRGB(width, height,path, imgOne);
		return new ImageIcon(imgOne);
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
     * @param num 
     */
    private static void createAndShowGUI(String arg, int num) {
        //Create and set up the window.
        JFrame frame = new JFrame("VideoPlayer");
        videoPath = arg;
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        VideoPlayer animator = new VideoPlayer(arg+"/"+arg,num);
                
        //Add content to the window.
        frame.add(animator, BorderLayout.CENTER);

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
		createAndShowGUI(args[0],1);
    }

	@Override
	public void stateChanged(ChangeEvent e) {
		// TODO Auto-generated method stub
		
	}
	public void loadHyperlink() {
		hyperlinks = new HashMap<Integer, ArrayList<String[]>>();
  	  //determine which object 
  	  //based on the object, read the information in from metafile 
	  int count = 0; //for each bounding box, there are 7 lines 
  	  try(BufferedReader br = new BufferedReader(new FileReader(videoPath+"_metadata.txt"))) {
  		    StringBuilder sb = new StringBuilder();
  		    String line = br.readLine();
  		    String[] box = null;
  		    int fn =1; //frame number
  		    while (line != null) {
  		    	//skips the box name
  		    	if(count == 0 ) {
  		    		 box = new String[6];
  		    	}else if(count == 1 ) {
  		  		    String info =line.split(":")[1];
  		    		fn = Integer.valueOf(info);
  		    		hyperlinks.put(fn, new ArrayList<String[]>());  		    		 count++;
 		    	}else {
 		  		    String info =line.split(":")[1];
 		    		box[count-3] = info;
 		    	}
  		        count++;
	  		    line = br.readLine();
  		        if(count == 9) {
  		        	count=0;
  		        	hyperlinks.get(fn).add(box);
  		        }
  		    }
  		} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
    
	}
}
