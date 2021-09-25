
package minesweeper.model;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * THIS CLASS IS A NEW ADDITION TO THE TEMPLATE BY:
 * @author santeripitkanen
 * BASED ON THE TEMPLATE SQUARE CLASS
 */

/**
 * Represent a single square on the board.
 * All the methods regarding the information of this square, 
 * i.e. amount of surrounding mines and whether this Square has a mine are only accessible if the square has been opened
 * @see Board
 */
public class BotSquare {
    private int surroundingMines; //Number of surrounding squares with mines
    private int locationX; 
    private int locationY;
    private int untouchedSquaresCount;
    private int numberOfFlags;
    private int openedSquaresCount;
    private int minesLeft;
    private int minedPairsCount;
    private ArrayList<Square> surroundingSquaresList;
    private ArrayList<Square> surroundingUntouchedSquares;
    private ArrayList<BotPair> surroundingPairsList;
    private ArrayList<BotPair> otherPairsList;
    private ArrayList<BotPair> pairsContainingMine;
    private ArrayList<BotPair> newAllPairsList;
    private ArrayList<Square> untouchedSquaresExclPairs;
    private ArrayList<BotPair> allTouchingMinedPairs;
    private Board board;
    private Square square;
    private boolean squareIsSolved;

    public Highlight highlight = Highlight.NONE;

    /**
     * 
     *
     * @param square Square to be inspected
     */
    public BotSquare(Square square, Board board) {
        this.square = square;
        this.board = board;
        this.surroundingMines = square.surroundingMines();
        this.surroundingSquaresList = new ArrayList<>();
        this.surroundingPairsList = new ArrayList<>();
        this.locationX = square.getX();
        this.locationY = square.getY();
        this.squareIsSolved = false; 
        
        listSurroundingSquares();
    }
    
    /**
     * Make list of squares that are adjacent to starting square
     */
    public void listSurroundingSquares() {
        for (int x = square.getX() - 1; x < square.getX() + 2; x++) {
            for (int y = square.getY() - 1; y < square.getY() + 2; y++) {
                // check that new square is within board and it is not the original parameter square
                if (board.withinBoard(x, y) && notOriginalSquare(this.square, x, y)) {
                    this.surroundingSquaresList.add(this.board.getSquareAt(x, y));
                }
            }
        }
    }
    
    /**
     * Refresh all data regarding surrounding squares and pairs
     * @param all list of previous pairs
     */
    public void refreshStats(ArrayList<BotPair> allPairs) {
        if (!isSquareSolved()) {
            this.numberOfFlags = 0;
            this.openedSquaresCount = 0;
            this.untouchedSquaresCount = 0;
            this.otherPairsList = new ArrayList<>();
            this.surroundingUntouchedSquares = new ArrayList<>();
            // check surroundings for unopened flags, opened squares and untouched squares
            for (int i = 0; i < this.surroundingSquaresList.size(); i++) {
                if (this.surroundingSquaresList.get(i).isFlagged()) {
                    this.numberOfFlags++;
                }
                if (this.surroundingSquaresList.get(i).isOpened()) {
                    this.openedSquaresCount++;
                }
                if (!this.surroundingSquaresList.get(i).isFlagged() && !this.surroundingSquaresList.get(i).isOpened()) {
                    this.surroundingUntouchedSquares.add(this.surroundingSquaresList.get(i));
                    this.untouchedSquaresCount++;
                }
            }
         
            this.minesLeft = this.surroundingMines - this.numberOfFlags;
            
            // Update All Pairs list by removing any pair that contains flagged or opened square
            // PAIR  for now = set of two squares that contain one mine in either one
            // this can be used as "one square containing mine" to deduce possible moves
            this.newAllPairsList = allPairs;
            for (int i = this.newAllPairsList.size() - 1; i > -1 ; i--) {
                if (this.newAllPairsList.get(i).getFirst().isFlagged() || this.newAllPairsList.get(i).getFirst().isOpened() || this.newAllPairsList.get(i).getSecond().isFlagged() || this.newAllPairsList.get(i).getSecond().isOpened()){
                    this.newAllPairsList.remove(i);
                }
            }  
            
            // add any pair that might surround the square
            if (!this.surroundingUntouchedSquares.isEmpty() && this.untouchedSquaresCount == 2 && this.minesLeft == 1) {
                BotPair pair = makePair(this.surroundingUntouchedSquares, 0, 1);
                boolean samePair = false;
                for (int i = this.newAllPairsList.size() - 1; i > -1 ; i--) {
                    if (this.newAllPairsList.get(i).samePair(pair)) {
                        samePair = true;
                    }
                    
                } 
                if (!samePair) {
                    this.newAllPairsList.add(pair);
                } 
            }
            // Collect all previously mapped pairs where pairs both squares are adjacent to original square
            this.allTouchingMinedPairs = new ArrayList<>();
            if (!this.newAllPairsList.isEmpty()) {
                for (int i = 0; i < this.newAllPairsList.size(); i++) {
                    if (pairSurroundsSquare(i)) {
                        this.allTouchingMinedPairs.add(this.newAllPairsList.get(i));
                    }
                }
            }
            // Get all Squares that are included in adjacent pairs to different list pairedSquares
            // list will include only unique squares ie. no duplicates
            // this list will be used in the next step
            ArrayList<Square> pairedSquares = new ArrayList<>();
            if (!this.allTouchingMinedPairs.isEmpty()) {
                for (int i = 0; i < this.allTouchingMinedPairs.size(); i++) {
                    // check that neither square is already included in the list
                    if (DoesNotContainSquare(pairedSquares, this.allTouchingMinedPairs.get(i).first)) {
                        pairedSquares.add(this.allTouchingMinedPairs.get(i).first);
                    }
                    if (DoesNotContainSquare(pairedSquares, this.allTouchingMinedPairs.get(i).second)) {
                        pairedSquares.add(this.allTouchingMinedPairs.get(i).second);
                    }
                }
            }
            // Get all of the untouched squares on their own list that are not included in any adjacent pair
            // These squares can be flagged or opened depending on the situation
            this.untouchedSquaresExclPairs = new ArrayList<>();
            this.minedPairsCount = 0;
            if (!this.surroundingUntouchedSquares.isEmpty() && !pairedSquares.isEmpty()) {
                for (int i = 0; i < this.surroundingUntouchedSquares.size(); i++) {
                    boolean onList = false;
                    for (int k = 0; k < pairedSquares.size(); k++) {
                        if (sameSquare(this.surroundingUntouchedSquares.get(i), pairedSquares.get(k))) {
                            this.minedPairsCount++;
                            onList = true;
                        }
                    }
                    if (!onList) {
                        this.untouchedSquaresExclPairs.add(this.surroundingUntouchedSquares.get(i));
                    }
                }
            }
            
            if (this.untouchedSquaresCount == 0) {
                this.squareIsSolved = true;
            }
        }
    }
    
