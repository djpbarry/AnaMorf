package AnaMorf;

import javax.swing.JDialog;

/**
 * A dialog displayed in response to a request from the user to exit the
 * application.
 *
 * @author   David J Barry <davejpbarry@gmail.com>
 * @version  01SEP2010
 */
public class ExitDialog extends javax.swing.JDialog {
    private static boolean exitProgram = false;

    /** Creates new form ExitDialog */
    public ExitDialog(JDialog parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     */

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        questionLabel = new javax.swing.JLabel();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Exit Program?");
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        questionLabel.setText("Do you really want to exit?");
        getContentPane().add(questionLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(65, 11, -1, -1));

        okButton.setText("OK");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });
        getContentPane().add(okButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(65, 52, -1, -1));

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });
        getContentPane().add(cancelButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(122, 52, -1, -1));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        exitProgram = false;
        dispose();
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        exitProgram = true;
        dispose();
    }//GEN-LAST:event_okButtonActionPerformed

    /**
     * Returns true if the OK button was clicked, false otherwise.
     */
    public boolean okButtonPressed(){
        return exitProgram;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JButton okButton;
    private javax.swing.JLabel questionLabel;
    // End of variables declaration//GEN-END:variables

}
