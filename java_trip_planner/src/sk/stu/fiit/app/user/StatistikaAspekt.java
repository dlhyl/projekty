/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.stu.fiit.app.user;

import java.util.Observable;
import sk.stu.fiit.app.user.Vyletnik;
/**
 * Statistiky, ktore su evidovane a sledovane u vyletnikov.
 * Maju typ, vyletnika, ktory ich vlastni a aktualnu hodnotu.
 * Pri zmene statickych hodnot su upozornene vsetky objekty,
 * ktore danu statistiku sleduju.
 * @author dlhyl
 */
public class StatistikaAspekt extends Observable {
    public enum DruhAspektu {
        VYLETY,
        CYKLISTICKE_VYLETY,
        TURISTICKE_VYLETY,
        VODACKE_VYLETY,
        PREJDENE_KM,
        PREJDENE_KROKY,
        ODZNAKY_POCET,
        ODZNAKY_BODY
    }
    
    private final DruhAspektu name;
    private final Vyletnik vyletnik;
    private double value;

    public StatistikaAspekt(DruhAspektu name, double value, Vyletnik vyletnik) {
        this.name = name;
        this.value = value;
        this.vyletnik = vyletnik;
    }

    public DruhAspektu getName() {
        return name;
    }

    public double getValue() {
        return value;
    }

    public Vyletnik getVyletnik() {
        return vyletnik;
    }

    /**
     * Zmeni hodnotu statistiky a upozorni vsetky objekty,
     * ktore danu statistiku sleduju.
     * @param value nova hodnota danej statistiky
     */
    public void setValue(double value) {
        this.value = value;
        setChanged();
        notifyObservers();
        clearChanged();
    }
    
    /**
     * Prida hodnotu k danej statistike a upozorni vsetky objekty,
     * ktore danu statistiku sleduju.
     * @param value hodnota o kolko ma byt zvysena
     */
    public void addValue(double value) {
        this.value += value;
        setChanged();
        notifyObservers();
        clearChanged();
    }
}
