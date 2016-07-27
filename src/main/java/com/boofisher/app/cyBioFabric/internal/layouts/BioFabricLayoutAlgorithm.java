package com.boofisher.app.cyBioFabric.internal.layouts;

import java.util.Set;

import org.cytoscape.event.CyEventHelper;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.layout.AbstractLayoutAlgorithm;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.undo.UndoSupport;

public class BioFabricLayoutAlgorithm extends AbstractLayoutAlgorithm {
	
	public BioFabricLayoutAlgorithm(UndoSupport undo) {
		super("biofabric", "BioFabric Layout", undo);	
	}


	@Override
	public TaskIterator createTaskIterator(CyNetworkView networkView, Object context, Set<View<CyNode>> nodesToLayOut, String layoutAttribute) {
		return new TaskIterator(new BioFabricLayoutAlgorithmTask(getName(), networkView, nodesToLayOut, layoutAttribute, undoSupport));
	}
}

