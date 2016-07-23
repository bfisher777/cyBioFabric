package com.boofisher.app.cyBioFabric.internal.graphics;

import static com.google.common.base.Preconditions.checkNotNull;

import java.awt.Graphics;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.RootPaneContainer;

import org.apache.log4j.Logger;
import org.cytoscape.application.CyUserLog;
import org.cytoscape.view.model.VisualLexicon;
import com.boofisher.app.cyBioFabric.internal.cytoscape.view.CySRNetworkView;
import com.boofisher.app.cyBioFabric.internal.data.GraphicsData;
import com.boofisher.app.cyBioFabric.internal.eventbus.EventBusProvider;
import com.google.common.eventbus.EventBus;

public class RenderingPanel extends JPanel{
	
	final Logger logger = Logger.getLogger(CyUserLog.NAME);
	
	private static final long serialVersionUID = -8440123285895117402L;
	protected final GraphicsData graphicsData;
	protected final GraphicsConfiguration configuration;
	
	
	protected JPanel picture;
	protected JScrollPane jsp;
	    
	public RenderingPanel(
			CySRNetworkView networkView, 
			VisualLexicon visualLexicon, 
			EventBusProvider eventBusProvider, 
			GraphicsConfiguration configuration,									
			JComponent inputComponent) {
		super();		
		
		this.configuration = checkNotNull(configuration);
		EventBus eventBus = eventBusProvider.getEventBus(networkView);				
		
		//set up scroll pane on main view
		if( this instanceof RootPaneContainer) {
			jsp = new JScrollPane(this);
			this.graphicsData = new GraphicsData(networkView, visualLexicon, eventBus, this, inputComponent, jsp);
			graphicsData.setIsMain(true);						
		}else{			 
			this.graphicsData = new GraphicsData(networkView, visualLexicon, eventBus, this, inputComponent, null);
		}				
	}
	
	@Override
    public void paintComponent(Graphics g) {										
		super.paintComponent(g);
		graphicsData.setMyGraphics(g);
		graphicsData.setScreenHeight(this.getHeight());
		graphicsData.setScreenWidth(this.getWidth());
		
		//now call the graphics configuration rendering procedures
		// Doesn't really need to be split into two methods, but it allows GrapicsConfigurations to 
		// only override update() and leave the drawing to AbstractGraphicsConfiguration.
		//update() handles picking and picked node rendering 
		configuration.update();
		//draw scene calls the rendering procedures
		configuration.drawScene();
		
    }	

	public GraphicsData getGraphicsData(){return this.graphicsData;}
					
}
