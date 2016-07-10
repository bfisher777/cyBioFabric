package com.boofisher.app.cySimpleRenderer.internal.layouts;

import java.util.Set;

import com.boofisher.app.cySimpleRenderer.internal.layouts.SphericalLayoutAlgorithmTask;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.layout.AbstractLayoutAlgorithm;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.undo.UndoSupport;

public class SphericalLayoutAlgorithm extends AbstractLayoutAlgorithm {

	public SphericalLayoutAlgorithm(UndoSupport undo) {
		super("spherical", "SR Spherical Layout", undo);
	}

	@Override
	public TaskIterator createTaskIterator(CyNetworkView networkView, Object context, Set<View<CyNode>> nodesToLayOut, String layoutAttribute) {
		return new TaskIterator(new SphericalLayoutAlgorithmTask(getName(), networkView, nodesToLayOut, layoutAttribute, undoSupport));
	}
	
}
