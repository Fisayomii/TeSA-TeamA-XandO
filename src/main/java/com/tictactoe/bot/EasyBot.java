// EasyBot.java
package com.tictactoe.bot;

import java.util.List;
import java.util.Random;

import com.tictactoe.engine.Board;
import com.tictactoe.model.Mark;

public class EasyBot implements Bot {

    private final Random random = new Random();

    @Override
    public int chooseMove(Board board, Mark botMark) {
        List<Integer> moves = board.availableMoves();
        return moves.get(random.nextInt(moves.size()));
    }
}
