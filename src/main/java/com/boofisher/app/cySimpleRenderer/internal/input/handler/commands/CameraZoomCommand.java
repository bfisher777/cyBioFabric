package com.boofisher.app.cySimpleRenderer.internal.input.handler.commands;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.boofisher.app.cySimpleRenderer.internal.data.GraphicsData;
import com.boofisher.app.cySimpleRenderer.internal.data.GraphicsSelectionData;
import com.boofisher.app.cySimpleRenderer.internal.geometric.Vector3;
import com.boofisher.app.cySimpleRenderer.internal.input.handler.MouseWheelCommand;
import com.boofisher.app.cySimpleRenderer.internal.tools.NetworkToolkit;

import org.apache.log4j.Logger;
import org.cytoscape.application.CyUserLog;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTableUtil;
import org.cytoscape.view.model.CyNetworkView;

public class CameraZoomCommand implements MouseWheelCommand {

	final Logger logger = Logger.getLogger(CyUserLog.NAME);
	private final GraphicsData graphicsData;
	
	
	public CameraZoomCommand(GraphicsData graphicsData) {
		this.graphicsData = graphicsData;
	}


	@Override
	public void execute(int dWheel) {		
		if( (graphicsData.getZoomFactor() + dWheel) > 0 ){
			//logger.warn("In Camera Zoom command changing main zoom incrementing by: " + dWheel);
			graphicsData.changeZoomFactor(dWheel);
		}
	}

}
