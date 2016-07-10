package com.boofisher.app.cySimpleRenderer.internal.layouts;

import java.util.Set;

import com.boofisher.app.cySimpleRenderer.internal.layouts.FlattenLayoutAlgorithmTask;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.layout.AbstractLayoutAlgorithm;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.undo.UndoSupport;

public class FlattenLayoutAlgorithm extends AbstractLayoutAlgorithm {

	public FlattenLayoutAlgorithm(UndoSupport undoSupport) {
		super("flattenSR", "SR Flatten Network", undoSupport);
	}

	@Override
	public TaskIterator createTaskIterator(CyNetworkView networkView, Object layoutContext, Set<View<CyNode>> nodesToLayOut, String layoutAttribute) {
		return new TaskIterator(new FlattenLayoutAlgorithmTask(getName(), networkView, nodesToLayOut, layoutAttribute, undoSupport));
	}

}
