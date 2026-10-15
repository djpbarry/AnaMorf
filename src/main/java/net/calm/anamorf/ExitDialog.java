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

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * A confirmation dialog shown when the user asks to exit the application.
 */
public class ExitDialog extends JDialog {

    private boolean exitProgram;

    /**
     * Creates the confirmation dialog.
     *
     * @param parent the parent dialog
     * @param modal whether the dialog is modal
     */
    public ExitDialog(JDialog parent, boolean modal) {
        super(parent, modal);
        this.exitProgram = false;
        buildUI();
        centreOnScreen();
    }

    private void buildUI() {
        setTitle("Exit Program?");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new java.awt.BorderLayout());

        JPanel questionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        questionPanel.add(new JLabel("Do you really want to exit?"));
        getContentPane().add(questionPanel, java.awt.BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton okButton = new JButton("OK");
        okButton.addActionListener(e -> {
            exitProgram = true;
            dispose();
        });
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> {
            exitProgram = false;
            dispose();
        });
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        getContentPane().add(buttonPanel, java.awt.BorderLayout.SOUTH);

        pack();
    }

    /**
     * Returns true if the OK button was clicked, false otherwise.
     */
    public boolean okButtonPressed() {
        return exitProgram;
    }

    private void centreOnScreen() {
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(screen.width / 2 - getWidth() / 2, screen.height / 2 - getHeight() / 2);
    }
}
