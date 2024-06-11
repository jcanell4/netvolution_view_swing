/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.elsquatrecaps.netvolution.view.swing.dialogs;

import javax.swing.JFrame;


/**
 *
 * @author josep
 */
public class InfoDialog extends AlertDialog{
    
    public InfoDialog(JFrame ownerJFrame) {
        super(ownerJFrame);
    }
    
//    public static void display(String message){
//        AlertDialog.display(Alert.AlertType.INFORMATION, message);
//    }
//    
//    public static void display(String title, String message){
//        AlertDialog.display(Alert.AlertType.INFORMATION, title, message);
//    }
//    
//    
//    public static void displayLargeInfo(String title, String shortMesssage, String largeInfo){
//        alertFx.setAlertType(Alert.AlertType.INFORMATION);
//        alertFx.setTitle(title);
//        alertFx.setContentText(shortMesssage);
//        
//        TextArea textArea = new TextArea(largeInfo);
//        textArea.setEditable(false);
//        textArea.setWrapText(true);
//
//        textArea.setMaxWidth(Double.MAX_VALUE);
//        textArea.setMaxHeight(Double.MAX_VALUE);
//        GridPane.setVgrow(textArea, Priority.ALWAYS);
//        GridPane.setHgrow(textArea, Priority.ALWAYS);
//
//        GridPane expContent = new GridPane();
//        expContent.setMaxWidth(Double.MAX_VALUE);
//        expContent.add(textArea, 0, 0);
//
//        // Set expandable Exception into the dialog pane.
//        alertFx.getDialogPane().setContent(expContent);
//
//        alertFx.showAndWait();        
//    }
//    public static void display(String message){
//        AlertDialog.display(Alert.AlertType.INFORMATION, message);
//    }
//    
//    public static void display(String title, String message){
//        AlertDialog.display(Alert.AlertType.INFORMATION, title, message);
//    }
//    
//    
//    public static void displayLargeInfo(String title, String shortMesssage, String largeInfo){
//        alertFx.setAlertType(Alert.AlertType.INFORMATION);
//        alertFx.setTitle(title);
//        alertFx.setContentText(shortMesssage);
//        
//        TextArea textArea = new TextArea(largeInfo);
//        textArea.setEditable(false);
//        textArea.setWrapText(true);
//
//        textArea.setMaxWidth(Double.MAX_VALUE);
//        textArea.setMaxHeight(Double.MAX_VALUE);
//        GridPane.setVgrow(textArea, Priority.ALWAYS);
//        GridPane.setHgrow(textArea, Priority.ALWAYS);
//
//        GridPane expContent = new GridPane();
//        expContent.setMaxWidth(Double.MAX_VALUE);
//        expContent.add(textArea, 0, 0);
//
//        // Set expandable Exception into the dialog pane.
//        alertFx.getDialogPane().setContent(expContent);
//
//        alertFx.showAndWait();        
//    }
}
