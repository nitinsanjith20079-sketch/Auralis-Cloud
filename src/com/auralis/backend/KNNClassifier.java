package com.auralis.backend;

import com.auralis.model.SongData;

import java.util.*;

/**
 * FILE 3: KNNClassifier.java
 * K-Nearest Neighbors classifier (K = 5).
 */
public class KNNClassifier {

    private List<SongData> dataset = new ArrayList<>();
    private static final int K = 5;

    private double lastConfidence;
    private List<SongData> lastNeighbors;

    public void setDataset(List<SongData> dataset) {
        this.dataset = dataset;
    }

    public String classify(double[] features) {
        if (dataset.isEmpty()) return "Unknown";

        List<NeighborResult> results = new ArrayList<>();
        for (SongData song : dataset) {
            double dist = euclideanDistance(features, song.getFeatures());
            results.add(new NeighborResult(song, dist));
        }

        results.sort(Comparator.comparingDouble(r -> r.distance));

        int k = Math.min(K, results.size());
        lastNeighbors = new ArrayList<>();
        Map<String, Integer> votes = new HashMap<>();

        for (int i = 0; i < k; i++) {
            SongData neighbor = results.get(i).song;
            lastNeighbors.add(neighbor);
            votes.merge(neighbor.getGenre(), 1, Integer::sum);
        }

        String predicted = Collections.max(votes.entrySet(),
                Map.Entry.comparingByValue()).getKey();

        int maxVotes = votes.get(predicted);
        lastConfidence = (maxVotes * 100.0) / k;

        return predicted;
    }

    private double euclideanDistance(double[] a, double[] b) {
        double sum = 0;
        for (int i = 0; i < a.length; i++) {
            sum += Math.pow(a[i] - b[i], 2);
        }
        return Math.sqrt(sum);
    }

    public String formatNeighbors(List<SongData> neighbors) {
        if (neighbors == null || neighbors.isEmpty()) return "No neighbours found.";

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-4s %-20s %-12s%n", "#", "Song", "Genre"));
        sb.append("─".repeat(40)).append("\n");

        for (int i = 0; i < neighbors.size(); i++) {
            SongData s = neighbors.get(i);
            sb.append(String.format("%-4d %-20s %-12s%n",
                    (i + 1), s.getSongName(), s.getGenre()));
        }
        return sb.toString();
    }

    public double getLastConfidence()        { return lastConfidence; }
    public List<SongData> getLastNeighbors() { return lastNeighbors; }

    private static class NeighborResult {
        SongData song;
        double distance;

        NeighborResult(SongData song, double distance) {
            this.song = song;
            this.distance = distance;
        }
    }
}
