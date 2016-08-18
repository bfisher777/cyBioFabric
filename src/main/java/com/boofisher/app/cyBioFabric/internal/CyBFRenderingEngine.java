package com.boofisher.app.cyBioFabric.internal;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.print.Printable;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.RootPaneContainer;
import javax.swing.plaf.basic.BasicInternalFrameUI;

import com.boofisher.app.cyBioFabric.internal.biofabric.BioFabricNetworkViewAddedHandler;
import com.boofisher.app.cyBioFabric.internal.biofabric.BioFabricNetworkViewToBeDestroyedHandler;
import com.boofisher.app.cyBioFabric.internal.biofabric.app.BioFabricApplication;
import com.boofisher.app.cyBioFabric.internal.biofabric.app.BioFabricWindow;
import com.boofisher.app.cyBioFabric.internal.biofabric.model.BioFabricNetwork;
import com.boofisher.app.cyBioFabric.internal.biofabric.cmd.CommandSet;
import com.boofisher.app.cyBioFabric.internal.cytoscape.view.BNVisualPropertyValue;
import com.boofisher.app.cyBioFabric.internal.cytoscape.view.BioFabricVisualLexicon;
import com.boofisher.app.cyBioFabric.internal.cytoscape.view.CyBFNetworkView;
import com.boofisher.app.cyBioFabric.internal.cytoscape.view.listeners.BioFabricFitContentListener;
import com.boofisher.app.cyBioFabric.internal.cytoscape.view.listeners.BioFabricZoomInListener;
import com.boofisher.app.cyBioFabric.internal.cytoscape.view.listeners.BioFabricZoomOutListener;
import com.boofisher.app.cyBioFabric.internal.eventbus.EventBusProvider;
import com.boofisher.app.cyBioFabric.internal.graphics.GraphicsConfiguration;
import com.boofisher.app.cyBioFabric.internal.graphics.RenderingPanel;
import com.boofisher.app.cyBioFabric.internal.task.TaskFactoryListener;

import org.apache.log4j.Logger;
import org.cytoscape.application.CyUserLog;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.presentation.RenderingEngine;
import org.cytoscape.work.swing.DialogTaskManager;


public class CyBFRenderingEngine implements RenderingEngine<CyNetwork> {
	
	final Logger logger = Logger.getLogger(CyUserLog.NAME);
	
	private final CyBFNetworkView networkView;
	private final VisualLexicon visualLexicon;
	
