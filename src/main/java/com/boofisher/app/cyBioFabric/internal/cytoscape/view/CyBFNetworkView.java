package com.boofisher.app.cyBioFabric.internal.cytoscape.view;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.boofisher.app.cyBioFabric.internal.CyBFNetworkViewRenderer;
import com.boofisher.app.cyBioFabric.internal.biofabric.app.BioFabricApplication;
import com.boofisher.app.cyBioFabric.internal.cytoscape.view.CyBFEdgeView;
import com.boofisher.app.cyBioFabric.internal.cytoscape.view.CyBFNodeView;
import com.boofisher.app.cyBioFabric.internal.cytoscape.view.CyBFView;
import com.boofisher.app.cyBioFabric.internal.cytoscape.view.DefaultValueVault;
import com.boofisher.app.cyBioFabric.internal.cytoscape.view.listeners.ApplyPreferredLayoutListenerInterface;
import com.boofisher.app.cyBioFabric.internal.cytoscape.view.listeners.BioFabricFitContentListenerInterface;
import com.boofisher.app.cyBioFabric.internal.cytoscape.view.listeners.BioFabricViewListenerInterface;
import com.boofisher.app.cyBioFabric.internal.cytoscape.view.listeners.BioFabricZoomAllNetworkListenerInterface;
import com.boofisher.app.cyBioFabric.internal.cytoscape.view.listeners.BioFabricZoomInListenerInterface;
import com.boofisher.app.cyBioFabric.internal.cytoscape.view.listeners.BioFabricZoomOutListenerInterface;
import com.boofisher.app.cyBioFabric.internal.cytoscape.view.listeners.BioFabricZoomSelectedListenerInterface;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyle;
/*
 * Used to create view model
 * */
public class CyBFNetworkView extends CyBFView<CyNetwork> implements CyNetworkView {

	private final CyNetwork network;
	
	private final VisualLexicon visualLexicon;
	
	private ArrayList<BioFabricViewListenerInterface> bioFabricViewListeners;
	
	/*
	 * This object (VisualMappingManager) manages mapping from view
	 * model to VisualStyle. User objects can access all VisualStyles and
	 * VisualMappingFunctions through this class.
	 * */
	private final VisualMappingManager visualMappingManager;
	
	private BioFabricApplication bioFabricApplication;	
	/**
	 * The camera associated with the main network viewing window used to
	 * perform operations such as fitting all nodes onto the screen
	 * 
	 */
	private List<Component> canvases = new ArrayList<>(2);
	
	// Assumes indices of nodes are unique
	private Map<Long, View<CyNode>> nodeViews;
	private Map<Long, View<CyEdge>> edgeViews;
	
	private boolean doFitContent;	
	//keeping track of last networkScaleFactor
	private double  networkScaleFactor;	
	
	public CyBFNetworkView(CyNetwork network, VisualLexicon visualLexicon, VisualMappingManager visualMappingManager, int appNum) {
		super(new DefaultValueVault(visualLexicon));
		
		this.network = network;
		this.visualLexicon = visualLexicon;
		this.visualMappingManager = visualMappingManager;				
		
		nodeViews = new HashMap<>();
		edgeViews = new HashMap<>();
		
		for (CyNode node : network.getNodeList()) {
			CyBFNodeView nodeView = new CyBFNodeView(defaultValues, node);
			nodeViews.put(node.getSUID(), nodeView);
		}
		
		for (CyEdge edge : network.getEdgeList()) {
			CyBFEdgeView edgeView = new CyBFEdgeView(defaultValues, edge);
			edgeViews.put(edge.getSUID(), edgeView);
		}
		
		this.bioFabricViewListeners = new ArrayList<BioFabricViewListenerInterface>();
		this.networkScaleFactor = this.getVisualProperty(BasicVisualLexicon.NETWORK_SCALE_FACTOR);
		//the number of biofabric applications created giving a unique number to each application
		this.bioFabricApplication = new BioFabricApplication(false, appNum);
	}
	
	public BioFabricApplication getBioFabricApplication(){ return bioFabricApplication; }
	
	public void addBioFabricViewListener(BioFabricViewListenerInterface viewListener){
		this.bioFabricViewListeners.add(viewListener);
	}
	
	@Override
	public CyNetwork getModel() {
		return network;
	}

	@Override
	public View<CyNode> getNodeView(CyNode node) {
		return nodeViews.get(node.getSUID());
	}

	@Override
	public Collection<View<CyNode>> getNodeViews() {
		return nodeViews.values();
	}

	@Override
	public View<CyEdge> getEdgeView(CyEdge edge) {
		return edgeViews.get(edge.getSUID());
	}

