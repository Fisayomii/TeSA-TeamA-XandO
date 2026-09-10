package com.tictactoe.db;
public record Tally(String playerName, String botLevel, int wins, int losses, int draws) {
    public int total() { return wins + losses + draws; }
}