package miinat;

import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author tpatja
 */
public class MiinaEngine {

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
    
    public MiinaEngine(int width, int height, int n_mines)
    {
        this.width = width;
        this.height = height;
        this.n_mines = n_mines;
        this.game_over = false;
        this.squares = new ArrayList<Square>();
    }

    public int getHeight() {
        return this.height;
    }
    
    public int getWidth() {
        return this.width;
    }
    
    public boolean isGameOver() {
        return game_over;
    }
    
    public Square squareAt(int x, int y) {
        for(Square s : squares) {
            if(s.x == x && s.y == y)
                return s;
        }
        return null;
    }
    
    
    private Square getRandomSquare() {
        if(this.squares.size() == 0)
            return null;
        int idx = (int)(Math.random() * (this.squares.size()-1));
        return this.squares.get(idx);
    }
    
    public void init() 
    {
        for(int y=0; y < this.height; ++y) {
            for(int x=0; x < this.width; ++x) {
                this.squares.add(new Square(x,y));
            }
        }
        
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
    }

    
    private int n_mines;
    private int width;
    private int height;
    private boolean game_over;
    private ArrayList<Square> squares;
}
