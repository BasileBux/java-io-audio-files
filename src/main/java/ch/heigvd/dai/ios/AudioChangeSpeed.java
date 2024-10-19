package ch.heigvd.dai.ios;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Date;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.JOptionPane;

// Solution from: https://stackoverflow.com/a/5762444
public class AudioChangeSpeed implements SpeedModifiable {

    @Override
    public void changeSpeed(String filename, float intensity) {
        System.out.println("Playback Rate: " + intensity);

        // open file
        try (InputStream audioSrc = new FileInputStream(filename); InputStream bufferedAudioSrc = new BufferedInputStream(audioSrc); AudioInputStream ais = AudioSystem.getAudioInputStream(bufferedAudioSrc);) {
            AudioFormat af = ais.getFormat();

            int frameSize = af.getFrameSize();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] b = new byte[2 ^ 16];
            int read = 1;
            while (read > -1) {
                read = ais.read(b);
                if (read > 0) {
                    baos.write(b, 0, read);
                }
            }
            System.out.println("End entire: \t" + new Date());

            byte[] b1 = baos.toByteArray();
            byte[] b2 = new byte[b1.length / (int)intensity];
            for (int ii = 0; ii < b2.length / frameSize; ii++) {
                for (int jj = 0; jj < frameSize; jj++) {
                    b2[(ii * frameSize) + jj] = b1[(ii * frameSize * (int)intensity) + jj];
                }
            }
            System.out.println("End sub-sample: \t" + new Date());

            ByteArrayInputStream bais = new ByteArrayInputStream(b2);
            AudioInputStream aisAccelerated
                    = new AudioInputStream(bais, af, b2.length);
            Clip clip = AudioSystem.getClip();
            clip.open(aisAccelerated);
            clip.loop(2 * (int)intensity);
            clip.start();

            JOptionPane.showMessageDialog(null, "Exit?");

        } catch (Exception e) {
            System.out.println("error to open audio input : " + e.getMessage());
        }

    }
}
