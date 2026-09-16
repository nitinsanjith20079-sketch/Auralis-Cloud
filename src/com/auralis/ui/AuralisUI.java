package com.auralis.ui;

import com.auralis.backend.AudioFeatureExtractor;
import com.auralis.backend.FileManager;
import com.auralis.backend.KNNClassifier;
import com.auralis.model.SongData;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.util.List;

/**
 * FILE 5: AuralisUI.java
 * Swing GUI — accepts .wav and .mp3 files.
 */
public class AuralisUI extends JFrame {

    private static final Color BG_DARK    = new Color(10, 10, 15);
    private static final Color BG_CARD    = new Color(22, 22, 32);
    private static final Color PURPLE     = new Color(163, 0, 249);
    private static final Color CYAN       = new Color(0, 212, 255);
    private static final Color TEXT_WHITE = new Color(255, 255, 255);
    private static final Color TEXT_GRAY  = new Color(170, 170, 170);
    private static final Color SUCCESS    = new Color(46, 204, 113);
    private static final Color WARNING    = new Color(241, 196, 15);

    private JLabel  statusLabel, genreLabel, confidenceLabel, fileLabel;
    private WaveformPanel waveformPanel;
    private JTextArea featureArea, neighborArea;
    private JProgressBar progressBar;

    private final AudioFeatureExtractor extractor;
    private final KNNClassifier classifier;
    private final FileManager fileManager;

    public AuralisUI() {
        this.extractor   = new AudioFeatureExtractor();
        this.classifier  = new KNNClassifier();
        this.fileManager = new FileManager();

        loadDataset();
        setupWindow();
        buildUI();
    }

    private void loadDataset() {
        try {
            List<SongData> dataset = fileManager.loadDataset("data/songs_dataset.csv");
            classifier.setDataset(dataset);
            System.out.println("📚 Loaded " + dataset.size() + " songs.");
        } catch (Exception e) {
            System.err.println("⚠ Dataset error: " + e.getMessage());
        }
    }

