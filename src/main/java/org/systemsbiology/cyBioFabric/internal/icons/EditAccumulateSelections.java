package org.systemsbiology.cyBioFabric.internal.icons;

import java.awt.event.ActionEvent;
import java.util.Map;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.systemsbiology.cyBioFabric.internal.biofabric.cmd.CommandSet;
import org.systemsbiology.cyBioFabric.internal.biofabric.util.ResourceManager;

public class EditAccumulateSelections extends AbstractCyAction implements BioFabricImageIcon {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7908487804892014560L;
	private String COMMAND_SET_NAME;	
	
	public EditAccumulateSelections(Map<String, String> configProps,  CyApplicationManager applicationManager, 
			CyNetworkViewManager networkViewManager, TaskFactoryPredicate taskFactoryPredicate){
	    super(configProps, applicationManager, networkViewManager, taskFactoryPredicate);	    	    
		this.useCheckBoxMenuItem();    
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {	
		
		CommandSet fc = CommandSet.getCmds(COMMAND_SET_NAME);
	    fc.getAction(CommandSet.BUILD_SELECT, false, null).actionPerformed(null);
	    
	    String accumulate = ResourceManager.getManager().getString("command.BuildSelect");
		String doNotAccumulate = ResourceManager.getManager().getString("cytoMenuItemTitle.unAccumulate");
	    String name = this.getName();
	    if(name.equals(accumulate)){
	    	this.setName(doNotAccumulate);
	    }else{
	    	this.setName(accumulate);
	    }
	}
	
	@Override
	public void registerCommandSetName(String commandSetName){
		this.COMMAND_SET_NAME = commandSetName;
	}	
}