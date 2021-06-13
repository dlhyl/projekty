/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.stu.fiit.gui;

import sk.stu.fiit.gui.vylet_dashboard.VyletDashboardPanel;
import java.awt.CardLayout;
import java.awt.Color;
import java.time.LocalDate;
import java.util.Locale;
import java.util.ResourceBundle;
import sk.stu.fiit.app.Potulkor;
import sk.stu.fiit.app.trip.DostupnostVyletnika;
import sk.stu.fiit.app.trip.TypVyletu;
import sk.stu.fiit.app.trip.Vylet;
import sk.stu.fiit.app.user.Pouzivatel.Pohlavie;
import sk.stu.fiit.app.user.Pouzivatel;
import sk.stu.fiit.app.user.Vyletnik;
import sk.stu.fiit.gui.dashboard.*;
import sk.stu.fiit.gui.utils.RoundedImage;

/**
 *
 * @author dlhyl
 */
public class MainWindow extends javax.swing.JFrame {
    private Potulkor potulkor = null;
    private Pouzivatel loggedUser = null;
    private ProfilPanel pnlDashProfil = null;
    private MojeVyletyPanel pnlDashMojeVylety = null;
    private NovyVyletPanel pnlNovyVylet = null;
    private PozvankyPanel pnlDashPozvanky = null;
    private StatistikyPanel pnlDashStatistiky = null;
    private OdznakyPanel pnlDashOdznaky = null;
    private RebricekPanel pnlDashRebricek = null;
    private VyletDashboardPanel pnlVyletDashboard = null;
    private DashboardPanel pnlDashboardMenu;
    
    public Potulkor getPotulkor() {
        return potulkor;
    }

    protected void setPotulkor(Potulkor potulkor) {
        this.potulkor = potulkor;
    }
    /**
     * Creates new form MainWindow
     */
    public MainWindow() {
        potulkor = new Potulkor();
        potulkor.getKatalogTipov().setZoznamMiestZaujmuFromXML();
        potulkor.getKatalogTipov().setZoznamTrasFromXML();
        
        initExample1();
        initComponents();
    }
    
    
    
