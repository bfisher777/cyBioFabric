package com.boofisher.app.cyBioFabric.internal.biofabric;

import org.cytoscape.view.model.events.NetworkViewAddedEvent;
import org.cytoscape.view.model.events.NetworkViewAddedListener;

import com.boofisher.app.cyBioFabric.internal.CyBFNetworkViewRenderer;

public class BioFabricNetworkViewAddedListener  implements NetworkViewAddedListener{

	NetworkViewAddedHandler networkCreatedHandler;
	
	public BioFabricNetworkViewAddedListener(NetworkViewAddedHandler networkCreatedHandler){
		this.networkCreatedHandler = networkCreatedHandler;
	}
	
	@Override
	public void handleEvent(NetworkViewAddedEvent e) {		
		if(e.getNetworkView().getRendererId().equals(CyBFNetworkViewRenderer.ID)){
			networkCreatedHandler.handleEvent(e.getNetworkView().getSUID());
		}
		
	}

}
