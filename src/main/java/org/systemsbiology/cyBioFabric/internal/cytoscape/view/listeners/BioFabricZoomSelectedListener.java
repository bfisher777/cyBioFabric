package org.systemsbiology.cyBioFabric.internal.cytoscape.view.listeners;

import javax.swing.SwingUtilities;

import org.systemsbiology.cyBioFabric.internal.biofabric.cmd.CommandSet;

public class BioFabricZoomSelectedListener implements BioFabricZoomSelectedListenerInterface{
	

	private String COMMAND_SET_NAME;
	
	public BioFabricZoomSelectedListener(String name){
		
		this.COMMAND_SET_NAME = name;
		
	}
	
	@Override
	public void performZoomSelected() {
		
		//if it's not gettng called from UI thread then ignore
		if(SwingUtilities.isEventDispatchThread()){
			//command name is a unique name for each CommandSet created
		    CommandSet fc = CommandSet.getCmds(COMMAND_SET_NAME);
		    fc.getAction(CommandSet.ZOOM_TO_SELECTIONS, false, null).actionPerformed(null);
		}else{
			SwingUtilities.invokeLater(new Runnable(){

				@Override
				public void run() {						
					//command name is a unique name for each CommandSet created
				    CommandSet fc = CommandSet.getCmds(COMMAND_SET_NAME);
				    fc.getAction(CommandSet.ZOOM_TO_SELECTIONS, false, null).actionPerformed(null);				
				}					
			});
		}
	}

}
