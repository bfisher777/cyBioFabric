package com.boofisher.app.cyBioFabric.internal.biofabric;

import java.util.HashMap;
import java.util.Map;

import org.cytoscape.application.events.SetCurrentRenderingEngineEvent;
import org.cytoscape.view.layout.CyLayoutAlgorithm;
import org.cytoscape.view.layout.CyLayoutAlgorithmManager;
import org.cytoscape.view.presentation.events.RenderingEngineAboutToBeRemovedEvent;
import org.cytoscape.view.presentation.events.RenderingEngineAddedEvent;

import com.boofisher.app.cyBioFabric.internal.CyBFNetworkViewRenderer;
import com.boofisher.app.cyBioFabric.internal.CyBFRenderingEngine;
import com.boofisher.app.cyBioFabric.internal.biofabric.app.BioFabricApplication;
import com.boofisher.app.cyBioFabric.internal.layouts.BioFabricLayoutInterface;
import com.boofisher.app.cyBioFabric.internal.layouts.DefaultBioFabricLayoutAlgorithm;

public class BioFabricSetCurrentRenderingEngineHandler {
	CyLayoutAlgorithmManager layoutAlgorithmManager;
	CyLayoutAlgorithm defaultLayoutStorage;//used to store layout algorithm
	
	public BioFabricSetCurrentRenderingEngineHandler(CyLayoutAlgorithmManager manager){
		 this.layoutAlgorithmManager = manager;
		 defaultLayoutStorage = manager.getDefaultLayout();
		
	}	
	
	/* Handle the rendering engine change event, store default layout and use a 
	 * BioFabricLayout instead
	** 
	** @param id */
	public void handleEvent(SetCurrentRenderingEngineEvent e){
		System.out.println("Renderer is changing");	
		
		if(e.getRenderingEngine() == null){
		//do nothing
		//else if changing to a biofabric renderer
		}else if(e.getRenderingEngine().getRendererId().equals(CyBFNetworkViewRenderer.ID)){
			System.out.println(" Should be a BioFabric renderer: " + e.getRenderingEngine().getRendererId());	
			if(!(layoutAlgorithmManager.getDefaultLayout() instanceof BioFabricLayoutInterface)){
				//defaultLayoutStorage is an instance of a biofabric layout
				if(defaultLayoutStorage instanceof BioFabricLayoutInterface){
					layoutAlgorithmManager.setDefaultLayout(defaultLayoutStorage);
				}else {
					CyLayoutAlgorithm layout = layoutAlgorithmManager.getLayout("default-biofabric-layout");
					defaultLayoutStorage = layoutAlgorithmManager.getDefaultLayout(); 
					layoutAlgorithmManager.setDefaultLayout(layout);
				}
			}
	    //else changing to a non-biofabric renderer	
		}else{
			System.out.println(" Should be a non BioFabric renderer: " + e.getRenderingEngine().getRendererId());			
			//handle case where we are changing to a non BioFabric renderer with BioFabirc default layout
			if(layoutAlgorithmManager.getDefaultLayout()instanceof BioFabricLayoutInterface){				
				//defaultLayoutStorage is not an instance of a biofabric layout
				if(!(defaultLayoutStorage instanceof BioFabricLayoutInterface)){
					layoutAlgorithmManager.setDefaultLayout(defaultLayoutStorage);
					System.out.println("default layout has been set to " + defaultLayoutStorage.getName());
				}else {
					CyLayoutAlgorithm layout = layoutAlgorithmManager.getLayout("force-directed");
					defaultLayoutStorage = layoutAlgorithmManager.getDefaultLayout(); 
					layoutAlgorithmManager.setDefaultLayout(layout);
				}
			}
		}						
	}
}
