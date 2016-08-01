package com.boofisher.app.cyBioFabric.internal.cytoscape.view;

import static java.util.Arrays.asList;
import static org.cytoscape.view.presentation.property.LineTypeVisualProperty.DOT;
import static org.cytoscape.view.presentation.property.LineTypeVisualProperty.EQUAL_DASH;
import static org.cytoscape.view.presentation.property.LineTypeVisualProperty.SOLID;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.ContinuousRange;
import org.cytoscape.view.model.DiscreteRange;
import org.cytoscape.view.model.NullDataType;
import org.cytoscape.view.model.Range;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.cytoscape.view.presentation.property.BooleanVisualProperty;
import org.cytoscape.view.presentation.property.IntegerVisualProperty;
import org.cytoscape.view.presentation.property.NullVisualProperty;
import org.cytoscape.view.presentation.property.StringVisualProperty;

public class BioFabricVisualLexicon extends BasicVisualLexicon{
	
	protected static final Range<Integer> ARBITRARY_INTEGER_RANGE = new ContinuousRange<>(Integer.class,
			Integer.MIN_VALUE, Integer.MAX_VALUE, true, true);

	/** The root visual property */
	public static final VisualProperty<NullDataType> ROOT = new NullVisualProperty( "CY_BIOFABRIC_ROOT", "cyBioFabric Rendering Engine Root Visual Property");
	

	/***************************************************************************************************************************
	 * BioFabric Network
	 * * ***********************************************************************************************************************/
	public static final VisualProperty<BNVisualPropertyValue> BIOFABRIC_NETWORK = new BNVisualProperty("BIOFABRIC_NETWORK", "BioFabric Network", CyNetwork.class);
	
	/***************************************************************************************************************************
	 * These data are found in PaintCache
	 * * ***********************************************************************************************************************/
	public static final VisualProperty<Integer> PAINT_CACHE_STROKE_SIZE = 
			new IntegerVisualProperty(0, ARBITRARY_INTEGER_RANGE, "PAINT_CACHE_STROKE_SIZE", "Paint Cache Stroke Size", CyNetwork.class);
	
	
	/***************************************************************************************************************************
	 * These data are found in BioFabricNetwork.NodeInfo
	 * * ***********************************************************************************************************************/
	 
	public static final VisualProperty<String> NODE_NAME = new StringVisualProperty("", ARBITRARY_STRING_RANGE,
			"NODE_NAME", "Node Name", true, CyNode.class);
	
	public static final VisualProperty<Integer> NODE_ROW = 
			new IntegerVisualProperty(0, ARBITRARY_INTEGER_RANGE, "NODE_ROW", "Node Row", CyNode.class);
	
	public static final VisualProperty<String> NODE_COLOR_KEY = new StringVisualProperty("", ARBITRARY_STRING_RANGE,
			"NODE_COLOR_KEY", "Node Color Key", true, CyNode.class);
	
	public static final VisualProperty<Integer> COL_RANGE_HIGH = 
			new IntegerVisualProperty(0, ARBITRARY_INTEGER_RANGE, "COL_RANGE_HIGH", "Column Range High", CyNode.class);
	
	public static final VisualProperty<Integer> COL_RANGE_LOW = 
			new IntegerVisualProperty(0, ARBITRARY_INTEGER_RANGE, "COL_RANGE", "Column Range", CyNode.class);
	
	public static final VisualProperty<Integer> SHADOW_COL_RANGE_HIGH = 
			new IntegerVisualProperty(0, ARBITRARY_INTEGER_RANGE, "SHADOW_COL_RANGE_HIGH", "Shadow Column Range High", CyNode.class);
	
	public static final VisualProperty<Integer> SHADOW_COL_RANGE_LOW = 
			new IntegerVisualProperty(0, ARBITRARY_INTEGER_RANGE, "SHADOW_COL_RANGE_LOW", "Shadow Column Range Low", CyNode.class);
	
	public static final VisualProperty<Integer> PLAIN_DRAIN_ZONE_LOW = 
			new IntegerVisualProperty(0, ARBITRARY_INTEGER_RANGE, "PLAIN_DRAIN_ZONE_LOW", "Plain Drain Zone Low", CyNode.class);
	
	public static final VisualProperty<Integer> PLAIN_DRAIN_ZONE_HIGH = 
			new IntegerVisualProperty(0, ARBITRARY_INTEGER_RANGE, "PLAIN_DRAIN_ZONE_HIGH", "Plain Drain Zone High", CyNode.class);
	
	public static final VisualProperty<Integer> SHADOW_DRAIN_ZONE_LOW = 
			new IntegerVisualProperty(0, ARBITRARY_INTEGER_RANGE, "SHADOW_DRAIN_ZONE_LOW", "Shadow Drain Zone Low", CyNode.class);			
	
	public static final VisualProperty<Integer> SHADOW_DRAIN_ZONE_HIGH = 
			new IntegerVisualProperty(0, ARBITRARY_INTEGER_RANGE, "SHADOW_DRAIN_ZONE_HIGH", "Shadow Drain Zone HIGH", CyNode.class);
		
