/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.stu.fiit.app.user;

/**
 * Biometria obsahuje biometricke udaje, ktore si vyletnik
 * zaznamenal pocas vyletu. Su to kroky a prejdene kilometre.
 * @author dlhyl
 */
public class Biometria {
    private int kroky;
    private double km;
    
    public Biometria() {
    }

    public Biometria(int kroky, double km) {
        this.kroky = kroky;
        this.km = km;
    }
    
    public int getKroky() {
        return kroky;
    }

    public void setKroky(int kroky) {
        this.kroky = kroky;
    }

    public double getKm() {
        return km;
    }

    public void setKm(double km) {
        this.km = km;
    }
}
