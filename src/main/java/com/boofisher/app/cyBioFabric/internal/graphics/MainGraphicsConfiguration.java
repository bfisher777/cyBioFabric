package com.boofisher.app.cyBioFabric.internal.graphics;

import org.apache.log4j.Logger;
import org.cytoscape.application.CyUserLog;
import com.boofisher.app.cyBioFabric.internal.graphics.AbstractGraphicsConfiguration;//open-gl

public class MainGraphicsConfiguration extends AbstractGraphicsConfiguration {
	final Logger logger = Logger.getLogger(CyUserLog.NAME);	
	private final String NAME = "MainGraphicsConfiguration";
	//private final ShapePickingProcessor shapePickingProcessor;
	
	public MainGraphicsConfiguration() {
		
	}	
	

	@Override
	public String toString() {
		return NAME;
	}

}