/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.stu.fiit.app.user;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import javafx.util.Pair;
import sk.stu.fiit.app.trip.Pozvanka;
import sk.stu.fiit.app.trip.Vylet;

/**
 * Vyletnik je pouzivatel, ktory chce aplikaciu pouzivat na planovanie vyletov.
 * Ma svoju historiu vyletov, kde ku kazdemu vyletu ma priradenu biometriu,
 * dalej ma statistiky, pozvanky (prijate aj odoslane) a odznaky.
 * @author dlhyl
 */
public class Vyletnik extends Pouzivatel {
    private final ArrayList <Pair <Vylet,Biometria>> historiaVyletov = new ArrayList<>();
    private final Statistika stats = new Statistika(this);
    private final ArrayList<Pozvanka> pozvanky = new ArrayList<>();
    private final HashSet<Odznak> odznaky = new HashSet<>();
    
    public Vyletnik(String login, String password, String meno, String priezvisko, Pohlavie pohlavie) {
        super(login, password, meno, priezvisko, pohlavie);
    }
    
    public ArrayList<Pair<Vylet, Biometria>> getList() {
        return historiaVyletov;
    }
    
    /**
     * Overenie ci bol vyletnik uz pozvany do vyletu
     * @param v vylet
     * @return true ak bol pozvany do vyletu v, false ak nie
     */
    public boolean hasPozvanka(Vylet v) {
        return this.pozvanky.stream().anyMatch(i -> Boolean.logicalAnd(i.getVylet().equals(v), i.getPrijemca().equals(this)));
    }
    
    /**
     * Vratenie vsetkych vyletov vyletnika
     * @return zoznam vyletov
     */
    public ArrayList<Vylet> getVyletList() {
        ArrayList<Vylet> v = new ArrayList<>();
        for (Pair<Vylet,Biometria> p : historiaVyletov)
            v.add((Vylet)p.getKey());
        
        v.sort(Comparator.comparing(Vylet::isZruseny).reversed().thenComparing(Vylet::getBeginDate).reversed());
        return v;
    }
    
    /**
     * Ziskanie biometrie pre vylet v
     * @param v vylet
     * @return biometria k vyletu
     */
    public Biometria getBiometria(Vylet v) {
        try {
            return historiaVyletov.stream().filter(i -> i.getKey().equals(v)).findFirst().get().getValue();
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Nastavenie biometrie pre vylet a update statistik
     * @param v vylet, pre ktory sa ma pridat biometria
     * @param b biometria, ktora sa ma priradit k vyletu
     */
    public void setBiometria(Vylet v, Biometria b) {
        Biometria bio = historiaVyletov.stream().filter(i -> i.getKey().equals(v)).findFirst().get().getValue();
        stats.pridajKroky(b.getKroky()-bio.getKroky());
        stats.pridajKm(b.getKm()-bio.getKm());
        bio.setKm(b.getKm());
        bio.setKroky(b.getKroky());
    }
    
    /**
     * Poslanie pozvanky prijemcovi,
     * prida pozvanku do svojho zoznamu 
     * a tiez do prijemcovho zoznamu
     * @param p pozvanka
     */
    public void posliPozvanku(Pozvanka p) {
        this.pozvanky.add(p);
        p.getPrijemca().getPozvanky().add(p);
    }
    
    /**
     * Pridanie vyletu s biometriou do historie vyletov
     * @param vylet vylet
     * @param biometria biometria
     */
    public void pridajVylet(Vylet vylet, Biometria biometria){
        historiaVyletov.add(new Pair <Vylet,Biometria> (vylet, biometria));
        stats.priradVylet(vylet);
    }
    
    /**
     * Odstranenie vyletu z historie vyletov (ak vyletnik opustil vylet)
     * @param v vylet
     */
    public void odstranVylet(Vylet v) {
        v.odstranVyletnika(this);
        historiaVyletov.removeIf(item -> item.getKey().equals(v));
        stats.odstranVylet(v);
    }
    
    /**
     * Pridanie odomknuteho odznaku a update statistik
     * @param o 
     */
    public void pridajOdznak(Odznak o) {
        this.odznaky.add(o);
        this.stats.pridajOdznak(o);
    }

    public Statistika getStats() {
        return stats;
    }
    
    public ArrayList<Pozvanka> getPozvanky() {
        return pozvanky;
    }
    
    /**
     * Metoda na zistenie, ci vyletnik ma dany odznak
     * @param o odznak
     * @return 
     */
    public boolean hasOdznak(Odznak o) {
        return odznaky.contains(o);
    };
    
    @Override
    public String toString() {
        return this.getCeleMeno();
    }
}
