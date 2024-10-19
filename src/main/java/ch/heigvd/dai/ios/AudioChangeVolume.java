package ch.heigvd.dai.ios;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;

//todo : corriger et prendre que ceux que j'ai besoin !
import java.io.*;
import javax.sound.sampled.*;

public class AudioChangeVolume implements VolumeModifiable {

    private static final int BUFFER_SIZE = 1024; // Buffer size for reading audio data


    @Override
    public void changeVolume(String filename, float volumeIntensity) {
        //todo : plutot faire une erreur via la CLI !
        // Ensure volumeIntensity is between 0 and 1
        //volumeIntensity = Math.max(0, volumeIntensity);

        try (InputStream audioSrc = new FileInputStream(filename);
             InputStream bufferedAudioSrc = new BufferedInputStream(audioSrc);
             AudioInputStream ais = AudioSystem.getAudioInputStream(bufferedAudioSrc);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            AudioFormat format = ais.getFormat(); //todo : pourquoi j'ai besoin de ça ?

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

        while ((bytesRead = ais.read(buffer)) != -1) { //while not at the end of the file...
            for (int i = 0; i < bytesRead; i += 2) { //qu'est ce qu'on fait là ?

                // Assuming 16-bit audio, adjust the sample
                short sample = (short) ((buffer[i] & 0xFF) | (buffer[i + 1] << 8));
                sample = (short) (sample * volumeIntensity);

                // Clip the sample if it exceeds the maximum or minimum values
                //sample = (short) sample;

                // Write the adjusted sample back to the buffer
                buffer[i] = (byte) (sample & 0xFF);
                buffer[i + 1] = (byte) ((sample >> 8) & 0xFF);
            }

            baos.write(buffer, 0, bytesRead);
        }
    }

    private void writeAdjustedAudio(ByteArrayOutputStream baos, AudioFormat format, String outputFilename) throws IOException {
        byte[] adjustedAudio = baos.toByteArray();
        File outputFile = new File(outputFilename); //todo : est-ce que c'est ok de faire comme ça ?

        try ( ByteArrayInputStream bais = new ByteArrayInputStream(adjustedAudio);
              AudioInputStream adjustedAudioStream = new AudioInputStream(bais, format, adjustedAudio.length / format.getFrameSize())) {

            AudioSystem.write(adjustedAudioStream, AudioFileFormat.Type.WAVE, outputFile);
        }
    }
}
