/* 
 * Copyright (C) 2015 David Barry <david.barry at cancer.org.uk>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package AnaMorf;

import ij.IJ;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.text.DecimalFormat;

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
        java.awt.GridBagConstraints gridBagConstraints;

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
        fourierFracCheck = new javax.swing.JCheckBox();
        boxFracCheck = new javax.swing.JCheckBox();
        circCheck = new javax.swing.JCheckBox();
        lacCheck = new javax.swing.JCheckBox();
        tipsCheck = new javax.swing.JCheckBox();
        branchCheck = new javax.swing.JCheckBox();
        jPanel6 = new javax.swing.JPanel();
        jPanel13 = new javax.swing.JPanel();
        thresholdLabel1 = new javax.swing.JLabel();
        manualThresholdField1 = new javax.swing.JTextField();
        lightBGRadio = new javax.swing.JRadioButton();
        darkBGRadio = new javax.swing.JRadioButton();
        autoThresholdRadio = new javax.swing.JRadioButton();
        manualThresholdRadio = new javax.swing.JRadioButton();
        thresholdLabel = new javax.swing.JLabel();
        manualThresholdField = new javax.swing.JTextField();
        subBackgroundCheck = new javax.swing.JCheckBox();
        jLabel1 = new javax.swing.JLabel();
        backgroundTextField = new javax.swing.JTextField();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();
        jSeparator3 = new javax.swing.JSeparator();
        maskImageCheck = new javax.swing.JCheckBox();
        watershedCheckBox = new javax.swing.JCheckBox();
        edgeCheckBox = new javax.swing.JCheckBox();
        wholeImageCheckBox = new javax.swing.JCheckBox();
        doMorphFilterCheck = new javax.swing.JCheckBox();
        jPanel7 = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        okButton = new javax.swing.JButton();
        exitButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("AnaMorf");
        setPreferredSize(new java.awt.Dimension(640, 480));
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

        jPanel12.setLayout(new java.awt.GridBagLayout());

        areaCheck.setSelected(area);
        areaCheck.setText("Projected Area");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 0.3;
        gridBagConstraints.weighty = 0.3;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        jPanel12.add(areaCheck, gridBagConstraints);

        thlCheck.setSelected(thl);
        thlCheck.setText("Total Length");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 0.3;
        gridBagConstraints.weighty = 0.3;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        jPanel12.add(thlCheck, gridBagConstraints);

        hguCheck.setSelected(hgu);
        hguCheck.setText("Hyphal Growth Unit");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 0.3;
        gridBagConstraints.weighty = 0.3;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        jPanel12.add(hguCheck, gridBagConstraints);

        fourierFracCheck.setSelected(fourfrac);
        fourierFracCheck.setText("Fourier Fractal Dimension");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 0.3;
        gridBagConstraints.weighty = 0.3;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        jPanel12.add(fourierFracCheck, gridBagConstraints);

        boxFracCheck.setSelected(boxfrac);
        boxFracCheck.setText("Box-Counting Fractal Dimension");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 0.3;
        gridBagConstraints.weighty = 0.3;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        jPanel12.add(boxFracCheck, gridBagConstraints);

        circCheck.setSelected(circ);
        circCheck.setText("Circularity");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 0.3;
        gridBagConstraints.weighty = 0.3;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        jPanel12.add(circCheck, gridBagConstraints);

        lacCheck.setSelected(lac);
        lacCheck.setText("Lacunarity");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 0.3;
        gridBagConstraints.weighty = 0.3;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        jPanel12.add(lacCheck, gridBagConstraints);

        tipsCheck.setSelected(tips);
        tipsCheck.setText("Number of Endpoints");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 0.3;
        gridBagConstraints.weighty = 0.3;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        jPanel12.add(tipsCheck, gridBagConstraints);

        branchCheck.setSelected(branches);
        branchCheck.setText("Number of Branchpoints");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 0.3;
        gridBagConstraints.weighty = 0.3;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        jPanel12.add(branchCheck, gridBagConstraints);

        jPanel8.add(jPanel12);

        jPanel2.add(jPanel8);

        jTabbedPane1.addTab("Basic", jPanel2);

        jPanel6.setLayout(new java.awt.GridBagLayout());

        jPanel13.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel13.setLayout(new java.awt.GridBagLayout());

        thresholdLabel1.setText("Filter Radius:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 0);
        jPanel13.add(thresholdLabel1, gridBagConstraints);

        manualThresholdField1.setText(String.valueOf(filterRadius));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 1.0;
        jPanel13.add(manualThresholdField1, gridBagConstraints);

        lightBGRadio.setSelected(lightBackground);
        lightBGRadio.setText("Light Background");
        lightBGRadio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lightBGRadioActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 1.0;
        jPanel13.add(lightBGRadio, gridBagConstraints);

        darkBGRadio.setSelected(!lightBackground);
        darkBGRadio.setText("Dark Background");
        darkBGRadio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                darkBGRadioActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 1.0;
        jPanel13.add(darkBGRadio, gridBagConstraints);

        autoThresholdRadio.setSelected(autoThreshold);
        autoThresholdRadio.setText("Auto Threshold");
        autoThresholdRadio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                autoThresholdRadioActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 1.0;
        jPanel13.add(autoThresholdRadio, gridBagConstraints);

        manualThresholdRadio.setSelected(!autoThreshold);
        manualThresholdRadio.setText("Manual Threshold");
        manualThresholdRadio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                manualThresholdRadioActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 1.0;
        jPanel13.add(manualThresholdRadio, gridBagConstraints);

        thresholdLabel.setText("Manual Grey Level Threshold:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 0);
        jPanel13.add(thresholdLabel, gridBagConstraints);

        manualThresholdField.setText(""+manualThreshold);
        manualThresholdField.setEnabled(manualThresholdRadio.isSelected());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 1.0;
        jPanel13.add(manualThresholdField, gridBagConstraints);

        subBackgroundCheck.setSelected(subBackground);
        subBackgroundCheck.setText("Remove Uneven Background?");
        subBackgroundCheck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                subBackgroundCheckActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 1.0;
        jPanel13.add(subBackgroundCheck, gridBagConstraints);

        jLabel1.setText("Maximum Object Size ("+IJ.micronSymbol+"m^2):");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 0);
        jPanel13.add(jLabel1, gridBagConstraints);

        backgroundTextField.setText(""+backgroundRadius);
        backgroundTextField.setEnabled(subBackgroundCheck.isSelected());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 1.0;
        jPanel13.add(backgroundTextField, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel13.add(jSeparator1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel13.add(jSeparator2, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel13.add(jSeparator3, gridBagConstraints);

        maskImageCheck.setSelected(createMasks);
        maskImageCheck.setText("Create Mask Images?");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 1.0;
        jPanel13.add(maskImageCheck, gridBagConstraints);

        watershedCheckBox.setSelected(doWatershed);
        watershedCheckBox.setText("Separate Touching Objects?");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 1.0;
        jPanel13.add(watershedCheckBox, gridBagConstraints);

        edgeCheckBox.setText("Exclude Edge Objects?");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 1.0;
        jPanel13.add(edgeCheckBox, gridBagConstraints);

        wholeImageCheckBox.setSelected(wholeImage);
        wholeImageCheckBox.setText("Analyse Whole Image?");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 1.0;
        jPanel13.add(wholeImageCheckBox, gridBagConstraints);

        doMorphFilterCheck.setSelected(doMorphFiltering);
        doMorphFilterCheck.setText("Attempt to Fix Breaks?");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 1.0;
        jPanel13.add(doMorphFilterCheck, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel6.add(jPanel13, gridBagConstraints);

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
        fourfrac = fourierFracCheck.isSelected();
        boxfrac = boxFracCheck.isSelected();
        lightBackground = lightBGRadio.isSelected();
        branches = branchCheck.isSelected();
        doMorphFiltering = doMorphFilterCheck.isSelected();
        doWatershed = watershedCheckBox.isSelected();
        wholeImage = wholeImageCheckBox.isSelected();
        excludeEdges = edgeCheckBox.isSelected();

        int k = imageFormatCombo.getSelectedIndex();
        if (k >= 0) {
            formatIndex = k;
        }

        String branchText = minBranchField.getText();
        String resText = imageResField.getText();
        String circText = maxCircField.getText();
        String areaText = minAreaField.getText();
        String thresholdText = manualThresholdField.getText();
        String backgroundText = backgroundTextField.getText();
        try {
            imageRes = (resText != null) ? Double.parseDouble(resText) : imageRes;
            minLength = (branchText != null) ? Double.parseDouble(branchText) : 0.0;
            maxCirc = (circText != null) ? Double.parseDouble(circText) : 0.0;
            minArea = (areaText != null) ? Double.parseDouble(areaText) : 0.0;
            backgroundRadius = (backgroundText != null) ? Double.parseDouble(backgroundText) : 0.0;
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

    private void subBackgroundCheckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_subBackgroundCheckActionPerformed
        backgroundTextField.setEnabled(subBackgroundCheck.isSelected());
    }//GEN-LAST:event_subBackgroundCheckActionPerformed

    public boolean[] getOptions() {
        boolean options[] = {area, circ, hgu, thl, tips, branches, fourfrac, boxfrac, lac};
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
        return fourfrac;
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

    public boolean isDoWatershed() {
        return doWatershed;
    }

    public double getBackgroundRadius() {
        return backgroundRadius;
    }

    public boolean isWholeImage() {
        return wholeImage;
    }

    public boolean isExcludeEdges() {
        return excludeEdges;
    }

    public double getFilterRadius() {
        return filterRadius;
    }

    @Override
    public String toString() {
        return "Image Format: " + (String) imageFormatCombo.getSelectedItem()
                + ", Image Resolution: " + imageRes
                + ", Minimum Branch Length: " + minLength
                + ", Maximum Circularity: " + maxCirc
                + ", Minimum Area: " + minArea
                + ", Measure Area: " + areaCheck.isSelected()
                + ", Measure Circularity: " + circCheck.isSelected()
                + ", Measure Total Length: " + thlCheck.isSelected()
                + ", Count End-points: " + tipsCheck.isSelected()
                + ", Measure Hyphal Growth Unit: " + hguCheck.isSelected()
                + ", Measure Lacunarity: " + lacCheck.isSelected()
                + ", Measure Fourier Dimensions: " + fourierFracCheck.isSelected()
                + ", Measure Box-Counting Fractal Dimensions: " + boxFracCheck.isSelected()
                + ", Count Branch-points: " + branchCheck.isSelected()
                + ", Light Background: " + lightBGRadio.isSelected()
                + ", Dark Background: " + darkBGRadio.isSelected()
                + ", Subtract Background: " + subBackgroundCheck.isSelected()
                + ", Background Radius: " + backgroundRadius
                + ", Create Mask Images: " + maskImageCheck.isSelected()
                + ", Do Morphogical Filtering: " + doMorphFilterCheck.isSelected()
                + ", Do Watershed Filtering: " + watershedCheckBox.isSelected()
                + ", Auto-Threshold: " + autoThresholdRadio.isSelected()
                + ", Use Manual Threshold: " + manualThresholdRadio.isSelected()
                + ", Manual Threshold Value: " + manualThresholdField.getText()
                + ", Analyse Whole Image: " + wholeImageCheckBox.isSelected()
                + ", Exclude Edge Objects: " + edgeCheckBox.isSelected();
    }

    /**
     * Returns true if the user exited the application.
     */
    public boolean exitProgram() {
        return exit;
    }
    private static int formatIndex = 4, manualThreshold = 100;
    private static double minLength = 2.5, maxCirc = 1.0, minArea = 20.0,
            imageRes = 0.267, backgroundRadius = 10.0, filterRadius = 0.267;
    private static boolean createMasks = true, subBackground = false, area = false,
            circ = false, thl = true, tips = true, hgu = false, fourfrac = false,
            boxfrac = true, lac = false, exit, autoThreshold = true, lightBackground = false,
            branches = false, doMorphFiltering = true, doWatershed = false,
            wholeImage = true, excludeEdges = false;
    private DecimalFormat threePlaces = new DecimalFormat("0.000");
    private DecimalFormat onePlace = new DecimalFormat("0.0");
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox areaCheck;
    private javax.swing.JRadioButton autoThresholdRadio;
    private javax.swing.JTextField backgroundTextField;
    private javax.swing.JCheckBox boxFracCheck;
    private javax.swing.JCheckBox branchCheck;
    private javax.swing.JCheckBox circCheck;
    private javax.swing.JRadioButton darkBGRadio;
    private javax.swing.JCheckBox doMorphFilterCheck;
    private javax.swing.JCheckBox edgeCheckBox;
    private javax.swing.JButton exitButton;
    private javax.swing.JCheckBox fourierFracCheck;
    private javax.swing.JCheckBox hguCheck;
    private javax.swing.JComboBox imageFormatCombo;
    private javax.swing.JLabel imageFormatLabel;
    private javax.swing.JTextField imageResField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JCheckBox lacCheck;
    private javax.swing.JRadioButton lightBGRadio;
    private javax.swing.JTextField manualThresholdField;
    private javax.swing.JTextField manualThresholdField1;
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
    private javax.swing.JLabel thresholdLabel1;
    private javax.swing.JCheckBox tipsCheck;
    private javax.swing.JCheckBox watershedCheckBox;
    private javax.swing.JCheckBox wholeImageCheckBox;
    // End of variables declaration//GEN-END:variables
}
