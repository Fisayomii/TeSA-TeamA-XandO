package com.tictactoe.db;

import com.tictactoe.model.GameMode;
import com.tictactoe.model.GameStatus;
import com.tictactoe.model.ResultForHuman;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcScoreRepository implements ScoreRepository {

    @Override
    public void saveMatch(MatchRecord m) {
        String sql = """
            INSERT INTO matches (mode, player_x, player_o, result, move_sequence)
            VALUES (?, ?, ?, ?, ?)
            """;
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, m.mode().name());
            ps.setString(2, m.playerX());
            ps.setString(3, m.playerO());
            ps.setString(4, m.result().name());
            ps.setString(5, m.moveSequence());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("saveMatch failed", e);
        }
    }

    @Override
    public void applyResult(String playerName, String botLevel, ResultForHuman result) {
        String upsertTally = """
            INSERT INTO score_tallies (player_name, bot_level, wins, losses, draws)
            VALUES (?, ?, ?, ?, ?)
            ON CONFLICT (player_name, bot_level) DO UPDATE SET
                wins   = score_tallies.wins   + EXCLUDED.wins,
                losses = score_tallies.losses + EXCLUDED.losses,
                draws  = score_tallies.draws  + EXCLUDED.draws
            """;
        String readStreak =
                "SELECT current_streak FROM win_streaks WHERE player_name = ? AND bot_level = ?";
        String upsertStreak = """
            INSERT INTO win_streaks (player_name, bot_level, current_streak, best_streak)
            VALUES (?, ?, ?, ?)
            ON CONFLICT (player_name, bot_level) DO UPDATE SET
                current_streak = EXCLUDED.current_streak,
                best_streak    = GREATEST(win_streaks.best_streak, EXCLUDED.current_streak)
            """;

        try (Connection con = Database.getConnection()) {
            con.setAutoCommit(false);                 // start a transaction
            try {
                try (PreparedStatement ps = con.prepareStatement(upsertTally)) {
                    ps.setString(1, playerName);
                    ps.setString(2, botLevel);
                    ps.setInt(3, result == ResultForHuman.WIN  ? 1 : 0);
                    ps.setInt(4, result == ResultForHuman.LOSS ? 1 : 0);
                    ps.setInt(5, result == ResultForHuman.DRAW ? 1 : 0);
                    ps.executeUpdate();
                }

                int currentStreak = 0;
                try (PreparedStatement ps = con.prepareStatement(readStreak)) {
                    ps.setString(1, playerName);
                    ps.setString(2, botLevel);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) currentStreak = rs.getInt("current_streak");
                    }
                }
                int nextStreak = (result == ResultForHuman.WIN) ? currentStreak + 1 : 0;

                try (PreparedStatement ps = con.prepareStatement(upsertStreak)) {
                    ps.setString(1, playerName);
                    ps.setString(2, botLevel);
                    ps.setInt(3, nextStreak);
                    ps.setInt(4, nextStreak);
                    ps.executeUpdate();
                }

                con.commit();                        // all three steps succeeded
            } catch (SQLException inner) {
                con.rollback();                      // undo everything
                throw inner;
            }
        } catch (SQLException e) {
            throw new RuntimeException("applyResult failed", e);
        }
    }

    @Override
    public Tally getTally(String playerName, String botLevel) {
        String sql = """
            SELECT wins, losses, draws FROM score_tallies
            WHERE player_name = ? AND bot_level = ?
            """;
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, playerName);
            ps.setString(2, botLevel);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Tally(playerName, botLevel,
                            rs.getInt("wins"), rs.getInt("losses"), rs.getInt("draws"));
                }
                return new Tally(playerName, botLevel, 0, 0, 0);   // no games yet
            }
        } catch (SQLException e) {
            throw new RuntimeException("getTally failed", e);
        }
    }

    @Override
    public List<Streak> leaderboard(int limit) {
        String sql = """
            SELECT player_name, bot_level, current_streak, best_streak
            FROM win_streaks
            ORDER BY best_streak DESC, player_name ASC
            LIMIT ?
            """;
        List<Streak> out = new ArrayList<>();
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(new Streak(rs.getString("player_name"), rs.getString("bot_level"),
                            rs.getInt("current_streak"), rs.getInt("best_streak")));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("leaderboard failed", e);
        }
        return out;
    }

    @Override
    public void saveGame(SavedGame g) {
        String sql = """
            INSERT INTO saved_games (slot_name, mode, board_state, next_player, player_x, player_o)
            VALUES (?, ?, ?, ?, ?, ?)
            ON CONFLICT (slot_name) DO UPDATE SET
                mode = EXCLUDED.mode,
                board_state = EXCLUDED.board_state,
                next_player = EXCLUDED.next_player,
                saved_at = now()
            """;
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, g.slot());
            ps.setString(2, g.mode().name());
            ps.setString(3, g.boardState());
            ps.setString(4, String.valueOf(g.nextPlayer()));
            ps.setString(5, "");
            ps.setString(6, "");
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("saveGame failed", e);
        }
    }

    @Override
    public Optional<SavedGame> loadGame(String slot) {
        String sql = "SELECT mode, board_state, next_player FROM saved_games WHERE slot_name = ?";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, slot);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new SavedGame(
                            slot,
                            GameMode.valueOf(rs.getString("mode")),
                            rs.getString("board_state"),
                            rs.getString("next_player").charAt(0)));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("loadGame failed", e);
        }
    }

    @Override
    public List<MatchRecord> recentMatches(int limit) {
        String sql = """
            SELECT mode, player_x, player_o, result, move_sequence
            FROM matches
            ORDER BY played_at DESC
            LIMIT ?
            """;
        List<MatchRecord> out = new ArrayList<>();
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(new MatchRecord(
                            GameMode.valueOf(rs.getString("mode")),
                            rs.getString("player_x"),
                            rs.getString("player_o"),
                            GameStatus.valueOf(rs.getString("result")),
                            rs.getString("move_sequence")));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("recentMatches failed", e);
        }
        return out;
    }

    @Override
    public List<Tally> allTallies(String playerName) {
        String sql = """
            SELECT bot_level, wins, losses, draws
            FROM score_tallies
            WHERE player_name = ?
            """;
        List<Tally> out = new ArrayList<>();
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, playerName);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(new Tally(
                            playerName,
                            rs.getString("bot_level"),
                            rs.getInt("wins"),
                            rs.getInt("losses"),
                            rs.getInt("draws")));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("allTallies failed", e);
        }
        return out;
    }

    @Override
    public Streak getStreak(String playerName, String botLevel) {
        String sql = """
            SELECT current_streak, best_streak
            FROM win_streaks
            WHERE player_name = ? AND bot_level = ?
            """;
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, playerName);
            ps.setString(2, botLevel);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Streak(playerName, botLevel,
                            rs.getInt("current_streak"),
                            rs.getInt("best_streak"));
                }
                return new Streak(playerName, botLevel, 0, 0);
            }
        } catch (SQLException e) {
            throw new RuntimeException("getStreak failed", e);
        }
    }

    @Override
    public List<String> listSavedSlots() {
        String sql = "SELECT slot_name FROM saved_games ORDER BY saved_at DESC";
        List<String> slots = new ArrayList<>();
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                slots.add(rs.getString("slot_name"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("listSavedSlots failed", e);
        }
        return slots;
    }

    @Override
    public void deleteSavedGame(String slot) {
        String sql = "DELETE FROM saved_games WHERE slot_name = ?";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, slot);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("deleteSavedGame failed", e);
        }
    }
}
