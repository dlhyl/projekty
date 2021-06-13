/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.stu.fiit.app.user;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.ImageIcon;

/**
 * Pouzivatel aplikacie ma prihlasovacie meno,
 * hash hesla, meno, priezvisko, pohlavie a volitelny obrazok
 * @author dlhyl
 */
public abstract class Pouzivatel {
    public enum Pohlavie {
        MUZ, ZENA
    }
    
    private String login;
    private String password_hash;
    private ImageIcon obrazok;
    private String meno;
    private String priezvisko;
    private Pohlavie pohlavie;

    public Pouzivatel(String login, String password, String meno, String priezvisko, Pohlavie pohlavie) {
        this.login = login;
        this.password_hash = this.createHash(password);
        this.meno = meno;
        this.priezvisko = priezvisko;
        this.pohlavie = pohlavie;
    }
    
    /**
     * Validacia hesla pomocou regex-u, minimalne 8 znakov, z toho aspon 1 cislica
     * @param pw heslo
     * @return true, ak je validne, inak false
     */
    public static boolean isValidPassword(String pw){
        Pattern hesloRegex = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$");
        Matcher macka = hesloRegex.matcher(pw);
        return macka.matches();
    }
    
    /**
     * Vytvorenie md5 hashu pre heslo
     * @param pw heslo
     * @return md5 hash hesla
     */
    public static String createHash(String pw){
        try {
            MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] pwBytes = pw.getBytes("UTF-8");
            byte[] hashBytes = md.digest(pwBytes);
            return new BigInteger(1,hashBytes).toString(16);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Pouzivatel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Pouzivatel.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword_hash() {
        return password_hash;
    }

    public void setPassword(String password) {
        this.password_hash = createHash(password);
    }

    public ImageIcon getObrazok() {
        return obrazok;
    }

    public void setObrazok(ImageIcon obrazok) {
        this.obrazok = obrazok;
    }

    public String getMeno() {
        return meno;
    }

    public String getPriezvisko() {
        return priezvisko;
    }

    public Pohlavie getPohlavie() {
        return pohlavie;
    }
    
    public String getCeleMeno() {
        return this.getMeno() + " " + this.getPriezvisko();
    }
}
