package com.boofisher.app.cyBioFabric.internal.graphics;

import org.apache.log4j.Logger;
import org.cytoscape.application.CyUserLog;

public class ThumbnailGraphicsConfiguration  extends AbstractGraphicsConfiguration {
	final Logger logger = Logger.getLogger(CyUserLog.NAME);
	private final String NAME = "ThumbnailGraphicsConfiguration";
	
	public ThumbnailGraphicsConfiguration() {			
		
	}	
	
	@Override
	public String toString() {
		return NAME;
	}
}
