/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.stu.fiit.app.trip;

import java.net.URL;

/**
 * Enumeracia typov vyletu spolu s metodou na ziskanie nazvu a ikonky
 * @author dlhyl
 */
public enum TypVyletu {
    TURISTICKY("Turistický","hiking.png"), CYKLISTICKY("Cyklistický","cycling.png"), VODACKY("Vodácky","watersport.png");
    
    public final String nazov;
    public final URL iconUrl;
    
    private TypVyletu(String nazov, String iconName) {
        this.nazov = nazov;
        this.iconUrl = getClass().getResource("/sk/stu/fiit/assets/icons/"+iconName);
    }
    
    public URL getIconURL(){
        return iconUrl;
    }
    
    @Override 
    public String toString() { 
        return this.nazov; 
    }
}
