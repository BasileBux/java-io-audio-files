package ch.heigvd.dai.ios;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Paths;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

/**
 * This class implements the SpeedModifiable interface and provides functionality
 * to change the speed of an audio file.
 */
public class AudioChangeSpeed implements SpeedModifiable {

    @Override
    /**
     * Changes the speed of the specified audio file by the given intensity.
     *
     * @param inputFilename The path to the input audio file.
     * @param intensity The intensity of speed change. Values greater than 1.0 speed up the audio,
     *                  values between 0.0 and 1.0 slow it down.
     */
    public void changeSpeed(String inputFilename, float intensity) {
        if (intensity < 0.0) {
            System.out.println("Error: the intensity parameter should be positive.");
            return;
        }
        String outputFilename = getOutputFilename(inputFilename, intensity);

        // Solution modified: https://stackoverflow.com/a/5762444
        try (InputStream audioSrc = new FileInputStream(inputFilename); BufferedInputStream bufferedAudioSrc = new BufferedInputStream(audioSrc)) {

            // Mark the stream with a large enough buffer size
            bufferedAudioSrc.mark(Integer.MAX_VALUE);

            // Read the format information
            AudioFileFormat aff = AudioSystem.getAudioFileFormat(bufferedAudioSrc);
            AudioFileFormat.Type fileType = aff.getType();

            bufferedAudioSrc.reset();

            // get the AudioInputStream
            try (AudioInputStream ais = AudioSystem.getAudioInputStream(bufferedAudioSrc)) {
                AudioFormat af = ais.getFormat();
                int frameSize = af.getFrameSize();

                // put audio file in a byte array (b)
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] b = new byte[2 ^ 16];
                int read;
                while ((read = ais.read(b)) > -1) {
                    baos.write(b, 0, read);
                }

                byte[] b1 = baos.toByteArray();

                // change speed by calculating the audio frames and filling a new byte array (b2)
                byte[] b2;
                if (intensity == 1.0) {
                    System.out.println("No speed change needed.");
                    return;
                } else if (intensity > 1.0) {
                    // speed up
                    int newLength = (int) (b1.length / intensity);
                    b2 = new byte[newLength];
                    for (int ii = 0; ii < newLength / frameSize; ii++) {
                        for (int jj = 0; jj < frameSize; jj++) {
                            b2[(ii * frameSize) + jj] = b1[(int) (ii * frameSize * intensity) + jj];
                        }
                    }
                } else {
                    // slow down
                    int newLength = (int) (b1.length / intensity);
                    b2 = new byte[newLength];
                    for (int ii = 0; ii < newLength / frameSize; ii++) {
                        for (int jj = 0; jj < frameSize; jj++) {
                            int origIndex = (int) (ii * frameSize * intensity) + jj;
                            if (origIndex < b1.length) {
                                b2[(ii * frameSize) + jj] = b1[origIndex];
                            } else {
                                b2[(ii * frameSize) + jj] = 0;
                            }
                        }
                    }
                }

                ByteArrayInputStream bais = new ByteArrayInputStream(b2);
                AudioInputStream aisAccelerated = new AudioInputStream(bais, af, b2.length);

                try (OutputStream outputStream = new FileOutputStream(outputFilename)) {
                    /* Here we use the AudioSystem class to abstract the file writing because of the metadata. This could have been done manually. But for a purpose of time and complexity, we decided not to reinvent the wheel. */
                    AudioSystem.write(aisAccelerated, fileType, outputStream);
                } catch (Exception e) {
                    System.out.println("error to write output file: " + e.getMessage());
                }

            } catch (Exception e) {
                System.out.println("error to open audio input : " + e.getMessage());
            }

        } catch (Exception e) {
            System.out.println("error to open audio input : " + e.getMessage());
        }

    }

    // Written with help from chatGPT
    /**
     * Generates a new output filename based on the input filename and speed intensity.
     *
     * @param inputFilename The original input filename.
     * @param intensity The speed change intensity.
     * @return A new filename with speed information appended.
     */
    private String getOutputFilename(String inputFilename, float intensity) {
        String filename = Paths.get(inputFilename).getFileName().toString();
        int extensionIndex = filename.lastIndexOf(".");
        String fileExtension = filename.substring(extensionIndex);
        String fileNameWithoutExtension = filename.substring(0, extensionIndex);
        return fileNameWithoutExtension + "_x" + intensity + fileExtension;
    }
}
