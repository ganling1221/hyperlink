package audio;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javax.swing.Timer;

/**
 * plays a wave file using PlaySound class
 * 
 * @author Giulio
 */
public class PlayWaveFile {
   
   public PlaySound playSound;

    /**
     * <Replace this with one clearly defined responsibility this method does.>
     * 
     * @param args
     *            the name of the wave file to play
    * @throws PlayWaveException 
     */
    public PlayWaveFile(String audioFileName) throws PlayWaveException {
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
	
	try
   {
      Thread.sleep(1000);
   } catch (InterruptedException e)
   {
      // TODO Auto-generated catch block
      e.printStackTrace();
   }

    }

}
