package org.elsquatrecaps.netvolution.view.swing.dialogs;

import java.io.PrintWriter;
import java.io.StringWriter;
import javax.swing.JFrame;
import javax.swing.JOptionPane;


/**
 *
 * @author josep
 */
public class ErrorDialog extends AlertDialog{
    
    @Override
    public void display(String message){
        super.display(JOptionPane.ERROR_MESSAGE, message);
    }
    
    public void display(String title, String message){
        super.display(JOptionPane.ERROR_MESSAGE, title, message);
    }
    
    public static void displayException(JFrame ownerJFrame, String title, String Message, Exception ex){
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        
        ex.printStackTrace(pw);
        
        String text = String.format("%s\n\nThe exception stacktrace was:\n%s", Message, sw.toString());


        JOptionPane.showConfirmDialog(ownerJFrame, text, title, JOptionPane.ERROR_MESSAGE);
    }

    public ErrorDialog(JFrame ownerJFrame) {
        super(ownerJFrame);
    }
}
