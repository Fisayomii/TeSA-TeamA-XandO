package com.tictactoe.ui;

/**
 * The GUI calls this when the player clicks a square.
 * Part 5 implements it.
 */
public interface BoardClickListener {

    void onCellClicked(int index);
}