/*
 * Copyright (C) 2014-2016 Jean-Christophe Malapert
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
package io.github.malapert.jwcs.crs.gui;

import io.github.malapert.jwcs.crs.Ecliptic;
import io.github.malapert.jwcs.crs.Equatorial;
import io.github.malapert.jwcs.datum.FK4;
import io.github.malapert.jwcs.datum.FK4NoEterms;
import io.github.malapert.jwcs.datum.FK5;
import io.github.malapert.jwcs.crs.Galactic;
import io.github.malapert.jwcs.datum.ICRS;
import io.github.malapert.jwcs.datum.J2000;
import io.github.malapert.jwcs.datum.CoordinateReferenceFrame.ReferenceFrame;
import io.github.malapert.jwcs.position.SkyPosition;
import io.github.malapert.jwcs.crs.AbstractCrs;
import io.github.malapert.jwcs.crs.AbstractCrs.CoordinateReferenceSystem;
import io.github.malapert.jwcs.crs.SuperGalactic;
import io.github.malapert.jwcs.utility.DMS;
import io.github.malapert.jwcs.utility.HMS;
import java.awt.BorderLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFrame;
import io.github.malapert.jwcs.datum.CoordinateReferenceFrame;
import io.github.malapert.jwcs.proj.exception.JWcsError;

/**
 *
 * @author malapert
 */
public class ConvertSelectionPanel extends javax.swing.JPanel {
    
    private final static String DEFAULT_PRECISION = "%.10f";
    
    private String precision = DEFAULT_PRECISION;

