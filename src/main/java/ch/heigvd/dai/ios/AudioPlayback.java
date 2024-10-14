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




        try (InputStream audioSrc = getClass().getResourceAsStream(filename);
             InputStream bufferedAudioSrc = new BufferedInputStream(audioSrc);
             AudioInputStream ais = AudioSystem.getAudioInputStream(bufferedAudioSrc)){

            playSound(ais);
            //AudioInputStream audioInputStream = new AudioInputStream(s, WAVE, 256);
        } catch (Exception e) {
            System.out.println("error to open audio input : " + e.getMessage());
        }
    }



    /**
     * Classe qui joue un son depuis un fichier ouvert en AudioInputStram
     * from https://stackoverflow.com/questions/35051956/intellij-feature-not-supported-at-this-language-level-i-cant-compile
     * @param ais
     */
    private void playSound(AudioInputStream ais) {
        try {
            Clip clip = AudioSystem.getClip();
            clip.open(ais);
            clip.start();
        } catch(Exception ex) {
            System.out.println("Error with playing sound.");
            ex.printStackTrace();
        }
    }

}
