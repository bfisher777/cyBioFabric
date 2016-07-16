package com.boofisher.app.cySimpleRenderer.internal.rendering;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;

import com.boofisher.app.cySimpleRenderer.internal.data.GraphicsData;

public class RenderLabels implements GraphicsProcedure {
	
	private static final int DEFAULT_TEXT_FONT_SIZE = 9;
	private static final String DEFAULT_FONT_NAME = "SansSerif";
	private static final Color TEXT_DEFAULT_COLOR = Color.BLACK;
	
	GraphicsData graphicsData;
	
	@Override
	public void initialize(GraphicsData graphicsData) {
		this.graphicsData = graphicsData;
	}
	
	
	//TODO this doesn't work
	@Override
	public void execute(GraphicsData graphicsData) {
		int width = graphicsData.getContainer().getWidth();
		int height = graphicsData.getContainer().getHeight();
		int midWidth = width/2;
		int midHeight = height/2;
		

		float x, y;
		CyNetworkView networkView = graphicsData.getNetworkView();
		
		BufferedImage bImage = graphicsData.getBufferedImage();
		Graphics2D imageGraphics = bImage.createGraphics();	
		
		if(graphicsData.getShowLabels()){
			JScrollPane pictureScrollPane = graphicsData.getScrollPane();
			// networkView.updateView();
			for (View<CyNode> nodeView : networkView.getNodeViews()) {
				x = nodeView.getVisualProperty(BasicVisualLexicon.NODE_X_LOCATION).floatValue();
				y = nodeView.getVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION).floatValue();
				//abstract method implementing class will write 
				drawNodeLabels(nodeView, imageGraphics, midWidth, midHeight, x, y, pictureScrollPane);
			}
		}
		imageGraphics.dispose();
	}
	
	public void drawNodeLabels(View<CyNode> nodeView, Graphics2D g2, int midWidth, int midHeight, 
			float x, float y, JScrollPane pictureScrollPane) {
		//handle text labels drawing
		String text = nodeView.getVisualProperty(BasicVisualLexicon.NODE_LABEL);		
		Color textColor = null;
		
		if (text != null) {
				
			//TODO this if statement doesn't work
			if (pictureScrollPane.getViewport().getSize().getWidth() > x &&
					pictureScrollPane.getViewport().getSize().getHeight() > y && x > 0 && y > 0 || true) {
				
				// TODO: Check if there is a way around this cast
				textColor = (Color) nodeView.getVisualProperty(BasicVisualLexicon.NODE_LABEL_COLOR);
				if (textColor == null) {
					
					// Use black as default if no node label color was found
					textColor = TEXT_DEFAULT_COLOR;
				}
				int textFontSize = DEFAULT_TEXT_FONT_SIZE;
				
				textFontSize = (textFontSize > 14 ) ? 14 : textFontSize;
				textFontSize = (textFontSize < 2 ) ? 2 : textFontSize;
				Font font = new Font(DEFAULT_FONT_NAME, Font.PLAIN, textFontSize);
				
				g2.setColor(textColor);
				g2.setFont(font);
				g2.drawString(text, x+midWidth, y+midHeight);		
			}
		}		
	}	

}
