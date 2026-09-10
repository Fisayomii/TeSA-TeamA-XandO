package com.tictactoe.app;

import com.tictactoe.bot.Bot;
import com.tictactoe.bot.BotFactory;
import com.tictactoe.db.MatchRecord;
import com.tictactoe.db.SavedGame;
import com.tictactoe.db.ScoreRepository;
import com.tictactoe.engine.Board;
import com.tictactoe.engine.GameEngine;
import com.tictactoe.model.GameMode;
import com.tictactoe.model.GameStatus;
import com.tictactoe.model.Mark;
import com.tictactoe.model.ResultForHuman;
import com.tictactoe.ui.BoardClickListener;
import com.tictactoe.ui.GameView;

public class GameController implements BoardClickListener {

    private static final String QUICKSAVE_SLOT = "quicksave";

    private final GameView view;
    private final ScoreRepository repository;
    private Bot bot;

    private GameEngine engine;
    private GameMode mode;
    private String playerXName;
    private String playerOName;

    public GameController(GameView view, ScoreRepository repository) {
        this.view = view;
        this.repository = repository;
    }

    public void newGame(GameMode mode, String playerXName, String playerOName) {
        this.mode = mode;
        this.playerXName = playerXName;
        this.playerOName = mode.isVsBot() ? "CPU" : playerOName;
        this.bot = mode.isVsBot() ? BotFactory.forMode(mode) : null;
        this.engine = new GameEngine();
        view.showBoard(engine.getBoard().serialize());
        updateStatusForTurn();
    }

    @Override
    public void onCellClicked(int index) {
        if (engine.status() != GameStatus.IN_PROGRESS) return;
        if (mode.isVsBot() && engine.getCurrentPlayer() == Mark.O) return;

        engine.move(index);
        view.showBoard(engine.getBoard().serialize());

        if (checkGameOver()) return;
        updateStatusForTurn();

        if (mode.isVsBot() && engine.getCurrentPlayer() == Mark.O) {
            triggerBotMove();
        }
    }

    private void triggerBotMove() {
        view.runInBackground(
                () -> bot.chooseMove(engine.getBoard(), Mark.O),
                move -> {
                    engine.move(move);
                    view.showBoard(engine.getBoard().serialize());
                    if (!checkGameOver()) {
                        updateStatusForTurn();
                    }
                }
        );
    }

    private boolean checkGameOver() {
        GameStatus status = engine.status();
        if (status == GameStatus.IN_PROGRESS) return false;

        if (engine.winningLine() != null) {
            view.highlightWinningLine(engine.winningLine());
        }
        finish(status);
        return true;
    }

    private void finish(GameStatus status) {
        repository.saveMatch(new MatchRecord(mode, playerXName, playerOName, status, engine.moveSequence()));

        if (mode.isVsBot()) {
            ResultForHuman result = switch (status) {
                case X_WON -> ResultForHuman.WIN;
                case O_WON -> ResultForHuman.LOSS;
                case DRAW -> ResultForHuman.DRAW;
                case IN_PROGRESS -> throw new IllegalStateException("Game isn't over");
            };
            repository.applyResult(playerXName, mode.botLevel(), result);
        }

        view.showGameOver(gameOverMessage(status));
        view.showScoreboard(repository.allTallies(playerXName), repository.leaderboard(10));
    }

    private String gameOverMessage(GameStatus status) {
        return switch (status) {
            case X_WON -> playerXName + " (X) wins!";
            case O_WON -> playerOName + " (O) wins!";
            case DRAW -> "It's a draw!";
            case IN_PROGRESS -> "";
        };
    }

    private void updateStatusForTurn() {
        String name = (engine.getCurrentPlayer() == Mark.X) ? playerXName : playerOName;
        view.setStatus(name + "'s turn (" + engine.getCurrentPlayer() + ")");
    }

    public void saveAndExit() {
        repository.saveGame(new SavedGame(
                QUICKSAVE_SLOT, mode, engine.getBoard().serialize(), markToChar(engine.getCurrentPlayer())
        ));
        view.showMenu();
    }

    public void resumeQuicksave() {
        repository.loadGame(QUICKSAVE_SLOT).ifPresentOrElse(save -> {
            this.mode = save.mode();
            this.playerXName = PlayerNames.ask("Player X name:", "Player 1");
            this.playerOName = mode.isVsBot() ? "CPU" : PlayerNames.ask("Player O name:", "Player 2");
            this.bot = mode.isVsBot() ? BotFactory.forMode(mode) : null;

            Board board = Board.deserialize(save.boardState());
            Mark nextPlayer = charToMark(save.nextPlayer());
            this.engine = new GameEngine(board, nextPlayer);

            view.showBoard(engine.getBoard().serialize());
            updateStatusForTurn();
        }, () -> {
            view.setStatus("No saved game found");
            view.showMenu();
        });
    }

    private char markToChar(Mark mark) {
        return switch (mark) {
            case X -> 'X';
            case O -> 'O';
            case EMPTY -> '_';
        };
    }

    private Mark charToMark(char c) {
        return switch (c) {
            case 'X' -> Mark.X;
            case 'O' -> Mark.O;
            default -> Mark.EMPTY;
        };
    }
}