package miinat.engine;

/**
 *
 * Interface used for querying name for a high score entry
 * and notifying of high score changes
 * 
 */
public interface IHighScoreManagerAdapter {

    /**
     * Request user's name for new high score
     * @return the name
     */
    public String getNameForHighScore();

    /**
     * Called when high scores change. 
     */
    public void highScoresChanged();

}
