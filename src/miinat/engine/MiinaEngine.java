package miinat.engine;

import java.util.ArrayList;
import java.util.Random;

import miinat.engine.Square;


/**
 * Class implementing minesweeper game logic
 *
 * @author tpatja
 */
public class MiinaEngine {

    private int nMines;
    private int width;
    private int height;
    private boolean gameOver;
    private IEngineObserver observer;
    private ArrayList<Square> squares;
    
    /**
     * Constructor
     * 
     * @param observer Observer interface that will receive events
     */
    public MiinaEngine(IEngineObserver observer)
    {
        this.gameOver = false;
        this.observer = observer;
        this.squares = new ArrayList<>();

    }

    /** (Re)Start game with given parameters.
     * 
     * @param width   Width of minefield
     * @param height  Height of minefield
     * @param nMines  Amount of mines
     */
    public void startGame(int width, int height, int nMines) {
        this.width = width;
        this.height = height;
        this.nMines = nMines;
        this.initSquares();
        this.randomlyPlaceMines();
        observer.gameStarted();
    }

    /** (Re)Start game with given level
     * 
     * @param l Level describing amount of mines, width and height
     */
    public void startGame(Level l) {
        this.setLevelParams(l);
        this.initSquares();
        this.randomlyPlaceMines();
        observer.gameStarted();
    }

    /**
     * (Re)Start game with mines represented by given string
     * 
     * Format: '*' means mine, '-' means no mine.
     * Remarks:
     *         data length must be equal to amount of squares.
     *         amount of mines must be equal to this.nMines
     *         
     * @param mineData 
     */
    public void startGame(int width, int height, int nMines, String mineData) {
        this.width = width;
        this.height = height;
        this.nMines = nMines;
        this.initSquares();
        
        assert mineData.length() == this.squares.size();
        int givenMineCount=0;
        for(int i=0; i<mineData.length(); ++i) {
            if(mineData.charAt(i) == '*')
                ++givenMineCount;
        }
        // TODO: maybe OK to mutate this.nMines in this case?
        assert givenMineCount == this.nMines;
        
        for(int y=0; y<this.getHeight(); ++y) {
            for(int x=0; x<this.getWidth(); ++x) {
                int idx = y * this.getWidth() + x;
                this.squareAt(x,y).hasMine = mineData.charAt(idx) == '*';
            }
        }
        observer.gameStarted();
    }

    /**
     * Helper for avoiding repetition
     */
    private void initSquares() {
        this.squares.clear();
        for(int y=0; y < this.height; ++y) {
            for(int x=0; x < this.width; ++x) {
                this.squares.add(new Square(x,y));
            }
        }

    }
    
    
    public int getHeight() {
        return this.height;
    }
    
    public int getWidth() {
        return this.width;
    }
    
    public int getMineCount() {
        return this.nMines;
    }
    
    public boolean isGameOver() {
        return this.gameOver;
    }
    
    public Square squareAt(int x, int y) {
        for(Square s : squares) {
            if(s.x == x && s.y == y)
                return s;
        }
        return null;
    }
    
    
    private Square getRandomSquare() {
        if(this.squares.isEmpty())
            return null;
        int idx = (int)(Math.random() * (this.squares.size()-1));
        return this.squares.get(idx);
    }
    
    
    /**
     * Pre-defined levels that define width, height and mine count
     */
    public enum Level {
        Beginner,
        Intermediate,
        Advanced
    }
    
    /**
     * Set width, height and mine count based on given level
     * @param l given level
     */
    private void setLevelParams(Level l) {
        switch(l) {
            case Beginner:
                this.width = 9;
                this.height = 9;
                this.nMines = 10;
                break;
            case Intermediate:
                this.width = 16;
                this.height = 16;
                this.nMines = 40;
                break;
            case Advanced:
                this.width = 30;
                this.height = 16;
                this.nMines = 99;
                break;
        }
    }
      
    
    private void randomlyPlaceMines() {
        for(int i=0; i < this.nMines; i++) {
            Square s = null;
            while(true) {
                s = this.getRandomSquare();
                if(!s.hasMine) {
                    s.hasMine = true;
                    break;
                }
            }
        }
    }
    

