// src/main/java/com/tictactoe/engine/Board.java
package com.tictactoe.engine;
import com.tictactoe.model.Mark;
import java.util.ArrayList;
import java.util.List;
/** A 3x3 grid of marks. Squares are numbered 0..8, row by row. */
public class Board {
    private final Mark[] cells; // always length 9
    /** A fresh, empty board. */
    public Board() {
        cells = new Mark[9];
        for (int i = 0; i < 9; i++) {
            cells[i] = Mark.EMPTY;
        }
    }
    /**
     * Copy constructor: builds a new board that is an independent copy of
     * another. The bot uses this to try out moves without touching the real game.
     */
    public Board(Board other) {
        this.cells = other.cells.clone(); // clone() copies the array's contents
    }
    public Mark get(int index) {
        return cells[index];
    }
    public boolean isEmpty(int index) {
        return cells[index] == Mark.EMPTY;
    }
    /** Put a mark on an empty square. Throws if the square is bad or taken. */
    public void place(int index, Mark mark) {
        if (index < 0 || index > 8) {
            throw new IllegalArgumentException("Square must be 0..8, was " + index);
        }
        if (cells[index] != Mark.EMPTY) {
            throw new IllegalStateException("Square " + index + " is already taken");
        }
        cells[index] = mark;
    }
    /** The indices of every empty square, in ascending order. */
    public List<Integer> availableMoves() {
        List<Integer> moves = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            if (cells[i] == Mark.EMPTY) {
                moves.add(i);
            }
        }
        return moves;
    }
    public boolean isFull() {
        return availableMoves().isEmpty();
    }
    /** Turn the board into a 9-char string, e.g. "X__O_X__O". */
    public String serialize() {
        StringBuilder sb = new StringBuilder(9);
        for (Mark cell : cells) {
            sb.append(switch (cell) {
                case X -> 'X';
                case O -> 'O';
                case EMPTY -> '_';
            });
        }
        return sb.toString();
    }
    /** Rebuild a board from a 9-char string produced by serialize(). */
    public static Board deserialize(String text) {
        if (text == null || text.length() != 9) {
            throw new IllegalArgumentException("Board text must be exactly 9 characters");
        }
        Board board = new Board();
        for (int i = 0; i < 9; i++) {
            board.cells[i] = switch (text.charAt(i)) {
                case 'X' -> Mark.X;
                case 'O' -> Mark.O;
                case '_' -> Mark.EMPTY;
                default -> throw new IllegalArgumentException(
                        "Unexpected character '" + text.charAt(i) + "' in board text");
            };
        }
        return board;
    }
}
