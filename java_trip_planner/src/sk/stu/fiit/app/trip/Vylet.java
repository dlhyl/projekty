/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.stu.fiit.app.trip;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import sk.stu.fiit.app.user.Biometria;
import sk.stu.fiit.app.user.Vyletnik;

/**
 * Vylet obsahuje nazov, typ, termin, zakladatela,
 * zoznam veducich a beznych vyletnikov, trasu, 
 * kalendar dostupnosti, rozpocet a ulohy
 * @author dlhyl
 */
public class Vylet {
    private final String nazov;
    private final TypVyletu typ;
    private LocalDate beginDate;
    private LocalDate endDate;
    private final int pocet_dni;
    private final Vyletnik zakladatel;
    private final ArrayList<Vyletnik> veduci = new ArrayList<>();
    private final ArrayList<Vyletnik> pleby = new ArrayList<>();
    private final Itinerar itinerar = new Itinerar();
    private Trasa trasa = new Trasa();
    private final KalendarDostupnosti dostupnost;
    private final Rozpocet rozpocet = new Rozpocet();
    private boolean dokonceny = false;
    private boolean zruseny = false;

    
    public Vylet(String nazov, TypVyletu typ, LocalDate beginDate, LocalDate endDate, MiestoZaujmu m, Vyletnik zakladatel) {
        this.pocet_dni = (int)Duration.between(beginDate.atStartOfDay(),endDate.atStartOfDay()).toDays()+1;
        this.dostupnost = new KalendarDostupnosti(pocet_dni);
        this.nazov = nazov;
        this.typ = typ;
        this.beginDate = beginDate;
        this.endDate = endDate;
        this.pridajVeduceho(zakladatel);
        this.zakladatel = zakladatel;
        this.trasa.addPOI(m);
    }
    
    /**
     * Pridanie vyletnika do vyletu
     * @param v vyletnik
     */
    public void pridajClena(Vyletnik v) {
        v.pridajVylet(this, new Biometria());
        pleby.add(v);
    }
    
    /**
     * Pridanie veduceho do vyletu
     * @param v vyletnik (veduci)
     */
    public void pridajVeduceho(Vyletnik v) {
        v.pridajVylet(this, new Biometria());
        veduci.add(v);
    }
    
    /**
     * Povysenie bezneho clena na veduceho
     * @param v vyletnik (bezny clen)
     */
    public void povysVyletnika(Vyletnik v) {
        this.pleby.remove(v);
        this.veduci.add(v);
    }
    
    /**
     * Znizenie hodnosti veduceho na bezneho clena
     * @param v vyletnik (veduci)
     */
    public void znizVeduceho(Vyletnik v) {
        this.veduci.remove(v);
        this.pleby.add(v);
    }
    
    /**
     * Vrati termin vyletu
     * @return datum vo formate dd.MM.yyyy 
     */
    public String getDatumString() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        return beginDate.format(dtf)+" - "+endDate.format(dtf);
    }
    
    /**
     * Overi ci je dany vyletnik veduci clen alebo je bezny clen
     * @param v vyletnik
     * @return true, ak je veduci, inak false
     */
    public boolean isVeduci(Vyletnik v) {
        return this.getVeduci().stream().anyMatch(a -> a.equals(v));
    }
    
    /**
     * Overi ci je dany vyletnik iba bezny clen alebo je veduci
     * @param v vyletnik
     * @return true, ak je bezny clen, inak false
     */
    public boolean isUcastnik(Vyletnik v) {
        return this.getPleby().stream().anyMatch(a -> a.equals(v));
    }
    
    /**
     *  Vrati celkovy pocet ucastnikov vyletu
     * @return celkovy pocet ucastnikov vyletu
     */
    public int getPocetUcastnikov() {
        return this.getPleby().size() + this.getVeduci().size();
    }
    
    /**
     * Pri odstraneni vyletnika z vyletu sa vysporiadaju vsetky
     * vyletnikove zavazky (pridelene ulohy) a odstrania jeho dostupne terminy
     * @param vyletnik 
     */
    public void odstranVyletnika(Vyletnik vyletnik) {
        this.rozpocet.vyrovnajZavazky(vyletnik);
        this.dostupnost.odstranDostupnostVyletnika(vyletnik);
        int index = this.veduci.indexOf(vyletnik);
        if (index > -1) this.veduci.remove(index);
        else this.pleby.remove(vyletnik); 
    }
    
    /**
     * Pridanie vsetkych miest zaujmu z trasy vyletu do itinerara
     */
    public void pridajTrasuDoItinerara() {
        for (MiestoZaujmu m : this.trasa.getMiestaZaujmu()) {
            this.itinerar.add(m);
        }
    }
    
    /**
     * Pri zruseni vyletu sa zmeni status vyletu na zruseny
     * a odstrani vsetkym ucastnikom vyletu statistiku o vylete
     */
    public void zrusVylet() {
       this.zruseny = true;
       pleby.forEach(pleb -> ((Vyletnik)pleb).getStats().odstranVylet(this));
       veduci.forEach(pleb -> ((Vyletnik)pleb).getStats().odstranVylet(this));
    }
    
    public void pridajUlohu(Uloha u) {
        this.getRozpocet().pridajUlohu(u);
    }
    
    public LocalDate getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(LocalDate beginDate) {
        this.beginDate = beginDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public ArrayList<Vyletnik> getVeduci() {
        return veduci;
    }

    public ArrayList<Vyletnik> getPleby() {
        return pleby;
    }

    public Itinerar getItinerar() {
        return itinerar;
    }

    public KalendarDostupnosti getDostupnost() {
        return dostupnost;
    }

    public boolean isDokonceny() {
        return dokonceny;
    }

    public void setDokonceny(boolean dokonceny) {
        this.dokonceny = dokonceny;
    }

    public String getNazov() {
        return nazov;
    }

    public TypVyletu getTyp() {
        return typ;
    }

    public Rozpocet getRozpocet() {
        return rozpocet;
    }
    
    public Trasa getTrasa() {
        return trasa;
    }
    
    public void setTrasa(Trasa trasa) {
        this.trasa = trasa;
    }
    
    public boolean isZakladatel(Vyletnik v){
        return zakladatel.equals(v);
    }

    public int getPocet_dni() {
        return pocet_dni;
    }

    public boolean isZruseny() {
        return zruseny;
    }
}
