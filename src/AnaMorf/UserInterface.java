package AnaMorf;

import ij.IJ;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.text.DecimalFormat;

/**
 * An extension of a JFrame used to obtain input parameters from the user.
 *
 * @author David J Barry <davejpbarry@gmail.com>
 * @version 01SEP2010
 */
public class UserInterface extends javax.swing.JDialog {

    /**
     * Creates new form UserInterface
     */
    public UserInterface(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        exit = true;
        initComponents();
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dim.width / 2 - this.getWidth() / 2, dim.height / 2 - this.getHeight() / 2);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jPanel10 = new javax.swing.JPanel();
        imageFormatLabel = new javax.swing.JLabel();
        resLabel = new javax.swing.JLabel();
        minBranchLabel = new javax.swing.JLabel();
        maxCircLabel = new javax.swing.JLabel();
        minAreaLabel = new javax.swing.JLabel();
        jPanel11 = new javax.swing.JPanel();
        imageFormatCombo = new javax.swing.JComboBox();
        imageResField = new javax.swing.JTextField();
        minBranchField = new javax.swing.JTextField();
        maxCircField = new javax.swing.JTextField();
        minAreaField = new javax.swing.JTextField();
        jPanel8 = new javax.swing.JPanel();
        jPanel12 = new javax.swing.JPanel();
        areaCheck = new javax.swing.JCheckBox();
        thlCheck = new javax.swing.JCheckBox();
        hguCheck = new javax.swing.JCheckBox();
        fracCheck = new javax.swing.JCheckBox();
        jPanel13 = new javax.swing.JPanel();
        circCheck = new javax.swing.JCheckBox();
        tipsCheck = new javax.swing.JCheckBox();
        lacCheck = new javax.swing.JCheckBox();
        branchCheck = new javax.swing.JCheckBox();
        jPanel6 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        lightBGRadio = new javax.swing.JRadioButton();
        darkBGRadio = new javax.swing.JRadioButton();
        jPanel15 = new javax.swing.JPanel();
        subBackgroundCheck = new javax.swing.JCheckBox();
        maskImageCheck = new javax.swing.JCheckBox();
        doMorphFilterCheck = new javax.swing.JCheckBox();
        jPanel5 = new javax.swing.JPanel();
        jPanel16 = new javax.swing.JPanel();
        autoThresholdRadio = new javax.swing.JRadioButton();
        manualThresholdRadio = new javax.swing.JRadioButton();
        jPanel17 = new javax.swing.JPanel();
        thresholdLabel = new javax.swing.JLabel();
        manualThresholdField = new javax.swing.JTextField();
        filler4 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 32767));
        jPanel14 = new javax.swing.JPanel();
        jPanel18 = new javax.swing.JPanel();
        lacTolLabel = new javax.swing.JLabel();
        jPanel19 = new javax.swing.JPanel();
        lacTolField = new javax.swing.JTextField();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 32767));
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 32767));
        jPanel7 = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        okButton = new javax.swing.JButton();
        exitButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("AnaMorf");
        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.PAGE_AXIS));

        jTabbedPane1.setPreferredSize(new java.awt.Dimension(400, 300));

        jPanel2.setLayout(new javax.swing.BoxLayout(jPanel2, javax.swing.BoxLayout.PAGE_AXIS));

        jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel4.setMinimumSize(new java.awt.Dimension(260, 160));
        jPanel4.setPreferredSize(new java.awt.Dimension(216, 180));
        jPanel4.setLayout(new java.awt.GridLayout(1, 0));

        jPanel10.setLayout(new java.awt.GridLayout(5, 0));

        imageFormatLabel.setText("Image Format:");
        jPanel10.add(imageFormatLabel);

        resLabel.setText("Image Resolution ("+IJ.micronSymbol+"m/pixel):");
        jPanel10.add(resLabel);

        minBranchLabel.setText("Minimum Branch Length ("+IJ.micronSymbol+"m):");
        jPanel10.add(minBranchLabel);

        maxCircLabel.setText("Maximum Circularity:");
        jPanel10.add(maxCircLabel);

        minAreaLabel.setText("Minimum Area ("+IJ.micronSymbol+"m^2):");
        jPanel10.add(minAreaLabel);

        jPanel4.add(jPanel10);

        jPanel11.setLayout(new java.awt.GridLayout(5, 0));

        imageFormatCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "BMP", "GIF", "JPG", "PNG", "TIF" }));
        imageFormatCombo.setSelectedIndex(formatIndex);
        imageFormatCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                imageFormatComboActionPerformed(evt);
            }
        });
        jPanel11.add(imageFormatCombo);

        imageResField.setText(threePlaces.format(imageRes));
        jPanel11.add(imageResField);

        minBranchField.setText(onePlace.format(minLength));
        jPanel11.add(minBranchField);

        maxCircField.setText(threePlaces.format(maxCirc));
        jPanel11.add(maxCircField);

        minAreaField.setText(onePlace.format(minArea));
        jPanel11.add(minAreaField);

        jPanel4.add(jPanel11);

        jPanel2.add(jPanel4);

        jPanel8.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel8.setLayout(new java.awt.GridLayout(1, 0));

        jPanel12.setLayout(new java.awt.GridLayout(4, 0));

        areaCheck.setSelected(area);
        areaCheck.setText("Projected Area");
        jPanel12.add(areaCheck);

        thlCheck.setSelected(thl);
        thlCheck.setText("Total Length");
        jPanel12.add(thlCheck);

        hguCheck.setSelected(hgu);
        hguCheck.setText("Hyphal Growth Unit");
        jPanel12.add(hguCheck);

        fracCheck.setSelected(frac);
        fracCheck.setText("Fractal Dimension");
        jPanel12.add(fracCheck);

        jPanel8.add(jPanel12);

        jPanel13.setLayout(new java.awt.GridLayout(4, 0));

        circCheck.setSelected(circ);
        circCheck.setText("Circularity");
        jPanel13.add(circCheck);

        tipsCheck.setSelected(tips);
        tipsCheck.setText("Number of Endpoints");
        jPanel13.add(tipsCheck);

        lacCheck.setSelected(lac);
        lacCheck.setText("Lacunarity");
        jPanel13.add(lacCheck);

        branchCheck.setSelected(branches);
        branchCheck.setText("Number of Branchpoints");
        jPanel13.add(branchCheck);

        jPanel8.add(jPanel13);

        jPanel2.add(jPanel8);

        jTabbedPane1.addTab("Basic", jPanel2);

        jPanel6.setLayout(new javax.swing.BoxLayout(jPanel6, javax.swing.BoxLayout.PAGE_AXIS));

        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel3.setLayout(new java.awt.GridLayout(2, 2));

        jPanel1.setLayout(new java.awt.GridLayout(1, 0));

        lightBGRadio.setSelected(lightBackground);
        lightBGRadio.setText("Light Background");
        lightBGRadio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lightBGRadioActionPerformed(evt);
            }
        });
        jPanel1.add(lightBGRadio);

        darkBGRadio.setSelected(!lightBackground);
        darkBGRadio.setText("Dark Background");
        darkBGRadio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                darkBGRadioActionPerformed(evt);
            }
        });
        jPanel1.add(darkBGRadio);

        jPanel3.add(jPanel1);

        jPanel15.setLayout(new java.awt.GridLayout(2, 2));

        subBackgroundCheck.setSelected(subBackground);
        subBackgroundCheck.setText("Subtract Background?");
        jPanel15.add(subBackgroundCheck);

        maskImageCheck.setSelected(createMasks);
        maskImageCheck.setText("Create Mask Images?");
        jPanel15.add(maskImageCheck);

        doMorphFilterCheck.setSelected(doMorphFiltering);
        doMorphFilterCheck.setText("Perform Morphological Filtering?");
        jPanel15.add(doMorphFilterCheck);

        jPanel3.add(jPanel15);

        jPanel6.add(jPanel3);

        jPanel5.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel5.setLayout(new java.awt.GridLayout(3, 0));

        jPanel16.setLayout(new java.awt.GridLayout(1, 0));

        autoThresholdRadio.setSelected(autoThreshold);
        autoThresholdRadio.setText("Auto Threshold");
        autoThresholdRadio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                autoThresholdRadioActionPerformed(evt);
            }
        });
        jPanel16.add(autoThresholdRadio);

        manualThresholdRadio.setSelected(!autoThreshold);
        manualThresholdRadio.setText("Manual Threshold");
        manualThresholdRadio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                manualThresholdRadioActionPerformed(evt);
            }
        });
        jPanel16.add(manualThresholdRadio);

        jPanel5.add(jPanel16);

        jPanel17.setLayout(new java.awt.GridLayout(1, 0));

        thresholdLabel.setText("Manual Grey Level Threshold:");
        jPanel17.add(thresholdLabel);

        manualThresholdField.setText(""+manualThreshold);
        manualThresholdField.setEnabled(manualThresholdRadio.isSelected());
        jPanel17.add(manualThresholdField);

        jPanel5.add(jPanel17);
        jPanel5.add(filler4);

        jPanel6.add(jPanel5);

        jPanel14.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel14.setLayout(new java.awt.GridLayout(2, 0));

        jPanel18.setLayout(new java.awt.GridLayout(1, 0));

        lacTolLabel.setText("Lacunarity Tolerance:");
        jPanel18.add(lacTolLabel);

        jPanel14.add(jPanel18);

        jPanel19.setLayout(new java.awt.GridLayout(1, 0));

        lacTolField.setText(threePlaces.format(lacTol));
        jPanel19.add(lacTolField);

        jPanel14.add(jPanel19);
        jPanel14.add(filler2);
        jPanel14.add(filler3);

        jPanel6.add(jPanel14);

        jTabbedPane1.addTab("Advanced", jPanel6);

        getContentPane().add(jTabbedPane1);

        jPanel7.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        okButton.setText("OK");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });
        jPanel9.add(okButton);

        exitButton.setText("EXIT");
        exitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitButtonActionPerformed(evt);
            }
        });
        jPanel9.add(exitButton);

        jPanel7.add(jPanel9);

        getContentPane().add(jPanel7);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void exitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitButtonActionPerformed
        Toolkit.getDefaultToolkit().beep();
        ExitDialog exitDialog = new ExitDialog(this, true);
        exitDialog.setVisible(true);
        if (exitDialog.okButtonPressed()) {
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
        doMorphFiltering = doMorphFilterCheck.isSelected();

        int k = imageFormatCombo.getSelectedIndex();
        if (k >= 0) {
            formatIndex = k;
        }

        String branchText = minBranchField.getText();
        String resText = imageResField.getText();
        String circText = maxCircField.getText();
        String areaText = minAreaField.getText();
        String lacText = lacTolField.getText();
        String thresholdText = manualThresholdField.getText();
        try {
            imageRes = (resText != null) ? Double.parseDouble(resText) : imageRes;
            minLength = (branchText != null) ? Double.parseDouble(branchText) : 0.0;
            maxCirc = (circText != null) ? Double.parseDouble(circText) : 0.0;
            minArea = (areaText != null) ? Double.parseDouble(areaText) : 0.0;
            lacTol = (lacText != null) ? Double.parseDouble(lacText) : 0.0;
            if (!autoThreshold) {
                manualThreshold = (thresholdText != null) ? Integer.parseInt(thresholdText) : 0;
            }
            setVisible(false);
        } catch (NumberFormatException e) {
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
    }//GEN-LAST:event_imageFormatComboActionPerformed

    public boolean[] getOptions() {
        boolean options[] = {area, circ, hgu, thl, tips, branches, frac, lac};
        return options;
    }

    /**
     * Returns true if the user has selected the 'Area' check box, false
     * otherwise.
     */
    public boolean isArea() {
        return area;
    }

    /**
     * Returns true if the user has selected the 'Circularity' check box, false
     * otherwise.
     */
    public boolean isCirc() {
        return circ;
    }

    /**
     * Returns true if the user has selected the 'Hyphal Growth Unit' check box,
     * false otherwise.
     */
    public boolean isHGU() {
        return hgu;
    }

    /**
     * Returns true if the user has selected the 'Total Hyphal Length' check
     * box, false otherwise.
     */
    public boolean isTHL() {
        return thl;
    }

    /**
     * Returns true if the user has selected the 'Number of Tips' check box,
     * false otherwise.
     */
    public boolean isTips() {
        return tips;
    }

    /**
     * Returns true if the user has selected the 'Fractal Dimension' check box,
     * false otherwise.
     */
    public boolean isFrac() {
        return frac;
    }

    /**
     * Returns true if the user has selected the 'Lacunarity' check box, false
     * otherwise.
     */
    public boolean isLac() {
        return lac;
    }

    /**
     * Returns true if the user has selected the 'Create Mask Images?' check
     * box, false otherwise.
     */
    public boolean isCreateMasks() {
        return createMasks;
    }

    /**
     * Returns true if the user has selected the 'Subtract Background?' check
     * box, false otherwise.
     */
    public boolean isSubBackground() {
        return subBackground;
    }

    /**
     * Returns true if the user has selected the 'Light Background' radio
     * button, false otherwise
     */
    public boolean isLightBackground() {
        return lightBackground;
    }

    public boolean isBranches() {
        return branches;
    }

    /**
     * Returns the image resolution specified by the user.
     */
    public double getRes() {
        return imageRes;
    }

    /**
     * Returns the image format (file extension) specified by the user.
     */
    public String getImageFormat() {
        return (String) imageFormatCombo.getItemAt(formatIndex);
    }

    /**
     * Returns the minimum projected area specified by the user.
     */
    public double getMinArea() {
        return minArea;
    }

    /**
     * Returns the maximum circularity specified by the user.
     */
    public double getMaxCirc() {
        return maxCirc;
    }

    /**
     * Returns the minimum branch length specified by the user.
     */
    public double getMinLength() {
        return minLength;
    }

    /**
     * Returns the lacunarity tolerance specified by the user.
     */
    public double getLacTol() {
        return lacTol;
    }

    /**
     * Returns the manual grey-level threshold specified by the user if the
     * 'Manual' radio button was selected or -1 otherwise.
     */
    public int getManualThreshold() {
        if (autoThreshold) {
            return -1;
        } else {
            return manualThreshold;
        }
    }

    public boolean isDoMorphFiltering() {
        return doMorphFiltering;
    }

    /**
     * Returns true if the user exited the application.
     */
    public boolean exitProgram() {
        return exit;
    }
    private static int formatIndex = 3, manualThreshold = 100;
    private static double minLength = 20.0, maxCirc = 0.05, minArea = 1000.0,
            lacTol = 0.1, imageRes = 1.0;
    private static boolean createMasks = true, subBackground = true, area = true,
            circ = true, thl = true, tips = true, hgu = true, frac = true,
            lac = true, exit, autoThreshold = true, lightBackground = true,
            branches = true, doMorphFiltering = true;
    private DecimalFormat threePlaces = new DecimalFormat("0.000");
    private DecimalFormat onePlace = new DecimalFormat("0.0");
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox areaCheck;
    private javax.swing.JRadioButton autoThresholdRadio;
    private javax.swing.JCheckBox branchCheck;
    private javax.swing.JCheckBox circCheck;
    private javax.swing.JRadioButton darkBGRadio;
    private javax.swing.JCheckBox doMorphFilterCheck;
    private javax.swing.JButton exitButton;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private javax.swing.Box.Filler filler4;
    private javax.swing.JCheckBox fracCheck;
    private javax.swing.JCheckBox hguCheck;
    private javax.swing.JComboBox imageFormatCombo;
    private javax.swing.JLabel imageFormatLabel;
    private javax.swing.JTextField imageResField;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel19;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JTabbedPane jTabbedPane1;
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
