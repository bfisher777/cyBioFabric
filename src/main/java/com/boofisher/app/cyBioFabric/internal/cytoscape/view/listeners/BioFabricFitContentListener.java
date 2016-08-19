package com.boofisher.app.cyBioFabric.internal.cytoscape.view.listeners;

import javax.swing.SwingUtilities;

import com.boofisher.app.cyBioFabric.internal.biofabric.app.BioFabricWindow;
import com.boofisher.app.cyBioFabric.internal.biofabric.cmd.CommandSet;

public class BioFabricFitContentListener implements BioFabricFitContentListenerInterface{

	private BioFabricWindow bfw;
	
	public BioFabricFitContentListener(BioFabricWindow bfw){
		
		this.bfw = bfw;
		
	}
	
	@Override
	public void performFitContent() {
		
		//if it's not gettng called from UI thread then ignore
		if(SwingUtilities.isEventDispatchThread()){
			//command name is a unique name for each CommandSet created			
		    CommandSet fc = CommandSet.getCmds(bfw.COMMAND_NAME);
		    fc.getAction(CommandSet.ZOOM_TO_MODEL, false, null).actionPerformed(null);
		}else{
			SwingUtilities.invokeLater(new Runnable(){

				@Override
				public void run() {						
					CommandSet fc = CommandSet.getCmds(bfw.COMMAND_NAME);
				    fc.getAction(CommandSet.ZOOM_TO_MODEL, false, null).actionPerformed(null);						
				}					
			});
		}		
	}
}
