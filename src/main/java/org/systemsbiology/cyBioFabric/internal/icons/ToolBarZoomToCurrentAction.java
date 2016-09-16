package org.systemsbiology.cyBioFabric.internal.icons;

import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.Map;

import javax.swing.ImageIcon;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.systemsbiology.cyBioFabric.internal.biofabric.cmd.CommandSet;
import org.systemsbiology.cyBioFabric.internal.icons.actions.ZoomToCurrentSelectionAction;

public class ToolBarZoomToCurrentAction extends AbstractCyAction implements BioFabricImageIcon{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1694328005764973934L;
	private String commandSetName;
	private ZoomToCurrentSelectionAction zoomToCurrentSelectionAction;
	
	public ToolBarZoomToCurrentAction(Map<String, String> configProps,  CyApplicationManager applicationManager, 
			CyNetworkViewManager networkViewManager, BioFabricViewFactoryPredicate taskFactoryPredicate){
	    super(configProps, applicationManager, networkViewManager, taskFactoryPredicate);	    	    
	    	           
	    URL ugif = getClass().getResource("/images/ZoomToFabricSelected24.gif");          
	    ImageIcon icon = new ImageIcon(ugif);
	
	    putValue(LARGE_ICON_KEY, icon);	    
	    setToolbarGravity(5);
	    
	    zoomToCurrentSelectionAction = new ZoomToCurrentSelectionAction(taskFactoryPredicate, commandSetName);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {		
		zoomToCurrentSelectionAction.fireEvent();		
	}
	
	@Override
	public void registerCommandSetName(String commandSetName){
		this.commandSetName = commandSetName;
		zoomToCurrentSelectionAction.updateName(commandSetName);
	}	
}
