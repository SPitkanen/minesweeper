
package minesweeper.bot;

import minesweeper.generator.MinefieldGenerator;
import minesweeper.model.Board;
import minesweeper.model.Move;
import minesweeper.model.MoveType;
import minesweeper.model.Square;
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
        
        board.board[1][0].setMine();
        board.addMineSquareToList(board.board[1][0]);
        board.incrementAdjacentSquares(1, 0);
        
        board.board[1][4].setMine();
        board.addMineSquareToList(board.board[1][4]);
        board.incrementAdjacentSquares(1, 4);
        
        board.board[3][4].setMine();
        board.addMineSquareToList(board.board[3][4]);
        board.incrementAdjacentSquares(3, 4);
        
        board.board[4][4].setMine();
        board.addMineSquareToList(board.board[4][4]);
        board.incrementAdjacentSquares(4, 4);
        
        this.board.makeMove(new Move(MoveType.OPEN, 3, 1));
        
        
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
        this.board.makeMove(new Move(MoveType.FLAG, 1, 0));
        Square square = this.board.getSquareAt(1, 1);
        boolean value = this.project.AFN(square, board);
        assertEquals(true, value);
    }
    
    @Test
    public void afnReturnsFalse() {
        Square square = this.board.getSquareAt(1, 3);
        boolean value = this.project.AFN(square, board);
        assertEquals(false, value);
    }
    
    @Test
    public void amnReturnsTrue() {
        Square square = this.board.getSquareAt(4, 3);
        boolean value = this.project.AMN(square, board);
        assertEquals(true, value);
    }
    
    @Test
    public void amnReturnsFalse() {
        Square square = this.board.getSquareAt(0, 3);
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
}
