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
public class MiinaEngineTest implements MiinaEngine.MiinaEngineListener {

    private MiinaEngine engine;
    private final int WIDTH  = 10;
    private final int HEIGHT = 10;
    private final int MINES  = 15;
    private boolean gameOverCalled;
    private boolean gameStartedCalled;

    
    public MiinaEngineTest() {
        this.gameOverCalled = false;
        this.gameStartedCalled = false;
    }
    
    @Override
    public void gameOver() {
        System.out.println("game over");
        this.gameOverCalled = true;
    }
    
    @Override
    public void gameStarted() {
        this.gameStartedCalled = true;
    }


    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        engine = new MiinaEngine(this.WIDTH,this.HEIGHT,this.MINES, this);
        
        this.gameOverCalled = false;
        this.gameStartedCalled = false;
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void givenEngineInitedGameIsNotOver() {
        engine.init();
        assertFalse(engine.isGameOver());
    }

    @Test
    public void givenEngineInitedAmountOfMinesIsCorrect() {
        engine.init();

        int mines=0;
        for(int y=0; y<engine.getHeight(); ++y) {
            for(int x=0; x<engine.getWidth(); ++x) {
                if(engine.squareAt(x, y).hasMine)
                    ++mines;
            }
        }
        
        assertEquals(mines, this.MINES);
    }
    
    @Test
    public void givenEngineInitedNoMinesAreFlagged() {
        engine.init();

        int flagged=0;
        for(int y=0; y<engine.getHeight(); ++y) {
            for(int x=0; x<engine.getWidth(); ++x) {
                if(engine.squareAt(x, y).isFlagged)
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
        assertFalse(s.hasMine);
        assertFalse(s.isFlagged);
    }

    @Test
    public void givenEngineInitedAndMineUncoveredGameOverCalled() {
        engine.init();

        boolean callDone = false;
        for(int y=0; y<engine.getHeight(); ++y) {
            for(int x=0; x<engine.getWidth(); ++x) {
                if(engine.squareAt(x, y).hasMine) {
                    engine.uncoverSquare(x, y);
                    callDone = true;
                    break;
                }
            }
            if(callDone) break;
        }
        assertTrue(callDone);
        assertTrue(this.gameOverCalled);
        assertTrue(engine.isGameOver());
    }

    @Test
    public void givenEngineInitedAndNonMineUncoveredGameOverNotCalled() {
        engine.init();

        boolean callDone = false;
        for(int y=0; y<engine.getHeight(); ++y) {
            for(int x=0; x<engine.getWidth(); ++x) {
                if(!engine.squareAt(x, y).hasMine) {
                    engine.uncoverSquare(x, y);
                    callDone = true;
                    break;
                }
            }
            if(callDone) break;
        }
        assertTrue(callDone);
        assertFalse(this.gameOverCalled);
        assertFalse(engine.isGameOver());
    }

    @Test
    public void testEngineInitWithPredefinedMines() {
        
        StringBuilder sb = new StringBuilder(this.WIDTH*this.HEIGHT);
        sb.append("*---------")
          .append("-*--------")
          .append("-*----*---")
          .append("----*----*")
          .append("-------**-")
          .append("-*--------")
          .append("-*-----*--")
          .append("-*----*---")
          .append("-*--------")
          .append("-*--------");
        engine.init(sb.toString());
        
        assertTrue(this.gameStartedCalled);
        assertTrue( engine.squareAt(1, 1).hasMine );
        assertTrue( engine.squareAt(0, 0).hasMine );
        assertFalse( engine.squareAt(9, 9).hasMine );
        assertTrue( engine.squareAt(8, 4).hasMine );
    }
    
    @Test
    public void givenEngineInitWithPredefinedMinesSurroundingMineCountOK() {
        
        StringBuilder sb = new StringBuilder(this.WIDTH*this.HEIGHT);
        sb.append("*---------")
          .append("-*--------")
          .append("-*----*---")
          .append("----*----*")
          .append("-------**-")
          .append("-*--------")
          .append("-*-----*--")
          .append("-*----*---")
          .append("-*--------")
          .append("-*--------");
        engine.init(sb.toString());
        assertEquals( engine.countSurroundingMines(1, 0), 2);
        assertEquals( engine.countSurroundingMines(0, 1), 3);
        assertEquals( engine.countSurroundingMines(8, 0), 0);
    }
    
    
}
