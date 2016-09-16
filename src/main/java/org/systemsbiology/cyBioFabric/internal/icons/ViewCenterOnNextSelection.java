package org.systemsbiology.cyBioFabric.internal.icons;

import java.awt.event.ActionEvent;
import java.util.Map;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.systemsbiology.cyBioFabric.internal.biofabric.cmd.CommandSet;
import org.systemsbiology.cyBioFabric.internal.icons.actions.CenterOnNextSelectionAction;

public class ViewCenterOnNextSelection extends AbstractCyAction implements BioFabricImageIcon{
		
	/**
	 * 
	 */
	private static final long serialVersionUID = -2739537349897461736L;
	private String commandSetName;
	private CenterOnNextSelectionAction centerOnNextSelectionAction;
	
	public ViewCenterOnNextSelection(Map<String, String> configProps,  CyApplicationManager applicationManager, 
			CyNetworkViewManager networkViewManager, BioFabricViewFactoryPredicate taskFactoryPredicate){
	    super(configProps, applicationManager, networkViewManager, taskFactoryPredicate);	    	    
	    centerOnNextSelectionAction = new CenterOnNextSelectionAction(taskFactoryPredicate, commandSetName);    
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {				
	    centerOnNextSelectionAction.fireEvent();
	}
	
	@Override
	public void registerCommandSetName(String commandSetName){
		this.commandSetName = commandSetName;
		centerOnNextSelectionAction.updateName(commandSetName);
	}	
}
