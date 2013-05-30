package miinat.engine;

/**
 *
 * @author tpatja
 */
public class Square {
    public boolean hasMine;
    public boolean isFlagged;
    public int x;
    public int y;
    public int surroundingMines;

    public Square(int x, int y) {
        this.x = x;
        this.y = y;
        this.isFlagged = false;
        this.hasMine = false;
        this.surroundingMines = -1;

    }
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

}
    