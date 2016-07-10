package com.boofisher.app.cySimpleRenderer.internal;

import org.cytoscape.application.NetworkViewRenderer;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.presentation.RenderingEngineFactory;

public class CySRNetworkViewRenderer implements NetworkViewRenderer {

	public static final String ID = "com.boofisher.app";
	public static final String DISPLAY_NAME = "CySR";
	
	private final CyNetworkViewFactory networkViewFactory;
	private final RenderingEngineFactory<CyNetwork> mainFactory;
	private final RenderingEngineFactory<CyNetwork> birdsEyeFactory;
	
	public CySRNetworkViewRenderer(CyNetworkViewFactory networkViewFactory, 
			                       RenderingEngineFactory<CyNetwork> mainFactory, 
			                       RenderingEngineFactory<CyNetwork> birdsEyeFactory) {
		
		this.networkViewFactory = networkViewFactory;
		this.mainFactory = mainFactory;
		this.birdsEyeFactory = birdsEyeFactory;
	}
	
	
	@Override
	public String getId() {
		return ID;
	}

	@Override
	public CyNetworkViewFactory getNetworkViewFactory() {
		return networkViewFactory;
	}

	@Override
	public RenderingEngineFactory<CyNetwork> getRenderingEngineFactory(String context) {
		switch(context) {
			case DEFAULT_CONTEXT: return mainFactory;
			case BIRDS_EYE_CONTEXT: return birdsEyeFactory;
			case THUMBNAIL_CONTEXT: return null;
			case VISUAL_STYLE_PREVIEW_CONTEXT: return null;
			default: return null;
		}
	}

	@Override
	public String toString() {
		return DISPLAY_NAME;
	}
}

