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
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.util.Properties;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import net.calm.anamorf.params.DefaultParams;
import net.calm.iaclasslibrary.UIClasses.GUIMethods;
import net.calm.iaclasslibrary.UIClasses.PropertyExtractor;

/**
 * The parameter dialog shown by {@link Batch_Analyser} when it is run
 * interactively (rather than through the macro layer). The parameter surface is
 * defined by {@link DefaultParams}; each control is keyed to a property by its
 * label text, and the dialog writes the user's choices back into the shared
 * {@link Properties} object via {@link PropertyExtractor}.
 */
public class UserInterface extends JDialog implements GUIMethods {

    private static final String[] IMAGE_FORMATS = {"BMP", "GIF", "JPG", "PNG", "TIF"};
    private static final Insets INSETS = new Insets(5, 5, 5, 5);

    private final String title;
    private final Properties props;
    private boolean exit;

    // Basic tab
    private JComboBox<String> imageFormatCombo;
    private JTextField imageResField;
    private JTextField minBranchField;
    private JTextField maxCircField;
    private JTextField minAreaField;
    private JTextField curveWindowTextField;
    private JCheckBox areaCheck;
    private JCheckBox circCheck;
    private JCheckBox thlCheck;
    private JCheckBox hguCheck;
    private JCheckBox branchCheck;
    private JCheckBox tipsCheck;
    private JCheckBox fourierFracCheck;
    private JCheckBox boxFracCheck;
    private JCheckBox lacCheck;
    private JCheckBox curveCheckBox;

    // Advanced tab
    private JCheckBox preProcessCheckBox;
    private JRadioButton lightBGRadio;
    private JRadioButton darkBGRadio;
    private JTextField blurRadiusField;
    private JLabel blurRadiusLabel;
    private JCheckBox subBackgroundCheck;
    private JTextField backgroundTextField;
    private JLabel backgroundLabel;
    private JRadioButton autoThresholdRadio;
    private JRadioButton manualThresholdRadio;
    private JLabel autoThresholdLabel;
    private JComboBox<AutoThresholder.Method> threshComboBox;
    private JLabel thresholdLabel;
    private JTextField manualThresholdField;
    private JCheckBox maskImageCheck;
    private JCheckBox edgeCheckBox;
    private JCheckBox watershedCheckBox;
    private JCheckBox wholeImageCheckBox;
    private JCheckBox curveValsCheckBox;

    /**
     * Creates the parameter dialog.
     *
     * @param parent the parent frame (may be null)
     * @param modal whether the dialog is modal
     * @param title the dialog title
     * @param props the parameter set to read initial values from and write
     * choices back to; when null a fresh {@link DefaultParams} is used
     */
    public UserInterface(Frame parent, boolean modal, String title, Properties props) {
        super(parent, modal);
        this.title = title;
        this.props = (props != null) ? props : new DefaultParams();
        this.exit = true;
        buildUI();
        centreOnScreen();
    }

    private void buildUI() {
        setTitle(title);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new GridBagLayout());

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Basic", buildBasicPanel());
        tabs.addTab("Advanced", buildAdvancedPanel());

        GridBagConstraints tabsConstraints = new GridBagConstraints();
        tabsConstraints.gridx = 0;
        tabsConstraints.gridy = 0;
        tabsConstraints.fill = GridBagConstraints.BOTH;
        tabsConstraints.weightx = 1.0;
        tabsConstraints.weighty = 1.0;
        getContentPane().add(tabs, tabsConstraints);

        JPanel buttonPanel = new JPanel(new GridBagLayout());
        JButton okButton = new JButton("OK");
        okButton.addActionListener(e -> okButtonPressed());
        JButton exitButton = new JButton("EXIT");
        exitButton.addActionListener(e -> exitButtonPressed());
        buttonPanel.add(okButton, new GridBagConstraints());
        GridBagConstraints exitConstraints = new GridBagConstraints();
        exitConstraints.gridx = 1;
        buttonPanel.add(exitButton, exitConstraints);

        GridBagConstraints buttonConstraints = new GridBagConstraints();
        buttonConstraints.gridx = 0;
        buttonConstraints.gridy = 1;
        buttonConstraints.fill = GridBagConstraints.HORIZONTAL;
        buttonConstraints.weightx = 1.0;
        getContentPane().add(buttonPanel, buttonConstraints);

