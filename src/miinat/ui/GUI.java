package miinat.ui;

import java.awt.Color;
import miinat.engine.*;
import java.awt.GridLayout;
import javax.swing.*;
import java.awt.event.*;

/**
 *
 * A Swing graphical user interface (Main window)
 * 
 */
public class GUI 
extends 
        JFrame 
implements
        MouseListener,
        miinat.engine.IEngineObserver,
        miinat.engine.IHighScoreManagerAdapter {
    
    /**
     * Internally used container class containing a Square and JLabel for 
     * displaying it.
     */
    private class UiSquare {
        
        private JLabel label;
        private Square square;
    
        UiSquare(Square s, JLabel l) {
            this.square = s;
            this.label = l;
        }
        
        public JLabel getLabel() {
            return this.label;
        }
        public void setLabel(JLabel l) {
            this.label = l;
        }
        public Square getSquare() {
            return this.square;
        }
        public void setSquare(Square s) {
            this.square = s;
        }
        
    }
    
    private MiinaEngine engine;
    private UiSquare[][] squares;
    private GameState gameState;
    private GameLevel level;
    private Timer timeUpdater;
    private HighScoreDialog highScoreDialog;
    
    /**
     * Creates new form GUI
     */
    public GUI() {
        super("Miinat");
        this.initComponents();
        // disable user resizing to prevent game grid looking distorted
        this.setResizable(false); 
        engine = new MiinaEngine(this);
        engine.initHighScoreManager(this);
        this.gameState = GameState.Initial;
        this.level = GameLevel.Beginner;
        this.engine.startGame(this.level);
    }

    /**
     * (Re)Initialize UI data and re-size the window according to grid size
     * 
     */
    private void initUi() {
                
        boolean isReinit = this.initUiSquares();
        this.resizeAccordingToGameDimensions();
        
        if(isReinit) {
            // java "feature". needed if you dynamically add+remove components
            this.getContentPane().invalidate();
            this.getContentPane().validate();
        }
        GridLayout gridLayout = new GridLayout(engine.getHeight(), engine.getWidth());
        this.gridPanel.setLayout(gridLayout);
        gridLayout.addLayoutComponent(null, this);
        
    }
 
    /**
     * Remove possibly existing UI square labels, create 2-dimensional array
     * for UI square objects
     * @return true in case we are re-initializing (not 1st call)
     */
    private boolean initUiSquares() {
        boolean isReinit = false;
        if(this.squares != null) {
            isReinit = true;
            for(int x=0; x<this.squares.length; ++x) {
                for(int y=0; y<this.squares[x].length; ++y) {
                    this.gridPanel.remove(this.squares[x][y].label);
                }
            }
        }
        
        this.squares = null;
        this.squares = new UiSquare[engine.getWidth()][engine.getHeight()];
        this.createUiSquares();
        return isReinit;
    }
    
    
    /**
     * Create UiSquare objects 
     */
    private void createUiSquares() {
        for(int y=0; y<engine.getHeight(); ++y) {
            for(int x=0; x<engine.getWidth(); ++x) {
                JLabel label = new JLabel();
                label.setOpaque(true);
                label.setSize(20,20);
                label.setBackground(Color.GRAY);
                label.setBorder( BorderFactory.createLineBorder(Color.BLACK) );
                label.addMouseListener(this);
                this.squares[x][y] = new UiSquare(engine.squareAt(x, y), label);
                this.gridPanel.add(label);
            }
        }
    }
    
    
    /**
     * Dynamically resize main window according to game grid dimensions
     */
    private void resizeAccordingToGameDimensions() {
        final int gapSpace = 10;
        final int squareSide = 20;
        int w = engine.getWidth()*squareSide + gapSpace;
        int h = engine.getHeight()*squareSide + gapSpace 
                + this.getJMenuBar().getHeight() +
                  this.topPanel.getHeight();
        this.setSize(w, h);
    }
    
    private void updateUi() {
        for(int y=0; y<engine.getHeight(); ++y) {
            for(int x=0; x<engine.getWidth(); ++x) {
                UiSquare s = this.squares[x][y];
                s.label.setBackground( this.getColorForSquare(s.square) );
                s.label.setText( this.getTextForSquare(s.square) );
            }
        }
        this.gridPanel.repaint();
    }
    
    private Color getColorForSquare(Square square) {
        if(square.isCovered())
            return (square.isFlagged) ? Color.YELLOW : Color.GRAY;
        return (square.hasMine) ? Color.RED : Color.WHITE;
    }
    
    private String getTextForSquare(Square square) {
        if(!square.isCovered() && !square.hasMine && square.surroundingMines > 0)
            return Integer.toString(square.surroundingMines);
        return "";
    }
    
    @Override
    public void gameStarted() {
        this.initUi();
        this.gameState = GameState.Playing;
        for(int y=0; y<engine.getHeight(); ++y) {
            for(int x=0; x<engine.getWidth(); ++x) {
                this.squares[x][y].square = engine.squareAt(x, y);
            }
        }
        this.topButton.setText(":)");
        if(this.timeUpdater != null)
            this.timeUpdater.stop();
        this.timeUpdater = null;
        this.timeUpdater = new Timer(500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                durationLabel.setText(Integer.toString(engine.timeElapsedInSeconds()));
            }
        });
        this.timeUpdater.start();
        this.updateUi();
    }
    
    @Override
    public void gameOver(boolean won) {
        System.out.println("gameOver: won=" + won);
        this.timeUpdater.stop();
        this.timeUpdater = null;
        this.gameState = won ? GameState.GameWon : GameState.GameLost;
        if(won)
            this.topButton.setText(":D");
        else
            this.topButton.setText(":X");
        repaint();
    }
    
    @Override
    public void gameWinningStats(GameLevel level, int seconds) {
        System.out.println("game won in " + seconds + " seconds");
    }
    
    @Override
    public String getNameForHighScore() {
        return JOptionPane.showInputDialog(this, 
                "You have made the high score list. Please enter your name");
    }

    @Override
    public void highScoresChanged() {
        if(this.highScoreDialog != null) {
            this.highScoreDialog.refresh();
        }
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        topPanel = new javax.swing.JPanel();
        durationLabel = new javax.swing.JLabel();
        topButton = new javax.swing.JButton();
        gridPanel = new javax.swing.JPanel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem2 = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        beginner = new javax.swing.JMenuItem();
        intermediate = new javax.swing.JMenuItem();
        advanced = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        highScoreMenuItem = new javax.swing.JMenuItem();
        aboutMenuItem = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        durationLabel.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N

        topButton.setText(":)");
        topButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                topButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout topPanelLayout = new javax.swing.GroupLayout(topPanel);
        topPanel.setLayout(topPanelLayout);
        topPanelLayout.setHorizontalGroup(
            topPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, topPanelLayout.createSequentialGroup()
                .addComponent(topButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 208, Short.MAX_VALUE)
                .addComponent(durationLabel)
                .addContainerGap())
        );
        topPanelLayout.setVerticalGroup(
            topPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(durationLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, topPanelLayout.createSequentialGroup()
                .addComponent(topButton)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout gridPanelLayout = new javax.swing.GroupLayout(gridPanel);
        gridPanel.setLayout(gridPanelLayout);
        gridPanelLayout.setHorizontalGroup(
            gridPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        gridPanelLayout.setVerticalGroup(
            gridPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 211, Short.MAX_VALUE)
        );

        jMenu1.setText("Game");

        jMenuItem2.setText("New Game");
        jMenuItem2.setName(""); // NOI18N
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newGameActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem2);
        jMenu1.add(jSeparator1);

        beginner.setText("Beginner");
        beginner.setActionCommand("cmdBeginner");
        beginner.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                beginnerActionPerformed(evt);
            }
        });
        jMenu1.add(beginner);

        intermediate.setText("Intermediate");
        intermediate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                intermediateActionPerformed(evt);
            }
        });
        jMenu1.add(intermediate);

        advanced.setText("Advanced");
        advanced.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                advancedActionPerformed(evt);
            }
        });
        jMenu1.add(advanced);
        jMenu1.add(jSeparator2);

        jMenuItem3.setText("Exit");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem3);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Help");

        highScoreMenuItem.setText("High scores");
        highScoreMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                highScoreMenuItemActionPerformed(evt);
            }
        });
        jMenu2.add(highScoreMenuItem);

        aboutMenuItem.setText("About");
        aboutMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutMenuItemActionPerformed(evt);
            }
        });
        jMenu2.add(aboutMenuItem);

        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(topPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(gridPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(topPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(gridPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void aboutMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aboutMenuItemActionPerformed
        JOptionPane.showMessageDialog(this, 
                "Miinat\n\nMinesweeper clone\nAuthor: Teemu Patja <tp@iki.fi>");
                
    }//GEN-LAST:event_aboutMenuItemActionPerformed

    private void newGameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newGameActionPerformed
        engine.startGame(this.level);
    }//GEN-LAST:event_newGameActionPerformed

    private void exitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitActionPerformed
        System.exit(0);    
    }//GEN-LAST:event_exitActionPerformed

    private void beginnerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_beginnerActionPerformed
        this.level = GameLevel.Beginner;
        this.engine.startGame(this.level);
    }//GEN-LAST:event_beginnerActionPerformed

    private void intermediateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_intermediateActionPerformed
        this.level = GameLevel.Intermediate;
        this.engine.startGame(this.level);
    }//GEN-LAST:event_intermediateActionPerformed

    private void advancedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_advancedActionPerformed
        this.level = GameLevel.Advanced;
        this.engine.startGame(this.level);
    }//GEN-LAST:event_advancedActionPerformed

    private void topButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_topButtonActionPerformed
        engine.startGame(this.level);
    }//GEN-LAST:event_topButtonActionPerformed

    private void highScoreMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_highScoreMenuItemActionPerformed
        if(this.highScoreDialog == null) {
            this.highScoreDialog = new HighScoreDialog(engine.getHighScoreManager());
            this.highScoreDialog.setLocation(
                    new java.awt.Point((int)this.getLocation().getX() + this.getWidth(), 
                                       (int)this.getLocation().getY()) 
                    );
        }
            
        this.highScoreDialog.show();
    }//GEN-LAST:event_highScoreMenuItemActionPerformed

    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new GUI().setVisible(true);
            }
        });
    }
    
    
    @Override
    public void mouseClicked(MouseEvent e) {
        if(this.gameState != GameState.Playing)
            return;
        
        Square sq = this.uiSquareByMouseEventSource( e.getSource() ).square;
        if(SwingUtilities.isLeftMouseButton(e)) {
            engine.uncoverSquare(sq.x, sq.y);
        }
        else {
            engine.flagSquare(sq.x, sq.y, !sq.isFlagged);
        }
        this.updateUi();
    }
    
    private UiSquare uiSquareByMouseEventSource(Object o) {
       for(int y=0; y<engine.getHeight(); ++y) {
            for(int x=0; x<engine.getWidth(); ++x) {
                UiSquare us = this.squares[x][y];
                if(us.getLabel() == o)
                    return us;
            }
        }
       return null;
    }
    
    @Override
    public void mousePressed(MouseEvent e) {}
    @Override
    public void mouseReleased(MouseEvent e) {}
    
    @Override
    public void mouseEntered(MouseEvent e) {
        
        if(this.gameState != GameState.Playing) 
            return;
        
        UiSquare us = this.uiSquareByMouseEventSource(e.getSource());
        if(us.getSquare().isCovered() && !us.getSquare().isFlagged) {
            us.getLabel().setBackground(Color.LIGHT_GRAY);
        }
    }
    @Override
    public void mouseExited(MouseEvent e) {
        
        if(this.gameState != GameState.Playing) 
            return;
        UiSquare us = this.uiSquareByMouseEventSource(e.getSource());
        if(us.getSquare().isCovered() && !us.getSquare().isFlagged) {
            us.getLabel().setBackground(Color.GRAY);
        }
    }
   
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem aboutMenuItem;
    private javax.swing.JMenuItem advanced;
    private javax.swing.JMenuItem beginner;
    private javax.swing.JLabel durationLabel;
    private javax.swing.JPanel gridPanel;
    private javax.swing.JMenuItem highScoreMenuItem;
    private javax.swing.JMenuItem intermediate;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JButton topButton;
    private javax.swing.JPanel topPanel;
    // End of variables declaration//GEN-END:variables

}
