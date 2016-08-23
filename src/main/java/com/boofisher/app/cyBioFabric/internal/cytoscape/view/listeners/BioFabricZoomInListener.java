package com.boofisher.app.cyBioFabric.internal.cytoscape.view.listeners;

import javax.swing.SwingUtilities;

import com.boofisher.app.cyBioFabric.internal.biofabric.cmd.CommandSet;

public class BioFabricZoomInListener implements BioFabricZoomInListenerInterface{

	private final String COMMAND_SET_NAME;
	
	public BioFabricZoomInListener(String name){
		
		this.COMMAND_SET_NAME = name;
		
	}
	
	@Override
	public void performZoomIn() {
		
		//if it's not gettng called from UI thread then ignore
		if(SwingUtilities.isEventDispatchThread()){
			//command name is a unique name for each CommandSet created
		    CommandSet fc = CommandSet.getCmds(COMMAND_SET_NAME);
		    fc.getAction(CommandSet.ZOOM_IN, true, null).actionPerformed(null);
		}else{
			SwingUtilities.invokeLater(new Runnable(){

				@Override
				public void run() {						
					//command name is a unique name for each CommandSet created
				    CommandSet fc = CommandSet.getCmds(COMMAND_SET_NAME);
				    fc.getAction(CommandSet.ZOOM_IN, true, null).actionPerformed(null);					
				}					
			});
		}
	}

}
