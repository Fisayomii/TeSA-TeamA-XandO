// com/tictactoe/db/SavedGame.java
package com.tictactoe.db;
import com.tictactoe.model.GameMode;
public record SavedGame(String slot, GameMode mode, String boardState, char nextPlayer) {}