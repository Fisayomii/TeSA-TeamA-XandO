// src/test/java/com/tictactoe/bot/HardBotTest.java
package com.tictactoe.bot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import com.tictactoe.engine.Board;
import com.tictactoe.engine.GameEngine;
import com.tictactoe.model.GameStatus;
import com.tictactoe.model.Mark;

class HardBotTest {

    private final HardBot hardBot = new HardBot();
    private final EasyBot easyBot = new EasyBot();

    @Test
    void takesAnImmediateWin() {
        // X X _
        // O O _
        // _ _ _
        // Hard bot is X, playing square 2 wins immediately.
        Board board = new Board();
        board.place(0, Mark.X);
        board.place(3, Mark.O);
        board.place(1, Mark.X);
        board.place(4, Mark.O);

        int move = hardBot.chooseMove(board, Mark.X);

        assertEquals(2, move, "Hard bot should take the immediate win at square 2");
    }

    @Test
    void blocksAnImmediateThreat() {
        // O O _
        // X _ _
        // _ _ _
        // Hard bot is X; opponent (O) threatens to win at square 2. Must block.
        Board board = new Board();
        board.place(0, Mark.O);
        board.place(3, Mark.X);
        board.place(1, Mark.O);

        int move = hardBot.chooseMove(board, Mark.X);

        assertEquals(2, move, "Hard bot should block the opponent's winning move at square 2");
    }

    @RepeatedTest(20)
    void neverLosesAgainstARandomOpponent() {
        // Play a full game: HardBot vs EasyBot, alternating who goes first
        // across repetitions isn't required by the spec, but we verify
        // HardBot (as X) never loses regardless of EasyBot's random play.
        GameEngine engine = new GameEngine();

        while (engine.status() == GameStatus.IN_PROGRESS) {
            Mark current = engine.getCurrentPlayer();
            int move = (current == Mark.X)
                    ? hardBot.chooseMove(engine.getBoard(), Mark.X)
                    : easyBot.chooseMove(engine.getBoard(), Mark.O);
            engine.move(move);
        }

        assertNotEquals(GameStatus.O_WON, engine.status(),
                "Hard bot (X) should never lose to a random opponent");
    }
}
