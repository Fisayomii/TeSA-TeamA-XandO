package com.tictactoe.bot;

import com.tictactoe.model.GameMode;

public class BotFactory {

    public static Bot forMode(GameMode mode) {
        return switch (mode) {
            case PVE_EASY ->
                new EasyBot();
            case PVE_MEDIUM ->
                new MediumBot();
            case PVE_HARD ->
                new HardBot();
            case PVP ->
                throw new IllegalArgumentException("PVP has no bot");
        };
    }
}
