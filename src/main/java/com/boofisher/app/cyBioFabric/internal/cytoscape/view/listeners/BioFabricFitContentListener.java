package com.boofisher.app.cyBioFabric.internal.cytoscape.view.listeners;

import javax.swing.SwingUtilities;

import com.boofisher.app.cyBioFabric.internal.biofabric.cmd.CommandSet;

public class BioFabricFitContentListener implements BioFabricFitContentListenerInterface{

	private final String COMMAND_SET_NAME;
	
	public BioFabricFitContentListener(String name){
		
		this.COMMAND_SET_NAME = name;
		
	}
	
	@Override
	public void performFitContent() {
		
		//if it's not gettng called from UI thread then ignore
		if(SwingUtilities.isEventDispatchThread()){
			//command name is a unique name for each CommandSet created			
		    CommandSet fc = CommandSet.getCmds(COMMAND_SET_NAME);
		    fc.getAction(CommandSet.ZOOM_TO_MODEL, false, null).actionPerformed(null);
		}else{
			SwingUtilities.invokeLater(new Runnable(){

				@Override
				public void run() {						
					CommandSet fc = CommandSet.getCmds(COMMAND_SET_NAME);
				    fc.getAction(CommandSet.ZOOM_TO_MODEL, false, null).actionPerformed(null);						
				}					
			});
		}		
	}
}
