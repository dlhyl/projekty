/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.stu.fiit.gui.utils;

import java.awt.Component;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

/**
 * Custom dialog with custom label
 * @author dlhyl
 */
public class CustomDialog {
    public CustomDialog(Component c, String message, String title,int messageType) {
        JLabel label = new JLabel();
        label.setFont(new Font("Gill Sans MT", Font.BOLD, 18));
        label.setText(message);
        JOptionPane.showMessageDialog(c,label,title,messageType);
    }
}
