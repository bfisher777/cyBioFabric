package com.boofisher.app.cySimpleRenderer.internal.graphics;

import java.util.Collection;

import javax.swing.JComponent;
import javax.swing.RootPaneContainer;

import com.boofisher.app.cySimpleRenderer.internal.eventbus.FitInViewEvent;
import com.boofisher.app.cySimpleRenderer.internal.input.handler.MainEventBusListener;
import com.boofisher.app.cySimpleRenderer.internal.input.handler.MainInputEventListener;

import com.boofisher.app.cySimpleRenderer.internal.input.handler.InputEventListener;

import org.apache.log4j.Logger;
import org.cytoscape.application.CyUserLog;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.View;

import com.boofisher.app.cySimpleRenderer.internal.data.GraphicsData;//open-gl
import com.boofisher.app.cySimpleRenderer.internal.graphics.AbstractGraphicsConfiguration;//open-gl
import com.boofisher.app.cySimpleRenderer.internal.input.handler.ToolPanel;
import com.boofisher.app.cySimpleRenderer.internal.rendering.RenderEdges;
import com.boofisher.app.cySimpleRenderer.internal.rendering.RenderNodes;
import com.google.common.eventbus.EventBus;

public class MainGraphicsConfiguration extends AbstractGraphicsConfiguration {
	final Logger logger = Logger.getLogger(CyUserLog.NAME);
	private JComponent frame;
	private ToolPanel toolPanel;
	private InputEventListener inputHandler;		
	
	public MainGraphicsConfiguration() {					
		add(new RenderEdges());
		add(new RenderNodes());						
	}
	
	
	@Override
	public void initializeFrame(JComponent component, JComponent inputComponent) {
		this.frame = component;
		if(component instanceof RootPaneContainer) {
			this.toolPanel = new ToolPanel((RootPaneContainer)component, inputComponent);
		}
	}
	
	
	@Override
	public void initialize(GraphicsData graphicsData) {
		super.initialize(graphicsData);
		
		// Input handler		
		inputHandler = MainInputEventListener.attach(graphicsData.getInputComponent(), graphicsData);
		
		// EventBus
		EventBus eventBus = graphicsData.getEventBus();
		if(toolPanel != null) {
			toolPanel.setEventBus(eventBus);
		}
		MainEventBusListener eventBusListener = new MainEventBusListener(graphicsData);
		eventBus.register(eventBusListener);		
		
		// Manually fit the network into the view for the first frame
		if(graphicsData.getZoomFactor() == 0){
			logger.warn("main zoom == 0");
			Collection<View<CyNode>> nodeViews = graphicsData.getNetworkView().getNodeViews();				
			eventBusListener.handleFitInViewEvent(new FitInViewEvent(nodeViews));
		}
	}
	
	
	@Override
	public void update() {
		
	}

	
	@Override
	public void dispose() {
		inputHandler.dispose();
	}
	

	@Override
	public String toString() {
		return "MainGraphicsConfiguration";
	}

}