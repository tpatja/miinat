package miinat.engine;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import javax.crypto.Cipher;
import javax.crypto.SealedObject;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;



/**
 * Class responsible for storing and retrieving high scores.
 * 
 * Saved entries are encrypted using using DES to make it more 
 * difficult for clever users to fake high scores :)
 *
 */
public class HighScoreManager implements IEngineObserver {

    private final String FILENAME="miinat.dat";
    private final int MAX_ENTRIES_PER_LEVEL=10;
    private final String ENCRYPTION_KEY="A2k0nbaU7MruljxW";
    private SecretKey secretKey;
    
    private ArrayList<HighScoreEntry> entries;
    private IHighScoreNameProvider nameProvider;
    private boolean autoPersist;
    
    /**
     * Constructor
     * 
     * @param nameProvider interface for getting name when gameWinningStats
     *   gets called
     * @param autoPersist if true, entries are automatically loaded from disk 
     *  in constructor and saved to disk when created
     */
    HighScoreManager(IHighScoreNameProvider nameProvider, boolean autoPersist) {
        this.nameProvider = nameProvider;
        
        try {
            DESKeySpec desKeySpec = new DESKeySpec(this.ENCRYPTION_KEY.getBytes());
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            this.secretKey = keyFactory.generateSecret(desKeySpec);
        }
        catch( InvalidKeyException | NoSuchAlgorithmException | InvalidKeySpecException e){
            System.err.println("Failed to initialize crypto key, "
                    + "entries will be persisted without encryption");
            this.secretKey = null;
        }
        this.entries = new ArrayList<>();
        this.autoPersist = autoPersist;
        if(this.autoPersist)
            this.loadEntries();
    }
    
    /**
     * load entries from disk
     */
    private void loadEntries() {
        
        try {
            FileInputStream in = new FileInputStream(this.FILENAME);
            ObjectInputStream is = new ObjectInputStream(in);
            for(;;) {
                
                HighScoreEntry entry = loadSingleEntry(is);
                if(entry == null)
                    break;
                entries.add(entry);
            }
            is.close();
            in.close();
        }
        catch (Exception ex) {
            System.err.println("loadEntries: " + ex.getMessage());
        }
        finally {
            System.out.println("entries loaded");
            
        }
    }
    
    /**
     * Load a single entry from given input stream
     * Encryption is used if this.secretKey is non-null
     * 
     * @param ois
     * @return Loaded entry or null
     */
    private HighScoreEntry loadSingleEntry(ObjectInputStream ois) {
        
        try {
            if(this.secretKey != null) {
                SealedObject sealedObject = (SealedObject) ois.readObject();
                if(sealedObject == null)
                    return null;

                String algorithmName = sealedObject.getAlgorithm();
                Cipher cipher = Cipher.getInstance(algorithmName);
                cipher.init(Cipher.DECRYPT_MODE, this.secretKey);

                return (HighScoreEntry) sealedObject.getObject(cipher);
            }
            else {
                return (HighScoreEntry) ois.readObject();
            }
        }
        catch(Exception e) {
            return null;
        }

    }
    
    
    /**
     * Persist entries to disk
     */
    private void saveEntries() {
        
        if(this.entries.isEmpty())
            return;
        
        
        boolean backupOk = false;
        try {
            File f = new File(this.FILENAME);
            f.renameTo( new File(this.FILENAME + ".bak") );
        }
        catch( Exception e ) {
            System.err.println("Failed to backup highscore file");
        }
        finally {
            backupOk = true;
        }
        
        FileOutputStream fos = null;
        ObjectOutputStream ous = null;
        try {
            fos = new FileOutputStream(this.FILENAME);
            ous = new ObjectOutputStream(fos);
                
            for(HighScoreEntry entry : this.entries) {
                this.saveSingleEntry(entry, ous);
            }
            if(ous != null)
                ous.close();
            if(fos != null)
                fos.close();
        }
        catch(IOException e) {
            System.err.println("Failed to persist" + e.getMessage());
            // roll back
            try {
                File f = new File(this.FILENAME + ".bak");
                f.renameTo( new File(this.FILENAME ) );
            }
            catch(Exception e2) {
                System.err.println(e2.getMessage());
            }
            finally {
                System.out.println("Restored old entries");
            }
        }
        finally {
            System.out.println("entries saved");
            if(backupOk) {
                try {
                    new File(this.FILENAME + ".bak").delete();
                }
                catch(Exception e) {
                    System.err.println("Failed to remove backup file" +
                            e.getMessage());    
                }
            }
        }
    }
    
