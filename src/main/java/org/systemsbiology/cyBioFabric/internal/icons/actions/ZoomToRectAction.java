package org.systemsbiology.cyBioFabric.internal.icons.actions;

import org.cytoscape.event.CyEventHelper;
import org.cytoscape.view.model.events.UpdateNetworkPresentationEvent;
import org.systemsbiology.cyBioFabric.internal.biofabric.cmd.CommandSet;
import org.systemsbiology.cyBioFabric.internal.icons.BioFabricViewFactoryPredicate;


/*
 * Class used to implement the action for the AbstractCyAction so that
 * multiple AbstractCyActions can use the same action when actionPerformed is fired.
 * */
public class ZoomToRectAction {

	BioFabricViewFactoryPredicate taskFactoryPredicate; 
	CyEventHelper eventHelper;
	String commandSetName;
	
	public ZoomToRectAction(BioFabricViewFactoryPredicate taskFactoryPredicate, 
			CyEventHelper eventHelper, String commandSetName){
		this.eventHelper = eventHelper;
		this.taskFactoryPredicate = taskFactoryPredicate;
		this.commandSetName = commandSetName;
	}
	
	public void fireEvent(){
		CommandSet fc = CommandSet.getCmds(commandSetName);
	    fc.getAction(CommandSet.ZOOM_TO_RECT, false, null).actionPerformed(null);
	    
	    /* 
	     * UpdateNetworkPresentationEvent
	     * If something has been changed in the view model, presentation layer should
         * catch the event and update its visualization. This event will be used in such
         * objects, mainly rendering engines, in the presentation layer. This means by
         * firing this event, Cytoscape will invoke "redraw" method in the rendering
         * engine.*/
	    eventHelper.fireEvent(new UpdateNetworkPresentationEvent(taskFactoryPredicate.getBFNetworkView()));
	    // This is necessary, otherwise, this does not update presentation! 
		eventHelper.flushPayloadEvents();
	}
	
	public void updateName(String commandSetName){
		this.commandSetName = commandSetName;
	}
}