	/***************************************************************************************************************************
	 * These data are found in BioFabricNetwork.LinkInfo
	 * ***********************************************************************************************************************/		
	
	public static final VisualProperty<String> LINK_INFO_COLOR_KEY = new StringVisualProperty("", ARBITRARY_STRING_RANGE,
			"LINK_COLOR_KEY", "Link Color Key", true, CyEdge.class);
	
	public static final VisualProperty<Integer> LINK_INFO_START_ROW = 
			new IntegerVisualProperty(0, ARBITRARY_INTEGER_RANGE, "LINK_INFO_START_ROW", "Link Info Start Row",  CyEdge.class);
	
	public static final VisualProperty<Integer> LINK_INFO_END_ROW = 
			new IntegerVisualProperty(0, ARBITRARY_INTEGER_RANGE, "LINK_INFO_END_ROW", "Link Info End Row", CyEdge.class);
	
	public static final VisualProperty<Integer> LINK_INFO_SHADOW_COL_NUMBER = 
			new IntegerVisualProperty(0, ARBITRARY_INTEGER_RANGE, "LINK_INFO_SHADOW_COLUMN_NUMBER", "Link Info Shadow Column Number", CyEdge.class);	
	
	public static final VisualProperty<Integer> LINK_INFO_COL_NUMBER = 
			new IntegerVisualProperty(0, ARBITRARY_INTEGER_RANGE, "LINK_INFO_COLUMN_NUMBER", "Link Info Column Number", CyEdge.class);
	
	/***************************************************************************************************************************
	 * These data are found in FabricLink
	 * ************************************************************************************************************************/		
	/*public static final VisualProperty<String> SOURCE = new StringVisualProperty("", ARBITRARY_STRING_RANGE,
			"SOURCE", "Source", true, CyEdge.class);
	
	public static final VisualProperty<String> TARGET = new StringVisualProperty("", ARBITRARY_STRING_RANGE,
			"TARGET", "Target", true, CyEdge.class);
	
	public static final VisualProperty<String> FABRIC_LINK_RELATION = new StringVisualProperty("", ARBITRARY_STRING_RANGE,
			"FABRIC_LINK_RELATION", "Fabric Link Relation", true, CyEdge.class);*/
	
	public static final VisualProperty<Boolean> FABRIC_LINK_DIRECTED = 
			new BooleanVisualProperty(false, "FABRIC_LINK_DIRECTED", "Fabric Link Directed", CyEdge.class);
	
	public static final VisualProperty<Boolean> FABRIC_LINK_IS_SHADOW = 
			new BooleanVisualProperty(false, "FABRIC_LINK_IS_SHADOW", "Fabric Link Is Shadow", CyEdge.class);
	
	public static final VisualProperty<Boolean> FABRIC_LINK_IS_FEED_BACK = 
			new BooleanVisualProperty(false, "FABRIC_LINK_IS_FEED_BACK", "Fabric Link Is Feedback", CyEdge.class);
		
		
	private final Set<VisualProperty<?>> supportedProps = new HashSet<>();
	private final Map<VisualProperty<?>, Collection<?>> supportedValuesMap = new HashMap<>();
	
	
	public BioFabricVisualLexicon() {
		super(ROOT);
		
		addVisualProperty(BIOFABRIC_NETWORK, BasicVisualLexicon.NETWORK);
		
		addVisualProperty(NODE_NAME, BasicVisualLexicon.NODE);
		addVisualProperty(NODE_ROW, BasicVisualLexicon.NODE);
		addVisualProperty(NODE_COLOR_KEY, BasicVisualLexicon.NODE);
		addVisualProperty(COL_RANGE_HIGH, BasicVisualLexicon.NODE);
		addVisualProperty(COL_RANGE_LOW, BasicVisualLexicon.NODE);
		addVisualProperty(SHADOW_COL_RANGE_HIGH, BasicVisualLexicon.NODE);		
		addVisualProperty(SHADOW_COL_RANGE_LOW, BasicVisualLexicon.NODE);
		addVisualProperty(PLAIN_DRAIN_ZONE_HIGH, BasicVisualLexicon.NODE);
		addVisualProperty(PLAIN_DRAIN_ZONE_LOW, BasicVisualLexicon.NODE);
		addVisualProperty(SHADOW_DRAIN_ZONE_HIGH, BasicVisualLexicon.NODE);
		addVisualProperty(SHADOW_DRAIN_ZONE_LOW, BasicVisualLexicon.NODE);
		
		addVisualProperty(LINK_INFO_COLOR_KEY, BasicVisualLexicon.EDGE);
		addVisualProperty(LINK_INFO_START_ROW, BasicVisualLexicon.EDGE);
		addVisualProperty(LINK_INFO_END_ROW, BasicVisualLexicon.EDGE);
		addVisualProperty(LINK_INFO_SHADOW_COL_NUMBER, BasicVisualLexicon.EDGE);
		addVisualProperty(LINK_INFO_COL_NUMBER, BasicVisualLexicon.EDGE);
		
		//addVisualProperty(SOURCE, BasicVisualLexicon.EDGE);
		//addVisualProperty(TARGET, BasicVisualLexicon.EDGE);
		//addVisualProperty(FABRIC_LINK_RELATION, BasicVisualLexicon.EDGE);
		addVisualProperty(FABRIC_LINK_DIRECTED, BasicVisualLexicon.EDGE);
		addVisualProperty(FABRIC_LINK_IS_SHADOW, BasicVisualLexicon.EDGE);
		addVisualProperty(FABRIC_LINK_IS_FEED_BACK, BasicVisualLexicon.EDGE);
		
		initSupportedProps();
	}
	
