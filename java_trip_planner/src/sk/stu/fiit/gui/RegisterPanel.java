/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.stu.fiit.gui;

import sk.stu.fiit.gui.utils.RoundedPanel;
import java.awt.Color;
import java.awt.Font;
import java.util.logging.Level;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import sk.stu.fiit.app.CustomLogger;
import sk.stu.fiit.app.Potulkor;
import sk.stu.fiit.app.user.Pouzivatel.Pohlavie;
import sk.stu.fiit.app.user.Pouzivatel;
import sk.stu.fiit.app.user.Vyletnik;

/**
 *
 * @author dlhyl
 */
public class RegisterPanel extends javax.swing.JPanel {
    private SplashPanel splashPanel;
    /**
     * Creates new form LoginPanel
     */
    public RegisterPanel(SplashPanel s) {
        this.splashPanel = s;
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

        btngrpPohlavie = new javax.swing.ButtonGroup();
        pnlRegister = new RoundedPanel(60,new Color(0, 0, 0, 150));
        inputLogin = new javax.swing.JTextField();
        lblLogin = new javax.swing.JLabel();
        lblConfirmPassword = new javax.swing.JLabel();
        btnRegiister = new javax.swing.JButton();
        pwField2 = new javax.swing.JPasswordField();
        lblBack = new javax.swing.JLabel();
        lblMeno = new javax.swing.JLabel();
        inputMeno = new javax.swing.JTextField();
        lblPriezvisko = new javax.swing.JLabel();
        inputPriezvisko = new javax.swing.JTextField();
        lblHeslo = new javax.swing.JLabel();
        pwField1 = new javax.swing.JPasswordField();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 3), new java.awt.Dimension(0, 3), new java.awt.Dimension(0, 0));
        lblGender = new javax.swing.JLabel();
        rbMale = new javax.swing.JRadioButton();
        rbFemale = new javax.swing.JRadioButton();

        setMinimumSize(new java.awt.Dimension(800, 800));
        setName(""); // NOI18N
        setPreferredSize(new java.awt.Dimension(800, 800));
        setLayout(new java.awt.GridBagLayout());

        pnlRegister.setMinimumSize(new java.awt.Dimension(500, 500));
        pnlRegister.setOpaque(false);
        pnlRegister.setPreferredSize(new java.awt.Dimension(500, 500));
        pnlRegister.setLayout(new java.awt.GridBagLayout());

        inputLogin.setFont(new java.awt.Font("Gill Sans MT", 1, 24)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 2.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 30);
        pnlRegister.add(inputLogin, gridBagConstraints);

        lblLogin.setFont(new java.awt.Font("Gill Sans MT", 1, 24)); // NOI18N
        lblLogin.setForeground(new java.awt.Color(255, 102, 102));
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("sk/stu/fiit/gui/bundle"); // NOI18N
        lblLogin.setText(bundle.getString("LOGIN")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 25, 0, 0);
        pnlRegister.add(lblLogin, gridBagConstraints);

        lblConfirmPassword.setFont(new java.awt.Font("Gill Sans MT", 1, 24)); // NOI18N
        lblConfirmPassword.setForeground(new java.awt.Color(255, 102, 102));
        lblConfirmPassword.setText(bundle.getString("POTVRĎTE HESLO")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 25, 0, 0);
        pnlRegister.add(lblConfirmPassword, gridBagConstraints);

        btnRegiister.setBackground(new java.awt.Color(144, 30, 57));
        btnRegiister.setFont(new java.awt.Font("Gill Sans MT", 1, 24)); // NOI18N
        btnRegiister.setForeground(new java.awt.Color(255, 204, 204));
        btnRegiister.setText(bundle.getString("REGISTROVAŤ")); // NOI18N
        btnRegiister.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btnRegiisterMouseReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.weightx = 2.5;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 20, 20);
        pnlRegister.add(btnRegiister, gridBagConstraints);

        pwField2.setFont(new java.awt.Font("Gill Sans MT", 1, 24)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 2.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 30);
        pnlRegister.add(pwField2, gridBagConstraints);

        lblBack.setBackground(new java.awt.Color(255, 255, 255));
        lblBack.setFont(new java.awt.Font("Gill Sans MT", 1, 18)); // NOI18N
        lblBack.setForeground(new java.awt.Color(200, 200, 200));
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
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.weightx = 2.5;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(30, 30, 0, 30);
        pnlRegister.add(lblBack, gridBagConstraints);

        lblMeno.setFont(new java.awt.Font("Gill Sans MT", 1, 24)); // NOI18N
        lblMeno.setForeground(new java.awt.Color(255, 102, 102));
        lblMeno.setText(bundle.getString("MENO")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 25, 0, 0);
        pnlRegister.add(lblMeno, gridBagConstraints);

        inputMeno.setFont(new java.awt.Font("Gill Sans MT", 1, 24)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 2.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 30);
        pnlRegister.add(inputMeno, gridBagConstraints);

        lblPriezvisko.setFont(new java.awt.Font("Gill Sans MT", 1, 24)); // NOI18N
        lblPriezvisko.setForeground(new java.awt.Color(255, 102, 102));
        lblPriezvisko.setText(bundle.getString("PRIEZVISKO")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 25, 0, 0);
        pnlRegister.add(lblPriezvisko, gridBagConstraints);

        inputPriezvisko.setFont(new java.awt.Font("Gill Sans MT", 1, 24)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 2.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 30);
        pnlRegister.add(inputPriezvisko, gridBagConstraints);

        lblHeslo.setFont(new java.awt.Font("Gill Sans MT", 1, 24)); // NOI18N
        lblHeslo.setForeground(new java.awt.Color(255, 102, 102));
        lblHeslo.setText(bundle.getString("HESLO")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 25, 0, 0);
        pnlRegister.add(lblHeslo, gridBagConstraints);

        pwField1.setFont(new java.awt.Font("Gill Sans MT", 1, 24)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 2.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 30);
        pnlRegister.add(pwField1, gridBagConstraints);

        filler1.setBackground(new java.awt.Color(255, 102, 102));
        filler1.setOpaque(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 3.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 45, 5, 55);
        pnlRegister.add(filler1, gridBagConstraints);

        lblGender.setFont(new java.awt.Font("Gill Sans MT", 1, 24)); // NOI18N
        lblGender.setForeground(new java.awt.Color(255, 102, 102));
        lblGender.setText(bundle.getString("POHLAVIE")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 25, 0, 0);
        pnlRegister.add(lblGender, gridBagConstraints);

        btngrpPohlavie.add(rbMale);
        rbMale.setFont(new java.awt.Font("Gill Sans MT", 1, 22)); // NOI18N
        rbMale.setForeground(new java.awt.Color(255, 102, 102));
        rbMale.setText(bundle.getString("MUŽ")); // NOI18N
        rbMale.setActionCommand("muz");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        pnlRegister.add(rbMale, gridBagConstraints);

        btngrpPohlavie.add(rbFemale);
        rbFemale.setFont(new java.awt.Font("Gill Sans MT", 1, 22)); // NOI18N
        rbFemale.setForeground(new java.awt.Color(255, 102, 102));
        rbFemale.setText(bundle.getString("ŽENA")); // NOI18N
        rbFemale.setActionCommand("zena");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        pnlRegister.add(rbFemale, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.PAGE_START;
        add(pnlRegister, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void lblBackMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblBackMouseReleased
        this.splashPanel.setDefaultCard();
    }//GEN-LAST:event_lblBackMouseReleased

    private void btnRegiisterMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnRegiisterMouseReleased
        String login = inputLogin.getText();
        String meno = inputMeno.getText();
        String priezvisko = inputPriezvisko.getText();
        String pw1 = String.valueOf(pwField2.getPassword());
        String pw2 = String.valueOf(pwField1.getPassword());
        JLabel label = new JLabel();
        label.setFont(new Font("Gill Sans MT", Font.BOLD, 18));
        
        if (login.isEmpty() || meno.isEmpty() || priezvisko.isEmpty() || pw1.isEmpty() || pw2.isEmpty() ||  btngrpPohlavie.getSelection() == null) {
            label.setText(java.util.ResourceBundle.getBundle("sk/stu/fiit/gui/bundle").getString("error_vyplnte_polia"));
            JOptionPane.showMessageDialog(this.getParent(),label,java.util.ResourceBundle.getBundle("sk/stu/fiit/gui/bundle").getString("CHYBA"),JOptionPane.WARNING_MESSAGE);
            CustomLogger.getLogger().log(Level.WARNING, "[Register] nevyplnene vsetky udaje");
            return;
        }
        
        if (!pw1.equals(pw2)) {
            label.setText(java.util.ResourceBundle.getBundle("sk/stu/fiit/gui/bundle").getString("heslo_error_rozne"));
            JOptionPane.showMessageDialog(this.getParent(),label,java.util.ResourceBundle.getBundle("sk/stu/fiit/gui/bundle").getString("CHYBA"),JOptionPane.WARNING_MESSAGE);
            CustomLogger.getLogger().log(Level.WARNING, "[Register] hesla sa nezhoduju");
            return;
        }
        
        if (!Pouzivatel.isValidPassword(pw1)) { 
            label.setText(java.util.ResourceBundle.getBundle("sk/stu/fiit/gui/bundle").getString("heslo_error"));
            JOptionPane.showMessageDialog(this.getParent(),label,java.util.ResourceBundle.getBundle("sk/stu/fiit/gui/bundle").getString("CHYBA"),JOptionPane.WARNING_MESSAGE);
            CustomLogger.getLogger().log(Level.WARNING, "[Register] hesla sa nezhoduju");
            return;
        }
        
        Potulkor p = ((MainWindow)this.splashPanel.getMainWindow()).getPotulkor();
        if (p.pouzivatelExists(login)) {
            label.setText(java.util.ResourceBundle.getBundle("sk/stu/fiit/gui/bundle").getString("rovnake_meno"));
            JOptionPane.showMessageDialog(this.getParent(),label,java.util.ResourceBundle.getBundle("sk/stu/fiit/gui/bundle").getString("CHYBA"),JOptionPane.WARNING_MESSAGE);
            CustomLogger.getLogger().log(Level.WARNING, "[Register] pouzivatel s tymto menom uz existuje:" + login);
            return;
        }
        Vyletnik usr = new Vyletnik(login, pw1, meno, priezvisko, btngrpPohlavie.getSelection().getActionCommand().equals("muz")? Pohlavie.MUZ : Pohlavie.ZENA);
        p.pridajPouzivatela(usr);
        label.setText(java.util.ResourceBundle.getBundle("sk/stu/fiit/gui/bundle").getString("register_ok"));
        JOptionPane.showMessageDialog(this.getParent(),label,java.util.ResourceBundle.getBundle("sk/stu/fiit/gui/bundle").getString("USPECH"),JOptionPane.INFORMATION_MESSAGE);
        ((MainWindow)this.splashPanel.getMainWindow()).login(usr);
        CustomLogger.getLogger().log(Level.INFO, "[Register] pouzivatel zaregistrovany:"+usr.getLogin());
    }//GEN-LAST:event_btnRegiisterMouseReleased


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnRegiister;
    private javax.swing.ButtonGroup btngrpPohlavie;
    private javax.swing.Box.Filler filler1;
    private javax.swing.JTextField inputLogin;
    private javax.swing.JTextField inputMeno;
    private javax.swing.JTextField inputPriezvisko;
    private javax.swing.JLabel lblBack;
    private javax.swing.JLabel lblConfirmPassword;
    private javax.swing.JLabel lblGender;
    private javax.swing.JLabel lblHeslo;
    private javax.swing.JLabel lblLogin;
    private javax.swing.JLabel lblMeno;
    private javax.swing.JLabel lblPriezvisko;
    private javax.swing.JPanel pnlRegister;
    private javax.swing.JPasswordField pwField1;
    private javax.swing.JPasswordField pwField2;
    private javax.swing.JRadioButton rbFemale;
    private javax.swing.JRadioButton rbMale;
    // End of variables declaration//GEN-END:variables
}
