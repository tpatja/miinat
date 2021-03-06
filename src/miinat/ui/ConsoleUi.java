package miinat.ui;

import miinat.engine.MiinaEngine;
import miinat.engine.GameLevel;
import miinat.engine.IEngineObserver;

import java.util.Scanner;


/**
 * A simple console user interface
 */
public class ConsoleUi implements IEngineObserver {
    
    private MiinaEngine engine;
    private GameState state;
        
    public ConsoleUi() {
        this.state = GameState.Initial;
        this.engine = new MiinaEngine(this);
        this.engine.startGame(GameLevel.Beginner);
    }
    
    public void startGame() {
        update();
    }
    
    @Override
    public void gameStarted() {
        this.state = GameState.Playing;
    }
    
    @Override
    public void gameOver(boolean won) {
        this.state = (won) ? GameState.GameWon : GameState.GameLost;
        update();
    }
    
    @Override
    public void gameWinningStats(GameLevel level, int seconds) {
        System.out.println("game won in " + seconds + " seconds");
    }
    
    private void update() {
        if(this.state == GameState.Playing ||this.state == GameState.Initial) {
            drawBoard();
            getInput();
        }
        else if(this.state == GameState.GameWon) {
            drawBoard();
            System.out.println("Game Over. You won.");
        }
        else if(this.state == GameState.GameLost) {
            drawBoard();
            System.out.println("Game Over. You lost.");
        }
    }
   
    /**
     * Draw the grid
     */
    private void drawBoard() {
        String repr = engine.getGridRepresentation();
        for(int y=0;y<engine.getHeight(); ++y) {
            for(int x=0; x<engine.getWidth(); ++x) {
                System.out.print(repr.charAt(y*engine.getWidth() +x));
            }
            System.out.println();
        }
        System.out.println();
    }
    
    /**
     * Get input from user
     */
    private void getInput() {
        while(true) {
            System.out.println("input move>");
            Scanner s = new Scanner(System.in);
            String input = s.next();
            if(input.matches("[0-9]+,[0-9]+")) {
                int x = Integer.parseInt(input.split(",")[0]);
                int y = Integer.parseInt(input.split(",")[1]);
                if(x >= 0 && x < engine.getWidth() &&
                   y >= 0 && y < engine.getHeight() ) {
                    System.out.println("uncovering x=" + x + ",y=" + y);
                    engine.uncoverSquare(x, y);
                    if(!engine.isGameOver()) {
                        update();
                    }
                    break;
                }
            }
            else if(input.startsWith("q")) {
                System.exit(0);
            }
            System.out.println("format=x,y\n"
                    + "q to quit\n"
                    + "coordinates are 0 based and must be on the board");
        }
    }
    
    public static void main(String args[]) {
        ConsoleUi ui = new ConsoleUi();
        ui.startGame();
    }
    
}
