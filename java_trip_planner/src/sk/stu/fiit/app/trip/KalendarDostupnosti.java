/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.stu.fiit.app.trip;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import sk.stu.fiit.app.user.Vyletnik;

/**
 * Do kalendaru dostupnosti si ucastnici vyletu zapisuju 
 * svoje dostupne terminy a aplikacia vygeneruje odporucane
 * terminy konanie na zakladade poctu dni a uspokojenia najvyssieho
 * poctu vyletnikov.
 * @author dlhyl
 */
public class KalendarDostupnosti {
    
    /**
     * Trieda popisujuci najlepsi vhodny termin alebo
     * vyhovujuci termin vyletnika, obsahuje datum od a do,
     * zhodu a volitelne aj vyletnika
     */
    public class NajlepsiTermin {
        private final LocalDate datumOd;
        private final LocalDate datumDo;
        private final int zhoda;
        private Vyletnik vyletnik;

        public NajlepsiTermin(LocalDate datumOd, LocalDate datumDo, int zhoda) {
            this.datumOd = datumOd;
            this.datumDo = datumDo;
            this.zhoda = zhoda;
        }

        public NajlepsiTermin(LocalDate datumOd, LocalDate datumDo, Vyletnik v, int zhoda) {
            this.datumOd = datumOd;
            this.datumDo = datumDo;
            this.vyletnik = v;
            this.zhoda = zhoda;
        }
        
        public LocalDate getDatumOd() {
            return datumOd;
        }

        public LocalDate getDatumDo() {
            return datumDo;
        }

        public int getZhoda() {
            return zhoda;
        }

        public Vyletnik getVyletnik() {
            return vyletnik;
        }
    }
    
    /**
     * Trieda najlepsie terminy vykonava generovanie najvhodnejsich
     * terminov konania vyletu podla momentalne dostupnosti vyletnikov.
     * Obsahuje pocet dni, kolko ma vylet trvat, pomocne pole terminy a 
     * najvhodnejsie (odporucane) terminy.
     */
    public class NajlepsieTerminy {
        private final ArrayList<NajlepsiTermin> terminy = new ArrayList<>();
        private ArrayList<NajlepsiTermin> najlepsieTerminy = new ArrayList<NajlepsiTermin>();
        private final int days;
        
        public NajlepsieTerminy(int days) {
            this.days = days;
        }
        
        /**
         * Prida dostupne terminy vyletnika do pomocneho pola terminy a updatne
         * najvhodnejsie terminy konania vyletu.
         * @param from zaciatok intervalu dostupnosti
         * @param to koniec intervalu dostupnosti
         */
        private void pridajTermin(NajlepsiTermin from, NajlepsiTermin to) {
            this.terminy.add(from);
            this.terminy.add(to);
            updateNaj();
        }
        
        /**
         * Najde a odstrani dostupny termin vyletnika z pomocneho pola terminy,
         * pri odstraneni dostupneho terminu vyletnika sa musia updatenut najvhodnejsie
         * terminy konania vyletu.
         * @param v Vyletnik
         */
        private void odstranTerminy(Vyletnik v) {
            this.terminy.removeIf(termin -> termin.getVyletnik().equals(v));
            updateNaj();
        }
        
        /**
         * Prepocitanie najlepsich terminov podla najvacsieho prekryvu datumov.
         * Najprv datumy zoradi vzostupne, vypocita prekryvy a zoradi vygenerovane
         * intervaly podla najvyssej zhody.
         */
        private void updateNaj() {
            Collections.sort(terminy, Comparator.comparing(NajlepsiTermin::getDatumOd));
            ArrayList<NajlepsiTermin> arr = new ArrayList<NajlepsiTermin>();
            for (int i = 0; i<terminy.size()-1; i++) {
                NajlepsiTermin t1 = terminy.get(i);
                NajlepsiTermin t2 = terminy.get(i+1);
                int zhoda = t1.getZhoda()+ (i > 0 ? arr.get(i-1).getZhoda() : 0);
                arr.add(new NajlepsiTermin(t1.getDatumOd(), zhoda == 1 ? t1.getDatumDo() : t2.getDatumOd(), zhoda));
            }
            Collections.sort(arr, Comparator.comparing(NajlepsiTermin::getZhoda).reversed());
            this.najlepsieTerminy = arr;
        }
        