    private void setupWindow() {
        setTitle("🎵 Auralis - AI Music Genre Classifier");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 750);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(900, 650));
        getContentPane().setBackground(BG_DARK);
    }

    private void buildUI() {
        setLayout(new BorderLayout(15, 15));
        ((JComponent) getContentPane()).setBorder(new EmptyBorder(20, 20, 20, 20));
        add(buildHeader(), BorderLayout.NORTH);

        JPanel center = new JPanel(new GridLayout(1, 2, 15, 15));
        center.setOpaque(false);
        center.add(buildLeftPanel());
        center.add(buildRightPanel());
        add(center, BorderLayout.CENTER);

        add(buildFooter(), BorderLayout.SOUTH);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setOpaque(false);

        JLabel title = new JLabel("🎵 Auralis");
        title.setFont(new Font("Segoe UI", Font.BOLD, 36));
        title.setForeground(PURPLE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("AI-Powered Music Genre Classification");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitle.setForeground(CYAN);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        header.add(title);
        header.add(Box.createVerticalStrut(5));
        header.add(subtitle);
        header.add(Box.createVerticalStrut(15));
        return header;
    }

    private JPanel buildLeftPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BG_CARD);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(PURPLE, 1),
                new EmptyBorder(20, 20, 20, 20)));

        JLabel uploadTitle = new JLabel("📁 Upload Audio File");
        uploadTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        uploadTitle.setForeground(TEXT_WHITE);
        uploadTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton uploadButton = createStyledButton("Choose .wav or .mp3 File", PURPLE);
        uploadButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        uploadButton.addActionListener(e -> handleUpload());

        fileLabel = new JLabel("No file selected");
        fileLabel.setForeground(TEXT_GRAY);
        fileLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel waveTitle = new JLabel("🌊 Waveform Visualization");
        waveTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        waveTitle.setForeground(TEXT_WHITE);
        waveTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        waveformPanel = new WaveformPanel();
        waveformPanel.setPreferredSize(new Dimension(400, 150));
        waveformPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
        waveformPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel featureTitle = new JLabel("📊 Extracted Features");
        featureTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        featureTitle.setForeground(TEXT_WHITE);
        featureTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        featureArea = new JTextArea(4, 30);
        featureArea.setEditable(false);
        featureArea.setBackground(BG_DARK);
        featureArea.setForeground(CYAN);
        featureArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        featureArea.setText("No audio loaded yet.");
        featureArea.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(uploadTitle);
        panel.add(Box.createVerticalStrut(10));
        panel.add(uploadButton);
        panel.add(Box.createVerticalStrut(5));
        panel.add(fileLabel);
        panel.add(Box.createVerticalStrut(20));
        panel.add(waveTitle);
        panel.add(Box.createVerticalStrut(10));
        panel.add(waveformPanel);
        panel.add(Box.createVerticalStrut(20));
        panel.add(featureTitle);
        panel.add(Box.createVerticalStrut(10));
        panel.add(new JScrollPane(featureArea));
        return panel;
    }

    private JPanel buildRightPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BG_CARD);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CYAN, 1),
                new EmptyBorder(20, 20, 20, 20)));

        JLabel predTitle = new JLabel("🎯 Prediction Result");
        predTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        predTitle.setForeground(TEXT_WHITE);
        predTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        genreLabel = new JLabel("—");
        genreLabel.setFont(new Font("Segoe UI", Font.BOLD, 48));
        genreLabel.setForeground(PURPLE);
        genreLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        confidenceLabel = new JLabel("Confidence: —");
        confidenceLabel.setForeground(CYAN);
        confidenceLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        progressBar.setForeground(SUCCESS);
        progressBar.setBackground(BG_DARK);
        progressBar.setBorderPainted(false);
        progressBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        progressBar.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel neighborTitle = new JLabel("📋 Top 5 Nearest Neighbors");
        neighborTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        neighborTitle.setForeground(TEXT_WHITE);
        neighborTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        neighborArea = new JTextArea(10, 30);
        neighborArea.setEditable(false);
        neighborArea.setBackground(BG_DARK);
        neighborArea.setForeground(TEXT_WHITE);
        neighborArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        neighborArea.setText("Neighbours will appear here.");
        neighborArea.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton exportButton = createStyledButton("💾 Export Result", CYAN);
        exportButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        exportButton.addActionListener(e -> handleExport());

        panel.add(predTitle);
        panel.add(Box.createVerticalStrut(15));
        panel.add(genreLabel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(confidenceLabel);
        panel.add(Box.createVerticalStrut(15));
        panel.add(progressBar);
        panel.add(Box.createVerticalStrut(25));
        panel.add(neighborTitle);
        panel.add(Box.createVerticalStrut(10));
        panel.add(new JScrollPane(neighborArea));
        panel.add(Box.createVerticalStrut(15));
        panel.add(exportButton);
        return panel;
    }

    private JPanel buildFooter() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);

        statusLabel = new JLabel("✅ Ready");
        statusLabel.setForeground(SUCCESS);

        JLabel version = new JLabel("Auralis v1.0 | Java | KNN | WAV + MP3");
        version.setForeground(TEXT_GRAY);

        footer.add(statusLabel, BorderLayout.WEST);
        footer.add(version, BorderLayout.EAST);
        return footer;
    }

    private void handleUpload() {
        JFileChooser chooser = new JFileChooser("data/genres/");
        chooser.setFileFilter(new FileNameExtensionFilter(
                "Audio Files (*.wav, *.mp3)", "wav", "mp3"));
        int result = chooser.showOpenDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) return;

        File selected = chooser.getSelectedFile();
        fileLabel.setText("📄 " + selected.getName());
        statusLabel.setText("⏳ Processing...");
        statusLabel.setForeground(WARNING);

        new Thread(() -> processAudio(selected)).start();
    }

    private void processAudio(File file) {
        try {
            extractor.loadAudio(file);
            double[] features = extractor.getFeatureVector();

            SwingUtilities.invokeLater(() -> {
                progressBar.setValue(50);
                waveformPanel.setAudioData(extractor.getAudioSamples());
                featureArea.setText(String.format(
                        "📢 Amplitude : %.4f%n🌊 ZCR       : %.4f%n✨ Centroid  : %.2f",
                        features[0], features[1], features[2]));
            });

            String genre = classifier.classify(features);
            double confidence = classifier.getLastConfidence();
            List<SongData> neighbors = classifier.getLastNeighbors();

            SwingUtilities.invokeLater(() -> {
                progressBar.setValue(100);
                genreLabel.setText(genre.toUpperCase());
                confidenceLabel.setText(String.format("Confidence: %.0f%%", confidence));
                neighborArea.setText(classifier.formatNeighbors(neighbors));
                statusLabel.setText("✅ Classification complete!");
                statusLabel.setForeground(SUCCESS);
            });

        } catch (Exception e) {
            SwingUtilities.invokeLater(() -> {
                statusLabel.setText("❌ Error: " + e.getMessage());
                statusLabel.setForeground(Color.RED);
            });
        }
    }

    private void handleExport() {
        if (genreLabel.getText().equals("—")) {
            JOptionPane.showMessageDialog(this, "Classify a song first!");
            return;
        }
        try {
            fileManager.exportResult("output/results.txt",
                    fileLabel.getText(), genreLabel.getText(), confidenceLabel.getText());
            statusLabel.setText("💾 Exported to output/results.txt");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Export failed: " + e.getMessage());
        }
    }

    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(240, 40));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        return button;
    }

    private static class WaveformPanel extends JPanel {
        private float[] audioData;

        WaveformPanel() {
            setBackground(new Color(5, 5, 10));
            setBorder(BorderFactory.createLineBorder(new Color(0, 212, 255, 100), 1));
        }

        void setAudioData(float[] data) {
            this.audioData = data;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth(), h = getHeight(), mid = h / 2;

            g2.setColor(new Color(255, 255, 255, 30));
            g2.drawLine(0, mid, w, mid);

            if (audioData == null || audioData.length == 0) {
                g2.setColor(TEXT_GRAY);
                g2.drawString("Waveform will appear here", w / 2 - 70, mid + 5);
                return;
            }

            g2.setColor(PURPLE);
            int step = Math.max(1, audioData.length / w);
            for (int x = 0; x < w; x++) {
                int idx = x * step;
                if (idx >= audioData.length) break;
                int amp = (int) (Math.abs(audioData[idx]) * mid * 0.9);
                g2.drawLine(x, mid - amp, x, mid + amp);
            }
        }
    }
}
