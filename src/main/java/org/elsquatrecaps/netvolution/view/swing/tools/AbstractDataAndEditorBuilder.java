/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.elsquatrecaps.netvolution.view.swing.tools;

import com.fasterxml.jackson.databind.JsonNode;
import javax.swing.JPanel;


/**
 *
 * @author josep
 */
public abstract class AbstractDataAndEditorBuilder<T> {
    private T dataStructure;

    public AbstractDataAndEditorBuilder() {
    }

    public AbstractDataAndEditorBuilder(T dataStructure) {
        this.dataStructure = dataStructure;
    }

    /**
     * @return the dataStructure
     */
    public T getDataStructure() {
        return dataStructure;
    }

    /**
     * @param dataStructure the dataStructure to set
     */
    public void setDataStructure(T dataStructure) {
        this.dataStructure = dataStructure;
    }
    
    public abstract void updateDataStructureFromJson(JsonNode node/*, Scene scene*/);
    
    public abstract JsonNode getJsonFromDataStructure();
    
    public abstract void buildNodeEditor(JPanel nodeToAttach);
    
}
