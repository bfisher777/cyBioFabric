package org.systemsbiology.cyBioFabric.internal.icons;

import org.cytoscape.work.TaskFactory;
import org.systemsbiology.cyBioFabric.internal.cytoscape.view.CyBFNetworkView;

public class BioFabricStopButtonFactoryPredicate extends BioFabricViewFactoryPredicate implements TaskFactory{	

	public BioFabricStopButtonFactoryPredicate(){
		super();
	}
	
	//TODO this is not working properly, not sure where
	@Override
	public boolean isReady() {		
		boolean ready = false;				
		
		if(networkView != null && isBioFabricView){
			ready = networkView.getBioFabricApplication().getBioFabricWindow().getFabricPanel().getCollectingZoomMode();			
		}
		
		return ready;
	}
}