package com.tictactoe.app;

import javax.swing.JOptionPane;

public class PlayerNames {
    public static String ask(String prompt, String defaultValue) {
        String name = JOptionPane.showInputDialog(null, prompt, defaultValue);
        if (name == null || name.isBlank()) {
            return defaultValue;
        }
        return name.trim();
    }
}