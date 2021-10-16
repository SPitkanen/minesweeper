
package minesweeper.bot;

import minesweeper.generator.MinefieldGenerator;
import minesweeper.model.Board;
import minesweeper.model.Move;
import minesweeper.model.MoveType;
import minesweeper.model.Square;
import java.util.HashSet;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * THIS TEST CLASS IS A NEW ADDITION TO THE TEMPLATE BY:
 * @author santeripitkanen
 */
public class ProjectBotTest {
    
    private MinefieldGenerator generator;
    private Board board;
    private Bot bot;
    private ProjectBot project;
    
    @Before
    public void setUp() {
        this.project = new ProjectBot();
        this.bot = BotSelect.getBot();
        this.generator = new MinefieldGenerator();
        this.board = new Board(generator, 5, 5, 0);
        
        this.board.setTotalMines(6);
        
        board.board[0][0].setMine();
        board.addMineSquareToList(board.board[0][0]);
        board.incrementAdjacentSquares(0, 0);
        
        board.board[1][0].setMine();
        board.addMineSquareToList(board.board[1][0]);
        board.incrementAdjacentSquares(1, 0);
        
        board.board[0][1].setMine();
        board.addMineSquareToList(board.board[0][1]);
        board.incrementAdjacentSquares(0, 1);
        
        board.board[3][2].setMine();
        board.addMineSquareToList(board.board[3][2]);
        board.incrementAdjacentSquares(3, 2);
        
        board.board[4][2].setMine();
        board.addMineSquareToList(board.board[4][2]);
        board.incrementAdjacentSquares(4, 2);
        
        board.board[0][4].setMine();
        board.addMineSquareToList(board.board[0][4]);
        board.incrementAdjacentSquares(0, 4);
        
        this.board.firstMove = false;
        
        this.board.makeMove(new Move(MoveType.OPEN, 4, 4));
        
        
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @After
    public void tearDown() {
    }
    
    @Test
    public void firstMoveIsFlag() {
        Move move = this.bot.makeMove(board);
        assertEquals(MoveType.FLAG, move.type);
    }
    
    @Test
    public void afnReturnsTrue() {
        this.board.makeMove(new Move(MoveType.FLAG, 0, 4));
        Square square = this.board.getSquareAt(1, 4);
        boolean value = this.project.AFN(square, board);
        assertEquals(true, value);
    }
    
    @Test
    public void afnReturnsFalse() {
        this.project.pairs = new HashSet<>();
        Square square = this.board.getSquareAt(1, 4);
        boolean value = this.project.AFN(square, board);
        assertEquals(false, value);
    }
    
    @Test
    public void amnReturnsTrue() {
        this.board.makeMove(new Move(MoveType.OPEN, 0, 3));
        Square square = this.board.getSquareAt(1, 4);
        boolean value = this.project.AMN(square, board);
        assertEquals(true, value);
    }
    
    @Test
    public void amnReturnsFalse() {
        Square square = this.board.getSquareAt(1, 4);
        boolean value = this.project.AMN(square, board);
        assertEquals(false, value);
    }
    
    @Test 
    public void randomSquareIsWithinBoard() {
        Square rndSquare = this.project.randomSquare(board);
        boolean value = false;
        if (rndSquare.getX() > -1 && rndSquare.getX() < 5 && rndSquare.getY() > -1 && rndSquare.getY() < 5) {
            value = true;
        }
        assertEquals(true, value);
    }
    
    @Test 
    public void randomSquareIsUntouched() {
        Square rndSquare = this.project.randomSquare(board);
        boolean value = false;
        if (!rndSquare.isFlagged() && !rndSquare.isOpened()) {
            value = true;
        }
        assertEquals(true, value);
    }
    
    @Test
    public void notOriginalSquareRejectsOriginal() {
        Square square = this.board.getSquareAt(1, 0);
        boolean value = this.project.notOriginalSquare(square, 1, 0);
        assertEquals(false, value);
    }
    
    @Test
    public void notOriginalSquareAcceptsNotOriginal() {
        Square square = this.board.getSquareAt(1, 0);
        boolean value = this.project.notOriginalSquare(square, 0, 0);
        assertEquals(true, value);
    }
    
    @Test
    public void randomMoveReturnsMove() {
        Move move = this.project.randomMove(board);
        assertEquals(MoveType.OPEN, move.type);
    }
    
    @Test
    public void placeFlagReturnsFlagMove() {
        Square square = board.getSquareAt(1, 0);
        Move move = this.project.placeFlag(square);
        assertEquals(MoveType.FLAG, move.type);
    }
    
    @Test
    public void openSquareReturnsOpenMove() {
        Square square = board.getSquareAt(0, 3);
        Move move = this.project.openSquare(square);
        assertEquals(MoveType.OPEN, move.type);
    }
    
    @Test
    public void checkSurroundingSquaresIncreasesFlagCount() {
        Move move = new Move(MoveType.FLAG, 0, 4);
        board.makeMove(move);
        this.project.zeroValues();
        Square square = board.getSquareAt(1, 4);
        this.project.checkSurroundingSquares(square, board);
        assertEquals(1, this.project.flags);
    }
    
    @Test
    public void chechSurroundingPairsTrue() {
        this.project.pairs = new HashSet<>();
        Square square = board.getSquareAt(1, 4);
        project.AFN(square, board);
        Square square2 = board.getSquareAt(1, 3);
        project.checkSurroundingSquares(square2, board);
        boolean returnValue = project.checkSurroundingPairs(square2, board);
        assertEquals(true, returnValue);
    }
    
    @Test
    public void chechSurroundingPairsFalse() {
        this.project.pairs = new HashSet<>();
        Move move = new Move(MoveType.FLAG, 0, 4);
        board.makeMove(move);
        Square square = board.getSquareAt(1, 4);
        project.AFN(square, board);
        Square square2 = board.getSquareAt(1, 3);
        project.checkSurroundingSquares(square2, board);
        boolean returnValue = project.checkSurroundingPairs(square2, board);
        assertEquals(false, returnValue);
    }
    
    @Test
    public void gameWon() {
        while (!board.gameWon) {
            Move move = bot.makeMove(board);
            board.makeMove(move);
        }
        
        assertEquals(true, board.gameWon);
    }
    
    @Test
    public void AFNWithPairsReturnsTrue() {
        this.project.pairs = new HashSet<>();
        Square square = board.getSquareAt(1, 4);
        this.project.AFN(square, board);
        Square square2 = board.getSquareAt(1, 3);
        boolean returnValue = this.project.AFNwithPairs(square2, board);
        assertEquals(true, returnValue);
    }
    
    @Test
    public void AFNWithPairsReturnsFalse() {
        this.project.pairs = new HashSet<>();
        Square square = board.getSquareAt(1, 4);
        this.project.AFN(square, board);
        Move move = new Move(MoveType.FLAG, 0, 4);
        board.makeMove(move);
        Square square2 = board.getSquareAt(1, 3);
        boolean returnValue = this.project.AFNwithPairs(square2, board);
        assertEquals(false, returnValue);
    }
    
    @Test
    public void AMNWithPairsReturnsTrue() {
        this.project.pairs = new HashSet<>();
        Move move = new Move(MoveType.OPEN, 1, 2);
        board.makeMove(move);
        Square square = board.getSquareAt(2, 3);
        this.project.AFN(square, board);
        Square square2 = board.getSquareAt(3, 3);
        boolean returnValue = this.project.AMNwithPairs(square2, board);
        assertEquals(true, returnValue);
    }
    
    @Test
    public void AMNWithPairsReturnsFalse() {
        this.project.pairs = new HashSet<>();
        Square square = board.getSquareAt(1, 4);
        this.project.AFN(square, board);
        Square square2 = board.getSquareAt(1, 3);
        boolean returnValue = this.project.AMNwithPairs(square2, board);
        assertEquals(false, returnValue);
    }
}
