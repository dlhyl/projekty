/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.stu.fiit.app.trip;

import org.openstreetmap.gui.jmapviewer.Coordinate;

/**
 * Miesto zaujmu je miesto v trase, ktore moze byt navstivene a pridane do mapy.
 * Obsahuje suradnice, nazov a moze mat nadradeny region a makro region.
 * @author dlhyl
 */
public class MiestoZaujmu {
    private final double coordinateLong;
    private final double coordinateLat;
    private String macroRegion;
    private String region;
    private String name;

    public MiestoZaujmu(double coordinateLong, double coordinateLat, String name) {
        this.coordinateLong = coordinateLong;
        this.coordinateLat = coordinateLat;
        this.name = name;
    }

    public MiestoZaujmu(double coordinateLong, double coordinateLat, String macroRegion, String region, String name) {
        this.coordinateLong = coordinateLong;
        this.coordinateLat = coordinateLat;
        this.macroRegion = macroRegion;
        this.region = region;
        this.name = name;
    }

    public String getMacroRegion() {
        return macroRegion;
    }

    public String getRegion() {
        return region;
    }

    public String getName() {
        return name;
    }
    
    /**
     * Vratenie suradnic miesta zaujmu
     * @return suradnice miesta zaujmu
     */
    public Coordinate getCoordinate() {
        return new Coordinate(coordinateLat, coordinateLong);
    }
    
    @Override
    public String toString(){ 
        return this.getName()+", "+this.getRegion();
    }
}