	@Override
	public Collection<View<CyEdge>> getEdgeViews() {
		return edgeViews.values();
	}

	@Override
	public Collection<View<? extends CyIdentifiable>> getAllViews() {
		Collection<View<? extends CyIdentifiable>> views = new HashSet<>();
		views.addAll(getNodeViews());
		views.addAll(getEdgeViews());
		views.add(this);
		return views;
	}

	/**
	 * Center the network
	 */
	@Override
	public void fitContent() {
		this.doFitContent = true;
		updateView();
	}

	@Override
	public void fitSelected() {
		// Obtain selected nodes
		Set<View<CyNode>> selectedNodeViews = new HashSet<View<CyNode>>();
		
		for (View<CyNode> nodeView : getNodeViews()) {
			if (nodeView.getVisualProperty(BasicVisualLexicon.NODE_SELECTED)) {
				selectedNodeViews.add(nodeView);
			}
		}
		
		if (selectedNodeViews.isEmpty()) {
			return;
		}
		
		// MKTODO 
//		if (networkCamera != null) {
//			NetworkToolkit.fitInView(networkCamera, selectedNodeViews, 180.0, 2.3, 1.8);
//		}
		
		// Request focus for the network view to be ready for keyboard input
//		requestNetworkFocus();
	}

	@Override
	public void updateView() {
		System.out.println("updateView has been called in CyBFNetworkView");		
		matchNodes();
		matchEdges();				
		
		for(int i = 0; i < canvases.size(); i++) {
			canvases.get(i).repaint();
		}
		
		for(BioFabricViewListenerInterface bFVL : this.bioFabricViewListeners){			
			fireAway(bFVL);
		}
	}
	
	// Checks if there is a discrepancy between number of nodes and nodeViews, attempts
	// to fix discrepancy by removing extra views and adding missing views
	// TODO: Currently considers the set of views to be OK if node and nodeView counts match,
	// does not check if there is an actual 1:1 relationship
	private void matchNodes() {
		int nodeCountDifference = network.getNodeCount() - nodeViews.size();
		
		// Check if nodes have been added to the network
		if (nodeCountDifference > 0) {
			for (CyNode node : network.getNodeList()) {
				
				// Found a node without a view?
				if (nodeViews.get(node.getSUID()) == null) {
					
					CyBFNodeView nodeView = new CyBFNodeView(defaultValues, node);
					
					nodeViews.put(node.getSUID(), nodeView);
					
					nodeCountDifference--;
				}
			}
			
			// Did we fail to match every node with a node view?
			if (nodeCountDifference != 0) {
				
				// TODO: Use exception
				System.out.println("WindNetworkView.matchNodes(): node count mismatch by " + nodeCountDifference);
			}
		// Check if nodes have been removed from the network
		} else if (nodeCountDifference < 0) {
			long nodeIndex;
			HashSet<Long> toBeRemovedIndices = new HashSet<>();
			
			for (View<CyNode> nodeView : nodeViews.values()) {
				
				nodeIndex = nodeView.getModel().getSUID();
				
				// TODO: Currently performs check by checking if the view's node index is still valid
				if (network.getNode(nodeIndex) == null) {
					toBeRemovedIndices.add(nodeIndex);
				}
			}
			
			for (Long index : toBeRemovedIndices) {
				nodeViews.remove(index);
			}
		}
	}
	
	private void matchEdges() {
		int edgeCountDifference = network.getEdgeCount() - edgeViews.size();
		
		// Check if nodes have been added to the network
		if (edgeCountDifference > 0) {
			for (CyEdge edge : network.getEdgeList()) {
				
				// Found a edge without a view?
				if (edgeViews.get(edge.getSUID()) == null) {
					
					CyBFEdgeView edgeView = new CyBFEdgeView(defaultValues, edge);
					
					edgeViews.put(edge.getSUID(), edgeView);
					
					edgeCountDifference--;
				}
			}
			
			// Did we fail to match every edge with a edge view?
			if (edgeCountDifference != 0) {
				
				// TODO: Use exception
				System.out.println("WindNetworkView.matchEdges(): edge count mismatch by " + edgeCountDifference);
			}
		// Check if edges have been removed from the network
		} else if (edgeCountDifference < 0) {
			Long edgeIndex;
			HashSet<Long> toBeRemovedIndices = new HashSet<>();
			
			for (View<CyEdge> edgeView : edgeViews.values()) {
				
				edgeIndex = edgeView.getModel().getSUID();
				
				// TODO: Currently performs check by checking if the view's edge index is still valid
				if (network.getEdge(edgeIndex) == null) {
					toBeRemovedIndices.add(edgeIndex);
				}
			}
			
			for (Long index : toBeRemovedIndices) {
				edgeViews.remove(index);
			}
		}
	}

