/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.stu.fiit.gui.dashboard;

import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import sk.stu.fiit.app.Potulkor;
import sk.stu.fiit.app.user.Pouzivatel;
import sk.stu.fiit.app.user.Vyletnik;
import sk.stu.fiit.gui.MainWindow;
import sk.stu.fiit.gui.utils.CustomScrollBarUI;
import sk.stu.fiit.gui.utils.RoundedImage;
import sk.stu.fiit.gui.utils.RoundedPanel;
import sk.stu.fiit.gui.utils.CellRenderers.UcastniciCellRenderer;

/**
 *
 * @author dlhyl
 */
public class RebricekPanel extends javax.swing.JPanel {
    private final MainWindow main;
    private final ArrayList<Vyletnik> vyletnikZoznam;
    /**
     * Creates new form RebricekPanel
     */
    public RebricekPanel(MainWindow m) {
        this.main = m;
        this.vyletnikZoznam = m.getPotulkor().getRebricekVyletnikov();
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

        pnlTitle = new RoundedPanel(20, new Color(101, 21, 40, 180));
        lblTitle = new javax.swing.JLabel();
        pnlRebricek = new RoundedPanel(50, new Color(144,30,57,180));
        lblBack = new javax.swing.JLabel();
        pnlOverview = new javax.swing.JPanel();
        lblIcon = new javax.swing.JLabel();
        lblPosition = new javax.swing.JLabel();
        lblPoints = new javax.swing.JLabel();
        lblName = new javax.swing.JLabel();
        pnlList = new RoundedPanel(50, new Color(0,0,0,100));
        scrollUsers = new javax.swing.JScrollPane();
        DefaultListModel dlm = new DefaultListModel();
        for (Vyletnik v : this.vyletnikZoznam)
        dlm.addElement(v);
        listUsers = new javax.swing.JList<>(dlm);

        setOpaque(false);
        setLayout(new java.awt.GridBagLayout());

        pnlTitle.setMaximumSize(new java.awt.Dimension(0, 0));
        pnlTitle.setMinimumSize(new java.awt.Dimension(0, 0));
        pnlTitle.setOpaque(false);
        pnlTitle.setPreferredSize(new java.awt.Dimension(0, 0));
        pnlTitle.setLayout(new java.awt.GridBagLayout());

        lblTitle.setFont(new java.awt.Font("Gill Sans MT", 1, 24)); // NOI18N
        lblTitle.setForeground(new java.awt.Color(255, 204, 204));
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("sk/stu/fiit/gui/bundle"); // NOI18N
        lblTitle.setText(bundle.getString("REBRÍČEK")); // NOI18N
        lblTitle.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 25, 0, 0);
        pnlTitle.add(lblTitle, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 300;
        gridBagConstraints.ipady = 50;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 0.4;
        gridBagConstraints.insets = new java.awt.Insets(80, 100, -1, 100);
        add(pnlTitle, gridBagConstraints);

        pnlRebricek.setOpaque(false);
        pnlRebricek.setLayout(new java.awt.GridBagLayout());

        lblBack.setBackground(new java.awt.Color(255, 255, 255));
        lblBack.setFont(new java.awt.Font("Gill Sans MT", 1, 24)); // NOI18N
        lblBack.setForeground(new java.awt.Color(200, 200, 200));
        lblBack.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblBack.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sk/stu/fiit/assets/icons/previous.png"))); // NOI18N
        lblBack.setText(bundle.getString("SPÄŤ")); // NOI18N
        lblBack.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                lblBackMouseReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(25, 0, 0, 25);
        pnlRebricek.add(lblBack, gridBagConstraints);

        pnlOverview.setMinimumSize(new java.awt.Dimension(0, 0));
        pnlOverview.setOpaque(false);
        pnlOverview.setPreferredSize(new java.awt.Dimension(0, 0));
        pnlOverview.setLayout(new java.awt.GridBagLayout());

        ImageIcon i = ((Vyletnik)main.getLoggedInPouzivatel()).getObrazok();
        if (i == null) i = new ImageIcon(getClass().getResource("/sk/stu/fiit/assets/icons/profile-user-256.png"));
        lblIcon.setIcon(RoundedImage.getImage(i, 100));
        lblIcon.setMaximumSize(new java.awt.Dimension(100, 100));
        lblIcon.setMinimumSize(new java.awt.Dimension(100, 100));
        lblIcon.setPreferredSize(new java.awt.Dimension(100, 100));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.PAGE_END;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 0.7;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        pnlOverview.add(lblIcon, gridBagConstraints);

        lblPosition.setFont(new java.awt.Font("Gill Sans MT", 1, 28)); // NOI18N
        lblPosition.setForeground(new java.awt.Color(255, 204, 204));
        int position = this.vyletnikZoznam.indexOf((Vyletnik)this.main.getLoggedInPouzivatel());
        lblPosition.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblPosition.setText(position==-1?"":"#"+String.valueOf(position+1));
        lblPosition.setMaximumSize(new java.awt.Dimension(0, 35));
        lblPosition.setMinimumSize(new java.awt.Dimension(0, 35));
        lblPosition.setPreferredSize(new java.awt.Dimension(0, 35));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        pnlOverview.add(lblPosition, gridBagConstraints);

        lblPoints.setFont(new java.awt.Font("Gill Sans MT", 1, 28)); // NOI18N
        lblPoints.setForeground(new java.awt.Color(255, 204, 204));
        lblPoints.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblPoints.setText(String.valueOf(((Vyletnik)main.getLoggedInPouzivatel()).getStats().getPoints())+java.util.ResourceBundle.getBundle("sk/stu/fiit/gui/bundle").getString(" BODOV"));
        lblPoints.setMaximumSize(new java.awt.Dimension(0, 0));
        lblPoints.setMinimumSize(new java.awt.Dimension(0, 0));
        lblPoints.setPreferredSize(new java.awt.Dimension(0, 35));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        pnlOverview.add(lblPoints, gridBagConstraints);

        lblName.setFont(new java.awt.Font("Gill Sans MT", 1, 24)); // NOI18N
        lblName.setForeground(new java.awt.Color(255, 204, 204));
        lblName.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblName.setText(((Vyletnik)this.main.getLoggedInPouzivatel()).getMeno());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.PAGE_START;
        gridBagConstraints.weighty = 0.3;
        pnlOverview.add(lblName, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 50;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.3;
        pnlRebricek.add(pnlOverview, gridBagConstraints);

        pnlList.setMinimumSize(new java.awt.Dimension(0, 0));
        pnlList.setOpaque(false);
        pnlList.setPreferredSize(new java.awt.Dimension(0, 0));
        pnlList.setLayout(new java.awt.GridBagLayout());

        scrollUsers.getViewport().setOpaque(false);
        scrollUsers.setViewportBorder(null);
        scrollUsers.getVerticalScrollBar().setPreferredSize(new Dimension(10, 0));
        scrollUsers.getVerticalScrollBar().setUI(new CustomScrollBarUI());
        scrollUsers.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        scrollUsers.setOpaque(false);

        listUsers.setOpaque(false);
        listUsers.setCellRenderer(new UcastniciCellRenderer(vyletnikZoznam,60));
        scrollUsers.setViewportView(listUsers);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(15, 15, 15, 15);
        pnlList.add(scrollUsers, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.7;
        gridBagConstraints.insets = new java.awt.Insets(10, 25, 25, 25);
        pnlRebricek.add(pnlList, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 100, 100, 100);
        add(pnlRebricek, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void lblBackMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblBackMouseReleased
        this.main.changeDashboardScreen("cardDashboardMenu");
    }//GEN-LAST:event_lblBackMouseReleased
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel lblBack;
    private javax.swing.JLabel lblIcon;
    private javax.swing.JLabel lblName;
    private javax.swing.JLabel lblPoints;
    private javax.swing.JLabel lblPosition;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JList<Vyletnik> listUsers;
    private javax.swing.JPanel pnlList;
    private javax.swing.JPanel pnlOverview;
    private javax.swing.JPanel pnlRebricek;
    private javax.swing.JPanel pnlTitle;
    private javax.swing.JScrollPane scrollUsers;
    // End of variables declaration//GEN-END:variables
}