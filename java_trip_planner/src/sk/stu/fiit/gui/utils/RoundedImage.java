/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.stu.fiit.gui.utils;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;

/**
 * ImageIcon in circular shape
 * @author dlhyl
 */
public class RoundedImage {
    public static ImageIcon getImage(ImageIcon i) {
        BufferedImage mask = new BufferedImage(i.getIconWidth(), i.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
        int diameter = Math.min(mask.getWidth(), mask.getHeight());
        Graphics2D g2d = mask.createGraphics();
        g2d.fillOval(0, 0, diameter - 1, diameter - 1);
        g2d.dispose();
        BufferedImage masked = new BufferedImage(diameter, diameter, BufferedImage.TYPE_INT_ARGB);
        g2d = masked.createGraphics();
        int x = (diameter - i.getIconWidth()) / 2;
        int y = (diameter - i.getIconHeight()) / 2;
        i.paintIcon(null, g2d, x, y);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.DST_IN));
        g2d.drawImage(mask, 0, 0, null);
        g2d.dispose();
        return new ImageIcon(masked);
    }
    
    public static ImageIcon getImage(ImageIcon icon, int size) {
        ImageIcon i = resizeImage(icon, size);
        BufferedImage mask = new BufferedImage(i.getIconWidth(), i.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
        int diameter = Math.min(mask.getWidth(), mask.getHeight());
        Graphics2D g2d = mask.createGraphics();
        g2d.fillOval(0, 0, diameter - 1, diameter - 1);
        g2d.dispose();
        BufferedImage masked = new BufferedImage(diameter, diameter, BufferedImage.TYPE_INT_ARGB);
        g2d = masked.createGraphics();
        int x = (diameter - i.getIconWidth()) / 2;
        int y = (diameter - i.getIconHeight()) / 2;
        i.paintIcon(null, g2d, x, y);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.DST_IN));
        g2d.drawImage(mask, 0, 0, null);
        g2d.dispose();
        return new ImageIcon(masked);
    }
    
    public static ImageIcon resizeImage(ImageIcon icon, int size) {
        int nw = icon.getIconWidth();
        int nh = icon.getIconHeight();
        boolean must = nh>0 && nw>0 && nh < size && nw < size;
        if(nw > size || must && nw > nh)
        {
          must = false;
          nw = size;
          nh = (nw * icon.getIconHeight()) / icon.getIconWidth();
        }
        if(nh > size || must)
        {
          nh = size;
          nw = (icon.getIconWidth() * nh) / icon.getIconHeight();
        }
        return new ImageIcon(icon.getImage().getScaledInstance(nw, nh, Image.SCALE_DEFAULT));
    }
}
