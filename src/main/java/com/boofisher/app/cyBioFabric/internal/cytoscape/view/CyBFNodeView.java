package com.boofisher.app.cyBioFabric.internal.cytoscape.view;


import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;

public class CyBFNodeView extends CyBFView<CyNode> {

	private final CyNode node;
	
	public CyBFNodeView(DefaultValueVault defaultValueVault, CyNode node) {
		super(defaultValueVault);
		this.node = node;
	}
	
	@Override
	public CyNode getModel() {
		return node;
	}

	@Override
	public <T> T getVisualProperty(VisualProperty<T> visualProperty) {
		T value = super.getVisualProperty(visualProperty);
		
		/*
		 * Its very common for nodes in 2D networks to have a zero value for BasicVisualLexicon.NODE_DEPTH.
		 * This makes the nodes look flat when rendered in SR and causes problems
		 * with layout algorithms. The cheap solution is to override the depth to be equal to width
		 * when the depth value is zero.
		 */
		if(visualProperty.equals(BasicVisualLexicon.NODE_DEPTH) && value instanceof Double && ((Double)value).doubleValue() == 0.0) {
			return (T) getVisualProperty(BasicVisualLexicon.NODE_WIDTH);
		}
		
		return value;
	}
	
}