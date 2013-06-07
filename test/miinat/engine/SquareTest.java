package miinat.engine;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class SquareTest {

    @Test
    public void givenSquareCreatedStateOK() {
        Square s = new Square(1,1);
        assertEquals(s.x, 1);
        assertEquals(s.y, 1);
        assertFalse(s.hasMine);
        assertFalse(s.isFlagged);
        assertTrue(s.isCovered());
    }

    @Test
    public void givenSquareCreatedAndSurroundingMinesSetSquareIsNotCovered() {
        Square s = new Square(1,1);
        s.surroundingMines = 2; // setting surrounding mines implies uncovering
        assertFalse(s.isCovered());
    }
    
    @Test
    public void givenSquareCreatedAndResetStateOK() {
        Square s = new Square(1,1);
        s.isFlagged = true;
        s.surroundingMines = 3;
        assertFalse(s.isCovered());
        s.reset();
        assertTrue(s.x == 1);
        assertTrue(s.y == 1);
        assertTrue(s.isCovered());
        assertFalse(s.isFlagged);
    }
    
}
