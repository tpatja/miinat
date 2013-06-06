package miinat.engine;


import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Class for testing game logic in MiinaEngine
 *
 */
public class MiinaEngineTest implements IEngineObserver {

    private MiinaEngine engine;
    
    private final MiinaEngine.Level LEVEL = MiinaEngine.Level.Beginner; 
    
    // these correspond to beginner level
    private final int WIDTH  = 9;
    private final int HEIGHT = 9;
    private final int MINES  = 10;

    private boolean gameOverCalled;
    private boolean gameStartedCalled;
    private boolean gameOverParamValue;
    private boolean gameWinningStatsCalled;
    private int gameWinningStatsSecondsParam;
    private MiinaEngine.Level gameWinningStatsLevelParamValue;

    
    public MiinaEngineTest() {
        this.gameOverCalled = false;
        this.gameStartedCalled = false;
        this.gameOverParamValue = false;
    }
    
    @Override
    public void gameOver(boolean won) {
        this.gameOverCalled = true;
        this.gameOverParamValue = won;
    }
    
    @Override
    public void gameStarted() {
        this.gameStartedCalled = true;
    }
    
    @Override
    public void gameWinningStats(MiinaEngine.Level level, int seconds) {
        System.out.println("gameWinningStats: level=" + level + ",seconds=" + seconds);
        this.gameWinningStatsCalled = true;
        this.gameWinningStatsSecondsParam = seconds;
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        engine = new MiinaEngine(this);
        
        this.gameOverCalled = false;
        this.gameStartedCalled = false;
        this.gameOverParamValue = false;
        this.gameWinningStatsCalled = false;
        this.gameWinningStatsSecondsParam = -1;
        this.gameWinningStatsLevelParamValue = MiinaEngine.Level.Beginner;
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void givenEngineInitedGameIsNotOver() {
        engine.startGame(this.WIDTH,this.HEIGHT,this.MINES);
        assertFalse(engine.isGameOver());
    }

    @Test
    public void givenEngineInitedAmountOfMinesIsCorrect() {
        engine.startGame(this.WIDTH,this.HEIGHT,this.MINES);

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
        engine.startGame(this.LEVEL);

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
    public void givenEngineInitedAndMineUncoveredGameOverCalled() {
        engine.startGame(this.LEVEL);

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
        assertTrue(this.gameOverParamValue == false);
        assertTrue(engine.isGameOver());
    }

    @Test
    public void givenEngineInitedAndNonMineUncoveredGameOverNotCalled() {
        engine.startGame(this.LEVEL);

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
    public void testEngineInitedWithPredefinedMines() {
        
        StringBuilder sb = new StringBuilder(this.WIDTH*this.HEIGHT);
        sb.append("*--------")
          .append("-*-------")
          .append("-*----*--")
          .append("----*----")
          .append("-------**")
          .append("---------")
          .append("-------*-")
          .append("------*--")
          .append("-*-------");
          
        engine.startGame(this.LEVEL, sb.toString());
        
        assertTrue(this.gameStartedCalled);
        assertTrue( engine.squareAt(0, 0).hasMine );
        assertTrue( engine.squareAt(1, 1).hasMine );
        assertFalse(engine.squareAt(8, 8).hasMine );
        assertTrue( engine.squareAt(8, 4).hasMine );
        assertTrue( sb.toString().equals(getMineRepresentation()));
    }
    
    @Test
    public void givenEngineInitedWithPredefinedMinesSurroundingMineCountOK() {
        
        StringBuilder sb = new StringBuilder(this.WIDTH*this.HEIGHT);
        sb.append("*--------")
          .append("-*-------")
          .append("-*----*--")
          .append("----*----")
          .append("-------**")
          .append("-*-------")
          .append("-------*-")
          .append("------*--")
          .append("---------");
        engine.startGame(this.LEVEL, sb.toString());
        assertEquals( engine.countSurroundingMines(1, 0), 2);
        assertEquals( engine.countSurroundingMines(0, 1), 3);
        assertEquals( engine.countSurroundingMines(8, 0), 0);
    }

    
    @Test
    public void givenEngineInitedWithPredefinedMinesUncoveringOK() {
        
        StringBuilder sb = new StringBuilder(this.WIDTH*this.HEIGHT);
        sb.append("*--------")
          .append("-*-------")
          .append("-----*---")
          .append("---*----*")
          .append("------**-")
          .append("---------")
          .append("------*--")
          .append("-----*---")
          .append("-*-------");
        engine.startGame(this.WIDTH,this.HEIGHT,this.MINES, sb.toString());
        //showMines();
        engine.uncoverSquare(2,0);        
        assertFalse( engine.squareAt(2,0).isCovered() );
    
        sb.setLength(0);
        sb.append( "##1######")
           .append("#########")
           .append("#########")
           .append("#########")
           .append("#########")
           .append("#########")
           .append("#########")
           .append("#########")
           .append("#########");
 
        assertTrue( engine.getGridRepresentation().equals(sb.toString()) );
  
        engine.uncoverSquare(4,0);
        
        sb.setLength(0);
        sb.append(  "##1______")
            .append("##1_111__")
            .append("##212#111")
            .append("#########")
            .append("#########")
            .append("#########")
            .append("#########")
            .append("#########")
            .append("#########");

        
        assertTrue( engine.getGridRepresentation().equals(sb.toString()) );
              
    }


    @Test
    public void givenEngineInitedWithPredefinedMinesFlaggingAllMinesWinsGame() {
        StringBuilder sb = new StringBuilder(this.WIDTH*this.HEIGHT);
        sb.append("***------")
          .append("****-----")
          .append("***------")
          .append("---------")
          .append("---------")
          .append("---------")
          .append("---------")
          .append("---------")
          .append("---------");
        engine.startGame(this.WIDTH,this.HEIGHT,this.MINES, sb.toString());
        
        engine.flagSquare(0,0, true);
        engine.flagSquare(1,0, true);
        engine.flagSquare(2,0, true);
        engine.flagSquare(0,1, true);
        engine.flagSquare(1,1, true);
        engine.flagSquare(2,1, true);
        engine.flagSquare(3,1, true);
        engine.flagSquare(0,2, true);
        engine.flagSquare(1,2, true);
        engine.flagSquare(2,2, true);
        
        assertTrue(this.gameOverCalled && this.gameOverParamValue == true);
                
    }

    @Test
    public void givenEngineInitedWithPredefinedMinesFlaggingNonMinesDoesNotEndGame() {
        StringBuilder sb = new StringBuilder(this.WIDTH*this.HEIGHT);
        sb.append("*--------")
          .append("-*-------")
          .append("-*-------")
          .append("----*---*")
          .append("------*--")
          .append("-*-------")
          .append("---------")
          .append("-*---*---")
          .append("-*-------");
        engine.startGame(this.LEVEL, sb.toString());
        
        engine.flagSquare(0,0, true);
        engine.flagSquare(1,1, true);
        engine.flagSquare(1,2, true);
        engine.flagSquare(6,2, true);
        engine.flagSquare(4,3, true);
        engine.flagSquare(7,3, true);
        engine.flagSquare(7,4, true);
        engine.flagSquare(8,4, true);
        engine.flagSquare(1,5, true);
        engine.flagSquare(1,6, true);
        engine.flagSquare(7,6, true);
        engine.flagSquare(1,7, true);
        engine.flagSquare(7,7, true);
        engine.flagSquare(1,8, true);
        assertTrue(!this.gameOverCalled);
        
        engine.flagSquare(5,5, true);
        assertTrue(!this.gameOverCalled);
    }
    
    @Test
    public void givenEngineInitedWithPredefinedMinesFlaggingAllMinesWinningStatsCalled() {
        StringBuilder sb = new StringBuilder(this.WIDTH*this.HEIGHT);
        sb.append("**-------")
          .append("**-------")
          .append("**-------")
          .append("**-------")
          .append("**-------")
          .append("---------")
          .append("---------")
          .append("---------")
          .append("---------");
        
        engine.startGame(this.LEVEL, sb.toString());
        
        engine.flagSquare(0,0, true);
        engine.flagSquare(0,1, true);
        engine.flagSquare(0,2, true);
        engine.flagSquare(0,3, true);
        engine.flagSquare(0,4, true);
        engine.flagSquare(1,0, true);
        engine.flagSquare(1,1, true);
        engine.flagSquare(1,2, true);
        assertTrue(!this.gameOverCalled); 
        engine.flagSquare(1,3, true);
        engine.flagSquare(1,4, true);
        
        assertTrue(this.gameOverCalled);
        assertTrue(this.gameWinningStatsCalled);
        assertTrue(this.gameWinningStatsSecondsParam != -1);
  
    }
    
    
    private String getMineRepresentation() {
        StringBuilder sb = new StringBuilder(this.WIDTH*this.HEIGHT);
        for(int y=0;y<engine.getHeight(); ++y) {
            for(int x=0; x<engine.getWidth(); ++x) {
                Square s = engine.squareAt(x, y);
                sb.append(s.hasMine ? "*" : "-");
            }
        }
        return sb.toString();
    }
    
    private void showMines() {
        String repr = getMineRepresentation();
        for(int y=0;y<engine.getHeight(); ++y) {
            for(int x=0; x<engine.getWidth(); ++x) {
                System.out.print(repr.charAt(y*engine.getWidth() +x));
            }
            System.out.println();
        }
        System.out.println();
    }
        
    private void showGrid() {
        String repr = engine.getGridRepresentation();
        for(int y=0;y<engine.getHeight(); ++y) {
            for(int x=0; x<engine.getWidth(); ++x) {
                System.out.print(repr.charAt(y*engine.getWidth() +x));
            }
            System.out.println();
        }
        System.out.println();
    }    
}
