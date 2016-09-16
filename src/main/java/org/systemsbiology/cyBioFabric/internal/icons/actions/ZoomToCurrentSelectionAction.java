package org.systemsbiology.cyBioFabric.internal.icons.actions;

import org.systemsbiology.cyBioFabric.internal.biofabric.cmd.CommandSet;
import org.systemsbiology.cyBioFabric.internal.icons.BioFabricViewFactoryPredicate;

/*
 * Class used to implement the action for the AbstractCyAction so that
 * multiple AbstractCyActions can use the same action when actionPerformed is fired.
 * */
public class ZoomToCurrentSelectionAction {

	BioFabricViewFactoryPredicate taskFactoryPredicate; 	
	String commandSetName;
	
	public ZoomToCurrentSelectionAction(BioFabricViewFactoryPredicate taskFactoryPredicate, String commandSetName){		
		this.taskFactoryPredicate = taskFactoryPredicate;
		this.commandSetName = commandSetName;
	}
	
	public void fireEvent(){
		CommandSet fc = CommandSet.getCmds(commandSetName);
	    fc.getAction(CommandSet.ZOOM_TO_CURRENT_SELECTION, false, null).actionPerformed(null);	
	}
	
	public void updateName(String commandSetName){
		this.commandSetName = commandSetName;		
	}
}
