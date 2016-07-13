package com.boofisher.app.cySimpleRenderer.internal.input.handler;

import java.util.Collection;

import org.apache.log4j.Logger;
import org.cytoscape.application.CyUserLog;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.View;

import com.boofisher.app.cySimpleRenderer.internal.data.GraphicsData;
import com.boofisher.app.cySimpleRenderer.internal.eventbus.FitInViewEvent;
import com.boofisher.app.cySimpleRenderer.internal.eventbus.ShowLabelsEvent;
import com.boofisher.app.cySimpleRenderer.internal.tools.NetworkToolkit;
import com.google.common.eventbus.Subscribe;

public class MainEventBusListener {
	
	final Logger logger = Logger.getLogger(CyUserLog.NAME);
	private final GraphicsData graphicsData;

	public MainEventBusListener(GraphicsData graphicsData) {
		this.graphicsData = graphicsData;
	}
	
	
	@Subscribe
	public void handleShowLabelsEvent(ShowLabelsEvent showLabelsEvent) {
		//logger.warn("handleShowLabelsEvent handled");
		
		graphicsData.setShowLabels(showLabelsEvent.showLabels());
		graphicsData.getNetworkView().updateView();
	}	
	
	@Subscribe
	public void handleFitInViewEvent(FitInViewEvent e) {
		// ignore selected node views, always use all of them
		Collection<View<CyNode>> nodeViews = graphicsData.getNetworkView().getNodeViews(); 
		
		int zoom = (int)NetworkToolkit.fitInView(nodeViews, GraphicsData.DISTANCE_SCALE, 1.0, 5.0);		
		graphicsData.setZoomFactor(zoom);		
	}
		
}

