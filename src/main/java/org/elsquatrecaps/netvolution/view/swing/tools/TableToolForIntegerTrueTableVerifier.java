package org.elsquatrecaps.netvolution.view.swing.tools;

import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.DefaultCellEditor;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

/**
 *
 * @author josep
 */
public class TableToolForIntegerTrueTableVerifier {
    
//    public static JTable createJTableFromDataEstructure
    
    public static TableModel createTableModelForIORSTrueTableVerifier(boolean ed){
        return new TableModelForIORSTrueTableVerifier(ed);
    }
    
    public static TableModel createTableModelForIORTrueTableVerifier(boolean ed){
        return new TableModelForIORTrueTableVerifier(ed);
    }
    
    public static TableModel createTableModelForIOTrueTableVerifier(boolean ed){
        return new TableModelForIOTrueTableVerifier(false);
    }
    
    public static TableModel createTableModelForIORSTrueTableVerifier(int i, int o, boolean ed){
        return new TableModelForIORSTrueTableVerifier(i, o, ed);
    }
    
    public static TableModel createTableModelForIORTrueTableVerifier(int i, int o, boolean ed){
        return new TableModelForIORTrueTableVerifier(i, o, ed);
    }
    
    public static TableModel createTableModelForIOTrueTableVerifier(int i, int o, boolean ed){
        return new TableModelForIOTrueTableVerifier(i, o, false);
    }
    
    public static void updateTableFromEstructure(List<Map<String,List<Float>>> dataStructure, JTable table){
        if(table.getModel() instanceof TableModelForIORSTrueTableVerifier){
            updateTableFromEstructureIORS(dataStructure, table);
            updateColumnWidthForIORSModel(table);
        }else if(table.getModel() instanceof TableModelForIORTrueTableVerifier){
            updateTableFromEstructureIOR(dataStructure, table);
        }else if(table.getModel() instanceof TableModelForIOTrueTableVerifier){
            updateTableFromEstructureIO(dataStructure, table);
        }
        TableModelForIOTrueTableVerifier model = (TableModelForIOTrueTableVerifier) table.getModel();
        model.dataStructure = dataStructure;
        model.fireTableDataChanged();
    }
    
    public static void updateColumnWidth(JTable table){
        if(table.getModel() instanceof TableModelForIORSTrueTableVerifier){
            updateColumnWidthForIORSModel(table);
        }
    }
    
    private static void updateColumnWidthForIORSModel(JTable table){
        int witdhForColumns;
        int minWitdhForColumns = 25;
        int widthForSumColumn = 120;
        int w = table.getParent().getWidth();
        witdhForColumns = (w-widthForSumColumn)/(table.getColumnCount()-1);
        if(witdhForColumns<minWitdhForColumns){
            widthForSumColumn = w-minWitdhForColumns*(table.getColumnCount()-1);
            witdhForColumns = minWitdhForColumns;
        }
        for(int i=0; i<table.getColumnCount(); i++){
             TableColumn tc = table.getColumnModel().getColumn(i);
             if(i<table.getColumnCount()-1){
                 tc.setPreferredWidth(witdhForColumns);
             }else{
                 tc.setPreferredWidth(widthForSumColumn);
             }
        }
    }
    
