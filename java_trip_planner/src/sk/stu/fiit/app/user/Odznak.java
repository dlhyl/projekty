/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.stu.fiit.app.user;

import java.util.Observable;
import java.util.Observer;
import sk.stu.fiit.app.user.StatistikaAspekt.DruhAspektu;

/**
 * Vyletnik ziska odznak za splnenie nejakej ulohy.
 * Odznak sleduje nejaku statiku, vdaka comu bude po zmene
 * statisickej hodnoty odznak upozorneni a v pripade splnenia
 * podmienky sa odznak odomkne.
 * @author dlhyl
 */
public class Odznak implements Observer {
    
    /**
     * Podmienka urcuje aku statistiku odznak sleduje
     * a pri akej hodnote sa odznak odomkne.
     */
    public static class Podmienka {
        private final DruhAspektu typ;
        private final int value;

        public Podmienka(DruhAspektu typ, int value) {
            this.typ = typ;
            this.value = value;
        }

        public DruhAspektu getTyp() {
            return typ;
        }

        public int getValue() {
            return value;
        }
    }
    
    private final String popis;
    private final Podmienka podmienka;
    private final int pointy;
    private final String ikonka;

    public Odznak(String popis, Podmienka podmienka, int pointy, String ikonka) {
        this.popis = popis;
        this.pointy = pointy;
        this.ikonka = ikonka;
        this.podmienka = podmienka;
    }

    /**
     * Pri zmene statistiky, ktoru odznak sleduje sa zavola funkcia update.
     * Update skontroluje, ci je splnena podmienka a ak ano, odstrani sledovanu
     * statistiku a odomkne odznak.
     * @param o sledovana statistika
     * @param arg 
     */
    @Override
    public void update(Observable o, Object arg) {
        StatistikaAspekt stat = (StatistikaAspekt) o;
        if (stat.getValue() >= this.podmienka.getValue()) {
            o.deleteObserver(this);
            stat.getVyletnik().pridajOdznak(this);
        }
    }

    public String getIkonka() {
        return ikonka;
    }

    public String getPopis() {
        return popis;
    }

    public int getPointy() {
        return pointy;
    }
    
    public Podmienka getPodmienka() {
        return podmienka;
    }
    
    /**
     * Metoda na sledovanie statistiky odznakom.
     * @param asp Statistika ktoru bude odznak sledovat
     */
    public void subscribe(StatistikaAspekt asp) {
        asp.addObserver(this);
    }
}
