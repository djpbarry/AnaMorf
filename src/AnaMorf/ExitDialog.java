package AnaMorf;

import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.JDialog;

/**
 * A dialog displayed in response to a request from the user to exit the
 * application.
 *
 * @author David J Barry <davejpbarry@gmail.com>
 * @version 01SEP2010
 */
public class ExitDialog extends javax.swing.JDialog {

    private static boolean exitProgram = false;

    /**
     * Creates new form ExitDialog
     */
    public ExitDialog(JDialog parent, boolean modal) {
        super(parent, modal);
        initComponents();
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dim.width / 2 - this.getWidth() / 2, dim.height / 2 - this.getHeight() / 2);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        questionLabel = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Exit Program?");
        getContentPane().setLayout(new java.awt.GridLayout(2, 2));

        questionLabel.setText("Do you really want to exit?");
        jPanel1.add(questionLabel);

        getContentPane().add(jPanel1);

        okButton.setText("OK");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });
        jPanel2.add(okButton);

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });
        jPanel2.add(cancelButton);

        getContentPane().add(jPanel2);

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
    public boolean okButtonPressed() {
        return exitProgram;
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JButton okButton;
    private javax.swing.JLabel questionLabel;
    // End of variables declaration//GEN-END:variables
}
