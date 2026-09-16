package com.auralis.backend;

import javax.sound.sampled.*;
import java.io.File;

/**
 * FILE 2: AudioFeatureExtractor.java
 *
 * Loads .wav AND .mp3 files using Java Sound API.
 * MP3 support is provided by MP3SPI + JLayer + Tritonus libraries.
 * No code changes needed — AudioSystem auto-detects format.
 */
public class AudioFeatureExtractor {

    private float[] audioSamples;
    private int     sampleRate;

    /**
     * Loads an audio file (.wav or .mp3) and converts it to normalized float samples.
     */
    public void loadAudio(File file) throws Exception {
        AudioInputStream stream = AudioSystem.getAudioInputStream(file);
        AudioFormat format = stream.getFormat();
        this.sampleRate = (int) format.getSampleRate();

        byte[] bytes = stream.readAllBytes();
        stream.close();

        // Convert to normalized float samples (-1.0 to 1.0)
        this.audioSamples = new float[bytes.length / 2];
        for (int i = 0; i < audioSamples.length; i++) {
            int low  = bytes[2 * i] & 0xFF;
            int high = bytes[2 * i + 1] << 8;
            audioSamples[i] = (high | low) / 32768.0f;
        }
    }

    /** Feature 1: Average Amplitude (loudness). */
    public double calculateAverageAmplitude() {
        double sum = 0;
        for (float s : audioSamples) sum += Math.abs(s);
        return sum / audioSamples.length;
    }

    /** Feature 2: Zero-Crossing Rate. */
    public double calculateZeroCrossingRate() {
        int crossings = 0;
        for (int i = 1; i < audioSamples.length; i++) {
            if ((audioSamples[i] >= 0 && audioSamples[i - 1] < 0) ||
                (audioSamples[i] < 0 && audioSamples[i - 1] >= 0)) {
                crossings++;
            }
        }
        return (double) crossings / audioSamples.length;
    }

    /** Feature 3: Spectral Centroid (simplified proxy). */
    public double calculateSpectralCentroid() {
        return calculateZeroCrossingRate() * 1000.0;
    }

    /** Returns [amplitude, zcr, centroid]. */
    public double[] getFeatureVector() {
        return new double[]{
                calculateAverageAmplitude(),
                calculateZeroCrossingRate(),
                calculateSpectralCentroid()
        };
    }

    public float[] getAudioSamples() { return audioSamples; }
    public int getSampleRate()       { return sampleRate; }
}
