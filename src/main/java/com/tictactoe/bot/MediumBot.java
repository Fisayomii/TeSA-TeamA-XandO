package com.tictactoe.bot;

import java.util.List;
import java.util.Random;

import com.tictactoe.engine.Board;
import com.tictactoe.model.Mark;

public class MediumBot implements Bot {

    private final Random random = new Random();
    private static final int[] CORNERS = {0, 2, 6, 8};
    private static final int[] EDGES = {1, 3, 5, 7};

    @Override
    public int chooseMove(Board board, Mark botMark) {
        List<Integer> moves = board.availableMoves();

        if (random.nextDouble() < 0.25) {
            return moves.get(random.nextInt(moves.size()));
        }

        Integer win = findWinningMove(board, botMark);
        if (win != null) {
            return win;
        }

        Integer block = findWinningMove(board, botMark.opponent());
        if (block != null) {
            return block;
        }

        return preferredPosition(board);
    }

    private Integer findWinningMove(Board board, Mark mark) {
        for (int move : board.availableMoves()) {
            Board copy = new Board(board);
            copy.place(move, mark);
            if (BotSupport.wins(copy, mark)) {
                return move;
            }
        }
        return null;
    }

    private int preferredPosition(Board board) {
        List<Integer> moves = board.availableMoves();
        if (moves.contains(4)) {
            return 4;
        }
        for (int corner : CORNERS) {
            if (moves.contains(corner)) {
                return corner;
            }
        }
        for (int edge : EDGES) {
            if (moves.contains(edge)) {
                return edge;
            }
        }
        return moves.get(0);
    }
}
