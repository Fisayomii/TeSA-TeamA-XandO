package com.tictactoe.model;
/** A square's contents, or whose turn it is. */
public enum Mark {
    X, O, EMPTY;
    /** The other player. EMPTY has no opponent, so it returns itself. */
    public Mark opponent() {
        return switch (this) {
            case X -> O;
            case O -> X;
            case EMPTY -> EMPTY;
        };
    }
}