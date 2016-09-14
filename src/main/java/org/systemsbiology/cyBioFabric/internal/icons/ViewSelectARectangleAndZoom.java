package org.systemsbiology.cyBioFabric.internal.icons;

import java.awt.event.ActionEvent;
import java.util.Map;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.event.CyEventHelper;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.systemsbiology.cyBioFabric.internal.biofabric.cmd.CommandSet;

public class ViewSelectARectangleAndZoom extends AbstractCyAction implements BioFabricImageIcon{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7715729109562083703L;
	private String COMMAND_SET_NAME;
	private final CyEventHelper eventHelper;
	
	public ViewSelectARectangleAndZoom(Map<String, String> configProps,  CyApplicationManager applicationManager, 
			CyNetworkViewManager networkViewManager, BioFabricViewFactoryPredicate taskFactoryPredicate, CyEventHelper eventHelper){
	    super(configProps, applicationManager, networkViewManager, taskFactoryPredicate);	    	    
	    this.eventHelper = eventHelper;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {		
		CommandSet fc = CommandSet.getCmds(COMMAND_SET_NAME);
	    fc.getAction(CommandSet.ZOOM_TO_RECT, false, null).actionPerformed(null);		
	    
		 // This is necessary, otherwise, this does not update presentation!  This will unhide the stop/cancel button
		//TODO this is not working properly, not sure where
		 eventHelper.fireEvent(null);
	}
	
	@Override
	public void registerCommandSetName(String commandSetName){
		this.COMMAND_SET_NAME = commandSetName;
	}	
}