package com.boofisher.app.cySimpleRenderer.internal.layouts;

import java.util.Set;

import com.boofisher.app.cySimpleRenderer.internal.layouts.GridLayoutAlgorithmTask;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.layout.AbstractLayoutAlgorithm;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.undo.UndoSupport;

public class GridLayoutAlgorithm extends AbstractLayoutAlgorithm {

	public GridLayoutAlgorithm(UndoSupport undo) {
		super("gridSR", "SR Grid Layout", undo);
	}

	@Override
	public TaskIterator createTaskIterator(CyNetworkView networkView, Object context, Set<View<CyNode>> nodesToLayOut, String layoutAttribute) {
		return new TaskIterator(new GridLayoutAlgorithmTask(getName(), networkView, nodesToLayOut, layoutAttribute, undoSupport));
	}
	
}
