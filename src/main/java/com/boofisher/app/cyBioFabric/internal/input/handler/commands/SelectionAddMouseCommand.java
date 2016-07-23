package com.boofisher.app.cyBioFabric.internal.input.handler.commands;

import com.boofisher.app.cyBioFabric.internal.data.GraphicsData;
import com.boofisher.app.cyBioFabric.internal.data.GraphicsSelectionData;
import com.boofisher.app.cyBioFabric.internal.data.PickingData;
import com.boofisher.app.cyBioFabric.internal.input.handler.MouseCommandAdapter;
import com.boofisher.app.cyBioFabric.internal.tools.NetworkToolkit;
import org.apache.log4j.Logger;
import org.cytoscape.application.CyUserLog;
import org.cytoscape.view.model.CyNetworkView;

public class SelectionAddMouseCommand extends MouseCommandAdapter {

	final Logger logger = Logger.getLogger(CyUserLog.NAME);
	/** A constant that stands for "no type is here" */
	public static final int NO_TYPE = -1;

	/** A constant representing the type node */
	public static final int NODE_TYPE = 0;

	/** A constant representing the type edge */
	public static final int EDGE_TYPE = 1;

	/** A constant that stands for "no index is here" */
	// TODO: NO_INDEX relies on cytoscape's guarantee that node and edge indices
	// are nonnegative
	public static final int NO_INDEX = -1; // Value representing that no node
											// or edge index is being held 
	
	
	private final GraphicsData graphicsData;
	private final GraphicsSelectionData selectionData;
	
	
	public SelectionAddMouseCommand(GraphicsData graphicsData) {
		this.graphicsData = graphicsData;
		this.selectionData = graphicsData.getSelectionData();
	}
	
	
	@Override
	public void clicked(int x, int y) {
		CyNetworkView networkView = graphicsData.getNetworkView();		
		//TODO: Default shape picking processor sets closest picked node in Cy3D, need to rework for simple render
		//Normalize x, y to account for zoom, screen position, create real world point
		//Iterate through shapes to see if shape contains point.
		//Select first hit
		
		
		long newHoverNodeIndex = graphicsData.getPickingData().getClosestPickedNodeIndex();
		long newHoverEdgeIndex = graphicsData.getPickingData().getClosestPickedEdgeIndex();
		
		selectionData.setHoverNodeIndex(newHoverNodeIndex);
		selectionData.setHoverEdgeIndex(newHoverEdgeIndex);
		
		if (!selectionData.isDragSelectMode()) {
			if (newHoverNodeIndex != NO_INDEX) {
				if (NetworkToolkit.checkNodeSelected(newHoverNodeIndex, networkView)) {
					// Deselect the node if it was already selected
					NetworkToolkit.setNodeSelected(newHoverNodeIndex, networkView, false);
				} else {
					// Select the node if it was not selected
					NetworkToolkit.setNodeSelected(newHoverNodeIndex, networkView, true);
				}
				
			} else if (newHoverEdgeIndex != NO_INDEX) {
				if (NetworkToolkit.checkEdgeSelected(newHoverEdgeIndex, networkView)) {
					// Deselect the edge if it was already selected
					NetworkToolkit.setEdgeSelected(newHoverEdgeIndex, networkView, false);
				} else {				
					// Select the edge if it was not selected
					NetworkToolkit.setEdgeSelected(newHoverEdgeIndex, networkView, true);
				}
			}
		}
	}
	
	
	// Drag movement
	
	@Override
	public void dragStart(int x, int y) {
		selectionData.setSelectTopLeftX(x);
		selectionData.setSelectTopLeftY(y);
		selectionData.setSelectTopLeftFound(true);
	}

	@Override
	public void dragMove(int x, int y) {
		selectionData.setSelectBottomRightX(x);
		selectionData.setSelectBottomRightY(y);
		
		if (Math.abs(selectionData.getSelectTopLeftX() - x) >= 1 && Math.abs(selectionData.getSelectTopLeftY() - y) >= 1) {
			selectionData.setDragSelectMode(true);
		}
	}

	@Override
	public void dragEnd(int x, int y) {
		CyNetworkView networkView = graphicsData.getNetworkView();
		PickingData pickingData = graphicsData.getPickingData();
		
		selectionData.setDragSelectMode(false);
		selectionData.setSelectTopLeftFound(false);
		
		for (long index : pickingData.getPickedNodeIndices()) {
			NetworkToolkit.setNodeSelected(index, networkView, true);
		}
		for (long index : pickingData.getPickedEdgeIndices()) {
			NetworkToolkit.setEdgeSelected(index, networkView, true);
		}
		
		pickingData.getPickedNodeIndices().clear();
		pickingData.getPickedEdgeIndices().clear();
	}
	
	
	@Override
	public void moved(int x, int y) {
		// MKTODO I think this returns the picked node index computed from the last frame.
		long newHoverNodeIndex = graphicsData.getPickingData().getClosestPickedNodeIndex();
		long newHoverEdgeIndex = graphicsData.getPickingData().getClosestPickedEdgeIndex();
		
		selectionData.setHoverNodeIndex(newHoverNodeIndex);
		selectionData.setHoverEdgeIndex(newHoverEdgeIndex);
	}

	@Override
	public void exited() {
		selectionData.setHoverNodeIndex(NO_INDEX);
		selectionData.setHoverEdgeIndex(NO_INDEX);
	}
			
}