    /**
     * Save a single entry to given ObjectOutputStream
     * Encryption is used if this.secretKey is non-null
     * @param entry entry to save
     * @param ous 
     */
    private void saveSingleEntry(HighScoreEntry entry, ObjectOutputStream ous) {
        try {
            if(this.secretKey != null) {
                Cipher ecipher = Cipher.getInstance("DES");
                ecipher.init(Cipher.ENCRYPT_MODE, this.secretKey);
                ous.writeObject( new SealedObject(entry, ecipher) );
            }
            else {
                ous.writeObject(entry);
            }
        }
        catch(Exception e) {
            System.err.println("Failed to save entry " + entry 
                    + " " + e.getMessage() );
        }
        
    }
    
    
    /**
     * Get a sorted list of high score entries for given level
     * 
     * @param level level for which to get high score entries
     * @return sorted list of entries 
     */
    public List<HighScoreEntry> getEntries(MiinaEngine.Level level)  {
        List<HighScoreEntry> ret = new ArrayList<>();
        for(HighScoreEntry entry : this.entries) {
            if(entry.level == level)
                ret.add(entry);
        }
        Collections.sort(ret, new HighScoreEntryComparator());
        return ret;
    }
    
    @Override
    public void gameStarted() {
        
    }
    
    @Override 
    public void gameOver(boolean won) {
        
    }
    
    @Override
    public void gameWinningStats(MiinaEngine.Level level, int seconds) {
        
        if(this.makesHighScore(level, seconds)) {
            
            this.insertEntry( new HighScoreEntry(level,
                seconds, 
                new Date(), 
                this.nameProvider.getNameForHighScore() ) );

            if(this.autoPersist)
                this.saveEntries();

        }
    }
    
    /**
     * Add a high score entry to internal collection making sure that
     * no more than maximum amount of entries are kept per level
     * @param entry high score entry to add
     */
    private void insertEntry(HighScoreEntry entry) {
        
        if(Collections.frequency(entries, this) != 0) { 
            //disallow duplicates. this exists only for unit tests
            // it will never happen in real use
            System.out.println("ignoring attempt to insert duplicate entry");
            return;
        }
        
        List<HighScoreEntry> levelEntries = this.getEntries(entry.level);
        assert( levelEntries.size() <= this.MAX_ENTRIES_PER_LEVEL );
        if(levelEntries.size() == this.MAX_ENTRIES_PER_LEVEL) {
            // remove worst
            HighScoreEntry worst = Collections.max(levelEntries,
                new HighScoreEntryComparator());
            assert( worst.time > entry.time );
            this.entries.remove( worst );
        }
        this.entries.add(entry);
    }
    
    /**
     * Checks whether given time makes the high score list for the given level
     * @param level    given level
     * @param seconds  given time
     * @return true if make high score list
     */
    private boolean makesHighScore(MiinaEngine.Level level, int seconds) {
        List<HighScoreEntry> levelEntries = getEntries(level);
        
        if(levelEntries.size() < this.MAX_ENTRIES_PER_LEVEL)
            return true;
        HighScoreEntry worst = Collections.max(levelEntries,
                new HighScoreEntryComparator());
        if(worst.time > seconds)
            return true;
        return false;
    }
       
}
