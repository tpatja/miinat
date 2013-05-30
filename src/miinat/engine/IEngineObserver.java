package miinat.engine;

/**
 *
 * @author tpatja
 */
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
    
}
