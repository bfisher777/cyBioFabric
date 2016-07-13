package com.boofisher.app.cySimpleRenderer.internal.graphics;

import java.util.Collection;

import javax.swing.JComponent;
import javax.swing.RootPaneContainer;

import com.boofisher.app.cySimpleRenderer.internal.eventbus.FitInViewEvent;
import com.boofisher.app.cySimpleRenderer.internal.input.handler.MainEventBusListener;
import com.boofisher.app.cySimpleRenderer.internal.input.handler.MainInputEventListener;

import com.boofisher.app.cySimpleRenderer.internal.input.handler.InputEventListener;

import org.apache.log4j.Logger;
import com.boofisher.app.cySimpleRenderer.internal.picking.DefaultShapePickingProcessor;
import com.boofisher.app.cySimpleRenderer.internal.picking.ShapePickingProcessor;
import org.cytoscape.application.CyUserLog;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.View;

import com.boofisher.app.cySimpleRenderer.internal.data.GraphicsData;//open-gl
import com.boofisher.app.cySimpleRenderer.internal.graphics.AbstractGraphicsConfiguration;//open-gl
import com.boofisher.app.cySimpleRenderer.internal.input.handler.ToolPanel;
import com.boofisher.app.cySimpleRenderer.internal.rendering.RenderEdges;
import com.boofisher.app.cySimpleRenderer.internal.rendering.RenderLabels;
import com.boofisher.app.cySimpleRenderer.internal.rendering.RenderNetwork;
import com.boofisher.app.cySimpleRenderer.internal.rendering.RenderNodes;
import com.boofisher.app.cySimpleRenderer.internal.rendering.UpdateEdges;
import com.boofisher.app.cySimpleRenderer.internal.rendering.UpdateNodes;
import com.boofisher.app.cySimpleRenderer.internal.rendering.UpdateView;
import com.google.common.eventbus.EventBus;

public class MainGraphicsConfiguration extends AbstractGraphicsConfiguration {
	final Logger logger = Logger.getLogger(CyUserLog.NAME);
	private JComponent frame;
	private ToolPanel toolPanel;
	private InputEventListener inputHandler;
	
	//private final ShapePickingProcessor shapePickingProcessor;
	
	public MainGraphicsConfiguration() {
		
		//TODO fix this
		//shapePickingProcessor = new DefaultShapePickingProcessor(new RenderNodesProcedure(), new RenderArcEdgesProcedure());
		
		add(new RenderNetwork());
		//add(new UpdateEdges());//draw updated edges over bImage
		//add(new UpdateNodes());//draw updated nodes over bImage
		//add(new UpdateView()); //perform transformations on view
		//add(new RenderLabels());//add JLabel over top of panel and draw labels on it, then hide or show
	}
	
	
	@Override
	public void initializeFrame(JComponent component, JComponent inputComponent) {
		this.frame = component;
		if(component instanceof RootPaneContainer) {
			logger.warn("Added tool bar to main");
			this.toolPanel = new ToolPanel((RootPaneContainer)component, inputComponent);
			graphicsData.setIsMain(true);
		}
	}
	
	
	@Override
	public void initialize(GraphicsData graphicsData) {
		super.initialize(graphicsData);				
		
		// Input handler		
		inputHandler = MainInputEventListener.attach(graphicsData.getInputComponent(), graphicsData);
		
		// EventBus
		EventBus eventBus = graphicsData.getEventBus();		
		
		if(toolPanel != null) {
			toolPanel.setEventBus(eventBus);
		}
		
		MainEventBusListener eventBusListener = new MainEventBusListener(graphicsData);
		eventBus.register(eventBusListener);		

		Collection<View<CyNode>> nodeViews = graphicsData.getNetworkView().getNodeViews();				
		eventBusListener.handleFitInViewEvent(new FitInViewEvent(nodeViews));
	}
	
	
	//TODO: still needed here?
	/*@Override
	public void update() {
		shapePickingProcessor.processPicking(graphicsData);
	}*/

	
	@Override
	public void dispose() {
		inputHandler.dispose();
	}
	

	@Override
	public String toString() {
		return "MainGraphicsConfiguration";
	}

}