package com.boofisher.app.cyBioFabric.internal.input.handler.commands;

import java.util.List;

import com.boofisher.app.cyBioFabric.internal.data.GraphicsData;
import com.boofisher.app.cyBioFabric.internal.input.handler.MouseCommand;
import com.boofisher.app.cyBioFabric.internal.input.handler.MouseCommandAdapter;
import com.boofisher.app.cyBioFabric.internal.input.handler.commands.SelectionAddMouseCommand;
import com.boofisher.app.cyBioFabric.internal.tools.NetworkToolkit;

import org.apache.log4j.Logger;
import org.cytoscape.application.CyUserLog;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTableUtil;
import org.cytoscape.view.model.CyNetworkView;

/**
 * Deselects currently selected nodes and edges before selecting new ones.
 * 
 * @author mkucera
 */
public class SelectionMouseCommand extends MouseCommandAdapter {
	final Logger logger = Logger.getLogger(CyUserLog.NAME);
	private final GraphicsData graphicsData;
	private final SelectionAddMouseCommand addCommand;
	
	public SelectionMouseCommand(GraphicsData graphicsData) {
		this.graphicsData = graphicsData;
		this.addCommand = new SelectionAddMouseCommand(graphicsData);
	}

	@Override
	public void clicked(int x, int y) {
		logger.warn("Selection mouse command clicked event handled");
		deselectOther();
		addCommand.clicked(x, y);
	}
	
	@Override
	public void dragStart(int x, int y) {
		deselectOther();
		addCommand.dragStart(x, y);
	}
	
	private void deselectOther() {
		CyNetworkView networkView = graphicsData.getNetworkView();
		
		// Deselect currently selected nodes
		List<CyNode> selectedNodes = CyTableUtil.getNodesInState(networkView.getModel(), "selected", true);
		for (CyNode node : selectedNodes) {
			NetworkToolkit.setNodeSelected(node.getSUID(), networkView, false);
		}

		// Deselect currently selected edges
		List<CyEdge> selectedEdges = CyTableUtil.getEdgesInState(networkView.getModel(), "selected", true);
		for (CyEdge edge : selectedEdges) {
			NetworkToolkit.setEdgeSelected(edge.getSUID(), networkView, false);
		}
	}
	

	@Override
	public void dragMove(int x, int y) {
		addCommand.dragMove(x, y);
	}

	@Override
	public void dragEnd(int x, int y) {
		addCommand.dragEnd(x, y);
	}
	
	@Override
	public void moved(int x, int y) {
		addCommand.moved(x, y);
	}
	
	@Override
	public void exited() {
		addCommand.exited();
	}
	
	@Override
	public MouseCommand modify() {
		return addCommand;
	}

}

