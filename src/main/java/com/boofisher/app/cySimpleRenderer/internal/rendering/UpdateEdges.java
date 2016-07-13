package com.boofisher.app.cySimpleRenderer.internal.rendering;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Set;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;

import com.boofisher.app.cySimpleRenderer.internal.data.GraphicsData;
import com.boofisher.app.cySimpleRenderer.internal.graphics.RenderingPanel;
import com.boofisher.app.cySimpleRenderer.internal.tools.NetworkToolkit;
import com.boofisher.app.cySimpleRenderer.internal.tools.PairIdentifier;

public class UpdateEdges implements GraphicsProcedure {
	
	GraphicsData graphicsData;
	boolean isMain;
	
	
	@Override
	public void initialize(GraphicsData graphicsData) {
		this.graphicsData = graphicsData;		
	}
	
	
	
	@Override
	public void execute(GraphicsData graphicsData) {
		

		updateEdges();
	}
	
	public void updateEdges(){
	
	}

}