    private void initExample1() {
        Vyletnik marek = new Vyletnik("marek","marek123","Marek","Siroky", Pohlavie.MUZ);
        Vyletnik jurko = new Vyletnik("jurko","jurko123","Juraj","Vysoky", Pohlavie.MUZ);
        potulkor.pridajPouzivatela(marek);
        potulkor.pridajPouzivatela(jurko);
        
        Vylet v = new Vylet("Super vylet", TypVyletu.TURISTICKY, LocalDate.now().plusDays(2), LocalDate.now().plusDays(4), potulkor.getKatalogTipov().getMiestoZaujmuByName("Bratislava"), marek);
        v.pridajClena(jurko);
        v.getDostupnost().pridajDostupnost(new DostupnostVyletnika(marek, LocalDate.now().plusDays(10), LocalDate.now().plusDays(15)));
        
        Vylet v1 = new Vylet("Super vylet", TypVyletu.TURISTICKY, LocalDate.now().minusDays(4), LocalDate.now().minusDays(2), potulkor.getKatalogTipov().getMiestoZaujmuByName("Bratislava"), marek);
        v1.pridajClena(jurko);
        v1.getDostupnost().pridajDostupnost(new DostupnostVyletnika(marek, LocalDate.now().minusDays(10), LocalDate.now().minusDays(15)));      
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

        pnlSplash = new SplashPanel(this);
        pnlDashboard = new javax.swing.JPanel();
        pnlDashContent = new javax.swing.JPanel();
        pnlHeader = new javax.swing.JPanel();
        lblPotulky = new javax.swing.JLabel();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 32767));
        lblProfile = new javax.swing.JLabel();
        pnlContent = new javax.swing.JPanel();
        pnlDark = new javax.swing.JPanel();
        backGround = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Potulky");
        setPreferredSize(new java.awt.Dimension(1280, 960));
        getContentPane().setLayout(new java.awt.CardLayout());
        getContentPane().add(pnlSplash, "cardSplash");

        pnlDashboard.setLayout(new java.awt.GridBagLayout());

        pnlDashContent.setOpaque(false);
        pnlDashContent.setLayout(new java.awt.BorderLayout());

        pnlHeader.setBackground(new Color(101, 21, 40));
        pnlHeader.setPreferredSize(new java.awt.Dimension(0, 60));
        pnlHeader.setLayout(new java.awt.GridBagLayout());

        lblPotulky.setFont(new java.awt.Font("Gill Sans MT", 1, 24)); // NOI18N
        lblPotulky.setForeground(new java.awt.Color(255, 102, 102));
        lblPotulky.setText("Potulky");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 0, 0);
        pnlHeader.add(lblPotulky, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.weightx = 1.0;
        pnlHeader.add(filler1, gridBagConstraints);

        lblProfile.setFont(new java.awt.Font("Gill Sans MT", 1, 24)); // NOI18N
        lblProfile.setForeground(new java.awt.Color(255, 102, 102));
        lblProfile.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sk/stu/fiit/assets/icons/profile-user.png"))); // NOI18N
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("sk/stu/fiit/gui/bundle"); // NOI18N
        lblProfile.setText(bundle.getString("PROFIL")); // NOI18N
        lblProfile.setIconTextGap(10);
        lblProfile.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                lblProfileMouseReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 20);
        pnlHeader.add(lblProfile, gridBagConstraints);

        pnlDashContent.add(pnlHeader, java.awt.BorderLayout.PAGE_START);

        pnlContent.setOpaque(false);
        pnlContent.setLayout(new java.awt.CardLayout());

        this.changeDashboardScreen("cardDashboardMenu");

        pnlDashContent.add(pnlContent, java.awt.BorderLayout.CENTER);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        pnlDashboard.add(pnlDashContent, gridBagConstraints);

        pnlDark.setBackground(new java.awt.Color(0, 0, 0, 160));

        javax.swing.GroupLayout pnlDarkLayout = new javax.swing.GroupLayout(pnlDark);
        pnlDark.setLayout(pnlDarkLayout);
        pnlDarkLayout.setHorizontalGroup(
            pnlDarkLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 2080, Short.MAX_VALUE)
        );
        pnlDarkLayout.setVerticalGroup(
            pnlDarkLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1183, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        pnlDashboard.add(pnlDark, gridBagConstraints);

        backGround.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sk/stu/fiit/assets/background.png"))); // NOI18N
        backGround.setMaximumSize(new java.awt.Dimension(0, 0));
        backGround.setMinimumSize(new java.awt.Dimension(0, 0));
        backGround.setPreferredSize(new java.awt.Dimension(0, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        pnlDashboard.add(backGround, gridBagConstraints);

        getContentPane().add(pnlDashboard, "cardDashboard");

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void lblProfileMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblProfileMouseReleased
        this.changeDashboardScreen("cardProfil");
    }//GEN-LAST:event_lblProfileMouseReleased

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainWindow().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel backGround;
    private javax.swing.Box.Filler filler1;
    private javax.swing.JLabel lblPotulky;
    private javax.swing.JLabel lblProfile;
    private javax.swing.JPanel pnlContent;
    private javax.swing.JPanel pnlDark;
    private javax.swing.JPanel pnlDashContent;
    private javax.swing.JPanel pnlDashboard;
    private javax.swing.JPanel pnlHeader;
    private javax.swing.JPanel pnlSplash;
    // End of variables declaration//GEN-END:variables

    public void changeDashboardScreen(String cardName) {
        switch(cardName) {
            case "cardNovyVylet":
                if (pnlNovyVylet != null) pnlContent.remove(pnlNovyVylet);
                pnlNovyVylet = new NovyVyletPanel(this);
                pnlContent.add(pnlNovyVylet, cardName);
                break;
            case "cardMojeVylety":
                if (pnlDashMojeVylety != null) pnlContent.remove(pnlDashMojeVylety);
                pnlDashMojeVylety = new MojeVyletyPanel(this);
                pnlContent.add(pnlDashMojeVylety, cardName);
                break;
            case "cardRebricek":
                if (pnlDashRebricek != null) pnlContent.remove(pnlDashRebricek);pnlDashRebricek = new RebricekPanel(this);pnlContent.add(pnlDashRebricek, cardName);
                break;
            case "cardPozvanky":
                if (pnlDashPozvanky != null) pnlContent.remove(pnlDashPozvanky);pnlDashPozvanky = new PozvankyPanel(this);pnlContent.add(pnlDashPozvanky, cardName);
                break;
            case "cardStatistiky":
                if (pnlDashStatistiky != null) pnlContent.remove(pnlDashStatistiky);pnlDashStatistiky = new StatistikyPanel(this);  pnlContent.add(pnlDashStatistiky, cardName);
                break;
            case "cardOdznaky":
                if (pnlDashOdznaky != null) pnlContent.remove(pnlDashOdznaky);pnlDashOdznaky = new OdznakyPanel(this);pnlContent.add(pnlDashOdznaky, cardName);
                break;
            case "cardDashboardMenu":
                if (pnlDashboardMenu != null) pnlContent.remove(pnlDashboardMenu);pnlDashboardMenu = new DashboardPanel(this);pnlContent.add(pnlDashboardMenu, cardName);
                break;
            case "cardProfil":
                if (pnlDashProfil != null) pnlContent.remove(pnlDashProfil);
                pnlDashProfil = new ProfilPanel(this);
                pnlContent.add(pnlDashProfil, cardName);
                break;
        }
        
        CardLayout c = (CardLayout) pnlContent.getLayout();
        c.show(pnlContent, cardName);
    }

    public void login(Pouzivatel p) {
        this.loggedUser = p;
        lblProfile.setText(this.getLoggedInPouzivatel().getMeno());
        changeProfileLabelIcon();
        changeDashboardScreen("cardDashboardMenu");
        CardLayout c2 = (CardLayout)this.getContentPane().getLayout();
        c2.show(this.getContentPane(),"cardDashboard");  
    }
    
    public void logout() {
        this.loggedUser = null;
        changeProfileLabelIcon();
        ((SplashPanel)pnlSplash).setDefaultCard();
        CardLayout c = (CardLayout)this.getContentPane().getLayout();
        c.show(this.getContentPane(),"cardSplash");
    }
    
    public Pouzivatel getLoggedInPouzivatel() {
        return loggedUser;
    }

    public void showVyletDashboard(Vylet v) {
        if (pnlVyletDashboard != null)
            pnlContent.remove(pnlVyletDashboard);
        pnlVyletDashboard = new VyletDashboardPanel(this, v);
        pnlContent.add(pnlVyletDashboard, "cardVyletDashboard");
        CardLayout c = (CardLayout) pnlContent.getLayout();
        c.show(pnlContent, "cardVyletDashboard");
    }
    
    protected void changeLanguage(String language) {
        if (language == null) return;
        
        switch (language) {
            case "SK": 
                Locale.setDefault(new Locale("sk", "SK"));
                ResourceBundle.clearCache();
                break;
            case "EN":
                Locale.setDefault(new Locale("en", "US"));
                ResourceBundle.clearCache();
                break;
        }
    }
    
    protected void changeProfileLabelIcon() {
        if (this.getLoggedInPouzivatel() != null && this.getLoggedInPouzivatel().getObrazok() != null)
            lblProfile.setIcon(RoundedImage.getImage(this.getLoggedInPouzivatel().getObrazok(), 32));
        else 
            lblProfile.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sk/stu/fiit/assets/icons/profile-user.png"))); // NOI18N
    }
}
