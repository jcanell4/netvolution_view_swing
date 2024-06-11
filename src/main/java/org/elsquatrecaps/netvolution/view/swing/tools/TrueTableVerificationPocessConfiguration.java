package org.elsquatrecaps.netvolution.view.swing.tools;

import javax.swing.JFrame;

/**
 *
 * @author josep
 */
public class TrueTableVerificationPocessConfiguration  extends VerificationProcessConfiguration<IntegerTableEditor>{

    public TrueTableVerificationPocessConfiguration(JFrame frame) {
        this("True table", frame);
    }

    public TrueTableVerificationPocessConfiguration(PositionType editorPositionType, JFrame frame) {
        this("True table", editorPositionType, frame);
    }

    public TrueTableVerificationPocessConfiguration(String name, JFrame frame) {
        super("TRUE_TABLE", name, new IntegerTableEditor(PositionType.FIXED, frame));
    }
    
    public TrueTableVerificationPocessConfiguration(String name, PositionType editorPositionType, JFrame frame) {
        super("TRUE_TABLE", name, new IntegerTableEditor(editorPositionType, frame));
    }
    
}
