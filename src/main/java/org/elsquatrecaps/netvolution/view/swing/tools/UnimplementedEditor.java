/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.elsquatrecaps.netvolution.view.swing.tools;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.TextNode;
import java.awt.BorderLayout;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author josep
 */
public class UnimplementedEditor extends AbstractDataAndEditorBuilder<String>{
    private JPanel rootPaneToAtach=null;
    private JLabel text;

    public UnimplementedEditor() {
    }
    
    public UnimplementedEditor(String txt) {
        super(txt);
    }
    
    @Override
    public void buildNodeEditor(JPanel nodeToAttach) {
        if(rootPaneToAtach==null){
            text = new JLabel();
            text.setFont(new Font(text.getFont().getName(), text.getFont().getStyle(), 25));
            if(this.getDataStructure()==null){
                this.setDataStructure("This editor is not implemented yet");
            }
            text.setText(this.getDataStructure());  
            JPanel textPane = new JPanel();
            textPane.add(text);
            text.setLocation(20, 20);
            rootPaneToAtach = new JPanel();
            rootPaneToAtach.setLayout(new BorderLayout());
            rootPaneToAtach.add(textPane, BorderLayout.CENTER);
        }else{
            text.setText(this.getDataStructure());
        }
        if(nodeToAttach.getComponents().length>0){
            nodeToAttach.removeAll();
        }
        nodeToAttach.add(rootPaneToAtach);
        rootPaneToAtach.setVisible(true);
        rootPaneToAtach.revalidate();
    }
    
    @Override
    public void updateDataStructureFromJson(JsonNode data/*, Scene scene*/) {
        this.setDataStructure(data.asText());
    }

    @Override
    public JsonNode getJsonFromDataStructure() {
        return new TextNode(this.getDataStructure());
    }
}
