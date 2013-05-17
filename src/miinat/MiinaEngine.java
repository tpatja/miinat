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
            this.flagged = false;
            this.has_mine = false;
        }
        public Square(int x, int y, boolean has_mine) {
            this.x = x;
            this.y = y;
            this.flagged = false;
            this.has_mine = has_mine;
        }
        public boolean has_mine;
        public boolean flagged;
        public int x;
        public int y;
    }
    
    public MiinaEngine(int width, int height, int n_mines, MiinaEngineListener listener)
    {
        this.width = width;
        this.height = height;
        this.n_mines = n_mines;
        this.game_over = false;
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
        return this.game_over;
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
        for(int i=0; i < this.n_mines; i++) {
            Square s = null;
            while(true) {
                s = this.getRandomSquare();
            
                if(!s.has_mine) {
                    s.has_mine = true;
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
     *         amount of mines must be equal to this.n_mines
     *         
     * @param mine_data 
     */
    public void init(String mine_data) {
        assert mine_data.length() == this.squares.size();
        int given_mines=0;
        for(int i=0; i<mine_data.length(); ++i) {
            if(mine_data.charAt(i) == '*')
                ++given_mines;
        }
        // TODO: maybe OK to mutate this.n_mines in this case?
        assert given_mines == this.n_mines;
        
        for(int y=0; y<this.getHeight(); ++y) {
            for(int x=0; x<this.getWidth(); ++x) {
                int idx = y * this.getWidth() + x;
                this.squareAt(x,y).has_mine = mine_data.charAt(idx) == '*';
            }
        }
        listener.gameStarted();
    }
    

    public void uncoverSquare(int x, int y) {
        assert !this.game_over;
        Square s = this.squareAt(x, y);
        if(s.has_mine) {
            this.game_over = true;
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
        for(int x_offset = -1; x_offset <= 1; ++x_offset) {
            for(int y_offset = -1; y_offset <= 1; ++y_offset) {
                int x = s.x + x_offset;
                int y = s.y + y_offset;
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
            if(neighbor.has_mine)
                ++ret;
        }
        return ret;
    }
    
    
    private int n_mines;
    private int width;
    private int height;
    private boolean game_over;
    private MiinaEngineListener listener;
    private ArrayList<Square> squares;
}
