package org.systemsbiology.cyBioFabric.internal.icons;

import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.Map;

import javax.swing.ImageIcon;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.systemsbiology.cyBioFabric.internal.biofabric.cmd.CommandSet;
import org.systemsbiology.cyBioFabric.internal.icons.actions.CenterOnPrevSelectionAction;

public class ToolBarCenterOnPreviousAction extends AbstractCyAction implements BioFabricImageIcon{
		
	/**
	 * 
	 */
	private static final long serialVersionUID = -6057238176130426712L;
	private String commandSetName;	
	private CenterOnPrevSelectionAction centerOnPrevSelectionAction;
	
	public ToolBarCenterOnPreviousAction(Map<String, String> configProps,  CyApplicationManager applicationManager, 
			CyNetworkViewManager networkViewManager, BioFabricViewFactoryPredicate taskFactoryPredicate){
	    super(configProps, applicationManager, networkViewManager, taskFactoryPredicate);	    	    
	    	           
	    URL ugif = getClass().getResource("/images/Back24.gif");          
	    ImageIcon icon = new ImageIcon(ugif);
	
	    putValue(LARGE_ICON_KEY, icon);	    
	    setToolbarGravity(5);	
	    
	    centerOnPrevSelectionAction = new CenterOnPrevSelectionAction(taskFactoryPredicate, commandSetName);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {		
		centerOnPrevSelectionAction.fireEvent();	
	}
	
	@Override
	public void registerCommandSetName(String commandSetName){
		this.commandSetName = commandSetName;
		centerOnPrevSelectionAction.updateName(commandSetName);
	}	
}
