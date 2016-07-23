package com.boofisher.app.cyBioFabric.internal.cytoscape.view;

import org.cytoscape.model.CyEdge;

public class CySREdgeView extends CySRView<CyEdge> {

	private final CyEdge edge;
	
	public CySREdgeView(DefaultValueVault defaultValueVault, CyEdge edge) {
		super(defaultValueVault);
		this.edge = edge;
	}
	
	@Override
	public CyEdge getModel() {
		return edge;
	}
	
}
