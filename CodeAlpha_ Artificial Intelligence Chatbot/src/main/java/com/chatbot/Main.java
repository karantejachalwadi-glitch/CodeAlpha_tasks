package com.chatbot;

import com.chatbot.gui.ChatbotGUI;

import javax.swing.*;

/**
 * Main entry point for the AI Chatbot application.
 * Launches the GUI on the Swing Event Dispatch Thread.
 */
public class Main {

    public static void main(String[] args) {
        // Use system look-and-feel as base, then override with custom theme
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) { /* fall back to default */ }

        // Launch GUI on EDT
        SwingUtilities.invokeLater(() -> new ChatbotGUI());
    }
}
