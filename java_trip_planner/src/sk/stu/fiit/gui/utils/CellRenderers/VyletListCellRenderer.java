/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.stu.fiit.gui.utils.CellRenderers;

import java.awt.Color;
import java.awt.Component;
import java.text.DecimalFormat;
import java.time.LocalDate;
import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import sk.stu.fiit.app.user.Biometria;
import sk.stu.fiit.app.trip.Vylet;
import sk.stu.fiit.app.user.Vyletnik;
import sk.stu.fiit.gui.utils.RoundedPanel;

/**
 *
 * @author dlhyl
 */
public class VyletListCellRenderer extends JPanel implements ListCellRenderer {
    private final Vyletnik vyletnik;
    /**
     * Creates new form VyletListCellRenderer
     */
    public VyletListCellRenderer(Vyletnik vyletnik) {
        this.vyletnik = vyletnik;
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

        pnlDark = new RoundedPanel(50, new Color(0,0,0,80));
        pnlContent = new RoundedPanel(50, new Color(101, 21, 40 , 180));
        lblNazov = new javax.swing.JLabel();
        lblDatum = new javax.swing.JLabel();
        lblKroky = new javax.swing.JLabel();
        lblIcon = new javax.swing.JLabel();
        lblKm = new javax.swing.JLabel();
        lblMiesto = new javax.swing.JLabel();

        setMinimumSize(new java.awt.Dimension(0, 120));
        setOpaque(false);
        setPreferredSize(new java.awt.Dimension(0, 120));
        setLayout(new java.awt.GridBagLayout());

        pnlDark.setVisible(false);
        pnlDark.setEnabled(false);
        pnlDark.setFocusable(false);
        pnlDark.setOpaque(false);
        pnlDark.setPreferredSize(new java.awt.Dimension(0, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        add(pnlDark, gridBagConstraints);

        pnlContent.setOpaque(false);
        pnlContent.setPreferredSize(new java.awt.Dimension(0, 0));
        pnlContent.setLayout(new java.awt.GridBagLayout());

        lblNazov.setFont(new java.awt.Font("Gill Sans MT", 1, 28)); // NOI18N
        lblNazov.setForeground(new java.awt.Color(255, 102, 102));
        lblNazov.setText("Bratislava");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 30, 0, 0);
        pnlContent.add(lblNazov, gridBagConstraints);

        lblDatum.setFont(new java.awt.Font("Gill Sans MT", 0, 20)); // NOI18N
        lblDatum.setForeground(new java.awt.Color(255, 204, 204));
        lblDatum.setText("16/2/2021 - 17/2/2023");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(0, 30, 10, 0);
        pnlContent.add(lblDatum, gridBagConstraints);

        lblKroky.setFont(new java.awt.Font("Arial", 0, 20)); // NOI18N
        lblKroky.setForeground(new java.awt.Color(255, 204, 204));
        lblKroky.setText("100 krokov");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHEAST;
        gridBagConstraints.weighty = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 3, 0);
        pnlContent.add(lblKroky, gridBagConstraints);

        lblIcon.setPreferredSize(new java.awt.Dimension(24, 24));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(15, 10, 0, 15);
        pnlContent.add(lblIcon, gridBagConstraints);

        lblKm.setFont(new java.awt.Font("Arial", 0, 20)); // NOI18N
        lblKm.setForeground(new java.awt.Color(255, 204, 204));
        lblKm.setText("100km");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.weighty = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(3, 10, 0, 0);
        pnlContent.add(lblKm, gridBagConstraints);

        lblMiesto.setFont(new java.awt.Font("Gill Sans MT", 0, 20)); // NOI18N
        lblMiesto.setForeground(new java.awt.Color(255, 204, 204));
        lblMiesto.setText("Trencin");
        lblMiesto.setToolTipText("");
        lblMiesto.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        lblMiesto.setPreferredSize(new java.awt.Dimension(63, 30));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(5, 30, 5, 0);
        pnlContent.add(lblMiesto, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        add(pnlContent, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        Vylet v = (Vylet)value;
        if (v.getTrasa().getMiestaZaujmu().isEmpty()) {
            lblMiesto.setVisible(false);
        } else {
            String name1 = v.getTrasa().getMiestaZaujmu().get(0).getName();
            String name2 = v.getTrasa().getMiestaZaujmu().get(v.getTrasa().getMiestaZaujmu().size()-1).getName();
            lblMiesto.setVisible(true);
            lblMiesto.setText(name1+" > "+name2);
        }
        
        lblNazov.setText(v.getNazov());
        lblNazov.setIcon(new ImageIcon(v.getTyp().getIconURL()));
        lblDatum.setText(v.getBeginDate().toString() +" - "+v.getEndDate().toString());
        lblIcon.setIcon(null);
        lblIcon.setText(""); lblKm.setText(""); lblKroky.setText("");
        pnlDark.setVisible(false);
        if (v.isDokonceny()) {
            lblIcon.setIcon(new ImageIcon(getClass().getResource("/sk/stu/fiit/assets/icons/check.png")));
        }
        if (v.isZruseny()) { 
            lblIcon.setIcon(new ImageIcon(getClass().getResource("/sk/stu/fiit/assets/icons/cancel.png"))); 
            pnlDark.setVisible(true);
        }
        if (LocalDate.now().isAfter(v.getEndDate()) && !v.isZruseny()) {
            ((RoundedPanel)pnlContent).setBackgroundColor(new Color(80, 15, 25 , 180));
            Biometria b = vyletnik.getBiometria(v);
            if (b != null) {
                lblKroky.setText(String.valueOf(b.getKroky())+java.util.ResourceBundle.getBundle("sk/stu/fiit/gui/bundle").getString(" KROKOV"));
                lblKm.setText(new DecimalFormat("0.00").format(b.getKm())+" km");
            }
        } else {
            ((RoundedPanel)pnlContent).setBackgroundColor(new Color(101, 21, 40 , 180));
        }
        return this;
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel lblDatum;
    private javax.swing.JLabel lblIcon;
    private javax.swing.JLabel lblKm;
    private javax.swing.JLabel lblKroky;
    private javax.swing.JLabel lblMiesto;
    private javax.swing.JLabel lblNazov;
    private javax.swing.JPanel pnlContent;
    private javax.swing.JPanel pnlDark;
    // End of variables declaration//GEN-END:variables
}
