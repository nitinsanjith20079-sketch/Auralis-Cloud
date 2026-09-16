package com.auralis.model;

/**
 * FILE 1: SongData.java
 * Represents one song with its 3 audio features.
 */
public class SongData {

    private final String songName;
    private final String genre;
    private final double avgAmplitude;
    private final double zeroCrossingRate;
    private final double spectralCentroid;

    public SongData(String songName, String genre,
                    double avgAmplitude, double zeroCrossingRate,
                    double spectralCentroid) {
        this.songName         = songName;
        this.genre            = genre;
        this.avgAmplitude     = avgAmplitude;
        this.zeroCrossingRate = zeroCrossingRate;
        this.spectralCentroid = spectralCentroid;
    }

    public String getSongName()         { return songName; }
    public String getGenre()            { return genre; }
    public double getAvgAmplitude()     { return avgAmplitude; }
    public double getZeroCrossingRate() { return zeroCrossingRate; }
    public double getSpectralCentroid() { return spectralCentroid; }

    public double[] getFeatures() {
        return new double[]{avgAmplitude, zeroCrossingRate, spectralCentroid};
    }

    @Override
    public String toString() {
        return String.format("%s [%s] (amp=%.3f, zcr=%.3f, centroid=%.1f)",
                songName, genre, avgAmplitude, zeroCrossingRate, spectralCentroid);
    }
}
