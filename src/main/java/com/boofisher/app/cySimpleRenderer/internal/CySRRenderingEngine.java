package com.boofisher.app.cySimpleRenderer.internal;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.print.Printable;
import java.util.Properties;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.RootPaneContainer;

import com.boofisher.app.cySimpleRenderer.internal.cytoscape.view.CySRNetworkView;
import com.boofisher.app.cySimpleRenderer.internal.data.GraphicsData;
import com.boofisher.app.cySimpleRenderer.internal.eventbus.EventBusProvider;
import com.boofisher.app.cySimpleRenderer.internal.graphics.BirdsEyePanel;
import com.boofisher.app.cySimpleRenderer.internal.graphics.GraphicsConfiguration;
import com.boofisher.app.cySimpleRenderer.internal.graphics.MainPanel;
import com.boofisher.app.cySimpleRenderer.internal.task.TaskFactoryListener;

import org.apache.log4j.Logger;
import org.cytoscape.application.CyUserLog;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.presentation.RenderingEngine;
import org.cytoscape.work.swing.DialogTaskManager;

public class CySRRenderingEngine implements RenderingEngine<CyNetwork> {
	
	final Logger logger = Logger.getLogger(CyUserLog.NAME);
	
	private final CySRNetworkView networkView;
	private final VisualLexicon visualLexicon;
	
	private JPanel panel;	
	
	
	public CySRRenderingEngine(
			JComponent component,
			JComponent inputComponent,
			CySRNetworkView viewModel, 
			VisualLexicon visualLexicon, 
			EventBusProvider eventBusProvider, 
			GraphicsConfiguration configuration,
			TaskFactoryListener taskFactoryListener, 
			DialogTaskManager taskManager) {
		
		this.networkView = viewModel;
		this.visualLexicon = visualLexicon;
		
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
						
			MainPanel mp = new MainPanel(networkView, visualLexicon, eventBusProvider, 
					configuration, inputComponent);	
			panel = mp;			
			mp.setIgnoreRepaint(false); 
			mp.setDoubleBuffered(true);			
			
			RootPaneContainer rootPaneContainer = (RootPaneContainer) container;
			Container pane = rootPaneContainer.getContentPane();
			pane.setLayout(new BorderLayout());
			pane.add(panel, BorderLayout.CENTER);
		} else {
			BirdsEyePanel bp = new BirdsEyePanel(networkView, visualLexicon, eventBusProvider,
					configuration, inputComponent); 	
			panel = bp;								
			bp.setIgnoreRepaint(false); 
			bp.setDoubleBuffered(true);					
			
			container.setLayout(new BorderLayout());
			container.add(panel, BorderLayout.CENTER);
		}
						
		// When networkView.updateView() is called it will repaint all containers it owns
		networkView.addContainer(panel); 
		
		configuration.initializeFrame(container, inputComponent);
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

		Dimension panelSize = panel.getSize();
		
		panel.setSize(width, height);
		panel.paint(image.getGraphics());
		panel.setSize(panelSize);
		
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
		return CySRNetworkViewRenderer.ID;
	}
	
	@Override
	public void dispose() {
	}
}

