
package main;

import javax.swing.JOptionPane;
import student.Profile;

/**
 *
 * @author Michael
 */
public class EditProfileWindow extends javax.swing.JFrame {
    private Profile profile; //profile that is being edited or created
    private StartWindow window; //StartWindow source
    private boolean changed; //has the user made any edits

    /**
     * Creates new form CreateProfileWindow
     *
     * @param window
     */
    public EditProfileWindow(StartWindow window) {
        initComponents();

        this.window = window;
        profile = window.getProfile();

        //if editing a profile, store that profile and change display stuff
        if (profile != null) {
            if (profile.isLocked())
                passwordCheckbox.setSelected(true);
            setTitle("Edit Profile"); //not "Create Profile"
            doneButton.setText("Save Profile"); //not "Create Profile"
            doneButton.setEnabled(true);
        }

        updateInfoDisplay();
    }

    private void updateInfoDisplay() {
        if (profile != null && !profile.isTemp()) { //take profile info and put it into the boxes
            usernameField.setText(profile.getString(Profile.USERNAME));
            idField.setText(profile.getString(Profile.ID));
            birthdateField.setText(
                    profile.getString(Profile.MONTH) + " "
                    + profile.getString(Profile.DAY) + ", "
                    + profile.getString(Profile.YEAR)
            );
            gradeField.setText(profile.getString(Profile.GRADE));
            schoolField.setText(profile.getString(Profile.SCHOOL));
        }
        pack();
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

        jButton2 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        filler15 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 5), new java.awt.Dimension(0, 5), new java.awt.Dimension(32767, 5));
        cancelButton = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        usernameField = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        birthdateField = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        gradeField = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        schoolField = new javax.swing.JTextField();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(4, 0), new java.awt.Dimension(4, 0), new java.awt.Dimension(4, 32767));
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(4, 0), new java.awt.Dimension(4, 0), new java.awt.Dimension(4, 32767));
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(4, 0), new java.awt.Dimension(4, 0), new java.awt.Dimension(4, 32767));
        filler4 = new javax.swing.Box.Filler(new java.awt.Dimension(4, 0), new java.awt.Dimension(4, 0), new java.awt.Dimension(4, 32767));
        filler5 = new javax.swing.Box.Filler(new java.awt.Dimension(4, 0), new java.awt.Dimension(4, 0), new java.awt.Dimension(4, 32767));
        idField = new javax.swing.JPasswordField();
        passwordCheckbox = new javax.swing.JCheckBox();
        filler6 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 20), new java.awt.Dimension(0, 20), new java.awt.Dimension(32767, 20));
        editButton = new javax.swing.JButton();
        jPanel8 = new javax.swing.JPanel();
        doneButton = new javax.swing.JButton();

        jButton2.setText("jButton2");

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Create New Profile");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.Y_AXIS));
        jPanel1.add(filler15);

        cancelButton.setText("Cancel");
        cancelButton.setAlignmentX(0.5F);
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });
        jPanel1.add(cancelButton);

        java.awt.GridBagLayout jPanel6Layout = new java.awt.GridBagLayout();
        jPanel6Layout.columnWidths = new int[] {0, 5, 0};
        jPanel6Layout.rowHeights = new int[] {0, 5, 0, 5, 0, 5, 0, 5, 0, 5, 0, 5, 0, 5, 0, 5, 0, 5, 0, 5, 0, 5, 0, 5, 0};
        jPanel6.setLayout(jPanel6Layout);

        jLabel6.setText("Username");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel6.add(jLabel6, gridBagConstraints);

        usernameField.setEditable(false);
        usernameField.setPreferredSize(new java.awt.Dimension(144, 28));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        jPanel6.add(usernameField, gridBagConstraints);

        jLabel7.setText("Student ID");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel6.add(jLabel7, gridBagConstraints);

        jLabel8.setText("Birthdate");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel6.add(jLabel8, gridBagConstraints);

        birthdateField.setEditable(false);
        birthdateField.setPreferredSize(new java.awt.Dimension(144, 28));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 12;
        jPanel6.add(birthdateField, gridBagConstraints);

        jLabel9.setText("Grade");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 16;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel6.add(jLabel9, gridBagConstraints);

        gradeField.setEditable(false);
        gradeField.setPreferredSize(new java.awt.Dimension(144, 28));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 16;
        jPanel6.add(gradeField, gridBagConstraints);

        jLabel10.setText("School");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 20;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel6.add(jLabel10, gridBagConstraints);

        schoolField.setEditable(false);
        schoolField.setPreferredSize(new java.awt.Dimension(144, 28));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 20;
        jPanel6.add(schoolField, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        jPanel6.add(filler1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 16;
        jPanel6.add(filler2, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 20;
        jPanel6.add(filler3, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 8;
        jPanel6.add(filler4, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 12;
        jPanel6.add(filler5, gridBagConstraints);

        idField.setEditable(false);
        idField.setPreferredSize(new java.awt.Dimension(144, 28));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 8;
        jPanel6.add(idField, gridBagConstraints);

        passwordCheckbox.setText("Protect Using ID");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 24;
        gridBagConstraints.gridwidth = 3;
        jPanel6.add(passwordCheckbox, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 22;
        jPanel6.add(filler6, gridBagConstraints);

        editButton.setText("Edit Profile Information");
        editButton.setAlignmentX(0.5F);
        editButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel6.add(editButton, gridBagConstraints);

        jPanel1.add(jPanel6);

        jPanel8.setLayout(new java.awt.GridLayout(1, 0));

        doneButton.setFont(new java.awt.Font("sansserif", 1, 12)); // NOI18N
        doneButton.setText("Create Profile");
        doneButton.setEnabled(false);
        doneButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                doneButtonActionPerformed(evt);
            }
        });
        jPanel8.add(doneButton);

        jPanel1.add(jPanel8);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 588, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 388, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void editButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editButtonActionPerformed
        setEnabled(false);
        Profile newlyCreated = WebAccessor.createNewProfile();

        if (newlyCreated != null) { //successful profile update thru SAC (no close or error)
            profile = newlyCreated;
            updateInfoDisplay();
            doneButton.setEnabled(true);
            changed = true;
            System.out.println("changed");
        }
        else
            JOptionPane.showMessageDialog(null, "An error occurred during the update.",
                        "WebDriver error", JOptionPane.ERROR_MESSAGE);

        //focus stuff
        setEnabled(true);

        toFront();
        requestFocus();
        requestFocusInWindow();
    }//GEN-LAST:event_editButtonActionPerformed

    private void doneButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_doneButtonActionPerformed
        profile.setLocked(passwordCheckbox.isSelected());   //use checkbox to set if profile is protected

        int confirm = JOptionPane.YES_OPTION;
        if (changed && window.getProfile()!=null) {
            String oldName = window.getProfile().getField(0);
            confirm = JOptionPane.showConfirmDialog(null, "Are you sure? The previous profile \""
                    + oldName+ "\" and all customizations will be overwritten.", "Confirm Overwrite", JOptionPane.YES_NO_OPTION);
        }

        if (confirm == JOptionPane.YES_OPTION) {
            window.setProfile(profile); //put the new profile into the start window
            System.out.println("set as active: " + profile);
            window.setVisible(true); //make it visible
            window.requestFocus(); //make it on top
            dispose(); //get rid of this window
        }
    }//GEN-LAST:event_doneButtonActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        cancelButtonActionPerformed(null);
    }//GEN-LAST:event_formWindowClosing

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        int confirm = JOptionPane.YES_OPTION;
        if (changed)
            confirm = JOptionPane.showConfirmDialog(null, "Are you sure? The new"
                    + " profile you created will be lost.", "Confirm Cancel", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            window.setVisible(true);
            window.requestFocus();
        }
    }//GEN-LAST:event_cancelButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField birthdateField;
    private javax.swing.JButton cancelButton;
    private javax.swing.JButton doneButton;
    private javax.swing.JButton editButton;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler15;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private javax.swing.Box.Filler filler4;
    private javax.swing.Box.Filler filler5;
    private javax.swing.Box.Filler filler6;
    private javax.swing.JTextField gradeField;
    private javax.swing.JPasswordField idField;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JCheckBox passwordCheckbox;
    private javax.swing.JTextField schoolField;
    private javax.swing.JTextField usernameField;
    // End of variables declaration//GEN-END:variables
}
