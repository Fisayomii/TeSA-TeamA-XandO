package com.tictactoe.ui;

import com.tictactoe.db.Streak;
import com.tictactoe.db.Tally;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class MainWindow extends JFrame implements GameView {

    private final CardLayout cards = new CardLayout();
    private final JPanel root = new JPanel(cards);

    private final BoardPanel boardPanel = new BoardPanel();

    private final JLabel statusLabel =
            new JLabel("Welcome", SwingConstants.CENTER);

    private final MenuPanel menuPanel;

    private final ScoreboardPanel scoreboardPanel =
            new ScoreboardPanel(this::showMenu);

    public MainWindow(MenuPanel.Actions menuActions) {

        super("Tic Tac Toe");

        this.menuPanel = new MenuPanel(menuActions);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(460, 560);
        setLocationRelativeTo(null);

        JPanel gameScreen =
                new JPanel(new BorderLayout(8, 8));

        gameScreen.setBorder(
                BorderFactory.createEmptyBorder(
                        10, 10, 10, 10
                )
        );

        statusLabel.setFont(
                statusLabel.getFont().deriveFont(
                        Font.BOLD, 18f
                )
        );

        gameScreen.add(
                statusLabel,
                BorderLayout.NORTH
        );

        gameScreen.add(
                boardPanel,
                BorderLayout.CENTER
        );

        root.add(menuPanel, "menu");
        root.add(gameScreen, "game");
        root.add(scoreboardPanel, "scoreboard");

        add(root);

        showMenu();
    }

    @Override
    public void showBoard(String nineChars) {

        SwingUtilities.invokeLater(() -> {
            boardPanel.render(nineChars);
            cards.show(root, "game");
        });
    }

    @Override
    public void setStatus(String message) {

        SwingUtilities.invokeLater(
                () -> statusLabel.setText(message)
        );
    }

    @Override
    public void highlightWinningLine(int[] line) {

        SwingUtilities.invokeLater(
                () -> boardPanel.highlight(line)
        );
    }

    @Override
    public void showGameOver(String message) {

        SwingUtilities.invokeLater(
                () -> JOptionPane.showMessageDialog(
                        this,
                        message,
                        "Game over",
                        JOptionPane.INFORMATION_MESSAGE
                )
        );
    }

    @Override
    public void showScoreboard(
            List<Tally> tallies,
            List<Streak> leaderboard
    ) {

        SwingUtilities.invokeLater(() -> {
            scoreboardPanel.update(
                    tallies,
                    leaderboard
            );

            cards.show(root, "scoreboard");
        });
    }

    @Override
    public void showMenu() {

        SwingUtilities.invokeLater(
                () -> cards.show(root, "menu")
        );
    }

    @Override
    public void setBoardClickListener(
            BoardClickListener listener
    ) {

        boardPanel.setListener(listener);
    }

    @Override
    public <T> void runInBackground(
            Supplier<T> work,
            Consumer<T> whenDone
    ) {

        new SwingWorker<T, Void>() {

            @Override
            protected T doInBackground() {
                return work.get();
            }

            @Override
            protected void done() {
                try {
                    whenDone.accept(get());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

        }.execute();
    }
}