/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.stu.fiit.gui.vylet_dashboard;

import java.awt.Color;
import java.util.logging.Level;
import javax.swing.JOptionPane;
import sk.stu.fiit.app.CustomLogger;
import sk.stu.fiit.app.user.Biometria;
import sk.stu.fiit.app.trip.Vylet;
import sk.stu.fiit.app.user.Vyletnik;
import sk.stu.fiit.gui.utils.CustomDialog;
import sk.stu.fiit.gui.utils.RoundedPanel;

/**
 *
 * @author dlhyl
 */
public class VyletBiometriaPanel extends RoundedPanel {
    private final VyletDashboardPanel m;
    private final Vylet v;
    private final Biometria b;
    /**
     * Creates new form VyletMapaPanel
     */
    public VyletBiometriaPanel(VyletDashboardPanel m, Vylet v) {
        super(50, new Color(144,30,57,180));
        this.m = m; 
        this.v = v;
        this.b=((Vyletnik)m.getHlavneOkno().getLoggedInPouzivatel()).getBiometria(v);
        setOpaque(false);
        initComponents();
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

        lblBack = new javax.swing.JLabel();
        lblTitle = new javax.swing.JLabel();
        pnlContent = new RoundedPanel(25, new Color(0,0,0,100));
        lblSteps = new javax.swing.JLabel();
        lblKm = new javax.swing.JLabel();
        btnAdd = new javax.swing.JButton();
        spinnerSteps = new javax.swing.JSpinner();
        spinnerKm = new javax.swing.JSpinner();

        setOpaque(false);
        setLayout(new java.awt.GridBagLayout());

        lblBack.setBackground(new java.awt.Color(255, 255, 255));
        lblBack.setFont(new java.awt.Font("Gill Sans MT", 1, 24)); // NOI18N
        lblBack.setForeground(new java.awt.Color(200, 200, 200));
        lblBack.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sk/stu/fiit/assets/icons/previous.png"))); // NOI18N
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("sk/stu/fiit/gui/bundle"); // NOI18N
        lblBack.setText(bundle.getString("SPÄŤ")); // NOI18N
        lblBack.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                lblBackMouseReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(25, 0, 25, 25);
        add(lblBack, gridBagConstraints);

        lblTitle.setFont(new java.awt.Font("Gill Sans MT", 1, 24)); // NOI18N
        lblTitle.setForeground(new java.awt.Color(255, 102, 102));
        lblTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTitle.setText(bundle.getString("BIOMETRICKÉ ÚDAJE Z VÝLETU")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.PAGE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(25, 0, 0, 0);
        add(lblTitle, gridBagConstraints);

        pnlContent.setOpaque(false);
        pnlContent.setLayout(new java.awt.GridBagLayout());

        lblSteps.setFont(new java.awt.Font("Gill Sans MT", 1, 24)); // NOI18N
        lblSteps.setForeground(new java.awt.Color(255, 102, 102));
        lblSteps.setText(bundle.getString("POČET KROKOV")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(30, 30, 0, 0);
        pnlContent.add(lblSteps, gridBagConstraints);

        lblKm.setFont(new java.awt.Font("Gill Sans MT", 1, 24)); // NOI18N
        lblKm.setForeground(new java.awt.Color(255, 102, 102));
        lblKm.setText(bundle.getString("POČET PREJDENÝCH KM")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 30, 0, 0);
        pnlContent.add(lblKm, gridBagConstraints);

        btnAdd.setBackground(new java.awt.Color(144, 30, 57));
        btnAdd.setFont(new java.awt.Font("Gill Sans MT", 1, 18)); // NOI18N
        btnAdd.setForeground(new java.awt.Color(255, 204, 204));
        btnAdd.setText(bundle.getString("PRIDAŤ ÚDAJE")); // NOI18N
        btnAdd.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btnAddMouseReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 30, 0);
        pnlContent.add(btnAdd, gridBagConstraints);

        spinnerSteps.setFont(new java.awt.Font("Gill Sans MT", 1, 22)); // NOI18N
        spinnerSteps.setModel(new javax.swing.SpinnerNumberModel(0, 0, null, 1));
        spinnerSteps.setValue(b.getKroky());
        spinnerSteps.setBorder(null);
        spinnerSteps.setPreferredSize(new java.awt.Dimension(180, 38));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(30, 0, 0, 30);
        pnlContent.add(spinnerSteps, gridBagConstraints);

        spinnerKm.setFont(new java.awt.Font("Gill Sans MT", 1, 22)); // NOI18N
        spinnerKm.setModel(new javax.swing.SpinnerNumberModel(0.0d, 0.0d, null, 1.0d));
        spinnerKm.setValue(b.getKm());
        spinnerKm.setEditor(new javax.swing.JSpinner.NumberEditor(spinnerKm, "0.00"));
        spinnerKm.setPreferredSize(new java.awt.Dimension(140, 38));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 30);
        pnlContent.add(spinnerKm, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.ipadx = 50;
        gridBagConstraints.ipady = 50;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(30, 30, 30, 30);
        add(pnlContent, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void lblBackMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblBackMouseReleased
        this.m.changeScreen("cardVyletDash");
    }//GEN-LAST:event_lblBackMouseReleased

    private void btnAddMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnAddMouseReleased
        ((Vyletnik)this.m.getHlavneOkno().getLoggedInPouzivatel()).setBiometria(v, new Biometria((int)spinnerSteps.getValue(), (double)spinnerKm.getValue()));
        this.m.changeScreen("cardVyletDash");
        new CustomDialog(this.getParent(), java.util.ResourceBundle.getBundle("sk/stu/fiit/gui/bundle").getString("message_pridana_biometria"), java.util.ResourceBundle.getBundle("sk/stu/fiit/gui/bundle").getString("OK"), JOptionPane.INFORMATION_MESSAGE);
        CustomLogger.getLogger().log(Level.INFO, "[VyletBiometria] pridana biometria; user:" + this.m.getHlavneOkno().getLoggedInPouzivatel().getLogin());
    }//GEN-LAST:event_btnAddMouseReleased

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JLabel lblBack;
    private javax.swing.JLabel lblKm;
    private javax.swing.JLabel lblSteps;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JPanel pnlContent;
    private javax.swing.JSpinner spinnerKm;
    private javax.swing.JSpinner spinnerSteps;
    // End of variables declaration//GEN-END:variables
}
