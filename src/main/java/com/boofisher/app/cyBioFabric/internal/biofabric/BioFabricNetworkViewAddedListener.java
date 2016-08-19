package com.boofisher.app.cyBioFabric.internal.biofabric;

import javax.swing.SwingUtilities;

import org.cytoscape.view.model.events.NetworkViewAddedEvent;
import org.cytoscape.view.model.events.NetworkViewAddedListener;

import com.boofisher.app.cyBioFabric.internal.CyBFNetworkViewRenderer;


/**
 * 
 * Use this class to listen for network added events.  Can then import styles?
 * 
 * */
public class BioFabricNetworkViewAddedListener  implements NetworkViewAddedListener{

	BioFabricNetworkViewAddedHandler networkCreatedHandler;
	
	public BioFabricNetworkViewAddedListener(BioFabricNetworkViewAddedHandler networkCreatedHandler){
		this.networkCreatedHandler = networkCreatedHandler;
	}
	
	@Override
	public void handleEvent(NetworkViewAddedEvent e) {		
		if(e.getNetworkView().getRendererId().equals(CyBFNetworkViewRenderer.ID)){
			if(SwingUtilities.isEventDispatchThread()){
				//do something
			}else{
				SwingUtilities.invokeLater(new Runnable(){

					@Override
					public void run() {						
						//do something						
					}					
				});
			}
		}
		
	}

}
