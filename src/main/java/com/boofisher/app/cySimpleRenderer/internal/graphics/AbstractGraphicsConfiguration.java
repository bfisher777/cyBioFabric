package com.boofisher.app.cySimpleRenderer.internal.graphics;

import java.util.LinkedList;
import java.util.List;

//import javax.media.opengl.GL2;
import javax.swing.JComponent;

import  com.boofisher.app.cySimpleRenderer.internal.data.GraphicsData;
import  com.boofisher.app.cySimpleRenderer.internal.graphics.GraphicsConfiguration;
import  com.boofisher.app.cySimpleRenderer.internal.rendering.GraphicsProcedure;

public abstract class AbstractGraphicsConfiguration implements GraphicsConfiguration {

	private List<GraphicsProcedure> renderProcedures = new LinkedList<GraphicsProcedure>();
	
	protected GraphicsData graphicsData;
	
	
	protected void add(GraphicsProcedure procedure) {
		if(!renderProcedures.contains(procedure))
			renderProcedures.add(procedure);
	}
	
	@Override
	public void initialize(GraphicsData graphicsData) {
		this.graphicsData = graphicsData;
		for (GraphicsProcedure proc : renderProcedures) {
			proc.initialize(graphicsData);
		}
		
	}	
	
	
	@Override
	public void drawScene() {
			
		for (GraphicsProcedure proc : renderProcedures) {
			proc.execute(graphicsData);
		}		
	}
	
	@Override
	public void update() {
	}
	
	@Override
	public void dispose() {
	}
	
	@Override
	public void initializeFrame(JComponent container, JComponent inputComponent) {
	}

}
