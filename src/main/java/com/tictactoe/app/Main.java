package com.tictactoe.app;

import com.tictactoe.db.Database;
import com.tictactoe.db.JdbcScoreRepository;
import com.tictactoe.db.ScoreRepository;
import com.tictactoe.model.GameMode;
import com.tictactoe.ui.MainWindow;
import com.tictactoe.ui.MenuPanel;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        Database.initSchema();
        ScoreRepository repository = new JdbcScoreRepository();

        SwingUtilities.invokeLater(() -> {
            final MainWindow[] windowHolder = new MainWindow[1];
            final GameController[] controllerHolder = new GameController[1];

            MenuPanel.Actions actions = new MenuPanel.Actions() {
                @Override
                public void startGame(GameMode mode) {
                    String playerX = PlayerNames.ask("Player X name:", "Player 1");
                    String playerO = mode.isVsBot()
                            ? "CPU"
                            : PlayerNames.ask("Player O name:", "Player 2");
                    controllerHolder[0].newGame(mode, playerX, playerO);
                }

                @Override
                public void openScoreboard() {
                    windowHolder[0].showScoreboard(
                            repository.allTallies("Player 1"),
                            repository.leaderboard(10)
                    );
                }

                @Override
                public void openLoadGame() {
                    controllerHolder[0].resumeQuicksave();
                }

                @Override
                public void quit() {
                    System.exit(0);
                }
            };

            MainWindow window = new MainWindow(actions);
            windowHolder[0] = window;

            GameController controller = new GameController(window, repository);
            controllerHolder[0] = controller;

            window.setBoardClickListener(controller);
            window.setVisible(true);
        });
    }
}