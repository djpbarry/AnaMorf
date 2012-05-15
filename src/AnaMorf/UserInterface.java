package AnaMorf;


import ij.IJ;
import java.awt.Toolkit;
import java.text.DecimalFormat;

/**
 * An extension of a JFrame used to obtain input parameters from the user.
 * 
 * @author   David J Barry <davejpbarry@gmail.com>
 * @version  01SEP2010
 */
public class UserInterface extends javax.swing.JDialog {

    /** Creates new form UserInterface */
    public UserInterface(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        exit = true;
        initComponents();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     */

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        imageFormatLabel = new javax.swing.JLabel();
        okButton = new javax.swing.JButton();
        imageFormatCombo = new javax.swing.JComboBox();
        exitButton = new javax.swing.JButton();
        resLabel = new javax.swing.JLabel();
        areaCheck = new javax.swing.JCheckBox();
        circCheck = new javax.swing.JCheckBox();
        thlCheck = new javax.swing.JCheckBox();
        tipsCheck = new javax.swing.JCheckBox();
        hguCheck = new javax.swing.JCheckBox();
        fracCheck = new javax.swing.JCheckBox();
        lacCheck = new javax.swing.JCheckBox();
        minBranchLabel = new javax.swing.JLabel();
        maxCircLabel = new javax.swing.JLabel();
        minAreaLabel = new javax.swing.JLabel();
        lacTolLabel = new javax.swing.JLabel();
        minBranchField = new javax.swing.JTextField();
        maxCircField = new javax.swing.JTextField();
        minAreaField = new javax.swing.JTextField();
        lacTolField = new javax.swing.JTextField();
        maskImageCheck = new javax.swing.JCheckBox();
        subBackgroundCheck = new javax.swing.JCheckBox();
        ciLabel = new javax.swing.JLabel();
        confField = new javax.swing.JTextField();
        thresholdLabel = new javax.swing.JLabel();
        manualThresholdRadio = new javax.swing.JRadioButton();
        autoThresholdRadio = new javax.swing.JRadioButton();
        manualThresholdField = new javax.swing.JTextField();
        lightBGRadio = new javax.swing.JRadioButton();
        darkBGRadio = new javax.swing.JRadioButton();
        imageResField = new javax.swing.JTextField();
        branchCheck = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("AnaMorf");
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        imageFormatLabel.setText("Image Format:");
        jPanel1.add(imageFormatLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 16, -1, -1));

