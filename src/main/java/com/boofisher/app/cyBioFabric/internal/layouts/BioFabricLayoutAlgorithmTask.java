package com.boofisher.app.cyBioFabric.internal.layouts;

import java.util.List;
import java.util.Set;

import com.boofisher.app.cyBioFabric.CommandSet;
import com.boofisher.app.cyBioFabric.internal.CyActivator;
import org.apache.log4j.Logger;
import org.cytoscape.application.CyUserLog;
import org.cytoscape.event.CyEventHelper;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.layout.AbstractLayoutTask;
import org.cytoscape.view.layout.LayoutPartition;
import org.cytoscape.view.layout.PartitionUtil;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.undo.UndoSupport;

public class BioFabricLayoutAlgorithmTask extends AbstractLayoutTask {
	final Logger logger = Logger.getLogger(CyUserLog.NAME);		
	
	public BioFabricLayoutAlgorithmTask(String name, CyNetworkView networkView, Set<View<CyNode>> nodesToLayOut,
			String layoutAttribute, UndoSupport undo) {
		super(name, networkView, nodesToLayOut, layoutAttribute, undo);				
	}

	@Override
	protected void doLayout(TaskMonitor taskMonitor) {
		
		 /* Partition the graph -- this builds the LayoutEdge and LayoutNode
		  * arrays as a byproduct. TODO: find out if this is needed*/
		List<LayoutPartition> layoutPartitions = PartitionUtil.partition(networkView, false, null);
		
		CommandSet onCommand = new CommandSet();				
		
		//TODO: revisit this
		CyActivator.setBioFabricNetwork(onCommand.loadDataFromCytoscape(networkView));				
	}		
}


/*
 * https://groups.google.com/forum/#!searchin/cytoscape-discuss/coordinate%7Csort:date/cytoscape-discuss/J7YUIJqgtIw/JE5vUEwxhX4J
 * https://groups.google.com/forum/#!searchin/cytoscape-discuss/order$20in$20table$20nodes/cytoscape-discuss/8DL0qfd-vXo/aSFn7-OikK8J
 * 
 * https://groups.google.com/forum/#!searchin/cytoscape-discuss/sort$20nodes/cytoscape-discuss/hEgk2zIgFLI/biS2n73aRagJ
public TaskIterator createTaskIterator(CyNetworkView networkView, Object context, Set<View<CyNode>> nodesToLayOut, String attrName) {



final CustomLayoutContext myContext = (CustomLayoutContext) context;

final CyNetwork network = applicationManager.getCurrentNetwork();


final CyTable nodeTable = network.getDefaultNodeTable();

final CyColumn pos = nodeTable.getColumn("position");

final List<Double> posVals = pos.getValues(Double.class);




Task task = new AbstractLayoutTask(toString(), networkView, nodesToLayOut, attrName, undoSupport) {

protected void doLayout(TaskMonitor taskMonitor) {

double curX = 0.0;

double curY = 0.0;

double mult = 100.0;

double nodePos = 0.0;

int counter = 0;

String name = "";




final VisualProperty<Double> xLoc = BasicVisualLexicon.NODE_X_LOCATION;

final VisualProperty<Double> yLoc = BasicVisualLexicon.NODE_Y_LOCATION;




//lays out nodes in a vertical line based on the position value in the node table

for (final View<CyNode> nView: nodesToLayOut) {

CyRow row = nodeTable.getRow(nView);

nodePos = row.get("position", Double.class);

nodePos = posVals.get(counter);

curX = 0.0;

curY = nodePos * mult;

nView.setVisualProperty(xLoc, curX);

nView.setVisualProperty(yLoc, curY);

counter++;

}

}

};


return new TaskIterator(task);

}

In the CyActivator class, this is the code that registers the service:

CyApplicationManager setAppMgr = getService(bc, CyApplicationManager.class);

UndoSupport setUndo = getService(bc, UndoSupport.class);

LayoutCustomNodes layoutCustomNodes = new LayoutCustomNodes(setUndo, setAppMgr);



Properties setProps = new Properties();

setProps.setProperty(PREFERRED_MENU, "Apps.Samples");

setProps.setProperty(TITLE, "Layout Set Nodes");

registerService(bc, layoutCustomNodes, CyLayoutAlgorithm.class, setProps);


CyLayoutAlgorithmManager layoutManager = getService(bc, CyLayoutAlgorithmManager.class);

TunableSetter tunableSetter = getService(bc, TunableSetter.class);

ApplySetLayoutTaskFactory applySetTF = new ApplySetLayoutTaskFactory(layoutManager, tunableSetter);


Properties applyCustomLayoutProperties = new Properties();

applyCustomLayoutProperties.setProperty(PREFERRED_MENU, "Apps.Samples");

applyCustomLayoutProperties.setProperty(TITLE, "Apply Custom Layout");

registerService(bc, applySetTF, NetworkViewTaskFactory.class, applyCustomLayoutProperties);

*/
