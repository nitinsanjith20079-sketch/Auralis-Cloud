package com.auralis.backend;

import com.auralis.model.SongData;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * FILE 4: FileManager.java
 * CSV loading + result export.
 */
public class FileManager {

    public List<SongData> loadDataset(String path) throws IOException {
        List<SongData> dataset = new ArrayList<>();
        File file = new File(path);

        if (!file.exists()) {
            throw new FileNotFoundException("Dataset not found: " + path);
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            boolean headerSkipped = false;

            while ((line = br.readLine()) != null) {
                if (!headerSkipped) { headerSkipped = true; continue; }
                if (line.trim().isEmpty()) continue;

                String[] parts = line.split(",");
                if (parts.length < 5) continue;

                try {
                    dataset.add(new SongData(
                            parts[0].trim(),
                            parts[1].trim(),
                            Double.parseDouble(parts[2].trim()),
                            Double.parseDouble(parts[3].trim()),
                            Double.parseDouble(parts[4].trim())
                    ));
                } catch (NumberFormatException e) {
                    System.err.println("⚠ Skipping invalid row: " + line);
                }
            }
        }
        return dataset;
    }

    public void exportResult(String path, String fileName,
                             String genre, String confidence) throws IOException {
        File file = new File(path);
        file.getParentFile().mkdirs();

        boolean isNew = !file.exists();

        try (PrintWriter pw = new PrintWriter(new FileWriter(file, true))) {
            if (isNew) {
                pw.println("═".repeat(60));
                pw.println("        AURALIS - CLASSIFICATION RESULTS");
                pw.println("═".repeat(60));
                pw.println();
            }

            String timestamp = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            pw.println("─".repeat(60));
            pw.println("Timestamp  : " + timestamp);
            pw.println("File       : " + fileName);
            pw.println("Genre      : " + genre);
            pw.println("Confidence : " + confidence);
            pw.println("─".repeat(60));
            pw.println();
        }
    }

    public void ensureOutputDir() {
        try {
            Files.createDirectories(Paths.get("output"));
        } catch (IOException e) {
            System.err.println("⚠ Could not create output dir: " + e.getMessage());
        }
    }
}
