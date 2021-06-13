/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.stu.fiit.app;

import java.util.ArrayList;
import javax.swing.DefaultListModel;
import sk.stu.fiit.app.trip.MiestoZaujmu;
import sk.stu.fiit.app.trip.Trasa;

/**
 * Katalog tipov obsahuje odporucane miesta zaujmu a trasy.
 * @author dlhyl
 */
public class Katalog {
    private ArrayList<MiestoZaujmu> zoznamMiestZaujmu = new ArrayList<>();
    private ArrayList<Trasa> zoznamTras = new ArrayList<>();

    public Katalog() {
    }

    /**
     * Funkcia vlozi do list modelu miesta zaujmu a trasy, ktore zacinaju
     * retazcom name. Vlozenych je maximalne 10 vysledkov.
     * @param dlm Default List Model do ktoreho sa maju miesta a trasy vlozit
     * @param name Retazec ktorym ma hladane miesto alebo trasa zacinat 
     */
    public void getZoznamMiestaTrasy(DefaultListModel dlm, String name) {
        dlm.removeAllElements();
        if (name.isEmpty()) return;
        int limit = 10;
        for (Trasa t: zoznamTras) {
            if (t.getNazov().toLowerCase().startsWith(name.toLowerCase())) {
                dlm.addElement(t);
                if (--limit == 0) break;
            }
        }
        for (MiestoZaujmu m: zoznamMiestZaujmu) {
            if (m.getName().toLowerCase().startsWith(name.toLowerCase())) {
                dlm.addElement(m);
                if (--limit == 0) break;
            }
        }
    }
    
    /**
     * Funkcia vlozi do list modelu miesta zaujmu, ktore zacinaju
     * retazcom name. Vlozenych je maximalne 10 vysledkov.
     * @param dlm Default List Model do ktoreho sa maju miesta zaujmu vlozit
     * @param name Retazec ktorym ma hladane miesto zacinat
     */
    public void getZoznamMiestZaujmu(DefaultListModel dlm, String name) {
        dlm.removeAllElements();
        if (name.isEmpty()) return;
        int limit = 10;
        for (MiestoZaujmu m: zoznamMiestZaujmu) {
            if (m.getName().toLowerCase().startsWith(name.toLowerCase())) {
                dlm.addElement(m);
                if (--limit == 0) break;
            }
        }
    }

    public ArrayList<MiestoZaujmu> getZoznamMiestZaujmu() {
        return zoznamMiestZaujmu;
    }
    
    public ArrayList<Trasa> getZoznamTras() {
        return zoznamTras;
    }
    
    /**
     * Nacita miesta zaujmu z XML suboru
     */
    public void setZoznamMiestZaujmuFromXML() {
        CustomXMLParser parser = new CustomXMLParser();
        this.zoznamMiestZaujmu = parser.getMiestaZaujmu();
    }
    
    /**
     * Nacita trasy z XML suboru
     */
    public void setZoznamTrasFromXML() {
        CustomXMLParser parser = new CustomXMLParser();
        this.zoznamTras = parser.getTrasy(this);
    }
    
    /**
     * Najde miesto zaujmu s rovnakym nazvom ako je v parametri name
     * @param name nazov miesta zaujmu
     * @return Miesto zaujmu 
     */
    public MiestoZaujmu getMiestoZaujmuByName(String name) {
        for (MiestoZaujmu m : zoznamMiestZaujmu) {
            if (m.getName().equals(name)) return m;
        }
        return null;
    }
}
