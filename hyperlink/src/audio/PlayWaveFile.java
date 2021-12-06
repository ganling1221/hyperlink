package audio;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * plays a wave file using PlaySound class
 * 
 * @author Giulio
 */
public class PlayWaveFile {
   
   PlaySound playSound;

    /**
     * <Replace this with one clearly defined responsibility this method does.>
     * 
     * @param args
     *            the name of the wave file to play
     */
    public PlayWaveFile(String audioFileName) {
    //public static void main(String[] args) {

	// get the command line parameters
    if (audioFileName.length() < 1) {
	//if (args.length < 1) {
	    System.err.println("usage: java -jar PlayWaveFile.jar [filename]");
	    return;
	}
	String filename = audioFileName;
	//String filename = args[0];

	// opens the inputStream
	FileInputStream inputStream;
	try {
	    inputStream = new FileInputStream(filename);
	} catch (FileNotFoundException e) {
	    e.printStackTrace();
	    return;
	}

	// initializes the playSound Object
	//PlaySound playSound = new PlaySound(inputStream);
	playSound = new PlaySound(inputStream);
/*
	// plays the sound
	try {
	    playSound.play();
	} catch (PlayWaveException e) {
	    e.printStackTrace();
	    return;
	}
	*/
    }

    public void playWav() throws PlayWaveException {
       playSound.play();
       /*
       try
      {
         playSound.play();
      } catch (PlayWaveException e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      */
    }
   
    public void stopWav() {
       playSound.stop();
       
    }
    
    public void pauseWav() {
       playSound.pause();
    }
    
    public void resumeWav() {
       playSound.resume();
    }

}
