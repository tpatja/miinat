/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package miinat.engine;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Teemu Patja <tp@iki.fi>
 */
public class HighScoreManagerTest implements HighScoreNameProvider {
    
    public HighScoreManagerTest() {
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
    public void testCreateEntry() {
        HighScoreEntry entry = new HighScoreEntry();
        entry.date = new Date();
        entry.level = MiinaEngine.Level.Beginner;
        entry.time = 99;
        entry.name = "teemu";
        System.out.println(entry);
    }

    @Test
    public void testAllWinningStatsAreSaved() {
        HighScoreManager man = new HighScoreManager(this, false);
        man.gameWinningStats(MiinaEngine.Level.Beginner, 10);
        man.gameWinningStats(MiinaEngine.Level.Beginner, 30);
        man.gameWinningStats(MiinaEngine.Level.Beginner, 15);
        man.gameWinningStats(MiinaEngine.Level.Beginner, 5);
        man.gameWinningStats(MiinaEngine.Level.Intermediate, 59);

        assertTrue( man.getEntries(MiinaEngine.Level.Beginner).size() == 4 );
        
    }
    
    @Test
    public void testGetEntriesSortingIsCorrect() {
        HighScoreManager man = new HighScoreManager(this, false);
        man.gameWinningStats(MiinaEngine.Level.Beginner, 10);
        man.gameWinningStats(MiinaEngine.Level.Beginner, 30);
        man.gameWinningStats(MiinaEngine.Level.Beginner, 15);
        man.gameWinningStats(MiinaEngine.Level.Beginner, 5);
        man.gameWinningStats(MiinaEngine.Level.Intermediate, 59);

        List<HighScoreEntry> entries = 
                man.getEntries(MiinaEngine.Level.Beginner);
        
        assertTrue( entries.get(0).time == 5 );
        assertTrue( entries.get(1).time == 10 );
        assertTrue( entries.get(2).time == 15 );
        assertTrue( entries.get(3).time == 30 );
        
    }
    
    @Test
    public void testOnlyMaxEntriesPerLevelGetSaved() {
        HighScoreManager man = new HighScoreManager(this, false);
        man.gameWinningStats(MiinaEngine.Level.Beginner, 10);
        man.gameWinningStats(MiinaEngine.Level.Beginner, 30);
        man.gameWinningStats(MiinaEngine.Level.Beginner, 15);
        man.gameWinningStats(MiinaEngine.Level.Beginner, 3245);
        man.gameWinningStats(MiinaEngine.Level.Beginner, 15);
        man.gameWinningStats(MiinaEngine.Level.Beginner, 45);
        man.gameWinningStats(MiinaEngine.Level.Beginner, 55);
        man.gameWinningStats(MiinaEngine.Level.Beginner, 25);
        man.gameWinningStats(MiinaEngine.Level.Beginner, 75);
        man.gameWinningStats(MiinaEngine.Level.Beginner, 85);
        man.gameWinningStats(MiinaEngine.Level.Beginner, 52);
        man.gameWinningStats(MiinaEngine.Level.Beginner, 85);
        
        List<HighScoreEntry> entries = man.getEntries(MiinaEngine.Level.Beginner);
        assertTrue( entries.size() == 10 );
    }
    
    @Test
    public void testBetterTimeDropsExistingWorstTimeWhenMaxEntriesIsReached() {
        HighScoreManager man = new HighScoreManager(this, false);
        man.gameWinningStats(MiinaEngine.Level.Beginner, 10);
        man.gameWinningStats(MiinaEngine.Level.Beginner, 30);
        man.gameWinningStats(MiinaEngine.Level.Beginner, 15);
        man.gameWinningStats(MiinaEngine.Level.Beginner, 3245);
        man.gameWinningStats(MiinaEngine.Level.Beginner, 15);
        
        man.gameWinningStats(MiinaEngine.Level.Beginner, 45);
        man.gameWinningStats(MiinaEngine.Level.Beginner, 55);
        man.gameWinningStats(MiinaEngine.Level.Beginner, 25);
        man.gameWinningStats(MiinaEngine.Level.Beginner, 75);
        man.gameWinningStats(MiinaEngine.Level.Beginner, 85);
        
        man.gameWinningStats(MiinaEngine.Level.Beginner, 52);
        
        
        List<HighScoreEntry> entries = man.getEntries(MiinaEngine.Level.Beginner);
        boolean bigOneFound = false;
        for(HighScoreEntry entry : entries) {
            if(entry.time == 3245)
                bigOneFound = true;
        }
        assertFalse( bigOneFound );
    }
    
    
    
    
    @Test
    public void testRot13(){
        String s = "ASDF,foo,123";
        //System.out.println(s);
        String s2 = HighScoreEntry.rot13(s);
        //System.out.println(s2);
        s2 = HighScoreEntry.rot13(s2);
        //System.out.println(s2);
        assertEquals(s, s2);
    }

    @Override
    public String getNameForHighScore() {
        return "teemu";
    }
    
}
