package org.systemsbiology.cyBioFabric.internal.icons;

import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.Map;

import javax.swing.ImageIcon;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.systemsbiology.cyBioFabric.internal.biofabric.cmd.CommandSet;
import org.systemsbiology.cyBioFabric.internal.icons.actions.CenterOnNextSelectionAction;

public class ToolBarCenterOnNextAction extends AbstractCyAction implements BioFabricImageIcon{
		
	/**
	 * 
	 */
	private static final long serialVersionUID = -3451030633954192345L;
	private String commandSetName;
	private CenterOnNextSelectionAction centerOnNextSelectionAction;
	
	public ToolBarCenterOnNextAction(Map<String, String> configProps,  CyApplicationManager applicationManager, 
			CyNetworkViewManager networkViewManager, BioFabricViewFactoryPredicate taskFactoryPredicate){
	    super(configProps, applicationManager, networkViewManager, taskFactoryPredicate);	    	    
	    	           
	    URL ugif = getClass().getResource("/images/Forward24.gif");          
	    ImageIcon icon = new ImageIcon(ugif);
	
	    putValue(LARGE_ICON_KEY, icon);	    
	    setToolbarGravity(5);
	    
	    centerOnNextSelectionAction = new CenterOnNextSelectionAction(taskFactoryPredicate, commandSetName);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {		
		centerOnNextSelectionAction.fireEvent();		
	}
	
	@Override
	public void registerCommandSetName(String commandSetName){
		this.commandSetName = commandSetName;
		centerOnNextSelectionAction.updateName(commandSetName);
	}	
}