        pack();
        updateEnabledState();
    }

    private JPanel buildBasicPanel() {
        JPanel panel = new JPanel(new GridBagLayout());

        imageFormatCombo = new JComboBox<>(IMAGE_FORMATS);
        imageFormatCombo.setSelectedItem(props.getProperty(DefaultParams.IMAGE_FORMAT_LABEL));
        imageResField = new JTextField(props.getProperty(DefaultParams.IMAGE_RES_LABEL));
        minBranchField = new JTextField(props.getProperty(DefaultParams.MIN_BRANCH_LABEL));
        maxCircField = new JTextField(props.getProperty(DefaultParams.MAX_CIRC_LABEL));
        minAreaField = new JTextField(props.getProperty(DefaultParams.MIN_AREA_LABEL));
        curveWindowTextField = new JTextField(props.getProperty(DefaultParams.CURVE_WIN_LABEL));

        int row = 0;
        addLabeledField(panel, DefaultParams.IMAGE_FORMAT_LABEL, imageFormatCombo, row++);
        addLabeledField(panel, DefaultParams.IMAGE_RES_LABEL, imageResField, row++);
        addLabeledField(panel, DefaultParams.MIN_BRANCH_LABEL, minBranchField, row++);
        addLabeledField(panel, DefaultParams.MAX_CIRC_LABEL, maxCircField, row++);
        addLabeledField(panel, DefaultParams.MIN_AREA_LABEL, minAreaField, row++);
        addLabeledField(panel, DefaultParams.CURVE_WIN_LABEL, curveWindowTextField, row++);

        areaCheck = checkbox(DefaultParams.PROJ_AREA_LABEL);
        circCheck = checkbox(DefaultParams.CIRC_LABEL);
        thlCheck = checkbox(DefaultParams.TOT_LENGTH_LABEL);
        hguCheck = checkbox(DefaultParams.MEAN_BRANCH_LABEL);
        branchCheck = checkbox(DefaultParams.NUM_BRANCH_LABEL);
        tipsCheck = checkbox(DefaultParams.NUM_END_LABEL);
        fourierFracCheck = checkbox(DefaultParams.FOURIER_FRAC_LABEL);
        boxFracCheck = checkbox(DefaultParams.BOX_COUNT_LABEL);
        lacCheck = checkbox(DefaultParams.LAC_LABEL);
        curveCheckBox = checkbox(DefaultParams.CURVE_LABEL);

        addCheckbox(panel, areaCheck, 0, row);
        addCheckbox(panel, circCheck, 1, row++);
        addCheckbox(panel, thlCheck, 0, row);
        addCheckbox(panel, hguCheck, 1, row++);
        addCheckbox(panel, branchCheck, 0, row);
        addCheckbox(panel, tipsCheck, 1, row++);
        addCheckbox(panel, fourierFracCheck, 0, row);
        addCheckbox(panel, boxFracCheck, 1, row++);
        addCheckbox(panel, lacCheck, 0, row);
        addCheckbox(panel, curveCheckBox, 1, row);

        return panel;
    }

    private JPanel buildAdvancedPanel() {
        JPanel panel = new JPanel(new GridBagLayout());

        preProcessCheckBox = checkbox(DefaultParams.PRE_PROCESS_LABEL);

        ButtonGroup backgroundGroup = new ButtonGroup();
        lightBGRadio = new JRadioButton(DefaultParams.LIGHT_BACK_LABEL);
        darkBGRadio = new JRadioButton(DefaultParams.DARK_BACK_LABEL);
        backgroundGroup.add(lightBGRadio);
        backgroundGroup.add(darkBGRadio);
        lightBGRadio.setSelected(Boolean.parseBoolean(props.getProperty(DefaultParams.LIGHT_BACK_LABEL)));
        darkBGRadio.setSelected(Boolean.parseBoolean(props.getProperty(DefaultParams.DARK_BACK_LABEL)));

        blurRadiusField = new JTextField(props.getProperty(DefaultParams.NOISE_RED_LABEL));

        subBackgroundCheck = checkbox(DefaultParams.REMOVE_BACK_LABEL);
        backgroundTextField = new JTextField(props.getProperty(DefaultParams.BACK_FILT_LABEL));

        ButtonGroup thresholdGroup = new ButtonGroup();
        autoThresholdRadio = new JRadioButton(DefaultParams.AUTO_THRESH_LABEL);
        manualThresholdRadio = new JRadioButton(DefaultParams.MAN_THRESH_LABEL);
        thresholdGroup.add(autoThresholdRadio);
        thresholdGroup.add(manualThresholdRadio);
        autoThresholdRadio.setSelected(Boolean.parseBoolean(props.getProperty(DefaultParams.AUTO_THRESH_LABEL)));
        manualThresholdRadio.setSelected(Boolean.parseBoolean(props.getProperty(DefaultParams.MAN_THRESH_LABEL)));

        threshComboBox = new JComboBox<>(AutoThresholder.Method.values());
        selectThresholdMethod(props.getProperty(DefaultParams.THRESH_METH_LABEL));
        manualThresholdField = new JTextField(props.getProperty(DefaultParams.THRESH_LEV_LABEL));

        maskImageCheck = checkbox(DefaultParams.CREATE_MASK_LABEL);
        edgeCheckBox = checkbox(DefaultParams.EXCLUDE_EDGE_LABEL);
        watershedCheckBox = checkbox(DefaultParams.SEPARATE_TOUCHING_LABEL);
        wholeImageCheckBox = checkbox(DefaultParams.WHOLE_IMAGE_LABEL);
        curveValsCheckBox = checkbox(DefaultParams.OUTPUT_CURVE_LABEL);

        preProcessCheckBox.addActionListener(e -> updateEnabledState());
        lightBGRadio.addActionListener(e -> updateEnabledState());
        darkBGRadio.addActionListener(e -> updateEnabledState());
        subBackgroundCheck.addActionListener(e -> updateEnabledState());
        autoThresholdRadio.addActionListener(e -> updateEnabledState());
        manualThresholdRadio.addActionListener(e -> updateEnabledState());

        int row = 0;
        panel.add(preProcessCheckBox, gbc(0, row++, 2, 1.0, 0.0));
        panel.add(lightBGRadio, gbc(0, row, 1, 1.0, 0.0));
        panel.add(darkBGRadio, gbc(1, row++, 1, 1.0, 0.0));
        addLabeledField(panel, DefaultParams.NOISE_RED_LABEL, blurRadiusField, row++);
        panel.add(subBackgroundCheck, gbc(0, row++, 2, 1.0, 0.0));
        addLabeledField(panel, DefaultParams.BACK_FILT_LABEL, backgroundTextField, row++);
        panel.add(autoThresholdRadio, gbc(0, row, 1, 1.0, 0.0));
        panel.add(manualThresholdRadio, gbc(1, row++, 1, 1.0, 0.0));
        addLabeledField(panel, DefaultParams.THRESH_METH_LABEL, threshComboBox, row++);
        addLabeledField(panel, DefaultParams.THRESH_LEV_LABEL, manualThresholdField, row++);
        panel.add(maskImageCheck, gbc(0, row, 1, 1.0, 0.0));
        panel.add(edgeCheckBox, gbc(1, row++, 1, 1.0, 0.0));
        panel.add(watershedCheckBox, gbc(0, row, 1, 1.0, 0.0));
        panel.add(wholeImageCheckBox, gbc(1, row++, 1, 1.0, 0.0));
        panel.add(curveValsCheckBox, gbc(0, row, 2, 1.0, 0.0));

        // Cache the labels created by addLabeledField so enable/disable logic
        // can reach them.
        for (java.awt.Component c : panel.getComponents()) {
            if (c instanceof JLabel label) {
                if (DefaultParams.NOISE_RED_LABEL.equals(label.getText())) {
                    blurRadiusLabel = label;
                } else if (DefaultParams.BACK_FILT_LABEL.equals(label.getText())) {
                    backgroundLabel = label;
                } else if (DefaultParams.THRESH_METH_LABEL.equals(label.getText())) {
                    autoThresholdLabel = label;
                } else if (DefaultParams.THRESH_LEV_LABEL.equals(label.getText())) {
                    thresholdLabel = label;
                }
            }
        }

        return panel;
    }

    private JCheckBox checkbox(String text) {
        JCheckBox box = new JCheckBox(text);
        box.setSelected(Boolean.parseBoolean(props.getProperty(text)));
        return box;
    }

    private void addLabeledField(JPanel panel, String labelText, JComponent field, int gridy) {
        JLabel label = new JLabel(labelText);
        label.setLabelFor(field);
        panel.add(label, gbc(0, gridy, 1, 1.0, 0.0));
        panel.add(field, gbc(1, gridy, 1, 1.0, 0.0));
    }

    private void addCheckbox(JPanel panel, JCheckBox box, int gridx, int gridy) {
        panel.add(box, gbc(gridx, gridy, 1, 1.0, 0.0));
    }

    private GridBagConstraints gbc(int gridx, int gridy, int gridwidth, double weightx, double weighty) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = gridx;
        c.gridy = gridy;
        c.gridwidth = gridwidth;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = weightx;
        c.weighty = weighty;
        c.insets = INSETS;
        return c;
    }

    private void selectThresholdMethod(String methodName) {
        try {
            threshComboBox.setSelectedItem(AutoThresholder.Method.valueOf(methodName));
        } catch (IllegalArgumentException e) {
            threshComboBox.setSelectedItem(AutoThresholder.Method.Default);
        }
    }

    private void updateEnabledState() {
        boolean preProcess = preProcessCheckBox.isSelected();
        blurRadiusLabel.setEnabled(preProcess);
        blurRadiusField.setEnabled(preProcess);
        lightBGRadio.setEnabled(preProcess);
        darkBGRadio.setEnabled(preProcess);
        subBackgroundCheck.setEnabled(preProcess);
        autoThresholdRadio.setEnabled(preProcess);
        manualThresholdRadio.setEnabled(preProcess);

        boolean removeBackground = preProcess && subBackgroundCheck.isSelected();
        backgroundLabel.setEnabled(removeBackground);
        backgroundTextField.setEnabled(removeBackground);

        boolean autoThreshold = preProcess && autoThresholdRadio.isSelected();
        boolean manualThreshold = preProcess && manualThresholdRadio.isSelected();
        autoThresholdLabel.setEnabled(autoThreshold);
        threshComboBox.setEnabled(autoThreshold);
        thresholdLabel.setEnabled(manualThreshold);
        manualThresholdField.setEnabled(manualThreshold);
    }

    private void okButtonPressed() {
        setVariables();
        exit = false;
        if (validateNumericFields()) {
            setVisible(false);
        }
    }

    private boolean validateNumericFields() {
        try {
            Double.parseDouble(minBranchField.getText());
            Double.parseDouble(imageResField.getText());
            Double.parseDouble(maxCircField.getText());
            Double.parseDouble(minAreaField.getText());
            Integer.parseInt(curveWindowTextField.getText());
            Double.parseDouble(backgroundTextField.getText());
            Double.parseDouble(blurRadiusField.getText());
            if (manualThresholdRadio.isSelected()) {
                Integer.parseInt(manualThresholdField.getText());
            }
            return true;
        } catch (NumberFormatException e) {
            Toolkit.getDefaultToolkit().beep();
            IJ.error("All text fields must be numeric.");
            return false;
        }
    }

    private void exitButtonPressed() {
        Toolkit.getDefaultToolkit().beep();
        ExitDialog exitDialog = new ExitDialog(this, true);
        exitDialog.setVisible(true);
        if (exitDialog.okButtonPressed()) {
            exit = true;
            setVisible(false);
        }
    }

    /**
     * Writes the current control values back into {@link #props}.
     */
    @Override
    public boolean setVariables() {
        setProperties(props, this);
        return false;
    }

    @Override
    public void setProperties(Properties p, Container c) {
        PropertyExtractor.setProperties(p, c, PropertyExtractor.WRITE);
    }

    /**
     * Returns the property set backing this dialog.
     */
    public Properties getProps() {
        return props;
    }

    /**
     * Returns true if the user requested to exit the application.
     */
    public boolean exitProgram() {
        return exit;
    }

    private void centreOnScreen() {
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(screen.width / 2 - getWidth() / 2, screen.height / 2 - getHeight() / 2);
    }
}
