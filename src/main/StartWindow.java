/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package main;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import student.Profile;
import window.MainWindow;

/**
 *
 * @author Michael
 */
public final class StartWindow extends javax.swing.JFrame {

    private Profile profile;

    /**
     * Creates new form StartWindow
     *
     * @param profile The current profile
     */
    public StartWindow(Profile profile) {
        initComponents();
        setProfile(profile);
    }

    public void setProfile(Profile profile) {
        this.profile = profile;

        boolean enabled = (this.profile != null); //not enabled if profile is null (no profile exists)

        //set all info in the text labels
        if (enabled) {
            usernameLabel.setText(profile.getString(Profile.USERNAME));
            dobLabel.setText(
                    profile.getString(Profile.MONTH) + " "
                    + profile.getString(Profile.DAY) + ", "
                    + profile.getString(Profile.YEAR)
            );
            schoolLabel.setText(profile.getString(Profile.SCHOOL) + ", Grade " + profile.getString(Profile.GRADE));
        }
        else {
            usernameLabel.setText("[no profile]");
            dobLabel.setText("[no profile]");
            schoolLabel.setText("[no profile]");
        }

        resetButtons();

        logInButton.requestFocus();
    }

    public Profile getProfile() {
        return profile;
    }

    private boolean verify() {
        if (profile == null)
            return true;
        if (!profile.isLocked()) //not locked, doesn't need verification
            return true;
        if (profile.isAccessGranted()) //already entered password previously
            return true;
        if (profile.isDenied()) //attempted too many times
            return false;

        final JButton ok = new JButton("OK");
        ok.addActionListener(event -> {
            JOptionPane pane = WindowUtil.getOptionPane((JComponent) event.getSource());
            pane.setValue(ok); //option pane gets JButton `okay` value
        });
        ok.setEnabled(false); //disable `okay` at beginning

        final JButton cancel = new JButton("Cancel");
        cancel.addActionListener(event -> {
            JOptionPane pane = WindowUtil.getOptionPane((JComponent) event.getSource());
            pane.setValue(cancel);
        });

        JPanel panel = new JPanel(); ///the panel to put in the verification dialog

        final JPasswordField field = new JPasswordField(6);
        panel.add(new JLabel("Enter your student ID:"));
        panel.add(field);
        Runnable updater = () -> {
            char[] password = field.getPassword();
            ok.setEnabled(password.length > 0);
            for (int i = 0; i < password.length; i++) //set array empty
                password[i] = 0;
        };
        WindowUtil.linkTextToButton(field, ok, updater);

        int choice = JOptionPane.showOptionDialog(null, panel, "Verification",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null,
                new Object[]{ok, cancel}, null);
        if (choice == 0) {
            char[] password = field.getPassword();
            try {
                if (Integer.parseInt(String.valueOf(password)) == Integer.parseInt(profile.getField(Profile.ID))) {
                    profile.grantAccess();
                    return true;
                }
                else
                    throw new NumberFormatException();
            }
            catch (NumberFormatException e) {
                profile.incAttempts();
                String msg = "Incorrect ID.";
                if (!profile.isDenied())
                    msg += " Please try again.";
                else
                    resetButtons(); //disable stuff because access is denied
                JOptionPane.showMessageDialog(null, msg, "Denied", JOptionPane.WARNING_MESSAGE);
                return verify();
            }
        }
        else
            return false;
    }

    private void resetButtons() { //enable/disable buttons
        if (profile == null) {
            editButton.setEnabled(true);
            nLogInButton.setEnabled(false);
            logInButton.setEnabled(false);
        }
        else if (profile.isDenied()) {
            editButton.setEnabled(false);
            nLogInButton.setEnabled(false);
            logInButton.setEnabled(false);
        }
        else {
            editButton.setEnabled(true);
            nLogInButton.setEnabled(true);
            logInButton.setEnabled(true);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        nLogInButton = new javax.swing.JButton();
        logInButton = new javax.swing.JButton();
        editButton = new javax.swing.JButton();
        usernameLabel = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        dobLabel = new javax.swing.JLabel();
        schoolLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(600, 400));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        getContentPane().setLayout(new java.awt.GridBagLayout());

        nLogInButton.setText("Log In Without Updating");
        nLogInButton.setEnabled(false);
        nLogInButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nLogInButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(1, 5, 2, 5);
        getContentPane().add(nLogInButton, gridBagConstraints);

        logInButton.setFont(new java.awt.Font("sansserif", 0, 24)); // NOI18N
        logInButton.setText("Log In");
        logInButton.setEnabled(false);
        logInButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logInButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(1, 5, 4, 5);
        getContentPane().add(logInButton, gridBagConstraints);

        editButton.setText("Edit Account Info");
        editButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 6, 5);
        getContentPane().add(editButton, gridBagConstraints);

        usernameLabel.setFont(new java.awt.Font("sansserif", 1, 18)); // NOI18N
        usernameLabel.setText("[no profile]");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(1, 5, 2, 5);
        getContentPane().add(usernameLabel, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 1, 5);
        getContentPane().add(jSeparator1, gridBagConstraints);

        dobLabel.setText("[no profile]");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(1, 5, 2, 5);
        getContentPane().add(dobLabel, gridBagConstraints);

        schoolLabel.setText("[no profile]");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(1, 5, 6, 5);
        getContentPane().add(schoolLabel, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void editButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editButtonActionPerformed
        if (verify()) {
            setVisible(false);
            EditProfileWindow editWindow = new EditProfileWindow(this);
            editWindow.setLocationRelativeTo(null);
            editWindow.setVisible(true);
        }
    }//GEN-LAST:event_editButtonActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        Driver.exit();
    }//GEN-LAST:event_formWindowClosing

    private void nLogInButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nLogInButtonActionPerformed
        dispose();
        new MainWindow(profile).showFrame();
    }//GEN-LAST:event_nLogInButtonActionPerformed

    private void logInButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_logInButtonActionPerformed
        try {
            WebAccessor.studentAccess(profile);
        }
        catch (InterruptedException ex) {
            Logger.getLogger(StartWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_logInButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel dobLabel;
    private javax.swing.JButton editButton;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JButton logInButton;
    private javax.swing.JButton nLogInButton;
    private javax.swing.JLabel schoolLabel;
    private javax.swing.JLabel usernameLabel;
    // End of variables declaration//GEN-END:variables
}