    /**
     * Creates new form ConvertSelectionPanel
     */
    public ConvertSelectionPanel() {
        initComponents();
        orginSkySystem.setModel(new DefaultComboBoxModel<>(CoordinateReferenceSystem.getCoordinateSystemArray()));
        targetSkySystem.setModel(new DefaultComboBoxModel<>(CoordinateReferenceSystem.getCoordinateSystemArray()));
        originRf.setModel(new DefaultComboBoxModel<>(ReferenceFrame.getRefenceFrameNametoArray()));
        targetRf.setModel(new DefaultComboBoxModel<>(ReferenceFrame.getRefenceFrameNametoArray()));
        setEnableReferenceFrame(CoordinateReferenceSystem.values()[0].hasCoordinateReferenceFrame(), true);
        setEnableReferenceFrame(CoordinateReferenceSystem.values()[0].hasCoordinateReferenceFrame(), false);
        setEnableReferenceFrameParameter(ReferenceFrame.getRefenceFrameNametoArray()[0], true);
        setEnableReferenceFrameParameter(ReferenceFrame.getRefenceFrameNametoArray()[0], false);
        this.errorMsg.setText("");
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSeparator1 = new javax.swing.JSeparator();
        orginSkySystem = new javax.swing.JComboBox<>();
        orginSkySLabel = new javax.swing.JLabel();
        originRfLabel = new javax.swing.JLabel();
        originRf = new javax.swing.JComboBox<>();
        originEquinoxLabel = new javax.swing.JLabel();
        originEquinox = new javax.swing.JTextField();
        originEpochLabel = new javax.swing.JLabel();
        originEpoch = new javax.swing.JTextField();
        targetSkySLabel = new javax.swing.JLabel();
        targetSkySystem = new javax.swing.JComboBox<>();
        targetRfLabel = new javax.swing.JLabel();
        targetRf = new javax.swing.JComboBox<>();
        targetEquinoxLabel = new javax.swing.JLabel();
        targetEquinox = new javax.swing.JTextField();
        targetEpochLabel = new javax.swing.JLabel();
        targetEpoch = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        originLong = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        originLat = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        targetLong = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        targetLat = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        convertButton = new javax.swing.JButton();
        errorMsg = new javax.swing.JLabel();
        sourceSexa = new javax.swing.JTextField();
        targetSexa = new javax.swing.JTextField();

        jSeparator1.setBackground(new java.awt.Color(247, 125, 25));
        jSeparator1.setForeground(new java.awt.Color(247, 125, 25));
        jSeparator1.setOpaque(true);

        orginSkySystem.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Equatorial", "Ecliptic", "Galactic", "Super Galactic" }));
        orginSkySystem.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                orginSkySystemItemStateChanged(evt);
            }
        });
        orginSkySystem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                orginSkySystemActionPerformed(evt);
            }
        });

        orginSkySLabel.setText("Origin sky system");

        originRfLabel.setText("Reference frame");

        originRf.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "ICRS", "FK5", "FK4", "FK4_NO_E", "J2000" }));
        originRf.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                originRfItemStateChanged(evt);
            }
        });

        originEquinoxLabel.setText("Equinox");
        originEquinoxLabel.setEnabled(false);

        originEquinox.setText("J2000.0");
        originEquinox.setEnabled(false);

        originEpochLabel.setText("Epoch");
        originEpochLabel.setEnabled(false);

        originEpoch.setText("B1950");
        originEpoch.setEnabled(false);
        originEpoch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                originEpochActionPerformed(evt);
            }
        });

        targetSkySLabel.setText("Target sky system");

        targetSkySystem.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Equatorial", "Ecliptic", "Galactic", "Super Galactic" }));
        targetSkySystem.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                targetSkySystemItemStateChanged(evt);
            }
        });

        targetRfLabel.setText("Reference frame");

        targetRf.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "ICRS", "FK5", "FK4", "FK4_NO_E", "J2000" }));
        targetRf.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                targetRfItemStateChanged(evt);
            }
        });

        targetEquinoxLabel.setText("Equinox");
        targetEquinoxLabel.setEnabled(false);

        targetEquinox.setText("J2000.0");
        targetEquinox.setEnabled(false);

        targetEpochLabel.setText("Epoch");
        targetEpochLabel.setEnabled(false);

        targetEpoch.setText("B1950");
        targetEpoch.setEnabled(false);

        jLabel9.setFont(new java.awt.Font("Ubuntu", 1, 14)); // NOI18N
        jLabel9.setText("Sky position :");

        originLong.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                originLongCaretUpdate(evt);
            }
        });
        originLong.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                originLongKeyReleased(evt);
            }
        });

        jLabel10.setText(",");

        originLat.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                originLatCaretUpdate(evt);
            }
        });
        originLat.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                originLatKeyReleased(evt);
            }
        });

        jLabel11.setFont(new java.awt.Font("Ubuntu", 1, 18)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(247, 125, 25));
        jLabel11.setText("=>");

        targetLong.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N
        targetLong.setEnabled(false);

        jLabel12.setText(",");

        targetLat.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N
        targetLat.setEnabled(false);

        jLabel13.setFont(new java.awt.Font("Ubuntu", 1, 20)); // NOI18N
        jLabel13.setText("                    Sky system converter");

        convertButton.setText("Convert");
        convertButton.setEnabled(false);
        convertButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                convertButtonActionPerformed(evt);
            }
        });

        errorMsg.setForeground(new java.awt.Color(237, 27, 27));

        sourceSexa.setEnabled(false);

        targetSexa.setEnabled(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(errorMsg, javax.swing.GroupLayout.PREFERRED_SIZE, 274, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(convertButton, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(orginSkySystem, 0, 293, Short.MAX_VALUE)
                                        .addComponent(orginSkySLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(originRfLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 277, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(originRf, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(originEquinoxLabel)
                                            .addComponent(originEpochLabel))
                                        .addGap(29, 29, 29)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(originEquinox, javax.swing.GroupLayout.DEFAULT_SIZE, 133, Short.MAX_VALUE)
                                            .addComponent(originEpoch))))
                                .addGap(49, 49, 49)
                                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(92, 92, 92)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(targetSkySLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(targetSkySystem, 0, 297, Short.MAX_VALUE)
                                            .addComponent(targetRfLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 235, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(targetRf, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(167, 167, 167)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(targetEquinoxLabel)
                                            .addComponent(targetEpochLabel))
                                        .addGap(30, 30, 30)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(targetEquinox)
                                            .addComponent(targetEpoch, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                        .addGap(98, 98, 98)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(targetSexa, javax.swing.GroupLayout.PREFERRED_SIZE, 291, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(targetLong, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(5, 5, 5)
                                                .addComponent(jLabel12)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(targetLat, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(1, 1, 1)
                                        .addComponent(originLong, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(6, 6, 6)
                                        .addComponent(jLabel10)
                                        .addGap(3, 3, 3)
                                        .addComponent(originLat, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(sourceSexa, javax.swing.GroupLayout.PREFERRED_SIZE, 226, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap(27, Short.MAX_VALUE))))
            .addGroup(layout.createSequentialGroup()
                .addGap(144, 144, 144)
                .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 392, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jLabel13)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(orginSkySLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(orginSkySystem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(27, 27, 27)
                            .addComponent(originRfLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(originRf, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(18, 18, 18)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(originEquinoxLabel)
                                .addComponent(originEquinox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGap(18, 18, 18)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(originEpochLabel)
                                .addComponent(originEpoch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGap(5, 5, 5))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(targetSkySLabel)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(targetSkySystem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(33, 33, 33)
                            .addComponent(targetRfLabel)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(targetRf, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(22, 22, 22)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(targetEquinoxLabel)
                                .addComponent(targetEquinox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGap(18, 18, 18)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(targetEpochLabel)
                                .addComponent(targetEpoch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addComponent(jSeparator1))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(originLong, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10)
                    .addComponent(originLat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11)
                    .addComponent(targetLong, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12)
                    .addComponent(targetLat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(sourceSexa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(targetSexa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 18, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(convertButton, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(errorMsg))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void orginSkySystemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_orginSkySystemActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_orginSkySystemActionPerformed

    private void originEpochActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_originEpochActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_originEpochActionPerformed

    private void orginSkySystemItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_orginSkySystemItemStateChanged
        try {
            if (evt.getStateChange() == java.awt.event.ItemEvent.SELECTED) {
                updateMenuBySkySystem(true);
            }
        } catch (RuntimeException ex) {
            this.errorMsg.setText(ex.getMessage());
        }
    }//GEN-LAST:event_orginSkySystemItemStateChanged

    private void originLongKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_originLongKeyReleased
        try {
            updateConvertButton();
        } catch (RuntimeException ex) {
            this.errorMsg.setText(ex.getMessage());
        }
    }//GEN-LAST:event_originLongKeyReleased

    private void originLatKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_originLatKeyReleased
        try {
            updateConvertButton();            
        } catch (RuntimeException ex) {
            this.errorMsg.setText(ex.getMessage());
        }
    }//GEN-LAST:event_originLatKeyReleased

    private void convertButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_convertButtonActionPerformed
        try {
            convertToTargetSkySystem();
            updateTargetSexagecimalCoordinates();
        } catch (RuntimeException ex) {
            this.errorMsg.setText(ex.getMessage());
        }
    }//GEN-LAST:event_convertButtonActionPerformed

    private void targetSkySystemItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_targetSkySystemItemStateChanged
        try {
            if (evt.getStateChange() == java.awt.event.ItemEvent.SELECTED) {
                updateMenuBySkySystem(false);
            }
            this.targetSexa.setText("");
            this.targetLong.setText("");
            this.targetLat.setText("");
        } catch (RuntimeException ex) {
            this.errorMsg.setText(ex.getMessage());
        }
    }//GEN-LAST:event_targetSkySystemItemStateChanged

    private void originRfItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_originRfItemStateChanged
        try {
            setEnableReferenceFrameParameter(String.valueOf(this.originRf.getSelectedItem()), true);
        } catch (RuntimeException ex) {
            this.errorMsg.setText(ex.getMessage());
        }
    }//GEN-LAST:event_originRfItemStateChanged

    private void targetRfItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_targetRfItemStateChanged
        try {
            setEnableReferenceFrameParameter(String.valueOf(this.targetRf.getSelectedItem()), false);
            this.targetSexa.setText("");
            this.targetLong.setText("");
            this.targetLat.setText("");
        } catch (RuntimeException ex) {
            this.errorMsg.setText(ex.getMessage());
        }
    }//GEN-LAST:event_targetRfItemStateChanged

    private void originLatCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_originLatCaretUpdate
        updateOriginSexagecimalCoordinates();
    }//GEN-LAST:event_originLatCaretUpdate

    private void originLongCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_originLongCaretUpdate
        updateOriginSexagecimalCoordinates();
    }//GEN-LAST:event_originLongCaretUpdate


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton convertButton;
    private javax.swing.JLabel errorMsg;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel orginSkySLabel;
    private javax.swing.JComboBox<String> orginSkySystem;
    private javax.swing.JTextField originEpoch;
    private javax.swing.JLabel originEpochLabel;
    private javax.swing.JTextField originEquinox;
    private javax.swing.JLabel originEquinoxLabel;
    private javax.swing.JTextField originLat;
    private javax.swing.JTextField originLong;
    private javax.swing.JComboBox<String> originRf;
    private javax.swing.JLabel originRfLabel;
    private javax.swing.JTextField sourceSexa;
    private javax.swing.JTextField targetEpoch;
    private javax.swing.JLabel targetEpochLabel;
    private javax.swing.JTextField targetEquinox;
    private javax.swing.JLabel targetEquinoxLabel;
    private javax.swing.JTextField targetLat;
    private javax.swing.JTextField targetLong;
    private javax.swing.JComboBox<String> targetRf;
    private javax.swing.JLabel targetRfLabel;
    private javax.swing.JTextField targetSexa;
    private javax.swing.JLabel targetSkySLabel;
    private javax.swing.JComboBox<String> targetSkySystem;
    // End of variables declaration//GEN-END:variables

    private void setEnableReferenceFrame(boolean hasReferenceFrame, boolean isOrigin) {
        if (isOrigin) {
            this.originRf.setEnabled(hasReferenceFrame);
            this.originRfLabel.setEnabled(hasReferenceFrame);
            this.originEpoch.setEnabled(hasReferenceFrame);
            this.originEpochLabel.setEnabled(hasReferenceFrame);
            this.originEquinox.setEnabled(hasReferenceFrame);
            this.originEquinoxLabel.setEnabled(hasReferenceFrame);
            setEnableReferenceFrameParameter(String.valueOf(this.originRf.getSelectedItem()), isOrigin);
        } else {
            this.targetRf.setEnabled(hasReferenceFrame);
            this.targetRfLabel.setEnabled(hasReferenceFrame);
            this.targetEpoch.setEnabled(hasReferenceFrame);
            this.targetEpochLabel.setEnabled(hasReferenceFrame);
            this.targetEquinox.setEnabled(hasReferenceFrame);
            this.targetEquinoxLabel.setEnabled(hasReferenceFrame);
            setEnableReferenceFrameParameter(String.valueOf(this.targetRf.getSelectedItem()), isOrigin);
        }
    }

    private void updateMenuBySkySystem(boolean isOrigin) {
        String skySystemName = (isOrigin) ? (String) orginSkySystem.getSelectedItem() : (String) targetSkySystem.getSelectedItem();
        CoordinateReferenceSystem skySystem = CoordinateReferenceSystem.valueOfByName(skySystemName);
        setEnableReferenceFrame(skySystem.hasCoordinateReferenceFrame(), isOrigin);
    }

    /**
     * Main method
     * @param args arguments
     */
    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> {
            createWindow();
        });
    }

    /**
     */
    public static void createWindow() {
        // create a new window
        ConvertSelectionPanel panel = new ConvertSelectionPanel();
        JFrame mapWindow = new JFrame("JWcs Converter");
        mapWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mapWindow.getContentPane().add(panel, BorderLayout.CENTER);
        mapWindow.pack();
        mapWindow.setLocationRelativeTo(null); // center on screen
        mapWindow.setVisible(true);
    }

    private void updateConvertButton() {
        if (this.originLong.getText() == null || this.originLat.getText() == null || this.originLong.getText().isEmpty() || this.originLat.getText().isEmpty()) {
            this.convertButton.setEnabled(false);
        } else {
            this.convertButton.setEnabled(true);
        }
    }
    
    private void updateOriginSexagecimalCoordinates() {
        String result = "";
        if(!this.originLong.getText().isEmpty()) {
            HMS hms = new HMS(this.originLong.getText());
            result+=hms.toString(true)+" ";
        }
        if(!this.originLat.getText().isEmpty()) {
            DMS dms = new DMS(this.originLat.getText());
            result+=dms.toString(true);
        }
        this.sourceSexa.setText(result);
    }    
    
    private void updateTargetSexagecimalCoordinates() {
        String result = "";
        if(!this.targetLong.getText().isEmpty()) {
            HMS hms = new HMS(this.targetLong.getText());
            result+=hms.toString(true)+" ";
        }
        if(!this.targetLat.getText().isEmpty()) {
            DMS dms = new DMS(this.targetLat.getText());
            result+=dms.toString(true);
        }
        this.targetSexa.setText(result);
    }     

    private void convertToTargetSkySystem() {
        String originSkySystemName = orginSkySystem.getSelectedItem().toString();
        CoordinateReferenceSystem originSkySystemC = CoordinateReferenceSystem.valueOfByName(originSkySystemName);
        CoordinateReferenceFrame originRefFrame = null;
        if (originSkySystemC.hasCoordinateReferenceFrame()) {
            String refFrameName = originRf.getSelectedItem().toString();
            String equinox = (originEquinox.isEnabled()) ? originEquinox.getText() : null;
            String epoch = (originEpoch.isEnabled()) ? originEpoch.getText() : null;
            originRefFrame = createReferenceFrame(ReferenceFrame.valueOfByName(refFrameName), equinox, epoch);
        }
        AbstractCrs originSkySystem = createSkySystem(originSkySystemC, originRefFrame);

        String targetSkySystemName = targetSkySystem.getSelectedItem().toString();
        CoordinateReferenceSystem targetSkySystemC = CoordinateReferenceSystem.valueOfByName(targetSkySystemName);
        CoordinateReferenceFrame targetRefFrame = null;
        if (targetSkySystemC.hasCoordinateReferenceFrame()) {
            String refFrameName = targetRf.getSelectedItem().toString();
            String equinox = (targetEquinox.isEnabled()) ? targetEquinox.getText() : null;
            String epoch = (targetEpoch.isEnabled()) ? targetEpoch.getText() : null;
            targetRefFrame = createReferenceFrame(ReferenceFrame.valueOfByName(refFrameName), equinox, epoch);
        }
        AbstractCrs tgetSkySystem = createSkySystem(targetSkySystemC, targetRefFrame);

        double longitude = Double.valueOf(originLong.getText());
        double latitude = Double.valueOf(originLat.getText());

        SkyPosition skyPosition = originSkySystem.convertTo(tgetSkySystem, longitude, latitude);
        targetLong.setText(String.format(getPrecision(),skyPosition.getLongitude()));
        targetLat.setText(String.format(getPrecision(),skyPosition.getLatitude()));
        this.errorMsg.setText("");
    }

    private CoordinateReferenceFrame createReferenceFrame(ReferenceFrame type, String equinox, String epoch) {
        CoordinateReferenceFrame result;
        switch (type) {
            case FK4:
                if(equinox.isEmpty() && epoch.isEmpty()) {
                    result = new FK4();
                } else if (epoch.isEmpty()) {
                    result = new FK4(equinox);
                } else {
                    result = new FK4(equinox, epoch);
                }
                break;
            case FK4_NO_E:
                if(equinox.isEmpty() && epoch.isEmpty()) {
                    result = new FK4NoEterms();
                } else if (epoch.isEmpty()) {
                    result = new FK4NoEterms(equinox);
                } else {
                    result = new FK4NoEterms(equinox, epoch);
                }
                break;
            case FK5:
                result = (equinox.isEmpty()) ? new FK5() : new FK5(equinox);
                break;
            case ICRS:
                result = new ICRS();
                break;
            case J2000:
                result = new J2000();
                break;
            default:
                throw new JWcsError("Reference frame " + type + " is not supported");
        }
        return result;
    }

    private AbstractCrs createSkySystem(CoordinateReferenceSystem name, CoordinateReferenceFrame refFrame) {
        AbstractCrs result;
        switch (name) {
            case ECLIPTIC:
                result = new Ecliptic(refFrame);
                break;
            case EQUATORIAL:
                result = new Equatorial(refFrame);
                break;
            case GALACTIC:
                result = new Galactic();
                break;
            case SUPER_GALACTIC:
                result = new SuperGalactic();
                break;
            default:
                throw new JWcsError("coordinate system " + name + " is not supported");
        }
        return result;
    }

    private void setEnableReferenceFrameParameter(String refFrameName, boolean isOrigin) {
        ReferenceFrame refFrame = ReferenceFrame.valueOfByName(refFrameName);
        switch (refFrame) {
            case FK4:
                if (isOrigin) {
                    this.originEquinoxLabel.setEnabled(true);
                    this.originEquinox.setEnabled(true);
                    this.originEpochLabel.setEnabled(true);
                    this.originEpoch.setEnabled(true);
                } else {
                    this.targetEquinoxLabel.setEnabled(true);
                    this.targetEquinox.setEnabled(true);
                    this.targetEpochLabel.setEnabled(true);
                    this.targetEpoch.setEnabled(true);
                }
                break;
            case FK4_NO_E:
                if (isOrigin) {
                    this.originEquinoxLabel.setEnabled(true);
                    this.originEquinox.setEnabled(true);
                    this.originEpochLabel.setEnabled(true);
                    this.originEpoch.setEnabled(true);
                } else {
                    this.targetEquinoxLabel.setEnabled(true);
                    this.targetEquinox.setEnabled(true);
                    this.targetEpochLabel.setEnabled(true);
                    this.targetEpoch.setEnabled(true);
                }
                break;
            case FK5:
                if (isOrigin) {
                    this.originEquinoxLabel.setEnabled(true);
                    this.originEquinox.setEnabled(true);
                    this.originEpochLabel.setEnabled(false);
                    this.originEpoch.setEnabled(false);
                } else {
                    this.targetEquinoxLabel.setEnabled(true);
                    this.targetEquinox.setEnabled(true);
                    this.targetEpochLabel.setEnabled(false);
                    this.targetEpoch.setEnabled(false);
                }
                break;
            case ICRS:
                if (isOrigin) {
                    this.originEquinoxLabel.setEnabled(false);
                    this.originEquinox.setEnabled(false);
                    this.originEpochLabel.setEnabled(false);
                    this.originEpoch.setEnabled(false);
                } else {
                    this.targetEquinoxLabel.setEnabled(false);
                    this.targetEquinox.setEnabled(false);
                    this.targetEpochLabel.setEnabled(false);
                    this.targetEpoch.setEnabled(false);
                }
                break;
            case J2000:
                if (isOrigin) {
                    this.originEquinoxLabel.setEnabled(false);
                    this.originEquinox.setEnabled(false);
                    this.originEpochLabel.setEnabled(false);
                    this.originEpoch.setEnabled(false);
                } else {
                    this.targetEquinoxLabel.setEnabled(false);
                    this.targetEquinox.setEnabled(false);
                    this.targetEpochLabel.setEnabled(false);
                    this.targetEpoch.setEnabled(false);
                }
                break;
            default:
                throw new IllegalArgumentException("The reference fame " + refFrameName + " is not supported");
        }
    }

    /**
     * @return the precision
     */
    public String getPrecision() {
        return precision;
    }

    /**
     * @param precision the precision to set
     */
    public void setPrecision(String precision) {
        this.precision = precision;
    }

}
