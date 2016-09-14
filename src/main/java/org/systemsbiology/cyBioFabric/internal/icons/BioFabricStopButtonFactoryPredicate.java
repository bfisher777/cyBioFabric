package org.systemsbiology.cyBioFabric.internal.icons;

import org.cytoscape.work.TaskFactory;
import org.systemsbiology.cyBioFabric.internal.cytoscape.view.CyBFNetworkView;

public class BioFabricStopButtonFactoryPredicate extends BioFabricViewFactoryPredicate implements TaskFactory{
	
	CyBFNetworkView networkView;

	public BioFabricStopButtonFactoryPredicate(){}
	
	//TODO this is not working properly, not sure where
	@Override
	public boolean isReady() {		
		boolean ready = false;				
		
		if(networkView != null && isBioFabricView){
			ready = networkView.getBioFabricApplication().getBioFabricWindow().getFabricPanel().getCollectingZoomMode();			
		}
		System.out.println("BioFabricStopButtonFactoryPredicate: isReady called: ready = " + ready);
		return ready;
	}
	
	/**
	 * networkView is registered in the BioFabricSetCurrentViewHandler class
	 * */
	public void registerNetworkView(CyBFNetworkView view){
		networkView = view;
	}
}