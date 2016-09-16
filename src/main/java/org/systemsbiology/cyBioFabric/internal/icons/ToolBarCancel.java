package org.systemsbiology.cyBioFabric.internal.icons;

import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.Map;

import javax.swing.ImageIcon;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.event.CyEventHelper;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.model.events.UpdateNetworkPresentationEvent;
import org.systemsbiology.cyBioFabric.internal.biofabric.cmd.CommandSet;

public class ToolBarCancel extends AbstractCyAction implements BioFabricImageIcon{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2993980316739838835L;
	private String COMMAND_SET_NAME;		
	private CyEventHelper eventHelper;
	private BioFabricViewFactoryPredicate taskFactoryPredicate;
	
	public ToolBarCancel(Map<String, String> configProps,  CyApplicationManager applicationManager, 
			CyNetworkViewManager networkViewManager, BioFabricViewFactoryPredicate taskFactoryPredicate, 
			CyEventHelper eventHelper){
	    super(configProps, applicationManager, networkViewManager, taskFactoryPredicate);	    	    
	    
	    this.taskFactoryPredicate = taskFactoryPredicate;
	    this.eventHelper = eventHelper;
	    URL ugif = getClass().getResource("/images/Stop24.gif");          
	    ImageIcon icon = new ImageIcon(ugif);
	
	    putValue(LARGE_ICON_KEY, icon);	    
	    setToolbarGravity(5);	    
	}
	
	//TODO make a action class to fire the event
	@Override
	public void actionPerformed(ActionEvent e) {		
		CommandSet fc = CommandSet.getCmds(COMMAND_SET_NAME);
	    fc.getAction(CommandSet.CANCEL, false, null).actionPerformed(null);	
	    

	    /* If something has been changed in the view model, presentation layer should
         * catch the event and update its visualization. This event will be used in such
         * objects, mainly rendering engines, in the presentation layer. This means by
         * firing this event, Cytoscape will invoke "redraw" method in the rendering
         * engine.*/
	    eventHelper.fireEvent(new UpdateNetworkPresentationEvent(taskFactoryPredicate.getBFNetworkView()));
	    // This is necessary, otherwise, this does not update presentation!  
		eventHelper.flushPayloadEvents();
	}
	
	@Override
	public void registerCommandSetName(String commandSetName){
		this.COMMAND_SET_NAME = commandSetName;
	}	
	
}
