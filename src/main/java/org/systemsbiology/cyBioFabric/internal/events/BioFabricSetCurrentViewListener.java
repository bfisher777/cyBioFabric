package org.systemsbiology.cyBioFabric.internal.events;

import javax.swing.SwingUtilities;

import org.cytoscape.application.events.SetCurrentNetworkViewEvent;
import org.cytoscape.application.events.SetCurrentNetworkViewListener;
import org.systemsbiology.cyBioFabric.internal.CyBFNetworkViewRenderer;
import org.systemsbiology.cyBioFabric.internal.cytoscape.view.CyBFNetworkView;

public class BioFabricSetCurrentViewListener implements SetCurrentNetworkViewListener{

	BioFabricSetCurrentViewHandler networkSetHandler;
	
	public BioFabricSetCurrentViewListener(BioFabricSetCurrentViewHandler networkCreatedHandler){
		this.networkSetHandler = networkCreatedHandler;
	}
	
	
	@Override
	public void handleEvent(SetCurrentNetworkViewEvent e) {

		if(SwingUtilities.isEventDispatchThread()){
			networkSetHandler.handleEvent(e.getNetworkView());
		}else{
			SwingUtilities.invokeLater(new Runnable(){

				@Override
				public void run() {						
					networkSetHandler.handleEvent(e.getNetworkView());						
				}					
			});
		}		
	}

}
