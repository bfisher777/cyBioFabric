package com.boofisher.app.cyBioFabric.internal;

import com.boofisher.app.cyBioFabric.internal.cytoscape.view.CySRNetworkView;
import com.boofisher.app.cyBioFabric.internal.eventbus.EventBusProvider;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.vizmap.VisualMappingManager;

public class CySRNetworkViewFactory implements CyNetworkViewFactory {

	private final VisualLexicon visualLexicon;
	private final VisualMappingManager visualMappingManager;
	private final EventBusProvider eventBusProvider;
	
	public CySRNetworkViewFactory(VisualLexicon visualLexicon, VisualMappingManager visualMappingManager, EventBusProvider eventBusProvider) {
		this.visualLexicon = visualLexicon;
		this.visualMappingManager = visualMappingManager;
		this.eventBusProvider = eventBusProvider;
	}
	
	@Override
	public CyNetworkView createNetworkView(CyNetwork network) {
		return new CySRNetworkView(network, visualLexicon, visualMappingManager, eventBusProvider);
	}

}
