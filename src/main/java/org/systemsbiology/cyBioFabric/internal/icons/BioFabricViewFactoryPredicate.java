package org.systemsbiology.cyBioFabric.internal.icons;

import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;
import org.systemsbiology.cyBioFabric.internal.cytoscape.view.CyBFNetworkView;

public class BioFabricViewFactoryPredicate implements TaskFactory{
	
	boolean isBioFabricView;
	CyBFNetworkView networkView;
	
	public BioFabricViewFactoryPredicate(){
		this.networkView = null;
	}

	@Override
	public TaskIterator createTaskIterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isReady() {
		// TODO Auto-generated method stub
		return isBioFabricView;
	}
	
	public void setIsBioFabricView(boolean ready){
		isBioFabricView = ready;
	}
	
	/**
	 * networkView is registered in the BioFabricSetCurrentViewHandler class
	 * */
	public void registerNetwork(CyBFNetworkView networkView){
		this.networkView = networkView;
	}
	
	public CyBFNetworkView getBFNetworkView(){
		return this.networkView;
	}
}
