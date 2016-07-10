package com.boofisher.app.cySimpleRenderer.internal.layouts;

import java.util.Set;

import com.boofisher.app.cySimpleRenderer.internal.layouts.CenterLayoutAlgorithmTask;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.layout.AbstractLayoutAlgorithm;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.undo.UndoSupport;

public class CenterLayoutAlgorithm extends AbstractLayoutAlgorithm {

	public CenterLayoutAlgorithm(UndoSupport undoSupport) {
		super("centerSR", "SR Center Network", undoSupport);
	}

	@Override
	public TaskIterator createTaskIterator(CyNetworkView networkView, Object context, Set<View<CyNode>> nodesToLayOut, String layoutAttribute) {
		return new TaskIterator(new CenterLayoutAlgorithmTask(getName(), networkView, nodesToLayOut, layoutAttribute, undoSupport));
	}

}
