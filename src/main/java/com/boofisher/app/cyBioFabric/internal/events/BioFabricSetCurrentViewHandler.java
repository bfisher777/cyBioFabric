package com.boofisher.app.cyBioFabric.internal.events;

import com.boofisher.app.cyBioFabric.internal.cytoscape.view.CyBFNetworkView;
import com.boofisher.app.cyBioFabric.internal.graphics.BioFabricCytoPanel;

public class BioFabricSetCurrentViewHandler {	
	
	BioFabricCytoPanel panel;
	
	public BioFabricSetCurrentViewHandler(BioFabricCytoPanel panel){
		this.panel = panel;
	}
	
	/* Handle the shut down event, clear cache, free threads etc.
	** Sets the bioFabricApplication to null
	** @param id */
	public void handleEvent(CyBFNetworkView view){
		panel.setNavAndControl(view.getBioFabricApplication().getBioFabricWindow().getNAC());
	}
	
}
