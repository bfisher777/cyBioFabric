package org.systemsbiology.cyBioFabric.internal.events;

import java.util.HashMap;
import java.util.Map;

import org.systemsbiology.cyBioFabric.internal.biofabric.app.BioFabricApplication;

public class BioFabricShutdownHandler {
Map<Long, BioFabricApplication> apps;
	
	public BioFabricShutdownHandler(){
		 apps = new HashMap<Long, BioFabricApplication>();
		
	}
	
	public void registerApplication(Long id, BioFabricApplication bfa){
		apps.put(id, bfa);
	}
	
	
	/*
	 * http://stackoverflow.com/questions/46898/how-to-efficiently-iterate-over-each-entry-in-a-map
	 * Iterate through map and shutdown each application
	 * */
	public void handleEvent(){
		
		for(Map.Entry<Long, BioFabricApplication> entry : apps.entrySet()){
			BioFabricApplication bfa = entry.getValue();
			if(bfa != null){
				bfa.shutdownFabric();
				bfa = null;//allow for garbage collection
			}
		}
	}
}
