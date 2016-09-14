package org.systemsbiology.cyBioFabric.internal.icons;

import java.util.ArrayList;
import java.util.List;


import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;

public class BioFabricViewPropertySelectedPredicate extends BioFabricViewFactoryPredicate {	
		
		CyNetworkView networkView;
		
		public BioFabricViewPropertySelectedPredicate(){
			this.networkView = null;
		}


		@Override
		public boolean isReady() {
			boolean ready = false;
			if (isBioFabricView && networkView != null) {
				// Also check whether or not there is at least one hidden element...
				final List<View<? extends CyIdentifiable>> views = new ArrayList<>();
					
				views.addAll(networkView.getNodeViews());				
				views.addAll(networkView.getEdgeViews());
				
				for (View<? extends CyIdentifiable> v : views) {
					final VisualProperty<?> vp = v.getModel() instanceof CyNode ? 
							BasicVisualLexicon.NODE_SELECTED : BasicVisualLexicon.EDGE_SELECTED;
					
					if (v.getVisualProperty(vp) == Boolean.TRUE){
						ready = true;
						break;
					}
				}
			}								
			return ready;
		}	
		
		public void registerNetwork(CyNetworkView networkView){
			this.networkView = networkView;
		}
}
