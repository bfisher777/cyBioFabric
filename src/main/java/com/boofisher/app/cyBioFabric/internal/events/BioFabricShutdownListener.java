package com.boofisher.app.cyBioFabric.internal.events;

import javax.swing.SwingUtilities;

import org.cytoscape.application.events.CyShutdownEvent;
import org.cytoscape.application.events.CyShutdownListener;

public class BioFabricShutdownListener implements CyShutdownListener{

	private BioFabricShutdownHandler shutdownHandler;
	
	public BioFabricShutdownListener(BioFabricShutdownHandler shutdownHandler){
		this.shutdownHandler = shutdownHandler;
	}
	
	@Override
	public void handleEvent(CyShutdownEvent arg0) {
		
		if(SwingUtilities.isEventDispatchThread()){
			shutdownHandler.handleEvent();
		}else{
			SwingUtilities.invokeLater(new Runnable(){

				@Override
				public void run() {						
					shutdownHandler.handleEvent();						
				}					
			});
		}
		
	}

}
