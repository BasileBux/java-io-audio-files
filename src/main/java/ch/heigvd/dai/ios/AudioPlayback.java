package ch.heigvd.dai.ios;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;

public class AudioPlayback implements Playable {

    @Override
    public void play(String filename) {

        try (InputStream audioSrc = new FileInputStream(filename);
             InputStream bufferedAudioSrc = new BufferedInputStream(audioSrc);
             AudioInputStream ais = AudioSystem.getAudioInputStream(bufferedAudioSrc)) {

            playSound(ais);

        } catch (Exception e) {
            System.out.println("error to open audio input : " + e.getMessage());
        }
    }

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
