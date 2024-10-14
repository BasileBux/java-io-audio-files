package ch.heigvd.dai.ios;


import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;

import static javax.sound.sampled.AudioFileFormat.Type.WAVE; //comment utiliser wave ?
//comment faire une méthode pour choisir le format ? On l'impose ?

public class Player implements Readable { //extends Readable ?


    @Override
    public void read(String filename) {
        //a changer, dans le but de test...
        String filename2 = "test.wav";

        try (InputStream s = new FileInputStream(filename2);
             AudioInputStream ais = AudioSystem.getAudioInputStream(s)){
            playSound(ais);
            //AudioInputStream audioInputStream = new AudioInputStream(s, WAVE, 256);
        } catch (Exception e) {
            System.out.println("error : " + e.getMessage());
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



