package com.tictactoe.ui;

import javax.swing.*;
import java.awt.*;

public class BoardPanel extends JPanel {

    private final JButton[] cells = new JButton[9];
    private BoardClickListener listener;

    public BoardPanel() {
        setLayout(new GridLayout(3, 3, 6, 6));

        Font font = new Font("SansSerif", Font.BOLD, 64);

        for (int i = 0; i < 9; i++) {
            JButton button = new JButton();

            button.setFont(font);
            button.setFocusPainted(false);
            button.setBackground(Color.WHITE);

            final int index = i;

            button.addActionListener(e -> {
                if (listener != null) {
                    listener.onCellClicked(index);
                }
            });

            cells[i] = button;
            add(button);
        }
    }

    public void setListener(BoardClickListener listener) {
        this.listener = listener;
    }

    public void render(String nineChars) {
        for (int i = 0; i < 9; i++) {
            char c = nineChars.charAt(i);

            cells[i].setText(c == '_' ? "" : String.valueOf(c));
            cells[i].setEnabled(c == '_');

            cells[i].setForeground(
                    c == 'X'
                            ? new Color(0x1565C0)
                            : new Color(0xC62828)
            );
        }
    }

    public void highlight(int[] line) {
        if (line == null) {
            return;
        }

        for (int idx : line) {
            cells[idx].setBackground(new Color(0xA5D6A7));
        }
    }

    public void clear() {
        for (JButton button : cells) {
            button.setText("");
            button.setEnabled(true);
            button.setBackground(Color.WHITE);
        }
    }
}