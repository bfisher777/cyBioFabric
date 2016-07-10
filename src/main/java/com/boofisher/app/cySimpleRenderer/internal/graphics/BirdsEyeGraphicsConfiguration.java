package com.boofisher.app.cySimpleRenderer.internal.graphics;

import javax.swing.JComponent;

import org.apache.log4j.Logger;
import org.cytoscape.application.CyUserLog;

import com.boofisher.app.cySimpleRenderer.internal.input.handler.BirdsEyeEventBusListener;

import com.boofisher.app.cySimpleRenderer.internal.data.GraphicsData;
import com.boofisher.app.cySimpleRenderer.internal.graphics.AbstractGraphicsConfiguration;
import com.boofisher.app.cySimpleRenderer.internal.rendering.RenderEdges;
import com.boofisher.app.cySimpleRenderer.internal.rendering.RenderNodes;
import com.google.common.eventbus.EventBus;

public class BirdsEyeGraphicsConfiguration extends AbstractGraphicsConfiguration {
	final Logger logger = Logger.getLogger(CyUserLog.NAME);
	private JComponent frame;	
	
	public BirdsEyeGraphicsConfiguration() {			
		add(new RenderEdges());
		add(new RenderNodes());
	}
	
	@Override
	public void initializeFrame(JComponent frame, JComponent inputComponent) {
		this.frame = frame;
	}
	
	@Override
	public void initialize(GraphicsData graphicsData) {
		super.initialize(graphicsData);
		
//		MouseZoneInputListener mouseZoneListener = MouseZoneInputListener.attach(frame, graphicsData.getInputComponent(), graphicsData);
//		mouseZoneListener.setMouseMode(MouseMode.CAMERA); // always stay in camera mode
//		BirdsEyeInputEventListener.attach(graphicsData.getInputComponent(), graphicsData, mouseZoneListener);
		
		EventBus eventBus = graphicsData.getEventBus();
		BirdsEyeEventBusListener eventBusListener = new BirdsEyeEventBusListener(graphicsData);
		eventBus.register(eventBusListener);
		
		if(graphicsData.getBirdZoom() == 0){			
			// Manually fit graph into the correct size for the first frame.
			eventBusListener.handleFitInViewEvent(null);
		}
				
	}
	
	
	@Override
	public String toString() {
		return "BirdsEyeGraphicsConfiguration";
	}
}
