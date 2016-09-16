package org.systemsbiology.cyBioFabric.internal;

import org.cytoscape.event.CyEventHelper;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.view.layout.CyLayoutAlgorithm;
import org.cytoscape.view.layout.CyLayoutAlgorithmManager;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.systemsbiology.cyBioFabric.internal.cytoscape.view.CyBFNetworkView;
import org.systemsbiology.cyBioFabric.internal.layouts.BioFabricLayoutInterface;

public class CyBFNetworkViewFactory implements CyNetworkViewFactory {

	private final VisualLexicon visualLexicon;
	private final VisualMappingManager visualMappingManager;	
	private BioFabricLayoutInterface bfLayoutAlg;
	private CyLayoutAlgorithmManager layoutAlgorithmManager;
	private int applicationNumber;
	private CyEventHelper cyEventHelper;
	
	public CyBFNetworkViewFactory(VisualLexicon visualLexicon, VisualMappingManager visualMappingManager, 
			CyLayoutAlgorithmManager layoutAlgorithmManager, BioFabricLayoutInterface bfLayoutAlg, 
			CyEventHelper cyEventHelper) {
		this.visualLexicon = visualLexicon;
		this.visualMappingManager = visualMappingManager;	
		this.bfLayoutAlg = bfLayoutAlg;
		this.layoutAlgorithmManager = layoutAlgorithmManager;
		this.cyEventHelper = cyEventHelper;
	}
	
	@Override
	public CyNetworkView createNetworkView(CyNetwork network) {
		
		CyLayoutAlgorithm layout = layoutAlgorithmManager.getLayout(bfLayoutAlg.getName());
		layoutAlgorithmManager.setDefaultLayout(layout);
		return new CyBFNetworkView(network, visualLexicon, visualMappingManager, applicationNumber++, cyEventHelper);
	}

}
