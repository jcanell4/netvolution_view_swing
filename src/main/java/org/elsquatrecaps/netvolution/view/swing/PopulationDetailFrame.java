package org.elsquatrecaps.netvolution.view.swing;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.layout.mxEdgeLabelLayout;
import com.mxgraph.layout.mxParallelEdgeLayout;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;
import java.awt.BorderLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.elsquatrecaps.netvolution.constants.CntFrames;
import org.elsquatrecaps.netvolution.shareddata.NetvolutionSharedData;
import org.elsquatrecaps.netvolution.view.swing.graphmodel.NeuralNetworkInformationSheet;
import org.elsquatrecaps.netvolution.view.swing.graphmodel.PtpNeuralNetworkPopulationViewer;
import org.elsquatrecaps.netvolution.view.swing.tools.TableToolForIntegerTrueTableVerifier;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.data.category.DefaultCategoryDataset;

/**
 *
 * @author josepcanellas
 */
public class PopulationDetailFrame extends NetvolutionBasicFrame{
    PtpNeuralNetworkPopulationViewer initialPopViewer;
    PtpNeuralNetworkPopulationViewer finalPopViewer;

    /**
     * Creates new form PopulationDetailFrame
     * @param sharedData
     */
    public PopulationDetailFrame(NetvolutionSharedData sharedData) {
        super(sharedData);
    }
    public void init(){
        initComponents();
        initPopulations();
        updateAgent();
    }
    
    private void initMoreComponents(){
        this.addComponentListener(new ComponentAdapter(){
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e); 
                TableToolForIntegerTrueTableVerifier.updateColumnWidth(initPopVerifierTable);
                TableToolForIntegerTrueTableVerifier.updateColumnWidth(endPopVerifierTable);
            }
        });
    
    }
    
    public void refreshInitialJFreeDistribution(){
        this.initialBetaPanelGraph.removeAll();
        this.initialBiasPanelGraph.removeAll();
        this.drawInitialJFreeDistribution();
        this.initialBetaPanelGraph.repaint();
        this.initialBiasPanelGraph.repaint();
        this.initialBetaPanelGraph.revalidate();
        this.initialBiasPanelGraph.revalidate();
    }
    
    private void refreshFinalJFreeDistribution(){
        this.finalBetaPanelGraph.removeAll();
        this.finalBiasPanelGraph.removeAll();
        this.drawFinalJFreeDistribution();
        this.finalBetaPanelGraph.repaint();
        this.finalBiasPanelGraph.repaint();
        this.finalBetaPanelGraph.revalidate();
        this.finalBiasPanelGraph.revalidate();
    }
    
    private void drawInitialJFreeDistribution(){
        //XYSeries serie ;
        DefaultCategoryDataset datasetBias = new DefaultCategoryDataset();     
        DefaultCategoryDataset datasetBeta = new DefaultCategoryDataset();     
        for(int i=0; i<initialPopViewer.getInformationSheet().getBiasList().size(); i++){
            datasetBias.setValue(initialPopViewer.getInformationSheet().getBiasList().get(i), "frequency",  initialPopViewer.getInformationSheet().getBiasIntervals().get(i));           
        }
        for(int i=0; i<initialPopViewer.getInformationSheet().getBetaList().size(); i++){
            datasetBeta.setValue(initialPopViewer.getInformationSheet().getBetaList().get(i), "frequency",  initialPopViewer.getInformationSheet().getBetaIntervals().get(i));           
        }
        JFreeChart chartBias = ChartFactory.createBarChart("Bias distribution", "Interval values", "Frequency", datasetBias);
        ChartPanel viewerBias = new ChartPanel(chartBias);
        chartBias.getCategoryPlot().getDomainAxis().setCategoryLabelPositions(CategoryLabelPositions.UP_45);
        chartBias.getCategoryPlot().getRangeAxis().setStandardTickUnits(NumberAxis.createIntegerTickUnits());        
        initialBiasPanelGraph.add(viewerBias);
        JFreeChart chartBeta = ChartFactory.createBarChart("Activation function beta distribution", "Interval values", "Frequency", datasetBeta);
        chartBeta.getCategoryPlot().getDomainAxis().setCategoryLabelPositions(CategoryLabelPositions.UP_45);
        chartBeta.getCategoryPlot().getRangeAxis().setStandardTickUnits(NumberAxis.createIntegerTickUnits());        
        ChartPanel viewerBeta = new ChartPanel(chartBeta);
        initialBetaPanelGraph.add(viewerBeta);
    }
    
    private void drawFinalJFreeDistribution(){
        //XYSeries serie ;
        DefaultCategoryDataset datasetBias = new DefaultCategoryDataset();     
        DefaultCategoryDataset datasetBeta = new DefaultCategoryDataset();     
        for(int i=0; i<finalPopViewer.getInformationSheet().getBiasList().size(); i++){
            datasetBias.setValue(finalPopViewer.getInformationSheet().getBiasList().get(i), "frequency",  finalPopViewer.getInformationSheet().getBiasIntervals().get(i));           
        }
        for(int i=0; i<finalPopViewer.getInformationSheet().getBetaList().size(); i++){
            datasetBeta.setValue(finalPopViewer.getInformationSheet().getBetaList().get(i), "frequency",  finalPopViewer.getInformationSheet().getBetaIntervals().get(i));           
        }
        JFreeChart chartBias = ChartFactory.createBarChart("Bias distribution", "Interval values", "Frequency", datasetBias);
        chartBias.getCategoryPlot().getDomainAxis().setCategoryLabelPositions(CategoryLabelPositions.UP_45);
        chartBias.getCategoryPlot().getRangeAxis().setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        ChartPanel viewerBias = new ChartPanel(chartBias);
        finalBiasPanelGraph.add(viewerBias);
        JFreeChart chartBeta = ChartFactory.createBarChart("Activation function beta distribution", "Interval values", "Frequency", datasetBeta);
        chartBeta.getCategoryPlot().getDomainAxis().setCategoryLabelPositions(CategoryLabelPositions.UP_45);
        chartBeta.getCategoryPlot().getRangeAxis().setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        ChartPanel viewerBeta = new ChartPanel(chartBeta);
        finalBetaPanelGraph.add(viewerBeta);
    }
    
    private void initPopulations(){
        initialPopViewer = new PtpNeuralNetworkPopulationViewer();
        finalPopViewer = new PtpNeuralNetworkPopulationViewer();
        JsonNode jsonConfig = getSharedData().jsonConfigData;
        ArrayNode ar = (ArrayNode) jsonConfig.get("evolutionarySystem").get("evolutionEnvironment").get("configEditors").get(
                jsonConfig.get("evolutionarySystem").get("evolutionEnvironment").get("verificationProcess").asText());
        Float[][] environmentInputSet = new Float[ar.size()][ar.get(0).get("inputs").size()];
        Float[][] environmentOutputSet = new Float[ar.size()][ar.get(0).get("outputs").size()];
        
        for(int i=0; i< ar.size(); i++){
            for(int j=0; j<environmentInputSet[i].length; j++){
                environmentInputSet[i][j] = (float) ar.get(i).get("inputs").get(j).asDouble();
            }
            for(int j=0; j<environmentOutputSet[i].length; j++){
                environmentOutputSet[i][j] = (float) ar.get(i).get("outputs").get(j).asDouble();
            }
        }
        
        List<String> vitalAdvantages = new ArrayList<>();
        Iterator<Map.Entry<String, JsonNode>> iterator = (Iterator<Map.Entry<String, JsonNode>>) jsonConfig.get("evolutionarySystem").get("evolutionProcessConditions").get("vitalAdvantages").fields();
        while(iterator.hasNext()){
            Map.Entry<String, JsonNode> e = iterator.next();
            if(e.getValue().asBoolean()){
                vitalAdvantages.add(e.getKey());
            }
        }
        List<String> reproductiveAdvantages = new ArrayList<>();
        iterator = (Iterator<Map.Entry<String, JsonNode>>) jsonConfig.get("evolutionarySystem").get("evolutionProcessConditions").get("reproductiveAdvantages").fields();
        while(iterator.hasNext()){
            Map.Entry<String, JsonNode> e = iterator.next();
            if(e.getValue().asBoolean()){
                reproductiveAdvantages.add(e.getKey());
            }
        }
        initialPopViewer.init(getSharedData().initialEvolutionaryProcessEvent.getInitialPopulation(), environmentInputSet, environmentOutputSet, vitalAdvantages, reproductiveAdvantages);
        finalPopViewer.init(getSharedData().completedEvolutionaryProcessEvent.getFinalPopulation(), environmentInputSet, environmentOutputSet, vitalAdvantages, vitalAdvantages);
    }
    
    public void updateAgent(){
        this.initialGraphPanel.removeAll();
        this.finalGraphPanel.removeAll();
        updateAgentOfInitialPop();        
        updateAgentOfFinalPop();
        this.initialGraphPanel.repaint();
        this.initialGraphPanel.revalidate();
        this.finalGraphPanel.repaint();
        this.finalGraphPanel.revalidate();
        refreshInitialJFreeDistribution();
        refreshFinalJFreeDistribution();
    }
    
    public void updateAgentOfInitialPop(){
        NeuralNetworkInformationSheet info = initialPopViewer.getInformationSheet();
        this.agentIdTextField.setText(info.getId());
        this.inputNeuronsTextField.setText(String.valueOf(info.getInputNeurons()));
        this.intermedialNeuronsTextField.setText(String.valueOf(info.getConnectionsLength()));
        this.outputNeuronsTextField.setText(String.valueOf(info.getOutpuNeurons()));
        this.neuronDensityTextField.setText(String.format("%6.4f", info.getNeuronDensityIndex()));
//        this.forwardConnectionsTextField.setText(String.valueOf(info.getForwardConnections()));
//        this.backwardConnectionsTextField.setText(String.valueOf(info.getBackwardConnections()));
        this.calculationEficiencyTextField.setText(String.format("%6.4f", info.getCalculationEficiencyIndex()));
        this.activationFunctionLinearityTextField.setText(String.format("%6.4f", info.getActivationFunctionLinearityDegree()));
        this.performanceTextField.setText(String.format("%6.4f", info.getPerformance()));
        TableToolForIntegerTrueTableVerifier.updateTableFromEstructure(info.getResultsFromValueTable(), initPopVerifierTable);
        // Añadir el grafo a un componente Swing
        mxGraph graph = initialPopViewer.getGraph();
        mxGraphComponent graphComponent = new mxGraphComponent(graph);
        this.initialGraphPanel.setLayout(new BorderLayout());
        this.initialGraphPanel.add(graphComponent);
        // Centrar el grafo
//        mxHierarchicalLayout layout = new mxHierarchicalLayout(graph);
//        layout.execute(graph.getDefaultParent(), initialPopViewer.getVertexRoots());
//        layout.setOrientation(SwingConstants.WEST);
//        layout.setInterHierarchySpacing(20);
//        layout1.setIntraCellSpacing(30);
//        layout1.setInterRankCellSpacing(50);
        mxCircleLayout layout0 = new mxCircleLayout(graph);
        layout0.execute(graph.getDefaultParent());
        mxEdgeLabelLayout layout2 = new mxEdgeLabelLayout(graph);
        layout2.execute(graph.getDefaultParent());
//        mxOrganicLayout layout3 = new mxOrganicLayout(graph);
//        layout3.execute(graph.getDefaultParent());
        mxParallelEdgeLayout layout4 = new mxParallelEdgeLayout(graph);
        layout4.execute(graph.getDefaultParent());
        graphComponent.zoomAndCenter();
    }
    
    public void updateAgentOfFinalPop(){
        NeuralNetworkInformationSheet info = this.finalPopViewer.getInformationSheet();
        this.agentIdTextFieldFinal.setText(info.getId());
        this.inputNeuronsTextField1.setText(String.valueOf(info.getInputNeurons()));
        this.intermedialNeuronsTextField1.setText(String.valueOf(info.getConnectionsLength()));
        this.outputNeuronsTextField1.setText(String.valueOf(info.getOutpuNeurons()));
        this.neuronDensityTextField1.setText(String.format("%6.4f", info.getNeuronDensityIndex()));
//        this.forwardConnectionsTextField1.setText(String.valueOf(info.getForwardConnections()));
//        this.backwardConnectionsTextField1.setText(String.valueOf(info.getBackwardConnections()));
        this.calculationEficiencyTextField1.setText(String.format("%6.4f", info.getCalculationEficiencyIndex()));
        this.activationFunctionLinearityTextField1.setText(String.format("%6.4f", info.getActivationFunctionLinearityDegree()));
        this.performanceTextField1.setText(String.format("%6.4f", info.getPerformance()));        
        TableToolForIntegerTrueTableVerifier.updateTableFromEstructure(info.getResultsFromValueTable(), endPopVerifierTable);        
        this.agentTextualViewTextPane.setText(info.getTextualView());
        // Añadir el grafo a un componente Swing
        mxGraph graph = finalPopViewer.getGraph();
        mxGraphComponent graphComponent = new mxGraphComponent(graph);
        this.finalGraphPanel.setLayout(new BorderLayout());
        this.finalGraphPanel.add(graphComponent);        
        // Centrar el grafo
//        mxHierarchicalLayout layout1 = new mxHierarchicalLayout(graph);
//        layout1.setOrientation(SwingConstants.WEST);
//        layout1.setInterHierarchySpacing(50);
//        layout1.setIntraCellSpacing(80);
//        layout1.setInterRankCellSpacing(80);
//        layout1.execute(graph.getDefaultParent(), finalPopViewer.getVertexRoots());
        mxCircleLayout layout0 = new mxCircleLayout(graph);
        layout0.setRadius(100);
        layout0.execute(graph.getDefaultParent());
        mxEdgeLabelLayout layout2 = new mxEdgeLabelLayout(graph);
        layout2.execute(graph.getDefaultParent());
//        mxOrganicLayout layout3 = new mxOrganicLayout(graph);
//        layout3.execute(graph.getDefaultParent());
        mxParallelEdgeLayout layout4 = new mxParallelEdgeLayout(graph);
        layout4.execute(graph.getDefaultParent());
        graphComponent.zoomAndCenter();
    }    
    
