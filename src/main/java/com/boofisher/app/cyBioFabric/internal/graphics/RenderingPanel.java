package com.boofisher.app.cyBioFabric.internal.graphics;

import static com.google.common.base.Preconditions.checkNotNull;

import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.RootPaneContainer;

import org.apache.log4j.Logger;
import org.cytoscape.application.CyUserLog;
import org.cytoscape.view.model.VisualLexicon;
import com.boofisher.app.cyBioFabric.internal.cytoscape.view.CyBFNetworkView;
import com.boofisher.app.cyBioFabric.internal.data.GraphicsData;
import com.boofisher.app.cyBioFabric.internal.eventbus.EventBusProvider;
import com.google.common.eventbus.EventBus;

public class RenderingPanel extends JPanel{
	
	final Logger logger = Logger.getLogger(CyUserLog.NAME);
	
	private static final long serialVersionUID = -8440123285895117402L;
	protected final GraphicsData graphicsData;
	protected final GraphicsConfiguration configuration;
	
	
	protected JPanel picture;
	    
	public RenderingPanel(
			CyBFNetworkView networkView, 
			VisualLexicon visualLexicon, 
			EventBusProvider eventBusProvider, 
			GraphicsConfiguration configuration,									
			JComponent inputComponent) {
		super();		
		
		this.configuration = checkNotNull(configuration);
		EventBus eventBus = eventBusProvider.getEventBus(networkView);				
		
		JScrollPane jsp = new JScrollPane(this);
	    
		
		//set up scroll pane on main view
		if( this instanceof RootPaneContainer) {
			this.graphicsData = new GraphicsData(networkView, visualLexicon, eventBus, this, inputComponent);
			graphicsData.setIsMain(true);						
		}else{			 
			this.graphicsData = new GraphicsData(networkView, visualLexicon, eventBus, this, inputComponent);
		}				
	}
	
	@Override
    public void paintComponent(Graphics g) {										
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D)g;
		graphicsData.setMyGraphics(g2d);
		graphicsData.setScreenHeight(this.getHeight());
		graphicsData.setScreenWidth(this.getWidth());
		graphicsData.setATransform(g2d.getTransform());
		
		//now call the graphics configuration rendering procedures
		//Doesn't really need to be split into two methods, but it allows GrapicsConfigurations to 
		//only override update() and leave the drawing to AbstractGraphicsConfiguration.
		//update() handles picking and picked node rendering 
		configuration.update();
		//draw scene calls the rendering procedures
		configuration.drawScene();
		
    }	

	public GraphicsData getGraphicsData(){return this.graphicsData;}
					
}
