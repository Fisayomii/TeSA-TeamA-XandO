package com.tictactoe.ui;

import com.tictactoe.model.GameMode;

import javax.swing.*;

public class UiDemo {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            final MainWindow[] windowHolder = new MainWindow[1];

            MenuPanel.Actions actions = new MenuPanel.Actions() {

                @Override
                public void startGame(GameMode mode) {
                    windowHolder[0].setStatus(
                            "Demo: " + mode + " - X to move"
                    );

                    windowHolder[0].showBoard(
                            "_________"
                    );
                }

                @Override
                public void openScoreboard() {
                    windowHolder[0].showScoreboard(
                            java.util.List.of(),
                            java.util.List.of()
                    );
                }

                @Override
                public void openLoadGame() {
                    JOptionPane.showMessageDialog(
                            null,
                            "load dialog"
                    );
                }

                @Override
                public void quit() {
                    System.exit(0);
                }
            };

            MainWindow window = new MainWindow(actions);

            windowHolder[0] = window;

            window.setBoardClickListener(
                    i -> window.setStatus(
                            "clicked " + i
                    )
            );

            window.setVisible(true);
        });
    }
}