        okButton.setText("OK");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });
        jPanel1.add(okButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(143, 450, -1, -1));

        imageFormatCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "BMP", "GIF", "JPG", "PNG", "TIF" }));
        imageFormatCombo.setSelectedIndex(formatIndex);
        imageFormatCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                imageFormatComboActionPerformed(evt);
            }
        });
        jPanel1.add(imageFormatCombo, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 13, 72, -1));

        exitButton.setText("EXIT");
        exitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitButtonActionPerformed(evt);
            }
        });
        jPanel1.add(exitButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(196, 450, -1, -1));

        resLabel.setText("Image Resolution ("+IJ.micronSymbol+"m/pixel):");
        jPanel1.add(resLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 40, -1, -1));

        areaCheck.setSelected(area);
        areaCheck.setText("Projected Area");
        jPanel1.add(areaCheck, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 295, -1, -1));

        circCheck.setSelected(circ);
        circCheck.setText("Circularity");
        circCheck.setEnabled(false);
        jPanel1.add(circCheck, new org.netbeans.lib.awtextra.AbsoluteConstraints(143, 295, -1, -1));

        thlCheck.setSelected(thl);
        thlCheck.setText("Total Length");
        jPanel1.add(thlCheck, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 318, -1, -1));

        tipsCheck.setSelected(tips);
        tipsCheck.setText("Number of Endpoints");
        jPanel1.add(tipsCheck, new org.netbeans.lib.awtextra.AbsoluteConstraints(143, 318, -1, -1));

        hguCheck.setSelected(hgu);
        hguCheck.setText("Hyphal Growth Unit");
        hguCheck.setEnabled(false);
        jPanel1.add(hguCheck, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 341, -1, -1));

        fracCheck.setSelected(frac);
        fracCheck.setText("Fractal Dimension");
        fracCheck.setEnabled(false);
        jPanel1.add(fracCheck, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 364, -1, -1));

        lacCheck.setSelected(lac);
        lacCheck.setText("Lacunarity");
        lacCheck.setEnabled(false);
        jPanel1.add(lacCheck, new org.netbeans.lib.awtextra.AbsoluteConstraints(143, 341, -1, -1));

        minBranchLabel.setText("Minimum Branch Length ("+IJ.micronSymbol+"m):");
        jPanel1.add(minBranchLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 66, -1, -1));

        maxCircLabel.setText("Maximum Circularity:");
        jPanel1.add(maxCircLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 92, -1, -1));

        minAreaLabel.setText("<html>Minimum Area ("+IJ.micronSymbol+"m<sup>2</sup>):</html>");
        jPanel1.add(minAreaLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 118, -1, -1));

        lacTolLabel.setText("Lacunarity Tolerance:");
        jPanel1.add(lacTolLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 144, -1, -1));

        minBranchField.setText(onePlace.format(minLength));
        jPanel1.add(minBranchField, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 63, -1, -1));

        maxCircField.setText(threePlaces.format(maxCirc));
        jPanel1.add(maxCircField, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 89, -1, -1));

        minAreaField.setText(onePlace.format(minArea));
        jPanel1.add(minAreaField, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 115, -1, -1));

        lacTolField.setText(threePlaces.format(lacTol));
        lacTolField.setEnabled(false);
        jPanel1.add(lacTolField, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 141, -1, -1));

        maskImageCheck.setSelected(createMasks);
        maskImageCheck.setText("Create Mask Images?");
        jPanel1.add(maskImageCheck, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 267, -1, -1));

        subBackgroundCheck.setSelected(subBackground);
        subBackgroundCheck.setText("Subtract Background?");
        jPanel1.add(subBackgroundCheck, new org.netbeans.lib.awtextra.AbsoluteConstraints(143, 267, -1, -1));

        ciLabel.setText("Confidence Level (%):");
        jPanel1.add(ciLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 170, -1, -1));

        confField.setText(onePlace.format(confLevel));
        jPanel1.add(confField, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 167, -1, -1));

        thresholdLabel.setText("Grey Level Threshold:");
        jPanel1.add(thresholdLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 196, -1, -1));

        manualThresholdRadio.setSelected(!autoThreshold);
        manualThresholdRadio.setText("Manual:");
        manualThresholdRadio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                manualThresholdRadioActionPerformed(evt);
            }
        });
        jPanel1.add(manualThresholdRadio, new org.netbeans.lib.awtextra.AbsoluteConstraints(117, 192, -1, -1));

        autoThresholdRadio.setSelected(autoThreshold);
        autoThresholdRadio.setText("Auto");
        autoThresholdRadio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                autoThresholdRadioActionPerformed(evt);
            }
        });
        jPanel1.add(autoThresholdRadio, new org.netbeans.lib.awtextra.AbsoluteConstraints(117, 215, -1, -1));

        manualThresholdField.setText(""+manualThreshold);
        manualThresholdField.setEnabled(manualThresholdRadio.isSelected());
        jPanel1.add(manualThresholdField, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 193, -1, -1));

        lightBGRadio.setSelected(lightBackground);
        lightBGRadio.setText("Light Background");
        lightBGRadio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lightBGRadioActionPerformed(evt);
            }
        });
        jPanel1.add(lightBGRadio, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 241, -1, -1));

        darkBGRadio.setSelected(!lightBackground);
        darkBGRadio.setText("Dark Background");
        darkBGRadio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                darkBGRadioActionPerformed(evt);
            }
        });
        jPanel1.add(darkBGRadio, new org.netbeans.lib.awtextra.AbsoluteConstraints(143, 241, -1, -1));

        imageResField.setText(threePlaces.format(imageRes));
        jPanel1.add(imageResField, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 37, -1, -1));

        branchCheck.setSelected(branches);
        branchCheck.setText("Number of Branchpoints");
        jPanel1.add(branchCheck, new org.netbeans.lib.awtextra.AbsoluteConstraints(143, 364, -1, -1));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void exitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitButtonActionPerformed
        Toolkit.getDefaultToolkit().beep();
        ExitDialog exitDialog = new ExitDialog(this, true);
        exitDialog.setVisible(true);
        if(exitDialog.okButtonPressed()){
            exit = true;
            setVisible(false);
        }
}//GEN-LAST:event_exitButtonActionPerformed

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        exit = false;

        lac = lacCheck.isSelected();
        thl = thlCheck.isSelected();
        createMasks = maskImageCheck.isSelected();
        tips = tipsCheck.isSelected();
        hgu = hguCheck.isSelected();
        subBackground = subBackgroundCheck.isSelected();
        circ = circCheck.isSelected();
        area = areaCheck.isSelected();
        frac = fracCheck.isSelected();
        lightBackground = lightBGRadio.isSelected();
        branches = branchCheck.isSelected();

        int k = imageFormatCombo.getSelectedIndex();
        if(k >= 0) formatIndex = k;

        String branchText = minBranchField.getText();
        String resText = imageResField.getText();
        String circText = maxCircField.getText();
        String areaText = minAreaField.getText();
        String lacText = lacTolField.getText();
        String confText = confField.getText();
        String thresholdText = manualThresholdField.getText();
        try{
            imageRes = (resText != null) ? Double.parseDouble(resText) : imageRes;
            minLength = (branchText != null) ? Double.parseDouble(branchText) : 0.0;
            maxCirc = (circText != null) ? Double.parseDouble(circText) : 0.0;
            minArea = (areaText != null) ? Double.parseDouble(areaText) : 0.0;
            lacTol = (lacText != null) ? Double.parseDouble(lacText) : 0.0;
            confLevel = (confText != null) ? Double.parseDouble(confText) : 0.0;
            if(!autoThreshold)
                manualThreshold = (thresholdText != null) ? Integer.parseInt(thresholdText) : 0;
            setVisible(false);
        } catch(NumberFormatException e) {
            Toolkit.getDefaultToolkit().beep();
            IJ.error("All text fields must be numeric.");
        }
    }//GEN-LAST:event_okButtonActionPerformed

    private void autoThresholdRadioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_autoThresholdRadioActionPerformed
        manualThresholdRadio.setSelected(!autoThresholdRadio.isSelected());
        manualThresholdField.setEnabled(manualThresholdRadio.isSelected());
    }//GEN-LAST:event_autoThresholdRadioActionPerformed

    private void manualThresholdRadioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_manualThresholdRadioActionPerformed
        autoThresholdRadio.setSelected(!manualThresholdRadio.isSelected());
        manualThresholdField.setEnabled(manualThresholdRadio.isSelected());
    }//GEN-LAST:event_manualThresholdRadioActionPerformed

    private void lightBGRadioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lightBGRadioActionPerformed
        darkBGRadio.setSelected(!lightBGRadio.isSelected());
    }//GEN-LAST:event_lightBGRadioActionPerformed

    private void darkBGRadioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_darkBGRadioActionPerformed
        lightBGRadio.setSelected(!darkBGRadio.isSelected());
    }//GEN-LAST:event_darkBGRadioActionPerformed

    private void imageFormatComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_imageFormatComboActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_imageFormatComboActionPerformed

    /**
     * Returns true if the user has selected the 'Area' check box, false otherwise.
     */
    public boolean isArea(){
        return area;
    }

    /**
     * Returns true if the user has selected the 'Circularity' check box, false
     * otherwise.
     */
    public boolean isCirc(){
        return circ;
    }

    /**
     * Returns true if the user has selected the 'Hyphal Growth Unit' check box,
     * false otherwise.
     */
    public boolean isHGU(){
        return hgu;
    }

    /**
     * Returns true if the user has selected the 'Total Hyphal Length' check box,
     * false otherwise.
     */
    public boolean isTHL(){
        return thl;
    }

    /**
     * Returns true if the user has selected the 'Number of Tips' check box,
     * false otherwise.
     */
    public boolean isTips(){
        return tips;
    }

    /**
     * Returns true if the user has selected the 'Fractal Dimension' check box,
     * false otherwise.
     */
    public boolean isFrac(){
        return frac;
    }

    /**
     * Returns true if the user has selected the 'Lacunarity' check box, false
     * otherwise.
     */
    public boolean isLac(){
        return lac;
    }

    /**
     * Returns true if the user has selected the 'Create Mask Images?' check box,
     * false otherwise.
     */
    public boolean isCreateMasks(){
        return createMasks;
    }

    /**
     * Returns true if the user has selected the 'Subtract Background?' check box,
     * false otherwise.
     */
    public boolean isSubBackground(){
        return subBackground;
    }

    /**
     * Returns true if the user has selected the 'Light Background' radio button,
     * false otherwise
     */
    public boolean isLightBackground(){
        return lightBackground;
    }

    public boolean isBranches() {
        return branches;
    }

    /**
     * Returns the image resolution specified by the user.
     */
    public double getRes(){
        return imageRes;
    }

    /**
     * Returns the image format (file extension) specified by the user.
     */
    public String getImageFormat(){
        return (String)imageFormatCombo.getItemAt(formatIndex);
    }

    /**
     * Returns the minimum projected area specified by the user.
     */
    public double getMinArea(){
        return minArea;
    }

    /**
     * Returns the maximum circularity specified by the user.
     */
    public double getMaxCirc(){
        return maxCirc;
    }

    /**
     * Returns the minimum branch length specified by the user.
     */
    public double getMinLength(){
        return minLength;
    }

    /**
     * Returns the lacunarity tolerance specified by the user.
     */
    public double getLacTol(){
        return lacTol;
    }

    /**
     * Returns the confidence level specified by the user.
     */
    public double getConfLevel(){
        return confLevel;
    }

    /**
     * Returns the manual grey-level threshold specified by the user if the 'Manual'
     * radio button was selected or -1 otherwise.
     */
    public int getManualThreshold(){
        if(autoThreshold) return -1;
        else return manualThreshold;
    }

    /**
     * Returns true if the user exited the application.
     */
    public boolean exitProgram(){
        return exit;
    }

    private static int formatIndex = 4, manualThreshold = 235;
    private static double minLength = 40.0, maxCirc = 0.05, minArea = 1000.0,
            lacTol = 100, confLevel = 95.0, imageRes = 1.12347;
    private static boolean createMasks = true, subBackground = true, area = true,
            circ = false, thl = true, tips = true, hgu = false, frac = false,
            lac = false, exit, autoThreshold = true, lightBackground = false,
            branches = true;
    private DecimalFormat threePlaces = new DecimalFormat("0.000");
    private DecimalFormat onePlace = new DecimalFormat("0.0");

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox areaCheck;
    private javax.swing.JRadioButton autoThresholdRadio;
    private javax.swing.JCheckBox branchCheck;
    private javax.swing.JLabel ciLabel;
    private javax.swing.JCheckBox circCheck;
    private javax.swing.JTextField confField;
    private javax.swing.JRadioButton darkBGRadio;
    private javax.swing.JButton exitButton;
    private javax.swing.JCheckBox fracCheck;
    private javax.swing.JCheckBox hguCheck;
    private javax.swing.JComboBox imageFormatCombo;
    private javax.swing.JLabel imageFormatLabel;
    private javax.swing.JTextField imageResField;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JCheckBox lacCheck;
    private javax.swing.JTextField lacTolField;
    private javax.swing.JLabel lacTolLabel;
    private javax.swing.JRadioButton lightBGRadio;
    private javax.swing.JTextField manualThresholdField;
    private javax.swing.JRadioButton manualThresholdRadio;
    private javax.swing.JCheckBox maskImageCheck;
    private javax.swing.JTextField maxCircField;
    private javax.swing.JLabel maxCircLabel;
    private javax.swing.JTextField minAreaField;
    private javax.swing.JLabel minAreaLabel;
    private javax.swing.JTextField minBranchField;
    private javax.swing.JLabel minBranchLabel;
    private javax.swing.JButton okButton;
    private javax.swing.JLabel resLabel;
    private javax.swing.JCheckBox subBackgroundCheck;
    private javax.swing.JCheckBox thlCheck;
    private javax.swing.JLabel thresholdLabel;
    private javax.swing.JCheckBox tipsCheck;
    // End of variables declaration//GEN-END:variables

}