    /** 
     * Uncover a square given its coordinates
     * 
     * @param x x-coordinate of square to uncover
     * @param y y-coordinate of square to uncover
     */
    public void uncoverSquare(int x, int y) {
        assert !this.gameOver;
        Square s = this.squareAt(x, y);
        
        // ignore invalid co-ordinates and already uncovered squares
        if(s == null || !s.isCovered()) 
            return;
        
        s.surroundingMines = this.countSurroundingMines(s);
        
        if(s.hasMine) {
            this.gameOver = true;
            this.observer.gameOver(false);
        }   
        else if(this.allNonMinesUncovered()) {
            System.out.println("all non-mines uncovered");
            this.gameOver = true;
            this.observer.gameOver(true);
        } 
        else {
            if(s.surroundingMines == 0) {
                for(Square neighbor : this.getSurroundingSquares(s) ) {
                    if(neighbor.isCovered())
                        this.uncoverSquare(neighbor.x, neighbor.y);
                }
            }
        }
    }
    
    /**
     * Mark a square as flagged.
     * 
     * If it results in all mines being flagged, game is won
     * 
     * @param x x-coordinate of square to mark as flagged
     * @param y y-coordinate of square to mark as flagged
     */
    public void flagSquare(int x, int y, boolean flagged) {
        Square s = this.squareAt(x, y);
        
        if(s == null || !s.isCovered()) 
            return;
        s.isFlagged = flagged;
        if(flagged && this.allMinesFlagged())
            this.observer.gameOver(true);
        
    }
    
    /**
     * Is square on the board
     * 
     * @param x
     * @param y
     * @return true if given coordinates are on the board
     */
    private boolean squareIsOnBoard(int x, int y) {
        return this.squareAt(x,y) != null;
    }
    
    /** 
     * Retrieve surrounding squares of given square
     * 
     * @param s given square
     * @return squares surrounding given square (max 8)
     */
    private ArrayList<Square> getSurroundingSquares(Square s) {
        ArrayList<Square> ret = new ArrayList<>();
        for(int xOffset = -1; xOffset <= 1; ++xOffset) {
            for(int yOffset = -1; yOffset <= 1; ++yOffset) {
                int x = s.x + xOffset;
                int y = s.y + yOffset;
                if( !(x == s.x && y == s.y) && this.squareIsOnBoard(x, y)) {
                    ret.add(this.squareAt(x, y));
                }
            }
        }
        return ret;
    }
    
    private int countSurroundingMines(Square s) {
        return this.countSurroundingMines(s.x, s.y);
    }
    
    public int countSurroundingMines(int x, int y) {
        int ret=0;
        ArrayList<Square> neighbors = this.getSurroundingSquares(this.squareAt(x, y));
        for(Square neighbor : neighbors) {
            if(neighbor.hasMine)
                ++ret;
        }
        return ret;
    }
    
    /**
     * Get a textual representation of user presentable grid data.
     * Format: one character per square, 
     *        '#' -> covered
     *        'X' -> mine
     *        '_' -> empty
     *        '<number>' -> empty with n surrounding mines
     * 
     * @return String containing representation of the grid data
     */
    public String getGridRepresentation() {
        StringBuilder sb = new StringBuilder(this.getWidth()*this.getHeight());
        for(int y=0;y<this.getHeight(); ++y) {
            for(int x=0; x<this.getWidth(); ++x) {
                Square s = this.squareAt(x, y);
                char ch;
                if(s.isCovered()) {
                    if(s.isFlagged)
                        sb.append("F");
                    else
                        sb.append("#");
                }
                else {
                    if(s.hasMine) {
                        sb.append("X");
                    }
                    else if (s.surroundingMines > 0) {
                        sb.append(s.surroundingMines);
                    }
                    else {
                        sb.append("_");
                    }
                }
            }
        }
        return sb.toString();
    }
    
    /**
     * Check if all squares that don't contain mines are uncovered 
     *  (winning condition)
     * @return true if game is won 
     */
    private boolean allNonMinesUncovered() {
        for(int y=0;y<this.getHeight(); ++y) {
            for(int x=0; x<this.getWidth(); ++x) {
                Square s = this.squareAt(x, y);
                if(!s.hasMine && s.isCovered())
                    return false;
            }
        }
        return true;
    }
    
    /**
     * Check if all mines are flagged
     *  (winning condition)
     * @return true if all are flagged
     */
    private boolean allMinesFlagged() {
        int nFlagged=0;
        int nRealMinesFlagged=0;
        for(int y=0;y<this.getHeight(); ++y) {
            for(int x=0; x<this.getWidth(); ++x) {
                Square s = this.squareAt(x, y);
                if(s.isFlagged)
                    ++nFlagged;
                if(s.isFlagged && s.hasMine)
                    ++nRealMinesFlagged;
            }
        }
        return (nFlagged == nRealMinesFlagged && nFlagged == this.nMines);
    }

}
