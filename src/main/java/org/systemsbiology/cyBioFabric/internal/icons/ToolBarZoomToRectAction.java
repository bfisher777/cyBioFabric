package org.systemsbiology.cyBioFabric.internal.icons;

import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.Map;

import javax.swing.ImageIcon;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.systemsbiology.cyBioFabric.internal.biofabric.cmd.CommandSet;

/*
 * Called ZoomToRect in biofabric command set class
 * Class will handle the button click event ZoomToRect
 * The command set name is a unique name for each command set running in a BiofabricApplication.class
 * Need to set/change the command set name when the user switches between network views, which is 
 * handled in the BioFabricSetCurrentViewHandler, see cyBioFabric.internal.events package
 * */
public class ToolBarZoomToRectAction extends AbstractCyAction implements BioFabricImageIcon{
    	
	private String COMMAND_SET_NAME;
	private static final long serialVersionUID = -1796000090394162849L;	
	
	public ToolBarZoomToRectAction(Map<String, String> configProps,  CyApplicationManager applicationManager, 
			CyNetworkViewManager networkViewManager, BioFabricViewFactoryPredicate taskFactoryPredicate){
	    super(configProps, applicationManager, networkViewManager, taskFactoryPredicate);	    	    
	    	           
        URL ugif = getClass().getResource("/images/ZoomToFabricRect24.gif");          
	    ImageIcon icon = new ImageIcon(ugif);
	
	    putValue(LARGE_ICON_KEY, icon);	    
	    setToolbarGravity(5);	    
	}

	@Override
	public void actionPerformed(ActionEvent e) {		
		CommandSet fc = CommandSet.getCmds(COMMAND_SET_NAME);
	    fc.getAction(CommandSet.ZOOM_TO_RECT, false, null).actionPerformed(null);		
	}

	@Override
	public void registerCommandSetName(String commandSetName){
		this.COMMAND_SET_NAME = commandSetName;
	}	
}
