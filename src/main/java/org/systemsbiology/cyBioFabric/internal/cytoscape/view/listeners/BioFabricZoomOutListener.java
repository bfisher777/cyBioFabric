package org.systemsbiology.cyBioFabric.internal.cytoscape.view.listeners;

import javax.swing.SwingUtilities;

import org.systemsbiology.cyBioFabric.internal.biofabric.cmd.CommandSet;

public class BioFabricZoomOutListener implements BioFabricZoomOutListenerInterface{

	private String COMMAND_SET_NAME;
	
	public BioFabricZoomOutListener(String name){
		
		this.COMMAND_SET_NAME = name;
		
	}
	
	@Override
	public void performZoomOut() {
		
		//if it's not gettng called from UI thread then ignore
		if(SwingUtilities.isEventDispatchThread()){
			//command name is a unique name for each CommandSet created
		    CommandSet fc = CommandSet.getCmds(COMMAND_SET_NAME);
		    fc.getAction(CommandSet.ZOOM_OUT, false, null).actionPerformed(null);
		}else{
			SwingUtilities.invokeLater(new Runnable(){

				@Override
				public void run() {						
					//command name is a unique name for each CommandSet created
				    CommandSet fc = CommandSet.getCmds(COMMAND_SET_NAME);
				    fc.getAction(CommandSet.ZOOM_OUT, false, null).actionPerformed(null);				
				}					
			});
		}
	}

}
