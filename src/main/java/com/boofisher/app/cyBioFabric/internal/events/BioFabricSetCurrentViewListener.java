package com.boofisher.app.cyBioFabric.internal.events;

import javax.swing.SwingUtilities;

import org.cytoscape.application.events.SetCurrentNetworkViewEvent;
import org.cytoscape.application.events.SetCurrentNetworkViewListener;
import com.boofisher.app.cyBioFabric.internal.CyBFNetworkViewRenderer;
import com.boofisher.app.cyBioFabric.internal.cytoscape.view.CyBFNetworkView;

public class BioFabricSetCurrentViewListener implements SetCurrentNetworkViewListener{

	BioFabricSetCurrentViewHandler networkSetHandler;
	
	public BioFabricSetCurrentViewListener(BioFabricSetCurrentViewHandler networkCreatedHandler){
		this.networkSetHandler = networkCreatedHandler;
	}
	
	
	@Override
	public void handleEvent(SetCurrentNetworkViewEvent e) {
		if(e.getNetworkView().getRendererId().equals(CyBFNetworkViewRenderer.ID)){
			if(e.getNetworkView() instanceof CyBFNetworkView){
				if(SwingUtilities.isEventDispatchThread()){
					networkSetHandler.handleEvent((CyBFNetworkView)e.getNetworkView());
				}else{
					SwingUtilities.invokeLater(new Runnable(){
	
						@Override
						public void run() {						
							networkSetHandler.handleEvent((CyBFNetworkView)e.getNetworkView());						
						}					
					});
				}
			}
		}
		
	}

}
