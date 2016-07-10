package com.boofisher.app.cySimpleRenderer.internal;

import static org.cytoscape.work.ServiceProperties.ID;
import static org.cytoscape.work.ServiceProperties.INSERT_SEPARATOR_AFTER;
import static org.cytoscape.work.ServiceProperties.INSERT_SEPARATOR_BEFORE;
import static org.cytoscape.work.ServiceProperties.MENU_GRAVITY;
import static org.cytoscape.work.ServiceProperties.TITLE;

import java.io.IOException;
import java.util.Properties;


import org.cytoscape.application.NetworkViewRenderer;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.service.util.AbstractCyActivator;
import org.cytoscape.task.EdgeViewTaskFactory;
import org.cytoscape.task.NetworkViewLocationTaskFactory;
import org.cytoscape.task.NetworkViewTaskFactory;
import org.cytoscape.task.NodeViewTaskFactory;
import org.cytoscape.util.swing.OpenBrowser;
import org.cytoscape.view.layout.CyLayoutAlgorithm;
import org.cytoscape.view.layout.CyLayoutAlgorithmManager;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.presentation.RenderingEngineManager;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.work.TunableSetter;
import org.cytoscape.work.swing.DialogTaskManager;
import org.cytoscape.work.undo.UndoSupport;
import org.osgi.framework.BundleContext;

import com.boofisher.app.cySimpleRenderer.internal.cytoscape.view.CySRVisualLexicon;
import com.boofisher.app.cySimpleRenderer.internal.eventbus.EventBusProvider;
import com.boofisher.app.cySimpleRenderer.internal.graphics.GraphicsConfigurationFactory;
import com.boofisher.app.cySimpleRenderer.internal.layouts.FlattenLayoutAlgorithm;
import com.boofisher.app.cySimpleRenderer.internal.task.TaskFactoryListener;
import com.boofisher.app.cySimpleRenderer.internal.layouts.BoxLayoutAlgorithm;
import com.boofisher.app.cySimpleRenderer.internal.layouts.CenterLayoutAlgorithm;
import com.boofisher.app.cySimpleRenderer.internal.layouts.GridLayoutAlgorithm;
import com.boofisher.app.cySimpleRenderer.internal.layouts.SphericalLayoutAlgorithm;

/*Main entry point into cytoscape
 * The application Simple Renderer will perform simple rendering of the input file to generate the image.
 * The app provides no user input handling and is meant to be a demonstration of writing a simple renderer
 * using Java 2-D with Cytoscape. */
public class CyActivator extends AbstractCyActivator {

