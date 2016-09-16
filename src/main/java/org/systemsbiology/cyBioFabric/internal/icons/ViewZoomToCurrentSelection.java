package org.systemsbiology.cyBioFabric.internal.icons;

import java.awt.event.ActionEvent;
import java.util.Map;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.systemsbiology.cyBioFabric.internal.biofabric.cmd.CommandSet;
import org.systemsbiology.cyBioFabric.internal.icons.actions.ZoomToCurrentSelectionAction;

public class ViewZoomToCurrentSelection extends AbstractCyAction implements BioFabricImageIcon{
		
	/**
	 * 
	 */
	private static final long serialVersionUID = 3492288790169342737L;
	private String commandSetName;	
	private ZoomToCurrentSelectionAction zoomToCurrentSelectionAction;
	
	public ViewZoomToCurrentSelection(Map<String, String> configProps,  CyApplicationManager applicationManager, 
			CyNetworkViewManager networkViewManager, BioFabricViewFactoryPredicate taskFactoryPredicate){
	    super(configProps, applicationManager, networkViewManager, taskFactoryPredicate);	    	    
		
	    zoomToCurrentSelectionAction = new ZoomToCurrentSelectionAction(taskFactoryPredicate, commandSetName);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {		
		zoomToCurrentSelectionAction.fireEvent();		
	}
	
	@Override
	public void registerCommandSetName(String commandSetName){
		this.commandSetName = commandSetName;
		zoomToCurrentSelectionAction.updateName(commandSetName);
	}	
}