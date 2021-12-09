package audio;

import java.io.IOException;
import java.io.File;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;


/**
 * 
 * Used to control the audio playback with functionalities
 * PLAY, PAUSE, STOP, REPLAY.
 */
public class PlayAudioClip {

    private AudioInputStream audioInputStream = null;

    private Clip audio;
    private Long currentFrame;
    private String filePath;


    /**
     * CONSTRUCTOR
     */
    public PlayAudioClip(String audioFilePath) throws IOException, LineUnavailableException, 
    UnsupportedAudioFileException {
       
       this.filePath = audioFilePath;
       audioInputStream = AudioSystem.getAudioInputStream(new File(filePath).getAbsoluteFile());
       
       audio = AudioSystem.getClip();
       audio.open(audioInputStream);
    }
    
    public void playAudioClip() {
       audio.start();
    }
    
    public void pauseAudioClip() {
       this.currentFrame = this.audio.getMicrosecondPosition();
       audio.stop(); 
    }
    
    public void resumeAudioClip() throws IOException, LineUnavailableException, 
    UnsupportedAudioFileException {
       
       audio.close();
       
       resetAudioClipStream();
       
       audio.setMicrosecondPosition(currentFrame);
       
       this.playAudioClip();
    }
    
    public void stopAudioClip() throws IOException, LineUnavailableException, 
    UnsupportedAudioFileException {
       
       currentFrame = 0L;
       
       audio.stop();
       audio.close();
    }
    
    public void restartAudioClip() throws IOException, LineUnavailableException,
    UnsupportedAudioFileException {
       
       audio.stop();
       audio.close();
       
       resetAudioClipStream();
       
       currentFrame = 0L;
       
       audio.setMicrosecondPosition(0);
       
       this.playAudioClip();
    }
    
    public void resetAudioClipStream() throws UnsupportedAudioFileException, IOException,
    LineUnavailableException {
       audioInputStream = AudioSystem.getAudioInputStream(new File(filePath).getAbsoluteFile());
       audio.open(audioInputStream);
    }
 
}