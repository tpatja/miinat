package miinat.engine;


public interface IEngineObserver {
    /**
     * Game started event
     */
    void gameStarted();
    
    /**
     * Game over event
     * 
     * @param won true if the user succeeded in uncovering all squares
     */
    void gameOver(boolean won);

    
    /**
     * Statistics of a won game. Emitted after gameOver(true)
     * 
     * @param level   Played level
     * @param seconds Time used in seconds
     */
    void gameWinningStats(MiinaEngine.Level level, int seconds);
    
}