	private void updateToMatchVisualStyle() {
		
		// TODO: Make the set declared below a private member field, formalize the set of node or edge specific visual properties
		// that do not need to be matched with visual style changes, such as 3D position.
		
		// These visual properties are object-specific such as x, y, z coordinates
		// and do not need to be updated according to the visual style
		Set<VisualProperty<?>> exemptProperties = new HashSet<VisualProperty<?>>();
		exemptProperties.add(BasicVisualLexicon.NODE_X_LOCATION);
		exemptProperties.add(BasicVisualLexicon.NODE_Y_LOCATION);
		exemptProperties.add(BasicVisualLexicon.NODE_Z_LOCATION);
		
		// Update visual properties according to the current visual style
		VisualStyle visualStyle = visualMappingManager.getVisualStyle(this);
		
		for (View<? extends CyIdentifiable> view : getAllViews()) {
			for (VisualProperty<?> visualProperty : visualLexicon.getAllVisualProperties()) {
				if (view.getVisualProperty(visualProperty) != null 
						&& visualStyle.getDefaultValue(visualProperty) != null
						&& visualStyle.getVisualMappingFunction(visualProperty) == null
						&& !exemptProperties.contains(visualProperty)) {
					view.setVisualProperty(visualProperty, (Object) visualStyle.getDefaultValue(visualProperty));
				}
			}
		}
	}
	
	@Override
	public <T, V extends T> void setViewDefault(VisualProperty<? extends T> visualProperty, V defaultValue) {
		defaultValues.modifyDefaultValue(visualProperty, defaultValue);
	}

	
	@Override
	public <T> T getVisualProperty(VisualProperty<T> visualProperty) {
		T value = super.getVisualProperty(visualProperty);
		
		if (value != null) {
			// If we were given an explicit value, return it
			return value;
		} else {
			// Otherwise, return the default value
			return defaultValues.getDefaultValue(visualProperty);
		}
	}


	public void addContainer(Component container) {
		canvases.add(container);
	}
	
	
//	/**
//	 * Requests focus for this network view so that it is ready to accept mouse and keyboard input.
//	 */
//	private void requestNetworkFocus() {
//		if (container != null) {
//			container.requestFocus();
//		}
//	}

	@Override
	public void dispose() {
	}

	@Override
	public String getRendererId() {
		return CyBFNetworkViewRenderer.ID;
	}
	
	private void fireAway(BioFabricViewListenerInterface bFVL){
				
		if((bFVL instanceof BioFabricZoomInListenerInterface) && zoomInChanged()){			
			
			((BioFabricZoomInListenerInterface)bFVL).performZoomIn();
			
		}else if((bFVL instanceof BioFabricZoomOutListenerInterface) && zoomOutChanged()){			
			
			((BioFabricZoomOutListenerInterface)bFVL).performZoomOut();
			
		}else if((bFVL instanceof ApplyPreferredLayoutListenerInterface) && refreshChanged()){
			
			((ApplyPreferredLayoutListenerInterface)bFVL).performApplyLayout();
			
		}else if((bFVL instanceof BioFabricFitContentListenerInterface) && fitContentChanged()){
			
			((BioFabricFitContentListenerInterface)bFVL).performFitContent();
			
		}else if((bFVL instanceof BioFabricZoomAllNetworkListenerInterface) && zoomAllNetworkChanged()){
			
			((BioFabricZoomAllNetworkListenerInterface)bFVL).performZoomAllNetwork();
			
		}else if((bFVL instanceof BioFabricZoomSelectedListenerInterface) && zoomSelectedChanged()){
			
			((BioFabricZoomSelectedListenerInterface)bFVL).performZoomSelected();
			
		}
	}

	private boolean zoomInChanged(){
		double zoom = this.getVisualProperty(BioFabricVisualLexicon.NETWORK_SCALE_FACTOR);			
		if(zoom > networkScaleFactor){
			networkScaleFactor = zoom;
			return true;
		}else{		
			return false;
		}
	}
	
	private boolean zoomOutChanged(){
		double zoom = this.getVisualProperty(BioFabricVisualLexicon.NETWORK_SCALE_FACTOR);
		if(zoom < networkScaleFactor){
			networkScaleFactor = zoom;
			return true;
		}else{		
			return false;
		}
	}
	
	private boolean refreshChanged(){
		return false;
	}
	
	private boolean fitContentChanged(){
		if(this.doFitContent){
			this.doFitContent = false;
			return true;
		}else{
			return false;
		}
	}
	
	private boolean zoomAllNetworkChanged(){
		return false;
	}
	
	private boolean zoomSelectedChanged(){
		return false;
	}		
}
