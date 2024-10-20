package ch.heigvd.dai.ios;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * This class implements the Playable interface and provides functionality
 * to play audio files.
 */
public class AudioPlayback implements Playable {

    @Override
    /**
     * Plays the audio file specified by the given filename.
     *
     * @param filename The path to the audio file to be played.
     */
    public void play(String filename) {

        try (InputStream audioSrc = new FileInputStream(filename);
             InputStream bufferedAudioSrc = new BufferedInputStream(audioSrc);
             AudioInputStream ais = AudioSystem.getAudioInputStream(bufferedAudioSrc)) {

            playSound(ais);

        } catch (Exception e) {
            System.out.println("error to open audio input : " + e.getMessage());
        }
    }

    /**
     * Plays the sound from the given AudioInputStream.
     *
     * @param ais The AudioInputStream to play.
     */
    private void playSound(AudioInputStream ais) {
        try(Clip clip = AudioSystem.getClip()){

            clip.open(ais);
            clip.setFramePosition(0);
            clip.start();

            // First loop : wait the audio start playing
            //second loop : keep the sound playing while it's running
            // made with help of chatgpt
            while (!clip.isRunning())
                Thread.sleep(10);
            while (clip.isRunning())
                Thread.sleep(10);
        } catch(Exception ex) {
            System.out.println("Error with playing sound.");
            ex.printStackTrace();
        }
    }
}