	private RenderingPanel panel;	
	private BioFabricWindow bioFabricWindow;
	private BioFabricWindow selectionWindow;	
	private final BioFabricNetworkViewAddedHandler addNetworkHandler; 
    private final BioFabricNetworkViewToBeDestroyedHandler destroyNetworkHandler;
    private final int count;
	        
	
	public CyBFRenderingEngine(
			JComponent component,
			JComponent inputComponent,
			CyBFNetworkView viewModel, 
			VisualLexicon visualLexicon, 
			EventBusProvider eventBusProvider, 
			GraphicsConfiguration configuration,
			TaskFactoryListener taskFactoryListener, 
			DialogTaskManager taskManager,
			BioFabricNetworkViewAddedHandler addNetworkHandler, 
			BioFabricNetworkViewToBeDestroyedHandler destroyNetworkHandler,                      
            int count) {
		
		this.networkView = viewModel;
		this.visualLexicon = visualLexicon;
		this.addNetworkHandler = addNetworkHandler;
		this.destroyNetworkHandler = destroyNetworkHandler;
		
		
		this.count = count; //will count the number of biofabric applications created giving a unique number to each application
		
		setUpCanvas(component, inputComponent, configuration, eventBusProvider, taskFactoryListener, taskManager);
	}
	
	
	/** Set up the canvas by creating and placing it, along with a Graphics
	 * object, into the container
	 * 
	 * @param container A container in the GUI window used to contain
	 * the rendered results
	 */
	private void setUpCanvas(JComponent container, JComponent inputComponent, 
			                 GraphicsConfiguration configuration, EventBusProvider eventBusProvider, 
			                 TaskFactoryListener taskFactoryListener, DialogTaskManager taskManager) {				
										
		if (container instanceof RootPaneContainer) {	
			//run the bioFabricApplication once per renderer
			BioFabricApplication bioFabricApplication = new BioFabricApplication(false, count, inputComponent);			
			
			final HashMap<String, Object> args = new HashMap<String, Object>();		
			bioFabricApplication.launch(args);
						
			bioFabricWindow = bioFabricApplication.getBioFabricWindow();
			selectionWindow = bioFabricApplication.getSelectionWindow();
			
			clearBorder();
			
			RootPaneContainer rootPaneContainer = (RootPaneContainer) container;
			Container pane = rootPaneContainer.getContentPane();
			pane.setLayout(new BorderLayout());
			pane.add(bioFabricWindow, BorderLayout.CENTER);
			
			BNVisualPropertyValue bnvpv = networkView.getVisualProperty(BioFabricVisualLexicon.BIOFABRIC_NETWORK);
			BioFabricNetwork bfn = bnvpv.getBioFabricNetwork();
			
			//this happens when a non-biofabric layout is used and the layout needs to be programmatically called
			//in the class CyBFRenderingEnginFactory
			// TODO Need to create a listener, remove the Thread.sleep
			while(bfn == null){
				try {
					System.err.println("bfn is null, going to sleep");
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				bfn = bnvpv.getBioFabricNetwork();
			}
			
			installBioFabricNetwork(bfn, bioFabricWindow, selectionWindow, true);
			
			registerHandlers(bioFabricApplication, new BioFabricZoomInListener(bioFabricWindow),
					new BioFabricZoomOutListener(bioFabricWindow), new BioFabricFitContentListener(bioFabricWindow));
			logger.warn("Added main view");			
		} else {			
			container.setVisible(false);
			/*// When networkView.updateView() is called it will repaint all containers it owns
			networkView.addContainer(panel); 
			panel = new RenderingPanel(networkView, visualLexicon, eventBusProvider, 
					configuration, inputComponent);
			panel.setIgnoreRepaint(false); 
			panel.setDoubleBuffered(true);
			
			container.setLayout(new BorderLayout());
			container.add(panel, BorderLayout.CENTER);
			
			//adds tool bar to frame
			configuration.initializeFrame(container, inputComponent);
			//set up event listeners / handlers / fit graph in view
			configuration.initialize(panel.getGraphicsData());
			*/
			logger.warn("Added birds eye view");
		}							
	}
	
	/*
	 * Method will add the biofabric application to the handler and ensure it is
	 * destroyed properly by calling the bfa shut down procedure
	 * */
	private void registerHandlers(BioFabricApplication bfa, BioFabricZoomInListener zoomIn, 
			BioFabricZoomOutListener zoomOut, BioFabricFitContentListener fitContent){
		addNetworkHandler.registerApplication(networkView.getSUID(), bfa);//TODO: this is not used remove or implement
		destroyNetworkHandler.registerApplication(networkView.getSUID(), bfa);
		
		networkView.addBioFabricViewListener(zoomIn);
		networkView.addBioFabricViewListener(zoomOut);
		networkView.addBioFabricViewListener(fitContent);		
	}

	
	/* http://stackoverflow.com/questions/7218608/hiding-title-bar-of-jinternalframe-java
	 * http://stackoverflow.com/questions/3620970/how-to-remove-the-borders-in-jinternalframe
	 * */
	private void clearBorder(){
		
		((BasicInternalFrameUI)bioFabricWindow.getUI()).setNorthPane(null);	
		((BasicInternalFrameUI)selectionWindow.getUI()).setNorthPane(null);
		
		((BasicInternalFrameUI)bioFabricWindow.getUI()).setWestPane(null);	
		((BasicInternalFrameUI)selectionWindow.getUI()).setWestPane(null);
		
		((BasicInternalFrameUI)bioFabricWindow.getUI()).setEastPane(null);	
		((BasicInternalFrameUI)selectionWindow.getUI()).setEastPane(null);
		
		((BasicInternalFrameUI)bioFabricWindow.getUI()).setSouthPane(null);	
		((BasicInternalFrameUI)selectionWindow.getUI()).setSouthPane(null);
		
		bioFabricWindow.setBorder(null);
		selectionWindow.setBorder(null);
		
	}
	
	
	//TODO not sure if I need to call this on the selectionWindow
	//Disable menu toolbar in biofabric network manually if desired
	public void installBioFabricNetwork(BioFabricNetwork bfn, BioFabricWindow bfw, BioFabricWindow selectionWindow, boolean showNav){
		  
		if(bfn != null){			
			
		    bfw.getFabricPanel().installModel(bfn);
		    selectionWindow.getFabricPanel().installModel(bfn);
		    
		    //command name is a unique name for each CommandSet created
		    CommandSet fc = CommandSet.getCmds(bfw.COMMAND_NAME);
		    
		    //build the nav window image?
		    try {
				fc.newModelOperations(bfn.getBuildData(), true);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		  }else{
			  System.err.println("Attempting to install a null BioFabricNetwork");
		  }
		  
		  bfw.showNavAndControl(showNav);
	  }
	
	@Override
	public View<CyNetwork> getViewModel() {
		return networkView;
	}

	@Override
	public VisualLexicon getVisualLexicon() {
		return visualLexicon;
	}

	@Override
	public Properties getProperties() {
		return null;
	}
	
	@Override
	public Printable createPrintable() {
		return null;
	}

	@Override
	public Image createImage(int width, int height) {
		Image image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB_PRE);		

		/*Dimension panelSize = panel.getSize();
		
		panel.setSize(width, height);
		panel.paint(image.getGraphics());
		panel.setSize(panelSize);*/
		
		return image;
	}

	@Override
	public <V> Icon createIcon(VisualProperty<V> vp, V value, int width, int height) {
		return null;
	}

	@Override
	public void printCanvas(java.awt.Graphics printCanvas) {
	}
	
	@Override
	public String getRendererId() {
		return CyBFNetworkViewRenderer.ID;
	}
	
	@Override
	public void dispose() {
	}
}

