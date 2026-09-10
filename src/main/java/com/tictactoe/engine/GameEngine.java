package com.tictactoe.engine;
import com.tictactoe.model.GameStatus;
import com.tictactoe.model.Mark;
import java.util.ArrayList;
import java.util.List;
public class GameEngine {
    /** Every trio of squares that forms a line. A 2D array: 8 lines, 3 squares each. */
    private static final int[][] WINNING_LINES = {
            {0, 1, 2}, {3, 4, 5}, {6, 7, 8}, // rows
            {0, 3, 6}, {1, 4, 7}, {2, 5, 8}, // columns
            {0, 4, 8}, {2, 4, 6} // diagonals
    };
    private final Board board;
    private Mark currentPlayer;
    private final List<Integer> moveHistory = new ArrayList<>();
    /** A brand-new game: empty board, X to move. */
    public GameEngine() {
        this(new Board(), Mark.X);
    }
    /** Resume from a given position (used by load-game). */
    public GameEngine(Board board, Mark startingPlayer) {
        this.board = board;
        this.currentPlayer = startingPlayer;
    }
    public Board getBoard() { return board; }
    public Mark getCurrentPlayer() { return currentPlayer; }
    /** All empty squares (delegates to the board). */
    public List<Integer> availableMoves() {
        return board.availableMoves();
    }
    /** The current player plays 'index', then the turn passes. */
    public void move(int index) {
        if (status() != GameStatus.IN_PROGRESS) {
            throw new IllegalStateException("The game is already over");
        }
        board.place(index, currentPlayer);
        moveHistory.add(index);
        currentPlayer = currentPlayer.opponent();
    }
    public GameStatus status() {
        for (int[] line : WINNING_LINES) {
            Mark a = board.get(line[0]);
            if (a != Mark.EMPTY
                    && a == board.get(line[1])
                    && a == board.get(line[2])) {
                return (a == Mark.X) ? GameStatus.X_WON : GameStatus.O_WON;
            }
        }
        return board.isFull() ? GameStatus.DRAW : GameStatus.IN_PROGRESS;
    }
    /** The three squares of the winning line, or null if nobody has won. */
    public int[] winningLine() {
        for (int[] line : WINNING_LINES) {
            Mark a = board.get(line[0]);
            if (a != Mark.EMPTY
                    && a == board.get(line[1])
                    && a == board.get(line[2])) {
                return line;
            }
        }
        return null;
    }
    /** e.g. "4,0,8,2" - the squares played, in order. For the database. */
    public String moveSequence() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < moveHistory.size(); i++) {
            if (i > 0) sb.append(',');
            sb.append(moveHistory.get(i));
        }
        return sb.toString();
    }
}
