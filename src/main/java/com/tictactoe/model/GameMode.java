package com.tictactoe.model;
public enum GameMode {
    PVP,
    // Player vs Player (local, two humans)
    PVE_EASY,
    PVE_MEDIUM,
    PVE_HARD;
    // Player vs Environment (computer), easy bot
    public boolean isVsBot() { return this != PVP; }
}
/** "EASY" / "MEDIUM" / "HARD" for the three bot modes. */
public String botLevel() {
    if (this == PVP) throw new IllegalStateException("PVP has no bot level");
    return name().substring("PVE_".length());
}
}