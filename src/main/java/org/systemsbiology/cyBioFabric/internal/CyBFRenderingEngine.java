package org.systemsbiology.cyBioFabric.internal;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterGraphics;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.RootPaneContainer;
import javax.swing.plaf.basic.BasicInternalFrameUI;

import org.apache.log4j.Logger;
import org.cytoscape.application.CyUserLog;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.view.layout.CyLayoutAlgorithm;
import org.cytoscape.view.layout.CyLayoutAlgorithmManager;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.presentation.RenderingEngine;
import org.cytoscape.work.swing.DialogTaskManager;
import org.systemsbiology.cyBioFabric.internal.biofabric.app.BioFabricApplication;
import org.systemsbiology.cyBioFabric.internal.biofabric.app.BioFabricWindow;
import org.systemsbiology.cyBioFabric.internal.biofabric.cmd.CommandSet;
import org.systemsbiology.cyBioFabric.internal.biofabric.model.BioFabricNetwork;
import org.systemsbiology.cyBioFabric.internal.biofabric.ui.display.BioFabricOverview;
import org.systemsbiology.cyBioFabric.internal.cytoscape.view.BNVisualPropertyValue;
import org.systemsbiology.cyBioFabric.internal.cytoscape.view.BioFabricVisualLexicon;
import org.systemsbiology.cyBioFabric.internal.cytoscape.view.CyBFNetworkView;
import org.systemsbiology.cyBioFabric.internal.cytoscape.view.listeners.ApplyPreferredLayoutListener;
import org.systemsbiology.cyBioFabric.internal.cytoscape.view.listeners.BioFabricFitContentListener;
import org.systemsbiology.cyBioFabric.internal.cytoscape.view.listeners.BioFabricZoomInListener;
import org.systemsbiology.cyBioFabric.internal.cytoscape.view.listeners.BioFabricZoomOutListener;
import org.systemsbiology.cyBioFabric.internal.cytoscape.view.listeners.BioFabricZoomSelectedListener;
import org.systemsbiology.cyBioFabric.internal.cytoscape.view.listeners.BioFabricZoomSelectedListenerInterface;
import org.systemsbiology.cyBioFabric.internal.events.BioFabricNetworkViewAddedHandler;
import org.systemsbiology.cyBioFabric.internal.events.BioFabricNetworkViewToBeDestroyedHandler;
import org.systemsbiology.cyBioFabric.internal.graphics.BirdsEyeGraphicsConfiguration;
import org.systemsbiology.cyBioFabric.internal.graphics.GraphicsConfiguration;
import org.systemsbiology.cyBioFabric.internal.graphics.ThumbnailGraphicsConfiguration;
import org.systemsbiology.cyBioFabric.internal.layouts.BioFabricLayoutInterface;
import org.systemsbiology.cyBioFabric.internal.task.TaskFactoryListener;


public class CyBFRenderingEngine implements RenderingEngine<CyNetwork>, Printable {
	
	final Logger logger = Logger.getLogger(CyUserLog.NAME);
	
	private final CyBFNetworkView networkView;
	private final VisualLexicon visualLexicon;
		
