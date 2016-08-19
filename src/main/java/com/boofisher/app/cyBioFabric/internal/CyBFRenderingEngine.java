package com.boofisher.app.cyBioFabric.internal;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.print.Printable;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Properties;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.RootPaneContainer;
import javax.swing.plaf.basic.BasicInternalFrameUI;

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
import com.boofisher.app.cyBioFabric.internal.events.BioFabricNetworkViewAddedHandler;
import com.boofisher.app.cyBioFabric.internal.events.BioFabricNetworkViewToBeDestroyedHandler;
import com.boofisher.app.cyBioFabric.internal.graphics.BirdsEyeGraphicsConfiguration;
import com.boofisher.app.cyBioFabric.internal.graphics.GraphicsConfiguration;
import com.boofisher.app.cyBioFabric.internal.graphics.ThumbnailGraphicsConfiguration;
import com.boofisher.app.cyBioFabric.internal.layouts.BioFabricLayoutInterface;
import com.boofisher.app.cyBioFabric.internal.task.TaskFactoryListener;

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


public class CyBFRenderingEngine implements RenderingEngine<CyNetwork> {
	
	final Logger logger = Logger.getLogger(CyUserLog.NAME);
	
	private final CyBFNetworkView networkView;
	private final VisualLexicon visualLexicon;
		
	private BioFabricWindow bioFabricWindow;
	private BioFabricWindow selectionWindow;	
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
			
			//handle shutdown events
			registerHandlers(bioFabricApplication);
			//listen for cytoscape button and menu events and pass along to biofabric
			registerViewListeners(new BioFabricZoomInListener(bioFabricWindow),
					new BioFabricZoomOutListener(bioFabricWindow), new BioFabricFitContentListener(bioFabricWindow));
			
			resetDefaultLayout(layoutAlgorithmManager, defaultLayout);		
			
		} else if(configuration instanceof ThumbnailGraphicsConfiguration){	
			bioFabricWindow = bioFabricApplication.getBioFabricWindow();
			
			//TODO: need to create a Thumbnail Biofabric JPanel to add to container			
			URL ugif = getClass().getResource("/images/BioFab16White.gif"); 
			ImageIcon myPicture = new ImageIcon(ugif);
						
			Dimension dimen = new Dimension(container.getPreferredSize());
			
			Image scaledImage = getScaledImage(myPicture.getImage(), (int)dimen.getWidth(), (int)dimen.getHeight());
			myPicture = new ImageIcon(scaledImage);
			JLabel picLabel = new JLabel(myPicture);
			
			container.setLayout(new BorderLayout());			
			container.add(picLabel, BorderLayout.CENTER);
			
		}else if(configuration instanceof BirdsEyeGraphicsConfiguration){			
			bioFabricWindow = bioFabricApplication.getBioFabricWindow();
			container.setLayout(new BorderLayout());			
			container.add(bioFabricWindow.getOverview(), BorderLayout.CENTER);									
		}				
	}
	
	/*
	 * Scale an Image
	 * http://stackoverflow.com/questions/6714045/how-to-resize-jlabel-imageicon
	 * 
	 * */
	private Image getScaledImage(Image srcImg, int w, int h){
		if(w <= 0 || h <= 0){  w = h = 150; }
		
	    BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
	    Graphics2D g2 = resizedImg.createGraphics();

	    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	    g2.drawImage(srcImg, 0, 0, w, h, null);
	    g2.dispose();

	    return resizedImg;
	}
	
	/*
	 * Method will return Cytoscape to original default layout
	 * 
	 * */
	private void resetDefaultLayout(CyLayoutAlgorithmManager layoutAlgorithmManager, 
            String defaultLayout){
		//reset default layout
		CyLayoutAlgorithm layout = layoutAlgorithmManager.getLayout(defaultLayout);
		if(layout != null  && !(layout instanceof BioFabricLayoutInterface)){			
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
	 * Method will add the view listeners to the networkView
	 * 
	 * */
	private void registerViewListeners(BioFabricZoomInListener zoomIn, 
			BioFabricZoomOutListener zoomOut, BioFabricFitContentListener fitContent){		
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

