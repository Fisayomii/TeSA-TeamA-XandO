// com/tictactoe/db/ScoreRepository.java
package com.tictactoe.db;

import com.tictactoe.model.ResultForHuman;
import java.util.List;
import java.util.Optional;

public interface ScoreRepository {

    /** Store a finished game in the match-history table. */
    void saveMatch(MatchRecord match);

    /** Most recent finished games, newest first. */
    List<MatchRecord> recentMatches(int limit);

    /**
     * Update the tally AND the win streak for one human/bot-level pairing,
     * in a single transaction. Call once per finished bot game.
     */
    void applyResult(String playerName, String botLevel, ResultForHuman result);

    Tally getTally(String playerName, String botLevel);

    List<Tally> allTallies(String playerName);

    Streak getStreak(String playerName, String botLevel);

    /** Highest 'best streak' values across all players, for the leaderboard. */
    List<Streak> leaderboard(int limit);

    void saveGame(SavedGame game);

    Optional<SavedGame> loadGame(String slot);

    List<String> listSavedSlots();

    void deleteSavedGame(String slot);
}