package ch.heigvd.dai.ios;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.*;

public class AudioChangeVolume implements VolumeModifiable {

    private static final int BUFFER_SIZE = 1024; // Buffer size for reading audio data


    @Override
    public void changeVolume(String filename, float volumeIntensity) {
        //todo : manage volumeIntensity values through the CLI !

        try (InputStream audioSrc = new FileInputStream(filename);
             InputStream bufferedAudioSrc = new BufferedInputStream(audioSrc);
             AudioInputStream ais = AudioSystem.getAudioInputStream(bufferedAudioSrc);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            AudioFormat format = ais.getFormat();

            processAudio(ais, baos, volumeIntensity);
            writeAdjustedAudio(baos, format, filename);

            System.out.println("Audio adjusted...");

        } catch (Exception e) {
            System.out.println("error to open audio input : " + e.getMessage());
        }
    }

    private void processAudio(AudioInputStream ais, ByteArrayOutputStream baos, float volumeIntensity) throws IOException {
        byte[] buffer = new byte[BUFFER_SIZE];
        int bytesRead;

        while ((bytesRead = ais.read(buffer)) != -1) { //read until there is no more data to read
            for (int i = 0; i < bytesRead; i += 2) { //increase of two because we work with 16-bit audio (2 byte per sample)

                // adjust the sample into a 16-bit short
                short sample = (short) ((buffer[i] & 0xFF) | (buffer[i + 1] << 8));
                sample = (short) (sample * volumeIntensity);

                // Write the adjusted sample back to the buffer (use two index because of the 16-bit)
                buffer[i] = (byte) (sample & 0xFF);
                buffer[i + 1] = (byte) ((sample >> 8) & 0xFF);
            }

            baos.write(buffer, 0, bytesRead);
        }
    }

    private void writeAdjustedAudio(ByteArrayOutputStream baos, AudioFormat format, String outputFilename) throws IOException {
        byte[] adjustedAudio = baos.toByteArray();
        File outputFile = new File(outputFilename); //TODO : is it ok to do so ?

        try ( ByteArrayInputStream bais = new ByteArrayInputStream(adjustedAudio);
              AudioInputStream adjustedAudioStream = new AudioInputStream(bais, format, adjustedAudio.length / format.getFrameSize())) {

            AudioSystem.write(adjustedAudioStream, AudioFileFormat.Type.WAVE, outputFile);
        }
    }
}
