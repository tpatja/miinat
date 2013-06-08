package miinat.engine;

/**
 * Class representing a square on the mine field
 */
public class Square {
    public boolean hasMine;
    public boolean isFlagged;
    public int x;
    public int y;
    public int surroundingMines;

    /**
     * Constructor
     * 
     * @param x X-coordinate
     * @param y Y-coordinate
     */
    public Square(int x, int y) {
        this.x = x;
        this.y = y;
        this.isFlagged = false;
        this.hasMine = false;
        this.surroundingMines = -1;

    }
    
    /**
     * Constructor
     * 
     * @param x X-coordinate
     * @param y Y-coordinate
     * @param hasMine If true, square has a mine
     */
    public Square(int x, int y, boolean hasMine) {
        this.x = x;
        this.y = y;
        this.isFlagged = false;
        this.hasMine = hasMine;
        this.surroundingMines = -1;
    }

    public boolean isCovered() {
        return this.surroundingMines == -1;
    }
    
    /**
     * Resets "visible" fields (all except coordinates) to initial state
     */
    public void reset() {
        this.isFlagged = false;
        this.hasMine = false;
        this.surroundingMines = -1;
    }

}
    