	private final BioFabricNetworkViewAddedHandler addNetworkHandler; 
    private final BioFabricNetworkViewToBeDestroyedHandler destroyNetworkHandler;    
	private final BioFabricApplication bioFabricApplication;
	        
	
	public CyBFRenderingEngine(
			JComponent component,
			JComponent inputComponent,
			CyBFNetworkView viewModel, 
			VisualLexicon visualLexicon, 
			GraphicsConfiguration configuration,
			TaskFactoryListener taskFactoryListener, 
			DialogTaskManager taskManager,
			BioFabricNetworkViewAddedHandler addNetworkHandler, 
			BioFabricNetworkViewToBeDestroyedHandler destroyNetworkHandler,
			CyLayoutAlgorithmManager layoutAlgorithmManager,
			String defaultLayout) {
		
		this.networkView = viewModel;
		this.visualLexicon = visualLexicon;
		this.addNetworkHandler = addNetworkHandler;
		this.destroyNetworkHandler = destroyNetworkHandler;
		this.bioFabricApplication = networkView.getBioFabricApplication();	
		
		setUpCanvas(component, inputComponent, configuration, taskFactoryListener, taskManager, 
				layoutAlgorithmManager, defaultLayout);
	}
	
	
	/** Set up the canvas by creating and placing it, along with a Graphics
	 * object, into the container
	 * 
	 * @param container A container in the GUI window used to contain
	 * the rendered results
	 */
	private void setUpCanvas(JComponent container, JComponent inputComponent, 
			                 GraphicsConfiguration configuration, TaskFactoryListener taskFactoryListener, 
			                 DialogTaskManager taskManager, CyLayoutAlgorithmManager layoutAlgorithmManager, 
			                 String defaultLayout) {				
										
		if (container instanceof RootPaneContainer) {	
					
			bioFabricApplication.attachInputComponent(inputComponent);
			final HashMap<String, Object> args = new HashMap<String, Object>();		
			bioFabricApplication.launch(args);
						
			BioFabricWindow bioFabricWindow = bioFabricApplication.getBioFabricWindow();
			BioFabricWindow selectionWindow = bioFabricApplication.getSelectionWindow();
			
			clearBorder(bioFabricWindow, selectionWindow);
			
			RootPaneContainer rootPaneContainer = (RootPaneContainer) container;
			Container pane = rootPaneContainer.getContentPane();
			pane.setLayout(new BorderLayout());
			pane.add(bioFabricWindow, BorderLayout.CENTER);
			
			BNVisualPropertyValue bnvpv = networkView.getVisualProperty(BioFabricVisualLexicon.BIOFABRIC_NETWORK);
			BioFabricNetwork bfn = bnvpv.getBioFabricNetwork();			
			
			installBioFabricNetwork(bfn, bioFabricWindow, selectionWindow, true, taskManager);
			
			//handle shutdown events
			registerHandlers(bioFabricApplication);
			
			//unique name of the Command Set
			String name = bioFabricWindow.COMMAND_NAME;
			//listen for cytoscape button and menu events and pass along to biofabric
			registerViewListeners(new BioFabricZoomInListener(name), 
					new ApplyPreferredLayoutListener(networkView, layoutAlgorithmManager.getDefaultLayout(), layoutAlgorithmManager, taskManager),
					new BioFabricZoomOutListener(name), new BioFabricFitContentListener(name), new BioFabricZoomSelectedListener(name));															
			
			resetDefaultLayout(layoutAlgorithmManager, defaultLayout);		
			
		} else if(configuration instanceof ThumbnailGraphicsConfiguration){
			
			//TODO: figure out how to scale this to fit the container properly
			BioFabricWindow bioFabricWindow = bioFabricApplication.getBioFabricWindow();
			BioFabricOverview overView = bioFabricWindow.getThumbnailView();
			
			container.setLayout(new BorderLayout());			
			container.add(overView);			
		}else if(configuration instanceof BirdsEyeGraphicsConfiguration){			
			BioFabricWindow bioFabricWindow = bioFabricApplication.getBioFabricWindow();
			
			container.setLayout(new BorderLayout());			
			container.add(bioFabricWindow.getNAC().getFabricOverviewPanel(), BorderLayout.CENTER);			
		}				
	}
	
	/*
	 * Method will return Cytoscape to original default layout
	 * Method will not allow a Biofabric layout to get set to the default layout as they do not
	 * work with any of the other renderers.
	 * 
	 * */
	private void resetDefaultLayout(CyLayoutAlgorithmManager layoutAlgorithmManager, 
            String defaultLayout){
		//reset default layout
		CyLayoutAlgorithm layout = layoutAlgorithmManager.getLayout(defaultLayout);
		if(!(layout instanceof BioFabricLayoutInterface)){			
			layoutAlgorithmManager.setDefaultLayout(layout);
		}else{
			layout = layoutAlgorithmManager.getLayout("force-directed");
			layoutAlgorithmManager.setDefaultLayout(layout);
		}
	}
	
	/*
	 * Method will add the biofabric application to the handler and ensure it is
	 * destroyed properly by calling the bfa shut down procedure
	 * 
	 * */
	private void registerHandlers(BioFabricApplication bfa){
		addNetworkHandler.registerApplication(networkView.getSUID(), bfa);//TODO: this is not used remove or implement
		destroyNetworkHandler.registerApplication(networkView.getSUID(), bfa);			
	}

	/*
	 * Method will add the view listeners to the networkView, used to handle cytoscape button click
	 * 
	 * */
	private void registerViewListeners(BioFabricZoomInListener zoomIn, ApplyPreferredLayoutListener applyLayout,
		BioFabricZoomOutListener zoomOut, BioFabricFitContentListener fitContent, BioFabricZoomSelectedListenerInterface zoomSelected){		
		networkView.addBioFabricViewListener(zoomIn);
		networkView.addBioFabricViewListener(zoomOut);
		networkView.addBioFabricViewListener(fitContent);	
		networkView.addBioFabricViewListener(applyLayout);
		networkView.addBioFabricViewListener(zoomSelected);
	}
	
