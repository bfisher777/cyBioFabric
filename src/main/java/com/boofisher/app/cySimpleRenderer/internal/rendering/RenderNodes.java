package com.boofisher.app.cySimpleRenderer.internal.rendering;


import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.image.BufferedImage;

import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;

import com.boofisher.app.cySimpleRenderer.internal.data.GraphicsData;
import com.boofisher.app.cySimpleRenderer.internal.graphics.AbstractRenderingPanel;

public class RenderNodes implements GraphicsProcedure {
	
	GraphicsData graphicsData;
	
	
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
		
		int zoom = graphicsData.isMain() ? graphicsData.getMainZoom() : graphicsData.getBirdZoom();

		CyNetworkView networkView = graphicsData.getNetworkView();
		
		BufferedImage bImage = graphicsData.isMain() ? graphicsData.getMainBufferedImage() : graphicsData.getBirdsEyeBufferedImage();
		Graphics2D imageGraphics = bImage.createGraphics();		
		
		drawNodesAndLabels(imageGraphics, networkView, midWidth, midHeight, zoom);
	}
	
	public void drawNodesAndLabels(Graphics2D g2, CyNetworkView networkView, int midWidth, int midHeight, int zoomFactor){
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
				shape = AbstractRenderingPanel.getShape(networkView, nodeView.getModel(), midWidth, midHeight, zoomFactor);						
				// draw Rectangle2D.Double
				g2.fill(shape);																
			}
		}
	}

}
