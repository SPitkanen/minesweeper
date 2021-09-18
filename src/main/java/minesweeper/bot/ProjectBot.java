
package minesweeper.bot;

import java.util.HashSet;
import java.util.Random;
import java.util.ArrayList;
import minesweeper.model.Board;
import minesweeper.model.GameStats;
import minesweeper.model.Move;
import minesweeper.model.MoveType;
import minesweeper.model.Highlight;
import minesweeper.model.Pair;
import minesweeper.model.Square;

/**
 * THIS CLASS IS A NEW ADDITION TO THE TEMPLATE BY:
 * @author santeripitkanen
 */

public class ProjectBot implements Bot {
    
    private ArrayList<Square> nonFlaggedSquares;
    private ArrayList<Square> suspectedMines;
    private GameStats gameStats;
    private int flagCount;
    private int notFlaggedCount;
    
    /**
     * Make a single decision based on the given Board state
     * @param board The current board state
     * @return Move to be made onto the board
     */
    @Override
    public Move makeMove (Board board) {
        HashSet<Square> openSquares = board.getOpenSquares();
        // bot will choose and open random square as first move
        if (openSquares.isEmpty()) {
            return randomMove(board);
        }
        // go through open squares
        for (Square square : openSquares) {
            // only consider squares that have adjacent mines
            if (square.surroundingMines() > 0) {
                checkSurroundingTiles(square, board);
                // if adjacent flagged + unopened and unflagged squares = starting squares' mine count,
                // all surrounding squares are mines
                if (this.notFlaggedCount == square.surroundingMines()) {
                    if (!this.suspectedMines.isEmpty()) {
                        return placeFlag();
                    }
                }
                // if square has as many adjacent flagged squares as starting squares' mine count
                // it's safe to open any surrounding unflagged square (assumes that flags are placed correctly)
                if (this.flagCount == square.surroundingMines()) {
                    if (!this.nonFlaggedSquares.isEmpty()) {
                        return openSquare();
                    }
                }
            }
        }
        // if it is not possible to make a move based on the two methods described above
        // the bot will make a random move
        return randomMove(board);
    }
    
    @Override
    public ArrayList<Move> getPossibleMoves(Board board) {
        ArrayList<Move> moves = new ArrayList<>();
        return moves;
    }
    
    @Override
    public void setGameStats(GameStats gameStats) {
        this.gameStats = gameStats;
    }
    
    /**
     * Check for surrounding unopenedtiles if they have been flagged or not. 
     * @param square starting squaro of which surrounding tiles we want to inspect
     * @param board current board status
     */
    public void checkSurroundingTiles(Square square, Board board) {
        this.flagCount = 0;
        this.notFlaggedCount = 0;
        this.suspectedMines = new ArrayList<>();
        this.nonFlaggedSquares = new ArrayList<>();
        // go through adjacent squares
        for (int x = square.getX() - 1; x < square.getX() + 2; x++) {
            for (int y = square.getY() - 1; y < square.getY() + 2; y++) {
                // check that new square is within board and it is not the original parameter square
                if (board.withinBoard(x, y) && notOriginalSquare(square, x, y)) {
                    // if square is not flagged, increase neutral square count by one
                    // add the square to suspected mines and non flagged squares lists
                    if (!board.getSquareAt(x, y).isFlagged() && !board.getSquareAt(x, y).isOpened()) {
                        this.notFlaggedCount++;
                        this.suspectedMines.add(board.getSquareAt(x, y));
                        this.nonFlaggedSquares.add(board.getSquareAt(x, y));
                    }
                    // if square is flagged, increase count but do not add to suspected mine list
                    if (board.getSquareAt(x, y).isFlagged() && !board.getSquareAt(x, y).isOpened()) {
                        this.notFlaggedCount++;
                    }
                    // if square is flagged, increase flag count by one
                    if (board.getSquareAt(x, y).isFlagged()) {
                        this.flagCount++;
                    }
                }
            }
        }
    }
    
     /**
     * Place Flag on first square on the suspectedMines list
     * @return FLAG move
     */
    public Move placeFlag() {
        Square mine = this.suspectedMines.get(0);
        return new Move(MoveType.FLAG, mine.getX(), mine.getY());
    }
    
    /**
     * Open firs square on nonFlaggedSquares list
     * @return OPEN move
     */
    public Move openSquare() {
        Square safeSquare =  this.nonFlaggedSquares.get(0);
        return new Move(MoveType.OPEN, safeSquare.getX(), safeSquare.getY());
    }
    
    /**
     * Make random move
     * @param board current board status
     * @return random move OPEN
     */
    public Move randomMove(Board board) {
        Square rngSquare = new Square(-1, -1);
        rngSquare = randomSquare(board);
        return new Move(MoveType.OPEN, rngSquare.getX(), rngSquare.getY());
    }
    
    /**
     * Make random square choice
     * @param board current board status
     * @return random unopened and unflagged square
     */
    public Square randomSquare(Board board) {
        Random random = new Random();
        Square square = new Square(-1, -1);
        while (true) {
            int x = random.nextInt(board.width);
            int y = random.nextInt(board.height);
            // check that square is unopened and unflagged
            if (!board.getSquareAt(x, y).isOpened() && !board.getSquareAt(x, y).isFlagged()) {
                square = board.getSquareAt(x, y);
                break;
            }
        }
        return square;
    }
    
    /**
     * Check that chosen square is not the original
     * @param square original square
     * @param x x axis position of square to be checked
     * @param y y axis position of square to be checked
     * @return boolean value, false if original square, else true
     */
    public boolean notOriginalSquare(Square square, int x, int y) {
        if (x == square.getX() && y == square.getY()) {
            return false;
        }
        return true;
    }
}
