/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.stu.fiit.app;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.logging.SimpleFormatter;
import sk.stu.fiit.gui.MainWindow;

/**
 * Vlastny logger sluziaci na zaznamenanie dolezitych udalosti do suboru potulky.log.
 * @author dlhyl
 */
public class CustomLogger {
    private static Logger logger = null;
    
    public static Logger getLogger() {
        if (logger == null) logger = loggerInit();
        return logger;
    }
    
    /**
     * Funkcia vrati nakonfigurovanu instanciu loggera
     * @return instancia loggeru 
     */
    private static Logger loggerInit() {  
        Logger l = Logger.getLogger("potulkor");
        try {
            FileHandler fh = new FileHandler(System.getProperty("user.dir")+"/potulky.log", true);
            l.addHandler(fh);
            fh.setFormatter(new SimpleFormatter()); 
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
        return l;
    }
}
