package org.elsquatrecaps.netvolution.view.swing.dialogs;

import javax.swing.JFrame;
import javax.swing.JOptionPane;


/**
 *
 * @author josep
 */
public class AlertDialog {
    protected JFrame ownerJFrame;

    public AlertDialog(JFrame ownerJFrame) {
        this.ownerJFrame = ownerJFrame;
    }
    
    public void display(int type, String message){
        JOptionPane.showMessageDialog(ownerJFrame, message,"ALERT", type);
    }
    
    public void display(String message){
        display(JOptionPane.WARNING_MESSAGE, message);
    }
    
    public void display(int type, String title, String message){
        JOptionPane.showMessageDialog(ownerJFrame, message, title, type);
    }
    
    public void display(String title, String message){
        display(JOptionPane.WARNING_MESSAGE, title, message);
    }
    
}
