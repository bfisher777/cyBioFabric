package org.systemsbiology.cyBioFabric.internal.icons;

import java.awt.event.ActionEvent;
import java.util.Map;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.systemsbiology.cyBioFabric.internal.biofabric.cmd.CommandSet;

public class ImportWithEdgeWeights extends AbstractCyAction implements BioFabricImageIcon{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -675760201185630864L;
	private String COMMAND_SET_NAME;	
	
	public ImportWithEdgeWeights(Map<String, String> configProps,  CyApplicationManager applicationManager, 
			CyNetworkViewManager networkViewManager, BioFabricViewFactoryPredicate taskFactoryPredicate){
	    super(configProps, applicationManager, networkViewManager, taskFactoryPredicate);	    	    
		    
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {		
		CommandSet fc = CommandSet.getCmds(COMMAND_SET_NAME);
	    fc.getAction(CommandSet.LOAD_WITH_EDGE_WEIGHTS, false, null).actionPerformed(null);		
	}
	
	@Override
	public void registerCommandSetName(String commandSetName){
		this.COMMAND_SET_NAME = commandSetName;
	}	
}