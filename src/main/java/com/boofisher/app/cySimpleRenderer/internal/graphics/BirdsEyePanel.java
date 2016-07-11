package com.boofisher.app.cySimpleRenderer.internal.graphics;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;

import org.apache.log4j.Logger;
import org.cytoscape.application.CyUserLog;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;

import com.boofisher.app.cySimpleRenderer.internal.cytoscape.view.CySRNetworkView;
import com.boofisher.app.cySimpleRenderer.internal.data.GraphicsData;
import com.boofisher.app.cySimpleRenderer.internal.eventbus.EventBusProvider;

public class BirdsEyePanel extends AbstractRenderingPanel{
	
	private static final long serialVersionUID = -8440123285895117402L;
	final Logger logger = Logger.getLogger(CyUserLog.NAME);
	
	public BirdsEyePanel(
			CySRNetworkView networkView, 
			VisualLexicon visualLexicon, 
			EventBusProvider eventBusProvider, 
			GraphicsConfiguration configuration,									
			JComponent inputComponent){
		super(networkView, visualLexicon, eventBusProvider, configuration, inputComponent, false); 
				
	}
	
	@Override
    public void paintComponent(Graphics g) {		

		configuration.initialize(graphicsData);
		CyNetworkView networkView = graphicsData.getNetworkView();
		
		zoom = graphicsData.getZoomFactor();		
		
		int width = this.getWidth();
		int height = this.getHeight();				

		//check if birds eye buffered image has already been created
		if(graphicsData.getBufferedImage() == null){						
			bImage = this.getGraphicsConfiguration().createCompatibleImage(width, height);
			Graphics2D imageGraphics = bImage.createGraphics();
			//paint background
			imageGraphics.setColor((Color) networkView.getVisualProperty(BasicVisualLexicon.NETWORK_BACKGROUND_PAINT));
			imageGraphics.fillRect(0,0, getWidth(), getHeight());
			
			graphicsData.setBufferedImage(bImage);
			//drawEdges(imageGraphics, networkView, midWidth, midHeight, zoom);
			//drawNodesAndLabels(imageGraphics, networkView, midWidth, midHeight, zoom);

			//now call the graphics configuration rendering procedures
			configuration.drawScene();
			
		}else{					
			bImage = graphicsData.getBufferedImage();
		}
			
		graphicsData.setBufferedImage(bImage);
		iImage = new ImageIcon(bImage);
		
		//Set up the scroll pane.
		picture = new JLabel(iImage);
        
        pictureScrollPane.setViewportView(picture);
    }	
	
	@Override
	public void drawNodeLabels(View<CyNode> nodeView, Graphics2D g, int midWidth, int midHeight, float x, float y) {
		// TODO Auto-generated method stub
		
	}
	

}