//        private TableModel createTableModel(){
//        TableModel ret = new DefaultTableModel() {
//            @Override
//            public int getRowCount() {
//                return getDataStructure().size();
//            }
//
//            @Override
//            public int getColumnCount() {
//                return inputsLength + outputsLength; 
//            }
//
//            @Override
//            public Object getValueAt(int r, int c) {
//                Object ret;
//                if(c<inputsLength){
//                    ret=getDataStructure().get(r).get("I").get(c);
//                }else{
//                    int nc = c-inputsLength;
//                    ret=getDataStructure().get(r).get("O").get(nc);
//                }
//                return ret;
//            }
//
//            @Override
//            public boolean isCellEditable(int rowIndex, int columnIndex) {
//                return true;
//            }
//
//            @Override
//            public void setValueAt(Object aValue, int r, int c) {
//                if(c<inputsLength){
//                    getDataStructure().get(r).get("I").set(c, (Integer) aValue);
//                }else{
//                    int nc = c-inputsLength;
//                    getDataStructure().get(r).get("O").set(nc, (Integer) aValue);
//                }
//                fireTableCellUpdated(r, c);                
//            }
//
//        };
//        
//        return ret;        
//    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton4 = new javax.swing.JButton();
        jToolBar1 = new javax.swing.JToolBar();
        viewCurrentConfigButton = new javax.swing.JButton();
        openConfigButton = new javax.swing.JButton();
        saveConfigButton = new javax.swing.JButton();
        runButton = new javax.swing.JButton();
        viewEvolveDataButton = new javax.swing.JButton();
        loadEvolveDataButton = new javax.swing.JButton();
        saveEvolveDataButton = new javax.swing.JButton();
        loadAgentDetailsButton = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        toPreviousAgentOfInitialPopButton = new javax.swing.JButton();
        toFirstAgentOfInitialPopButton = new javax.swing.JButton();
        toNextAgentOfInitialPopButton1 = new javax.swing.JButton();
        toLastAgentOfInitialPopButton1 = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        initialAgentPropertiesScrollPane = new javax.swing.JScrollPane();
        jPanel7 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        calculationEficiencyTextField = new javax.swing.JTextField();
        agentIdTextField = new javax.swing.JTextField();
        inputNeuronsTextField = new javax.swing.JTextField();
        intermedialNeuronsTextField = new javax.swing.JTextField();
        outputNeuronsTextField = new javax.swing.JTextField();
        neuronDensityTextField = new javax.swing.JTextField();
        activationFunctionLinearityTextField = new javax.swing.JTextField();
        jPanel8 = new javax.swing.JPanel();
        jPanel13 = new javax.swing.JPanel();
        jLabel19 = new javax.swing.JLabel();
        performanceTextField = new javax.swing.JTextField();
        jPanel15 = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        initPopVerifierTable = new javax.swing.JTable();
        initialAgentTextualViewScrollPane = new javax.swing.JScrollPane();
        agentTextualViewTextPane1 = new javax.swing.JTextPane();
        initialBiasPanelGraph = new javax.swing.JPanel();
        initialBetaPanelGraph = new javax.swing.JPanel();
        initialGraphScrollPane = new javax.swing.JScrollPane();
        initialGraphPanel = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        toNextAgentOfInitialPopButton = new javax.swing.JButton();
        toLastAgentOfInitialPopButton = new javax.swing.JButton();
        toFirstAgentOfInitialPopButton1 = new javax.swing.JButton();
        toPreviousAgentOfInitialPopButton1 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jPanel10 = new javax.swing.JPanel();
        toFirstAgentOfFinalPopButton = new javax.swing.JButton();
        toPreviousAgentOfFinalPopButton = new javax.swing.JButton();
        toNextAgentOfFinalPopButton1 = new javax.swing.JButton();
        toLastAgentOfFinalPopButton1 = new javax.swing.JButton();
        jPanel11 = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        finalAgentPropertiesScrollPane = new javax.swing.JScrollPane();
        jPanel16 = new javax.swing.JPanel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        calculationEficiencyTextField1 = new javax.swing.JTextField();
        agentIdTextFieldFinal = new javax.swing.JTextField();
        inputNeuronsTextField1 = new javax.swing.JTextField();
        intermedialNeuronsTextField1 = new javax.swing.JTextField();
        outputNeuronsTextField1 = new javax.swing.JTextField();
        neuronDensityTextField1 = new javax.swing.JTextField();
        activationFunctionLinearityTextField1 = new javax.swing.JTextField();
        jPanel18 = new javax.swing.JPanel();
        jPanel19 = new javax.swing.JPanel();
        jLabel29 = new javax.swing.JLabel();
        performanceTextField1 = new javax.swing.JTextField();
        jPanel20 = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        endPopVerifierTable = new javax.swing.JTable();
        finalAgentTextualViewScrollPane = new javax.swing.JScrollPane();
        agentTextualViewTextPane = new javax.swing.JTextPane();
        finalBiasPanelGraph = new javax.swing.JPanel();
        finalBetaPanelGraph = new javax.swing.JPanel();
        finalGraphScrollPane = new javax.swing.JScrollPane();
        finalGraphPanel = new javax.swing.JPanel();
        jPanel12 = new javax.swing.JPanel();
        toNextAgentOfFinalPopButton = new javax.swing.JButton();
        toLastAgentOfFinalPopButton = new javax.swing.JButton();
        toFirstAgentOfFinalPopButton1 = new javax.swing.JButton();
        toPreviousAgentOfFinalPopButton1 = new javax.swing.JButton();
        jMenuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        newConfigurationJMenuItem = new javax.swing.JMenuItem();
        openMenuItem = new javax.swing.JMenuItem();
        saveMenuItem = new javax.swing.JMenuItem();
        saveAsMenuItem = new javax.swing.JMenuItem();
        saveAsDefaultMenutem = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        saveEvolutionMenuItem = new javax.swing.JMenuItem();
        loadEvolutionMenuItem = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        exitMenuItem = new javax.swing.JMenuItem();
        helpMenu = new javax.swing.JMenu();
        contentMenuItem = new javax.swing.JMenuItem();
        aboutMenuItem = new javax.swing.JMenuItem();
        runMenu = new javax.swing.JMenu();
        runEvolveProcessMenuItem = new javax.swing.JMenuItem();
        stopEvolveProcessMenuItem = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        cancelEvolveProcessMenuItem = new javax.swing.JMenuItem();
        ViewMenu = new javax.swing.JMenu();
        viewConfigMenuItem = new javax.swing.JMenuItem();
        viewEvolutionGraphicMenuItem = new javax.swing.JMenuItem();
        viewPopuplationDetailMenuItem = new javax.swing.JMenuItem();

        jButton4.setText("<<");
        jButton4.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        jButton4.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jButton4.setMaximumSize(new java.awt.Dimension(30, 23));
        jButton4.setMinimumSize(new java.awt.Dimension(30, 30));
        jButton4.setName(""); // NOI18N
        jButton4.setPreferredSize(new java.awt.Dimension(30, 23));

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jToolBar1.setRollover(true);

        viewCurrentConfigButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/json-icon-16x16-25bn37hi.png"))); // NOI18N
        viewCurrentConfigButton.setToolTipText("View current config");
        viewCurrentConfigButton.setFocusable(false);
        viewCurrentConfigButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        viewCurrentConfigButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        viewCurrentConfigButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewCurrentConfigButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(viewCurrentConfigButton);

        openConfigButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/load_json-icon-16x16-25bn37hi.png"))); // NOI18N
        openConfigButton.setToolTipText("Open config file");
        openConfigButton.setFocusable(false);
        openConfigButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        openConfigButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        openConfigButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openConfigButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(openConfigButton);

        saveConfigButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/save-icon-16x16-2viu8b8q.png"))); // NOI18N
        saveConfigButton.setToolTipText("save config file");
        saveConfigButton.setEnabled(false);
        saveConfigButton.setFocusable(false);
        saveConfigButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        saveConfigButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        saveConfigButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveConfigButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(saveConfigButton);

        runButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/play-16x16-375.png"))); // NOI18N
        runButton.setToolTipText("evolve");
        runButton.setEnabled(false);
        runButton.setFocusable(false);
        runButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        runButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        runButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                runButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(runButton);

        viewEvolveDataButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/lineChart-16x16-204535.png"))); // NOI18N
        viewEvolveDataButton.setToolTipText("load data from saved evolution process");
        viewEvolveDataButton.setFocusable(false);
        viewEvolveDataButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        viewEvolveDataButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        viewEvolveDataButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewEvolveDataButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(viewEvolveDataButton);

        loadEvolveDataButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/loadLineChart-16x16-204535.png"))); // NOI18N
        loadEvolveDataButton.setToolTipText("load data from saved evolution process");
        loadEvolveDataButton.setFocusable(false);
        loadEvolveDataButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        loadEvolveDataButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        loadEvolveDataButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadEvolveDataButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(loadEvolveDataButton);

        saveEvolveDataButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/saveLineChart-16x16-204535.png"))); // NOI18N
        saveEvolveDataButton.setToolTipText("save data of current evolution process");
        saveEvolveDataButton.setEnabled(false);
        saveEvolveDataButton.setFocusable(false);
        saveEvolveDataButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        saveEvolveDataButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        saveEvolveDataButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveEvolveDataButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(saveEvolveDataButton);

        loadAgentDetailsButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/xarxa_nodes_16x16.png"))); // NOI18N
        loadAgentDetailsButton.setFocusable(false);
        loadAgentDetailsButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        loadAgentDetailsButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        loadAgentDetailsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadAgentDetailsButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(loadAgentDetailsButton);

        getContentPane().add(jToolBar1, java.awt.BorderLayout.NORTH);

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Initial population"));
        jPanel2.setLayout(new java.awt.BorderLayout(1, 1));

        jPanel4.setPreferredSize(new java.awt.Dimension(50, 329));

        toPreviousAgentOfInitialPopButton.setText("<");
        toPreviousAgentOfInitialPopButton.setPreferredSize(new java.awt.Dimension(30, 23));
        toPreviousAgentOfInitialPopButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toPreviousAgentOfInitialPopButtonActionPerformed(evt);
            }
        });

        toFirstAgentOfInitialPopButton.setText("<<");
        toFirstAgentOfInitialPopButton.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        toFirstAgentOfInitialPopButton.setMargin(new java.awt.Insets(2, 2, 2, 2));
        toFirstAgentOfInitialPopButton.setMaximumSize(new java.awt.Dimension(30, 23));
        toFirstAgentOfInitialPopButton.setMinimumSize(new java.awt.Dimension(30, 30));
        toFirstAgentOfInitialPopButton.setName(""); // NOI18N
        toFirstAgentOfInitialPopButton.setPreferredSize(new java.awt.Dimension(30, 23));
        toFirstAgentOfInitialPopButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toFirstAgentOfInitialPopButtonActionPerformed(evt);
            }
        });

        toNextAgentOfInitialPopButton1.setText(">");
        toNextAgentOfInitialPopButton1.setPreferredSize(new java.awt.Dimension(30, 23));
        toNextAgentOfInitialPopButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toNextAgentOfInitialPopButton1ActionPerformed(evt);
            }
        });

        toLastAgentOfInitialPopButton1.setText(">>");
        toLastAgentOfInitialPopButton1.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        toLastAgentOfInitialPopButton1.setMargin(new java.awt.Insets(2, 2, 2, 2));
        toLastAgentOfInitialPopButton1.setMaximumSize(new java.awt.Dimension(30, 23));
        toLastAgentOfInitialPopButton1.setMinimumSize(new java.awt.Dimension(30, 23));
        toLastAgentOfInitialPopButton1.setName(""); // NOI18N
        toLastAgentOfInitialPopButton1.setPreferredSize(new java.awt.Dimension(30, 23));
        toLastAgentOfInitialPopButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toLastAgentOfInitialPopButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(toPreviousAgentOfInitialPopButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(toFirstAgentOfInitialPopButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(toLastAgentOfInitialPopButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(toNextAgentOfInitialPopButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 1, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(108, Short.MAX_VALUE)
                .addComponent(toFirstAgentOfInitialPopButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(toPreviousAgentOfInitialPopButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(toNextAgentOfInitialPopButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(toLastAgentOfInitialPopButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(109, Short.MAX_VALUE))
        );

        jPanel2.add(jPanel4, java.awt.BorderLayout.WEST);

        jPanel5.setPreferredSize(new java.awt.Dimension(1024, 329));

        jTabbedPane2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jTabbedPane2.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);
        jTabbedPane2.setMinimumSize(new java.awt.Dimension(690, 339));
        jTabbedPane2.setPreferredSize(new java.awt.Dimension(740, 339));

        initialAgentPropertiesScrollPane.setMinimumSize(new java.awt.Dimension(500, 295));
        initialAgentPropertiesScrollPane.setViewportView(null);

        jPanel7.setMaximumSize(new java.awt.Dimension(700, 32767));
        jPanel7.setMinimumSize(new java.awt.Dimension(500, 295));
        jPanel7.setPreferredSize(new java.awt.Dimension(500, 295));

        jLabel1.setFont(new java.awt.Font("Liberation Sans", 1, 13)); // NOI18N
        jLabel1.setText("Agent ID:");

        jLabel2.setText("Input Neurons:");

        jLabel3.setText("Total neuron connections:");

        jLabel4.setText("Output Neurons:");

        jLabel5.setText("Neuron Density:");

        jLabel8.setText("Calculation eficiency index:");

        jLabel9.setText("<html>Activation function<br>linearity degree:</html>");
        jLabel9.setPreferredSize(new java.awt.Dimension(208, 35));

        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder("Performance"));

        jLabel19.setText("Performance value:");

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel19)
                .addGap(18, 18, 18)
                .addComponent(performanceTextField)
                .addContainerGap())
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel19)
                    .addComponent(performanceTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        initPopVerifierTable.setModel(TableToolForIntegerTrueTableVerifier.createTableModelForIORSTrueTableVerifier(2,1,false));
        jScrollPane5.setViewportView(initPopVerifierTable);

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 359, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel15Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 194, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(calculationEficiencyTextField)
                    .addComponent(agentIdTextField)
                    .addComponent(inputNeuronsTextField)
                    .addComponent(intermedialNeuronsTextField)
                    .addComponent(outputNeuronsTextField)
                    .addComponent(neuronDensityTextField)
                    .addComponent(activationFunctionLinearityTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(14, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(agentIdTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(inputNeuronsTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(intermedialNeuronsTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addComponent(outputNeuronsTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5)
                            .addComponent(neuronDensityTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel8)
                            .addComponent(calculationEficiencyTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(activationFunctionLinearityTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(9, Short.MAX_VALUE))
        );

        initialAgentPropertiesScrollPane.setViewportView(jPanel7);

        jTabbedPane2.addTab("Agent properties", initialAgentPropertiesScrollPane);

        initialAgentTextualViewScrollPane.setViewportView(agentTextualViewTextPane1);

        jTabbedPane2.addTab("Agent textual view", initialAgentTextualViewScrollPane);

        initialBiasPanelGraph.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        initialBiasPanelGraph.setLayout(new java.awt.BorderLayout());
        jTabbedPane2.addTab("Bias distribution", initialBiasPanelGraph);

        initialBetaPanelGraph.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        initialBetaPanelGraph.setLayout(new java.awt.BorderLayout());
        jTabbedPane2.addTab("Beta distribution in sigmoid function", initialBetaPanelGraph);

        initialGraphScrollPane.setBackground(new java.awt.Color(242, 242, 242));
        initialGraphScrollPane.setPreferredSize(new java.awt.Dimension(1000, 1000));

        initialGraphPanel.setPreferredSize(new java.awt.Dimension(1000, 359));

        javax.swing.GroupLayout initialGraphPanelLayout = new javax.swing.GroupLayout(initialGraphPanel);
        initialGraphPanel.setLayout(initialGraphPanelLayout);
        initialGraphPanelLayout.setHorizontalGroup(
            initialGraphPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1000, Short.MAX_VALUE)
        );
        initialGraphPanelLayout.setVerticalGroup(
            initialGraphPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 359, Short.MAX_VALUE)
        );

        initialGraphScrollPane.setViewportView(initialGraphPanel);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jTabbedPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 695, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(initialGraphScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 629, Short.MAX_VALUE)
                .addGap(1, 1, 1))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jTabbedPane2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(initialGraphScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addGap(2, 2, 2))
        );

        jPanel2.add(jPanel5, java.awt.BorderLayout.CENTER);

        jPanel6.setPreferredSize(new java.awt.Dimension(50, 329));

        toNextAgentOfInitialPopButton.setText(">");
        toNextAgentOfInitialPopButton.setPreferredSize(new java.awt.Dimension(30, 23));
        toNextAgentOfInitialPopButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toNextAgentOfInitialPopButtonActionPerformed(evt);
            }
        });

        toLastAgentOfInitialPopButton.setText(">>");
        toLastAgentOfInitialPopButton.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        toLastAgentOfInitialPopButton.setMargin(new java.awt.Insets(2, 0, 2, 0));
        toLastAgentOfInitialPopButton.setMaximumSize(new java.awt.Dimension(30, 23));
        toLastAgentOfInitialPopButton.setMinimumSize(new java.awt.Dimension(30, 23));
        toLastAgentOfInitialPopButton.setName(""); // NOI18N
        toLastAgentOfInitialPopButton.setPreferredSize(new java.awt.Dimension(30, 23));
        toLastAgentOfInitialPopButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toLastAgentOfInitialPopButtonActionPerformed(evt);
            }
        });

        toFirstAgentOfInitialPopButton1.setText("<<");
        toFirstAgentOfInitialPopButton1.setMargin(new java.awt.Insets(2, 0, 2, 0));
        toFirstAgentOfInitialPopButton1.setMaximumSize(new java.awt.Dimension(30, 23));
        toFirstAgentOfInitialPopButton1.setMinimumSize(new java.awt.Dimension(30, 23));
        toFirstAgentOfInitialPopButton1.setName(""); // NOI18N
        toFirstAgentOfInitialPopButton1.setPreferredSize(new java.awt.Dimension(30, 23));
        toFirstAgentOfInitialPopButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toFirstAgentOfInitialPopButton1ActionPerformed(evt);
            }
        });

        toPreviousAgentOfInitialPopButton1.setText("<");
        toPreviousAgentOfInitialPopButton1.setPreferredSize(new java.awt.Dimension(30, 23));
        toPreviousAgentOfInitialPopButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toPreviousAgentOfInitialPopButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(toLastAgentOfInitialPopButton, javax.swing.GroupLayout.DEFAULT_SIZE, 37, Short.MAX_VALUE)
                            .addComponent(toNextAgentOfInitialPopButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(toPreviousAgentOfInitialPopButton1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(toFirstAgentOfInitialPopButton1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 38, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap(111, Short.MAX_VALUE)
                .addComponent(toFirstAgentOfInitialPopButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(toPreviousAgentOfInitialPopButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(toNextAgentOfInitialPopButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(toLastAgentOfInitialPopButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(112, Short.MAX_VALUE))
        );

        jPanel2.add(jPanel6, java.awt.BorderLayout.EAST);

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Final Population"));
        jPanel3.setLayout(new java.awt.BorderLayout());

        jPanel10.setPreferredSize(new java.awt.Dimension(50, 329));

        toFirstAgentOfFinalPopButton.setText("<<");
        toFirstAgentOfFinalPopButton.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        toFirstAgentOfFinalPopButton.setMargin(new java.awt.Insets(2, 2, 2, 2));
        toFirstAgentOfFinalPopButton.setMaximumSize(new java.awt.Dimension(30, 23));
        toFirstAgentOfFinalPopButton.setMinimumSize(new java.awt.Dimension(30, 30));
        toFirstAgentOfFinalPopButton.setName(""); // NOI18N
        toFirstAgentOfFinalPopButton.setPreferredSize(new java.awt.Dimension(30, 23));
        toFirstAgentOfFinalPopButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toFirstAgentOfFinalPopButtonActionPerformed(evt);
            }
        });

        toPreviousAgentOfFinalPopButton.setText("<");
        toPreviousAgentOfFinalPopButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toPreviousAgentOfFinalPopButtonActionPerformed(evt);
            }
        });

        toNextAgentOfFinalPopButton1.setText(">");
        toNextAgentOfFinalPopButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toNextAgentOfFinalPopButton1ActionPerformed(evt);
            }
        });

        toLastAgentOfFinalPopButton1.setText(">>");
        toLastAgentOfFinalPopButton1.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        toLastAgentOfFinalPopButton1.setMargin(new java.awt.Insets(2, 2, 2, 2));
        toLastAgentOfFinalPopButton1.setMaximumSize(new java.awt.Dimension(30, 23));
        toLastAgentOfFinalPopButton1.setMinimumSize(new java.awt.Dimension(30, 30));
        toLastAgentOfFinalPopButton1.setName(""); // NOI18N
        toLastAgentOfFinalPopButton1.setPreferredSize(new java.awt.Dimension(30, 23));
        toLastAgentOfFinalPopButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toLastAgentOfFinalPopButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap(7, Short.MAX_VALUE)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(toPreviousAgentOfFinalPopButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(toFirstAgentOfFinalPopButton, javax.swing.GroupLayout.DEFAULT_SIZE, 35, Short.MAX_VALUE))
                .addContainerGap(8, Short.MAX_VALUE))
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(toNextAgentOfFinalPopButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(toLastAgentOfFinalPopButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                .addContainerGap(119, Short.MAX_VALUE)
                .addComponent(toFirstAgentOfFinalPopButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(toPreviousAgentOfFinalPopButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(toNextAgentOfFinalPopButton1)
                .addGap(10, 10, 10)
                .addComponent(toLastAgentOfFinalPopButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(119, Short.MAX_VALUE))
        );

        jPanel3.add(jPanel10, java.awt.BorderLayout.WEST);

        jPanel11.setAutoscrolls(true);
        jPanel11.setPreferredSize(new java.awt.Dimension(1024, 329));

        jTabbedPane1.setPreferredSize(new java.awt.Dimension(600, 500));

        finalAgentPropertiesScrollPane.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        finalAgentPropertiesScrollPane.setPreferredSize(new java.awt.Dimension(542, 500));
        finalAgentPropertiesScrollPane.setViewportView(null);

        jPanel16.setMaximumSize(new java.awt.Dimension(704, 32767));
        jPanel16.setMinimumSize(new java.awt.Dimension(300, 0));

        jLabel20.setFont(new java.awt.Font("Liberation Sans", 1, 13)); // NOI18N
        jLabel20.setText("Agent ID:");

        jLabel21.setText("Input Neurons:");

        jLabel22.setText("Total neuron connections:");

        jLabel23.setText("Output Neurons:");

        jLabel24.setText("Neuron Density:");

        jLabel27.setText("Calculation eficiency index:");

        jLabel28.setText("<html>Activation function<br>linearity degree:</html>");
        jLabel28.setPreferredSize(new java.awt.Dimension(208, 35));

        jPanel18.setBorder(javax.swing.BorderFactory.createTitledBorder("Performance"));

        jLabel29.setText("Performance value:");

        javax.swing.GroupLayout jPanel19Layout = new javax.swing.GroupLayout(jPanel19);
        jPanel19.setLayout(jPanel19Layout);
        jPanel19Layout.setHorizontalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel19Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel29)
                .addGap(18, 18, 18)
                .addComponent(performanceTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 252, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(8, Short.MAX_VALUE))
        );
        jPanel19Layout.setVerticalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel19Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel29)
                    .addComponent(performanceTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        endPopVerifierTable.setModel(TableToolForIntegerTrueTableVerifier.createTableModelForIORSTrueTableVerifier(2,1,false));
        jScrollPane6.setViewportView(endPopVerifierTable);

        javax.swing.GroupLayout jPanel20Layout = new javax.swing.GroupLayout(jPanel20);
        jPanel20.setLayout(jPanel20Layout);
        jPanel20Layout.setHorizontalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel20Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 371, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel20Layout.setVerticalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel20Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 259, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel18Layout = new javax.swing.GroupLayout(jPanel18);
        jPanel18.setLayout(jPanel18Layout);
        jPanel18Layout.setHorizontalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel20, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel19, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        jPanel18Layout.setVerticalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel20, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel27)
                    .addComponent(jLabel20)
                    .addComponent(jLabel21)
                    .addComponent(jLabel22)
                    .addComponent(jLabel23)
                    .addComponent(jLabel24)
                    .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(calculationEficiencyTextField1)
                    .addComponent(agentIdTextFieldFinal)
                    .addComponent(inputNeuronsTextField1)
                    .addComponent(intermedialNeuronsTextField1)
                    .addComponent(outputNeuronsTextField1)
                    .addComponent(neuronDensityTextField1)
                    .addComponent(activationFunctionLinearityTextField1, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addComponent(jPanel18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel18, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel16Layout.createSequentialGroup()
                        .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel20)
                            .addComponent(agentIdTextFieldFinal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel21)
                            .addComponent(inputNeuronsTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel22)
                            .addComponent(intermedialNeuronsTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel23)
                            .addComponent(outputNeuronsTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel24)
                            .addComponent(neuronDensityTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel27)
                            .addComponent(calculationEficiencyTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel28, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(activationFunctionLinearityTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(76, Short.MAX_VALUE))
        );

        finalAgentPropertiesScrollPane.setViewportView(jPanel16);

        jTabbedPane1.addTab("Agent properties", finalAgentPropertiesScrollPane);

        finalAgentTextualViewScrollPane.setViewportView(agentTextualViewTextPane);

        jTabbedPane1.addTab("Agent textual view", finalAgentTextualViewScrollPane);

        finalBiasPanelGraph.setLayout(new java.awt.BorderLayout());
        jTabbedPane1.addTab("Bias distribution", finalBiasPanelGraph);

        finalBetaPanelGraph.setLayout(new java.awt.BorderLayout());
        jTabbedPane1.addTab("Beta distribution in sigmoid function", finalBetaPanelGraph);

        finalGraphScrollPane.setPreferredSize(new java.awt.Dimension(1000, 1000));

        finalGraphPanel.setPreferredSize(new java.awt.Dimension(1732, 436));

        javax.swing.GroupLayout finalGraphPanelLayout = new javax.swing.GroupLayout(finalGraphPanel);
        finalGraphPanel.setLayout(finalGraphPanelLayout);
        finalGraphPanelLayout.setHorizontalGroup(
            finalGraphPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1732, Short.MAX_VALUE)
        );
        finalGraphPanelLayout.setVerticalGroup(
            finalGraphPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 436, Short.MAX_VALUE)
        );

        finalGraphScrollPane.setViewportView(finalGraphPanel);

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 699, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(finalGraphScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 622, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(finalGraphScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 351, Short.MAX_VALUE))
                .addGap(5, 5, 5))
        );

        jPanel3.add(jPanel11, java.awt.BorderLayout.CENTER);

        jPanel12.setPreferredSize(new java.awt.Dimension(50, 329));

        toNextAgentOfFinalPopButton.setText(">");
        toNextAgentOfFinalPopButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toNextAgentOfFinalPopButtonActionPerformed(evt);
            }
        });

        toLastAgentOfFinalPopButton.setText(">>");
        toLastAgentOfFinalPopButton.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        toLastAgentOfFinalPopButton.setMargin(new java.awt.Insets(2, 2, 2, 2));
        toLastAgentOfFinalPopButton.setMaximumSize(new java.awt.Dimension(30, 23));
        toLastAgentOfFinalPopButton.setMinimumSize(new java.awt.Dimension(30, 30));
        toLastAgentOfFinalPopButton.setName(""); // NOI18N
        toLastAgentOfFinalPopButton.setPreferredSize(new java.awt.Dimension(30, 23));
        toLastAgentOfFinalPopButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toLastAgentOfFinalPopButtonActionPerformed(evt);
            }
        });

        toFirstAgentOfFinalPopButton1.setText("<<");
        toFirstAgentOfFinalPopButton1.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        toFirstAgentOfFinalPopButton1.setMargin(new java.awt.Insets(2, 2, 2, 2));
        toFirstAgentOfFinalPopButton1.setMaximumSize(new java.awt.Dimension(30, 23));
        toFirstAgentOfFinalPopButton1.setMinimumSize(new java.awt.Dimension(30, 30));
        toFirstAgentOfFinalPopButton1.setName(""); // NOI18N
        toFirstAgentOfFinalPopButton1.setPreferredSize(new java.awt.Dimension(30, 23));
        toFirstAgentOfFinalPopButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toFirstAgentOfFinalPopButton1ActionPerformed(evt);
            }
        });

        toPreviousAgentOfFinalPopButton1.setText("<");
        toPreviousAgentOfFinalPopButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toPreviousAgentOfFinalPopButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(toNextAgentOfFinalPopButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(toLastAgentOfFinalPopButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(toPreviousAgentOfFinalPopButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(toFirstAgentOfFinalPopButton1, javax.swing.GroupLayout.DEFAULT_SIZE, 38, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel12Layout.createSequentialGroup()
                .addContainerGap(119, Short.MAX_VALUE)
                .addComponent(toFirstAgentOfFinalPopButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(toPreviousAgentOfFinalPopButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(toNextAgentOfFinalPopButton)
                .addGap(10, 10, 10)
                .addComponent(toLastAgentOfFinalPopButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(119, Short.MAX_VALUE))
        );

        jPanel3.add(jPanel12, java.awt.BorderLayout.EAST);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 1443, Short.MAX_VALUE)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 365, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);

        fileMenu.setMnemonic('f');
        fileMenu.setText("File");

        newConfigurationJMenuItem.setText("New configuration");
        newConfigurationJMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newConfigurationJMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(newConfigurationJMenuItem);

        openMenuItem.setMnemonic('o');
        openMenuItem.setText("Open configuration");
        openMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(openMenuItem);

        saveMenuItem.setMnemonic('s');
        saveMenuItem.setText("Save configuration");
        saveMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(saveMenuItem);

        saveAsMenuItem.setMnemonic('a');
        saveAsMenuItem.setText("Save configuration As ...");
        saveAsMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveAsMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(saveAsMenuItem);

        saveAsDefaultMenutem.setText("Save configuration as default");
        saveAsDefaultMenutem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveAsDefaultMenutemActionPerformed(evt);
            }
        });
        fileMenu.add(saveAsDefaultMenutem);

        jSeparator1.setName(""); // NOI18N
        fileMenu.add(jSeparator1);

        saveEvolutionMenuItem.setText("Save evolution data");
        saveEvolutionMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveEvolutionMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(saveEvolutionMenuItem);

        loadEvolutionMenuItem.setText("Load evolution data");
        loadEvolutionMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadEvolutionMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(loadEvolutionMenuItem);
        fileMenu.add(jSeparator3);

        exitMenuItem.setMnemonic('x');
        exitMenuItem.setText("Exit");
        exitMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(exitMenuItem);

        jMenuBar.add(fileMenu);

        helpMenu.setMnemonic('h');
        helpMenu.setText("Help");

        contentMenuItem.setMnemonic('c');
        contentMenuItem.setText("Contents");
        contentMenuItem.setEnabled(false);
        contentMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                contentMenuItemActionPerformed(evt);
            }
        });
        helpMenu.add(contentMenuItem);

        aboutMenuItem.setMnemonic('a');
        aboutMenuItem.setText("About");
        aboutMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutMenuItemActionPerformed(evt);
            }
        });
        helpMenu.add(aboutMenuItem);

        jMenuBar.add(helpMenu);

        runMenu.setText("Run");

        runEvolveProcessMenuItem.setText("Run evolution process");
        runEvolveProcessMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                runEvolveProcessMenuItemActionPerformed(evt);
            }
        });
        runMenu.add(runEvolveProcessMenuItem);

        stopEvolveProcessMenuItem.setText("Stop evolution process");
        runMenu.add(stopEvolveProcessMenuItem);
        runMenu.add(jSeparator2);

        cancelEvolveProcessMenuItem.setText("Cancel evolution process");
        runMenu.add(cancelEvolveProcessMenuItem);

        jMenuBar.add(runMenu);

        ViewMenu.setText("View");

        viewConfigMenuItem.setText("config data");
        ViewMenu.add(viewConfigMenuItem);

        viewEvolutionGraphicMenuItem.setText("evoljtion process data (text and graphic)");
        viewEvolutionGraphicMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewEvolutionGraphicMenuItemActionPerformed(evt);
            }
        });
        ViewMenu.add(viewEvolutionGraphicMenuItem);

        viewPopuplationDetailMenuItem.setText("neural agents detail");
        viewPopuplationDetailMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewPopuplationDetailMenuItemActionPerformed(evt);
            }
        });
        ViewMenu.add(viewPopuplationDetailMenuItem);

        jMenuBar.add(ViewMenu);

        setJMenuBar(jMenuBar);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void openConfigButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openConfigButtonActionPerformed
        // TODO add your handling code here:
        //        openMenuItemActionPerformed(evt);
    }//GEN-LAST:event_openConfigButtonActionPerformed

    private void saveConfigButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveConfigButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_saveConfigButtonActionPerformed

    private void runButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_runButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_runButtonActionPerformed

    private void saveEvolveDataButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveEvolveDataButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_saveEvolveDataButtonActionPerformed

    private void loadEvolveDataButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadEvolveDataButtonActionPerformed
        // TODO add your handling code here:
        this.getMenuEvents().loadDataItemEvent(new File(getDisplayedDataFilename()), getSharedData());
        this.getMenuEvents().createAndShowEvolutionaryProcessFrame(this.getSharedData());
        this.setVisible(false);
    }//GEN-LAST:event_loadEvolveDataButtonActionPerformed

    private void toFirstAgentOfFinalPopButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_toFirstAgentOfFinalPopButtonActionPerformed
        // TODO add your handling code here:
        this.finalPopViewer.setPostion(0);
        this.finalGraphPanel.removeAll();
        this.updateAgentOfFinalPop();
        this.finalGraphPanel.repaint();
        this.finalGraphPanel.revalidate();
    }//GEN-LAST:event_toFirstAgentOfFinalPopButtonActionPerformed

    private void toPreviousAgentOfInitialPopButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_toPreviousAgentOfInitialPopButtonActionPerformed
        // TODO add your handling code here:
        this.initialPopViewer.jumpPostion(-1);
        this.initialGraphPanel.removeAll();
        this.updateAgentOfInitialPop();
        this.initialGraphPanel.repaint();
        this.initialGraphPanel.revalidate();    
        refreshInitialJFreeDistribution();
    }//GEN-LAST:event_toPreviousAgentOfInitialPopButtonActionPerformed

    private void toFirstAgentOfInitialPopButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_toFirstAgentOfInitialPopButtonActionPerformed
        // TODO add your handling code here:
        this.initialPopViewer.setPostion(0);
        this.initialGraphPanel.removeAll();
        this.updateAgentOfInitialPop();
        this.initialGraphPanel.repaint();
        this.initialGraphPanel.revalidate();
        refreshInitialJFreeDistribution();
    }//GEN-LAST:event_toFirstAgentOfInitialPopButtonActionPerformed

    private void toPreviousAgentOfFinalPopButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_toPreviousAgentOfFinalPopButtonActionPerformed
        // TODO add your handling code here:
        this.finalPopViewer.jumpPostion(-1);
        this.finalGraphPanel.removeAll();
        this.updateAgentOfFinalPop();
        this.finalGraphPanel.repaint();
        this.finalGraphPanel.revalidate();  
        refreshFinalJFreeDistribution();
    }//GEN-LAST:event_toPreviousAgentOfFinalPopButtonActionPerformed

    private void toNextAgentOfInitialPopButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_toNextAgentOfInitialPopButtonActionPerformed
        // TODO add your handling code here:
        this.initialPopViewer.jumpPostion(1);
        this.initialGraphPanel.removeAll();
        this.updateAgentOfInitialPop();
        this.initialGraphPanel.repaint();
        this.initialGraphPanel.revalidate();      
        refreshInitialJFreeDistribution();
    }//GEN-LAST:event_toNextAgentOfInitialPopButtonActionPerformed

    private void toLastAgentOfInitialPopButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_toLastAgentOfInitialPopButtonActionPerformed
        this.initialPopViewer.setPositionToLast();
        this.initialGraphPanel.removeAll();
        this.updateAgentOfInitialPop();
        this.initialGraphPanel.repaint();
        this.initialGraphPanel.revalidate();
        refreshInitialJFreeDistribution();
    }//GEN-LAST:event_toLastAgentOfInitialPopButtonActionPerformed

    private void toNextAgentOfFinalPopButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_toNextAgentOfFinalPopButtonActionPerformed
        // TODO add your handling code here:
        this.finalPopViewer.jumpPostion(1);
        this.finalGraphPanel.removeAll();
        this.updateAgentOfFinalPop();
        this.finalGraphPanel.repaint();
        this.finalGraphPanel.revalidate();  
        refreshFinalJFreeDistribution();
    }//GEN-LAST:event_toNextAgentOfFinalPopButtonActionPerformed

    private void toLastAgentOfFinalPopButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_toLastAgentOfFinalPopButtonActionPerformed
        // TODO add your handling code here:
        this.finalPopViewer.setPositionToLast();
        this.finalGraphPanel.removeAll();
        this.updateAgentOfFinalPop();
        this.finalGraphPanel.repaint();
        this.finalGraphPanel.revalidate();        
    }//GEN-LAST:event_toLastAgentOfFinalPopButtonActionPerformed

    private void toPreviousAgentOfInitialPopButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_toPreviousAgentOfInitialPopButton1ActionPerformed
        // TODO add your handling code here:
        toPreviousAgentOfInitialPopButtonActionPerformed(evt);
    }//GEN-LAST:event_toPreviousAgentOfInitialPopButton1ActionPerformed

    private void toNextAgentOfInitialPopButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_toNextAgentOfInitialPopButton1ActionPerformed
        // TODO add your handling code here:
        toNextAgentOfInitialPopButtonActionPerformed(evt);
    }//GEN-LAST:event_toNextAgentOfInitialPopButton1ActionPerformed

    private void toLastAgentOfInitialPopButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_toLastAgentOfInitialPopButton1ActionPerformed
        // TODO add your handling code here:
        toLastAgentOfInitialPopButtonActionPerformed(evt);
    }//GEN-LAST:event_toLastAgentOfInitialPopButton1ActionPerformed

    private void toFirstAgentOfFinalPopButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_toFirstAgentOfFinalPopButton1ActionPerformed
        // TODO add your handling code here:
        toFirstAgentOfFinalPopButtonActionPerformed(evt);
    }//GEN-LAST:event_toFirstAgentOfFinalPopButton1ActionPerformed

    private void toPreviousAgentOfFinalPopButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_toPreviousAgentOfFinalPopButton1ActionPerformed
        // TODO add your handling code here:
        toPreviousAgentOfFinalPopButtonActionPerformed(evt);
    }//GEN-LAST:event_toPreviousAgentOfFinalPopButton1ActionPerformed

    private void toNextAgentOfFinalPopButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_toNextAgentOfFinalPopButton1ActionPerformed
        // TODO add your handling code here:
        toNextAgentOfFinalPopButtonActionPerformed(evt);
    }//GEN-LAST:event_toNextAgentOfFinalPopButton1ActionPerformed

    private void toLastAgentOfFinalPopButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_toLastAgentOfFinalPopButton1ActionPerformed
        // TODO add your handling code here:
        toLastAgentOfFinalPopButtonActionPerformed(evt);
    }//GEN-LAST:event_toLastAgentOfFinalPopButton1ActionPerformed

    private void toFirstAgentOfInitialPopButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_toFirstAgentOfInitialPopButton1ActionPerformed
        // TODO add your handling code here:
        toFirstAgentOfInitialPopButtonActionPerformed(evt);
    }//GEN-LAST:event_toFirstAgentOfInitialPopButton1ActionPerformed

    private void viewCurrentConfigButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewCurrentConfigButtonActionPerformed
        // TODO add your handling code here:
        NetvolutionBasicFrame nf = this.getEvolutionFrames(CntFrames.CONFIGURE_SYSTEM);
        this.getMenuEvents().showFrame(nf);
        this.setVisible(false);
    }//GEN-LAST:event_viewCurrentConfigButtonActionPerformed

    private void viewEvolveDataButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewEvolveDataButtonActionPerformed
        // TODO add your handling code here:
        this.getMenuEvents().createAndShowEvolutionaryProcessFrame(this.getSharedData());
        this.setVisible(false);
    }//GEN-LAST:event_viewEvolveDataButtonActionPerformed

    private void loadAgentDetailsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadAgentDetailsButtonActionPerformed
        // TODO add your handling code here:
        this.getMenuEvents().loadDataItemEvent(new File(getDisplayedDataFilename()), getSharedData());
        updateData();
    }//GEN-LAST:event_loadAgentDetailsButtonActionPerformed

    private void newConfigurationJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newConfigurationJMenuItemActionPerformed

    }//GEN-LAST:event_newConfigurationJMenuItemActionPerformed

    private void openMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openMenuItemActionPerformed
        // TODO add your handling code here:
        //        getMenuEvents().selectAndOpenConfigItemEvent((fAndCont) -> {
            //            this.setJsonConfigData(fAndCont.getContent());
            //            this.setFileConfigName(fAndCont.getFileName());
            //            updateGuiWithConfigData();
            //            return null;
            //        });
    }//GEN-LAST:event_openMenuItemActionPerformed

    private void saveMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveMenuItemActionPerformed
        // TODO add your handling code here:
        //        if(getFileConfigName()==null){
            //            setFileConfigName(getDefaultFileNameConfig());
            //        }
        //        updateConfigWithGuiData();
        //        getMenuEvents().saveConfigItemEvent(getJsonConfigData(), getFileConfigName());
    }//GEN-LAST:event_saveMenuItemActionPerformed

    private void saveAsMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveAsMenuItemActionPerformed
        // TODO add your handling code here:
        //        updateConfigWithGuiData();
        //        getMenuEvents().saveConfigAsItemEvent(getJsonConfigData(), (fname) -> {
            //            this.setFileConfigName(fname);
            //            return null;
            //        });
    }//GEN-LAST:event_saveAsMenuItemActionPerformed

    private void saveAsDefaultMenutemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveAsDefaultMenutemActionPerformed
        // TODO add your handling code here:
        //        setFileConfigName(getDefaultFileNameConfig());
        //        updateConfigWithGuiData();
        //        getMenuEvents().saveConfigItemEvent(getJsonConfigData(), getFileConfigName());
    }//GEN-LAST:event_saveAsDefaultMenutemActionPerformed

    private void saveEvolutionMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveEvolutionMenuItemActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_saveEvolutionMenuItemActionPerformed

    private void loadEvolutionMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadEvolutionMenuItemActionPerformed
        // TODO add your handling code here:
        loadEvolveDataButtonActionPerformed(evt);
    }//GEN-LAST:event_loadEvolutionMenuItemActionPerformed

    private void exitMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitMenuItemActionPerformed
        //        System.exit(0);
        this.setVisible(false);
    }//GEN-LAST:event_exitMenuItemActionPerformed

    private void contentMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_contentMenuItemActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_contentMenuItemActionPerformed

    private void aboutMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aboutMenuItemActionPerformed
        // TODO add your handling code here:
        getMenuEvents().aboutItemEvent();
    }//GEN-LAST:event_aboutMenuItemActionPerformed

    private void runEvolveProcessMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_runEvolveProcessMenuItemActionPerformed
        // TODO add your handling code here:
        runButtonActionPerformed(evt);
    }//GEN-LAST:event_runEvolveProcessMenuItemActionPerformed

    private void viewEvolutionGraphicMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewEvolutionGraphicMenuItemActionPerformed
        // TODO add your handling code here:
        //        viewEvolveDataButtonActionPerformed(evt);
    }//GEN-LAST:event_viewEvolutionGraphicMenuItemActionPerformed

    private void viewPopuplationDetailMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewPopuplationDetailMenuItemActionPerformed
        // TODO add your handling code here:
