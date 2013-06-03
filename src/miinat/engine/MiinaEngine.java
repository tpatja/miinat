package miinat.engine;

import java.util.ArrayList;
import java.util.Random;

import miinat.engine.Square;


/**
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
     * Contructor
     * 
     * @param width    Width of minefield
     * @param height   Height of minefield
     * @param nMines   Number of mines
     * @param observer Observer interface that will receive events
     */
    public MiinaEngine(int width, int height, int nMines, IEngineObserver observer)
    {
        this.width = width;
        this.height = height;
        this.nMines = nMines;
        this.gameOver = false;
        this.observer = observer;
        this.squares = new ArrayList<>();

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
     * (Re)Initialize engine with randomly placed mines
     */    
    public void init() 
    {
        for(int i=0; i < this.squares.size(); ++i)
            this.squares.get(i).reset();
        
        this.randomlyPlaceMines();
        observer.gameStarted();
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
     * Initialize engine with mines represented by given string
     * 
     * Format: '*' means mine, '-' means no mine.
     * Remarks:
     *         data length must be equal to amount of squares.
     *         amount of mines must be equal to this.nMines
     *         
     * @param mineData 
     */
    public void init(String mineData) {
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
     * Uncover a square given its coordinates
     * 
     * @param x x-coordinate of square to uncover
     * @param y y-coordinate of square to uncover
     */
    public void uncoverSquare(int x, int y) {
        assert !this.gameOver;
        Square s = this.squareAt(x, y);
        if(s == null) // ignore invalid co-ordinates
            return;
        
        s.surroundingMines = this.countSurroundingMines(s);
        if(s.hasMine) {
            this.gameOver = true;
            observer.gameOver(false);
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

}
