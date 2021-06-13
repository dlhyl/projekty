/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.stu.fiit.app;

import java.io.InputStream;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import sk.stu.fiit.app.trip.MiestoZaujmu;
import sk.stu.fiit.app.trip.Trasa;

/**
 * Vlastny parser na ziskanie preddefinovanych miest a tras z XML suboru.
 * @author dlhyl
 */
public class CustomXMLParser {
    private static final String filepath = "/sk/stu/fiit/app/PointsOfInterest.xml";

    public CustomXMLParser() {   
    }
    
    /**
     * Funkcia na ziskanie DOM Documentu z vlastneho XML suboru.
     * @return DOM Document
     */
    private Document getDocument(){
        try {
            InputStream inputFile = getClass().getResourceAsStream(filepath);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();
            return doc;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Funkcia vrati vsetky miesta zaujmu, ktore sa nachadzaju v XML subore.
     * @return zoznam miest zaujmu
     */
    public ArrayList<MiestoZaujmu> getMiestaZaujmu() {
        Document doc = this.getDocument();
        ArrayList<MiestoZaujmu> zoznam = new ArrayList<>();
        if (doc == null) return zoznam;
        NodeList pointsNode = doc.getElementsByTagName("points");
        // points
        for (int i = 0; i < pointsNode.getLength(); i++) {
            Node n = pointsNode.item(i);
            if (n.getNodeType() == Node.ELEMENT_NODE) {
                Element e = (Element) n;
                NodeList macroregions = e.getElementsByTagName("macroregion");

                // macroregions
                for (int j = 0; j < macroregions.getLength(); j++) {
                    Node n_mr = macroregions.item(j);
                    if (n_mr.getNodeType() == Node.ELEMENT_NODE) {
                        String macroregion = ((Element)n_mr).getAttribute("name");
                        NodeList regions = ((Element) n_mr).getElementsByTagName("region");

                        //regions
                        for (int k = 0; k < regions.getLength(); k++) {
                            Node n_r = regions.item(k);
                            if (n_r.getNodeType() == Node.ELEMENT_NODE) {
                                String region = ((Element)n_r).getAttribute("name");
                                NodeList cities = ((Element) n_r).getElementsByTagName("city");

                                // cities
                                for (int l = 0; l < cities.getLength(); l++) {
                                    Node n_c = cities.item(l);
                                    if (n_c.getNodeType() == Node.ELEMENT_NODE) {
                                        Element e_c = (Element) n_c;
                                        String lng = e_c.getAttribute("long");
                                        String lat = e_c.getAttribute("lat");
                                        String city = e_c.getTextContent();
                                        zoznam.add(new MiestoZaujmu(new Double(lng).doubleValue(), new Double(lat).doubleValue(), macroregion, region, city));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return zoznam;
    }
    
    /**
     * Funkcia vrati trasy, ktore sa nachadzaju v XML subore.
     * Trasa pozostava z viacerych miest zaujmu. Funkcia skusi naparovat
     * existujuce miesta zaujmu z katalogu s miestami zaujmu v trase.
     * @param katalog Katalog obsahujuci miesta zaujmu a trasy
     * @return zoznam tras
     */
    public ArrayList<Trasa> getTrasy(Katalog katalog) {
        Document doc = this.getDocument();
        ArrayList<Trasa> zoznam = new ArrayList<>();
        if (doc == null) return zoznam;
        NodeList pointsNode = doc.getElementsByTagName("routes");
        // all
        for (int i = 0; i < pointsNode.getLength(); i++) {
            Node n = pointsNode.item(i);
            if (n.getNodeType() == Node.ELEMENT_NODE) {
                Element e = (Element) n;
                NodeList routes = e.getElementsByTagName("route");

                // routes
                for (int j = 0; j < routes.getLength(); j++) {
                    Node n_r = routes.item(j);
                    if (n_r.getNodeType() == Node.ELEMENT_NODE) {
                        String route_name = ((Element)n_r).getAttribute("name");
                        Trasa t = new Trasa(route_name);
                        NodeList cities = ((Element) n_r).getElementsByTagName("city");

                        // cities
                        for (int k = 0; k < cities.getLength(); k++) {
                            Node n_c = cities.item(k);
                            if (n_c.getNodeType() == Node.ELEMENT_NODE) {
                                Element e_c = (Element) n_c;
                                String lng = e_c.getAttribute("long");
                                String lat = e_c.getAttribute("lat");
                                String city = e_c.getTextContent();
                                MiestoZaujmu m = null;
                                if (katalog != null) 
                                    m = katalog.getMiestoZaujmuByName(city);
                                if (m == null) 
                                    m = new MiestoZaujmu(new Double(lng).doubleValue(), new Double(lat).doubleValue(), city);
                                t.addPOI(m);
                            }
                        }
                        zoznam.add(t);
                    }
                }
            }
        }
        return zoznam;
    }
}
