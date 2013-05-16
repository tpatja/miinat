package miinat;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author tpatja
 */
public class MiinaEngineTest {

    private MiinaEngine engine;
    private final int WIDTH  = 10;
    private final int HEIGHT = 10;
    private final int MINES  = 15;

    
    public MiinaEngineTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        engine = new MiinaEngine(this.WIDTH,this.HEIGHT,this.MINES);
        engine.init();
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void givenEngineInitedGameIsNotOver() {
        assertFalse(engine.isGameOver());
    }

    @Test
    public void givenEngineInitedAmountOfMinesIsCorrect() {
        int mines=0;
        for(int y=0; y<engine.getHeight(); ++y) {
            for(int x=0; x<engine.getWidth(); ++x) {
                if(engine.squareAt(x, y).has_mine)
                    ++mines;
            }
        }
        
        assertEquals(mines, this.MINES);
    }
    
    @Test
    public void givenEngineInitedNoMinesAreFlagged() {
        int flagged=0;
        for(int y=0; y<engine.getHeight(); ++y) {
            for(int x=0; x<engine.getWidth(); ++x) {
                if(engine.squareAt(x, y).flagged)
                    ++flagged;
            }
        }
        
        assertEquals(flagged, 0);
    }
   
    @Test
    public void testSquare() {
        MiinaEngine.Square s = engine.new Square(1,1);
        assertEquals(s.x, 1);
        assertEquals(s.y, 1);
        assertFalse(s.has_mine);
        assertFalse(s.flagged);
        
    }
    
    
}
