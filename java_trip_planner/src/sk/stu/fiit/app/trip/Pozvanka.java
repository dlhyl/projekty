/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.stu.fiit.app.trip;

import sk.stu.fiit.app.user.Vyletnik;

/**
 * Pozvanie vyletnika do vyletu, obsahuje odosielatela, prijemcu,
 * vylet, sprievodnu spravu a status.
 * @author dlhyl
 */
public class Pozvanka {
    private final Vyletnik odosielatel;
    private final Vyletnik prijemca;
    private final Vylet vylet;
    private final String sprava;
    private StatusPozvanky status;
            
    public enum StatusPozvanky {
        ODOSLANA,
        ZAMIETNUTA,
        POTVRDENA;
    }

    public Pozvanka(Vyletnik odosielatel, Vyletnik prijemca, Vylet vylet, String sprava) {
        this.odosielatel = odosielatel;
        this.prijemca = prijemca;
        this.vylet = vylet;
        this.sprava = sprava;
        this.status = StatusPozvanky.ODOSLANA;
    }

    public Vyletnik getOdosielatel() {
        return odosielatel;
    }

    public Vyletnik getPrijemca() {
        return prijemca;
    }

    public Vylet getVylet() {
        return vylet;
    }

    public String getSprava() {
        return sprava;
    }

    public StatusPozvanky getStatus() {
        return status;
    }

    public void setStatus(StatusPozvanky status) {
        this.status = status;
    }
    
    public boolean isPotvrdena() {
        return getStatus().equals(StatusPozvanky.POTVRDENA);
    }
    
    public boolean isZamietnuta() {
        return getStatus().equals(StatusPozvanky.ZAMIETNUTA);
    }
}
