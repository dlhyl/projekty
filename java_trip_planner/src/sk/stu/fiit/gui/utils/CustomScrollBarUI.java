/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.stu.fiit.gui.utils;

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;

/**
 * Custom scrollbar UI
 * http://www.java2s.com/Tutorials/Java/Swing_How_to/JScrollPane/Create_custom_JScrollBar_for_JScrollPane.htm
 */
public class CustomScrollBarUI extends BasicScrollBarUI {
    private final Dimension d = new Dimension();
    private Color drag = Color.DARK_GRAY;
    private Color over = Color.LIGHT_GRAY;
    private Color def = Color.GRAY;
    
    
    public void CustomScrollBarUI () {
    }
    
    public void CustomScrollBarUI (Color drag, Color over, Color def) {
         this.drag = drag;
         this.over = over;
         this.def = def;
    }
      
    @Override
    protected JButton createDecreaseButton(int orientation) {
        return new JButton() {
            private static final long serialVersionUID = -3592643796245558676L;
            @Override
            public Dimension getPreferredSize() {
              return d;
            }
        };
    }

    @Override
    protected JButton createIncreaseButton(int orientation) {
        return new JButton() {
            private static final long serialVersionUID = 1L;
            @Override
              public Dimension getPreferredSize() {
                return d;
              }
        };
    }

    @Override
    protected void paintTrack(Graphics g, JComponent c, Rectangle r) {
    }

    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle r) {
      Graphics2D g2 = (Graphics2D) g.create();
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      Color color = null;
      JScrollBar sb = (JScrollBar) c;
      if (!sb.isEnabled() || r.width > r.height) {
        return;
      } else if (isDragging) {
        color = drag; // change color
      } else if (isThumbRollover()) {
        color = over; // change color
      } else {
        color = def; // change color
      }
      g2.setPaint(color);
      g2.fillRoundRect(r.x, r.y, r.width, r.height, 10, 10);
      g2.setPaint(Color.WHITE);
      g2.drawRoundRect(r.x, r.y, r.width, r.height, 10, 10);
      g2.dispose();
    }

    @Override
    protected void setThumbBounds(int x, int y, int width, int height) {
      super.setThumbBounds(x, y, width, height);
      scrollbar.repaint();
    }
}
