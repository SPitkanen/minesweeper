
package minesweeper.bot;

import java.util.HashSet;
import java.util.Random;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import minesweeper.model.Board;
import minesweeper.model.GameStats;
import minesweeper.model.Highlight;
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
        moves = findMoves(board);
        return moves;
    }
    
    @Override
    public void setGameStats(GameStats gameStats) {
        this.gameStats = gameStats;
    }
    
    /**
     * Find possible move to make. 
     * Algorithm gets all open squares that have adjacent mines and enters a loop while game is not over.
     * Algorithm inserts these squares into safe squares and goes through them while trying to find instances of AFN
     * AFN = All Free Neighbors; Surrounding mines = surrounding flags, meaning all of the remaining squares must be empty and can be opened
     * this happens by placing them to safeSquares. If not, the original square is placed to unsafeSquares for further inspection.
     * If safeSquares is empty, algorithm will try to find instances of AMN = All Mined Neighbors; placed flags + remaining unopened tiles = mine count
     * which means that remaining surrounding tiles are mines and can be flagged. This square is then removed from unsafeSquares.
     * After instances of AMN have been found, the algorithm will try to find AFMs from unsafeSquares.
     * This round will also finalise the pair list (Pair = two remaining untouched squares that must contain one mine)
     * 
     * If this previous piece of algorithm has not resulted in changed board state, it will try to 
     * deduce AFM and AMN squares by including surrounding pairs as one square (that contains mine).
     * If this still has not yelded results, only possible move is random move.
     * 
     * Change is used to stop the algorithm from entering an endless loop ie. only in case of a change on the board, it will continue.
     * 
     * @param board current board state
     * @return move to be made
     */
    public Move DSSP(Board board) {
        // Get all open squares with adjacent mines
        this.safeSquares = new ArrayList<>();
        HashSet<Square> openedSquares = board.getOpenSquares();
        openedSquares.stream().filter(s -> s.surroundingMines() > 0).forEach(s -> safeSquares.add(s));
        this.unsafeSquares = new ArrayList<>();
        while (!board.gameLost || !board.gameWon) {
            boolean change = false;
            if (safeSquares.isEmpty() && board.getUnopenedSquaresCount() > board.totalMines) {
                // Check that the game is not over
                if (board.gameLost || board.gameWon) {
                    break;
                }
                // if there are no squares in safeSquares (for example first move) select a random square
                Square rnd2 = randomSquare(board);
                safeSquares.add(rnd2);
            }
            // Go through safeSquares one by one selected square is removed from the list
            // when safeSquares is empty. move to unsafeSquares
            while (!safeSquares.isEmpty()) {
                Square first = safeSquares.get(0);
                safeSquares.remove(0);
                if (!first.isOpened()) {
                    // If selected square is unopened, it will be opened which results in a change of board state
                    change = true;
                    return openSquare(first);
                }
                if (first.isMine()) {
                    break;
                }
                this.pairs = new HashSet<>();
                // If all adjacent squares are free (AFN, Squares mine count = surrounding flags) all unopened surrounding squares are 
                // safe to insert to safeSquares, if not square is not secure and is placed into unsafeSquares.
                if (AFN(first, board)) {
                    safeSquares.addAll(this.unmarkedNeighbors);
                } else {
                    unsafeSquares.add(first);
                }
            }
            if (unsafeSquares.size() > 0) {
                // Go through unsafe squares and try to find instances of AMN (All of the remaining unopened adjacent tiles must be mines)
                // and place a flag on them. In case of AMN, tile is now secured and can be removed from the unsafeSquares list
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
                // Since the board state has likely changed, algorithm will go through AFN:s once more. 
                for (int i = unsafeSquares.size() - 1; i > -1; i--) {
                    if (AFN(unsafeSquares.get(i), board)) {
                        safeSquares.addAll(this.unmarkedNeighbors);
                        change = true;
                        unsafeSquares.remove(i);
                    }
                }
            }
            // In case previous attempts to find AFMs and AMNs does not result in a changed board state
            // algorithm will try to deduce solution using pairs (Pair = one mine in either one of two squares adjacent to 
            // square under inspection
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
            // If board state has not changed so far, break loop
            // and return random move
            if (change == false) {
                break;
            }
        }
        return randomMove(board);
    }
    
    /**
     * Find possible moves to make. 
     * findMoves goes through all opened squares trying to find instances of AFN or AMN. While doing so it also collects all
     * pairs available. The m,ethod then searches for AFNs and AMNs by using these pairs. Possible moves are first added to HashSet
     * to prevent duplicates and then converted to ArrayList for return.
     * 
     * @param board current board state
     * @return ArrayList of possible moves
     * 
     */
    public ArrayList findMoves(Board board) {
        ArrayList<Square> safeSquare = new ArrayList<>();
        HashSet<Move> moves = new HashSet<>();
        HashSet<Square> openedSquares = board.getOpenSquares();
        openedSquares.stream().filter(s -> s.surroundingMines() > 0).forEach(s -> safeSquare.add(s));
        this.pairs = new HashSet<>();
        for (int i = 0; i < safeSquare.size(); i++) {
            if (AFN(safeSquare.get(i), board)) {
                this.unmarkedNeighbors.stream().forEach(s -> moves.add(this.highlightOpen(s)));
            } else if (AMN(safeSquare.get(i), board)){
                this.unmarkedNeighbors.stream().forEach(s -> moves.add(this.highlightFlag(s)));
            }
        }
        for (int i = 0; i < safeSquare.size(); i++) {
            if (this.AFNwithPairs(safeSquare.get(i), board)) {
                this.unmarkedNeighbors.stream().forEach(s -> moves.add(this.highlightOpen(s)));
            } else if (this.AMNwithPairs(safeSquare.get(i), board)){
                this.unmarkedNeighbors.stream().forEach(s -> moves.add(this.highlightFlag(s)));
            }
        }
        ArrayList<Move> movesToReturn = new ArrayList<>();
        moves.stream().forEach(s -> movesToReturn.add(s));
        return movesToReturn;
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
        if (this.unmarkedNeighbors.size() == 2 && this.flags + 1 == square.surroundingMines()) {
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
        if (this.flags + this.unopened - 1 == square.surroundingMines() && this.pairCount == 1) {
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
        if (this.pairCount + this.flags == square.surroundingMines() && this.pairCount == 1) {
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
    
    public Move highlightOpen(Square square) {
        return new Move(square.getX(), square.getY(), Highlight.GREEN);
    }
    
    public Move highlightFlag(Square square) {
        return new Move(square.getX(), square.getY(), Highlight.RED);
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
