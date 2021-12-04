package hyperlink;


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
 * A slider show for a given video
 */
public class SliderDemo extends JPanel
                        implements ActionListener,
                                   WindowListener,
                                   ChangeListener, MouseListener {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//Set up animation parameters.ç
    static final int FPS_MIN = 1;
    static final int FPS_MAX = 9000;
    static final int FPS_INIT = 15;    //initial frames per second
	static int width = 352;
	static int height = 288;
    public int frameNumber = 1;
    int NUM_FRAMES = 9000;
    ImageIcon[] images = new ImageIcon[NUM_FRAMES];
    int delay;
    Timer timer;
    boolean frozen = false;
    String videoPath,framePath;
    //This label uses ImageIcon to show the doggy pictures.
    JLabel picture;
    private Point startPt = null;
    private Point endPt = null;
    private Point currentPt = null;
    boolean hasVideo = false;
    int[] currentBoundingBox;
    public SliderDemo(String paths) {
    	videoPath = paths;
    	framePath = videoPath+"/"+videoPath;

        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

       //delay = 1000 / FPS_INIT;

        //Create the label.
        JLabel sliderLabel = new JLabel("Frames Number", JLabel.CENTER);
        sliderLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        //Create the slider.
        JSlider frames = new JSlider(JSlider.HORIZONTAL,
                                              FPS_MIN, NUM_FRAMES, FPS_MIN);
        frames.addChangeListener(this);

        //Turn on labels at major tick marks.

        frames.setMajorTickSpacing(1000);
        frames.setMinorTickSpacing(10);
        frames.setPaintTicks(true);
        frames.setPaintLabels(true);
        frames.setBorder(
                BorderFactory.createEmptyBorder(0,0,10,0));
        Font font = new Font("Serif", Font.ITALIC, 6);
        frames.setFont(font);

        //Create the label that displays the animation.
        picture = new JLabel();
        picture.addMouseListener(this);
        picture.setHorizontalAlignment(JLabel.CENTER);
        picture.setAlignmentX(Component.CENTER_ALIGNMENT);
        picture.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLoweredBevelBorder(),
                BorderFactory.createEmptyBorder(10,10,10,10)));
        updatePicture(1); //display first frame

        //Put everything together.
        add(sliderLabel);
        add(frames);
        add(picture);
        setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        //Set up a timer that calls this object's action handler.
        timer = new Timer(delay, this);
        timer.setInitialDelay(delay * 7); //We pause animation twice per cycle
                                          //by restarting the timer
        timer.setCoalesce(true);
    }

    void createHyperlink() {
    	
    }
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
        	frameNumber = (int)source.getValue();
        }
        updatePicture(frameNumber);
    }

    /** Update the label to display the image for the current frame. */
    protected void updatePicture(int frameNumber) {
        //Set the image.
    	String formatted = String.format("%04d", frameNumber);
    	System.out.println(framePath+ formatted + ".rgb");
		ImageIcon image  = createImageIcon(framePath+ formatted + ".rgb");
		if(image != null) {
            picture.setIcon(image);
		}else {
			System.out.println("No such image");
		}
        
    }
    /** Update the label to display the image for the current frame. */
    protected void updatePictureBoundingBox(int frameNumber,int x, int y, int w, int h) {
        //Set the image.
    	String formatted = String.format("%04d", frameNumber);
    	System.out.println(framePath+ formatted + ".rgb");
		BufferedImage imgOne = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		readImageRGBBox(width, height,framePath+ formatted + ".rgb", imgOne, x, y, w, h);
		ImageIcon image = new ImageIcon(imgOne);
        picture.setIcon(image);       
    }
    
    /** Returns an ImageIcon, or null if the path was invalid. */
    protected  ImageIcon createImageIcon(String path) {
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
	
   public void mouseDragged(MouseEvent mEvt) {
	   
   }

   @Override
   public void mouseReleased(MouseEvent mEvt) {
      endPt = mEvt.getPoint();
      
      int x = Math.min(startPt.x, endPt.x);
      int y  = Math.min(startPt.y, endPt.y);
      int w = Math.abs(startPt.x - endPt.x);
      int h = Math.abs(startPt.y - endPt.y);
      updatePictureBoundingBox(frameNumber,x,y,w,h);
      currentBoundingBox = new int[] {x,y,w,h};
      startPt = mEvt.getPoint();
   }

   @Override
   public void mousePressed(MouseEvent mEvt) {
      startPt = mEvt.getPoint();
   }
	    
	    
	/** Read Image RGB
	 *  Reads the image of given width and height at the given imgPath into the provided BufferedImage.
	 */
	private  void readImageRGBBox(int width, int height, String imgPath, BufferedImage img, int startX,int startY, int w, int h)
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
					//(110,110) w:10 h:10
					if(x >=startX && y == startY  && x<=startX+w ) {
						img.setRGB(x,y, 0xff000000 | ((255 & 0xff) << 16) | ((0 & 0xff) << 8) | (0 & 0xff));
						ind++;
						continue;
					}
					if(x >=startX && y == startY+h  && x<=startX+w ) {
						img.setRGB(x,y, 0xff000000 | ((255 & 0xff) << 16) | ((0 & 0xff) << 8) | (0 & 0xff));
						ind++;
						continue;
					}
					if(x ==startX && y >= startY  && y<=startY+h ) {
						img.setRGB(x,y, 0xff000000 | ((255 & 0xff) << 16) | ((0 & 0xff) << 8) | (0 & 0xff));
						ind++;
						continue;
					}
					if(x ==startX+w && y >= startY  && y<=startY+h ) {
						img.setRGB(x,y, 0xff000000 | ((255 & 0xff) << 16) | ((0 & 0xff) << 8) | (0 & 0xff));
						ind++;
						continue;
					}
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
   

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		if(hasVideo) {
			JFrame frame = new JFrame();
			JOptionPane.showMessageDialog(
	                frame, "Please drag mouse to select area of interest of the primary video");
		}
		hasVideo = false;
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
	public void updatePath(String path) {
		videoPath = path;
		framePath = videoPath+"/"+videoPath;
		updatePicture(frameNumber);
		hasVideo=true;
		
	}
	public void sethasVideo() {
		hasVideo = false;
	}
	public int[] getCurrentBoundingBox() {
	      if(currentBoundingBox == null) {
	    	  return null;
	      }
	      return currentBoundingBox;
	}

}
