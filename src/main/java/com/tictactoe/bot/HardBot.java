package com.tictactoe.bot;

import java.util.List;

import com.tictactoe.engine.Board;
import com.tictactoe.model.Mark;

public class HardBot implements Bot {

    @Override
    public int chooseMove(Board board, Mark botMark) {
        int bestScore = Integer.MIN_VALUE;
        int bestMove = -1;

        for (int move : board.availableMoves()) {
            Board copy = new Board(board);
            copy.place(move, botMark);
            int score = minimax(copy, 1, false, botMark, Integer.MIN_VALUE, Integer.MAX_VALUE);
            if (score > bestScore) {
                bestScore = score;
                bestMove = move;
            }
        }
        return bestMove;
    }

    private int minimax(Board board, int depth, boolean isMaximizing, Mark botMark,
            int alpha, int beta) {
        Mark opponent = botMark.opponent();

        if (BotSupport.wins(board, botMark)) {
            return 10 - depth;
        }
        if (BotSupport.wins(board, opponent)) {
            return depth - 10;
        }
        if (board.isFull()) {
            return 0;
        }

        List<Integer> moves = board.availableMoves();

        if (isMaximizing) {
            int best = Integer.MIN_VALUE;
            for (int move : moves) {
                Board copy = new Board(board);
                copy.place(move, botMark);
                int score = minimax(copy, depth + 1, false, botMark, alpha, beta);
                best = Math.max(best, score);
                alpha = Math.max(alpha, best);
                if (beta <= alpha) {
                    break;
                }
            }
            return best;
        } else {
            int best = Integer.MAX_VALUE;
            for (int move : moves) {
                Board copy = new Board(board);
                copy.place(move, opponent);
                int score = minimax(copy, depth + 1, true, botMark, alpha, beta);
                best = Math.min(best, score);
                beta = Math.min(beta, best);
                if (beta <= alpha) {
                    break;
                }
            }
            return best;
        }
    }
}
