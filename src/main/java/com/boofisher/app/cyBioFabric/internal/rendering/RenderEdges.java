package com.boofisher.app.cyBioFabric.internal.rendering;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Set;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;

import com.boofisher.app.cyBioFabric.internal.data.GraphicsData;
import com.boofisher.app.cyBioFabric.internal.graphics.RenderingPanel;
import com.boofisher.app.cyBioFabric.internal.tools.NetworkToolkit;
import com.boofisher.app.cyBioFabric.internal.tools.PairIdentifier;

public class RenderEdges { //implements GraphicsProcedure {
	
	/*GraphicsData graphicsData;
	boolean isMain;
	
	
	@Override
	public void initialize(GraphicsData graphicsData) {
		this.graphicsData = graphicsData;		
	}
	
	
	
	@Override
	public void execute(GraphicsData graphicsData) {
		int width = graphicsData.getContainer().getWidth();
		int height = graphicsData.getContainer().getHeight();
		int midWidth = width/2;
		int midHeight = height/2;
		
		int zoom = graphicsData.getZoomFactor();				

		CyNetworkView networkView = graphicsData.getNetworkView();
		
		BufferedImage bImage = graphicsData.getBufferedImage();
		Graphics2D imageGraphics = bImage.createGraphics();		
		
		drawEdges(imageGraphics, networkView, midWidth, midHeight, zoom);
		imageGraphics.dispose();
	}
	
	public void drawEdges(Graphics2D g2, CyNetworkView networkView, int midWidth, int midHeight, int zoomFactor){
		
		// A set containing all pairs of nodes that have had an edge drawn between them
		Set<PairIdentifier> drawnPairs = new HashSet<PairIdentifier>();
		CyNode source, target;		
		
		Shape shape = null;
		boolean rectMade = false;
		
		double x1=0, x2=0, y1=0, y2=0;
		for (View<CyEdge> edgeView : networkView.getEdgeViews()) {
			source = edgeView.getModel().getSource();
			target = edgeView.getModel().getTarget();
			
			if(!rectMade){
				shape = RenderingPanel.getShape(networkView, edgeView.getModel().getTarget(), midWidth, midHeight, zoomFactor);
				rectMade = true;
				
				//set stroke and color			
				g2.setColor((Color) edgeView.getVisualProperty(BasicVisualLexicon.EDGE_PAINT));
				g2.setStroke( new BasicStroke( edgeView.getVisualProperty(BasicVisualLexicon.EDGE_WIDTH).intValue()));
			}
			
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
		}	
	}
*/
}