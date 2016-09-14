package org.systemsbiology.cyBioFabric.internal.layouts;

import java.util.Set;

import org.apache.log4j.Logger;
import org.cytoscape.application.CyUserLog;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.layout.AbstractLayoutTask;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.undo.UndoSupport;
import org.systemsbiology.cyBioFabric.internal.CyBFNetworkViewRenderer;
import org.systemsbiology.cyBioFabric.internal.biofabric.model.BioFabricNetwork;
import org.systemsbiology.cyBioFabric.internal.cytoscape.view.BNVisualPropertyValue;
import org.systemsbiology.cyBioFabric.internal.cytoscape.view.BioFabricVisualLexicon;

public class DefaultBioFabricLayoutAlgorithmTask extends AbstractLayoutTask {
	final Logger logger = Logger.getLogger(CyUserLog.NAME);		
	
	public DefaultBioFabricLayoutAlgorithmTask(String name, CyNetworkView networkView, Set<View<CyNode>> nodesToLayOut,
			String layoutAttribute, UndoSupport undo) {
		super(name, networkView, nodesToLayOut, layoutAttribute, undo);				
	}

	@Override
	protected void doLayout(TaskMonitor taskMonitor) {		
		BNVisualPropertyValue bnvp = networkView.getVisualProperty(BioFabricVisualLexicon.BIOFABRIC_NETWORK);
		
		DefaultBioFabricLayoutBuildTool onCommand = new DefaultBioFabricLayoutBuildTool();				
		
		//TODO: revisit this		
		BioFabricNetwork bfn = onCommand.loadDataFromCytoscape(networkView);
		bnvp.setBioFabricNetwork(bfn);				
	}		
}

