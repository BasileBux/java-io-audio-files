package ch.heigvd.dai.ios;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.util.Date;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

public class AudioChangeSpeed implements SpeedModifiable {

    @Override
    public void changeSpeed(String inputFilename, float intensity) {
        System.out.println("Playback Rate: " + intensity);

        // Determine the output filename
        String outputFilename = getOutputFilename(inputFilename, intensity);

        // open input file
        try (InputStream audioSrc = new FileInputStream(inputFilename);
             InputStream bufferedAudioSrc = new BufferedInputStream(audioSrc);
             AudioInputStream ais = AudioSystem.getAudioInputStream(bufferedAudioSrc)) {
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
            int newLength = (int) (b1.length / intensity);
            byte[] b2 = new byte[newLength];
            for (int ii = 0; ii < newLength / frameSize; ii++) {
                for (int jj = 0; jj < frameSize; jj++) {
                    b2[(ii * frameSize) + jj] = b1[(int) (ii * frameSize * intensity) + jj];
                }
            }
            System.out.println("End sub-sample: \t" + new Date());

            ByteArrayInputStream bais = new ByteArrayInputStream(b2);
            AudioInputStream aisAccelerated = new AudioInputStream(bais, af, b2.length);

            // save the modified audio to a file
            try (OutputStream outputStream = new FileOutputStream(outputFilename)) {
                AudioSystem.write(aisAccelerated, AudioFileFormat.Type.WAVE, outputStream);
            }

        } catch (Exception e) {
            System.out.println("error to open audio input : " + e.getMessage());
        }
    }

    private String getOutputFilename(String inputFilename, float intensity) {
        String filename = Paths.get(inputFilename).getFileName().toString();
        int extensionIndex = filename.lastIndexOf(".");
        String fileExtension = filename.substring(extensionIndex);
        String fileNameWithoutExtension = filename.substring(0, extensionIndex);
        return fileNameWithoutExtension + "_x" + intensity + fileExtension;
    }
}
