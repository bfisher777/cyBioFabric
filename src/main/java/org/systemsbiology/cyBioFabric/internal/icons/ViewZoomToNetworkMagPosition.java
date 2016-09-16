package org.systemsbiology.cyBioFabric.internal.icons;

import java.awt.event.ActionEvent;
import java.util.Map;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.systemsbiology.cyBioFabric.internal.biofabric.cmd.CommandSet;
import org.systemsbiology.cyBioFabric.internal.icons.actions.ZoomToMagnifierAction;

public class ViewZoomToNetworkMagPosition extends AbstractCyAction implements BioFabricImageIcon{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6620081450114742745L;
	private String commandSetName;	
	private ZoomToMagnifierAction zoomToMagnifierAction;
	
	public ViewZoomToNetworkMagPosition(Map<String, String> configProps,  CyApplicationManager applicationManager, 
			CyNetworkViewManager networkViewManager, BioFabricViewFactoryPredicate taskFactoryPredicate){
	    super(configProps, applicationManager, networkViewManager, taskFactoryPredicate);	    	    
		zoomToMagnifierAction = new ZoomToMagnifierAction(taskFactoryPredicate, commandSetName);    
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {		
		zoomToMagnifierAction.fireEvent();		
	}
	
	@Override
	public void registerCommandSetName(String commandSetName){
		this.commandSetName = commandSetName;
		zoomToMagnifierAction.updateName(commandSetName);
	}	
}
