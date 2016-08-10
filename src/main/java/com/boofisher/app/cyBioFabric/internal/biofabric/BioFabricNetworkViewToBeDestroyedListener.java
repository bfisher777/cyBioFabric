package com.boofisher.app.cyBioFabric.internal.biofabric;

import org.cytoscape.view.model.events.NetworkViewAboutToBeDestroyedEvent;
import org.cytoscape.view.model.events.NetworkViewAboutToBeDestroyedListener;

import com.boofisher.app.cyBioFabric.internal.CyBFNetworkViewRenderer;


public class BioFabricNetworkViewToBeDestroyedListener implements NetworkViewAboutToBeDestroyedListener{
	
	NetworkViewDestroyedHandler networkDestroyedHandler;
	
	public BioFabricNetworkViewToBeDestroyedListener(NetworkViewDestroyedHandler networkDestroyedHandler){
		this.networkDestroyedHandler = networkDestroyedHandler;
	}
	
	@Override
	public void handleEvent(NetworkViewAboutToBeDestroyedEvent e) {
		
		if(e.getNetworkView().getRendererId().equals(CyBFNetworkViewRenderer.ID)){			
			networkDestroyedHandler.handleEvent(e.getNetworkView().getSUID());
		}
		
	}



}
