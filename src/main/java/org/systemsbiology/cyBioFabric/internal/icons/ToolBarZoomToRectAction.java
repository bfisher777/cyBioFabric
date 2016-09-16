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
import org.systemsbiology.cyBioFabric.internal.icons.actions.ZoomToRectAction;

/*
 * Called ZoomToRect in biofabric command set class
 * Class will handle the button click event ZoomToRect
 * The command set name is a unique name for each command set running in a BiofabricApplication.class
 * Need to set/change the command set name when the user switches between network views, which is 
 * handled in the BioFabricSetCurrentViewHandler, see cyBioFabric.internal.events package
 * */
public class ToolBarZoomToRectAction extends AbstractCyAction implements BioFabricImageIcon{
    	
	private String commandSetName;
	private static final long serialVersionUID = -1796000090394162849L;

	ZoomToRectAction zoomToRectAction;
	
	public ToolBarZoomToRectAction(Map<String, String> configProps,  CyApplicationManager applicationManager, 
			CyNetworkViewManager networkViewManager, BioFabricViewFactoryPredicate taskFactoryPredicate, 
			CyEventHelper eventHelper){
	    super(configProps, applicationManager, networkViewManager, taskFactoryPredicate);	    	    

        URL ugif = getClass().getResource("/images/ZoomToFabricRect24.gif");          
	    ImageIcon icon = new ImageIcon(ugif);
	
	    putValue(LARGE_ICON_KEY, icon);	    
	    setToolbarGravity(5);
	    
	    this.zoomToRectAction = new ZoomToRectAction(taskFactoryPredicate, eventHelper, commandSetName);
	}

	@Override
	public void actionPerformed(ActionEvent e) {		
		zoomToRectAction.fireEvent();
	}

	@Override
	public void registerCommandSetName(String commandSetName){
		this.commandSetName = commandSetName;
		zoomToRectAction.updateName(commandSetName);
	}		
}
