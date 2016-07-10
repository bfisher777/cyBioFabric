package com.boofisher.app.cySimpleRenderer.internal.cytoscape.view;

import java.util.HashMap;
import java.util.Map;

import org.cytoscape.model.SUIDFactory;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.VisualProperty;

//This class assumes that the VisualProperty's getIdString() method returns a unique
//value amongst the VisualProperty objects

/**
 * A view is a base interface that defines methods used to set visual properties
 * for nodes, edges, and networks.
 *
 * Think of it as a row in the view-model table.
 */
public abstract class CySRView<S> implements View<S> {
	
	private final Map<String, VisualPropertyValue<?>> valueHolders;
	private final Long suid;
	protected final DefaultValueVault defaultValues;
		
	public CySRView(DefaultValueVault defaultValues) {
		this.suid = SUIDFactory.getNextSUID();
		this.valueHolders = new HashMap<String, VisualPropertyValue<?>>();
		this.defaultValues = defaultValues;
	}
	
	@Override
	public Long getSUID() {
		return suid;
	}
	
	/*Visual Property - An object which represents a type of visual entity, such as node color, size, etc.  
	Visual Property itself does NOT have any hierarchy/dependency. It will be implemented in VisualLexicon.*/
	public <T> T getVisualProperty(VisualProperty<T> visualProperty) {
		VisualPropertyValue<?> valueHolder = valueHolders.get(visualProperty.getIdString());
		
		T value = null;
		if(valueHolder != null) {
			value = (T) valueHolder.getValue();
		}
		if(value == null) {
			value = defaultValues.getDefaultValue(visualProperty);
		}
		
		return value;
	}

	public <T, V extends T> void setVisualProperty(VisualProperty<? extends T> visualProperty, V value) {
		VisualPropertyValue<V> valueHolder = new VisualPropertyValue<V>(value);
		
		valueHolders.put(visualProperty.getIdString(), valueHolder);
	}
	
	public <V> void setVisualProperty(String propertyID, V value) {
		VisualPropertyValue<V> valueHolder = new VisualPropertyValue<V>(value);
		
		valueHolders.put(propertyID, valueHolder);
	}

	@Override
	public <T, V extends T> void setLockedValue(VisualProperty<? extends T> visualProperty,
			V value) {
		setVisualProperty(visualProperty, value);
		
		valueHolders.get(visualProperty.getIdString()).setValueLocked(true);
	}

	@Override
	public boolean isValueLocked(VisualProperty<?> visualProperty) {
		if (valueHolders.get(visualProperty.getIdString()) != null
				&& valueHolders.get(visualProperty.getIdString()).isValueLocked()) {
			
			return true;
		} else {
			// TODO: Currently returns false even when visualProperty not found, check if should update
			return false;
		}
	}

	@Override
	public void clearValueLock(VisualProperty<?> visualProperty) {
		// TODO: Doesn't throw an exception if visualProperty not found
		if (valueHolders.get(visualProperty.getIdString()) != null) {
			
			valueHolders.get(visualProperty.getIdString()).setValueLocked(false);
		}
	}
	
	
	// MKTODO the below methods were auto generated, how to fix?
	@Override
	public boolean isSet(VisualProperty<?> vp) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isDirectlyLocked(VisualProperty<?> vp) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void clearVisualProperties() {
		// TODO Auto-generated method stub
		
	}
	
}