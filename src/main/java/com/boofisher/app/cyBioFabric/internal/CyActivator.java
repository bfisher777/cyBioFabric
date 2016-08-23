package com.boofisher.app.cyBioFabric.internal;

import static org.cytoscape.work.ServiceProperties.ID;
import static org.cytoscape.work.ServiceProperties.INSERT_SEPARATOR_AFTER;
import static org.cytoscape.work.ServiceProperties.INSERT_SEPARATOR_BEFORE;
import static org.cytoscape.work.ServiceProperties.MENU_GRAVITY;
import static org.cytoscape.work.ServiceProperties.TITLE;

import java.util.Properties;

import org.apache.log4j.Logger;
import org.cytoscape.application.CyUserLog;
import org.cytoscape.application.NetworkViewRenderer;
import org.cytoscape.application.events.CyShutdownListener;
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
import org.cytoscape.view.model.events.NetworkViewAboutToBeDestroyedListener;
import org.cytoscape.view.model.events.NetworkViewAddedListener;
import org.cytoscape.view.presentation.RenderingEngineManager;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.work.swing.DialogTaskManager;
import org.cytoscape.work.undo.UndoSupport;
import org.osgi.framework.BundleContext;

import com.boofisher.app.cyBioFabric.internal.biofabric.util.ResourceManager;
import com.boofisher.app.cyBioFabric.internal.cytoscape.view.BioFabricVisualLexicon;
import com.boofisher.app.cyBioFabric.internal.events.BioFabricNetworkViewAddedHandler;
import com.boofisher.app.cyBioFabric.internal.events.BioFabricNetworkViewAddedListener;
import com.boofisher.app.cyBioFabric.internal.events.BioFabricNetworkViewToBeDestroyedHandler;
import com.boofisher.app.cyBioFabric.internal.events.BioFabricNetworkViewToBeDestroyedListener;
import com.boofisher.app.cyBioFabric.internal.events.BioFabricShutdownHandler;
import com.boofisher.app.cyBioFabric.internal.events.BioFabricShutdownListener;
import com.boofisher.app.cyBioFabric.internal.graphics.GraphicsConfigurationFactory;
import com.boofisher.app.cyBioFabric.internal.layouts.BioFabricLayoutInterface;
import com.boofisher.app.cyBioFabric.internal.layouts.DefaultBioFabricLayoutAlgorithm;
import com.boofisher.app.cyBioFabric.internal.task.TaskFactoryListener;

/*Main entry point into cytoscape
 * The application Simple Renderer will perform simple rendering of the input file to generate the image.
 * The app provides no user input handling and is meant to be a demonstration of writing a simple renderer
 * using Java 2-D with Cytoscape. */
public class CyActivator extends AbstractCyActivator {	
	final Logger logger = Logger.getLogger(CyUserLog.NAME);

