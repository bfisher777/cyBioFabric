package com.boofisher.app.cySimpleRenderer.internal.rendering;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JComponent;

import org.apache.log4j.Logger;
import org.cytoscape.application.CyUserLog;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;

import com.boofisher.app.cySimpleRenderer.internal.data.GraphicsData;
import com.boofisher.app.cySimpleRenderer.internal.graphics.RenderingPanel;
import com.boofisher.app.cySimpleRenderer.internal.tools.NetworkToolkit;
import com.boofisher.app.cySimpleRenderer.internal.tools.PairIdentifier;

public class RenderNetwork implements GraphicsProcedure {
	final Logger logger = Logger.getLogger(CyUserLog.NAME);
	GraphicsData graphicsData;
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
		
		CyNetworkView networkView = graphicsData.getNetworkView();
		
		BufferedImage bImage = graphicsData.getBufferedImage();
		JComponent component = graphicsData.getContainer();
		
		if( bImage == null || bImage.getWidth() != width || height != bImage.getHeight()){
			logger.warn("Rendering main ? " + graphicsData.isMain());
			logger.warn("width = " + width + " height = " + height + " zoomFact = " + graphicsData.getZoomFactor());
			
			bImage = component.getGraphicsConfiguration().createCompatibleImage(width, height);
			Graphics2D imageGraphics = bImage.createGraphics();					
			
			//paint background
			imageGraphics.setColor((Color) networkView.getVisualProperty(BasicVisualLexicon.NETWORK_BACKGROUND_PAINT));
			imageGraphics.fillRect(0,0, width, height);	
						
			drawEdges(imageGraphics, networkView, midWidth, midHeight);
			drawNodes(imageGraphics, networkView, midWidth, midHeight);
			imageGraphics.dispose();
			graphicsData.setBufferedImage(bImage);
		}else{
			//do nothing network has already been drawn
		}	
			
	}
	
	public void drawEdges(Graphics2D g2, CyNetworkView networkView, int midWidth, int midHeight){
		
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
				shape = RenderingPanel.getShape(networkView, edgeView.getModel().getTarget(), midWidth, midHeight);
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
						x1 = sourceView.getVisualProperty(BasicVisualLexicon.NODE_X_LOCATION) + shape.getBounds2D().getWidth()/2;
						y1 = sourceView.getVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION) + shape.getBounds2D().getHeight()/2;
						
						x2 = targetView.getVisualProperty(BasicVisualLexicon.NODE_X_LOCATION) + shape.getBounds2D().getWidth()/2;
						y2 = targetView.getVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION) + shape.getBounds2D().getHeight()/2;
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
	
	public void drawNodes(Graphics2D g2, CyNetworkView networkView, int midWidth, int midHeight){
		Shape shape = null;
		
		// networkView.updateView();
		for (View<CyNode> nodeView : networkView.getNodeViews()) {
			
			if(nodeView == null) {
				// MKTODO why does this happen?
				System.err.println("nodeView is null: networkView.getNodeViews() returns: " + networkView.getNodeViews());
				continue;
			}					
			
			//set color			
			g2.setColor((Color) nodeView.getVisualProperty(BasicVisualLexicon.NODE_FILL_COLOR));		
						
			// Draw it only if the visual property says it is visible
			if (nodeView.getVisualProperty(BasicVisualLexicon.NODE_VISIBLE)) {
				shape = RenderingPanel.getShape(networkView, nodeView.getModel(), midWidth, midHeight);						
				// draw Rectangle2D.Double
				g2.fill(shape);																
			}
		}
	}
}
