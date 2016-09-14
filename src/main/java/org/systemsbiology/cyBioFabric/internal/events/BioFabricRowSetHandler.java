package org.systemsbiology.cyBioFabric.internal.events;

import java.util.Collection;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewManager;

public class BioFabricRowSetHandler {		
	CyApplicationManager am;
	CyNetworkViewManager vm;

	public BioFabricRowSetHandler(CyApplicationManager am, CyNetworkViewManager vm){
		 this.am = am;	
		 this.vm = vm;
	}
	
	public void handleEvent(){
		CyNetwork network = am.getCurrentNetwork();
		final Collection<CyNetworkView> views = vm.getNetworkViews(network);
		CyNetworkView networkView = null;
		
		if (views.isEmpty()){
			return;
		}else{
			networkView = views.iterator().next();
			networkView.updateView();
		}
	}

}
