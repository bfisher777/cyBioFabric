package org.systemsbiology.cyBioFabric.internal.icons;

import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;

public class BioFabricViewFactoryPredicate implements TaskFactory{
	
	boolean isBioFabricView;

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

}
