/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.stu.fiit.app.trip;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import sk.stu.fiit.app.user.Vyletnik;

/**
 * Dostupnost vyletnika obsahuje vyletnika, datum odkedy je dostupny
 * a datum dokedy je dostupny
 * @author dlhyl
 */
public class DostupnostVyletnika {
    private final Vyletnik vyletnik;
    private LocalDate datumOd;
    private LocalDate datumDo;

    public DostupnostVyletnika(Vyletnik vyletnik, LocalDate datumOd, LocalDate datumDo) {
        this.vyletnik = vyletnik;
        this.datumOd = datumOd;
        this.datumDo = datumDo;
    }

    public Vyletnik getVyletnik() {
        return vyletnik;
    }

    public LocalDate getDatumOd() {
        return datumOd;
    }

    public void setDatumOd(LocalDate datumOd) {
        this.datumOd = datumOd;
    }

    public LocalDate getDatumDo() {
        return datumDo;
    }

    public void setDatumDo(LocalDate datumDo) {
        this.datumDo = datumDo;
    }
    
    public String getDatumString() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        return datumOd.format(dtf)+" - "+datumDo.format(dtf);
    }
}
