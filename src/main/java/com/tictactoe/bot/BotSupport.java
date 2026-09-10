package com.tictactoe.bot;

import com.tictactoe.engine.Board;
import com.tictactoe.model.Mark;

final class BotSupport {

    private static final int[][] LINES = {
        {0, 1, 2}, {3, 4, 5}, {6, 7, 8},
        {0, 3, 6}, {1, 4, 7}, {2, 5, 8},
        {0, 4, 8}, {2, 4, 6}
    };

    private BotSupport() {
    }

    static boolean wins(Board board, Mark mark) {
        for (int[] line : LINES) {
            if (board.get(line[0]) == mark
                    && board.get(line[1]) == mark
                    && board.get(line[2]) == mark) {
                return true;
            }
        }
        return false;
    }
}
