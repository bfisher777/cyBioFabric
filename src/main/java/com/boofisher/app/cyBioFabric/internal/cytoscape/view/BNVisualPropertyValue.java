package com.boofisher.app.cyBioFabric.internal.cytoscape.view;

import org.cytoscape.view.presentation.property.values.AbstractVisualPropertyValue;

import com.boofisher.app.cyBioFabric.internal.biofabric.model.BioFabricNetwork;


/*
 * Class represents a biofabric network in cytoscape can be accessed from the network view
 * */
public class BNVisualPropertyValue extends AbstractVisualPropertyValue {
	
	private BioFabricNetwork bfn;

	public BNVisualPropertyValue(String displayName, String serializableString) {
		super(displayName, serializableString);
		bfn = null;
	}
	
	public void setBioFabricNetwork(BioFabricNetwork bioFabricNetwork){
		this.bfn = bioFabricNetwork;
	}
	
	public BioFabricNetwork getBioFabricNetwork(){
		return this.bfn;
	}

}
