package org.systemsbiology.cyBioFabric.internal.icons;

import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.Map;

import javax.swing.ImageIcon;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.systemsbiology.cyBioFabric.internal.biofabric.cmd.CommandSet;

public class ToolBarCenterOnNextAction extends AbstractCyAction implements BioFabricImageIcon{
		
	/**
	 * 
	 */
	private static final long serialVersionUID = -3451030633954192345L;
	private String COMMAND_SET_NAME;	
	
	public ToolBarCenterOnNextAction(Map<String, String> configProps,  CyApplicationManager applicationManager, 
			CyNetworkViewManager networkViewManager, BioFabricViewFactoryPredicate taskFactoryPredicate){
	    super(configProps, applicationManager, networkViewManager, taskFactoryPredicate);	    	    
	    	           
	    URL ugif = getClass().getResource("/images/Forward24.gif");          
	    ImageIcon icon = new ImageIcon(ugif);
	
	    putValue(LARGE_ICON_KEY, icon);	    
	    setToolbarGravity(5);	    
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {		
		CommandSet fc = CommandSet.getCmds(COMMAND_SET_NAME);
	    fc.getAction(CommandSet.CENTER_ON_NEXT_SELECTION, false, null).actionPerformed(null);		
	}
	
	@Override
	public void registerCommandSetName(String commandSetName){
		this.COMMAND_SET_NAME = commandSetName;
	}	
}