//        viewAgentDetailsButtonActionPerformed(evt);
    }//GEN-LAST:event_viewPopuplationDetailMenuItemActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu ViewMenu;
    private javax.swing.JMenuItem aboutMenuItem;
    private javax.swing.JTextField activationFunctionLinearityTextField;
    private javax.swing.JTextField activationFunctionLinearityTextField1;
    private javax.swing.JTextField agentIdTextField;
    private javax.swing.JTextField agentIdTextFieldFinal;
    private javax.swing.JTextPane agentTextualViewTextPane;
    private javax.swing.JTextPane agentTextualViewTextPane1;
    private javax.swing.JTextField calculationEficiencyTextField;
    private javax.swing.JTextField calculationEficiencyTextField1;
    private javax.swing.JMenuItem cancelEvolveProcessMenuItem;
    private javax.swing.JMenuItem contentMenuItem;
    private javax.swing.JTable endPopVerifierTable;
    private javax.swing.JMenuItem exitMenuItem;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JScrollPane finalAgentPropertiesScrollPane;
    private javax.swing.JScrollPane finalAgentTextualViewScrollPane;
    private javax.swing.JPanel finalBetaPanelGraph;
    private javax.swing.JPanel finalBiasPanelGraph;
    private javax.swing.JPanel finalGraphPanel;
    private javax.swing.JScrollPane finalGraphScrollPane;
    private javax.swing.JMenu helpMenu;
    private javax.swing.JTable initPopVerifierTable;
    private javax.swing.JScrollPane initialAgentPropertiesScrollPane;
    private javax.swing.JScrollPane initialAgentTextualViewScrollPane;
    private javax.swing.JPanel initialBetaPanelGraph;
    private javax.swing.JPanel initialBiasPanelGraph;
    private javax.swing.JPanel initialGraphPanel;
    private javax.swing.JScrollPane initialGraphScrollPane;
    private javax.swing.JTextField inputNeuronsTextField;
    private javax.swing.JTextField inputNeuronsTextField1;
    private javax.swing.JTextField intermedialNeuronsTextField;
    private javax.swing.JTextField intermedialNeuronsTextField1;
    private javax.swing.JButton jButton4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenuBar jMenuBar;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel19;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel20;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JButton loadAgentDetailsButton;
    private javax.swing.JMenuItem loadEvolutionMenuItem;
    private javax.swing.JButton loadEvolveDataButton;
    private javax.swing.JTextField neuronDensityTextField;
    private javax.swing.JTextField neuronDensityTextField1;
    private javax.swing.JMenuItem newConfigurationJMenuItem;
    private javax.swing.JButton openConfigButton;
    private javax.swing.JMenuItem openMenuItem;
    private javax.swing.JTextField outputNeuronsTextField;
    private javax.swing.JTextField outputNeuronsTextField1;
    private javax.swing.JTextField performanceTextField;
    private javax.swing.JTextField performanceTextField1;
    private javax.swing.JButton runButton;
    private javax.swing.JMenuItem runEvolveProcessMenuItem;
    private javax.swing.JMenu runMenu;
    private javax.swing.JMenuItem saveAsDefaultMenutem;
    private javax.swing.JMenuItem saveAsMenuItem;
    private javax.swing.JButton saveConfigButton;
    private javax.swing.JMenuItem saveEvolutionMenuItem;
    private javax.swing.JButton saveEvolveDataButton;
    private javax.swing.JMenuItem saveMenuItem;
    private javax.swing.JMenuItem stopEvolveProcessMenuItem;
    private javax.swing.JButton toFirstAgentOfFinalPopButton;
    private javax.swing.JButton toFirstAgentOfFinalPopButton1;
    private javax.swing.JButton toFirstAgentOfInitialPopButton;
    private javax.swing.JButton toFirstAgentOfInitialPopButton1;
    private javax.swing.JButton toLastAgentOfFinalPopButton;
    private javax.swing.JButton toLastAgentOfFinalPopButton1;
    private javax.swing.JButton toLastAgentOfInitialPopButton;
    private javax.swing.JButton toLastAgentOfInitialPopButton1;
    private javax.swing.JButton toNextAgentOfFinalPopButton;
    private javax.swing.JButton toNextAgentOfFinalPopButton1;
    private javax.swing.JButton toNextAgentOfInitialPopButton;
    private javax.swing.JButton toNextAgentOfInitialPopButton1;
    private javax.swing.JButton toPreviousAgentOfFinalPopButton;
    private javax.swing.JButton toPreviousAgentOfFinalPopButton1;
    private javax.swing.JButton toPreviousAgentOfInitialPopButton;
    private javax.swing.JButton toPreviousAgentOfInitialPopButton1;
    private javax.swing.JMenuItem viewConfigMenuItem;
    private javax.swing.JButton viewCurrentConfigButton;
    private javax.swing.JMenuItem viewEvolutionGraphicMenuItem;
    private javax.swing.JButton viewEvolveDataButton;
    private javax.swing.JMenuItem viewPopuplationDetailMenuItem;
    // End of variables declaration//GEN-END:variables

    @Override
    public void updateData() {
        initPopulations();
        updateAgent();
        updateViewForControls();
    }
    
    private void updateViewForControls(){
        viewCurrentConfigButton.setEnabled(true);
        openConfigButton.setEnabled(true);
        saveConfigButton.setEnabled(false);
        runButton.setEnabled(false);
        viewEvolveDataButton.setEnabled(true);
        loadEvolveDataButton.setEnabled(true);
        saveEvolveDataButton.setEnabled(false);
        loadAgentDetailsButton.setEnabled(true);
    }        
}
