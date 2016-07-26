package com.boofisher.app.cyBioFabric.internal;

import java.awt.Container;

import javax.swing.JComponent;

import com.boofisher.app.cyBioFabric.internal.CyBFRenderingEngine;
import com.boofisher.app.cyBioFabric.internal.cytoscape.view.CyBFNetworkView;
import com.boofisher.app.cyBioFabric.internal.eventbus.EventBusProvider;
import com.boofisher.app.cyBioFabric.internal.graphics.GraphicsConfiguration;
import com.boofisher.app.cyBioFabric.internal.graphics.GraphicsConfigurationFactory;
import com.boofisher.app.cyBioFabric.internal.task.TaskFactoryListener;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.presentation.RenderingEngine;
import org.cytoscape.view.presentation.RenderingEngineFactory;
import org.cytoscape.view.presentation.RenderingEngineManager;
import org.cytoscape.work.swing.DialogTaskManager;

/** The RenderingEngineFactory for the CySRRenderingEngine
 * You should provide two rendering engine factories, 
 * one for the main view, and another for the smaller birds-eye view.

The RenderingEngineFactory creates an instance of RenderingEngine for the given network view.
The RenderingEngineFactory also provides access to your VisualLexicon.
A container object is passed to the factory whenever a RenderingEngine is created. 
This is typically an instance of JComponent, and will be the parent for the RenderingEngine's drawing canvas.
Your RenderingEngineFactory must also register the newly created RenderingEngine with the Cytoscape RenderingEngineManager.
In CySR there is one class CySRRenderingEngineFactory that implements RenderingEngineFactory. Two instances are created,
each is parameterized with a GraphicsConfigurationFactory which provides functionality that is specific to the 
main view or the birds-eye view.

 * @author paperwing (Yue Dong)
 */
public class CyBFRenderingEngineFactory implements RenderingEngineFactory<CyNetwork> {
	
	private final RenderingEngineManager renderingEngineManager;
	private final VisualLexicon visualLexicon;
	private final TaskFactoryListener taskFactoryListener;
	private final DialogTaskManager taskManager;
	private final EventBusProvider eventBusProvider;
	
	private final GraphicsConfigurationFactory graphicsConfigFactory;
	
	
	public CyBFRenderingEngineFactory(
			RenderingEngineManager renderingEngineManager, 
			VisualLexicon lexicon,
			TaskFactoryListener taskFactoryListener,
			DialogTaskManager taskManager,
			EventBusProvider eventBusFactory,
			GraphicsConfigurationFactory graphicsConfigFactory) {	
		
		this.renderingEngineManager = renderingEngineManager;
		this.visualLexicon = lexicon;
		this.taskFactoryListener = taskFactoryListener;
		this.taskManager = taskManager;
		this.eventBusProvider = eventBusFactory;
		this.graphicsConfigFactory = graphicsConfigFactory;
	}
	
	
	/**
	 * Catch these errors up front.
	 * 
	 * @throws ClassCastException if the viewModel is not an instance of CySRNetworkView
	 * @throws ClassCastException if the container is not an instance of JComponent
	 */
	@Override
	public RenderingEngine<CyNetwork> createRenderingEngine(Object container, View<CyNetwork> viewModel) {
		// Verify the type of the view up front.
		CyBFNetworkView cySRViewModel = (CyBFNetworkView) viewModel;
		JComponent component = (JComponent) container;
		
		GraphicsConfiguration configuration = graphicsConfigFactory.createGraphicsConfiguration();
		
		// TODO the birds eye view should not be attaching input listeners to the outer component
		// Is the Birds eye view above the top glass pane?
		JComponent inputComponent = getKeyboardComponent(component, cySRViewModel.getSUID());
		if(inputComponent == null)
			inputComponent = component; // happens for birds-eye-view
		
		CyBFRenderingEngine engine = new CyBFRenderingEngine(component, inputComponent, cySRViewModel, visualLexicon, eventBusProvider,
				                                             configuration, taskFactoryListener, taskManager);
		
		renderingEngineManager.addRenderingEngine(engine);
		return engine;
	}
	
	/**
	 * This is a HACK for now to get the component to attach hotkeys and cursors to.
	 */
	private JComponent getKeyboardComponent(JComponent start, long suid) {
		String componentName = "__CyNetworkView_" + suid; // see ViewUtil.createUniqueKey(CyNetworkView)
		Container parent = start;
		while(parent != null) {
			if(componentName.equals(parent.getName())) {
				return (JComponent) parent;
			}
			parent = parent.getParent();
		}
		return null;
	}
	
	
	@Override
	public VisualLexicon getVisualLexicon() {
		return visualLexicon;
	}
}

