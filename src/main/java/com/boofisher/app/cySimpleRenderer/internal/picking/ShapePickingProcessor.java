package com.boofisher.app.cySimpleRenderer.internal.picking;

import com.boofisher.app.cySimpleRenderer.internal.data.GraphicsData;

//Read-only from GraphicsData and SelectionData, writes to PickingData
public interface ShapePickingProcessor {
	
	public void initialize(GraphicsData graphicsData);
	
	public void processPicking(GraphicsData graphicsData);

}

