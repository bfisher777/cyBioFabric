package org.systemsbiology.cyBioFabric.internal;

import static org.cytoscape.work.ServiceProperties.ID;
import static org.cytoscape.work.ServiceProperties.INSERT_SEPARATOR_AFTER;
import static org.cytoscape.work.ServiceProperties.INSERT_SEPARATOR_BEFORE;
import static org.cytoscape.work.ServiceProperties.MENU_GRAVITY;
import static org.cytoscape.work.ServiceProperties.TITLE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.CyUserLog;
import org.cytoscape.application.NetworkViewRenderer;
import org.cytoscape.application.events.CyShutdownListener;
import org.cytoscape.application.events.SetCurrentNetworkViewListener;
import org.cytoscape.application.swing.ActionEnableSupport;
import org.cytoscape.application.swing.CyAction;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.CytoPanel;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.service.util.AbstractCyActivator;
import org.cytoscape.task.EdgeViewTaskFactory;
import org.cytoscape.task.NetworkViewLocationTaskFactory;
import org.cytoscape.task.NetworkViewTaskFactory;
import org.cytoscape.task.NodeViewTaskFactory;
import org.cytoscape.util.swing.OpenBrowser;
import org.cytoscape.view.layout.CyLayoutAlgorithm;
import org.cytoscape.view.layout.CyLayoutAlgorithmManager;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.model.events.NetworkViewAboutToBeDestroyedListener;
import org.cytoscape.view.model.events.NetworkViewAddedListener;
import org.cytoscape.view.presentation.RenderingEngineManager;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.work.swing.DialogTaskManager;
import org.cytoscape.work.undo.UndoSupport;
import org.osgi.framework.BundleContext;
import org.systemsbiology.cyBioFabric.internal.biofabric.util.ResourceManager;
import org.systemsbiology.cyBioFabric.internal.cytoscape.view.BioFabricVisualLexicon;
import org.systemsbiology.cyBioFabric.internal.events.BioFabricNetworkViewAddedHandler;
import org.systemsbiology.cyBioFabric.internal.events.BioFabricNetworkViewAddedListener;
import org.systemsbiology.cyBioFabric.internal.events.BioFabricNetworkViewToBeDestroyedHandler;
import org.systemsbiology.cyBioFabric.internal.events.BioFabricNetworkViewToBeDestroyedListener;
import org.systemsbiology.cyBioFabric.internal.events.BioFabricSetCurrentViewHandler;
import org.systemsbiology.cyBioFabric.internal.events.BioFabricSetCurrentViewListener;
import org.systemsbiology.cyBioFabric.internal.events.BioFabricShutdownHandler;
import org.systemsbiology.cyBioFabric.internal.events.BioFabricShutdownListener;
import org.systemsbiology.cyBioFabric.internal.graphics.BioFabricCytoPanel;
import org.systemsbiology.cyBioFabric.internal.graphics.GraphicsConfigurationFactory;
import org.systemsbiology.cyBioFabric.internal.icons.BioFabricImageIcon;
import org.systemsbiology.cyBioFabric.internal.icons.ZoomToRectAction;
import org.systemsbiology.cyBioFabric.internal.layouts.BioFabricLayoutInterface;
import org.systemsbiology.cyBioFabric.internal.layouts.DefaultBioFabricLayoutAlgorithm;
import org.systemsbiology.cyBioFabric.internal.task.TaskFactoryListener;

/*Main entry point into cytoscape
 * The application Simple Renderer will perform simple rendering of the input file to generate the image.
 * The app provides no user input handling and is meant to be a demonstration of writing a simple renderer
 * using Java 2-D with Cytoscape. */
public class CyActivator extends AbstractCyActivator {	
	final Logger logger = Logger.getLogger(CyUserLog.NAME);

