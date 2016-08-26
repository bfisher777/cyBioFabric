package org.systemsbiology.cyBioFabric.internal.cytoscape.view.listeners;

import org.cytoscape.view.layout.CyLayoutAlgorithm;
import org.cytoscape.view.layout.CyLayoutAlgorithmManager;
import org.cytoscape.work.swing.DialogTaskManager;
import org.systemsbiology.cyBioFabric.internal.cytoscape.view.CyBFNetworkView;

public interface ApplyPreferredLayoutListenerInterface extends BioFabricViewListenerInterface{
	
	void performApplyLayout();

}
