package com.tictactoe.ui;

import com.tictactoe.model.GameMode;

import javax.swing.*;
import java.awt.*;

public class MenuPanel extends JPanel {

    /**
     * The controller supplies these;
     * the menu just calls them.
     */
    public interface Actions {

        void startGame(GameMode mode);

        void openScoreboard();

        void openLoadGame();

        void quit();
    }

    public MenuPanel(Actions actions) {

        setLayout(new GridLayout(0, 1, 8, 8));

        setBorder(
                BorderFactory.createEmptyBorder(
                        40, 60, 40, 60
                )
        );

        JLabel title = new JLabel(
                "TIC TAC TOE",
                SwingConstants.CENTER
        );

        title.setFont(
                title.getFont().deriveFont(
                        Font.BOLD, 28f
                )
        );

        add(title);

        add(button(
                "Play a friend",
                e -> actions.startGame(GameMode.PVP)
        ));

        add(button(
                "Computer - Easy",
                e -> actions.startGame(GameMode.PVE_EASY)
        ));

        add(button(
                "Computer - Medium",
                e -> actions.startGame(GameMode.PVE_MEDIUM)
        ));

        add(button(
                "Computer - Hard",
                e -> actions.startGame(GameMode.PVE_HARD)
        ));

        add(button(
                "Scoreboard",
                e -> actions.openScoreboard()
        ));

        add(button(
                "Load saved game",
                e -> actions.openLoadGame()
        ));

        add(button(
                "Quit",
                e -> actions.quit()
        ));
    }

    private JButton button(
            String text,
            java.awt.event.ActionListener onClick
    ) {
        JButton b = new JButton(text);
        b.addActionListener(onClick);
        return b;
    }
}