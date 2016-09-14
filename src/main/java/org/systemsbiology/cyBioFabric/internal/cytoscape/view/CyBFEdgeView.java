package org.systemsbiology.cyBioFabric.internal.cytoscape.view;

import org.cytoscape.model.CyEdge;

public class CyBFEdgeView extends CyBFView<CyEdge> {

	private final CyEdge edge;
	
	public CyBFEdgeView(DefaultValueVault defaultValueVault, CyEdge edge) {
		super(defaultValueVault);
		this.edge = edge;
	}
	
	@Override
	public CyEdge getModel() {
		return edge;
	}
	
}
