/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package main;

import student.Course;

/**
 *
 * @author Michael
 */
public class InfoPanel extends javax.swing.JPanel {

    private Course course;

    public InfoPanel(Course course) {
        this.course = course;
        initComponents();
        nameLabel.setText(course.getName());
        periodLabel.setText(String.format("Period %d", course.getPeriod()));
        estField.setText("" + course.getActualEstimate());
        calcField.setText(String.format("%.2f", course.calcActualGradeUsing(course.getMajorSplit())));
    }

    public double getCustomSplit() {
        return (double) splitSpinner.getValue();
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

        nameLabel = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        estField = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        calcField = new javax.swing.JTextField();
        periodLabel = new javax.swing.JLabel();
        splitSpinner = WindowUtil.makeSpinner(null,
            course.getMajorSplit(), .5, .9, .05, "0.00");

        setLayout(new java.awt.GridBagLayout());

        nameLabel.setFont(new java.awt.Font("sansserif", 1, 18)); // NOI18N
        nameLabel.setText("Very Long Name of Some AP Class of Some Sort");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new java.awt.Insets(19, 19, 8, 19);
        add(nameLabel, gridBagConstraints);

        jLabel2.setText("Estimated Average (according to SAC)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(19, 19, 19, 19);
        add(jLabel2, gridBagConstraints);

        estField.setEditable(false);
        estField.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        estField.setText("jTextField1");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(19, 19, 19, 19);
        add(estField, gridBagConstraints);

        jLabel3.setText("Major Grade Split Ratio");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(19, 19, 19, 19);
        add(jLabel3, gridBagConstraints);

        jLabel4.setText("Calculated Average Using Ratio");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(19, 19, 19, 19);
        add(jLabel4, gridBagConstraints);

        calcField.setEditable(false);
        calcField.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        calcField.setText("jTextField3");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(19, 19, 19, 19);
        add(calcField, gridBagConstraints);

        periodLabel.setText("Period 0");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new java.awt.Insets(0, 19, 19, 19);
        add(periodLabel, gridBagConstraints);

        splitSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                splitSpinnerStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(19, 19, 19, 19);
        add(splitSpinner, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void splitSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_splitSpinnerStateChanged
        calcField.setText(String.format("%.2f", course.calcActualGradeUsing((double)splitSpinner.getValue())));
    }//GEN-LAST:event_splitSpinnerStateChanged


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField calcField;
    private javax.swing.JTextField estField;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel nameLabel;
    private javax.swing.JLabel periodLabel;
    private javax.swing.JSpinner splitSpinner;
    // End of variables declaration//GEN-END:variables
}