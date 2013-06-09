package miinat.ui;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import miinat.engine.HighScoreEntry;
import miinat.engine.GameLevel;

/**
 * Swing dialog for displaying high scores
 */
public class HighScoreDialog extends javax.swing.JDialog {

    private miinat.engine.HighScoreManager highScoreManager;
    
    HighScoreDialog(miinat.engine.HighScoreManager highScoreManager) {
        this.setTitle("Miinat high scores");
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.highScoreManager = highScoreManager;
        this.initComponents();
        this.initListSelectionListener();
        this.initKeyListener();
        this.levelChoiceList.setSelectedIndex(0);
    }

    private void initListSelectionListener() {
        this.levelChoiceList.addListSelectionListener( new ListSelectionListener() {
            
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if(!e.getValueIsAdjusting()) {
                    updateHighScoreList();
                }
            }
        });
    }
    
    private void initKeyListener() {
        this.levelChoiceList.addKeyListener( new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {}

            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == e.VK_ESCAPE) {
                    HighScoreDialog.this.setVisible(false);
                    HighScoreDialog.this.dispose();
                }
            }
            
            @Override
            public void keyReleased(KeyEvent e) {}
        });
    }
    
    private void updateScoreList(GameLevel level) {
        List<HighScoreEntry> entries = this.highScoreManager.getEntries(level);
        if(entries.isEmpty()) {
            this.textArea.setText("No entries!");
            return;
        }
        
        this.textArea.setText("");
        this.textArea.append(String.format("\t%s\t%s\t%s\n\n", 
                "Name", "Time", "Date"));
        
        int i=0;
        for(HighScoreEntry entry : entries) {
            this.textArea.append(String.format("%d\t%s\t%d\t%s\n", 
                    ++i, entry.name, entry.time, entry.date.toLocaleString()));
        }
        
    }
    
    private void updateHighScoreList() {
        if(levelChoiceList.getSelectedValue() == null)
            return;
        switch(levelChoiceList.getSelectedValue().toString()) {
            case "Beginner":
                updateScoreList(GameLevel.Beginner);
                break;
            case "Intermediate":
                updateScoreList(GameLevel.Intermediate);
                break;
            case "Advanced":
                updateScoreList(GameLevel.Advanced);
                break;
        }
    }
    
    public void refresh() {
        this.updateHighScoreList();
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        levelChoiceScrollPane = new javax.swing.JScrollPane();
        levelChoiceList = new javax.swing.JList();
        jScrollPane1 = new javax.swing.JScrollPane();
        textArea = new javax.swing.JTextArea();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setFocusCycleRoot(false);
        setModalExclusionType(java.awt.Dialog.ModalExclusionType.APPLICATION_EXCLUDE);
        setResizable(false);

        levelChoiceList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Beginner", "Intermediate", "Advanced" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        levelChoiceList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        levelChoiceList.setAutoscrolls(false);
        levelChoiceScrollPane.setViewportView(levelChoiceList);

        textArea.setColumns(20);
        textArea.setRows(5);
        textArea.setFocusable(false);
        textArea.setRequestFocusEnabled(false);
        jScrollPane1.setViewportView(textArea);

        jLabel1.setText("High scores");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(levelChoiceScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 417, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(levelChoiceScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(183, Short.MAX_VALUE))
                    .addComponent(jScrollPane1)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JList levelChoiceList;
    private javax.swing.JScrollPane levelChoiceScrollPane;
    private javax.swing.JTextArea textArea;
    // End of variables declaration//GEN-END:variables
}
