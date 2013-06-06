package miinat.ui;

import java.awt.Color;
import miinat.engine.*;
import java.awt.GridLayout;
import javax.swing.*;
import java.awt.event.*;

/**
 *
 * @author Teemu Patja <tp@iki.fi>
 */
public class GUI 
extends 
        JFrame 
implements
        MouseListener,
        miinat.engine.IEngineObserver {

    
    
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
    private MiinaEngine.Level level;
    
    public enum GameState {
        Initial,
        GameRunning,
        GameWon,
        GameLost
    }
    
    /**
     * Creates new form GUI
     */
    public GUI() {
        initComponents();
        // disable user resizing so grid will always be visible
        this.setResizable(false); 
        engine = new MiinaEngine(this);
        this.gameState = GameState.Initial;
        this.level = MiinaEngine.Level.Beginner;
        this.engine.startGame(this.level);
    }

    /**
     * (Re)Initialize UI data and re-size the window according to grid size
     * 
     */
    private void initUi() {
        
        
        boolean isReinit = false;
        if(this.squares != null) {
            isReinit = true;
            for(int x=0; x<this.squares.length; ++x) {
                for(int y=0; y<this.squares[x].length; ++y) {
                    remove(this.squares[x][y].label);
                }
            }
        }
        
        this.squares = null;
        this.squares = new UiSquare[engine.getWidth()][engine.getHeight()];
        
        int w = engine.getWidth()*20 + 10;
        int h = engine.getHeight()*20 + 10 + this.getJMenuBar().getHeight();
        setSize(w, h);
        for(int y=0; y<engine.getHeight(); ++y) {
            for(int x=0; x<engine.getWidth(); ++x) {
                JLabel label = new JLabel();
                label.setOpaque(true);
                label.setSize(20,20);
                label.setBackground(Color.GRAY);
                label.setBorder( BorderFactory.createLineBorder(Color.BLACK) );
                label.addMouseListener(this);
                this.squares[x][y] = new UiSquare(engine.squareAt(x, y), label);
                add(label);
            }
        }
        
        if(isReinit) {
            // java "feature". needed if you dynamically add+remove components
            this.getContentPane().invalidate();
            this.getContentPane().validate();
        }
        GridLayout gridLayout = new GridLayout(engine.getHeight(), engine.getWidth());
        setLayout(gridLayout);
        gridLayout.addLayoutComponent(null, this);
        
    }
    
    private void updateUi() {
        for(int y=0; y<engine.getHeight(); ++y) {
            for(int x=0; x<engine.getWidth(); ++x) {
                UiSquare s = this.squares[x][y];
                if(s.square.isCovered()) {
                    if(s.square.isFlagged)
                        s.label.setBackground(Color.YELLOW);
                    else
                        s.label.setBackground(Color.GRAY);
                    s.label.setText("");
                }
                else {
                    if(s.square.hasMine) {
                        s.label.setBackground(Color.RED);
                    }
                    else {
                        if (s.square.surroundingMines > 0)
                            s.label.setText(Integer.toString(s.square.surroundingMines));
                        s.label.setBackground(Color.WHITE);
                    }
                }
            }
        }
        this.repaint();
    }
    
    @Override
    public void gameStarted() {
        initUi();
        this.gameState = GameState.GameRunning;
        for(int y=0; y<engine.getHeight(); ++y) {
            for(int x=0; x<engine.getWidth(); ++x) {
                this.squares[x][y].square = engine.squareAt(x, y);
            }
        }
        updateUi();
    }
    
    @Override
    public void gameOver(boolean won) {
        System.out.println("gameOver: won=" + won);
        this.gameState = won ? GameState.GameWon : GameState.GameLost;
        repaint();
    }
    
    @Override
    public void gameWinningStats(MiinaEngine.Level level, int seconds) {
        System.out.println("game won in " + seconds + " seconds");
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

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
        jMenuItem1 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

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
        intermediate.getAccessibleContext().setAccessibleName("Intermediate");

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

        jMenuItem1.setText("About");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem1);

        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 279, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        JOptionPane.showMessageDialog(this, 
                "Miinat\n\nMinesweeper clone\nAuthor: Teemu Patja <tp@iki.fi>");
                
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void newGameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newGameActionPerformed
        engine.startGame(this.level);
    }//GEN-LAST:event_newGameActionPerformed

    private void exitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitActionPerformed
        System.exit(0);    
    }//GEN-LAST:event_exitActionPerformed

    private void beginnerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_beginnerActionPerformed
        this.level = MiinaEngine.Level.Beginner;
        this.engine.startGame(this.level);
    }//GEN-LAST:event_beginnerActionPerformed

    private void intermediateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_intermediateActionPerformed
        this.level = MiinaEngine.Level.Intermediate;
        this.engine.startGame(this.level);
    }//GEN-LAST:event_intermediateActionPerformed

    private void advancedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_advancedActionPerformed
        this.level = MiinaEngine.Level.Advanced;
        this.engine.startGame(this.level);
    }//GEN-LAST:event_advancedActionPerformed

    
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
        if(this.gameState != GameState.GameRunning)
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
    public void mousePressed(MouseEvent e) {
        
        
    }
    @Override
    public void mouseReleased(MouseEvent e) {
        
    }
    @Override
    public void mouseEntered(MouseEvent e) {
        
        if(this.gameState != GameState.GameRunning) 
            return;
        
        UiSquare us = this.uiSquareByMouseEventSource(e.getSource());
        if(us.getSquare().isCovered() && !us.getSquare().isFlagged) {
            us.getLabel().setBackground(Color.LIGHT_GRAY);
        }
    }
    @Override
    public void mouseExited(MouseEvent e) {
        
        if(this.gameState != GameState.GameRunning) 
            return;
        UiSquare us = this.uiSquareByMouseEventSource(e.getSource());
        if(us.getSquare().isCovered() && !us.getSquare().isFlagged) {
            us.getLabel().setBackground(Color.GRAY);
        }
    }
   
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem advanced;
    private javax.swing.JMenuItem beginner;
    private javax.swing.JMenuItem intermediate;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    // End of variables declaration//GEN-END:variables


}
