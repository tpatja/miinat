package miinat.engine;

import java.util.Date;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class HighScoreEntryTest {
    
    public HighScoreEntryTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void testCreate() {
        HighScoreEntry entry = new HighScoreEntry(
                    GameLevel.Beginner,
                     99, new Date(), "teemu");
        assertTrue( entry != null);
        assertTrue( entry.name.equals("teemu") );

    }
    
    @Test
    public void testEquals() {
        Date d = new Date();
        
        HighScoreEntry entry = new HighScoreEntry();
        entry.level = GameLevel.Beginner;
        entry.time = 99;
        entry.date = d;
        entry.name = "teemu";
        
        HighScoreEntry entry2 = new HighScoreEntry(
                            GameLevel.Beginner,
                             99, d, "teemu");
                
        assertEquals(entry, entry2);
        assertTrue(entry.equals(entry2));
        assertTrue(entry2.equals(entry));
    }

}