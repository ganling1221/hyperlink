package hyperlink;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import javax.swing.event.*;

import audio.PlayWaveFile;
import audio.PlaySound;
import audio.PlayWaveException;

/*
 * SliderDemo.java requires all the files in the images/doggy
 * directory.
 */
public class VideoPlayer extends JPanel
                        implements ActionListener,
                                   WindowListener,
                                   ChangeListener,
                                   MouseListener {
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
    static final int FPS_INIT = 30;    //initial frames per second
    String video1Path;
    int frameNumber;
    int NUM_FRAMES = 9000;
    static int width = 352;
    static int height = 288;
    ImageIcon[] images = new ImageIcon[NUM_FRAMES];
    int delay;
    Timer timer;
    boolean frozen = false;

    //This label uses ImageIcon to show the doggy pictures.
    JLabel picture;
    

    public static String[] inputArgs;

    private static PlaySound soundtrack;
    private static PlayWaveFile wavFile;
    int buttonState = -1;    // 0 = PLAY; 1 = PAUSE; 2 = STOP; 3 = REPLAY
    Thread videoThread = null;
    Thread audioThread = null;
    boolean paused;
    boolean reset;
    static FileInputStream inputStream;
    int currentBytes;


    public VideoPlayer(String path, int num) {
        video1Path = path;
        setLayout(new BorderLayout());
        frameNumber = num;
        delay = 1000 / FPS_INIT;


        audioThread = new Thread(new audioFile());
        videoThread = new Thread(new videoFile());


        //Create the PLAY button.
        JButton play = new JButton("PLAY");
        play.addActionListener(this);
        play.addActionListener(new ActionListener() { 
              public void actionPerformed(ActionEvent e) { 
                 setButtonState(0);
                 
                 videoThread.run();
                 
                 if (getButtonState() == 0 && paused == true) {
                    try
                  {
                     playSound();
                  } catch (IOException e1)
                  {
                     // TODO Auto-generated catch block
                     e1.printStackTrace();
                  } catch (UnsupportedAudioFileException e1)
                  {
                     // TODO Auto-generated catch block
                     e1.printStackTrace();
                  }
                 } 
                 
                 if (getButtonState() == 0 && reset == true) {

                    startAnimation();
                    
                    try
                  {
                     playSound();
                  } catch (IOException e1)
                  {
                     // TODO Auto-generated catch block
                     e1.printStackTrace();
                  } catch (UnsupportedAudioFileException e1)
                  {
                     // TODO Auto-generated catch block
                     e1.printStackTrace();
                  }
                 }
                 paused = false;
                 reset = false;
              } 
            } );
        
        
        // Create the PAUSE button.
        JButton pause = new JButton("PAUSE");
        pause.addActionListener(this);
        pause.addActionListener(new ActionListener() { 
              public void actionPerformed(ActionEvent e) { 
                setButtonState(1);

                try
               {
                  stopAnimation();
                  stopSound();
                  paused = true;
                  
               } catch (InterruptedException e1)
               {
                  // TODO Auto-generated catch block
                  e1.printStackTrace();
               }
              } 
            } );
        
        
      //Create the STOP button.
        JButton stop = new JButton("STOP");
        stop.addActionListener(this);
        stop.addActionListener(new ActionListener() { 
              public void actionPerformed(ActionEvent e) { 
                setButtonState(2);
                
                try
               {
                  stopAnimation();
                  stopSound();
               } catch (InterruptedException e1)
               {
                  // TODO Auto-generated catch block
                  e1.printStackTrace();
               }
              } 
            } );
        
        
      //Create the REPLAY button.
        JButton replay = new JButton("REPLAY");
        replay.addActionListener(this);
        replay.addActionListener(new ActionListener() { 
              public void actionPerformed(ActionEvent e) { 
                setButtonState(3);
                reset = true;

                resetAnimation();
                resetSound();
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
        buttonsPanel.add(pause);    
        buttonsPanel.add(stop);
        buttonsPanel.add(replay);   
        add(buttonsPanel,BorderLayout.NORTH);

        add(picture,BorderLayout.CENTER);
        picture.addMouseListener(this);
        setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        //Set up a timer that calls this object's action handler.
        //timer = new Timer(delay, this);
        timer = new Timer(FPS_MAX, this);    // Audio/video sync for AIFilmOne, AIFilmTwo,
        timer.setInitialDelay(delay * 7); //We pause animation twice per cycle
                                          //by restarting the timer
        timer.setCoalesce(true);
    }

    public void playPause() throws IOException, UnsupportedAudioFileException {
       if (getButtonState() == 0 && paused) {
          startAnimation();

          wavFile.playSound.readBytes = currentBytes;
          wavFile.playSound.resume();
       }
       
    }
    
    /** Add a listener for window events. */
    void addWindowListener(Window w) {
        w.addWindowListener(this);
    }

    //React to window events.
    public void windowIconified(WindowEvent e) {
        try
      {
         stopAnimation();
         stopSound();   // added
      } catch (InterruptedException e1)
      {
         // TODO Auto-generated catch block
         e1.printStackTrace();
      }
    }
    public void windowDeiconified(WindowEvent e) {
        startAnimation();
        try
      {
         playSound();
      } catch (IOException e1)
      {
         // TODO Auto-generated catch block
         e1.printStackTrace();
      } catch (UnsupportedAudioFileException e1)
      {
         // TODO Auto-generated catch block
         e1.printStackTrace();
      }    //added
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
      if(clickedOnTracedObject(x,y)) {
          //determine which object 
          //based on the object, read the information in from metafile 
        Pattern pattern = Pattern.compile("path:", Pattern.CASE_INSENSITIVE);
        Pattern pattern2 = Pattern.compile("subFrame:", Pattern.CASE_INSENSITIVE);

          try(BufferedReader br = new BufferedReader(new FileReader("metadata.txt"))) {
                StringBuilder sb = new StringBuilder();
                String line = br.readLine();
                while (line != null) {
                    //regex match the prefex path:
                    Matcher matcher = pattern.matcher(line);
                    Matcher matcher2 = pattern2.matcher(line);

                    boolean matchFound = matcher.find();
                    boolean matchFound2 = matcher2.find();
                    if(matchFound) {
                        sb.append(line.split(":")[1]);
                    }
                    if(matchFound2) {
                        num = Integer.valueOf(line.split(":")[1]);
                        break;
                    }
                    line = br.readLine();
                }
                 path = sb.toString();
            } catch (FileNotFoundException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
      }
      createAndShowGUI(path,num);
    }

    public boolean clickedOnTracedObject(int x, int y) {
        return true;
    }
    
    // Start playing the audio file
    public void playSound() throws IOException, UnsupportedAudioFileException {
       if (reset) {
          try
         {
            wavFile.playSound.play();
         } catch (PlayWaveException e)
         {
            // TODO Auto-generated catch block
            e.printStackTrace();
         }
          
       }
       
       if (paused) {
          
          wavFile.playSound.resume();
          audioThread.interrupt();
          
       }
       else {
       
          audioThread.start();
       
       }
       
    }
    
    // Start playing video/images
    public void startAnimation() {

       timer.start();
       frozen = false;

    }
    
    public void stopSound() {
       // PAUSE button enabled
       if (getButtonState() == 1) {
          wavFile.playSound.pause(); 
       }
       
       // STOP button enabled
       if (getButtonState() == 2) {
          wavFile.playSound.stop(); 
       }
       
       // REPLAY button enabled
       if (getButtonState() == 3) {
          wavFile.playSound.reset();
       }
       
       audioThread.interrupt();
    }

    public void stopAnimation() throws InterruptedException {
        //Stop the animating thread.
       timer.stop();
       frozen = true;
       
       videoThread.interrupt();
       
    }
    
    public void resetAnimation() {
       timer.restart();
       frameNumber = 1;
       updatePicture(frameNumber);
       
       timer.stop();
       frozen = true;
       videoThread.interrupt(); 
    }
    
    public void resetSound() {
       wavFile.playSound.reset();
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
        ImageIcon image = createImageIcon(video1Path+ formatted + ".rgb");
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
        JFrame frame = new JFrame("SliderDemo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        VideoPlayer animator = new VideoPlayer(arg, num);
       
        //Add content to the window.
        frame.add(animator, BorderLayout.CENTER);

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    public void setInputArgs(String[] input) {
       inputArgs = input;
    }

    
    public static void main(String[] args) {

       try
      {
         wavFile = new PlayWaveFile(args[1]);
      } catch (PlayWaveException e1)
      {
         // TODO Auto-generated catch block
         e1.printStackTrace();
      }

       createAndShowGUI(args[0], 1);

    }

    @Override
    public void stateChanged(ChangeEvent e) {
        // TODO Auto-generated method stub
        
    }
   
    
   public int getButtonState() {
      return buttonState;
   }
   
   private void setButtonState(int state) {
      buttonState = state;
   }

   
   public class videoFile implements Runnable {
      public void run() {
         // TODO Auto-generated method stub
         // PLAY button enabled
         if (getButtonState() == 0) {
            startAnimation();
            try
            {
               playSound();
            } catch (IOException e)
            {
               // TODO Auto-generated catch block
               e.printStackTrace();
            } catch (UnsupportedAudioFileException e)
            {
               // TODO Auto-generated catch block
               e.printStackTrace();
            }
         }
         
         // PLAY button enabled after PAUSE
         if (getButtonState() == 0 && paused == true) {
            startAnimation();
            
            try
            {
               playSound();
            } catch (IOException e)
            {
               // TODO Auto-generated catch block
               e.printStackTrace();
            } catch (UnsupportedAudioFileException e)
            {
               // TODO Auto-generated catch block
               e.printStackTrace();
            }
         }
         
         // STOP button enabled
         if (getButtonState() == 2) {
            try
            {
               stopAnimation();
            } catch (InterruptedException e)
            {
               // TODO Auto-generated catch block
               e.printStackTrace();
            }
            stopSound();
         }
      }
   }

   public class audioFile implements Runnable {
      @SuppressWarnings("static-access")
      public void run() {
         
         // Initial PLAY button pressed
         if (getButtonState() == 0 && paused == false) {
            try
         {
            wavFile.playSound.play();   // ADDED
            //soundtrack.play();    WORKINGGG
         } catch (PlayWaveException e)
         {
            // TODO Auto-generated catch block
            e.printStackTrace();
         }
         }
         
      }
   }

}