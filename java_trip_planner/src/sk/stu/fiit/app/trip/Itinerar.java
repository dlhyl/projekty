/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.stu.fiit.app.trip;

import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * Itinerar vyletu spaja miesta zaujmu s konkretnym casom.
 * @author dlhyl
 */
public class Itinerar {
    
    /**
     * Jednotka v itinerari - miesto zaujmu s datumom
     */
    public class ItinerarItem {
        private LocalDateTime datum;
        private final MiestoZaujmu miesto;

        public ItinerarItem(MiestoZaujmu miesto) {
            this.miesto = miesto;
        }

        public ItinerarItem(LocalDateTime datum, MiestoZaujmu miesto) {
            this.datum = datum;
            this.miesto = miesto;
        }
        
        public LocalDateTime getDatum() {
            return datum;
        }

        public void setDatum(LocalDateTime datum) {
            this.datum = datum;
        }

        public MiestoZaujmu getMiesto() {
            return miesto;
        }
    }
    
    private final ArrayList <ItinerarItem> itinerar = new ArrayList<>();
    
    public Itinerar() { 
    }

    public ArrayList<ItinerarItem> getItinerar() {
        return itinerar;
    }
    
    public void add(LocalDateTime date, MiestoZaujmu poi){
        getItinerar().add(new ItinerarItem(date, poi));
    }

    public void add(MiestoZaujmu miestoZaujmu) {
        getItinerar().add(new ItinerarItem(miestoZaujmu));
    }
}