    /**
     * Check that specifick square is not included in a list
     * @param list list of squares
     * @param square to be searched from the list
     * @return boolean value of the result
     */
    public boolean DoesNotContainSquare(ArrayList<Square> list, Square square) {
        for (int i = 0; i < list.size(); i++) {
            if (sameSquare(list.get(i), square)) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Check that if both squares included in a pair are adjacent to the original square
     * @param i list place of square under inspection
     * @return boolean value of the result
     */
    public boolean pairSurroundsSquare(int i) {
        boolean first = false;
        boolean second = false;
        for (int k = 0; k < this.surroundingSquaresList.size(); k++) {
            if (sameSquare(this.surroundingSquaresList.get(k), this.newAllPairsList.get(i).first)) {
                first = true;
            }
            if (sameSquare(this.surroundingSquaresList.get(k), this.newAllPairsList.get(i).second)) {
                second = true;
            }
        }
        if (first && second) {
            return true;
        }
        return false;
    }
    
    /**
     * Check if two squares are the same
     * @param s first square
     * @param p second square
     * @return boolean value of the result
     */
    public boolean sameSquare(Square s, Square p) {
        if (s.getX() != p.getX()) {
            return false;
        }
        if (s.getY() != p.getY()) {
            return false;
        }
        return true;
    }
    
    /**
     * Make new pair from two squares
     * @param list where squares are picked
     * @param a place on the list of the first square
     * @param b place on the list of the second square
     * @return new pair
     */
    public BotPair makePair(ArrayList<Square> list, int a, int b) {
        Square first = list.get(a);
        Square second = list.get(b);
        BotPair pair = new BotPair(first, second);
        return pair;
    }
    
    /**
     * Check that square is not the original square
     * @param square original square
     * @param x x position of square to be checked
     * @param y y position of square to be checked
     * @return boolean value of the result
     */
    public boolean notOriginalSquare(Square square, int x, int y) {
        if (x == square.getX() && y == square.getY()) {
            return false;
        }
        return true;
    }
    
    public int getUntouchedSquaresCount() {
        return this.untouchedSquaresCount;
    }
    
    public int getFlagCount() {
        return this.numberOfFlags;
    }
    
    public int getOpenedSquaresCount() {
        return this.openedSquaresCount;
    }
    
    public boolean isSquareSolved() {
        return this.squareIsSolved;
    }
    
    public ArrayList<BotPair> getPairsContainingMines() {
        return this.pairsContainingMine;
    }
    
    public int getCountOfMinedPairs() {
        return this.allTouchingMinedPairs.size();
    }
    
    public int getMinesLeft() {
        return this.minesLeft;
    }
    
    public ArrayList<BotPair> getCommonPairs() {
        return this.otherPairsList;
    }
    
    public ArrayList<BotPair> getNewAllPairsList() {
        return this.newAllPairsList;
    }
    
    public ArrayList<Square> getUntouchedSquares() {
        return this.surroundingUntouchedSquares;
    }
    
    public ArrayList<Square> getUntouchedExclPairs() {
        if (this.allTouchingMinedPairs.size() > 1) {
            this.untouchedSquaresExclPairs = new ArrayList<>();
        }
        return this.untouchedSquaresExclPairs;
    }

    /**
     * Get the X coordinate of the Square
     * @return Square's X coordinate
     */
    public int getX() {
        return this.locationX;
    }

    /**
     * Get the Y coordinate of the Square
     * @return Square's Y coordinate
     */
    public int getY() {
        return this.locationY;
    }

}
