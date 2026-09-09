package com.tictactoe.ui;

import com.tictactoe.db.Streak;
import com.tictactoe.db.Tally;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * What Part 5 (the controller) can ask the GUI to do.
 * The GUI (MainWindow) implements this.
 */
public interface GameView {

    void showBoard(String nineChars);

    void setStatus(String message);

    void highlightWinningLine(int[] line);

    void showGameOver(String message);

    void showScoreboard(List<Tally> tallies, List<Streak> leaderboard);

    void showMenu();

    <T> void runInBackground(
            Supplier<T> work,
            Consumer<T> whenDone
    );

    void setBoardClickListener(BoardClickListener listener);
}