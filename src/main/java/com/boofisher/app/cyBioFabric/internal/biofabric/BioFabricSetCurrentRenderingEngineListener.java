package com.boofisher.app.cyBioFabric.internal.biofabric;

import javax.swing.SwingUtilities;

import org.cytoscape.application.events.CyShutdownEvent;
import org.cytoscape.application.events.SetCurrentRenderingEngineEvent;
import org.cytoscape.application.events.SetCurrentRenderingEngineListener;
import org.cytoscape.view.presentation.events.RenderingEngineAboutToBeRemovedEvent;
import org.cytoscape.view.presentation.events.RenderingEngineAboutToBeRemovedListener;
import org.cytoscape.view.presentation.events.RenderingEngineAddedEvent;
import org.cytoscape.view.presentation.events.RenderingEngineAddedListener;

public class BioFabricSetCurrentRenderingEngineListener implements SetCurrentRenderingEngineListener{
	private BioFabricSetCurrentRenderingEngineHandler rendererChangedHandler;
	
	public BioFabricSetCurrentRenderingEngineListener(BioFabricSetCurrentRenderingEngineHandler rendererChangedHandler){
		this.rendererChangedHandler = rendererChangedHandler;
	}
	
	@Override
	public void handleEvent(SetCurrentRenderingEngineEvent e) {
		
		if(SwingUtilities.isEventDispatchThread()){
			rendererChangedHandler.handleEvent(e);
		}else{
			SwingUtilities.invokeLater(new Runnable(){

				@Override
				public void run() {						
					rendererChangedHandler.handleEvent(e);						
				}					
			});
		}
		
	}

}
