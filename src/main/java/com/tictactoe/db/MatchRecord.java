// com/tictactoe/db/MatchRecord.java
package com.tictactoe.db;
import com.tictactoe.model.GameMode;
import com.tictactoe.model.GameStatus;
public record MatchRecord(GameMode mode, String playerX, String playerO,
                          GameStatus result, String moveSequence) {}