package miinat;

import java.util.ArrayList;
import java.util.Random;



/**
 *
 * @author tpatja
 */
public class MiinaEngine {

    
    public interface MiinaEngineListener
    {
        void gameStarted();
        void gameOver();
    }

    public class Square {
        public Square(int x, int y) {
            this.x = x;
            this.y = y;
            this.isFlagged = false;
            this.hasMine = false;
        }
        public Square(int x, int y, boolean hasMine) {
            this.x = x;
            this.y = y;
            this.isFlagged = false;
            this.hasMine = hasMine;
        }
        public boolean hasMine;
        public boolean isFlagged;
        public int x;
        public int y;
    }
    
    public MiinaEngine(int width, int height, int nMines, MiinaEngineListener listener)
    {
        this.width = width;
        this.height = height;
        this.nMines = nMines;
        this.gameOver = false;
        this.listener = listener;
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
     * Initialize engine with randomly placed mines
     */    
    public void init() 
    {
        
        // randomly place mines
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
        listener.gameStarted();
    }
    
    /**
     * Initialize engine with mines represented by given string
     * 
     * Format: '*' means mine, '-' means no mine.
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
        listener.gameStarted();
    }
    

    public void uncoverSquare(int x, int y) {
        assert !this.gameOver;
        Square s = this.squareAt(x, y);
        if(s.hasMine) {
            this.gameOver = true;
            listener.gameOver();
        }
        else {
            // TODO: implement logic
        }
        
    }
    
    /**
     * 
     * @param x
     * @param y
     * @return true if given coordinates are on the board
     */
    private boolean squareIsOnBoard(int x, int y) {
        return this.squareAt(x,y) != null;
    }
    
    /** 
     * 
     * @param s
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
    
    public int countSurroundingMines(int x, int y) {
        int ret=0;
        ArrayList<Square> neighbors = this.getSurroundingSquares(this.squareAt(x, y));
        for(Square neighbor : neighbors) {
            if(neighbor.hasMine)
                ++ret;
        }
        return ret;
    }
    
    
    private int nMines;
    private int width;
    private int height;
    private boolean gameOver;
    private MiinaEngineListener listener;
    private ArrayList<Square> squares;
}
