package audio;

import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedInputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

import hyperlink.VideoPlayer;

import javax.sound.sampled.DataLine.Info;

/**
 * 
 * <Replace this with a short description of the class.>
 * 
 * @author Giulio
 */
public class PlaySound implements Runnable {

    private InputStream waveStream;

    //private final int EXTERNAL_BUFFER_SIZE = 650000; // 128Kb
    private final int EXTERNAL_BUFFER_SIZE = 524288; // 128Kb
    
    public SourceDataLine dataLine = null;
    public AudioInputStream audioInputStream = null;
    public AudioFormat audioFormat;

    /**
     * CONSTRUCTOR
     */
    public PlaySound(InputStream waveStream) {
		this.waveStream = waveStream;
    }

    public void play() throws PlayWaveException {

		//AudioInputStream audioInputStream = null;
		try {
			InputStream bufferedIn = new BufferedInputStream(this.waveStream); // new
		    audioInputStream = AudioSystem.getAudioInputStream(bufferedIn);
		} catch (UnsupportedAudioFileException e1) {
		    throw new PlayWaveException(e1);
		} catch (IOException e1) {
		    throw new PlayWaveException(e1);
		}

		// Obtain the information about the AudioInputStream
		audioFormat = audioInputStream.getFormat();
		//AudioFormat audioFormat = audioInputStream.getFormat();
		Info info = new Info(SourceDataLine.class, audioFormat);

		// opens the audio channel
		//SourceDataLine dataLine = null;
		try {
		    dataLine = (SourceDataLine) AudioSystem.getLine(info);
		    dataLine.open(audioFormat, this.EXTERNAL_BUFFER_SIZE);
		} catch (LineUnavailableException e1) {
		    throw new PlayWaveException(e1);
		}

		// Starts the music :P
		dataLine.start();

		int readBytes = 0;
		byte[] audioBuffer = new byte[this.EXTERNAL_BUFFER_SIZE];

		try {
		    while (readBytes != -1) {
			readBytes = audioInputStream.read(audioBuffer, 0,
				audioBuffer.length);
			if (readBytes >= 0){
			    dataLine.write(audioBuffer, 0, readBytes);
			}
		    }
		} catch (IOException e1) {
		    throw new PlayWaveException(e1);
		} finally {
		    // plays what's left and and closes the audioChannel
		    dataLine.drain();
		    dataLine.close();
		}

    }
    
    public void start() {
       dataLine.start();
    }
    
    public void stop() {
       //dataLine.drain();
       dataLine.close();
    }
    
    public void resume() {
       //dataLine.close();
       dataLine.start();
    }
    
    public void pause() {
       dataLine.stop();
       
       if (VideoPlayer.currButton == 10) {
          dataLine.start();
       }
    }

    // get current frame position in audio file
    public int getCurrentAudioPos() {
       return dataLine.getFramePosition();
    }
    
    public long getAudioLength() {
       return audioInputStream.getFrameLength();
    }
    
    public float getAudioFrameRate() {
       return audioFormat.getFrameRate();
    }
    
   public void run() {

      try
      {
         Thread.sleep(100000);
         play();
      } catch (PlayWaveException e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      } catch (InterruptedException e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      
   }
}