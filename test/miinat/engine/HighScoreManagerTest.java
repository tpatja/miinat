package miinat.engine;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Date;
import java.util.List;
import javax.crypto.Cipher;
import javax.crypto.SealedObject;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;


public class HighScoreManagerTest implements IHighScoreManagerAdapter {
    
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
    public void testAllWinningStatsAreSaved() {
        HighScoreManager man = new HighScoreManager(this, false);
        man.gameWinningStats(GameLevel.Beginner, 10);
        man.gameWinningStats(GameLevel.Beginner, 30);
        man.gameWinningStats(GameLevel.Beginner, 15);
        man.gameWinningStats(GameLevel.Beginner, 5);
        man.gameWinningStats(GameLevel.Intermediate, 59);

        assertTrue( man.getEntries(GameLevel.Beginner).size() == 4 );
        
    }
    
    @Test
    public void testGetEntriesSortingIsCorrect() {
        HighScoreManager man = new HighScoreManager(this, false);
        man.gameWinningStats(GameLevel.Beginner, 10);
        man.gameWinningStats(GameLevel.Beginner, 30);
        man.gameWinningStats(GameLevel.Beginner, 15);
        man.gameWinningStats(GameLevel.Beginner, 5);
        man.gameWinningStats(GameLevel.Intermediate, 59);

        List<HighScoreEntry> entries = 
                man.getEntries(GameLevel.Beginner);
        
        assertTrue( entries.get(0).time == 5 );
        assertTrue( entries.get(1).time == 10 );
        assertTrue( entries.get(2).time == 15 );
        assertTrue( entries.get(3).time == 30 );
        
    }
    
    @Test
    public void testOnlyMaxEntriesPerLevelGetSaved() {
        HighScoreManager man = new HighScoreManager(this, false);
        man.gameWinningStats(GameLevel.Beginner, 10);
        man.gameWinningStats(GameLevel.Beginner, 30);
        man.gameWinningStats(GameLevel.Beginner, 15);
        man.gameWinningStats(GameLevel.Beginner, 3245);
        man.gameWinningStats(GameLevel.Beginner, 15);
        man.gameWinningStats(GameLevel.Beginner, 45);
        man.gameWinningStats(GameLevel.Beginner, 55);
        man.gameWinningStats(GameLevel.Beginner, 25);
        man.gameWinningStats(GameLevel.Beginner, 75);
        man.gameWinningStats(GameLevel.Beginner, 85);
        man.gameWinningStats(GameLevel.Beginner, 52);
        man.gameWinningStats(GameLevel.Beginner, 85);
        
        List<HighScoreEntry> entries = man.getEntries(GameLevel.Beginner);
        assertTrue( entries.size() == 10 );
    }
    
    @Test
    public void testBetterTimeDropsExistingWorstTimeWhenMaxEntriesIsReached() {
        HighScoreManager man = new HighScoreManager(this, false);
        man.gameWinningStats(GameLevel.Beginner, 10);
        man.gameWinningStats(GameLevel.Beginner, 30);
        man.gameWinningStats(GameLevel.Beginner, 15);
        man.gameWinningStats(GameLevel.Beginner, 3245);
        man.gameWinningStats(GameLevel.Beginner, 15);
        
        man.gameWinningStats(GameLevel.Beginner, 45);
        man.gameWinningStats(GameLevel.Beginner, 55);
        man.gameWinningStats(GameLevel.Beginner, 25);
        man.gameWinningStats(GameLevel.Beginner, 75);
        man.gameWinningStats(GameLevel.Beginner, 85);
        
        
        man.gameWinningStats(GameLevel.Intermediate, 751);
        man.gameWinningStats(GameLevel.Intermediate, 791);
        man.gameWinningStats(GameLevel.Advanced, 8544);
        
        
        man.gameWinningStats(GameLevel.Beginner, 52);
        
        
        List<HighScoreEntry> entries = man.getEntries(GameLevel.Beginner);
        boolean bigOneFound = false;
        for(HighScoreEntry entry : entries) {
            if(entry.time == 3245)
                bigOneFound = true;
        }
        assertFalse( bigOneFound );
    }
    
    @Test
    public void testCreateHighScoreData() {
        
        return; // this is just a tool to create some test data in miinat.dat
        /*
        HighScoreManager man = new HighScoreManager(this, true);
        man.gameWinningStats(GameLevel.Beginner, 10);
        man.gameWinningStats(GameLevel.Beginner, 30);
        man.gameWinningStats(GameLevel.Beginner, 15);
        man.gameWinningStats(GameLevel.Beginner, 3245);
        man.gameWinningStats(GameLevel.Beginner, 15);
        
        man.gameWinningStats(GameLevel.Beginner, 45);
        man.gameWinningStats(GameLevel.Beginner, 55);
        man.gameWinningStats(GameLevel.Beginner, 25);
        man.gameWinningStats(GameLevel.Beginner, 75);
        man.gameWinningStats(GameLevel.Beginner, 85);
        
        
        man.gameWinningStats(GameLevel.Intermediate, 751);
        man.gameWinningStats(GameLevel.Intermediate, 791);
        man.gameWinningStats(GameLevel.Advanced, 8544);
        
        man.gameWinningStats(GameLevel.Advanced, 8544);
        man.gameWinningStats(GameLevel.Advanced, 98343);
        
        man.gameWinningStats(GameLevel.Beginner, 52);
        */
        
    }
    
    @Test
    public void testEncryptedSaveRestore() {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();

           byte key[] = "A2k0nbaU7MruljxW".getBytes();
           DESKeySpec desKeySpec = new DESKeySpec(key);
           SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
           SecretKey skey = keyFactory.generateSecret(desKeySpec);

           Cipher ecipher = Cipher.getInstance("DES");
           ecipher.init(Cipher.ENCRYPT_MODE, skey);
           
           HighScoreEntry entry = new HighScoreEntry();
           entry.date = new Date();
           entry.level = GameLevel.Beginner;
           entry.time = 99;
           entry.name = "teemu";
                   
           SealedObject so = new SealedObject(entry, ecipher);

           ObjectOutputStream o = new ObjectOutputStream(out);
           o.writeObject(so);
           byte[] data = out.toByteArray();
           o.close();
           out.close();
           
           {
               ByteArrayInputStream bis = new ByteArrayInputStream(data);
               ObjectInputStream ois = new ObjectInputStream(bis);
               Object object = ois.readObject();
               SealedObject sealedObject = (SealedObject) object;
               String algorithmName = sealedObject.getAlgorithm();
               System.out.println("detected encryption algorithm: " 
                       + algorithmName);
               Cipher cipher = Cipher.getInstance(algorithmName);
               cipher.init(Cipher.DECRYPT_MODE, skey);

               HighScoreEntry loadedEntry = (HighScoreEntry) sealedObject.getObject(cipher);
               
               assertTrue( loadedEntry != null);
               assertEquals( loadedEntry, entry );
               
               ois.close();
               bis.close();
   
           }
           
        } 
        catch ( Exception ex) {
            System.err.println("fail:" + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    @Override
    public String getNameForHighScore() {
        return "teemu";
    }
    
    @Override
    public void highScoresChanged(){}
}
