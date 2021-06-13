/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.stu.fiit.app.trip;

import java.text.DecimalFormat;
import java.util.ArrayList;
import sk.stu.fiit.app.user.Vyletnik;

/**
 * Rozpocet vyletu obsahujuci celkove vydavky a zoznam uloh.
 * @author dlhyl
 */
public class Rozpocet {
    private Double suma = 0.0d;
    private ArrayList<Uloha> zoznamUloh = new ArrayList<>();

    public Rozpocet() {
    }

    public double getSuma() {
        return suma;
    }
    
    /**
     * Vyrovnanie zavazkov vyletnika.
     * Vsetky ulohy, ktore zaobstarava budu uvolnene.
     * @param usr vyletnik
     */
    public void vyrovnajZavazky(Vyletnik usr) {
        for (Uloha u : this.zoznamUloh) {
            if (u.getVybavovac().equals(usr)) {
                u.setVybavovac(null);
                u.setZaobstarana(false);
            }
        }
    }
    
    /**
     * Overenie ci vsetky ulohy boli uz zaobstarane
     * @return true, ak su vsetky ulohy zaobstarane, inak false
     */
    public boolean isSpleneneUlohy() {
        return this.zoznamUloh.stream().allMatch(i -> i.isZaobstarana());
    }
    
    /**
     * Ziskanie zoznamu uloh, ktore zaobstarava dany vyletnik.
     * Zaobstarane ulohy budu na konci zoznamu.
     * @param v vyletnik
     * @return zoznam uloh, ktore zaobstarava
     */
    public ArrayList<Uloha> getZoznamUloh(Vyletnik v) {
        ArrayList<Uloha> arr = new ArrayList<>();
        for (Uloha u: zoznamUloh)
            if (u.getVybavovac() != null && u.getVybavovac().equals(v))
                arr.add(u.isZaobstarana() ? Math.max(0,arr.size()-1) : 0,u);
        return arr;
    }
    
    /**
     * Vydavky prepocitane na jedneho clena
     * @param pocetUcastnikov
     * @return vydavky na 1 clena
     */
    public double getSuma(int pocetUcastnikov) {
        if (pocetUcastnikov == 0)
            return suma;
        return suma/pocetUcastnikov;
    }
    
    /**
     * Pridanie ulohy a aktualizovanie celkovych vydavkov
     * @param u uloha
     */
    public void pridajUlohu(Uloha u) {
        if (u.getCena() != null) 
            this.suma += u.getCena();
        this.zoznamUloh.add(u);
    }
    
    public String getSumaString() {
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(this.suma);
    }
    
    public String getSumaString(int pocetUcastnikov) {
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(this.getSuma(pocetUcastnikov));
    }

    public void setSuma(double suma) {
        this.suma = suma;
    }

    public ArrayList<Uloha> getZoznamUloh() {
        return zoznamUloh;
    }
}
