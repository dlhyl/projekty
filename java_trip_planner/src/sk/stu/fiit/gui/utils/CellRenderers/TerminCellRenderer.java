/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.stu.fiit.gui.utils.CellRenderers;

import java.awt.Color;
import java.awt.Component;
import java.time.format.DateTimeFormatter;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import sk.stu.fiit.app.trip.KalendarDostupnosti.NajlepsiTermin;
import sk.stu.fiit.gui.utils.RoundedPanel;

/**
 *
 * @author dlhyl
 */
public class TerminCellRenderer extends javax.swing.JPanel implements ListCellRenderer<NajlepsiTermin>{
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    /**
     * Creates new form ItinerarCellRenderer
     */
    public TerminCellRenderer() {
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

        pnlContent = new RoundedPanel(25, new Color(101, 21, 40));
        lblDate = new javax.swing.JLabel();
        lblMatch = new javax.swing.JLabel();

        setMinimumSize(new java.awt.Dimension(0, 50));
        setOpaque(false);
        setPreferredSize(new java.awt.Dimension(0, 40));
        setLayout(new java.awt.GridBagLayout());

        pnlContent.setMinimumSize(new java.awt.Dimension(0, 50));
        pnlContent.setOpaque(false);
        pnlContent.setPreferredSize(new java.awt.Dimension(0, 40));
        pnlContent.setLayout(new java.awt.GridBagLayout());

        lblDate.setFont(new java.awt.Font("Gill Sans MT", 1, 18)); // NOI18N
        lblDate.setForeground(new java.awt.Color(255, 204, 204));
        lblDate.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblDate.setText("21.2.2021");
        lblDate.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
        lblDate.setMinimumSize(new java.awt.Dimension(0, 0));
        lblDate.setPreferredSize(new java.awt.Dimension(0, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 3.0;
        gridBagConstraints.weighty = 3.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        pnlContent.add(lblDate, gridBagConstraints);

        lblMatch.setFont(new java.awt.Font("Gill Sans MT", 1, 20)); // NOI18N
        lblMatch.setForeground(new java.awt.Color(255, 102, 102));
        lblMatch.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblMatch.setText("100%");
        lblMatch.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
        lblMatch.setMinimumSize(new java.awt.Dimension(0, 0));
        lblMatch.setPreferredSize(new java.awt.Dimension(50, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weighty = 3.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 10);
        pnlContent.add(lblMatch, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 3, 0);
        add(pnlContent, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

   


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel lblDate;
    private javax.swing.JLabel lblMatch;
    private javax.swing.JPanel pnlContent;
    // End of variables declaration//GEN-END:variables

    @Override
    public Component getListCellRendererComponent(JList<? extends NajlepsiTermin> list, NajlepsiTermin value, int index, boolean isSelected, boolean cellHasFocus) {
        if (value.getDatumOd() != null && value.getDatumDo() != null) {
            lblDate.setText(value.getDatumOd().format(dtf) +" - "+value.getDatumDo().format(dtf));
            lblMatch.setText(String.valueOf(value.getZhoda())+"%");
        } else {
            lblDate.setText("");
            lblMatch.setText("");
        }
        if (isSelected) {
            ((RoundedPanel)pnlContent).setBackgroundColor(new Color(144, 30, 57));
        } else {
            ((RoundedPanel)pnlContent).setBackgroundColor(new Color(101, 21, 40));
        }
        return this;
    }
}