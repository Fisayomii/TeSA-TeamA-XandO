package com.tictactoe.bot;

import com.tictactoe.engine.Board;
import com.tictactoe.model.Mark;

public interface Bot {

    int chooseMove(Board board, Mark botMark);
}
