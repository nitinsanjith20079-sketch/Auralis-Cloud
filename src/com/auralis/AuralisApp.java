package com.auralis;

import com.auralis.ui.AuralisUI;
import javax.swing.*;

/**
 * FILE 6: AuralisApp.java
 * Main entry point — invoked by Webswing on the server.
 */
public class AuralisApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                System.out.println("Using default look and feel");
            }

            AuralisUI app = new AuralisUI();
            app.setVisible(true);
            System.out.println("🎵 Auralis running with WAV + MP3 support.");
        });
    }
}
