package org.systemsbiology.cyBioFabric.internal.icons;

import java.awt.event.ActionEvent;
import java.util.Map;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.systemsbiology.cyBioFabric.internal.biofabric.cmd.CommandSet;
import org.systemsbiology.cyBioFabric.internal.icons.actions.ZoomToMousePositionAction;

public class ViewZoomToMousePosition extends AbstractCyAction implements BioFabricImageIcon{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3607653691681519032L;
	private String commandSetName;	
	private ZoomToMousePositionAction zoomToMousePositionAction;
	
	public ViewZoomToMousePosition(Map<String, String> configProps,  CyApplicationManager applicationManager, 
			CyNetworkViewManager networkViewManager, BioFabricViewFactoryPredicate taskFactoryPredicate){
	    super(configProps, applicationManager, networkViewManager, taskFactoryPredicate);
	    
	    zoomToMousePositionAction = new ZoomToMousePositionAction(taskFactoryPredicate, commandSetName);		    
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {		
		zoomToMousePositionAction.fireEvent();		
	}
	
	@Override
	public void registerCommandSetName(String commandSetName){
		this.commandSetName = commandSetName;
		zoomToMousePositionAction.updateName(commandSetName);
	}	
}