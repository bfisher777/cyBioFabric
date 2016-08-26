package com.boofisher.app.cyBioFabric.internal.cytoscape.view.listeners;

import javax.swing.SwingUtilities;

import org.cytoscape.view.layout.CyLayoutAlgorithm;
import org.cytoscape.view.layout.CyLayoutAlgorithmManager;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.swing.DialogTaskManager;

import com.boofisher.app.cyBioFabric.internal.cytoscape.view.CyBFNetworkView;

public class ApplyPreferredLayoutListener implements ApplyPreferredLayoutListenerInterface{

	private CyBFNetworkView cyBFViewModel;
	private CyLayoutAlgorithm layout;
	private CyLayoutAlgorithmManager layoutManager; 
	private DialogTaskManager taskManager;
	
	public ApplyPreferredLayoutListener(CyBFNetworkView cyBFViewModel, CyLayoutAlgorithm layout, CyLayoutAlgorithmManager layoutManager, 
			DialogTaskManager taskManager){
		
		this.cyBFViewModel = cyBFViewModel;
		this.layout = layout;
		this.layoutManager = layoutManager;
		this.taskManager = taskManager;
		
	}
	
	@Override
	public void performApplyLayout() {
		
		//if it's not gettng called from UI thread then ignore
		if(SwingUtilities.isEventDispatchThread()){
			runLayoutAlgorithm(cyBFViewModel, layout, layoutManager, taskManager);
		}else{
			SwingUtilities.invokeLater(new Runnable(){

				@Override
				public void run() {						
					runLayoutAlgorithm(cyBFViewModel, layout, layoutManager, taskManager);						
				}					
			});
		}		
	}

	//Programmatically run layout algorithm
	private void runLayoutAlgorithm(CyBFNetworkView cyBFViewModel, CyLayoutAlgorithm layout, CyLayoutAlgorithmManager layoutManager, 
			DialogTaskManager taskManager){
			
		if(layout == null){
			System.out.println("Attempting to apply preffered layout and layout is null");
			layout = layoutManager.getDefaultLayout();
		}

		// apply the layout
		TaskIterator taskIterator = layout.createTaskIterator(cyBFViewModel, layout.getDefaultLayoutContext(), CyLayoutAlgorithm.ALL_NODE_VIEWS, null);
		taskManager.execute(taskIterator);
	}

}