    private static TableColumn createNewTableColumn(int modelIndex, int l1, int l2, int l3, boolean editable, boolean floatEditor){
       TableColumn ret=null;
        ret = new TableColumn(modelIndex);
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
                if(column<l1){
                    l.setBackground(Color.WHITE);
                    l.setForeground(Color.BLACK);
                }else if(l3>0 && column>=l3){
                    l.setBackground(Color.LIGHT_GRAY);
                    l.setForeground(Color.BLACK);
                }else if(l2>0 == column>=l2){
                    l.setBackground(Color.PINK);
                    l.setForeground(Color.RED);
                }else{
                    l.setBackground(Color.GRAY);
                    l.setForeground(Color.WHITE);
                }
                return l;
            }
        });
        if(modelIndex<l1){
            ret.setHeaderValue(String.format("I%d", modelIndex+1));
        }else if(modelIndex>=l3){
            ret.setHeaderValue(String.format("S%d", modelIndex-l3+1));
        }else if(modelIndex>=l2){
            ret.setHeaderValue(String.format("R%d", modelIndex-l2+1));
        }else{
            ret.setHeaderValue(String.format("O%d", modelIndex-l1+1));
        }
        
        if(editable && floatEditor){
            ret.setCellEditor(new FloatCellEditor(new JTextField()));
        }else if(editable){
            ret.setCellEditor(new IntegerCellEditor(new JTextField()));
        }
       return ret;
    }
    
    private static void updateTableFromEstructureIORS(List<Map<String,List<Float>>> dataStructure, JTable table){
        int inputsLength = ((TableModelForIOTrueTableVerifier) table.getModel()).inputsLength;
        int outputsLength = ((TableModelForIOTrueTableVerifier) table.getModel()).outputsLength;
        boolean editable =  ((TableModelForIOTrueTableVerifier) table.getModel()).editable;
        int responseLength = outputsLength;
        int sumLength = outputsLength;
        int ilObjective = dataStructure.get(0).get("I").size();
        int olObjective = dataStructure.get(0).get("O").size();
        if(inputsLength>dataStructure.get(0).get("I").size()){
            //delete inputs
            for(int i=inputsLength-1; i>=dataStructure.get(0).get("I").size(); i--){
                table.removeColumn(table.getColumnModel().getColumn(i));
                inputsLength--;
            }
            ((TableModelForIOTrueTableVerifier) table.getModel()).inputsLength = inputsLength;
        }else{
            for(int i=inputsLength; i<ilObjective; i++){
                TableColumn tc = createNewTableColumn(i, ilObjective, ilObjective+olObjective, ilObjective+olObjective+olObjective, editable, false);
                table.addColumn(tc);
                inputsLength++;
            }
            ((TableModelForIOTrueTableVerifier) table.getModel()).inputsLength = inputsLength;
        }
        if(outputsLength>dataStructure.get(0).get("O").size()){
            //delete outputs
            for(int i=inputsLength+outputsLength-1; i>=inputsLength+dataStructure.get(0).get("O").size(); i--){
                table.removeColumn(table.getColumnModel().getColumn(i));
                outputsLength--;
            }            
            ((TableModelForIOTrueTableVerifier) table.getModel()).outputsLength = outputsLength;
        }else{
            for(int i=outputsLength; i<olObjective; i++){
                TableColumn tc = createNewTableColumn(i+inputsLength, inputsLength, inputsLength+olObjective, inputsLength+olObjective+olObjective, editable, false);
                table.addColumn(tc);
                outputsLength++;
            }
            ((TableModelForIOTrueTableVerifier) table.getModel()).outputsLength = outputsLength;
        }            
        if(responseLength>dataStructure.get(0).get("R").size()){
            //delete outputs
            for(int i=inputsLength+outputsLength+responseLength-1; i>=inputsLength+outputsLength+dataStructure.get(0).get("R").size(); i--){
                table.removeColumn(table.getColumnModel().getColumn(i));
                responseLength--;
            }            
        }else{
            for(int i=responseLength; i<dataStructure.get(0).get("R").size(); i++){
                TableColumn tc = createNewTableColumn(i+inputsLength+outputsLength, inputsLength, inputsLength+outputsLength, inputsLength+outputsLength+outputsLength, editable, false);
                table.addColumn(tc);
                responseLength++;
            }
        }            
        if(sumLength>dataStructure.get(0).get("S").size()){
            //delete outputs
            for(int i=inputsLength+outputsLength+responseLength-1; i>=inputsLength+outputsLength+dataStructure.get(0).get("S").size(); i--){
                table.removeColumn(table.getColumnModel().getColumn(i));
                responseLength--;
            }            
        }else{
            for(int i=sumLength; i<dataStructure.get(0).get("S").size(); i++){
                TableColumn tc = createNewTableColumn(i+inputsLength+outputsLength+outputsLength, inputsLength, inputsLength+outputsLength, inputsLength+outputsLength+outputsLength, editable, true);
                table.addColumn(tc);
                responseLength++;
            }
        }            
    }

    private static void updateTableFromEstructureIOR(List<Map<String,List<Float>>> dataStructure, JTable table){
        int inputsLength = ((TableModelForIOTrueTableVerifier) table.getModel()).inputsLength;
        int outputsLength = ((TableModelForIOTrueTableVerifier) table.getModel()).outputsLength;
        boolean editable =  ((TableModelForIOTrueTableVerifier) table.getModel()).editable;
        int responseLength = outputsLength;
        int ilObjective = dataStructure.get(0).get("I").size();
        int olObjective = dataStructure.get(0).get("O").size();
        if(inputsLength>dataStructure.get(0).get("I").size()){
            //delete inputs
            for(int i=inputsLength-1; i>=dataStructure.get(0).get("I").size(); i--){
                table.removeColumn(table.getColumnModel().getColumn(i));
                inputsLength--;
            }
            ((TableModelForIOTrueTableVerifier) table.getModel()).inputsLength = inputsLength;
        }else{
            for(int i=inputsLength; i<ilObjective; i++){
                TableColumn tc = createNewTableColumn(i, ilObjective, ilObjective+olObjective, ilObjective+olObjective+olObjective, editable, false);
                table.addColumn(tc);
                inputsLength++;
            }
            ((TableModelForIOTrueTableVerifier) table.getModel()).inputsLength = inputsLength;
        }
        if(outputsLength>dataStructure.get(0).get("O").size()){
            //delete outputs
            for(int i=inputsLength+outputsLength-1; i>=inputsLength+dataStructure.get(0).get("O").size(); i--){
                table.removeColumn(table.getColumnModel().getColumn(i));
                outputsLength--;
            }            
            ((TableModelForIOTrueTableVerifier) table.getModel()).outputsLength = outputsLength;
        }else{
            for(int i=outputsLength; i<olObjective; i++){
                TableColumn tc = createNewTableColumn(i+inputsLength, inputsLength, inputsLength+olObjective, -1, editable, false);
                table.addColumn(tc);
                outputsLength++;
            }
            ((TableModelForIOTrueTableVerifier) table.getModel()).outputsLength = outputsLength;
        }            
        if(responseLength>dataStructure.get(0).get("R").size()){
            //delete outputs
            for(int i=inputsLength+outputsLength+responseLength-1; i>=inputsLength+outputsLength+dataStructure.get(0).get("R").size(); i--){
                table.removeColumn(table.getColumnModel().getColumn(i));
                responseLength--;
            }            
        }else{
            for(int i=responseLength; i<dataStructure.get(0).get("R").size(); i++){
                TableColumn tc = createNewTableColumn(i+inputsLength+outputsLength, inputsLength, inputsLength+outputsLength, -1, editable, false);
                table.addColumn(tc);
                responseLength++;
            }
        }            
    }
    
    private static void updateTableFromEstructureIO(List<Map<String,List<Float>>> dataStructure, JTable table){
        int inputsLength = ((TableModelForIOTrueTableVerifier) table.getModel()).inputsLength;
        int outputsLength = ((TableModelForIOTrueTableVerifier) table.getModel()).outputsLength;
        boolean editable =  ((TableModelForIOTrueTableVerifier) table.getModel()).editable;
        if(inputsLength>dataStructure.get(0).get("I").size()){
            //delete inputs
            for(int i=inputsLength-1; i>=dataStructure.get(0).get("I").size(); i--){
                table.removeColumn(table.getColumnModel().getColumn(i));
                inputsLength--;
            }
            ((TableModelForIOTrueTableVerifier) table.getModel()).inputsLength = inputsLength;
        }else{
            for(int i=inputsLength; i<dataStructure.get(0).get("I").size(); i++){
                TableColumn tc = createNewTableColumn(i, inputsLength, -1, -1, editable, false);
                table.addColumn(tc);
                inputsLength++;
            }
            ((TableModelForIOTrueTableVerifier) table.getModel()).inputsLength = inputsLength;
        }
        if(outputsLength>dataStructure.get(0).get("O").size()){
            //delete outputs
            for(int i=inputsLength+outputsLength-1; i>=inputsLength+dataStructure.get(0).get("O").size(); i--){
                table.removeColumn(table.getColumnModel().getColumn(i));
                outputsLength--;
            }            
            ((TableModelForIOTrueTableVerifier) table.getModel()).outputsLength = outputsLength;
        }else{
            int olObjective = dataStructure.get(0).get("O").size();
            for(int i=outputsLength; i<olObjective; i++){
                TableColumn tc = createNewTableColumn(i+inputsLength, inputsLength, -1, -1, editable, false);
                table.addColumn(tc);
                outputsLength++;
            }
            ((TableModelForIOTrueTableVerifier) table.getModel()).outputsLength = outputsLength;
        }            
    }
    
    
    public static class TableModelForIORSTrueTableVerifier extends TableModelForIOTrueTableVerifier{        
        public TableModelForIORSTrueTableVerifier() {
            super(0,0,false);
        }
        
        public TableModelForIORSTrueTableVerifier(boolean ed) {
            super(0,0, ed);
        }
        
        public TableModelForIORSTrueTableVerifier(int il, int ol) {
            super(il, ol, false);
        }
        
        public TableModelForIORSTrueTableVerifier(List<Map<String,List<Float>>> dataStructure) {
            super(dataStructure, false);
        }
        
        public TableModelForIORSTrueTableVerifier(int il, int ol, boolean ed) {
            super(il, ol, ed);
        }
        
        public TableModelForIORSTrueTableVerifier(List<Map<String,List<Float>>> dataStructure, boolean ed) {
            super(dataStructure, ed);
        }
        
        @Override
        public int getColumnCount() {
            return inputsLength + outputsLength + outputsLength + outputsLength; 
        }
        
        @Override
        public Object getValueAt(int r, int c) {
            Object ret;
            if(c<inputsLength){
                ret=getDataStructure().get(r).get("I").get(c).intValue();
            }else if(c<inputsLength+outputsLength){
                int nc = c-inputsLength;
                ret=getDataStructure().get(r).get("O").get(nc).intValue();                
            }else if(c<inputsLength+outputsLength+outputsLength){
                int nc = c-inputsLength-outputsLength;
                ret=getDataStructure().get(r).get("R").get(nc).intValue();
            }else{
                int nc = c-inputsLength-outputsLength-outputsLength;
                ret=getDataStructure().get(r).get("S").get(nc);
            }
            return ret;
        }

        @Override
        public void setValueAt(Object aValue, int r, int c) {
            if(c<inputsLength){
                getDataStructure().get(r).get("I").set(c, (Float) aValue);
            }else if(c<inputsLength+outputsLength){
                int nc = c-inputsLength;
                getDataStructure().get(r).get("O").set(nc, (Float) aValue);
            }else if(c<inputsLength+outputsLength+outputsLength){
                int nc = c-inputsLength-outputsLength;
                getDataStructure().get(r).get("R").set(nc, (Float) aValue);
            }else{
                int nc = c-inputsLength-outputsLength-outputsLength;
                getDataStructure().get(r).get("S").set(nc, (Float) aValue);
            }
            fireTableCellUpdated(r, c);                
        }     
    }
    
    public static class TableModelForIORTrueTableVerifier extends TableModelForIOTrueTableVerifier{
        public TableModelForIORTrueTableVerifier() {
            super(0,0,false);
        }
        
        public TableModelForIORTrueTableVerifier(boolean ed) {
            super(0,0, ed);
        }
        
        public TableModelForIORTrueTableVerifier(int il, int ol) {
            super(il, ol, false);
        }
        
        public TableModelForIORTrueTableVerifier(List<Map<String,List<Float>>> dataStructure) {
            super(dataStructure, false);
        }
        
        public TableModelForIORTrueTableVerifier(int il, int ol, boolean ed) {
            super(il, ol, ed);
        }
        
        public TableModelForIORTrueTableVerifier(List<Map<String,List<Float>>> dataStructure, boolean ed) {
            super(dataStructure, ed);
        }
        
        @Override
        public int getColumnCount() {
            return inputsLength + outputsLength + outputsLength; 
        }
        
        @Override
        public Object getValueAt(int r, int c) {
            Object ret;
            if(c<inputsLength){
                ret=getDataStructure().get(r).get("I").get(c).intValue();
            }else if(c<inputsLength+outputsLength){
                int nc = c-inputsLength;
                ret=getDataStructure().get(r).get("O").get(nc).intValue();                
            }else{
                int nc = c-inputsLength-outputsLength;
                ret=getDataStructure().get(r).get("R").get(nc).intValue();
            }
            return ret;
        }

        @Override
        public void setValueAt(Object aValue, int r, int c) {
            if(c<inputsLength){
                getDataStructure().get(r).get("I").set(c, (Float) aValue);
            }else if(c<inputsLength+outputsLength){
                int nc = c-inputsLength;
                getDataStructure().get(r).get("O").set(nc, (Float) aValue);
            }else{
                int nc = c-inputsLength-outputsLength;
                getDataStructure().get(r).get("R").set(nc, (Float) aValue);
            }
            fireTableCellUpdated(r, c);                
        }     
    }
    
    public static class TableModelForIOTrueTableVerifier extends DefaultTableModel{
        protected List<Map<String,List<Float>>> dataStructure;
        protected int inputsLength;
        protected int outputsLength;
        protected boolean editable;

        public TableModelForIOTrueTableVerifier() {
            this(0,0,false);
        }
        
        public TableModelForIOTrueTableVerifier(boolean ed) {
            this(0,0, ed);
        }
        
        public TableModelForIOTrueTableVerifier(int il, int ol) {
            this(il, ol, false);
        }
        
        public TableModelForIOTrueTableVerifier(List<Map<String,List<Float>>> dataStructure) {
            this(dataStructure, false);
        }
        
        public TableModelForIOTrueTableVerifier(int il, int ol, boolean ed) {
            inputsLength = il;
            outputsLength = ol;
            editable = ed;
            dataStructure = new ArrayList<>();
            for(int i=0; i<inputsLength; i++){
                this.addColumn(String.format("I%d", i+1));
            }
            for(int i=0; i<outputsLength; i++){
                this.addColumn(String.format("O%d", i+1));
            }
            for(int i=0; i<outputsLength; i++){
                this.addColumn(String.format("R%d", i+1));
            }
        }
        
        public TableModelForIOTrueTableVerifier(List<Map<String,List<Float>>> dataStructure, boolean ed) {
            inputsLength = this.getDataStructure().get(0).get("I").size();
            outputsLength = this.getDataStructure().get(0).get("O").size();
            editable = ed;
            this.dataStructure = dataStructure;
            for(int i=0; i<inputsLength; i++){
                this.addColumn(String.format("I%d", i+1));
            }
            for(int i=0; i<outputsLength; i++){
                this.addColumn(String.format("O%d", i+1));
            }
            for(int i=0; i<outputsLength; i++){
                this.addColumn(String.format("R%d", i+1));
            }
        }
        protected final List<Map<String,List<Float>>> getDataStructure(){
            return dataStructure;
        }
        
        @Override
        public int getRowCount() {
            int ret;
            if(getDataStructure()==null){
                ret = 0;
            }else{
                ret = getDataStructure().size();
            }
            return ret;
        }

        @Override
        public int getColumnCount() {
            return inputsLength + outputsLength; 
        }

        @Override
        public Object getValueAt(int r, int c) {
            Object ret;
            if(c<inputsLength){
                ret=getDataStructure().get(r).get("I").get(c).intValue();
            }else{
                int nc = c-inputsLength;
                ret=getDataStructure().get(r).get("O").get(nc).intValue();
            }
            return ret;
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return editable;
        }

        @Override
        public void setValueAt(Object aValue, int r, int c) {
            if(c<inputsLength){
                getDataStructure().get(r).get("I").set(c, (Float) aValue);
            }else{
                int nc = c-inputsLength;
                getDataStructure().get(r).get("O").set(nc, (Float) aValue);
            }
            fireTableCellUpdated(r, c);                
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

    private static class FloatCellEditor extends DefaultCellEditor {
        JTextField tf;
        Float oldValue;

        public FloatCellEditor(JTextField textField) {
            super(textField);
            tf = textField;
            this.setClickCountToStart(2);
        }

        @Override
        public Object getCellEditorValue() {
            Float ret;
            try{
            ret = Float.valueOf(tf.getText().trim());
            }catch(NumberFormatException ne){
                ret = oldValue;
            }
            return ret;
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            if(value instanceof Float){
                oldValue=(Float) value;
            }else{
                oldValue=0f;
            }
           tf.setText(String.valueOf(value));
           return tf;
       }
    }        
}
