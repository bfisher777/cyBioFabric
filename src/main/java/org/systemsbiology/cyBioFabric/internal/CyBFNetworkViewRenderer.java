package org.systemsbiology.cyBioFabric.internal;

import org.cytoscape.application.NetworkViewRenderer;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.presentation.RenderingEngineFactory;

public class CyBFNetworkViewRenderer implements NetworkViewRenderer {

	public static final String ID = "org.systemsbiology.biofabric";
	public static final String DISPLAY_NAME = "CyBioFabric";
	
	private final CyNetworkViewFactory networkViewFactory;
	private final RenderingEngineFactory<CyNetwork> mainFactory;
	private final RenderingEngineFactory<CyNetwork> birdsEyeFactory;
	private final RenderingEngineFactory<CyNetwork> thumbnailFactory;
	
	public CyBFNetworkViewRenderer(CyNetworkViewFactory networkViewFactory, 
			                       RenderingEngineFactory<CyNetwork> mainFactory, 
			                       RenderingEngineFactory<CyNetwork> birdsEyeFactory,
			                       RenderingEngineFactory<CyNetwork> thumbnailFactory) {
		
		this.networkViewFactory = networkViewFactory;
		this.mainFactory = mainFactory;
		this.birdsEyeFactory = birdsEyeFactory;
		this.thumbnailFactory = thumbnailFactory;		
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
			case DEFAULT_CONTEXT:				
				return mainFactory;
			case BIRDS_EYE_CONTEXT:				
				return birdsEyeFactory;
			case THUMBNAIL_CONTEXT:				
				return thumbnailFactory;
			case VISUAL_STYLE_PREVIEW_CONTEXT: 
				return null;
			default: 				
				return null;
		}
	}

	@Override
	public String toString() {
		return DISPLAY_NAME;
	}
}

