package org.elsquatrecaps.netvolution.view.swing.tools;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import org.elsquatrecaps.netvolution.view.swing.dialogs.ErrorDialog;

/**
 *
 * @author josep
 */
//public class IntegerTableEditor extends AbstractDataAndEditorBuilder<ObservableList<Map<String,List<Integer>>>>{
public class IntegerTableEditor extends AbstractDataAndEditorBuilder<List<Map<String,List<Integer>>>>{
    ErrorDialog alertFx;
    JTable table;
    JButton deleteSelectedColumnButton;
    JButton deleteSelectedRowButton;
    PositionType editorPositionType;
    private double auxToCalculateAbsolutePosition;
    private final double controlPaneWidth = 250;
    private int inputsLength=0;
    private int outputsLength=0;
    private String cssTable;
    private boolean initialized=false;
    private JPanel rootPaneToAtach;
    
    private final JFrame scene;

    public IntegerTableEditor(PositionType editorPositionType, JFrame frame) {
        this.editorPositionType = editorPositionType;
        scene=frame;
        alertFx = new ErrorDialog(scene);
    }
    
    private void initEditorWidthCalculation(JPanel nodeToattache, JFrame scene){
        if(editorPositionType.equals(PositionType.RELATIVE)){
            auxToCalculateAbsolutePosition = nodeToattache.getWidth()/scene.getWidth();
        }else{
            auxToCalculateAbsolutePosition = nodeToattache.getWidth()-scene.getWidth();
        }
    }
    
    private double calculateEditorWidth(JFrame scene){
        double ret;
        if(editorPositionType.equals(PositionType.RELATIVE)){
            ret = scene.getWidth()*auxToCalculateAbsolutePosition-controlPaneWidth-8;
        }else{
            ret = scene.getWidth()+auxToCalculateAbsolutePosition-controlPaneWidth-8;
        }
        return ret;
    }
    
    private void createDataCollection(){
        ArrayList<Map<String,List<Integer>>> l = new ArrayList<>();
        this.setDataStructure(l);
        l.add(new HashMap<>());
        this.getDataStructure().get(0).put("I", new ArrayList<>());
        this.getDataStructure().get(0).get("I").add(0);
        this.getDataStructure().get(0).put("O", new ArrayList<>());
        this.getDataStructure().get(0).get("O").add(0);
    }
    
    
    public void updateTableByData(){
        if(inputsLength>this.getDataStructure().get(0).get("I").size()){
            //delete inputs
            for(int i=inputsLength-1; i>=this.getDataStructure().get(0).get("I").size(); i--){
                table.removeColumn(table.getColumnModel().getColumn(i));
                inputsLength--;
            }
        }else{
            for(int i=inputsLength; i<this.getDataStructure().get(0).get("I").size(); i++){
                TableColumn tc = createNewTableColumn();
                table.addColumn(tc);
                inputsLength++;
            }
        }
        if(outputsLength>this.getDataStructure().get(0).get("O").size()){
            //delete outputs
            for(int i=inputsLength+outputsLength-1; i>=inputsLength+this.getDataStructure().get(0).get("O").size(); i--){
                table.removeColumn(table.getColumnModel().getColumn(i));
                outputsLength--;
            }            
        }else{
            for(int i=outputsLength; i<this.getDataStructure().get(0).get("O").size(); i++){
                TableColumn tc = createNewTableColumn();
                table.addColumn(tc);
                outputsLength++;
            }
        }
    }
    
    private static class IntegerCellEditor extends DefaultCellEditor {
        JTextField tf;
        Integer oldValue;

        public IntegerCellEditor(JTextField textField) {
            super(textField);
            tf = textField;
            this.setClickCountToStart(2);
        }

        @Override
        public Object getCellEditorValue() {
            Integer ret;
            try{
            ret = Integer.valueOf(tf.getText().trim());
            }catch(NumberFormatException ne){
                ret = oldValue;
            }
            return ret;
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            if(value instanceof Integer){
                oldValue=(Integer) value;
            }else{
                oldValue=0;
            }
           tf.setText(String.valueOf(value));
           return tf;
       }

    }
    
