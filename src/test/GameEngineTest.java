package com.tictactoe.engine;
import com.tictactoe.model.GameStatus;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class GameEngineTest {
    @Test
    void newGameIsInProgressWithXToMove() {
        GameEngine game = new GameEngine();
        assertEquals(GameStatus.IN_PROGRESS, game.status());
        assertEquals(9, game.availableMoves().size());
    }
    @Test
    void topRowIsAWinForX() {
        GameEngine game = new GameEngine();
        game.move(0); game.move(3);
        game.move(1); game.move(4);
        game.move(2); // X: 0,1,2
        assertEquals(GameStatus.X_WON, game.status());
        assertArrayEquals(new int[]{0, 1, 2}, game.winningLine());
    }
    @Test
    void fullBoardWithNoLineIsADraw() {
        GameEngine game = new GameEngine();
// X O X / X O O / O X X -> no three in a row
        int[] order = {0, 1, 2, 4, 3, 5, 7, 6, 8};
        for (int move : order) game.move(move);
        assertEquals(GameStatus.DRAW, game.status());
    }
    @Test
    void playingAfterGameOverThrows() {
        GameEngine game = new GameEngine();
        game.move(0); game.move(3);
        game.move(1); game.move(4);
        game.move(2); // X wins
        assertThrows(IllegalStateException.class, () -> game.move(8));
    }
    @Test
    void serializeRoundTrips() {
        GameEngine game = new GameEngine();
        game.move(4); game.move(0);
        String text = game.getBoard().serialize();
        assertEquals("O___X____", text);
        assertEquals(text, Board.deserialize(text).serialize());
    }
}