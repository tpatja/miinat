/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package miinat.ui;

import miinat.engine.MiinaEngine;
import miinat.engine.IEngineObserver;

import java.util.Scanner;


/**
 *
 * @author tpatja
 */
public class ConsoleUi implements IEngineObserver {
    
    private MiinaEngine engine;
    
    private enum GameState {
        Initial,
        Playing,
        GameWon,
        GameLost
    }
    private GameState state;
        
    public ConsoleUi() {
        this.engine = new MiinaEngine(10, 10, 15, this);
        this.engine.init();
        this.state = GameState.Initial;
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
    
    private void getInput() {
        while(true) {
            System.out.println("input move>");
            Scanner s = new Scanner(System.in);
            String input = s.next();
            if(input.matches("[0-9]+,[0-9]+")) {
                int x = Integer.parseInt(input.split(",")[0]);
                int y = Integer.parseInt(input.split(",")[1]);
                System.out.println("uncovering x=" + x + ",y=" + y);
                engine.uncoverSquare(x, y);
                if(!engine.isGameOver()) {
                    update();
                }
                break;
            }
            else if(input.startsWith("q")) {
                System.exit(0);
            }
            else {
                System.out.println("format=x,y, q to quit");
            }
        }
    }
    
    public static void main(String args[]) {
        ConsoleUi ui = new ConsoleUi();
        ui.startGame();
    }
    
}
