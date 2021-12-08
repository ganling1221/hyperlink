package audio;

import java.io.IOException;
import java.io.InputStream;
import java.util.Timer;
import java.io.BufferedInputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;


import javax.sound.sampled.DataLine.Info;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;

/**
 * 
 * <Replace this with a short description of the class.>
 * 
 * @author Giulio
 */
public class PlaySound {

    private InputStream waveStream;

    private final int EXTERNAL_BUFFER_SIZE = 524288; // 128Kb
    
    public SourceDataLine dataLine = null;
    public AudioInputStream audioInputStream = null;
    public AudioFormat audioFormat;
    public int currentFramePos;
    public int readBytes;
    byte[] audioBuffer;
    byte[] tempBuffer;
    InputStream bufferedIn;
    
    boolean reset;
    boolean stopped;
    boolean paused;


    /**
     * CONSTRUCTOR
    * @throws PlayWaveException 
     */
    public PlaySound(InputStream waveStream) throws PlayWaveException {
        this.waveStream = waveStream;
    }                                                            

    public void play() throws PlayWaveException {                

        //AudioInputStream audioInputStream = null;
        try {
            bufferedIn = new BufferedInputStream(this.waveStream); // new
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

        // Starts the music 
        dataLine.start();
        
        readBytes = 0;
        audioBuffer = new byte[this.EXTERNAL_BUFFER_SIZE];
        
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
            //dataLine.drain();
            //dataLine.close();

        }
        
    }
    
    // Start playing audio from dataline where it last 'stopped'
    public void start() {
       stopped = false;
       paused = false;
       
       dataLine.start();
    }
    
    // Used to stop playing audio from dataline, then drain the rest of the data from 
    // dataline before closing it.
    public void stop() {
       stopped = true;
       
       dataLine.stop();
       dataLine.drain();
       dataLine.close();
    }
    
    // Pause ('stop') the audio from dataline;
    // Attempted to mark current audio frame position of when 'pause' is pressed
    // so that audioInputStream can be reset to that frame position
    // -- Audio should start playing from this marked position if done correctly
    public void pause() {
       paused = true;
       
       currentFramePos = getCurrentAudioPos();
       //audioInputStream.mark(dataLine.getFramePosition());
       
       dataLine.stop();
       
       /*
       try
      {
         audioInputStream.reset();
      } catch (IOException e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      */
       
    }
    
    // Resume ('start') playing from dataline from where we last paused ('stopped')
    // Attempted to reset audioInputStream to marked position from 'pause()'
    public void resume() throws IOException, UnsupportedAudioFileException {

       paused = false;

       //audioInputStream.reset();
       
       dataLine.start();
       
       readBytes = 0;
       tempBuffer = new byte[this.EXTERNAL_BUFFER_SIZE];
       
       while (readBytes != -1 && currentFramePos < audioInputStream.getFrameLength()) {
          readBytes = audioInputStream.read(tempBuffer, 0,
          tempBuffer.length);

          if (readBytes >= 0){

             dataLine.write(tempBuffer, 0, readBytes);
          }
             
       }
       
    }
    
    // To be implemented for REPLAY button
    public void reset() {
       reset = true;
       dataLine.stop();
       
       try
      {
         audioInputStream.close();
      } catch (IOException e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
       
    }

    // get current frame position in audio file
    public int getCurrentAudioPos() {
       return dataLine.getFramePosition();
    }
    
    // get total audio length
    public long getAudioLength() {
       return audioInputStream.getFrameLength();
    }
    
    
}