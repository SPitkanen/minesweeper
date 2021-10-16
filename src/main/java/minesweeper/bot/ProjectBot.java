
package minesweeper.bot;

import java.util.HashSet;
import java.util.Random;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import minesweeper.model.Board;
import minesweeper.model.GameStats;
import minesweeper.model.Move;
import minesweeper.model.MoveType;
import minesweeper.model.Pair;
import minesweeper.model.Square;
/**
 * THIS CLASS IS A NEW ADDITION TO THE TEMPLATE BY:
 * @author santeripitkanen
 */

public class ProjectBot implements Bot {
    
    private GameStats gameStats;
    public int flags;
    public int unopened;
    public int pairCount;
    public HashSet<Pair> pairs;
    private ArrayList<Square> unmarkedNeighbors;
    private ArrayList<Square> safeSquares;
    private ArrayList<Square> unsafeSquares;
    private BlockingQueue<Move> queue;
    
    public void ProjectBot() {
        this.flags = 0;
        this.pairCount = 0;
        this.unopened = 0;
        this.unmarkedNeighbors = new ArrayList<>();
        this.pairs = new HashSet<>();
    }
    
    /**
     * Make a single decision based on the given Board state
     * @param board The current board state
     * @return Move to be made onto the board
     */
    @Override
    public Move makeMove (Board board) {
        return DSSP(board);
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
    
    public Move DSSP(Board board) {
        this.safeSquares = new ArrayList<>();
        HashSet<Square> openedSquares = board.getOpenSquares();
        openedSquares.stream().filter(s -> s.surroundingMines() > 0).forEach(s -> safeSquares.add(s));
        this.unsafeSquares = new ArrayList<>();
        while (!board.gameLost || !board.gameWon) {
            boolean change = false;
            if (safeSquares.isEmpty() && board.getUnopenedSquaresCount() > board.totalMines) {
                if (board.gameLost || board.gameWon) {
                    break;
                }
                Square rnd2 = randomSquare(board);
                safeSquares.add(rnd2);
            }
            while (!safeSquares.isEmpty()) {
                Square first = safeSquares.get(0);
                safeSquares.remove(0);
                if (!first.isOpened()) {
                    change = true;
                    return openSquare(first);
                }
                if (first.isMine()) {
                    break;
                }
                this.pairs = new HashSet<>();
                if (AFN(first, board)) {
                    safeSquares.addAll(this.unmarkedNeighbors);
                } else {
                    unsafeSquares.add(first);
                }
            }
            if (unsafeSquares.size() > 0) {
                for (int i = unsafeSquares.size() - 1; i > -1; i--) {
                    if (AMN(unsafeSquares.get(i), board)) {
                        for (int k = 0; k < this.unmarkedNeighbors.size(); k++) {
                            change = true;
                            return placeFlag(this.unmarkedNeighbors.get(k));
                        }
                        unsafeSquares.remove(i);
                    }

                }
            }
            if (unsafeSquares.size() > 0) {
                this.pairs = new HashSet<>();
                for (int i = unsafeSquares.size() - 1; i > -1; i--) {
                    if (AFN(unsafeSquares.get(i), board)) {
                        safeSquares.addAll(this.unmarkedNeighbors);
                        unsafeSquares.remove(i);
                    }
                }
            }
            if (change == false) {
                if (unsafeSquares.size() > 0) {
                    for (int i = unsafeSquares.size() - 1; i > -1; i--) {
                        if (AMNwithPairs(unsafeSquares.get(i), board)) {
                            for (int k = 0; k < this.unmarkedNeighbors.size(); k++) {
                                change = true;
                                return placeFlag(this.unmarkedNeighbors.get(k));
                            }
                            unsafeSquares.remove(i);
                        }

                    }
                }
                if (unsafeSquares.size() > 0) {
                    for (int i = unsafeSquares.size() - 1; i > -1; i--) {
                        if (AFNwithPairs(unsafeSquares.get(i), board)) {
                            for (int k = 0; k < this.unmarkedNeighbors.size(); k++) {
                                change = true;
                                return openSquare(this.unmarkedNeighbors.get(k));
                            }
                            unsafeSquares.remove(i);
                        }

                    }
                }
            }
            if (change == false) {
                break;
            }
        }
        return randomMove(board);
    }
    
    /**
     * Check if square is AFN. If aknown adjacent flags = total surrounding mines,
     * all of the remaining untouched squares must be free of mines and can be safely opened
     * @param square square under inspection
     * @param board current board status
     * @return boolean value
     */
    public boolean AFN(Square square, Board board) {
        zeroValues();
        checkSurroundingSquares(square, board);
        if (this.unmarkedNeighbors.size() == 2) {
            Pair<Square> pair = new Pair(this.unmarkedNeighbors.get(0), this.unmarkedNeighbors.get(1));
            this.pairs.add(pair);
        }
        if (this.flags == square.surroundingMines()) {
            return true;
        }
        return false;
    }
    
    /**
     * Check if square is AMN. If adjacent untouched squares + known adjacent flags = total surrounding mines,
     * all of the remaining untouched squares must be mines and can be flagged
     * @param square square under inspection
     * @param board current board status
     * @return boolean value
     */
    public boolean AMN(Square square, Board board) {
        zeroValues();
        checkSurroundingSquares(square, board);
        if (this.unopened + this.flags == square.surroundingMines()) {
            return true;
        }
        
        return false;
    }
    
    /**
     * Check if square is AMN, but consider pairs as one mined square
     * @param square square under inspection
     * @param board current board status
     * @return boolean value
     */
    public boolean AMNwithPairs(Square square, Board board) {
        zeroValues();
        checkSurroundingSquares(square, board);
        checkSurroundingPairs(square, board);
        if (this.flags + this.unopened - 1 == square.surroundingMines() && this.pairCount > 0) {
            return true;
        }
        return false;
    }
    
    /**
     * Check if square is AFN, but consider pairs as one mined square
     * @param square square under inspection
     * @param board current board status
     * @return boolean value
     */
    public boolean AFNwithPairs (Square square, Board board) {
        zeroValues();
        checkSurroundingSquares(square, board);
        checkSurroundingPairs(square, board);
        if (this.pairCount + this.flags == square.surroundingMines() && this.pairCount > 0) {
            return true;
        }
        return false;
    }
    
    /**
     * Zero all values that are used when searching through adjacent squares
     */
    public void zeroValues() {
        this.flags = 0;
        this.unopened = 0;
        this.pairCount = 0;
        this.unmarkedNeighbors = new ArrayList<>();
    }
    
    /**
     * Check if square has adjacent flagged, opened or unopened squares
     * @param square square under inspection
     * @param board current board status
     */
    public void checkSurroundingSquares(Square square, Board board) {
        for (int x = square.getX() - 1; x < square.getX() + 2; x++) {
            for (int y = square.getY() - 1; y < square.getY() + 2; y++) {
                // check that new square is within board and it is not the original parameter square
                if (board.withinBoard(x, y) && notOriginalSquare(square, x, y)) {
                    if (board.getSquareAt(x, y).isFlagged()) {
                        this.flags++;
                    }
                    if (!board.getSquareAt(x, y).isFlagged() && !board.getSquareAt(x, y).isOpened()) {
                       this.unopened++;
                       this.unmarkedNeighbors.add(board.getSquareAt(x, y));
                    }
                }
            }
        }
    }
    
    /**
     * Check if square has known adjacent pairs, stop search is pair is found
     * @param square square under inspection
     * @param board current board status
     * @return boolean value
     */
    public boolean checkSurroundingPairs(Square square, Board board) {
        if (this.unopened > 2) {
            for (int i = 0; i < this.unopened; i++) {
                for (int k = 0; k < this.unopened; k++) {
                    Pair<Square> pair = new Pair(this.unmarkedNeighbors.get(i), this.unmarkedNeighbors.get(k));
                    if (this.pairs.contains(pair)) {
                        Square square1 = this.unmarkedNeighbors.get(i);
                        Square square2 = this.unmarkedNeighbors.get(k);
                        this.unmarkedNeighbors.remove(square1);
                        this.unmarkedNeighbors.remove(square2);
                        this.pairCount++;
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
     /**
     * Place Flag on square
     * @param square to be flagged
     * @return FLAG move
     */
    public Move placeFlag(Square square) {
        return new Move(MoveType.FLAG, square.getX(), square.getY());
    }
    
    /**
     * Open square
     * @param square to be opened
     * @return OPEN move
     */
    public Move openSquare(Square square) {
        return new Move(MoveType.OPEN, square.getX(), square.getY());
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
