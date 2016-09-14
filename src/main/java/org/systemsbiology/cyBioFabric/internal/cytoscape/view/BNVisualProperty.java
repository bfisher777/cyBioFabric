package org.systemsbiology.cyBioFabric.internal.cytoscape.view;


import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.view.model.AbstractVisualProperty;
import org.cytoscape.view.model.DiscreteRange;
import com.google.common.collect.Sets;

/*
 * Class represents a biofabic network visual property added to the BioFabricVisualLexicon
 *
 * */
public class BNVisualProperty extends AbstractVisualProperty<BNVisualPropertyValue> {

	public static final BNVisualPropertyValue BFN  = new BNVisualPropertyValue("BioFabricNetwork", "BIOFABRIC_NETWORK");
	
	public static final DiscreteRange<BNVisualPropertyValue> RANGE =  new DiscreteRange<>(BNVisualPropertyValue.class, Sets.newHashSet(BFN));
	
	public BNVisualProperty(String id, String displayName, Class<? extends CyIdentifiable> targetObjectDataType) {
		super(BFN, RANGE, id, displayName, targetObjectDataType);
	}

	@Override
	public String toSerializableString(BNVisualPropertyValue value) {
		return value.getSerializableString();
	}

	@Override
	public BNVisualPropertyValue parseSerializableString(String value) {
		for(BNVisualPropertyValue detailLevel : RANGE.values()) {
			if (detailLevel.getSerializableString().equalsIgnoreCase(value)) {
				return detailLevel;
			}
		}
		return null;
	}

}
