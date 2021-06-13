/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.stu.fiit.app.trip;

import java.util.ArrayList;
import java.util.stream.Collectors;
import javafx.util.Pair;
import org.openstreetmap.gui.jmapviewer.Coordinate;

/**
 * Trasa sa sklada z urciteho poctu miest zaujmu
 * @author dlhyl
 */
public class Trasa {
    private String nazov;
    private final ArrayList<MiestoZaujmu> miestaZaujmu;
    private final ArrayList<Double> vzdialenosti;
    
    /**
     * Vytvorenie prazdnej trasy
     */
    public Trasa() {
        this.miestaZaujmu = new ArrayList<>();
        this.vzdialenosti = new ArrayList<>();
    }
    
    /**
     * Vytvorenie prazdnej trasy s nazvom
     * @param nazov 
     */
    public Trasa(String nazov) {
        this.nazov = nazov;
        this.miestaZaujmu = new ArrayList<>();
        this.vzdialenosti = new ArrayList<>();
    }

    /**
     * Vytvorenie trasy s nazvom, miestami zaujmu a vzdialenostami
     * @param nazov nazov trasy
     * @param miestaZaujmu zoznam miest zaujmu
     * @param vzdialenosti zoznam vzdialenosti trasy
     */
    public Trasa(String nazov, ArrayList<MiestoZaujmu> miestaZaujmu, ArrayList<Double> vzdialenosti) {
        this.nazov = nazov;
        this.miestaZaujmu = miestaZaujmu;
        this.vzdialenosti = vzdialenosti;
    }

    /**
     * Pridanie miesta zaujmu na koniec trasy
     * a prepocitanie vzdialenosti
     * @param poi miesto zaujmu
     */
    public void addPOI(MiestoZaujmu poi){
        this.miestaZaujmu.add(poi);
        int s = this.vzdialenosti.size();
        this.vzdialenosti.add(s == 0 ? new Double(0d) : this.vzdialenosti.get(s-1)+getDistance(this.miestaZaujmu.get(s-1).getCoordinate(), poi.getCoordinate()));
    }
    
    /**
     * Odstranenie miesta zaujmu z trasy
     * a prepocitanie vzdialenosti
     * @param poi 
     */
    public void removePOI(MiestoZaujmu poi) {
        int index = this.miestaZaujmu.indexOf(poi);
        if (index != -1) {
            this.miestaZaujmu.remove(index);
            this.vzdialenosti.remove(index);
            updateVzdialenosti(Math.max(index - 1, 0));
        }
    }
    
    /**
     * Vymena dvoch miesta zaujmu vo vylete
     * a prepocitanie vzdialenosti
     * @param index1 index prveho miesta zaujmu
     * @param index2 index druheho miesta zaujmu
     */
    public void swapPOIs(int index1, int index2) {
        if (index1 != -1 && index2 != -1 && index1<this.miestaZaujmu.size() && index2<this.miestaZaujmu.size()) {
            MiestoZaujmu m = this.miestaZaujmu.get(index1);
            this.miestaZaujmu.set(index1, this.miestaZaujmu.get(index2));
            this.miestaZaujmu.set(index2, m);
            this.updateVzdialenosti(Math.min(index1, index2));
        }
    }
    
    /**
     * Vratenie zoznamu suradnic vsetkych miest zaujmu z trasy
     * @return zoznam suradnic
     */
    public ArrayList<Coordinate> getCoordinatesArray(){
        return (ArrayList<Coordinate>) miestaZaujmu.stream().map(item -> item.getCoordinate()).collect(Collectors.toList());
    }

    /**
     * Prepocitanie vzdialenost od miesta zaujmu na indexe v parametri
     * @param index index miesta zaujmu
     */
    private void updateVzdialenosti(int index) {
        for (int i = index; i<miestaZaujmu.size(); i++) {
            this.vzdialenosti.set(i, (i == 0) ? new Double(0d) : this.vzdialenosti.get(i-1)+getDistance(this.miestaZaujmu.get(i-1).getCoordinate(), this.miestaZaujmu.get(i).getCoordinate()));
        }
    }
    
    /**
     * Vratenie miest zaujmu trasy spolu s kumulativnou vzdielenostou
     * @return zoznam miest zaujmu a vzdialnosti
     */
    public ArrayList<Pair<MiestoZaujmu, Double>> getMiestaVzdialenosti() {
        ArrayList<Pair<MiestoZaujmu, Double>> arr = new ArrayList<Pair<MiestoZaujmu, Double>>();
        if (miestaZaujmu.size() != vzdialenosti.size()) return arr;
        int i = 0;
        for (MiestoZaujmu m : this.miestaZaujmu) 
            arr.add(new Pair(m,this.vzdialenosti.get(i++)));
        return arr;
    }
    
    /**
     * Vypocitanie vzdusnej vzdialenost medzi dvomi suradnicami
     * @param a suradnica miesta 1
     * @param b suradnica miesta 2
     * @return vzdialnost v km
     */
    public static Double getDistance(Coordinate a, Coordinate b){
        final int R = 6371;
        Double latDistance = toRad(b.getLat()-a.getLat());
        Double lonDistance = toRad(b.getLon()-a.getLon());
        Double s1 = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) + Math.cos(toRad(a.getLat())) * Math.cos(toRad(b.getLat())) * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        Double s2 = 2 * Math.atan2(Math.sqrt(s1), Math.sqrt(1-s1));
        Double distance = R * s2;
        return distance;
    }
 
    /**
     * Konverzia stupnov do radianov
     * @param value stupne
     * @return radiany
     */
    private static Double toRad(Double value) {
        return value * Math.PI / 180;
    }
    
    public ArrayList<MiestoZaujmu> getMiestaZaujmu() {
        return miestaZaujmu;
    }
    
    public ArrayList<Double> getVzdialenosti() {
        return vzdialenosti;
    }

    public String getNazov() {
        return nazov;
    }
    
    @Override
    public String toString() {
        return this.getNazov();
    }
}
