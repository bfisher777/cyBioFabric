package com.boofisher.app.cyBioFabric.internal.eventbus;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;
import java.util.WeakHashMap;

import com.boofisher.app.cyBioFabric.internal.cytoscape.view.CySRNetworkView;

import com.google.common.eventbus.EventBus;

/**
 * Acts as a single point for accessing the event bus for a Cy3DNetworkView.
 * 
 * @author mkucera
 */
public class EventBusProvider {

	private Map<Long,EventBus> eventBusMap = new WeakHashMap<>();
	
	
	/**
	 * Returns an event bus that can be used by all the parts of the renderer
	 * for a particular 3D network view.
	 * 
	 * Note: The parameter type is rather restrictive to ensure its not accidentally
	 * called on any CyIdentifiable.
	 */
	public synchronized EventBus getEventBus(CySRNetworkView identifiable) {
		Long suid = checkNotNull(identifiable).getSUID();
		
		/*EventBus allows publish-subscribe-style communication between components without 
		requiring the components to explicitly register with one another (and thus be aware 
				of each other). It is designed exclusively to replace traditional Java in-process 
		event distribution using explicit registration. It is not a general-purpose 
		publish-subscribe system, nor is it intended for interprocess communication.*/
		EventBus eventBus = eventBusMap.get(suid);
		if(eventBus == null) {
			eventBus = new EventBus(suid.toString());
			eventBusMap.put(suid, eventBus);
		}
		return eventBus;
	}
	
}
