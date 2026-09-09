-- Tic Tac Toe database schema. Safe to run every time the app starts.

CREATE TABLE IF NOT EXISTS matches (
    id            BIGSERIAL PRIMARY KEY,
    mode          VARCHAR(20)  NOT NULL,           -- PVP / PVE_EASY / PVE_MEDIUM / PVE_HARD
    player_x      VARCHAR(50)  NOT NULL,
    player_o      VARCHAR(50)  NOT NULL,
    result        VARCHAR(20)  NOT NULL,           -- X_WON / O_WON / DRAW
    move_sequence VARCHAR(40),                     -- e.g. "4,0,8,2,6"
    played_at     TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS score_tallies (
    player_name VARCHAR(50) NOT NULL,
    bot_level   VARCHAR(20) NOT NULL,              -- EASY / MEDIUM / HARD
    wins        INTEGER NOT NULL DEFAULT 0,
    losses      INTEGER NOT NULL DEFAULT 0,
    draws       INTEGER NOT NULL DEFAULT 0,
    PRIMARY KEY (player_name, bot_level)
);

CREATE TABLE IF NOT EXISTS win_streaks (
    player_name    VARCHAR(50) NOT NULL,
    bot_level      VARCHAR(20) NOT NULL,
    current_streak INTEGER NOT NULL DEFAULT 0,
    best_streak    INTEGER NOT NULL DEFAULT 0,
    PRIMARY KEY (player_name, bot_level)
);

CREATE TABLE IF NOT EXISTS saved_games (
    slot_name   VARCHAR(50) PRIMARY KEY,
    mode        VARCHAR(20) NOT NULL,
    board_state CHAR(9)     NOT NULL,              -- "X__O_X__O", '_' = empty
    next_player CHAR(1)     NOT NULL,              -- 'X' or 'O'
    player_x    VARCHAR(50) NOT NULL,
    player_o    VARCHAR(50) NOT NULL,
    saved_at    TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_streaks_best ON win_streaks (best_streak DESC);
