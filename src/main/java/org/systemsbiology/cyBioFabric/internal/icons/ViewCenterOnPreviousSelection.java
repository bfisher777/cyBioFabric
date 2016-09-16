package org.systemsbiology.cyBioFabric.internal.icons;

import java.awt.event.ActionEvent;
import java.util.Map;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.systemsbiology.cyBioFabric.internal.biofabric.cmd.CommandSet;
import org.systemsbiology.cyBioFabric.internal.icons.actions.CenterOnPrevSelectionAction;

public class ViewCenterOnPreviousSelection extends AbstractCyAction implements BioFabricImageIcon{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -299479373562204455L;
	private String commandSetName;
	private CenterOnPrevSelectionAction centerOnPrevSelectionAction;
	
	public ViewCenterOnPreviousSelection(Map<String, String> configProps,  CyApplicationManager applicationManager, 
			CyNetworkViewManager networkViewManager, BioFabricViewFactoryPredicate taskFactoryPredicate){
	    super(configProps, applicationManager, networkViewManager, taskFactoryPredicate);	    	    
	    centerOnPrevSelectionAction = new CenterOnPrevSelectionAction(taskFactoryPredicate, commandSetName);    
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {		
		centerOnPrevSelectionAction.fireEvent();		
	}
	
	@Override
	public void registerCommandSetName(String commandSetName){
		this.commandSetName = commandSetName;
		centerOnPrevSelectionAction.updateName(commandSetName);
	}	
}