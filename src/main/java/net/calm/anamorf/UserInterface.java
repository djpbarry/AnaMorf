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
package net.calm.anamorf;

import ij.IJ;
import ij.process.AutoThresholder;
import ij.process.AutoThresholder.Method;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.text.DecimalFormat;
import java.util.Properties;
import javax.swing.DefaultComboBoxModel;

import net.calm.anamorf.params.DefaultParams;
import net.calm.iaclasslibrary.UIClasses.GUIMethods;
import net.calm.iaclasslibrary.UIClasses.PropertyExtractor;

public class UserInterface extends javax.swing.JDialog implements GUIMethods {

    /**
     * Creates new form UserInterface
     */
    public UserInterface(java.awt.Frame parent, boolean modal, String title, Properties props) {
        super(parent, modal);
        exit = true;
        this.title = title;
        if (props != null) {
            this.props = props;
        } else {
            this.props = new DefaultParams();
        }
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
        basicPanel = new javax.swing.JPanel();
        imageFormatLabel = new javax.swing.JLabel();
        resLabel = new javax.swing.JLabel();
        minBranchLabel = new javax.swing.JLabel();
        maxCircLabel = new javax.swing.JLabel();
        minAreaLabel = new javax.swing.JLabel();
        imageFormatCombo = new javax.swing.JComboBox();
        imageResField = new javax.swing.JTextField();
        minBranchField = new javax.swing.JTextField();
        maxCircField = new javax.swing.JTextField();
        minAreaField = new javax.swing.JTextField();
        areaCheck = new javax.swing.JCheckBox();
        thlCheck = new javax.swing.JCheckBox();
        hguCheck = new javax.swing.JCheckBox();
        fourierFracCheck = new javax.swing.JCheckBox();
        boxFracCheck = new javax.swing.JCheckBox();
        circCheck = new javax.swing.JCheckBox();
        lacCheck = new javax.swing.JCheckBox();
        tipsCheck = new javax.swing.JCheckBox();
        branchCheck = new javax.swing.JCheckBox();
        curveCheckBox = new javax.swing.JCheckBox();
        curveWindowLabel = new javax.swing.JLabel();
        curveWindowTextField = new javax.swing.JTextField();
        advancedPanel = new javax.swing.JPanel();
        blurRadiusLabel = new javax.swing.JLabel();
        blurRadiusField = new javax.swing.JTextField();
        lightBGRadio = new javax.swing.JRadioButton();
        darkBGRadio = new javax.swing.JRadioButton();
        autoThresholdRadio = new javax.swing.JRadioButton();
        manualThresholdRadio = new javax.swing.JRadioButton();
        thresholdLabel = new javax.swing.JLabel();
        manualThresholdField = new javax.swing.JTextField();
        subBackgroundCheck = new javax.swing.JCheckBox();
        backgroundLabel = new javax.swing.JLabel();
        backgroundTextField = new javax.swing.JTextField();
        maskImageCheck = new javax.swing.JCheckBox();
        watershedCheckBox = new javax.swing.JCheckBox();
        edgeCheckBox = new javax.swing.JCheckBox();
        wholeImageCheckBox = new javax.swing.JCheckBox();
        threshComboBox = new javax.swing.JComboBox();
        autoThresholdLabel = new javax.swing.JLabel();
        preProcessCheckBox = new javax.swing.JCheckBox();
        curveValsCheckBox = new javax.swing.JCheckBox();
        jPanel7 = new javax.swing.JPanel();
        okButton = new javax.swing.JButton();
        exitButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(title);
        setPreferredSize(new java.awt.Dimension(640, 640));
        getContentPane().setLayout(new java.awt.GridBagLayout());

        jTabbedPane1.setPreferredSize(new java.awt.Dimension(400, 300));

        basicPanel.setLayout(new java.awt.GridBagLayout());

        imageFormatLabel.setText(DefaultParams.IMAGE_FORMAT_LABEL);
        imageFormatLabel.setLabelFor(imageFormatCombo);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        basicPanel.add(imageFormatLabel, gridBagConstraints);

        resLabel.setText(DefaultParams.IMAGE_RES_LABEL);
        resLabel.setLabelFor(imageResField);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        basicPanel.add(resLabel, gridBagConstraints);

        minBranchLabel.setText(DefaultParams.MIN_BRANCH_LABEL);
        minBranchLabel.setLabelFor(minBranchField);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        basicPanel.add(minBranchLabel, gridBagConstraints);

        maxCircLabel.setText(DefaultParams.MAX_CIRC_LABEL);
        maxCircLabel.setLabelFor(maxCircField);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        basicPanel.add(maxCircLabel, gridBagConstraints);

        minAreaLabel.setText(DefaultParams.MIN_AREA_LABEL);
        minAreaLabel.setLabelFor(minAreaField);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        basicPanel.add(minAreaLabel, gridBagConstraints);

        imageFormatCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "BMP", "GIF", "JPG", "PNG", "TIF" }));
        imageFormatCombo.setSelectedItem(props.get(DefaultParams.IMAGE_FORMAT_LABEL));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        basicPanel.add(imageFormatCombo, gridBagConstraints);

        imageResField.setText(props.getProperty(DefaultParams.IMAGE_RES_LABEL));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        basicPanel.add(imageResField, gridBagConstraints);

        minBranchField.setText(props.getProperty(DefaultParams.MIN_BRANCH_LABEL));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        basicPanel.add(minBranchField, gridBagConstraints);

        maxCircField.setText(props.getProperty(DefaultParams.MAX_CIRC_LABEL));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        basicPanel.add(maxCircField, gridBagConstraints);

        minAreaField.setText(props.getProperty(DefaultParams.MIN_AREA_LABEL));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        basicPanel.add(minAreaField, gridBagConstraints);

        areaCheck.setSelected(Boolean.parseBoolean(props.getProperty(DefaultParams.PROJ_AREA_LABEL)));
        areaCheck.setText(DefaultParams.PROJ_AREA_LABEL);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        basicPanel.add(areaCheck, gridBagConstraints);

        thlCheck.setSelected(Boolean.parseBoolean(props.getProperty(DefaultParams.TOT_LENGTH_LABEL)));
        thlCheck.setText(DefaultParams.TOT_LENGTH_LABEL);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        basicPanel.add(thlCheck, gridBagConstraints);

        hguCheck.setSelected(Boolean.parseBoolean(props.getProperty(DefaultParams.FOURIER_FRAC_LABEL)));
        hguCheck.setText(DefaultParams.MEAN_BRANCH_LABEL);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        basicPanel.add(hguCheck, gridBagConstraints);

        fourierFracCheck.setSelected(Boolean.parseBoolean(props.getProperty(DefaultParams.FOURIER_FRAC_LABEL)));
        fourierFracCheck.setText(DefaultParams.FOURIER_FRAC_LABEL);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        basicPanel.add(fourierFracCheck, gridBagConstraints);

        boxFracCheck.setSelected(Boolean.parseBoolean(props.getProperty(DefaultParams.BOX_COUNT_LABEL)));
        boxFracCheck.setText(DefaultParams.BOX_COUNT_LABEL);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        basicPanel.add(boxFracCheck, gridBagConstraints);

        circCheck.setSelected(Boolean.parseBoolean(props.getProperty(DefaultParams.CIRC_LABEL)));
        circCheck.setText(DefaultParams.CIRC_LABEL);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        basicPanel.add(circCheck, gridBagConstraints);

        lacCheck.setSelected(Boolean.parseBoolean(props.getProperty(DefaultParams.LAC_LABEL)));
        lacCheck.setText(DefaultParams.LAC_LABEL);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        basicPanel.add(lacCheck, gridBagConstraints);

        tipsCheck.setSelected(Boolean.parseBoolean(props.getProperty(DefaultParams.NUM_END_LABEL)));
        tipsCheck.setText(DefaultParams.NUM_END_LABEL);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        basicPanel.add(tipsCheck, gridBagConstraints);

        branchCheck.setSelected(Boolean.parseBoolean(props.getProperty(DefaultParams.NUM_BRANCH_LABEL)));
        branchCheck.setText(DefaultParams.NUM_BRANCH_LABEL);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        basicPanel.add(branchCheck, gridBagConstraints);

        curveCheckBox.setText(DefaultParams.CURVE_LABEL);
        curveCheckBox.setSelected(Boolean.parseBoolean(props.getProperty(DefaultParams.CURVE_LABEL)));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        basicPanel.add(curveCheckBox, gridBagConstraints);

        curveWindowLabel.setText(DefaultParams.CURVE_WIN_LABEL);
        curveWindowLabel.setLabelFor(curveWindowTextField);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        basicPanel.add(curveWindowLabel, gridBagConstraints);

        curveWindowTextField.setText(props.getProperty(DefaultParams.CURVE_WIN_LABEL));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        basicPanel.add(curveWindowTextField, gridBagConstraints);

        jTabbedPane1.addTab("Basic", basicPanel);

        advancedPanel.setLayout(new java.awt.GridBagLayout());

        blurRadiusLabel.setText(DefaultParams.NOISE_RED_LABEL);
        blurRadiusLabel.setEnabled(Boolean.parseBoolean(props.getProperty(DefaultParams.PRE_PROCESS_LABEL)));
        blurRadiusLabel.setLabelFor(blurRadiusField);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(15, 5, 15, 5);
        advancedPanel.add(blurRadiusLabel, gridBagConstraints);

        blurRadiusField.setText(props.getProperty(DefaultParams.NOISE_RED_LABEL));
        blurRadiusField.setEnabled(Boolean.parseBoolean(props.getProperty(DefaultParams.PRE_PROCESS_LABEL)));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(15, 5, 15, 5);
        advancedPanel.add(blurRadiusField, gridBagConstraints);

        lightBGRadio.setSelected(Boolean.parseBoolean(props.getProperty(DefaultParams.LIGHT_BACK_LABEL)));
        lightBGRadio.setText(DefaultParams.LIGHT_BACK_LABEL);
        lightBGRadio.setEnabled(Boolean.parseBoolean(props.getProperty(DefaultParams.PRE_PROCESS_LABEL)));
        lightBGRadio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lightBGRadioActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(15, 5, 15, 5);
        advancedPanel.add(lightBGRadio, gridBagConstraints);

        darkBGRadio.setSelected(!Boolean.parseBoolean(props.getProperty(DefaultParams.LIGHT_BACK_LABEL)));
        darkBGRadio.setText(DefaultParams.DARK_BACK_LABEL);
        darkBGRadio.setEnabled(Boolean.parseBoolean(props.getProperty(DefaultParams.PRE_PROCESS_LABEL)));
        darkBGRadio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                darkBGRadioActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(15, 5, 15, 5);
        advancedPanel.add(darkBGRadio, gridBagConstraints);

        autoThresholdRadio.setSelected(Boolean.parseBoolean(props.getProperty(DefaultParams.AUTO_THRESH_LABEL)));
        autoThresholdRadio.setText(DefaultParams.AUTO_THRESH_LABEL);
        autoThresholdRadio.setEnabled(Boolean.parseBoolean(props.getProperty(DefaultParams.PRE_PROCESS_LABEL)));
        autoThresholdRadio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                autoThresholdRadioActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(15, 5, 0, 5);
        advancedPanel.add(autoThresholdRadio, gridBagConstraints);

        manualThresholdRadio.setSelected(!Boolean.parseBoolean(props.getProperty(DefaultParams.AUTO_THRESH_LABEL)));
        manualThresholdRadio.setText(DefaultParams.MAN_THRESH_LABEL);
        manualThresholdRadio.setEnabled(Boolean.parseBoolean(props.getProperty(DefaultParams.PRE_PROCESS_LABEL)));
        manualThresholdRadio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                manualThresholdRadioActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 15, 5);
        advancedPanel.add(manualThresholdRadio, gridBagConstraints);

        thresholdLabel.setText(DefaultParams.THRESH_LEV_LABEL);
        thresholdLabel.setEnabled(Boolean.parseBoolean(props.getProperty(DefaultParams.PRE_PROCESS_LABEL))&&manualThresholdRadio.isSelected());
        thresholdLabel.setLabelFor(manualThresholdField);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 15, 5);
        advancedPanel.add(thresholdLabel, gridBagConstraints);

        manualThresholdField.setText(props.getProperty(DefaultParams.THRESH_LEV_LABEL));
        manualThresholdField.setEnabled(Boolean.parseBoolean(props.getProperty(DefaultParams.PRE_PROCESS_LABEL)) && manualThresholdRadio.isSelected());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 15, 5);
        advancedPanel.add(manualThresholdField, gridBagConstraints);

        subBackgroundCheck.setSelected(Boolean.parseBoolean(props.getProperty(DefaultParams.REMOVE_BACK_LABEL)));
        subBackgroundCheck.setText(DefaultParams.REMOVE_BACK_LABEL);
        subBackgroundCheck.setEnabled(Boolean.parseBoolean(props.getProperty(DefaultParams.PRE_PROCESS_LABEL)));
        subBackgroundCheck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                subBackgroundCheckActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(15, 5, 15, 5);
        advancedPanel.add(subBackgroundCheck, gridBagConstraints);

        backgroundLabel.setText(DefaultParams.BACK_FILT_LABEL);
        backgroundLabel.setEnabled(Boolean.parseBoolean(props.getProperty(DefaultParams.BACK_FILT_LABEL)) && subBackgroundCheck.isSelected());
        backgroundLabel.setLabelFor(backgroundTextField);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(15, 5, 15, 5);
        advancedPanel.add(backgroundLabel, gridBagConstraints);

        backgroundTextField.setText(props.getProperty(DefaultParams.BACK_FILT_LABEL));
        backgroundTextField.setEnabled(Boolean.parseBoolean(props.getProperty(DefaultParams.PRE_PROCESS_LABEL)) && subBackgroundCheck.isSelected());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(15, 5, 15, 5);
        advancedPanel.add(backgroundTextField, gridBagConstraints);

        maskImageCheck.setSelected(Boolean.parseBoolean(props.getProperty(DefaultParams.CREATE_MASK_LABEL)));
        maskImageCheck.setText(DefaultParams.CREATE_MASK_LABEL);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(15, 5, 0, 5);
        advancedPanel.add(maskImageCheck, gridBagConstraints);

        watershedCheckBox.setSelected(Boolean.parseBoolean(props.getProperty(DefaultParams.SEPARATE_TOUCHING_LABEL)));
        watershedCheckBox.setText(DefaultParams.SEPARATE_TOUCHING_LABEL);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        advancedPanel.add(watershedCheckBox, gridBagConstraints);

        edgeCheckBox.setText(DefaultParams.EXCLUDE_EDGE_LABEL);
        edgeCheckBox.setSelected(Boolean.parseBoolean(props.getProperty(DefaultParams.EXCLUDE_EDGE_LABEL)));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(15, 5, 0, 5);
        advancedPanel.add(edgeCheckBox, gridBagConstraints);

        wholeImageCheckBox.setSelected(Boolean.parseBoolean(props.getProperty(DefaultParams.WHOLE_IMAGE_LABEL)));
        wholeImageCheckBox.setText(DefaultParams.WHOLE_IMAGE_LABEL);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        advancedPanel.add(wholeImageCheckBox, gridBagConstraints);

        threshComboBox.setModel(new DefaultComboBoxModel(AutoThresholder.Method.values()));
        threshComboBox.setSelectedItem(props.getProperty(DefaultParams.THRESH_METH_LABEL));
        threshComboBox.setEnabled(Boolean.parseBoolean(props.getProperty(DefaultParams.PRE_PROCESS_LABEL)) && Boolean.parseBoolean(props.getProperty(DefaultParams.AUTO_THRESH_LABEL)));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(15, 5, 0, 5);
        advancedPanel.add(threshComboBox, gridBagConstraints);

        autoThresholdLabel.setText(DefaultParams.THRESH_METH_LABEL);
        autoThresholdLabel.setEnabled(Boolean.parseBoolean(props.getProperty(DefaultParams.PRE_PROCESS_LABEL)) && autoThresholdRadio.isSelected());
        autoThresholdLabel.setLabelFor(threshComboBox);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(15, 5, 0, 5);
        advancedPanel.add(autoThresholdLabel, gridBagConstraints);

        preProcessCheckBox.setText(DefaultParams.PRE_PROCESS_LABEL);
        preProcessCheckBox.setSelected(Boolean.parseBoolean(props.getProperty(DefaultParams.PRE_PROCESS_LABEL)));
        preProcessCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                preProcessCheckBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(15, 5, 15, 5);
        advancedPanel.add(preProcessCheckBox, gridBagConstraints);

        curveValsCheckBox.setText(DefaultParams.OUTPUT_CURVE_LABEL);
        curveValsCheckBox.setSelected(Boolean.parseBoolean(props.getProperty(DefaultParams.OUTPUT_CURVE_LABEL)));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 15, 0);
        advancedPanel.add(curveValsCheckBox, gridBagConstraints);

        jTabbedPane1.addTab("Advanced", advancedPanel);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        getContentPane().add(jTabbedPane1, gridBagConstraints);

        jPanel7.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel7.setLayout(new java.awt.GridBagLayout());

        okButton.setText("OK");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });
        jPanel7.add(okButton, new java.awt.GridBagConstraints());

        exitButton.setText("EXIT");
        exitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitButtonActionPerformed(evt);
            }
        });
        jPanel7.add(exitButton, new java.awt.GridBagConstraints());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        getContentPane().add(jPanel7, gridBagConstraints);

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
        setVariables();
        exit = false;

        lac = lacCheck.isSelected();
        thl = thlCheck.isSelected();
        createMasks = maskImageCheck.isSelected();
        tips = tipsCheck.isSelected();
        hgu = hguCheck.isSelected();
        subBackground = subBackgroundCheck.isSelected();
        circ = circCheck.isSelected();
        area = areaCheck.isSelected();
        curvature = curveCheckBox.isSelected();
        fourfrac = fourierFracCheck.isSelected();
        boxfrac = boxFracCheck.isSelected();
        lightBackground = lightBGRadio.isSelected();
        branches = branchCheck.isSelected();
        doWatershed = watershedCheckBox.isSelected();
        wholeImage = wholeImageCheckBox.isSelected();
        excludeEdges = edgeCheckBox.isSelected();
        thresholdMethod = Method.valueOf(String.valueOf(threshComboBox.getSelectedItem()));
        preProcess = preProcessCheckBox.isSelected();
        curveVals = curveValsCheckBox.isSelected();

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
        String filterText = blurRadiusField.getText();
        String curveWindowText = curveWindowTextField.getText();
        try {
            imageRes = (resText != null) ? Double.parseDouble(resText) : imageRes;
            filterRadius = (filterText != null) ? Double.parseDouble(filterText) : filterRadius;
            imageRes2 = imageRes * imageRes;
            minLength = (branchText != null) ? Double.parseDouble(branchText) : 0.0;
            maxCirc = (circText != null) ? Double.parseDouble(circText) : 0.0;
            minArea = (areaText != null) ? Double.parseDouble(areaText) : 0.0;
            curvatureWindow = Integer.parseInt(curveWindowText);
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
        manualThresholdRadio.setSelected(false);
        updateThreshControls();
    }//GEN-LAST:event_autoThresholdRadioActionPerformed

    private void manualThresholdRadioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_manualThresholdRadioActionPerformed
        autoThresholdRadio.setSelected(false);
        updateThreshControls();
    }//GEN-LAST:event_manualThresholdRadioActionPerformed

    void updateThreshControls() {
        manualThresholdField.setEnabled(manualThresholdRadio.isSelected());
        thresholdLabel.setEnabled(manualThresholdRadio.isSelected());
        autoThresholdLabel.setEnabled(!manualThresholdRadio.isSelected());
        threshComboBox.setEnabled(!manualThresholdRadio.isSelected());
    }

    private void lightBGRadioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lightBGRadioActionPerformed
        darkBGRadio.setSelected(!lightBGRadio.isSelected());
    }//GEN-LAST:event_lightBGRadioActionPerformed

    private void darkBGRadioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_darkBGRadioActionPerformed
        lightBGRadio.setSelected(!darkBGRadio.isSelected());
    }//GEN-LAST:event_darkBGRadioActionPerformed

    private void subBackgroundCheckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_subBackgroundCheckActionPerformed
        backgroundTextField.setEnabled(subBackgroundCheck.isSelected());
        backgroundLabel.setEnabled(subBackgroundCheck.isSelected());
    }//GEN-LAST:event_subBackgroundCheckActionPerformed

    private void preProcessCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_preProcessCheckBoxActionPerformed
        blurRadiusField.setEnabled(preProcessCheckBox.isSelected());
        blurRadiusLabel.setEnabled(preProcessCheckBox.isSelected());
        lightBGRadio.setEnabled(preProcessCheckBox.isSelected());
        darkBGRadio.setEnabled(preProcessCheckBox.isSelected());
        subBackgroundCheck.setEnabled(preProcessCheckBox.isSelected());
        backgroundLabel.setEnabled(preProcessCheckBox.isSelected() && subBackgroundCheck.isSelected());
        backgroundTextField.setEnabled(preProcessCheckBox.isSelected() && subBackgroundCheck.isSelected());
        autoThresholdRadio.setEnabled(preProcessCheckBox.isSelected());
        manualThresholdRadio.setEnabled(preProcessCheckBox.isSelected());
        autoThresholdLabel.setEnabled(preProcessCheckBox.isSelected() && autoThresholdRadio.isSelected());
        thresholdLabel.setEnabled(preProcessCheckBox.isSelected() && !autoThresholdRadio.isSelected());
        threshComboBox.setEnabled(preProcessCheckBox.isSelected() && autoThresholdRadio.isSelected());
        manualThresholdField.setEnabled(preProcessCheckBox.isSelected() && !autoThresholdRadio.isSelected());
    }//GEN-LAST:event_preProcessCheckBoxActionPerformed

    public boolean[] getOptions() {
        boolean options[] = {area, circ, hgu, thl, tips, branches, fourfrac, boxfrac, lac, curvature};
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

    public double getImageRes2() {
        return imageRes2;
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

    public static boolean isCurvature() {
        return curvature;
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

    public Method getThresholdMethod() {
        return thresholdMethod;
    }

    public boolean isPreProcess() {
        return preProcess;
    }

    public int getCurvatureWindow() {
        return curvatureWindow;
    }

    public boolean isCurveVals() {
        return curveVals;
    }

    /**
     * Returns true if the user exited the application.
     */
    public boolean exitProgram() {
        return exit;
    }

    public boolean setVariables() {
        setProperties(props, this);
        return false;
    }

    public void setProperties(Properties p, Container c) {
        PropertyExtractor.setProperties(p, c, PropertyExtractor.WRITE);
    }

    public Properties getProps() {
        return props;
    }

    private final String title;
    private double imageRes2;
    private static int formatIndex = 3, manualThreshold = 100, curvatureWindow = 20;
    private static double minLength = 20.0, maxCirc = 0.2, minArea = 10.0,
            imageRes = 1.0, backgroundRadius = 50.0, filterRadius = 1.0;
    private static boolean createMasks = true, subBackground = true, area = true,
            circ = true, thl = true, tips = true, hgu = true, fourfrac = true,
            boxfrac = true, lac = true, exit, autoThreshold = true, lightBackground = true,
            branches = true, doWatershed = false,
            wholeImage = false, excludeEdges = true, curvature = true, preProcess = true, curveVals = false;
    private static Method thresholdMethod = Method.Otsu;
    private DecimalFormat threePlaces = new DecimalFormat("0.000");
    private DecimalFormat onePlace = new DecimalFormat("0.0");
    private Properties props;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel advancedPanel;
    private javax.swing.JCheckBox areaCheck;
    private javax.swing.JLabel autoThresholdLabel;
    private javax.swing.JRadioButton autoThresholdRadio;
    private javax.swing.JLabel backgroundLabel;
    private javax.swing.JTextField backgroundTextField;
    private javax.swing.JPanel basicPanel;
    private javax.swing.JTextField blurRadiusField;
    private javax.swing.JLabel blurRadiusLabel;
    private javax.swing.JCheckBox boxFracCheck;
    private javax.swing.JCheckBox branchCheck;
    private javax.swing.JCheckBox circCheck;
    private javax.swing.JCheckBox curveCheckBox;
    private javax.swing.JCheckBox curveValsCheckBox;
    private javax.swing.JLabel curveWindowLabel;
    private javax.swing.JTextField curveWindowTextField;
    private javax.swing.JRadioButton darkBGRadio;
    private javax.swing.JCheckBox edgeCheckBox;
    private javax.swing.JButton exitButton;
    private javax.swing.JCheckBox fourierFracCheck;
    private javax.swing.JCheckBox hguCheck;
    private javax.swing.JComboBox imageFormatCombo;
    private javax.swing.JLabel imageFormatLabel;
    private javax.swing.JTextField imageResField;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JCheckBox lacCheck;
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
    private javax.swing.JCheckBox preProcessCheckBox;
    private javax.swing.JLabel resLabel;
    private javax.swing.JCheckBox subBackgroundCheck;
    private javax.swing.JCheckBox thlCheck;
    private javax.swing.JComboBox threshComboBox;
    private javax.swing.JLabel thresholdLabel;
    private javax.swing.JCheckBox tipsCheck;
    private javax.swing.JCheckBox watershedCheckBox;
    private javax.swing.JCheckBox wholeImageCheckBox;
    // End of variables declaration//GEN-END:variables
}