	@Override
	public void start(BundleContext context) throws Exception {						
		
		ResourceManager.initManager("org.systemsbiology.cyBioFabric.internal.biofabric.props.BioFabric");
		
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
		
		//register biofabric layout(s)
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
				
		//Add image icons(buttons) to toolbar
		CyApplicationManager appManager = getService(context, CyApplicationManager.class);
		CyNetworkViewManager networkManager = getService(context, CyNetworkViewManager.class);
		ArrayList<BioFabricImageIcon> buttons = new ArrayList<BioFabricImageIcon>();
		Map<String, String> configProps = new HashMap<String, String>();
		configureZoomRectProperties(configProps);
		ZoomToRectAction zoomToRectAction = new ZoomToRectAction(configProps, appManager, networkManager);
		buttons.add(zoomToRectAction);
		registerService(context, zoomToRectAction, CyAction.class, new Properties());
		
		//adds the magnifier and the navigation tool to the control panel
		BioFabricCytoPanel bioFabricNavPanel = new BioFabricCytoPanel();
		registerService(context, bioFabricNavPanel, CytoPanelComponent.class, new Properties());
		
		//create and register all the event listeners
		//TODO: currently view added handle does nothing, implement or remove
		BioFabricNetworkViewAddedHandler addNetworkHandler = new BioFabricNetworkViewAddedHandler();
		BioFabricNetworkViewAddedListener bioFabricViewAddedListener = new BioFabricNetworkViewAddedListener(addNetworkHandler);
		registerService(context, bioFabricViewAddedListener, NetworkViewAddedListener.class, new Properties());
	
		BioFabricNetworkViewToBeDestroyedHandler destroyNetworkHandler = new BioFabricNetworkViewToBeDestroyedHandler();
		BioFabricNetworkViewToBeDestroyedListener bioFabricViewToBeDestroyedListener = new BioFabricNetworkViewToBeDestroyedListener(destroyNetworkHandler);
		registerService(context, bioFabricViewToBeDestroyedListener, NetworkViewAboutToBeDestroyedListener.class, new Properties());
		
		BioFabricShutdownHandler biofabricShutdownHandler = new BioFabricShutdownHandler();
		BioFabricShutdownListener bioFabricShutdownListener = new BioFabricShutdownListener(biofabricShutdownHandler);
		registerService(context, bioFabricShutdownListener, CyShutdownListener.class, new Properties());
		
		BioFabricSetCurrentViewHandler biofabricSetCurrentViewHandler = new BioFabricSetCurrentViewHandler(bioFabricNavPanel, buttons);
		BioFabricSetCurrentViewListener bioFabricSetCurrentViewListener = new BioFabricSetCurrentViewListener(biofabricSetCurrentViewHandler);
		registerService(context, bioFabricSetCurrentViewListener, SetCurrentNetworkViewListener.class, new Properties());						
		
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
	
	/*title - (The title of the menu.)
	preferredMenu - (The preferred menu for the action.)
	largeIconURL - (The icon to be used for the toolbar.)
	smallIconURL - (The icon to be used for the menu.)
	tooltip - (The toolbar or menu tooltip.)
	inToolBar - (Whether the action should be in the toolbar.)
	inMenuBar - (Whether the action should be in a menu.)
	insertSeparatorBefore - (Whether a separator should be inserted before this menu item.)
	insertSeparatorAfter - (Whether a separator should be inserted after this menu item.)
	enableFor - (System state that the action should be enabled for. See {@link ActionEnableSupport} for more detail.)
	accelerator - (Accelerator key bindings.)
	menuGravity - (Float value with 0.0 representing the top and larger values moving towards the bottom of the menu.)
	toolBarGravity - (Float value with 0.0 representing the top and larger values moving towards the bottom of the toolbar.)*/
	private void configureZoomRectProperties(Map<String, String> configProps){

		String iconName = ResourceManager.getManager().getString("command.ZoomToRect");		
		
		configProps.put("title", iconName);
		//configProps.put("preferredMenu", preferredMenu);
		configProps.put("largeIconURL", "/images/ZoomToFabricRect24.gif");
		//configProps.put("smallIconURL", smallIconURL);
		configProps.put("tooltip", iconName);
		configProps.put("inToolBar", new Boolean(true).toString());
		configProps.put("inMenuBar", new Boolean(false).toString());
		configProps.put("insertSeparatorBefore", new Boolean(false).toString());
		configProps.put("insertSeparatorAfter", new Boolean(false).toString());
		configProps.put("enableFor", ActionEnableSupport.ENABLE_FOR_NETWORK_AND_VIEW);
		//configProps.put("accelerator", accelerator);//custom key binding
		//configProps.put("menuGravity", new Float(5).toString());
		configProps.put("toolBarGravity", new Float(5).toString());			
	}
	
	private void configureProperties(String title, String preferredMenu, String largeIconURL, String smallIconURL, String tooltip,
			String inToolBar, String inMenuBar, String insertSeparatorBefore, String insertSeparatorAfter, String enableFor,
			String accelerator, String menuGravity, String toolBarGravity, Map<String, String> configProps){
		
		configProps.put("title", title);
		configProps.put("preferredMenu", preferredMenu);
		configProps.put("largeIconURL", largeIconURL);
		configProps.put("smallIconURL", smallIconURL);
		configProps.put("tooltip", tooltip);
		configProps.put("inToolBar", inToolBar);
		configProps.put("inMenuBar", inMenuBar);
		configProps.put("insertSeparatorBefore", insertSeparatorBefore);
		configProps.put("insertSeparatorAfter", insertSeparatorAfter);
		configProps.put("enableFor", enableFor);
		configProps.put("accelerator", accelerator);
		configProps.put("menuGravity", menuGravity);
		configProps.put("toolBarGravity", toolBarGravity);			
	}
	
	
}

