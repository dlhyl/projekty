/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.stu.fiit.gui.vylet_dashboard;

import sk.stu.fiit.gui.utils.CellRenderers.DostupnostCellRenderer;
import sk.stu.fiit.gui.utils.CellRenderers.TerminCellRenderer;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import sk.stu.fiit.app.CustomLogger;
import sk.stu.fiit.app.trip.DostupnostVyletnika;
import sk.stu.fiit.app.trip.KalendarDostupnosti.NajlepsiTermin;
import sk.stu.fiit.app.trip.Vylet;
import sk.stu.fiit.app.user.Vyletnik;
import sk.stu.fiit.gui.utils.CalendarPanel;
import sk.stu.fiit.gui.utils.CalendarPanel.CalendarUpdate;
import sk.stu.fiit.gui.utils.CustomDialog;
import sk.stu.fiit.gui.utils.CustomScrollBarUI;
import sk.stu.fiit.gui.utils.RoundedPanel;

/**
 *
 * @author dlhyl
 */
public class VyletKalendarPanel extends RoundedPanel implements CalendarUpdate {
    private final VyletDashboardPanel m;
    private final Vylet v;
    private final Vyletnik vyletnik;
    private LocalDate dtmOd = null;
    private LocalDate dtmDo = null;
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    /**
     * Creates new form VyletMapaPanel
     */
    public VyletKalendarPanel(VyletDashboardPanel m, Vylet v) {
        super(50, new Color(144,30,57,180));
        this.m = m; 
        this.v = v;
        this.vyletnik = (Vyletnik)this.m.getHlavneOkno().getLoggedInPouzivatel();
        setOpaque(false);
        initComponents();
        
        DostupnostVyletnika dv = v.getDostupnost().getDostupnostVyletnika(vyletnik);
        if (dv != null) {
            ((CalendarPanel)pnlFrom).setSelectedValue(dv.getDatumOd());
            ((CalendarPanel)pnlTo).setSelectedValue(dv.getDatumDo());
            pnlFrom.updateUI();
            pnlTo.updateUI();
        }
        checkControls();
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
        lblTo = new javax.swing.JLabel();
        lblFrom = new javax.swing.JLabel();
        lblDo = new javax.swing.JLabel();
        lblOd = new javax.swing.JLabel();
        pnlTo = new CalendarPanel(this,38,24);
        pnlFrom = new CalendarPanel(this,38,24);
        lblYourDate = new javax.swing.JLabel();
        lblTripDate = new javax.swing.JLabel();
        btnSaveDate = new javax.swing.JButton();
        controls = new RoundedPanel(25, new Color(0,0,0,100));
        lblDate = new javax.swing.JLabel();
        btnChooseSuggested = new javax.swing.JButton();
        scrollSuggestedDates = new javax.swing.JScrollPane();
        DefaultListModel dlm = new DefaultListModel();
        for (NajlepsiTermin nt : v.getDostupnost().getNajTerminy().getNavrhyTerminov(10))
        dlm.addElement(nt);
        listSuggestedDates = new javax.swing.JList<>(dlm);
        lblMatch = new javax.swing.JLabel();
        lblSuggestedTitle = new javax.swing.JLabel();
        lblAvailabilityTitle = new javax.swing.JLabel();
        scrollAvailability = new javax.swing.JScrollPane();
        DefaultListModel dlm2 = new DefaultListModel();
        for (DostupnostVyletnika dv : v.getDostupnost().getDostupnostiVyletnikov())
        dlm2.addElement(dv);
        listAvailabilityDates = new javax.swing.JList<>(dlm2);
        lblPreferredDate = new javax.swing.JLabel();
        lblyour = new javax.swing.JLabel();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 32767));
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 32767));

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
        lblTitle.setText(bundle.getString("KALENDÁR DOSTUPNOSTI")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.PAGE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(25, 0, 0, 0);
        add(lblTitle, gridBagConstraints);

        lblTo.setFont(new java.awt.Font("Gill Sans MT", 1, 20)); // NOI18N
        lblTo.setForeground(new java.awt.Color(255, 204, 204));
        lblTo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTo.setPreferredSize(new java.awt.Dimension(0, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 0.3;
        gridBagConstraints.weighty = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 25);
        add(lblTo, gridBagConstraints);

        lblFrom.setFont(new java.awt.Font("Gill Sans MT", 1, 20)); // NOI18N
        lblFrom.setForeground(new java.awt.Color(255, 204, 204));
        lblFrom.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblFrom.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        lblFrom.setPreferredSize(new java.awt.Dimension(0, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 0.3;
        gridBagConstraints.weighty = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(5, 25, 0, 0);
        add(lblFrom, gridBagConstraints);

        lblDo.setFont(new java.awt.Font("Gill Sans MT", 1, 18)); // NOI18N
        lblDo.setForeground(new java.awt.Color(255, 204, 204));
        lblDo.setText(bundle.getString("DO")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.PAGE_END;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 30);
        add(lblDo, gridBagConstraints);

        lblOd.setFont(new java.awt.Font("Gill Sans MT", 1, 18)); // NOI18N
        lblOd.setForeground(new java.awt.Color(255, 204, 204));
        lblOd.setText(bundle.getString("OD")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.PAGE_END;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 25, 5, 0);
        add(lblOd, gridBagConstraints);

        pnlTo.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 30);
        add(pnlTo, gridBagConstraints);

        pnlFrom.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.insets = new java.awt.Insets(0, 25, 0, 0);
        add(pnlFrom, gridBagConstraints);

        lblYourDate.setFont(new java.awt.Font("Gill Sans MT", 1, 24)); // NOI18N
        lblYourDate.setForeground(new java.awt.Color(255, 204, 204));
        DostupnostVyletnika d = this.v.getDostupnost().getDostupnostVyletnika(vyletnik);
        lblYourDate.setText(d==null ? "Nezadali ste termín" : d.getDatumString());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.3;
        gridBagConstraints.weighty = 0.2;
        gridBagConstraints.insets = new java.awt.Insets(0, 25, 0, 0);
        add(lblYourDate, gridBagConstraints);

        lblTripDate.setFont(new java.awt.Font("Gill Sans MT", 1, 24)); // NOI18N
        lblTripDate.setForeground(new java.awt.Color(255, 102, 102));
        lblTripDate.setText(this.v.getDatumString()+" ("+String.valueOf(v.getPocet_dni())+java.util.ResourceBundle.getBundle("sk/stu/fiit/gui/bundle").getString("DNI")+")");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.3;
        gridBagConstraints.weighty = 0.2;
        gridBagConstraints.insets = new java.awt.Insets(0, 25, 0, 0);
        add(lblTripDate, gridBagConstraints);

        btnSaveDate.setBackground(new java.awt.Color(144, 30, 57));
        btnSaveDate.setFont(new java.awt.Font("Gill Sans MT", 1, 16)); // NOI18N
        btnSaveDate.setForeground(new java.awt.Color(255, 204, 204));
        btnSaveDate.setText(bundle.getString("ULOŽIŤ")); // NOI18N
        btnSaveDate.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btnSaveDateMouseReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weighty = 0.2;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 30);
        add(btnSaveDate, gridBagConstraints);

        controls.setOpaque(false);
        controls.setLayout(new java.awt.GridBagLayout());

        lblDate.setFont(new java.awt.Font("Gill Sans MT", 1, 18)); // NOI18N
        lblDate.setForeground(new java.awt.Color(255, 204, 204));
        lblDate.setText(bundle.getString("TERMÍN")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 8.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 15, 0, 0);
        controls.add(lblDate, gridBagConstraints);

        if (!v.isVeduci(vyletnik)) btnChooseSuggested.setVisible(false);
        btnChooseSuggested.setBackground(new java.awt.Color(144, 30, 57));
        btnChooseSuggested.setFont(new java.awt.Font("Gill Sans MT", 1, 14)); // NOI18N
        btnChooseSuggested.setForeground(new java.awt.Color(255, 204, 204));
        btnChooseSuggested.setText(bundle.getString("VYBRAŤ TERMÍN")); // NOI18N
        btnChooseSuggested.setPreferredSize(new java.awt.Dimension(125, 30));
        btnChooseSuggested.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btnChooseSuggestedMouseReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.weighty = 0.2;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        controls.add(btnChooseSuggested, gridBagConstraints);

        scrollSuggestedDates.setOpaque(false);
        scrollSuggestedDates.getViewport().setOpaque(false);
        scrollSuggestedDates.setViewportBorder(null);
        scrollSuggestedDates.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        scrollSuggestedDates.setMinimumSize(new java.awt.Dimension(100, 80));
        scrollSuggestedDates.setOpaque(false);
        scrollSuggestedDates.setPreferredSize(new java.awt.Dimension(100, 0));
        scrollSuggestedDates.getVerticalScrollBar().setPreferredSize(new Dimension(5, 0));
        scrollSuggestedDates.getVerticalScrollBar().setUI(new CustomScrollBarUI());

        listSuggestedDates.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        listSuggestedDates.setOpaque(false);
        listSuggestedDates.setVisibleRowCount(4);
        listSuggestedDates.setCellRenderer(new TerminCellRenderer());
        scrollSuggestedDates.setViewportView(listSuggestedDates);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 3.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 15, 5, 15);
        controls.add(scrollSuggestedDates, gridBagConstraints);

        lblMatch.setFont(new java.awt.Font("Gill Sans MT", 1, 18)); // NOI18N
        lblMatch.setForeground(new java.awt.Color(255, 102, 102));
        lblMatch.setText(bundle.getString("ZHODA (%)")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 0.3;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 15);
        controls.add(lblMatch, gridBagConstraints);

        lblSuggestedTitle.setFont(new java.awt.Font("Gill Sans MT", 1, 18)); // NOI18N
        lblSuggestedTitle.setForeground(new java.awt.Color(255, 204, 204));
        lblSuggestedTitle.setText(bundle.getString("NAJVHODNEJŠIE TERMÍNY")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 0.3;
        gridBagConstraints.insets = new java.awt.Insets(5, 15, 0, 15);
        controls.add(lblSuggestedTitle, gridBagConstraints);

        lblAvailabilityTitle.setFont(new java.awt.Font("Gill Sans MT", 1, 18)); // NOI18N
        lblAvailabilityTitle.setForeground(new java.awt.Color(255, 204, 204));
        lblAvailabilityTitle.setText(bundle.getString("DOSTUPNOSTI VÝLETNÍKOV")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 0.3;
        gridBagConstraints.insets = new java.awt.Insets(5, 15, 0, 15);
        controls.add(lblAvailabilityTitle, gridBagConstraints);

        scrollAvailability.setOpaque(false);
        scrollAvailability.getViewport().setOpaque(false);
        scrollAvailability.setViewportBorder(null);
        scrollAvailability.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        scrollAvailability.setMinimumSize(new java.awt.Dimension(100, 80));
        scrollAvailability.setOpaque(false);
        scrollAvailability.setPreferredSize(new java.awt.Dimension(100, 0));
        scrollAvailability.getVerticalScrollBar().setPreferredSize(new Dimension(5, 0));
        scrollAvailability.getVerticalScrollBar().setUI(new CustomScrollBarUI());

        listAvailabilityDates.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        listAvailabilityDates.setOpaque(false);
        listAvailabilityDates.setVisibleRowCount(4);
        listAvailabilityDates.setCellRenderer(new DostupnostCellRenderer());
        scrollAvailability.setViewportView(listAvailabilityDates);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 3.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 15, 15, 15);
        controls.add(scrollAvailability, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 2.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 25, 25);
        add(controls, gridBagConstraints);

        lblPreferredDate.setFont(new java.awt.Font("Gill Sans MT", 1, 24)); // NOI18N
        lblPreferredDate.setForeground(new java.awt.Color(255, 102, 102));
        lblPreferredDate.setText(bundle.getString("PREFEROVANÝ DÁTUM")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.2;
        gridBagConstraints.weighty = 0.2;
        gridBagConstraints.insets = new java.awt.Insets(0, 25, 0, 0);
        add(lblPreferredDate, gridBagConstraints);

        lblyour.setFont(new java.awt.Font("Gill Sans MT", 1, 24)); // NOI18N
        lblyour.setForeground(new java.awt.Color(255, 204, 204));
        lblyour.setText(bundle.getString("VÁŠ TERMÍN")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.2;
        gridBagConstraints.weighty = 0.2;
        gridBagConstraints.insets = new java.awt.Insets(0, 25, 0, 0);
        add(lblyour, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.6;
        gridBagConstraints.weighty = 0.4;
        add(filler1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.weightx = 0.6;
        gridBagConstraints.weighty = 0.5;
        add(filler2, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void lblBackMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblBackMouseReleased
        this.m.changeScreen("cardVyletDash");          
    }//GEN-LAST:event_lblBackMouseReleased

    private void btnSaveDateMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnSaveDateMouseReleased
        if (this.dtmDo == null || this.dtmOd == null) {
            new CustomDialog(this.getParent(),java.util.ResourceBundle.getBundle("sk/stu/fiit/gui/bundle").getString("error_zvolte_vas_termin"),java.util.ResourceBundle.getBundle("sk/stu/fiit/gui/bundle").getString("CHYBA"),JOptionPane.WARNING_MESSAGE);
            CustomLogger.getLogger().log(Level.WARNING, "[VyletKalendar] nevybraty preferovany termin; user:" + this.m.getHlavneOkno().getLoggedInPouzivatel().getLogin());
            return;
        }
        DostupnostVyletnika d = this.v.getDostupnost().getDostupnostVyletnika(vyletnik);
        if (d == null) {
            this.v.getDostupnost().pridajDostupnost(new DostupnostVyletnika(vyletnik, dtmOd, dtmDo));
        } else {
            this.v.getDostupnost().updateDostupnost(d, dtmOd, dtmDo);
        }
        lblYourDate.setText(dtmOd.format(dtf)+" - "+dtmDo.format(dtf));
        DefaultListModel dlm = (DefaultListModel)listSuggestedDates.getModel();
        dlm.clear();
        for (NajlepsiTermin nt : v.getDostupnost().getNajTerminy().getNavrhyTerminov(10))
            dlm.addElement(nt);
        
        DefaultListModel dlm2 = (DefaultListModel)listAvailabilityDates.getModel();
        dlm2.clear();
        for (DostupnostVyletnika dv : v.getDostupnost().getDostupnostiVyletnikov())
            dlm2.addElement(dv);
        controls.updateUI();
    }//GEN-LAST:event_btnSaveDateMouseReleased

    private void btnChooseSuggestedMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnChooseSuggestedMouseReleased
        NajlepsiTermin nt = listSuggestedDates.getSelectedValue();
        if (nt == null) {
            new CustomDialog(this.getParent(),java.util.ResourceBundle.getBundle("sk/stu/fiit/gui/bundle").getString("error_zvolte_termin"),java.util.ResourceBundle.getBundle("sk/stu/fiit/gui/bundle").getString("CHYBA"),JOptionPane.WARNING_MESSAGE);
            CustomLogger.getLogger().log(Level.WARNING, "[VyletKalendar] nevybraty termin konania; user:" + this.m.getHlavneOkno().getLoggedInPouzivatel().getLogin());
            return;
        }
        if (!v.getRozpocet().isSpleneneUlohy()) {
            new CustomDialog(this.getParent(),java.util.ResourceBundle.getBundle("sk/stu/fiit/gui/bundle").getString("error_vysporiadajte_ulohy"),java.util.ResourceBundle.getBundle("sk/stu/fiit/gui/bundle").getString("CHYBA"),JOptionPane.WARNING_MESSAGE);
            CustomLogger.getLogger().log(Level.WARNING, "[VyletKalendar] nemozno vybrat termin - nevysporiadane ulohy; user:" + this.m.getHlavneOkno().getLoggedInPouzivatel().getLogin());
            return;
        }
        JFrame frame = new JFrame();
        JLabel label = new JLabel();
        label.setFont(new Font("Gill Sans MT", Font.BOLD, 18));
        label.setText(java.util.ResourceBundle.getBundle("sk/stu/fiit/gui/bundle").getString("message_vybrat_final_termin"));
        String[] options = {java.util.ResourceBundle.getBundle("sk/stu/fiit/gui/bundle").getString("ÁNO"), java.util.ResourceBundle.getBundle("sk/stu/fiit/gui/bundle").getString("NIE")};
        int dialogResult = JOptionPane.showOptionDialog(frame.getContentPane(),label,java.util.ResourceBundle.getBundle("sk/stu/fiit/gui/bundle").getString("VÝBER FINÁLNEHO TERMÍNU") ,0,JOptionPane.YES_NO_OPTION,null,options,null);
        if(dialogResult == 0) {
            this.v.setDokonceny(true);
            this.v.setBeginDate(nt.getDatumOd());
            this.v.setEndDate(nt.getDatumDo());
            lblTripDate.setText(this.v.getDatumString()+" ("+String.valueOf(v.getPocet_dni())+java.util.ResourceBundle.getBundle("sk/stu/fiit/gui/bundle").getString("DNI")+")");
            this.v.pridajTrasuDoItinerara();
            checkControls();
            CustomLogger.getLogger().log(Level.INFO, "[VyletKalendar] vybraty finalny termin vyletu; user:" + this.m.getHlavneOkno().getLoggedInPouzivatel().getLogin());
        }
    }//GEN-LAST:event_btnChooseSuggestedMouseReleased


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnChooseSuggested;
    private javax.swing.JButton btnSaveDate;
    private javax.swing.JPanel controls;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.JLabel lblAvailabilityTitle;
    private javax.swing.JLabel lblBack;
    private javax.swing.JLabel lblDate;
    private javax.swing.JLabel lblDo;
    private javax.swing.JLabel lblFrom;
    private javax.swing.JLabel lblMatch;
    private javax.swing.JLabel lblOd;
    private javax.swing.JLabel lblPreferredDate;
    private javax.swing.JLabel lblSuggestedTitle;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JLabel lblTo;
    private javax.swing.JLabel lblTripDate;
    private javax.swing.JLabel lblYourDate;
    private javax.swing.JLabel lblyour;
    private javax.swing.JList<DostupnostVyletnika> listAvailabilityDates;
    private javax.swing.JList<NajlepsiTermin> listSuggestedDates;
    private javax.swing.JPanel pnlFrom;
    private javax.swing.JPanel pnlTo;
    private javax.swing.JScrollPane scrollAvailability;
    private javax.swing.JScrollPane scrollSuggestedDates;
    // End of variables declaration//GEN-END:variables

    @Override
    public void updateCalendar(LocalDate value, CalendarPanel calendar) {
        if (value == null) {
            new CustomDialog(this.getParent(),java.util.ResourceBundle.getBundle("sk/stu/fiit/gui/bundle").getString("error_zvolte_datum"),java.util.ResourceBundle.getBundle("sk/stu/fiit/gui/bundle").getString("CHYBA"),JOptionPane.WARNING_MESSAGE);
            CustomLogger.getLogger().log(Level.WARNING, "[VyletKalendar] nevybraty datum; user:" + this.m.getHlavneOkno().getLoggedInPouzivatel().getLogin());
            return;
        }
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        if (calendar.equals(pnlFrom)) {
            this.dtmOd = value;
            lblFrom.setText(value.format(dtf));
            ((CalendarPanel)pnlTo).setMinDate(value);
            ((CalendarPanel)pnlTo).updateUI();
            this.dtmDo = ((CalendarPanel)pnlTo).getSelectedValue();
            lblTo.setText(this.dtmDo == null ? "" : this.dtmDo.format(dtf));
        } else if (calendar.equals(pnlTo)) {
            this.dtmDo = value;
            lblTo.setText(value.format(dtf));
        }
    }
    
    private void checkControls() {
        if (LocalDate.now().isAfter(v.getEndDate()) || v.isDokonceny()) {
            pnlTo.setVisible(false);
            pnlFrom.setVisible(false);
            btnChooseSuggested.setVisible(false);
            btnSaveDate.setVisible(false);
            lblOd.setVisible(false);
            lblDo.setVisible(false);
            lblTo.setVisible(false);
            lblFrom.setVisible(false);
        }
    }
}
