package miinat.engine;

/**
 *
 * @author tpatja
 */
public interface IEngineObserver {
    void gameStarted();
    void gameOver(boolean won);
    
}
