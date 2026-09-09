package com.tictactoe.ui;

import com.tictactoe.db.Streak;
import com.tictactoe.db.Tally;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ScoreboardPanel extends JPanel {

    private final DefaultTableModel tallyModel =
            new DefaultTableModel(
                    new Object[]{"Bot level", "Wins", "Losses", "Draws"}, 0
            );

    private final DefaultTableModel streakModel =
            new DefaultTableModel(
                    new Object[]{"Player", "Bot level", "Current", "Best"}, 0
            );

    public ScoreboardPanel(Runnable onBack) {

        setLayout(new BorderLayout(8, 8));

        setBorder(
                BorderFactory.createEmptyBorder(
                        10, 10, 10, 10
                )
        );

        JPanel tables = new JPanel(
                new GridLayout(2, 1, 8, 8)
        );

        tables.add(
                titled(
                        "Your record",
                        new JTable(tallyModel)
                )
        );

        tables.add(
                titled(
                        "Longest win streaks",
                        new JTable(streakModel)
                )
        );

        add(tables, BorderLayout.CENTER);

        JButton back = new JButton("Back to menu");

        back.addActionListener(
                e -> onBack.run()
        );

        add(back, BorderLayout.SOUTH);
    }

    private JScrollPane titled(
            String title,
            JTable table
    ) {
        JScrollPane sp = new JScrollPane(table);

        sp.setBorder(
                BorderFactory.createTitledBorder(title)
        );

        return sp;
    }

    public void update(
            List<Tally> tallies,
            List<Streak> leaderboard
    ) {
        tallyModel.setRowCount(0);

        for (Tally t : tallies) {
            tallyModel.addRow(
                    new Object[]{
                            t.botLevel(),
                            t.wins(),
                            t.losses(),
                            t.draws()
                    }
            );
        }

        streakModel.setRowCount(0);

        for (Streak s : leaderboard) {
            streakModel.addRow(
                    new Object[]{
                            s.playerName(),
                            s.botLevel(),
                            s.current(),
                            s.best()
                    }
            );
        }
    }
}