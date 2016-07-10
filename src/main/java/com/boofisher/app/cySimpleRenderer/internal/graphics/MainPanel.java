package com.boofisher.app.cySimpleRenderer.internal.graphics;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;

import org.cytoscape.application.CyUserLog;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import com.boofisher.app.cySimpleRenderer.internal.cytoscape.view.CySRNetworkView;
import com.boofisher.app.cySimpleRenderer.internal.data.GraphicsData;
import com.boofisher.app.cySimpleRenderer.internal.eventbus.EventBusProvider;

import org.apache.log4j.Logger;

public class MainPanel extends AbstractRenderingPanel{
	
	final Logger logger = Logger.getLogger(CyUserLog.NAME);
	/**
	 * 
	 */
	private static final long serialVersionUID = -4072881517530326300L;
	private static final int DEFAULT_TEXT_FONT_SIZE = 9;
	private static final String DEFAULT_FONT_NAME = "SansSerif";
	private static final Color TEXT_DEFAULT_COLOR = Color.BLACK;	
	    
	public MainPanel(
			CySRNetworkView networkView, 
			VisualLexicon visualLexicon, 
			EventBusProvider eventBusProvider, 
			GraphicsConfiguration configuration,						
			JComponent inputComponent,
			GraphicsData graphicsData){
		super(networkView, visualLexicon, eventBusProvider, configuration, inputComponent, true, graphicsData); 
	}
	
	@Override
    public void paintComponent(Graphics g) {	
				
		//set up event listeners / handlers
		configuration.initialize(graphicsData);	
		
		CyNetworkView networkView = graphicsData.getNetworkView();
		
		zoom = graphicsData.getMainZoom();
		
		//logger.warn("MAIN ZOOM = " + zoom);
		
		int width = this.getWidth();
		int height = this.getHeight();

		bImage = this.getGraphicsConfiguration().createCompatibleImage(width, height);
		Graphics2D imageGraphics = bImage.createGraphics();
		graphicsData.setMainBufferedImage(bImage);
		
		//paint background
		imageGraphics.setColor((Color) networkView.getVisualProperty(BasicVisualLexicon.NETWORK_BACKGROUND_PAINT));
		imageGraphics.fillRect(0,0, getWidth(), getHeight());
		
		//drawEdges(imageGraphics, networkView, midWidth, midHeight, zoom);
		//drawNodesAndLabels(imageGraphics, networkView, midWidth, midHeight, zoom);
		
		//now call the graphics configuration rendering procedures
		configuration.drawScene();
				
		graphicsData.setMainBufferedImage(bImage);
		
		iImage = new ImageIcon(bImage);
		
		//Set up the scroll pane.
        picture = new JLabel(iImage);
        
        pictureScrollPane.setViewportView(picture);

		//Image scaledImage = bImage.getScaledInstance((int)width/2,height/2,bImage.SCALE_SMOOTH);
		//g2.drawImage(bImage, 1, 1, null);
    }	

	@Override
	public void drawNodeLabels(View<CyNode> nodeView, Graphics2D g2, int midWidth, int midHeight, float x, float y) {
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
				int textFontSize = DEFAULT_TEXT_FONT_SIZE - zoom;
				
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
