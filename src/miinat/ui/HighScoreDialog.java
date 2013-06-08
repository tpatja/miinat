package miinat.ui;

import java.util.List;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import miinat.engine.HighScoreEntry;
import miinat.engine.MiinaEngine;

/**
 * Swing dialog for displaying high scores
 */
public class HighScoreDialog extends javax.swing.JFrame {

    private miinat.engine.HighScoreManager highScoreManager;
    
    public HighScoreDialog(miinat.engine.HighScoreManager highScoreManager) {
        super("Miinat high scores");
        this.highScoreManager = highScoreManager;
        initComponents();
        this.levelChoiceList.addListSelectionListener( new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if(!e.getValueIsAdjusting()) {
                    switch(levelChoiceList.getSelectedValue().toString()) {
                        case "Beginner":
                            System.out.println("beginner selected");
                            updateScoreList(MiinaEngine.Level.Beginner);
                            break;
                        case "Intermediate":
                            System.out.println("intermediate selected");
                            updateScoreList(MiinaEngine.Level.Intermediate);
                            break;
                        case "Advanced":
                            System.out.println("advanced selected");
                            updateScoreList(MiinaEngine.Level.Advanced);
                            break;
                    }
                }
            }
        });
    }

    private void updateScoreList(MiinaEngine.Level level) {
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
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        levelChoiceScrollPane = new javax.swing.JScrollPane();
        levelChoiceList = new javax.swing.JList();
        jScrollPane1 = new javax.swing.JScrollPane();
        textArea = new javax.swing.JTextArea();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setAlwaysOnTop(true);
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
