package com.boofisher.app.cySimpleRenderer.internal.rendering;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.image.BufferedImage;

import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;

import com.boofisher.app.cySimpleRenderer.internal.data.GraphicsData;
import com.boofisher.app.cySimpleRenderer.internal.graphics.RenderingPanel;

public class UpdateNodes implements GraphicsProcedure {
	
	GraphicsData graphicsData;
	
	
	@Override
	public void initialize(GraphicsData graphicsData) {
		this.graphicsData = graphicsData;
	}
			
	@Override
	public void execute(GraphicsData graphicsData) {
			
		updateNodes();
	}
	
	public void updateNodes(){
		
	}
}

