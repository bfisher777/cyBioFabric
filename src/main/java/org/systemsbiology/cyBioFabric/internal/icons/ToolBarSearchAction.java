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

public class ToolBarSearchAction extends AbstractCyAction implements BioFabricImageIcon{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5485787271891602701L;
	private String commandSetName;
	private SearchAction searchAction;
	
	public ToolBarSearchAction(Map<String, String> configProps,  CyApplicationManager applicationManager, 
			CyNetworkViewManager networkViewManager, BioFabricViewFactoryPredicate taskFactoryPredicate){
	    super(configProps, applicationManager, networkViewManager, taskFactoryPredicate);
	    
        
		 URL ugif = getClass().getResource("/images/Find24.gif");          
		 ImageIcon icon = new ImageIcon(ugif);
		
		 putValue(LARGE_ICON_KEY, icon);	    
		 setToolbarGravity(5);
		 
		 searchAction = new SearchAction(taskFactoryPredicate, commandSetName);
	}
	
	//TODO make a action class to fire the event
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
