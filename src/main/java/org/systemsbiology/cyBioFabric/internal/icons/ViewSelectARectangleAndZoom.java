package org.systemsbiology.cyBioFabric.internal.icons;

import java.awt.event.ActionEvent;
import java.util.Map;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.event.CyEventHelper;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.model.events.UpdateNetworkPresentationEvent;
import org.systemsbiology.cyBioFabric.internal.biofabric.cmd.CommandSet;
import org.systemsbiology.cyBioFabric.internal.icons.actions.ZoomToRectAction;

public class ViewSelectARectangleAndZoom extends AbstractCyAction implements BioFabricImageIcon{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7715729109562083703L;
	private String commandSetName;
	private ZoomToRectAction zoomToRectAction;
	
	public ViewSelectARectangleAndZoom(Map<String, String> configProps,  CyApplicationManager applicationManager, 
			CyNetworkViewManager networkViewManager, BioFabricViewFactoryPredicate taskFactoryPredicate, CyEventHelper eventHelper){
	    super(configProps, applicationManager, networkViewManager, taskFactoryPredicate);	    	    
	    
	    this.zoomToRectAction = new ZoomToRectAction(taskFactoryPredicate, eventHelper, commandSetName);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {		
		zoomToRectAction.fireEvent();
	}
	
	@Override
	public void registerCommandSetName(String commandSetName){
		this.commandSetName = commandSetName;
		zoomToRectAction.updateName(commandSetName);
	}	
}