	@Override
	public void start(BundleContext context) throws Exception {						
		
		ResourceManager.initManager("com.boofisher.app.cyBioFabric.internal.biofabric.props.BioFabric");
		
		//A simple interface that posts edits to the Cytoscape undo stack.
		UndoSupport undoSupport = getService(context, UndoSupport.class);
		
		//A task factory specifically for layout algorithms.
		CyLayoutAlgorithmManager layoutAlgorithmManager =  getService(context, CyLayoutAlgorithmManager.class);	
		//Save the default layout, restore it later in CyBFRenderingEngine
		String defaultLayout = layoutAlgorithmManager.getDefaultLayout().getName();
		
		/*A RenderingEngine should provide one, immutable lexicon implementing this interface. 
		 * This is a pre-defined tree of VisualProperties designed by the RenderingEngine developer.*/
		VisualLexicon cyBFVisualLexicon = new BioFabricVisualLexicon();		
		/*The Properties class represents a persistent set of properties. The Properties can be saved to a stream or loaded from a stream. 
		 *Each key and its corresponding value in the property list is a string.*/
		Properties cyBFVisualLexiconProps = new Properties();
		cyBFVisualLexiconProps.setProperty("serviceType", "visualLexicon");
		cyBFVisualLexiconProps.setProperty("id", "CyBioFabric");
		registerService(context, cyBFVisualLexicon, VisualLexicon.class, cyBFVisualLexiconProps);
		
		//register layout and then set it as the default layout
		BioFabricLayoutInterface bfLayoutAlg = new DefaultBioFabricLayoutAlgorithm(undoSupport);
		
		registerLayoutAlgorithms(context,				
				(DefaultBioFabricLayoutAlgorithm)bfLayoutAlg				
		);				

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
		
		// A specialization of TaskManager that creates a JDialog configuration object and expects the dialog parent to be a Window.
		DialogTaskManager dialogTaskManager = getService(context, DialogTaskManager.class);
		
		/*This class is responsible for keeping track of all the current view {@link TaskFactory} objects,
		 * which are used in situations such as needing to execute a certain task via the right-click menu.*/
		TaskFactoryListener taskFactoryListener = new TaskFactoryListener();
		registerServiceListener(context, taskFactoryListener, "addNodeViewTaskFactory", "removeNodeViewTaskFactory", NodeViewTaskFactory.class);
		registerServiceListener(context, taskFactoryListener, "addEdgeViewTaskFactory", "removeEdgeViewTaskFactory", EdgeViewTaskFactory.class);
		registerServiceListener(context, taskFactoryListener, "addNetworkViewTaskFactory", "removeNetworkViewTaskFactory", NetworkViewTaskFactory.class);
		registerServiceListener(context, taskFactoryListener, "addNetworkViewLocationTaskFactory", "removeNetworkViewLocationTaskFactory", NetworkViewLocationTaskFactory.class);
		
		
		//create and register all the event listeners
		BioFabricNetworkViewAddedHandler addNetworkHandler = new BioFabricNetworkViewAddedHandler();
		BioFabricNetworkViewAddedListener bioFabricViewAddedListener = new BioFabricNetworkViewAddedListener(addNetworkHandler);
		registerService(context, bioFabricViewAddedListener, NetworkViewAddedListener.class, new Properties());
	
		BioFabricNetworkViewToBeDestroyedHandler destroyNetworkHandler = new BioFabricNetworkViewToBeDestroyedHandler();
		BioFabricNetworkViewToBeDestroyedListener bioFabricViewToBeDestroyedListener = new BioFabricNetworkViewToBeDestroyedListener(destroyNetworkHandler);
		registerService(context, bioFabricViewToBeDestroyedListener, NetworkViewAboutToBeDestroyedListener.class, new Properties());
		
		BioFabricShutdownHandler biofabricShutdownHandler = new BioFabricShutdownHandler();
		BioFabricShutdownListener bioFabricShutdownListener = new BioFabricShutdownListener(biofabricShutdownHandler);
		registerService(context, bioFabricShutdownListener, CyShutdownListener.class, new Properties());		
			
		/*//add a new panel
		BioFabricCytoPanel bioFabricNavPanel = new BioFabricCytoPanel();
		//Register it as a service:
		registerService(context, bioFabricNavPanel, CytoPanelComponent.class, new Properties());*/
		
		
		// CyBF NetworkView factory
		/*Factory for CyNetworkView objects. Modules which need to create view models should import this as a service.
		 * Create a CyBFNetworkView*/
		CyBFNetworkViewFactory cyBFNetworkViewFactory = new CyBFNetworkViewFactory(cyBFVisualLexicon, 
				visualMappingManagerService, layoutAlgorithmManager, bfLayoutAlg);
		Properties cyBFNetworkViewFactoryProps = new Properties();
		cyBFNetworkViewFactoryProps.setProperty("serviceType", "factory");
		registerService(context, cyBFNetworkViewFactory, CyNetworkViewFactory.class, cyBFNetworkViewFactoryProps);
		
		// Main RenderingEngine factory
		/*GraphicsConfiguarionFactory is an enum, values implement abstract method createGraphicsConfiguration*/
		/*TODO: Implement graphics using Java 2D*/
		GraphicsConfigurationFactory mainFactory = GraphicsConfigurationFactory.MAIN_FACTORY;
		
		/*The RenderingEngineFactory creates an instance of RenderingEngine for the given network view.
		The RenderingEngineFactory also provides access to your VisualLexicon.
		A container object is passed to the factory whenever a RenderingEngine is created. 
		This is typically an instance of JComponent, and will be the parent for the RenderingEngine's drawing canvas.
		Your RenderingEngineFactory must also register the newly created RenderingEngine with the Cytoscape RenderingEngineManager.
		In CyBF there is one class CyBFRenderingEngineFactory that implements RenderingEngineFactory. Two instances are created,
		each is parameterized with a GraphicsConfigurationFactory which provides functionality that is specific to the 
		main view or the birds-eye view.*/	
		CyBFRenderingEngineFactory cyBFMainRenderingEngineFactory = new CyBFRenderingEngineFactory(
				renderingEngineManager, cyBFVisualLexicon, taskFactoryListener, dialogTaskManager, mainFactory, 
				addNetworkHandler, destroyNetworkHandler, layoutAlgorithmManager, defaultLayout);		
		// Bird's Eye RenderingEngine factory
		GraphicsConfigurationFactory birdsEyeFactory = GraphicsConfigurationFactory.BIRDS_EYE_FACTORY;
		CyBFRenderingEngineFactory cyBFBirdsEyeRenderingEngineFactory = new CyBFRenderingEngineFactory(
				renderingEngineManager, cyBFVisualLexicon, taskFactoryListener, dialogTaskManager, birdsEyeFactory, 
				addNetworkHandler, destroyNetworkHandler, layoutAlgorithmManager, defaultLayout);
		
		//Thumbnail RenderingEngine factory
		GraphicsConfigurationFactory thumbnailFactory = GraphicsConfigurationFactory.THUMBNAIL_FACTORY;
		CyBFRenderingEngineFactory cyBFThumbnailRenderingEngineFactory = new CyBFRenderingEngineFactory(
				renderingEngineManager, cyBFVisualLexicon, taskFactoryListener, dialogTaskManager, thumbnailFactory, 
				addNetworkHandler, destroyNetworkHandler, layoutAlgorithmManager, defaultLayout);
		
		// NetworkViewRenderer, this is the main entry point that Cytoscape will call into
		CyBFNetworkViewRenderer networkViewRenderer = new CyBFNetworkViewRenderer(cyBFNetworkViewFactory, 
				cyBFMainRenderingEngineFactory, cyBFBirdsEyeRenderingEngineFactory, cyBFThumbnailRenderingEngineFactory);
		registerService(context, networkViewRenderer, NetworkViewRenderer.class, new Properties());
		
		// Still need to register the rendering engine factory directly		
		Properties renderingEngineProps = new Properties();
		renderingEngineProps.setProperty(ID, CyBFNetworkViewRenderer.ID);
		registerAllServices(context, cyBFMainRenderingEngineFactory, renderingEngineProps);												
		
		// About dialog
		AboutDialogAction aboutDialogAction = new AboutDialogAction(application, openBrowser);
		aboutDialogAction.setPreferredMenu("Apps.CyBioFabric");
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

