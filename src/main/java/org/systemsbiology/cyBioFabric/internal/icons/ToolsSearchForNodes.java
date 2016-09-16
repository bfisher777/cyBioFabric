package org.systemsbiology.cyBioFabric.internal.icons;

import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.Map;

import javax.swing.ImageIcon;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.systemsbiology.cyBioFabric.internal.biofabric.cmd.CommandSet;
import org.systemsbiology.cyBioFabric.internal.icons.actions.SearchAction;

public class ToolsSearchForNodes extends AbstractCyAction implements BioFabricImageIcon{
		
	/**
	 * 
	 */
	private static final long serialVersionUID = 3114293178481300774L;
	private String commandSetName;	
	private SearchAction searchAction;
	
	public ToolsSearchForNodes(Map<String, String> configProps,  CyApplicationManager applicationManager, 
			CyNetworkViewManager networkViewManager, BioFabricViewFactoryPredicate taskFactoryPredicate){
	    super(configProps, applicationManager, networkViewManager, taskFactoryPredicate);	 
	    searchAction = new SearchAction(taskFactoryPredicate, commandSetName);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {		
		searchAction.fireEvent();		
	}
	
	@Override
	public void registerCommandSetName(String commandSetName){
		this.commandSetName = commandSetName;
		searchAction.updateName(commandSetName);
	}	
}
