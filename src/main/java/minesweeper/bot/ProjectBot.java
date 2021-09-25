
package minesweeper.bot;

import java.util.HashSet;
import java.util.Random;
import java.util.ArrayList;
import java.util.HashMap;
import minesweeper.model.Board;
import minesweeper.model.GameStats;
import minesweeper.model.Move;
import minesweeper.model.MoveType;
import minesweeper.model.Highlight;
import minesweeper.model.Pair;
import minesweeper.model.Square;
import minesweeper.model.BotSquare;
import minesweeper.model.BotPair;
/**
 * THIS CLASS IS A NEW ADDITION TO THE TEMPLATE BY:
 * @author santeripitkanen
 */

public class ProjectBot implements Bot {
    
    private ArrayList<Square> untouchedSquares;
    private GameStats gameStats;
    private int flagCount;
    private int untouchedSquareCount;
    private int minesLeft;
    private int rounds;
    private int previous;
    private int pairsContainingMinesCount;
    private HashMap<Square, BotSquare> openSquaresMap; 
    private ArrayList<BotPair> pairs;
    private ArrayList<Square> untouchedSquaresExclPairs;
    
    public void ProjectBot() {
    this.rounds = 0;
    this.previous = 0;
}
    
    /**
     * Make a single decision based on the given Board state
     * @param board The current board state
     * @return Move to be made onto the board
     */
    @Override
    public Move makeMove (Board board) {
        // get current open squares
        HashSet<Square> openSquares = board.getOpenSquares();
        // bot will choose and open random square as first move
        if (openSquares.isEmpty()) {
            this.openSquaresMap = new HashMap<>();
            updateOpenSquaresMap(openSquares, board);
            this.pairs = new ArrayList<>();
            return randomMove(board);
        }
        boolean move = false;
        this.rounds++;
        updateOpenSquaresMap(openSquares, board);
        // go through open squares
        for (Square square : openSquares) {
            // only consider squares that have adjacent mines
            if (square.surroundingMines() > 0) {
                checkSurroundingTiles(square, board);
                // if adjacent flagged + unopened and unflagged squares = starting squares' mine count,
                // all surrounding squares are mines
                if (this.untouchedSquareCount + this.flagCount == square.surroundingMines()) {
                    if (!this.untouchedSquares.isEmpty()) {
                        move = true;
                        return placeFlag(this.untouchedSquares);
                    }
                }
                // if square has as many adjacent flagged squares as starting squares' mine count
                // it's safe to open any surrounding unflagged square (assumes that flags are placed correctly)
                if (this.minesLeft == 0) {
                    if (!this.untouchedSquares.isEmpty()) {
                        move = true;
                        return openSquare(this.untouchedSquares);
                    }
                }
                // if bot find square that is adjacent to a pair containing one mine + current flag count = surrounding mine count
                // it is safe to open any other untouched square (not squares included in the mined pair)
                if (this.pairsContainingMinesCount + this.flagCount == square.surroundingMines()) {
                    if (!this.untouchedSquaresExclPairs.isEmpty()) {
                        move = true;
                        return openSquare(this.untouchedSquaresExclPairs);
                    }
                }
                // if pairs containing mines + flags + (all untouched squares - untouched squares in mined pairs) = surrounding mine count
                //it is safe to place falg on any untouched square not in mined pair
                if (this.pairsContainingMinesCount + this.flagCount + this.untouchedSquaresExclPairs.size() == square.surroundingMines() && this.untouchedSquaresExclPairs.size() == 1) {
                    if (!this.untouchedSquaresExclPairs.isEmpty()) {
                        move = true;
                        return placeFlag(this.untouchedSquaresExclPairs);
                    }
                }
            }
        }
        // if there are still pairs left and program has gone through one round
        // go through it again before making random move
        if (!this.pairs.isEmpty() && this.rounds == this.previous + 1) {
            makeMove(board);
        }
        this.rounds = this.previous;
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
     * Update list containing opened squares with new ones
     * @param openSquares starting square list
     * @param board current board status
     */
    public void updateOpenSquaresMap(HashSet<Square> openSquares, Board board) {
        for (Square square : openSquares) {
            if (square.surroundingMines() > 0 && !this.openSquaresMap.containsKey(square)) {
            BotSquare newSquare = new BotSquare(square, board);
            this.openSquaresMap.put(square, newSquare);
            }
        }
    }
    
    /**
     * Check for surrounding tiles status and get them from BotSquare 
     * @param square starting squaro of which surrounding tiles we want to inspect
     * @param board current board status
     */
    public void checkSurroundingTiles(Square square, Board board) {
        this.flagCount = 0;
        this.untouchedSquareCount = 0;
        this.untouchedSquares = new ArrayList<>();
        this.pairsContainingMinesCount = 0;
        this.untouchedSquaresExclPairs = new ArrayList<>();
        
        
        if (this.openSquaresMap.containsKey(square)) {
            BotSquare botSquare = this.openSquaresMap.get(square);
            botSquare.refreshStats(this.pairs);
            this.untouchedSquareCount = botSquare.getUntouchedSquaresCount();
            this.flagCount = botSquare.getFlagCount();
            this.minesLeft = botSquare.getMinesLeft();
            this.untouchedSquares = botSquare.getUntouchedSquares();
            this.untouchedSquaresExclPairs = botSquare.getUntouchedExclPairs();
            this.pairsContainingMinesCount = botSquare.getCountOfMinedPairs();
            this.pairs = botSquare.getNewAllPairsList();
        } else {
            BotSquare newSquare = new BotSquare(square, board);
            newSquare.refreshStats(this.pairs);
            this.openSquaresMap.put(square, newSquare);
            this.flagCount = newSquare.getFlagCount();
            this.minesLeft = newSquare.getMinesLeft();
            this.untouchedSquares = newSquare.getUntouchedSquares();
            this.untouchedSquaresExclPairs = newSquare.getUntouchedExclPairs();
            this.pairsContainingMinesCount = newSquare.getCountOfMinedPairs();
            this.pairs = newSquare.getNewAllPairsList();
        }
    }
    
     /**
     * Place Flag on first square on the suspectedMines list
     * @return FLAG move
     */
    public Move placeFlag(ArrayList<Square> list) {
        Square mine = list.get(0);
        return new Move(MoveType.FLAG, mine.getX(), mine.getY());
    }
    
    /**
     * Open firs square on nonFlaggedSquares list
     * @return OPEN move
     */
    public Move openSquare(ArrayList<Square> list) {
        Square safeSquare =  list.get(0);
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
