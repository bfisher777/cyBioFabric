package com.boofisher.app.cySimpleRenderer.internal.picking;

import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.cytoscape.application.CyUserLog;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;

import com.boofisher.app.cySimpleRenderer.internal.data.GraphicsData;
import com.boofisher.app.cySimpleRenderer.internal.data.GraphicsSelectionData;
import com.boofisher.app.cySimpleRenderer.internal.data.PickingData;
import com.boofisher.app.cySimpleRenderer.internal.picking.ShapePickingProcessor;
import com.boofisher.app.cySimpleRenderer.internal.rendering.GraphicsProcedure;
import com.boofisher.app.cySimpleRenderer.internal.rendering.RenderNetwork;
import com.boofisher.app.cySimpleRenderer.internal.tools.NetworkToolkit;
import com.boofisher.app.cySimpleRenderer.internal.tools.PairIdentifier;

public class DefaultShapePickingProcessor implements ShapePickingProcessor {

	final Logger logger = Logger.getLogger(CyUserLog.NAME);
	// Width and height of rectangular region around mouse
	// pointer to use for hit detection on lines
	private static final int HIT_BOX_SIZE = 4;
	
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

	private GraphicsProcedure drawNodesProcedure;
	private GraphicsProcedure drawEdgesProcedure;
	
	public DefaultShapePickingProcessor(GraphicsProcedure drawNodesProcedure, GraphicsProcedure drawEdgesProcedure) {
		this.drawNodesProcedure = drawNodesProcedure;
		this.drawEdgesProcedure = drawEdgesProcedure;
	}
	
	@Override
	public void initialize(GraphicsData graphicsData) {
		drawNodesProcedure.initialize(graphicsData);
		drawEdgesProcedure.initialize(graphicsData);
	}
	
	@Override
	public void processPicking(GraphicsData graphicsData) {
		GraphicsSelectionData selectionData = graphicsData.getSelectionData();
		int x = graphicsData.getMouseCurrentX();
		int y = graphicsData.getMouseCurrentY();
	
		if (selectionData.isDragSelectMode()) {
			int selectionBoxCenterX = (selectionData.getSelectTopLeftX() + selectionData.getSelectBottomRightX()) / 2;
			int selectionBoxCenterY = (selectionData.getSelectTopLeftY() + selectionData.getSelectBottomRightY()) / 2;
			int selectionBoxWidth = Math.max(1, Math.abs(selectionData.getSelectTopLeftX() - selectionData.getSelectBottomRightX()));
			int selectionBoxHeight = Math.max(1, Math.abs(selectionData.getSelectTopLeftY() - selectionData.getSelectBottomRightY()));
			
			performPick(selectionBoxCenterX, selectionBoxCenterY, selectionBoxWidth, selectionBoxHeight, true, graphicsData);
		} else {
			performPick(x, y, 2, 2, false, graphicsData);
		}
	}
	
	
	/**
	 * Perform a picking operation on the specified region to capture 3D shapes
	 * drawn in the given region
	 * 
	 * @param gl
	 *            The {@link GL2} object used for rendering
	 * @param x
	 *            The center x location, in window coordinates
	 * @param y
	 *            The center y location, in window coordinates
	 * @param width
	 *            The width of the box used for picking
	 * @param height
	 *            The height of the box used for picking
	 * @param selectAll
	 *            Whether or not to select all shapes captured in the given
	 *            region, or only to only take the frontmost one
	 * @return The edges and nodes found in the region, as a {@link PickResults}
	 *         object
	 */
	private void performPick(int x, int y, int width, int height, boolean selectAll, GraphicsData graphicsData) {		
			
		Point screenCoords = new Point(x, y);
		ArrayList<Long> edgeHits = new ArrayList<Long>();
		ArrayList<Long> nodeHits = new ArrayList<Long>();			
	
		int midHeight = graphicsData.getScreenHeight()/2;
		int midWidth = graphicsData.getScreenWidth()/2;
		
		CyNetworkView networkView = graphicsData.getNetworkView();
		
		getEdges(edgeHits, nodeHits, screenCoords, networkView, midWidth, midHeight, graphicsData.getZoomFactor(), graphicsData);
		getNodes(nodeHits, screenCoords, networkView, midWidth, midHeight, graphicsData.getZoomFactor(), graphicsData);
		
		//logger.warn("world coords are " + screenCoords.toString() + "hits size is " + edgeHits.size() + nodeHits.size());	
		parseSelectionBufferSelection(edgeHits, nodeHits, graphicsData.getPickingData());
		/*if (selectAll) {
			parseSelectionBufferMultipleSelection(edgeHits, nodeHits,graphicsData.getPickingData());			
		} else {
			parseSelectionBufferSingleSelection(edgeHits, nodeHits, graphicsData.getPickingData());			
		}*/
	}
	
