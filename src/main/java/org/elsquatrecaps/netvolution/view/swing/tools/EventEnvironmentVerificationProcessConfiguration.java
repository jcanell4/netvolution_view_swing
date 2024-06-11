/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.elsquatrecaps.netvolution.view.swing.tools;

/**
 *
 * @author josep
 */
public class EventEnvironmentVerificationProcessConfiguration extends VerificationProcessConfiguration<EventEnvironmentEditor>{

    public EventEnvironmentVerificationProcessConfiguration() {
        this("Event Environment");
    }
    
    public EventEnvironmentVerificationProcessConfiguration(PositionType editorPositionType) {
        this("Event Environment", editorPositionType);
    }

    public EventEnvironmentVerificationProcessConfiguration(String name) {
        super("EVENT_ENVIRONMENT", name, new UnimplementedEditor());
    }
    
    public EventEnvironmentVerificationProcessConfiguration(String name, PositionType editorPositionType) {
        super("EVENT_ENVIRONMENT",name, new UnimplementedEditor());
    }
    
}