        /**
         * Ziskanie odporucanych navrhov terminov z vygenerovanych intervalov.
         * Vrati maximalne taky pocet terminov ako je v parametri.
         * @param pocet maximalny pocet terminov, ktore ma funkcia vratit
         * @return zoznam navrhovanych terminov konania vyletu
         */
        public ArrayList<NajlepsiTermin> getNavrhyTerminov(int pocet) {
            ArrayList<NajlepsiTermin> arr = new ArrayList<NajlepsiTermin>();
            for (NajlepsiTermin n : this.najlepsieTerminy) {
                if (n.getDatumDo() != null && n.getZhoda() > 0) {
                    for (LocalDate start = n.getDatumOd(); start.isBefore(n.getDatumDo().minusDays(days-2)); start = start.plusDays(1)) {
                        boolean existuje = false;
                        if (n.getZhoda() == 1)
                            for (NajlepsiTermin nt : arr) {if (nt.getDatumOd().equals(start) && nt.getDatumDo().equals(start.plusDays(days-1))) { existuje = true;break;} }
                            
                        if (!existuje) 
                            arr.add(new NajlepsiTermin(start, start.plusDays(days-1), (n.getZhoda()*200)/terminy.size()));
                        if (arr.size() == pocet) break;
                    } 
                }
            }
            return arr;
        }
        
        /**
         * Ziskanie terminu vyletu s uplne najvacsou zhodou
         * @return najlepsi termin, v pripade ak neexistuje vrati null
         */
        public NajlepsiTermin getNajlepsiTermin() {
            for (NajlepsiTermin n : this.najlepsieTerminy) {
                if (n.getDatumDo() != null && n.getZhoda() > 0) {
                    for (LocalDate start = n.getDatumOd(); start.isBefore(n.getDatumDo().minusDays(days-2)); start = start.plusDays(1)) {
                        return new NajlepsiTermin(start, start.plusDays(days-1), (n.getZhoda()*200)/terminy.size());
                    } 
                }
            }
            return null;
        }

        public ArrayList<NajlepsiTermin> getNajlepsieTerminy() {
            return najlepsieTerminy;
        }
    }

    private final ArrayList<DostupnostVyletnika> dostupnostiVyletnikov = new ArrayList<>();
    private final NajlepsieTerminy najTerminy;

    public KalendarDostupnosti(int pocet) {
        this.najTerminy = new NajlepsieTerminy(pocet);
    }

    public ArrayList<DostupnostVyletnika> getDostupnostiVyletnikov() {
        return dostupnostiVyletnikov;
    }
    
    /**
     * Prida dostupnost vyletnika do zoznamu dostupnosti
     * a updatnu sa najlepsie terminy konania vyletu
     * @param d dostupnost vyletnika
     */
    public void pridajDostupnost(DostupnostVyletnika d) {
        DostupnostVyletnika dost = this.getDostupnostVyletnika(d.getVyletnik());
        if (dost == null) {
            this.dostupnostiVyletnikov.add(d);
            this.najTerminy.pridajTermin(new NajlepsiTermin(d.getDatumOd(),d.getDatumDo(), d.getVyletnik(), 1), new NajlepsiTermin(d.getDatumDo(), null, d.getVyletnik(), -1));
        }
    }

    /**
     * Updatnutie dostupnosti vyletnika a updatnutie najlepsich terminov konania vyletu
     * @param d dostupnost vyletnika
     * @param dOd novy datum odkedy je dostupny
     * @param dDo novy datum dokedy je dostupny
     */
    public void updateDostupnost(DostupnostVyletnika d, LocalDate dOd, LocalDate dDo) {
        d.setDatumOd(dOd);
        d.setDatumDo(dDo);
        najTerminy.odstranTerminy(d.getVyletnik());
        najTerminy.pridajTermin(new NajlepsiTermin(d.getDatumOd(),d.getDatumDo(), d.getVyletnik(), 1), new NajlepsiTermin(d.getDatumDo(), null, d.getVyletnik(), -1));
    }

    /**
     * Ziskanie dostupnosti vyletnika
     * @param v vyletnik
     * @return dostupnost vyletnika
     */
    public DostupnostVyletnika getDostupnostVyletnika(Vyletnik v) {
        for (DostupnostVyletnika dv : this.dostupnostiVyletnikov) {
            if (dv.getVyletnik().equals(v)) return dv;
        }
        return null;
    }
    
    /**
     * Odstranenie dostupnosti vyletnika
     * @param v vyletnik
     */
    public void odstranDostupnostVyletnika(Vyletnik v) {
        for (DostupnostVyletnika dv : this.dostupnostiVyletnikov) {
            if (dv.getVyletnik().equals(v)) { 
                this.dostupnostiVyletnikov.remove(dv);
                this.najTerminy.odstranTerminy(v);
            }
        }
    }

    public NajlepsieTerminy getNajTerminy() {
        return najTerminy;
    }
}
