package miinat.engine;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;



/**
 * Class responsible for storing and retrieving high scores.
 * 
 * Serialized entries are obfuscated using using ROT-13 before saving to disk 
 * to make it more difficult for clever users to fake high scores :)
 *
 */
public class HighScoreManager implements IEngineObserver {

    private final String FILENAME="miinat.dat";
    private final int MAX_ENTRIES_PER_LEVEL=10;
    
    private ArrayList<HighScoreEntry> entries;
    private HighScoreNameProvider nameProvider;
    private boolean autoPersist;
    
    /**
     * Constructor
     * 
     * @param nameProvider interface for getting name when gameWinningStats
     *   gets called
     * @param autoPersist if true, entries are automatically loaded from disk 
     *  in constructor and saved to disk when created
     */
    HighScoreManager(HighScoreNameProvider nameProvider, boolean autoPersist) {
        this.nameProvider = nameProvider;
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
                HighScoreEntry loadedEntry = (HighScoreEntry)is.readObject();
                if(loadedEntry == null)
                    break;
                System.out.println("got one");
                entries.add(loadedEntry);
            }
            is.close();
            in.close();
        }
        catch (ClassNotFoundException | IOException ex) {
            System.err.println(ex.getMessage());
        }
        finally {
            System.out.println("entries loaded");
            
        }

    }
    
    /**
     * Persist entries to disk
     */
    private void saveEntries() {
        
        try {
            FileOutputStream f = new FileOutputStream(this.FILENAME);
            ObjectOutput s = new ObjectOutputStream(f);
                
            for(HighScoreEntry entry : this.entries) {
                s.writeObject(entry);
            }
            s.close();
            f.close();
        }
        catch(IOException e) {
            System.out.println("Failed to persist" + e.getMessage());
        }
        finally {
            System.out.println("entries saved");
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
                this.nameProvider.getNameForHighScore()) );
            
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