	@Override
	public void start(BundleContext context) throws Exception {				

		/*This interface provides basic access to the Swing objects that constitute this application.*/
		CySwingApplication application = getService(context, CySwingApplication.class);
		
		/*A utility provided as an OSGi service for opening a web browser. 
		 *The Cytoscape property "defaultWebBrowser" may be set with an alternative
		 *command for opening a different web browser than default.*/
		OpenBrowser openBrowser = getService(context, OpenBrowser.class);
		
		/*Manager for RenderingEngine objects. All RenderingEngine objects created by RenderingEngineFactory should be registered to this manager.
		Register/unregister engines are handled through RenderingEngineAddedEvents.*/
		RenderingEngineManager renderingEngineManager = getService(context, RenderingEngineManager.class);
		
		/*Manager for VisualStyles. This object manages mapping from view model to VisualStyle. User objects can access all 
		 * VisualStyles and VisualMappingFunctions through this class.
		Add/Remove operations will be done through events. For more information, read JavaDoc for VisualStyleAddedEvent 
		and VisualStyleAboutToBeRemovedEvent.*/
		VisualMappingManager visualMappingManagerService = getService(context, VisualMappingManager.class);
		
		/*A simple interface that posts edits to the Cytoscape undo stack.*/
		UndoSupport undoSupport = getService(context, UndoSupport.class);
		
		/*A task factory specifically for layout algorithms.*/
		CyLayoutAlgorithmManager layoutAlgorithmManager =  getService(context, CyLayoutAlgorithmManager.class);
		TunableSetter tunableSetter = getService(context, TunableSetter.class);
		
		// A specialization of TaskManager that creates a JDialog configuration object and expects the dialog parent to be a Window.
		DialogTaskManager dialogTaskManager = getService(context, DialogTaskManager.class);
		
		/*This class is responsible for keeping track of all the current view {@link TaskFactory} objects,
		 * which are used in situations such as needing to execute a certain task via the right-click menu.*/
		TaskFactoryListener taskFactoryListener = new TaskFactoryListener();
		registerServiceListener(context, taskFactoryListener, "addNodeViewTaskFactory", "removeNodeViewTaskFactory", NodeViewTaskFactory.class);
		registerServiceListener(context, taskFactoryListener, "addEdgeViewTaskFactory", "removeEdgeViewTaskFactory", EdgeViewTaskFactory.class);
		registerServiceListener(context, taskFactoryListener, "addNetworkViewTaskFactory", "removeNetworkViewTaskFactory", NetworkViewTaskFactory.class);
		registerServiceListener(context, taskFactoryListener, "addNetworkViewLocationTaskFactory", "removeNetworkViewLocationTaskFactory", NetworkViewLocationTaskFactory.class);
		
		/*A RenderingEngine should provide one, immutable lexicon implementing this interface. 
		 * This is a pre-defined tree of VisualProperties designed by the RenderingEngine developer.*/
		VisualLexicon cySRVisualLexicon = new CySRVisualLexicon();		
		/*The Properties class represents a persistent set of properties. The Properties can be saved to a stream or loaded from a stream. 
		 *Each key and its corresponding value in the property list is a string.*/
		Properties cySRVisualLexiconProps = new Properties();
		cySRVisualLexiconProps.setProperty("serviceType", "visualLexicon");
		cySRVisualLexiconProps.setProperty("id", "CySR");
		registerService(context, cySRVisualLexicon, VisualLexicon.class, cySRVisualLexiconProps);

		/*EventBus allows publish-subscribe-style communication between components without 
		requiring the components to explicitly register with one another (and thus be aware 
				of each other). It is designed exclusively to replace traditional Java in-process 
		event distribution using explicit registration. It is not a general-purpose 
		publish-subscribe system, nor is it intended for interprocess communication.*/
		/*Acts as a single point for accessing the event bus for a CySRNetworkView.*/
		EventBusProvider eventBusProvider = new EventBusProvider();
		
		// CySR NetworkView factory
		/*Factory for CyNetworkView objects. Modules which need to create view models should import this as a service.
		 * Create a CySRNetworkView*/
		CySRNetworkViewFactory cySRNetworkViewFactory = new CySRNetworkViewFactory(cySRVisualLexicon, visualMappingManagerService, eventBusProvider);
		Properties cySRNetworkViewFactoryProps = new Properties();
		cySRNetworkViewFactoryProps.setProperty("serviceType", "factory");
		registerService(context, cySRNetworkViewFactory, CyNetworkViewFactory.class, cySRNetworkViewFactoryProps);

		
		// Main RenderingEngine factory
		/*GraphicsConfiguarionFactory is an enum, values implement abstract method createGraphicsConfiguration*/
/*		TODO: Implement graphics using Java 2D*/
		GraphicsConfigurationFactory mainFactory = GraphicsConfigurationFactory.MAIN_FACTORY;
		
		/*The RenderingEngineFactory creates an instance of RenderingEngine for the given network view.
		The RenderingEngineFactory also provides access to your VisualLexicon.
		A container object is passed to the factory whenever a RenderingEngine is created. 
		This is typically an instance of JComponent, and will be the parent for the RenderingEngine's drawing canvas.
		Your RenderingEngineFactory must also register the newly created RenderingEngine with the Cytoscape RenderingEngineManager.
		In CySR there is one class CySRRenderingEngineFactory that implements RenderingEngineFactory. Two instances are created,
		each is parameterized with a GraphicsConfigurationFactory which provides functionality that is specific to the 
		main view or the birds-eye view.*/
		CySRRenderingEngineFactory cySRMainRenderingEngineFactory = new CySRRenderingEngineFactory(
				renderingEngineManager, cySRVisualLexicon, taskFactoryListener, dialogTaskManager, eventBusProvider, mainFactory);		
		// Bird's Eye RenderingEngine factory
		GraphicsConfigurationFactory birdsEyeFactory = GraphicsConfigurationFactory.BIRDS_EYE_FACTORY;
		CySRRenderingEngineFactory cySRBirdsEyeRenderingEngineFactory = new CySRRenderingEngineFactory(
				renderingEngineManager, cySRVisualLexicon, taskFactoryListener, dialogTaskManager, eventBusProvider, birdsEyeFactory);

		
		// NetworkViewRenderer, this is the main entry point that Cytoscape will call into
		CySRNetworkViewRenderer networkViewRenderer = new CySRNetworkViewRenderer(
				cySRNetworkViewFactory, cySRMainRenderingEngineFactory, cySRBirdsEyeRenderingEngineFactory);
		registerService(context, networkViewRenderer, NetworkViewRenderer.class, new Properties());
		
		// Still need to register the rendering engine factory directly		
		Properties renderingEngineProps = new Properties();
		renderingEngineProps.setProperty(ID, CySRNetworkViewRenderer.ID);
		registerAllServices(context, cySRMainRenderingEngineFactory, renderingEngineProps);
		
		/*// Layout algorithms, A task factory specifically for layout algorithms.
		CyLayoutAlgorithm frAlgorithm = layoutAlgorithmManager.getLayout("fruchterman-rheingold");	
		
		registerLayoutAlgorithms(context,
				frAlgorithm,
				new SphericalLayoutAlgorithm(undoSupport),
				new GridLayoutAlgorithm(undoSupport),
				new BoxLayoutAlgorithm(undoSupport),
				new FlattenLayoutAlgorithm(undoSupport),
				new CenterLayoutAlgorithm(undoSupport)
		);*/
		
		// About dialog
		AboutDialogAction aboutDialogAction = new AboutDialogAction(application, openBrowser);
		aboutDialogAction.setPreferredMenu("Apps.CySR");
		registerAllServices(context, aboutDialogAction, new Properties());
		
	}

	
	private void registerLayoutAlgorithms(BundleContext context, CyLayoutAlgorithm... algorithms) {
		for(int i = 0; i < algorithms.length; i++) {
			Properties props = new Properties();
			props.setProperty("preferredTaskManager", "menu");
			props.setProperty(TITLE, algorithms[i].toString());
			props.setProperty(MENU_GRAVITY, "30." + (i+1));
			if(i == 0)
				props.setProperty(INSERT_SEPARATOR_BEFORE, "true");
			if(i == algorithms.length-1)
				props.setProperty(INSERT_SEPARATOR_AFTER, "true");
			
			registerService(context, algorithms[i], CyLayoutAlgorithm.class, props);
		}
	}
	
}

