package com.boofisher.app.cySimpleRenderer.internal.layouts;

import java.util.Set;

import com.boofisher.app.cySimpleRenderer.internal.layouts.BoxLayoutAlgorithmTask;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.layout.AbstractLayoutAlgorithm;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.undo.UndoSupport;

public class BoxLayoutAlgorithm extends AbstractLayoutAlgorithm {

	public BoxLayoutAlgorithm(UndoSupport undo) {
		super("boxSR", "SR Box Layout", undo);
	}


	@Override
	public TaskIterator createTaskIterator(CyNetworkView networkView, Object context, Set<View<CyNode>> nodesToLayOut, String layoutAttribute) {
		return new TaskIterator(new BoxLayoutAlgorithmTask(getName(), networkView, nodesToLayOut, layoutAttribute, undoSupport));
	}
}

