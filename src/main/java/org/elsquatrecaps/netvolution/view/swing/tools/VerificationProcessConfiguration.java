package org.elsquatrecaps.netvolution.view.swing.tools;

import org.elsquatrecaps.utilities.tools.Identifiable;

/**
 *
 * @author josep
 */
public class VerificationProcessConfiguration<T> implements Identifiable<String>{
    private String id;
    private String name;
    private AbstractDataAndEditorBuilder<T> editor;
    private PositionType editorPositionType;
    

    public VerificationProcessConfiguration(String id, String name, AbstractDataAndEditorBuilder editor) {
        this(id, name, editor, PositionType.FIXED);
    }
    
    public VerificationProcessConfiguration(String id, String name, AbstractDataAndEditorBuilder editor, PositionType editorPossitionType) {
        this.id=id;
        this.name = name;
        this.editor = editor;
        this.editorPositionType=editorPossitionType;
    }
    
    public VerificationProcessConfiguration(String name, AbstractDataAndEditorBuilder editor) {
        this(name, editor, PositionType.FIXED);
    }
    
    public VerificationProcessConfiguration(String name, AbstractDataAndEditorBuilder editor, PositionType editorPossitionType) {
        this.name = name.trim();
        this.editor = editor;
        this.editorPositionType=editorPossitionType;
        this.id=this.name.replaceAll(" ", "_").toUpperCase();
    }

    /**
     * @return the editor
     */
    public AbstractDataAndEditorBuilder<T> getEditor() {
        return editor;
    }
    

    public PositionType getEditorPositionType() {
        return editorPositionType;
    }

    public void setEditorPositionType(PositionType editorPositionType) {
        this.editorPositionType = editorPositionType;
    }

    @Override
    public String toString() {
        return this.getName();
    }

    /**
     * @return the id
     */
    @Override
    public String getId() {
        return id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }
}
