package com.boofisher.app.cyBioFabric.internal.layouts;

import java.util.Set;

import org.cytoscape.event.CyEventHelper;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.layout.AbstractLayoutAlgorithm;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.undo.UndoSupport;

//**note AbstractLayoutAlgorithm implements the getName method for the interface
public class DefaultBioFabricLayoutAlgorithm extends AbstractLayoutAlgorithm implements BioFabricLayoutInterface{
	
	public DefaultBioFabricLayoutAlgorithm(UndoSupport undo) {
		super("default-biofabric-layout", "Default BioFabric Layout", undo);	
	}

	@Override
	public TaskIterator createTaskIterator(CyNetworkView networkView, Object context, Set<View<CyNode>> nodesToLayOut, String layoutAttribute) {
		return new TaskIterator(new DefaultBioFabricLayoutAlgorithmTask(toString(), networkView, nodesToLayOut, layoutAttribute, undoSupport));
	}		
}

