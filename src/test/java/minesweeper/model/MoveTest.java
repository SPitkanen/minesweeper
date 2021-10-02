
package minesweeper.model;

import org.junit.Test;
import org.junit.After;
import org.junit.Before;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MoveTest {
    
    private Move move;

    @Before
    public void setUp() {
        // intentionally empty
    } 

    @After
    public void tearDown() {
        // intentionally empty
    }

    @Test
    public void canCreateAnOpenMove() {
        move = new Move(MoveType.OPEN, 5, 5);

        assertEquals(MoveType.OPEN, move.type);
    }

    @Test
    public void canCreateAFlagMove() {
        move = new Move(MoveType.FLAG, 5, 5);

        assertEquals(MoveType.FLAG, move.type);
    }

    @Test
    public void canCreateAChordMove() {
        move = new Move(MoveType.CHORD, 5, 5);

        assertEquals(MoveType.CHORD, move.type);
    }

    @Test
    public void canCreateAHighlightMove() {
        move = new Move(5, 5, Highlight.GREEN);

        assertEquals(MoveType.HIGHLIGHT, move.type);
    }

    @Test
    public void moveTimestampsAreNotEqual() {
        move = new Move(MoveType.OPEN, 5, 5);

        try {
            Thread.sleep(100);
        } catch(InterruptedException e) {

        }

        Move move2 = new Move(MoveType.OPEN, 5, 5);

        assertTrue(move.timestamp != move2.timestamp);
    }
    
    @Test
    public void toStringWorksToo() {
        move = new Move(MoveType.OPEN, 5, 5);
        
        assertEquals("Move: OPEN Distance: 0,0000 Time: 0,0000", move.toString());
    }

}