	/* 
	 * http://stackoverflow.com/questions/7218608/hiding-title-bar-of-jinternalframe-java
	 * http://stackoverflow.com/questions/3620970/how-to-remove-the-borders-in-jinternalframe
	 * */
	private void clearBorder(BioFabricWindow bioFabricWindow, BioFabricWindow selectionWindow){
		
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
	public void installBioFabricNetwork(BioFabricNetwork bfn, BioFabricWindow bfw, BioFabricWindow selectionWindow, 
			boolean showNav, DialogTaskManager taskManager){
		  
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
		  bfw.setDialogTaskManager(taskManager);//TODO used for displaying pop up menu not implemented yet
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
	
	/**
	 * For export image function.
	 * 
	 * @return A Printable object suitable for submission to a printer.
	 */
	@Override
	public Printable createPrintable() {
		System.out.println("Returning the printable renderer"); 
		return this;
	}

	/**
	 * Render an {@linkplain Image} object from current visualization.
	 * 
	 * @param width width of the image
	 * @param height height of the image
	 * 
	 * @return Image object created from current window.
	 */
	@Override
	public Image createImage(int width, int height) {
		System.out.println("Trying to createImage");
		Image image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB_PRE);		

		/*Dimension panelSize = panel.getSize();
		
		panel.setSize(width, height);
		panel.paint(image.getGraphics());
		panel.setSize(panelSize);*/
		
		return image;
	}

	/**
	 * Create {@link Icon} object for the given VisualProperty value.
	 * 
	 * @param <V> Data type, such as Color, String, Double, etc.
	 * 
	 * @param vp VisualProperty to be rendered as Icon.
	 * @param value Value for the Icon.  For example, if V is shape, this can be rectangle, triangle, and so on.
	 * @param width width of the icon
	 * @param height height of the icon
	 * 
	 * @return Icon rendered by this engine.
	 */
	@Override
	public <V> Icon createIcon(VisualProperty<V> vp, V value, int width, int height) {
		return null;
	}

	/**
	 * Render presentation on the given Java 2D Canvas.
	 * 
	 * @param printCanvas Graphics object provided by vector
	 */
	@Override
	public void printCanvas(java.awt.Graphics printCanvas) {		
		bioFabricApplication.getBioFabricWindow().printComponents(printCanvas);
	}
	
	/**
	 * This method must return the same ID which is returned by the associated
	 * implementation of NetworkViewRenderer.
	 * @return Must return the same value as NetworkViewRenderer.getId.
	 */
	@Override
	public String getRendererId() {
		return CyBFNetworkViewRenderer.ID;
	}
	
	@Override
	public void dispose() {
	}


	/**
	 * Code taken from DGraphView line 1946
     * Prints the page at the specified index into the specified
     * {@link Graphics} context in the specified
     * format.  A <code>PrinterJob</code> calls the
     * <code>Printable</code> interface to request that a page be
     * rendered into the context specified by
     * <code>graphics</code>.  The format of the page to be drawn is
     * specified by <code>pageFormat</code>.  The zero based index
     * of the requested page is specified by <code>pageIndex</code>.
     * If the requested page does not exist then this method returns
     * NO_SUCH_PAGE; otherwise PAGE_EXISTS is returned.
     * The <code>Graphics</code> class or subclass implements the
     * {@link PrinterGraphics} interface to provide additional
     * information.  If the <code>Printable</code> object
     * aborts the print job then it throws a {@link PrinterException}.
     * @param graphics the context into which the page is drawn
     * @param pageFormat the size and orientation of the page being drawn
     * @param pageIndex the zero based index of the page to be drawn
     * @return PAGE_EXISTS if the page is rendered successfully
     *         or NO_SUCH_PAGE if <code>pageIndex</code> specifies a
     *         non-existent page.
     * @exception java.awt.print.PrinterException
     *         thrown when the print job is terminated.
     */
	@Override
	public int print(Graphics g, PageFormat pageFormat, int page) throws PrinterException {
		if(g == null){
			System.out.println("Graphics is null");
		}else{
			
		}
		
		if (page == 0) {
			JComponent panel = bioFabricApplication.getBioFabricWindow();
			((Graphics2D) g).translate(pageFormat.getImageableX(),
					pageFormat.getImageableY());

			// make sure the whole image on the screen will fit to the printable
			// area of the paper
			double image_scale = Math
					.min(pageFormat.getImageableWidth()
							/ panel.getWidth(),
							pageFormat.getImageableHeight()
									/ panel.getHeight());

			if (image_scale < 1.0d) {
				((Graphics2D) g).scale(image_scale, image_scale);
			}

			// old school
			// g.clipRect(0, 0, getComponent().getWidth(),
			// getComponent().getHeight());
			// getComponent().print(g);

			// from InternalFrameComponent
			g.clipRect(0, 0, panel.getWidth(),
					panel.getHeight());
			
			panel.print(g);
			System.out.println("Page printed");
			return PAGE_EXISTS;
		} else {
			System.out.println("No Such Page Exists");
			return NO_SUCH_PAGE;
		}
	}
}

