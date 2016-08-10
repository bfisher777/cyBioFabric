package com.boofisher.app.cyBioFabric.internal;

import com.boofisher.app.cyBioFabric.internal.cytoscape.view.CyBFNetworkView;
import com.boofisher.app.cyBioFabric.internal.eventbus.EventBusProvider;
import com.boofisher.app.cyBioFabric.internal.layouts.BioFabricLayoutInterface;
import com.boofisher.app.cyBioFabric.internal.layouts.DefaultBioFabricLayoutAlgorithm;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.view.layout.CyLayoutAlgorithm;
import org.cytoscape.view.layout.CyLayoutAlgorithmManager;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.vizmap.VisualMappingManager;

public class CyBFNetworkViewFactory implements CyNetworkViewFactory {

	private final VisualLexicon visualLexicon;
	private final VisualMappingManager visualMappingManager;
	private final EventBusProvider eventBusProvider;
	BioFabricLayoutInterface bfLayoutAlg;
	CyLayoutAlgorithmManager layoutAlgorithmManager;
	
	public CyBFNetworkViewFactory(VisualLexicon visualLexicon, VisualMappingManager visualMappingManager, 
			EventBusProvider eventBusProvider, CyLayoutAlgorithmManager layoutAlgorithmManager, 
			BioFabricLayoutInterface bfLayoutAlg) {
		this.visualLexicon = visualLexicon;
		this.visualMappingManager = visualMappingManager;
		this.eventBusProvider = eventBusProvider;	
		this.bfLayoutAlg = bfLayoutAlg;
		this.layoutAlgorithmManager = layoutAlgorithmManager;
	}
	
	@Override
	public CyNetworkView createNetworkView(CyNetwork network) {
		
		CyLayoutAlgorithm layout = layoutAlgorithmManager.getLayout(bfLayoutAlg.getName());
		layoutAlgorithmManager.setDefaultLayout(layout);
		return new CyBFNetworkView(network, visualLexicon, visualMappingManager, eventBusProvider);
	}

}