	private void initSupportedProps() {
		supportedProps.add(BasicVisualLexicon.NETWORK);
		supportedProps.add(BasicVisualLexicon.NODE);				
		supportedProps.add(BasicVisualLexicon.EDGE);
		
		supportedProps.add(BasicVisualLexicon.NETWORK_BACKGROUND_PAINT);
		supportedProps.add(BasicVisualLexicon.NETWORK_WIDTH);
		supportedProps.add(BasicVisualLexicon.NETWORK_HEIGHT);
		supportedProps.add(BasicVisualLexicon.NETWORK_CENTER_X_LOCATION);
		supportedProps.add(BasicVisualLexicon.NETWORK_CENTER_Y_LOCATION);

		supportedProps.add(BasicVisualLexicon.NODE_SELECTED);
		//supportedProps.add(BasicVisualLexicon.NODE_FILL_COLOR);
		//supportedProps.add(BasicVisualLexicon.NODE_SELECTED_PAINT);
		supportedProps.add(BasicVisualLexicon.NODE_LABEL);
		supportedProps.add(BasicVisualLexicon.NODE_VISIBLE);				
		//supportedProps.add(BasicVisualLexicon.NODE_SIZE);		
		//supportedProps.add(BasicVisualLexicon.NODE_WIDTH);
		//supportedProps.add(BasicVisualLexicon.NODE_HEIGHT);
		
		supportedProps.add(BasicVisualLexicon.EDGE_VISIBLE);
		//supportedProps.add(BasicVisualLexicon.EDGE_LINE_TYPE);
		supportedProps.add(BasicVisualLexicon.EDGE_SELECTED);
		supportedProps.add(BasicVisualLexicon.EDGE_SELECTED_PAINT);
		supportedProps.add(BasicVisualLexicon.EDGE_PAINT);
		supportedProps.add(BasicVisualLexicon.EDGE_LABEL);		
		
		//See constant fields for definitions
		supportedProps.add(BIOFABRIC_NETWORK);
		supportedProps.add(NODE_NAME);
		supportedProps.add(NODE_ROW);
		supportedProps.add(NODE_COLOR_KEY);
		supportedProps.add(COL_RANGE_HIGH);
		supportedProps.add(COL_RANGE_LOW);
		supportedProps.add(SHADOW_COL_RANGE_HIGH);
		supportedProps.add(SHADOW_COL_RANGE_LOW);
		supportedProps.add(PLAIN_DRAIN_ZONE_HIGH);
		supportedProps.add(PLAIN_DRAIN_ZONE_HIGH);
		supportedProps.add(SHADOW_DRAIN_ZONE_LOW);
		supportedProps.add(SHADOW_DRAIN_ZONE_LOW);
		
		supportedProps.add(LINK_INFO_COLOR_KEY);
		supportedProps.add(LINK_INFO_START_ROW);
		supportedProps.add(LINK_INFO_END_ROW);
		supportedProps.add(LINK_INFO_COL_NUMBER);
		supportedProps.add(LINK_INFO_SHADOW_COL_NUMBER);
		
		//supportedProps.add(SOURCE);
		//supportedProps.add(TARGET);
		//supportedProps.add(FABRIC_LINK_RELATION);
		supportedProps.add(FABRIC_LINK_DIRECTED);
		supportedProps.add(FABRIC_LINK_IS_SHADOW);
		supportedProps.add(FABRIC_LINK_IS_FEED_BACK);
		
				
		supportedValuesMap.put(EDGE_LINE_TYPE, asList(SOLID, DOT, EQUAL_DASH));		
	}
	
	@Override
	public boolean isSupported(VisualProperty<?> vp) {
		return supportedProps.contains(vp) && super.isSupported(vp);
	}
	
	@Override
	public <T> Set<T> getSupportedValueRange(VisualProperty<T> vp) {
		if (vp.getRange() instanceof DiscreteRange) {
			final DiscreteRange<T> range = (DiscreteRange<T>) vp.getRange();
			final Collection<?> supportedList = supportedValuesMap.get(vp);
			
			if (supportedList != null) {
				final Set<T> set = new LinkedHashSet<>();
				
				for (T value : range.values()) {
					if (supportedList.contains(value))
						set.add(value);
				}
				
				return set;
			}
		}
		
		return Collections.emptySet();
	}
	
	
	

}
