/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.stu.fiit.gui.utils;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.MapMarkerCircle;
import org.openstreetmap.gui.jmapviewer.MapPolygonImpl;
import org.openstreetmap.gui.jmapviewer.tilesources.AbstractOsmTileSource;
import sk.stu.fiit.app.trip.MiestoZaujmu;
import sk.stu.fiit.app.trip.Trasa;

/**
 * Custom Map using JMapViewer ( https://wiki.openstreetmap.org/wiki/JMapViewer )
 * @author dlhyl
 */
public class MapPanel extends javax.swing.JPanel {
    private final JMapViewer mapa;
    private Trasa defaultTrasa = null;
    private Trasa novaTrasa = null;
    private final Color defaultColor = Color.RED;
    private final Color newColor = Color.YELLOW;
    private final int defaultStroke = 4;
          
    /**
     * Get the map tiles from freemap.sk
     */
    public static class MapkaResource extends AbstractOsmTileSource {
        private static final String PATTERN = "https://outdoor.tiles.freemap.sk";
        public MapkaResource() {
            super("freemapka", PATTERN, "standard");
            modTileFeatures = true;
        }
    }
    
    /**
     * Marker with custom style
     */
    private static class CustomMarker extends MapMarkerCircle {
        public CustomMarker(String name, Coordinate coord) {
            super(null, name, coord, 8, STYLE.FIXED, getDefaultStyle());
        }
    }
    
    public MapPanel() {
        this.mapa = new JMapViewer();
        initComponents();
        initMap();
    }
    
    public MapPanel(Trasa t) {
        this.defaultTrasa = t;
        this.mapa = new JMapViewer();
        initComponents();
        initMap();
        zobrazTrasu(t, defaultColor, defaultStroke);
    }
    
    public MapPanel(Trasa t, Trasa nova) {
        this.defaultTrasa = t;
        this.novaTrasa = nova;
        this.mapa = new JMapViewer();
        initComponents();
        initMap();
        zobrazTrasu(t, defaultColor, defaultStroke);
    }
    
    private void initMap() {
        this.mapa.setScrollWrapEnabled(false);
        this.mapa.setTileSource(new MapkaResource());
        this.mapa.setDisplayPosition(new Coordinate(48.730556, 19.457222), 7);
        add(this.mapa, BorderLayout.CENTER);
    }
    
    public void zobrazTrasu(Trasa t) {
        zobrazTrasu(t, newColor, defaultStroke);
    }
    
    public void zobrazDefaultTrasu() {
        zobrazTrasu(defaultTrasa, defaultColor, defaultStroke);
    }
    
    private void zobrazTrasu(Trasa t, Color c, int stroke) {
        ArrayList<MiestoZaujmu> miesta = t.getMiestaZaujmu();
        for (MiestoZaujmu miesto : miesta) this.pridajMiesto(miesto, c);
        
        ArrayList<Coordinate> r1 = t.getCoordinatesArray();
        ArrayList<Coordinate> r2 = t.getCoordinatesArray();
        Collections.reverse(r2);
        if (r2.size() > 0)
            r2.remove(0);
        r1.addAll(r2);
        MapPolygonImpl m = new MapPolygonImpl(r1);
        m.setStroke(new BasicStroke(stroke));
        m.setColor(c);
        this.mapa.addMapPolygon(m);
    }
    
    private void pridajMiesto(MiestoZaujmu m, Color c) {
        this.pridajBod(m.getCoordinate(), "", c);
    }
    
    public void pridajTrasu(Trasa t) {
        if (this.novaTrasa == null) this.novaTrasa = new Trasa(defaultTrasa.getNazov(), defaultTrasa.getMiestaZaujmu(), defaultTrasa.getVzdialenosti());
        for (MiestoZaujmu m : t.getMiestaZaujmu()) {
            this.novaTrasa.addPOI(m);
        }
        this.resetMapa();
        this.zobrazTrasu(novaTrasa);
    }
    
    public void pridajMiesto(MiestoZaujmu m) {
        if (this.novaTrasa == null) 
            this.novaTrasa = new Trasa(defaultTrasa.getNazov(), defaultTrasa.getMiestaZaujmu(), defaultTrasa.getVzdialenosti());
        this.novaTrasa.addPOI(m);
        this.resetMapa();
        this.zobrazTrasu(novaTrasa);
    }
    
    public void pridajBod(Coordinate bod) {
        this.pridajBod(bod, "", newColor);
    }
    
    public void pridajBod(Coordinate bod, String nazov) {
        this.pridajBod(bod, nazov, newColor);
    }
    
    private void pridajBod(Coordinate bod, String nazov, Color c) {
        CustomMarker cm = new CustomMarker(nazov, bod);
        cm.getStyle().setBackColor(c);
        cm.getStyle().setFont(new java.awt.Font("Gill Sans MT", 1, 15));
        this.mapa.addMapMarker(cm);
    }
    
    public void resetMapa() {
        this.mapa.removeAllMapMarkers();
        this.mapa.removeAllMapPolygons();
    }
    
    public void resetDisplay() {
        this.mapa.setDisplayPosition(new Coordinate(48.730556, 19.457222), 7);
    }
    
    public void setDefaultTrasa(Trasa t) {
        this.defaultTrasa = t;
    }

    public void setNovaTrasa(Trasa novaTrasa) {
        this.novaTrasa = novaTrasa;
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setLayout(new java.awt.BorderLayout());
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
