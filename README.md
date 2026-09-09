# Tic Tac Toe — Team A (X and O)

A Java desktop game: play a friend on one keyboard, or play the computer at three
skill levels. Results, tallies, and win streaks are stored in PostgreSQL.

> **This is the team working copy — the folder structure only, no implementation yet.**
> You write the code here. A complete, working reference implementation of every
> file lives in the sibling folder **`../tic-tac-toe_app`** — consult it only when
> the 20-minute rule in `SPRINT_2DAY.md` kicks in.
>
> Each `src/**` package folder currently holds just a `.gitkeep` placeholder;
> delete it once you add real files there. `pom.xml`, `src/main/resources/schema.sql`,
> `setup/`, `.github/`, and the two planning documents are kept as-is (they are frozen).

| | |
|---|---|
| **Language** | Java 17+ |
| **GUI** | Swing (built into the JDK) |
| **Database** | PostgreSQL 16+ via JDBC |
| **Build** | Maven |
| **Tests** | JUnit 5 |

## Documents

- **`SPRINT_2DAY.md`** — the 2-day, 5-person plan. Start here.
- **`EXECUTION_PLAN.md`** — the full reference manual: every tool and Java concept
  explained, every file walked through. Use it to understand the code.

## Run it

```bash
# 1. one-time setup (creates the DB, loads the schema, sets env vars)
#    Windows:
./setup/setup-windows.ps1
#    macOS / Linux:
bash setup/setup-mac-linux.sh

# 2. close and reopen your terminal, then:
mvn clean verify          # compile + run tests
mvn compile exec:java     # launch the app
```

If PostgreSQL is not reachable the app still runs, using in-memory scores that are
lost when it closes (you will see a `[db]` warning in the console).

### Package a single runnable file

```bash
mvn clean package
java -jar target/tic-tac-toe-1.0.0-shaded.jar
```

## Project layout

```
src/main/java/com/tictactoe/
  model/   shared enums (Mark, GameStatus, GameMode, ResultForHuman)
  engine/  the rules: Board, GameEngine                          (Part 1)
  bot/     EasyBot, MediumBot, HardBot, BotFactory               (Part 2)
  db/      ScoreRepository + JDBC and in-memory implementations  (Part 3)
  ui/      Swing screens: MainWindow, BoardPanel, ...            (Part 4)
  app/     GameController, PlayerNames, Main                     (Part 5)
src/main/resources/schema.sql
src/test/java/com/tictactoe/...   unit tests
```

## Team

| Part | Package | Owner |
|------|---------|-------|
| 1 — Game engine | `engine` | |
| 2 — Bot AI | `bot` | |
| 3 — Database | `db` | |
| 4 — GUI | `ui` | |
| 5 — Integration & multiplayer | `app` | |
