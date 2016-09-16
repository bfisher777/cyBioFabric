package org.systemsbiology.cyBioFabric.internal.icons.actions;

import org.systemsbiology.cyBioFabric.internal.biofabric.cmd.CommandSet;
import org.systemsbiology.cyBioFabric.internal.icons.BioFabricViewFactoryPredicate;

public class SearchAction {

	BioFabricViewFactoryPredicate taskFactoryPredicate; 	
	String commandSetName;
	
	public SearchAction(BioFabricViewFactoryPredicate taskFactoryPredicate, String commandSetName){		
		this.taskFactoryPredicate = taskFactoryPredicate;
		this.commandSetName = commandSetName;
	}
	
	public void fireEvent(){
		CommandSet fc = CommandSet.getCmds(commandSetName);
	    fc.getAction(CommandSet.SEARCH, false, null).actionPerformed(null);		
	}
	
	public void updateName(String commandSetName){
		this.commandSetName = commandSetName;		
	}
}
