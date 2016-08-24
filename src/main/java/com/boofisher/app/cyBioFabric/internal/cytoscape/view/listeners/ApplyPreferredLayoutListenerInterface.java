package com.boofisher.app.cyBioFabric.internal.cytoscape.view.listeners;

import org.cytoscape.view.layout.CyLayoutAlgorithm;
import org.cytoscape.view.layout.CyLayoutAlgorithmManager;
import org.cytoscape.work.swing.DialogTaskManager;

import com.boofisher.app.cyBioFabric.internal.cytoscape.view.CyBFNetworkView;

public interface ApplyPreferredLayoutListenerInterface extends BioFabricViewListenerInterface{
	
	void performApplyLayout();

}
