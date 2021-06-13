/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.stu.fiit.app.user;

import java.util.Observable;
import java.util.Observer;
import sk.stu.fiit.app.user.StatistikaAspekt.DruhAspektu;
import sk.stu.fiit.app.trip.Vylet;

/**
 * Vsetky statisticke hodnoty, ktore vyletnik eviduje
 * a body v komunitnom rebricku
 * @author dlhyl
 */
public class Statistika implements Observer{
    private int points = 0;
    private final StatistikaAspekt pocetVyletov;
    private final StatistikaAspekt pocetTuristickyVyletov;
    private final StatistikaAspekt pocetVodackychVyletov;
    private final StatistikaAspekt pocetCyklistickyVyletov;
    private final StatistikaAspekt pocetKm;
    private final StatistikaAspekt pocetKrokov;
    private final StatistikaAspekt pocetOdznakov;
    private final StatistikaAspekt odznakBody;
    
    public Statistika(Vyletnik v) {
        pocetVyletov = new StatistikaAspekt(DruhAspektu.VYLETY,0d, v);
        pocetTuristickyVyletov = new StatistikaAspekt(DruhAspektu.TURISTICKE_VYLETY,0d, v);
        pocetVodackychVyletov = new StatistikaAspekt(DruhAspektu.VODACKE_VYLETY,0d, v);
        pocetCyklistickyVyletov = new StatistikaAspekt(DruhAspektu.CYKLISTICKE_VYLETY,0d, v);
        pocetKm = new StatistikaAspekt(DruhAspektu.PREJDENE_KM,0d, v);
        pocetKrokov = new StatistikaAspekt(DruhAspektu.PREJDENE_KROKY,0d, v);
        pocetOdznakov = new StatistikaAspekt(DruhAspektu.ODZNAKY_POCET,0d, v);
        odznakBody = new StatistikaAspekt(DruhAspektu.ODZNAKY_BODY,0d, v);
        odznakBody.addObserver(this);
        pocetKrokov.addObserver(this);
        pocetKm.addObserver(this);
    }
    
    /**
     * Zvyseni poctu vyletov pri pridani vyletu
     * @param v vylet
     */
    public void priradVylet(Vylet v) {
        switch(v.getTyp()){
            case TURISTICKY:
                pocetTuristickyVyletov.addValue(1d);
                break;
            case CYKLISTICKY:
                pocetCyklistickyVyletov.addValue(1d);
                break;
            case VODACKY:
                pocetVodackychVyletov.addValue(1d);
                break;
        }
        pocetVyletov.addValue(1d);
    }
    
    /**
     * Znizenie poctu vyletov pri odstraneni vyletu
     * @param v vylet
     */
    public void odstranVylet(Vylet v) {
        switch(v.getTyp()){
            case TURISTICKY:
                pocetTuristickyVyletov.addValue(-1d);
                break;
            case CYKLISTICKY:
                pocetCyklistickyVyletov.addValue(-1d);
                break;
            case VODACKY:
                pocetVodackychVyletov.addValue(-1d);
                break;
        }
        pocetVyletov.addValue(-1d);
    }

    /**
     * Zvysenie poctu odznakov a ziskanie bodov 
     * pri odomknuti odznaku
     * @param o 
     */
    public void pridajOdznak(Odznak o) {
        odznakBody.addValue(o.getPointy());
        pocetOdznakov.addValue(1d);
    }
    
    public void pridajKroky(int kroky) {
        pocetKrokov.addValue((double)kroky);
    }
    
    public void pridajKm(double km) {
        pocetKm.addValue(km);
    }
    
    public int getPoints() {
        return points;
    } 

    public int getPocetVyletov() {
        return (int)pocetVyletov.getValue();
    }

    public int getPocetTuristickyVyletov() {
        return (int)pocetTuristickyVyletov.getValue();
    }

    public int getPocetVodackychVyletov() {
        return (int)pocetVodackychVyletov.getValue();
    }

    public int getPocetCyklistickyVyletov() {
        return (int)pocetCyklistickyVyletov.getValue();
    }

    public double getPocetKm() {
        return pocetKm.getValue();
    }

    public int getPocetKrokov() {
        return (int)pocetKrokov.getValue();
    }
    
    public int getPocetOdznakov() {
        return (int)pocetOdznakov.getValue();
    }
    
    public int getPocetBodovOdznaky() {
        return (int)odznakBody.getValue();
    }
    
    /**
     * Ziskanie statistiky podla druhu sledovanej statistiky
     * @param d druh sledovanej statstiky
     * @return konkretna statistika vyletnika
     */
    public StatistikaAspekt getAspekt(DruhAspektu d){ 
        switch(d){
            case VYLETY:
                return pocetVyletov;
            case CYKLISTICKE_VYLETY:
                return pocetCyklistickyVyletov;
            case TURISTICKE_VYLETY:
                return pocetTuristickyVyletov;
            case VODACKE_VYLETY:
                return pocetVodackychVyletov;
            case PREJDENE_KM:
                return pocetKm;
            case PREJDENE_KROKY:
                return pocetKrokov;
            case ODZNAKY_BODY:
                return odznakBody;
            case ODZNAKY_POCET:
                return odznakBody;
        }
        return null;
    }

    /**
     * Vypocet bodov pre komunitny rebricek, pri zmene jednej zo sledovanych
     * statistik (body za odznaky, pocet krokov, pocet km)
     * body = suma bodov za odznaky + 1 bod za kazdych 10 000 krokov + 1 bod za kazdych 10 km
     * @param o
     * @param arg 
     */
    @Override
    public void update(Observable o, Object arg) {
        this.points = (int) (odznakBody.getValue()+ (pocetKrokov.getValue()/10000) + (pocetKm.getValue()/10));
    }
}
