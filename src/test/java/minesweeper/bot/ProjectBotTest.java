
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
    
    private Bot bot;
    private MinefieldGenerator generator;
    private Board board;
    
    @Before
    public void setUp() {
        this.bot = BotSelect.getBot();
        this.generator = new MinefieldGenerator();
        this.board = new Board(generator, 5, 5, 0);
        
        Square s1 = new Square(0, 0);
        this.board.addSquare(s1, 0, 0);
        Square s2 = new Square(0, 1);
        s2.setMine();
        this.board.addSquare(s2, 0, 1);
        Square s3 = new Square(0, 2);
        s3.setMine();
        this.board.addSquare(s3, 0, 2);
        this.board.makeMove(new Move(MoveType.OPEN, 2, 2));
        
        
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void firstMoveIsFlag() {
        Move botMove = this.bot.makeMove(board);
        Move move = new Move(MoveType.FLAG, 0, 1);
        assertEquals(move, botMove);
    }
    
    @Test
    public void secondMoveOpensSquare() {
        Move move1 = this.bot.makeMove(board);
        this.board.makeMove(move1);
        Move botMove = this.bot.makeMove(board);
        Move move = new Move(MoveType.OPEN, 0, 0);
        assertEquals(move, botMove);
    }
}
