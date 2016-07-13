package com.boofisher.app.cySimpleRenderer.internal.input.handler.commands;

import com.boofisher.app.cySimpleRenderer.internal.data.GraphicsData;
import com.boofisher.app.cySimpleRenderer.internal.data.GraphicsSelectionData;
import com.boofisher.app.cySimpleRenderer.internal.data.PickingData;
import com.boofisher.app.cySimpleRenderer.internal.graphics.RenderingPanel;
import com.boofisher.app.cySimpleRenderer.internal.input.handler.MouseCommandAdapter;
import com.boofisher.app.cySimpleRenderer.internal.tools.NetworkToolkit;
import com.boofisher.app.cySimpleRenderer.internal.tools.PairIdentifier;
import com.boofisher.app.cySimpleRenderer.internal.tools.SUIDToolkit;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.nio.IntBuffer;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;

public class SelectionAddMouseCommand extends MouseCommandAdapter {

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
	
	
	//TODO fix this
/*	private Point viewToWorld(int x, int y){
		int zoom = graphicsData.getZoomFactor();
		int width = graphicsData.getContainer().getWidth();
		int height = graphicsData.getContainer().getHeight();
		int midWidth = width/2;
		int midHeight = height/2;
		
		return new Point( (x*zoom-midWidth), (y*zoom-midHeight) );
	}
	
	private void processPicking(Point start, Point end) {
		GraphicsSelectionData selectionData = graphicsData.getSelectionData();
		Point userStart = viewToWorld(selectionData.getSelectTopLeftX(), selectionData.getSelectTopLeftY());
		Point userEnd = viewToWorld(selectionData.getSelectTopLeftX(), selectionData.getSelectTopLeftY());
	
		if (selectionData.isDragSelectMode()) {
			
			int selectionBoxWidth = Math.max(1, Math.abs(selectionData.getSelectTopLeftX() - selectionData.getSelectBottomRightX()));
			int selectionBoxHeight = Math.max(1, Math.abs(selectionData.getSelectTopLeftY() - selectionData.getSelectBottomRightY()));
			
			performPick(userStart, userEnd, selectionBoxWidth, selectionBoxHeight, true);
		} else {
			performPick(userStart, userEnd, 2, 2, false);
		}		
	}
	
	private void performPick(Point userClick1, Point userClick2, int boxWidth, int boxHeight, boolean selectAll) {
		Long hits[];
		
		CyNetworkView networkView = graphicsData.getNetworkView();
		CyNode source, target;	
		Shape shape;
		
		for (View<CyEdge> edgeView : networkView.getEdgeViews()) {
			source = edgeView.getModel().getSource();
			target = edgeView.getModel().getTarget();
			
			shape = AbstractRenderingPanel.getShape(networkView, edgeView.getModel().getTarget(), midWidth, midHeight, zoomFactor);
				
				
				
			
			
			PairIdentifier pairIdentifier = NetworkToolkit.obtainPairIdentifier(source, target, networkView.getModel().getNodeList().size());
			
			// Only draw an edge between this source-target pair if one has not been drawn already
			//TODO mulit edges 
			if (!drawnPairs.contains(pairIdentifier)) {
			
				if(edgeView.getVisualProperty(BasicVisualLexicon.EDGE_LINE_TYPE).toString().equals("SOLID") || true){
					
					View<CyNode> sourceView = networkView.getNodeView(source);
					View<CyNode> targetView = networkView.getNodeView(target);
					
					if (sourceView != null && targetView != null) {
						x1 = sourceView.getVisualProperty(BasicVisualLexicon.NODE_X_LOCATION)/zoomFactor + shape.getBounds2D().getWidth()/2;
						y1 = sourceView.getVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION)/zoomFactor + shape.getBounds2D().getHeight()/2;
						
						x2 = targetView.getVisualProperty(BasicVisualLexicon.NODE_X_LOCATION)/zoomFactor + shape.getBounds2D().getWidth()/2;
						y2 = targetView.getVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION)/zoomFactor + shape.getBounds2D().getHeight()/2;
					}else{
						continue;
					}
					
					//draw edge between nodes
					// draw Rectangle2D.Double
					g2.draw(new Line2D.Double((x1+midWidth), (y1+midHeight), (x2+midWidth), (y2+midHeight)));
				}
				
				drawnPairs.add(pairIdentifier);
			}
		
		if (selectAll) {
			parseSelectionBufferMultipleSelection(hits, graphicsData.getPickingData());
		} else {
			parseSelectionBufferSingleSelection(hits, graphicsData.getPickingData());
		}
	}
	
	private void parseSelectionBufferSingleSelection(Long hits[], PickingData pickingData) {
		pickingData.setClosestPickedNodeIndex(NO_INDEX);
		pickingData.setClosestPickedEdgeIndex(NO_INDEX);
		
		

		if (hits > 0) {

			
			if (selectedType == NODE_TYPE) {
				pickingData.setClosestPickedNodeIndex(suid);
			} else if (selectedType == EDGE_TYPE) {
				pickingData.setClosestPickedEdgeIndex(suid);
			}
		}
	}
	
	
	private void parseSelectionBufferMultipleSelection(Long hits[], PickingData pickingData) {
		pickingData.getPickedNodeIndices().clear();
		pickingData.getPickedEdgeIndices().clear();


		if (hits > 0) {

			// Drag-selection; select all
			for (int i = 0; i < hits; i++) {


				if (selectedType == NODE_TYPE) {
					pickingData.getPickedNodeIndices().add(suid);
				} else if (selectedType == EDGE_TYPE) {
					pickingData.getPickedEdgeIndices().add(suid);
				}
			}
		}
	}*/
	
	
}
