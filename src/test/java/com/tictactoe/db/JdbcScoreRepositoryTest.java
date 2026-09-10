// src/test/java/com/tictactoe/db/JdbcScoreRepositoryTest.java
package com.tictactoe.db;

import com.tictactoe.model.ResultForHuman;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class JdbcScoreRepositoryTest {

    static final ScoreRepository repo = new JdbcScoreRepository();
    static final String PLAYER = "test_" + System.currentTimeMillis(); // unique each run

    @BeforeAll
    static void setup() {
        Database.initSchema();
    }

    @Test
    void winsIncreaseTallyAndStreak() {
        repo.applyResult(PLAYER, "HARD", ResultForHuman.WIN);
        repo.applyResult(PLAYER, "HARD", ResultForHuman.WIN);

        Tally t = repo.getTally(PLAYER, "HARD");
        assertEquals(2, t.wins());

        Streak s = repo.getStreak(PLAYER, "HARD");
        assertEquals(2, s.current());
        assertEquals(2, s.best());
    }

    @Test
    void aLossResetsCurrentStreakButKeepsBest() {
        repo.applyResult(PLAYER, "MEDIUM", ResultForHuman.WIN);
        repo.applyResult(PLAYER, "MEDIUM", ResultForHuman.WIN);
        repo.applyResult(PLAYER, "MEDIUM", ResultForHuman.WIN);
        repo.applyResult(PLAYER, "MEDIUM", ResultForHuman.LOSS);

        Streak s = repo.getStreak(PLAYER, "MEDIUM");
        assertEquals(0, s.current());
        assertEquals(3, s.best());
    }

    @Test
    void savedGameRoundTrips() {
        repo.saveGame(new SavedGame("slot_test",
                com.tictactoe.model.GameMode.PVE_HARD, "X__O_____", 'X'));
        var loaded = repo.loadGame("slot_test");
        assertTrue(loaded.isPresent());
        assertEquals("X__O_____", loaded.get().boardState());
        repo.deleteSavedGame("slot_test");
    }
}