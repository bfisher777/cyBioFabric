package com.boofisher.app.cyBioFabric.internal.rendering;


import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.image.BufferedImage;

import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;

import com.boofisher.app.cyBioFabric.internal.data.GraphicsData;
import com.boofisher.app.cyBioFabric.internal.graphics.RenderingPanel;

public class RenderNodes { //implements GraphicsProcedure {
	
	/*GraphicsData graphicsData;
	
	
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
		
		drawNodes(imageGraphics, networkView, midWidth, midHeight, zoom);
		imageGraphics.dispose();
	}
	
	public void drawNodes(Graphics2D g2, CyNetworkView networkView, int midWidth, int midHeight, int zoomFactor){
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
				shape = RenderingPanel.getShape(networkView, nodeView.getModel(), midWidth, midHeight, zoomFactor);						
				// draw Rectangle2D.Double
				g2.fill(shape);																
			}
		}
	}
*/
}
