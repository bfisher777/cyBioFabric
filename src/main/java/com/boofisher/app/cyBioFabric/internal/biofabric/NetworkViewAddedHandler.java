package com.boofisher.app.cyBioFabric.internal.biofabric;

import java.util.HashMap;
import java.util.Map;

import com.boofisher.app.cyBioFabric.internal.biofabric.app.BioFabricApplication;

public class NetworkViewAddedHandler {
	
	Map<Long, BioFabricApplication> apps;
	
	public NetworkViewAddedHandler(){
		 apps = new HashMap<Long, BioFabricApplication>();
		
	}
	
	public void registerApplication(Long id, BioFabricApplication bfa){
		apps.put(id, bfa);
	}
	
	public void handleEvent(long id){
		BioFabricApplication bfa = apps.get(id);
				
		if(bfa != null){
			
		}
	}

}