    private TableColumn createNewTableColumn(){
       TableColumn ret=null;
        ret = new TableColumn(table.getColumnCount());
        ret.setCellRenderer(new DefaultTableCellRenderer(){
//            JLabel l = new JLabel();
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JComponent l = (JComponent) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column); 
                if(l instanceof JLabel){
                    ((JLabel)l).setHorizontalAlignment(JLabel.CENTER);
                }else if(l instanceof JTextField){
                    ((JTextField)l).setHorizontalAlignment(JLabel.CENTER);
                }
                if(column<inputsLength){
                    l.setBackground(Color.WHITE);
                    l.setForeground(Color.BLACK);
                }else{
                    l.setBackground(Color.GRAY);
                    l.setForeground(Color.WHITE);
                }
                return l;
            }
        });
        ret.setCellEditor(new IntegerCellEditor(new JTextField()));
       return ret;
    }
    
    private TableColumnModel creaTableColumnModel(){
        TableColumnModel ret = new DefaultTableColumnModel(){
            @Override
            public void addColumn(TableColumn aColumn) {
                super.addColumn(aColumn); 
            }
        };
        return ret;
    }
    
    private TableModel createTableModel(){
        TableModel ret = new DefaultTableModel() {
            @Override
            public int getRowCount() {
                return getDataStructure().size();
            }

            @Override
            public int getColumnCount() {
                return inputsLength + outputsLength; 
            }

            @Override
            public Object getValueAt(int r, int c) {
                Object ret;
                if(c<inputsLength){
                    ret=getDataStructure().get(r).get("I").get(c);
                }else{
                    int nc = c-inputsLength;
                    ret=getDataStructure().get(r).get("O").get(nc);
                }
                return ret;
            }

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return true;
            }

            @Override
            public void setValueAt(Object aValue, int r, int c) {
                if(c<inputsLength){
                    getDataStructure().get(r).get("I").set(c, (Integer) aValue);
                }else{
                    int nc = c-inputsLength;
                    getDataStructure().get(r).get("O").set(nc, (Integer) aValue);
                }
                fireTableCellUpdated(r, c);                
            }

        };
        
        return ret;        
    }
    
    @Override
    public void  buildNodeEditor(JPanel nodeToAttach) {
        if(initialized){
            updateTableByData();
            if(nodeToAttach.getComponents().length>0){
                nodeToAttach.removeAll();
            }
            updateColumns(calculateEditorWidth(scene));  
        }else{
            createNodeEditor(nodeToAttach);
            initialized=true;
        }
        nodeToAttach.setVisible(true);
        nodeToAttach.add(rootPaneToAtach);
        rootPaneToAtach.revalidate();
    }
    
    public void  createNodeEditor(JPanel nodeToattache) {
        JPanel controlPane;
        JScrollPane tablePaneScroll;
        JButton addColumnInputButton;
        JButton addColumnOutputButton;
        JButton addRowButton;
        JButton addRowBeforeSelectedButton;
        
        initEditorWidthCalculation(nodeToattache, scene);
        if(getDataStructure()==null || getDataStructure().isEmpty()){
            createDataCollection();
        }
        
        table = new JTable(createTableModel(), creaTableColumnModel());
        table.setLocation(0, 0);
        table.setRowHeight(30);
        table.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer(){
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JComponent l = (JComponent) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column); 
                if(l instanceof JLabel){
                    ((JLabel)l).setHorizontalAlignment(JLabel.CENTER);
                }else if(l instanceof JTextField){
                    ((JTextField)l).setHorizontalAlignment(JLabel.CENTER);
                }
                return l;
            }
        });

        updateTableByData();
        table.getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        tablePaneScroll = new JScrollPane(table);
        addColumnInputButton = new JButton("Add an input column");
        addColumnInputButton.addActionListener((java.awt.event.ActionEvent evt) -> {
            addInputColumn(/*callback,*/ calculateEditorWidth(scene));
        });

        addColumnInputButton.setLocation(14, 14);
        addColumnInputButton.setPreferredSize(new Dimension(208, 24));
        
        addColumnOutputButton = new JButton("Add an output column");
        addColumnOutputButton.addActionListener((java.awt.event.ActionEvent evt) -> {
            addOutputColumn(calculateEditorWidth(scene));
        });
        addColumnOutputButton.setLocation(14, 56);
        addColumnOutputButton.setPreferredSize(new Dimension(208, 24));
        
        deleteSelectedColumnButton = new JButton("Delete selected column");
        deleteSelectedColumnButton.addActionListener((java.awt.event.ActionEvent evt) -> {
            deleteSelectedColumn(calculateEditorWidth(scene));
        });

        deleteSelectedColumnButton.setLocation(14, 98);
        deleteSelectedColumnButton.setPreferredSize(new Dimension(208, 24));
        deleteSelectedColumnButton.setEnabled(Boolean.FALSE);
        
        addRowButton = new JButton("Add a new row");
        addRowButton.addActionListener((java.awt.event.ActionEvent evt) -> {
            addRow();
        });
        
        addRowButton.setLocation(14, 140);
        addRowButton.setPreferredSize(new Dimension(208, 24));
        
        addRowBeforeSelectedButton = new JButton("Add a new row before the selected one");
        addRowBeforeSelectedButton.addActionListener((java.awt.event.ActionEvent evt) -> {
            addRowBeforeSelected();
        });
        
        addRowBeforeSelectedButton.setLocation(14, 186);
        addRowBeforeSelectedButton.setPreferredSize(new Dimension(208, 38));
        
        deleteSelectedRowButton = new JButton("Delete selected row");
        deleteSelectedRowButton.addActionListener((java.awt.event.ActionEvent evt) -> {
            deleteSelectedRow();
        });
        
        deleteSelectedRowButton.setLocation(14, 238);
        deleteSelectedRowButton.setPreferredSize(new Dimension(208, 24));
        deleteSelectedRowButton.setEnabled(Boolean.FALSE);

        controlPane = new JPanel();
        controlPane.setLayout(new BoxLayout(controlPane, BoxLayout.Y_AXIS));
        controlPane.add(addColumnInputButton);
        controlPane.add(addColumnOutputButton);
        controlPane.add(deleteSelectedColumnButton);
        controlPane.add(addRowButton);
        controlPane.add(addRowBeforeSelectedButton);
        controlPane.add(deleteSelectedRowButton);
        

        rootPaneToAtach = new JPanel(new BorderLayout());
        rootPaneToAtach.add(controlPane, BorderLayout.LINE_START);
        rootPaneToAtach.add(tablePaneScroll, BorderLayout.CENTER);


        scene.addComponentListener(new ComponentAdapter(){
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e); 
                updateColumns(calculateEditorWidth(scene));
            }
        });
        scene.addWindowStateListener(new WindowStateListener(){
            @Override
            public void windowStateChanged(WindowEvent we) {
                if((we.getNewState() & Frame.ICONIFIED)==Frame.ICONIFIED 
                        || (we.getNewState() & Frame.MAXIMIZED_HORIZ)==Frame.MAXIMIZED_HORIZ
                        || (we.getNewState() & Frame.MAXIMIZED_BOTH)==Frame.MAXIMIZED_BOTH){
                    updateColumns(calculateEditorWidth(scene));                    
                }
            }
        });
        
        updateColumns(calculateEditorWidth(scene));
        rootPaneToAtach.setVisible(true);        
    }
    
    private void updateColumns(Double s){
//           Double s = ((Region)table.getParent()).widthProperty().getValue();
        int width = (int) (s/table.getColumnCount());
        for(int i=0; i<table.getColumnCount(); i++){
             TableColumn tc = table.getColumnModel().getColumn(i);
             tc.setPreferredWidth(width);
             if(i<inputsLength){
                tc.setHeaderValue(String.format("I.%d", i+1));
             }else{
                 int nc = i-inputsLength;
                 tc.setHeaderValue(String.format("O.%d", nc+1));
             }
        }
    }
    
    private void addOutputColumn(double editorWidth){
        for(int i=0; i<this.getDataStructure().size(); i++){
            this.getDataStructure().get(i).get("O").add(0);
        }
        outputsLength++;
        TableColumn c = createNewTableColumn();
        table.addColumn(c);
        ((DefaultTableModel)table.getModel()).fireTableDataChanged();
        updateColumns(editorWidth);
        
        if(inputsLength+outputsLength>2 && !deleteSelectedColumnButton.isEnabled()){
            deleteSelectedColumnButton.setEnabled(Boolean.TRUE);
        }
    }
    
    private void addInputColumn(double editorWidth){
        for(int i=0; i<this.getDataStructure().size(); i++){
            this.getDataStructure().get(i).get("I").add(0);
        }
        inputsLength++;
        TableColumn c = createNewTableColumn();
        table.addColumn(c);
        ((DefaultTableModel)table.getModel()).fireTableDataChanged();
        updateColumns(editorWidth);
        
        if(inputsLength+outputsLength>2 && !deleteSelectedColumnButton.isEnabled()){
            deleteSelectedColumnButton.setEnabled(Boolean.TRUE);
        }
    }    
    
    private void deleteSelectedColumn(double editorWidth){
        String colType;
        int posData; 
        int cToDlete = table.getSelectedColumn();
        if(cToDlete>-1){
            if(cToDlete<inputsLength){
                colType="I";
                posData=cToDlete;
            }else{
                colType="O";
                posData=cToDlete-inputsLength;
            }
            if(getDataStructure().get(0).get(colType).size()>1){
                //Eliminar data
                for(int i=0; i<this.getDataStructure().size(); i++){
                    getDataStructure().get(i).get(colType).remove(posData);
                }
                if(colType.equals("I")){
                    inputsLength--;
                }else{
                    outputsLength--;
                }
                table.getColumnModel().removeColumn(table.getColumnModel().getColumn(inputsLength+outputsLength));
                ((DefaultTableModel)table.getModel()).fireTableDataChanged();
                updateColumns(editorWidth);

                if(inputsLength+outputsLength<=2){
                    deleteSelectedColumnButton.setEnabled(Boolean.FALSE);
                }
            }else{
                alertFx.display(String.format("You can't delete all %s.", colType.equals("I")?"inputs":"outputs"));
            }
        }
    }
    
    private void deleteSelectedRow() {
        int rowSelected = table.getSelectedRow();
        if(rowSelected>-1){
            getDataStructure().remove(rowSelected);
            ((DefaultTableModel)table.getModel()).fireTableDataChanged();
            if(getDataStructure().size()<=1){
                deleteSelectedRowButton.setEnabled(Boolean.FALSE);
            }      
        }
    }

    private void addRowBeforeSelected() {
        int rowSelected = table.getSelectedRow();
        if(rowSelected>-1){
            addRow();
            for(int i=getDataStructure().size()-1; i>rowSelected; i--){
                Map<String, List<Integer>> aux = getDataStructure().get(i-1);
                getDataStructure().set(i-1, getDataStructure().get(i));
                getDataStructure().set(i, aux);
            }
        }
    }


    private void addRow() {
        HashMap<String, List<Integer>> newRow = new HashMap<>();
        newRow.put("I", new ArrayList<>());
        newRow.put("O", new ArrayList<>());
        for(int i=0; i<inputsLength; i++){
            newRow.get("I").add(0);
        }
        for(int i=0; i<outputsLength; i++){
            newRow.get("O").add(0);
        }
        getDataStructure().add(newRow);
        ((DefaultTableModel)table.getModel()).fireTableDataChanged();
        if(!deleteSelectedRowButton.isEnabled()){
            deleteSelectedRowButton.setEnabled(Boolean.TRUE);
        }
    }

    @Override
    public void updateDataStructureFromJson(JsonNode tableData) {
        ArrayNode an = (ArrayNode) tableData;
        if(getDataStructure()==null){
            setDataStructure(new ArrayList<>());
        }
        getDataStructure().clear();
        for(int i=0; i<an.size(); i++){
            HashMap m = new HashMap<>();
            m.put("I", new ArrayList<>());
            m.put("O", new ArrayList<>());
            this.getDataStructure().add(m);
            for(int ii=0; ii<an.get(i).get("inputs").size(); ii++){
                this.getDataStructure().get(i).get("I").add(an.get(i).get("inputs").get(ii).asInt());        
            }
            for(int oi=0; oi<an.get(i).get("outputs").size(); oi++){
                this.getDataStructure().get(i).get("O").add(an.get(i).get("outputs").get(oi).asInt());
            }
        }
        if(initialized){
            updateTableByData();
        }
    }

    @Override
    public JsonNode getJsonFromDataStructure() {
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode arrayNode = objectMapper.createArrayNode();
        for(int i=0; i<getDataStructure().size(); i++){
            ArrayNode inputs = objectMapper.createArrayNode();
            for(Integer v: this.getDataStructure().get(i).get("I")){
                inputs.add(v);
            }
            ArrayNode outputs = objectMapper.createArrayNode();
            for(Integer v: this.getDataStructure().get(i).get("O")){
                outputs.add(v);
            }
            ObjectNode reg = objectMapper.createObjectNode();
            reg.set("inputs", inputs);
            reg.set("outputs", outputs);            
            arrayNode.add(reg);
        }
        return arrayNode;
    }
}
