package ch.heigvd.dai.ios;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.*;
import java.nio.file.Paths;

/**
 * This class implements the VolumeModifiable interface and provides functionality
 * to change the volume of an audio file.
 */
public class AudioChangeVolume implements VolumeModifiable {

    private static final int BUFFER_SIZE = 1024; // Buffer size for reading audio data


    @Override
    /**
     * Changes the volume of the specified audio file by the given intensity.
     *
     * @param filename The path to the input audio file.
     * @param volumeIntensity The intensity of volume change. Positive values increase volume, negative values decrease it.
     */
    public void changeVolume(String filename, float volumeIntensity) {

        try (InputStream audioSrc = new FileInputStream(filename);
             InputStream bufferedAudioSrc = new BufferedInputStream(audioSrc);
             AudioInputStream ais = AudioSystem.getAudioInputStream(bufferedAudioSrc);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            //format information
            AudioFormat format = ais.getFormat();
            AudioFileFormat aff = AudioSystem.getAudioFileFormat(ais);
            AudioFileFormat.Type fileType = aff.getType() ;

            processAudio(ais, baos, volumeIntensity);

            String newOutputFilename = getOutputFilename(filename, volumeIntensity);

            byte[] adjustedAudio = baos.toByteArray();
            File outputFile = new File(newOutputFilename);

            try ( ByteArrayInputStream bais = new ByteArrayInputStream(adjustedAudio);
                  AudioInputStream adjustedAudioStream = new AudioInputStream(bais, format, adjustedAudio.length / format.getFrameSize())) {


                AudioSystem.write(adjustedAudioStream, fileType, outputFile);
            }


            System.out.println("Audio adjusted...");

        } catch (Exception e) {
            System.out.println("error to open audio input : " + e.getMessage());
        }
    }

    /**
     * Processes the audio data, adjusting the volume based on the given intensity.
     *
     * @param ais The input AudioInputStream.
     * @param baos The output ByteArrayOutputStream.
     * @param volumeIntensity The intensity of volume change.
     * @throws IOException If an I/O error occurs.
     */
    private void processAudio(AudioInputStream ais, ByteArrayOutputStream baos, float volumeIntensity) throws IOException {
        byte[] buffer = new byte[BUFFER_SIZE];
        int bytesRead;

        while ((bytesRead = ais.read(buffer)) != -1) { //read until there is no more data to read
            for (int i = 0; i < bytesRead; i += 2) { //increase of two because we work with 16-bit audio (2 bytes per sample)

                short sample = (short) ((buffer[i] & 0xFF) | (buffer[i + 1] << 8)); //adjust the sample into a 16-bit short

                //volumeIntensity of 1 will double the volume, -1 will reduce it by half
                int sign = volumeIntensity >= 0 ? 1 : -1;
                sample = (short) (sample * (Math.pow(Math.abs(volumeIntensity) + 1, sign)));

                //Write the adjusted sample back to the buffer (use two index because of the 16-bit)
                buffer[i] = (byte) (sample & 0xFF);
                buffer[i + 1] = (byte) ((sample >> 8) & 0xFF);
            }

            baos.write(buffer, 0, bytesRead);
        }
    }

    // Written with help from chatGPT

    /**
     * Generates a new output filename based on the input filename and volume intensity.
     *
     * @param inputFilename The original input filename.
     * @param intensity The volume change intensity.
     * @return A new filename with volume information appended.
     */
    private String getOutputFilename(String inputFilename, float intensity) {
        String filename = Paths.get(inputFilename).getFileName().toString();
        int extensionIndex = filename.lastIndexOf(".");
        String fileExtension = filename.substring(extensionIndex);
        String fileNameWithoutExtension = filename.substring(0, extensionIndex);
        return fileNameWithoutExtension + "_at_" + intensity + "_volume" + fileExtension;
    }
}
