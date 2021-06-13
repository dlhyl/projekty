/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.stu.fiit.app.trip;

import sk.stu.fiit.app.user.Vyletnik;

/**
 * Uloha na zabezpecenie nejakeho zdroja pre vylet,
 * ma zadavatela, zaobstaravatela, nazov, popis, cenu a status
 * @author dlhyl
 */
public class Uloha {
    private final Vyletnik zadavatel;
    private Vyletnik vybavovac;
    private final String nazov;
    private final String komentar;
    private Double cena;
    private boolean zaobstarana = false;

    public Uloha(Vyletnik zadavatel, String nazov, String komentar, Double cena) {
        this.zadavatel = zadavatel;
        this.nazov = nazov;
        this.komentar = komentar;
        this.cena = cena;
    }

    /**
     * Nova uloha, ktora nema cenu
     * @param zadavatel Vyletnik - zadavatel
     * @param nazov - nazov ulohy
     * @param komentar  - popis ulohy
     */
    public Uloha(Vyletnik zadavatel, String nazov, String komentar) {
        this.zadavatel = zadavatel;
        this.nazov = nazov;
        this.komentar = komentar;
    }

    public boolean isZaobstarana() {
        return zaobstarana;
    }

    public void setZaobstarana(boolean zaobstarana) {
        this.zaobstarana = zaobstarana;
    }

    public Vyletnik getZadavatel() {
        return zadavatel;
    }

    public Vyletnik getVybavovac() {
        return vybavovac;
    }

    public void setVybavovac(Vyletnik vybavovac) {
        this.vybavovac = vybavovac;
    }

    public String getNazov() {
        return nazov;
    }

    public String getKomentar() {
        return komentar;
    }
    
    public Double getCena() {
        return cena;
    }
}
