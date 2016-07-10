package com.boofisher.app.cySimpleRenderer.internal.input.handler;

import java.util.Collection;

import com.boofisher.app.cySimpleRenderer.internal.data.GraphicsData;
//import com.boofisher.app.cySimpleRenderer.internal.eventbus.BoundingBoxUpdateEvent;
import com.boofisher.app.cySimpleRenderer.internal.eventbus.FitInViewEvent;
//import com.boofisher.app.cySimpleRenderer.internal.rendering.RenderBoundingBoxProcedure;
import com.boofisher.app.cySimpleRenderer.internal.tools.NetworkToolkit;

import org.apache.log4j.Logger;
import org.cytoscape.application.CyUserLog;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.View;

import com.google.common.eventbus.Subscribe;

/**
 * Updates the birds-eye camera whenever the main camera changes 
 * by listening for camera change events on the event bus.
 * 
 * @author mkucera
 */
public class BirdsEyeEventBusListener {
	
	final Logger logger = Logger.getLogger(CyUserLog.NAME);
	// The GraphicsData from the birds-eye renderer.
	private final GraphicsData graphicsData;
	
	
	public BirdsEyeEventBusListener(GraphicsData graphicsData) {
		this.graphicsData = graphicsData;
	}
	
	
	@Subscribe
	public void handleFitInViewEvent(FitInViewEvent e) {
		// ignore selected node views, always use all of them
		Collection<View<CyNode>> nodeViews = graphicsData.getNetworkView().getNodeViews(); 
		
		int zoom = (int)NetworkToolkit.fitInView(nodeViews, GraphicsData.DISTANCE_SCALE, 1.9, 5.0);				
		
		graphicsData.setBirdsEyeZoom(zoom);
	}

	
/*	@Subscribe
	public void handleMainCameraChangeEvent(MainCameraChangeEvent e) {
		
		
	}
	
	
	@Subscribe
	public void handleBoundingBoxUpdateEvent(BoundingBoxUpdateEvent e) {
		
	}*/
	
}
