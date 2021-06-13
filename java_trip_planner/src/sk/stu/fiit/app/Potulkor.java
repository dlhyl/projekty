/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.stu.fiit.app;

import sk.stu.fiit.app.user.Odznak;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.stream.Collectors;
import javax.swing.DefaultListModel;
import sk.stu.fiit.app.user.Odznak.Podmienka;
import sk.stu.fiit.app.user.StatistikaAspekt.DruhAspektu;
import sk.stu.fiit.app.user.Pouzivatel;
import sk.stu.fiit.app.user.Vyletnik;
import sk.stu.fiit.app.trip.Vylet;

/**
 * Hlavna trieda aplikacie obsahujuca zoznam uzivatelov,
 * zoznam vyletov, katalog tipov a zoznam odznakov.
 * @author dlhyl
 */
public class Potulkor {
    private final ArrayList<Pouzivatel> zoznamUzivatelov = new ArrayList<>();
    private final ArrayList<Vylet> zoznamVyletov = new ArrayList<>();
    private final Katalog katalogTipov = new Katalog();
    private final ArrayList<Odznak> zoznamOdznakov = new ArrayList<>();
    
    public Potulkor() {
        initOdznaky();
    }
    
    private void initOdznaky() {
        Odznak o1 = new Odznak("Založte svoj prvý výlet.",new Podmienka(DruhAspektu.VYLETY,1),15,"newbie.png");
        this.zoznamOdznakov.add(o1);
        Odznak o2 = new Odznak("Založte 3 turistické výlety.",new Podmienka(DruhAspektu.TURISTICKE_VYLETY,3),20,"hiking.png");
        this.zoznamOdznakov.add(o2);
        Odznak o3 = new Odznak("Založte 2 cyklistcké výlety.",new Podmienka(DruhAspektu.CYKLISTICKE_VYLETY,2),25,"cyclist.png");
        this.zoznamOdznakov.add(o3);
        Odznak o4 = new Odznak("Urobte 6 turistických výletov.",new Podmienka(DruhAspektu.TURISTICKE_VYLETY,6),100,"hiking-pro.png");
        this.zoznamOdznakov.add(o4);
        Odznak o5 = new Odznak("Prejdite 10 km.",new Podmienka(DruhAspektu.PREJDENE_KM,10),30,"distance.png");
        this.zoznamOdznakov.add(o5);
        Odznak o6 = new Odznak("Prejdite 30 km.",new Podmienka(DruhAspektu.PREJDENE_KM,30),60,"distance.png");
        this.zoznamOdznakov.add(o6);
        Odznak o7 = new Odznak("Našlapte 20 tisíc krokov.",new Podmienka(DruhAspektu.PREJDENE_KROKY,20000),50,"steps-smol.png");
        this.zoznamOdznakov.add(o7);
        Odznak o8 = new Odznak("Prejdite 100 tisíc krokov.",new Podmienka(DruhAspektu.PREJDENE_KROKY,100000),75,"steps-big.png");
        this.zoznamOdznakov.add(o8);      
    }

    public Katalog getKatalogTipov() {
        return katalogTipov;
    }
    
    /**
     * Pridanie pouzivatela do zoznamu vsetkych pouzivatelov aplikacie
     * @param p pouzivatel
     */
    public void pridajPouzivatela(Pouzivatel p) {
        if (p instanceof Vyletnik) {
            for (Odznak o: zoznamOdznakov) {
                o.subscribe(((Vyletnik)p).getStats().getAspekt(o.getPodmienka().getTyp()));
            }
        }
        zoznamUzivatelov.add(p);
    }
    
    public ArrayList<Pouzivatel> getZoznamUzivatelov() {
        return zoznamUzivatelov;
    }
    
    /**
     * Overenie ci sa pouzivatel s danym prihlasovacim menom
     * nachadza v zozname vsetkych pouzivatelov
     * @param login prihlasovacie meno pouzivatela
     * @return 
     */
    public boolean pouzivatelExists(String login) {
        return zoznamUzivatelov.stream().anyMatch(i -> i.getLogin().equals(login));
    }
    
    /**
     * Ziskanie pouzivatela podla prihlasovacieho mena a hesla.
     * @param login meno pouzivatela
     * @param pw heslo pouzivatela
     * @return 
     */
    public Pouzivatel getPouzivatel(String login, String pw) {
        for (Pouzivatel p : zoznamUzivatelov) {
            if (p.getLogin().equals(login) && p.getPassword_hash().equals(Pouzivatel.createHash(pw))) return p;
        }
        return null;
    }

    public ArrayList<Vylet> getZoznamVyletov() {
        return zoznamVyletov;
    }
    
    public void pridajVylet(Vylet v) {
        this.zoznamVyletov.add(v);
    }
    
    public ArrayList<Vyletnik> getZoznamVyletnikov() {
        ArrayList<Vyletnik> v = new ArrayList<>();
        for (Pouzivatel p : zoznamUzivatelov)
            if (p instanceof Vyletnik)
                v.add((Vyletnik)p);
        return v;        
    }
    
    /**
     * Naplnenie default list modelu pouzivatelmi, ktori zacinaju na substring
     * a nenachadzaju sa vo vylete, maximalne vsak 10 pouzivatelov.
     * @param model naplnany list model
     * @param name hladane meno pouzivatela
     * @param vylet vylet, do ktoreho ma byt uzivatel pozvany
     */
    public void getZoznamVyletnikov(DefaultListModel model, String name, Vylet vylet) {
        model.removeAllElements();
        if (name.isEmpty()) return;
        int limit = 10;
        for (Pouzivatel p : zoznamUzivatelov)
            if (p instanceof Vyletnik)
            {
                Vyletnik v = (Vyletnik) p;
                if (!vylet.isVeduci(v) && !vylet.isUcastnik(v))
                    if ((v.getCeleMeno()).toLowerCase().startsWith(name.toLowerCase()))
                    {
                        model.addElement(v);
                        if (--limit == 0) break;
                    }
            }
    }

    public ArrayList<Odznak> getZoznamOdznakov() {
        return zoznamOdznakov;
    }
    
    /**
     * Ziskanie zoznamu odznakov, ktore maju v podmienke zadanu statistiku
     * ako je v parametri d
     * @param d statistika, ktoru ma oznak sledovat
     * @return zoznam oznakov
     */
    public ArrayList<Odznak> getZoznamOdznakov(DruhAspektu d) {
        ArrayList<Odznak> arr = new ArrayList<Odznak>();
        for (Odznak o : arr)
            if (o.getPodmienka().getTyp().equals(d))
                arr.add(o);
        return arr;
    }
    
    /**
     * Ziskanie zoradeneho zoznamu vyletnikov podla poctu komunitnych bodov
     * zostupne, pre zobrazenie v komunitnom rebricku.
     * @return 
     */
    public ArrayList<Vyletnik> getRebricekVyletnikov() {
        return (ArrayList<Vyletnik>)this.getZoznamVyletnikov().stream().sorted((a,b) -> b.getStats().getPoints() - a.getStats().getPoints()).collect(Collectors.toList());
    }
    
    /**
     * Konvertor LocalDateTime datumu do Date
     * @param localDateTime LocalDate datum
     * @return Date datum
     */
    public static Date toDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }
}
