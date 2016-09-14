package org.systemsbiology.cyBioFabric.internal.events;

import javax.swing.SwingUtilities;

import org.cytoscape.model.events.RowsSetEvent;
import org.cytoscape.model.events.RowsSetListener;

public class BioFabricRowSetListener implements RowsSetListener{

	BioFabricRowSetHandler rowSetHandler;
	
	public BioFabricRowSetListener(BioFabricRowSetHandler rowSetHandler){
		this.rowSetHandler = rowSetHandler;
	}
	
	@Override
	public void handleEvent(RowsSetEvent e) {				
		if(SwingUtilities.isEventDispatchThread()){
			rowSetHandler.handleEvent();
		}else{
			SwingUtilities.invokeLater(new Runnable(){

				@Override
				public void run() {						
					rowSetHandler.handleEvent();						
				}					
			});
		}		
	}


}