	//TODO learn difference between view and model
	public void getNodes(ArrayList<Long> hits, Point screenCoords, CyNetworkView networkView, int midWidth, 
			int midHeight, int zoomFactor, GraphicsData graphicsData){
				
		Shape shape = null;
		
		// networkView.updateView();
		for (View<CyNode> nodeView : networkView.getNodeViews()) {
			
			if(nodeView == null) {
				// MKTODO why does this happen?
				System.err.println("nodeView is null: networkView.getNodeViews() returns: " + networkView.getNodeViews());
				continue;
			}					
					
						
			// Draw it only if the visual property says it is visible
			if (nodeView.getVisualProperty(BasicVisualLexicon.NODE_VISIBLE)) {
				shape = RenderNetwork.getShape(networkView, nodeView.getModel(), midWidth, midHeight, zoomFactor);						
				// draw Rectangle2D.Double
				if(shape.contains(screenCoords)){
					hits.add(nodeView.getModel().getSUID());
					break;//found hit so stop looking
				}													
			}
		}
	}
	
	//http://stackoverflow.com/questions/1797209/how-to-select-a-line
	public void getEdges(ArrayList<Long> edgeHits, ArrayList<Long> nodeHits, Point screenCoords, CyNetworkView networkView, int midWidth, 
			int midHeight, int zoomFactor, GraphicsData graphicsData){
		// A set containing all pairs of nodes that have had an edge drawn between them
		Set<PairIdentifier> drawnPairs = new HashSet<PairIdentifier>();
		CyNode source, target;		
		
		Shape shape = graphicsData.getMyShape();
		int boxX = (int) (screenCoords.getX() - HIT_BOX_SIZE / 2);
		int boxY = (int) (screenCoords.getY() - HIT_BOX_SIZE / 2);

		int boxWidth = HIT_BOX_SIZE;
		int boxHeight = HIT_BOX_SIZE;
		
		double x1=0, x2=0, y1=0, y2=0;
		for (View<CyEdge> edgeView : networkView.getEdgeViews()) {
			source = edgeView.getModel().getSource();
			target = edgeView.getModel().getTarget();			
			
			PairIdentifier pairIdentifier = NetworkToolkit.obtainPairIdentifier(source, target, networkView.getModel().getNodeList().size());
			
			if(shape == null){
				shape = RenderNetwork.getShape(networkView, edgeView.getModel().getTarget(), midWidth, midHeight, zoomFactor);			
				
				float edgeWidth = edgeView.getVisualProperty(BasicVisualLexicon.EDGE_WIDTH).intValue();
				edgeWidth = (zoomFactor > 0) ? (edgeWidth /(int)(zoomFactor)) : edgeWidth;
				//set stroke and color							
				graphicsData.setMyShape(shape);
			}
			
			// Only draw an edge between this source-target pair if one has not been drawn already
			//TODO mulit edges 
			if (!drawnPairs.contains(pairIdentifier)) {
			
				//TODO fix this
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
					
					Shape line = new Line2D.Double((x1+midWidth), (y1+midHeight), (x2+midWidth), (y2+midHeight));
					
					if(line.intersects(boxX, boxY, boxWidth, boxHeight)){						
						nodeHits.add(edgeView.getModel().getSource().getSUID());
						nodeHits.add(edgeView.getModel().getTarget().getSUID());
						edgeHits.add(edgeView.getModel().getSUID());
						break; //found hit so stop looking
					}
				}
				
				drawnPairs.add(pairIdentifier);
			}
		}	
	}
	
	private void parseSelectionBufferSelection(ArrayList<Long> edgeHits, ArrayList<Long> nodeHits, PickingData pickingData) {
		pickingData.setClosestPickedNodeIndex(NO_INDEX);
		pickingData.setClosestPickedEdgeIndex(NO_INDEX);
		

		for (long v : nodeHits) {	
			pickingData.getPickedNodeIndices().add(v);			
		}
		
		for (long v : edgeHits) {				
			pickingData.setClosestPickedEdgeIndex(v);
		}
	}
	
	//TODO add drag and select function
	private void parseSelectionBufferMultipleSelection(ArrayList<Long> edgeHits, ArrayList<Long> nodeHits, PickingData pickingData) {
		/*pickingData.getPickedNodeIndices().clear();
		pickingData.getPickedEdgeIndices().clear();

		// Current hit record is size 5 because we have
		// (numNames, minZ, maxZ, nameType, suidUpper, suidLower) for
		// indices 0-5 respectively
		final int sizeOfHitRecord = 6;

		if (hits > 0) {
			// The variable max helps keep track of the polygon that is closest
			// to the front of the screen
//			int max = buffer.get(2);
//			int maxType = buffer.get(3);
//
//			selectedType = buffer.get(3);
//			selectedIndex = buffer.get(4);

			// Drag-selection; select all
			for (int i = 0; i < hits; i++) {
				
				int offset = i * sizeOfHitRecord;
				
				int selectedType  = buffer.get(offset + 3);
				int selectedSuidUpper = buffer.get(offset + 4);
				int selectedSuidLower = buffer.get(offset + 5);
				
				long suid = SUIDToolkit.combineInts(selectedSuidUpper, selectedSuidLower);

				if (selectedType == NODE_TYPE) {
					pickingData.getPickedNodeIndices().add(suid);
				} else if (selectedType == EDGE_TYPE) {
					pickingData.getPickedEdgeIndices().add(suid);
				}
			}
		}*/
	}

}

