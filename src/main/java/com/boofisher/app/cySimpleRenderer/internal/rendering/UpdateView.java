package com.boofisher.app.cySimpleRenderer.internal.rendering;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.util.Collection;

import javax.swing.JComponent;

import org.apache.log4j.Logger;
import org.cytoscape.application.CyUserLog;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.View;

import com.boofisher.app.cySimpleRenderer.internal.data.GraphicsData;
import com.boofisher.app.cySimpleRenderer.internal.geometric.Vector3;
import com.boofisher.app.cySimpleRenderer.internal.tools.NetworkToolkit;

public class UpdateView implements GraphicsProcedure {
	
	final Logger logger = Logger.getLogger(CyUserLog.NAME);
	GraphicsData graphicsData;
	
	
	@Override
	public void initialize(GraphicsData graphicsData) {
		this.graphicsData = graphicsData;
	}
			
	@Override
	public void execute(GraphicsData graphicsData) {
		Graphics2D g2d = graphicsData.getBufferedImage().createGraphics();
		AffineTransform at = graphicsData.getATranform();
		JComponent component = graphicsData.getContainer();			
		int zoom = graphicsData.getZoomFactor();
		Collection<View<CyNode>> nodeViews = graphicsData.getNetworkView().getNodeViews();
		updateView(nodeViews, graphicsData, g2d, at, zoom);
		
		g2d.dispose();
	}
	
	//translate point of symmetry to the origin
	//scale the image
	//translate back to panels center
	public void updateView(Collection<View<CyNode>> nodeViews, GraphicsData graphicsData, Graphics2D g2d, AffineTransform at, int viewZoom){
		Vector3 center = NetworkToolkit.findCenter(graphicsData.getNetworkView().getNodeViews(), viewZoom);
		Vector3 farthestNode = NetworkToolkit.findFarthestNodeFromCenter(nodeViews, center, viewZoom);
		int worldWidth = (int)(Math.abs(center.x() - farthestNode.x()))*2;
	    int worldHeight  = (int)(Math.abs(center.y() - farthestNode.y()))*2;
		
	    int screenWidth = graphicsData.getScreenWidth();
	    int screenHeight = graphicsData.getScreenHeight();

	    Rectangle worldRec_ = new Rectangle(0, 
	    		0,worldWidth, worldHeight);   
	    
	    double zoomH = screenWidth / worldRec_.getWidth();
	    double zoomV = screenHeight / worldRec_.getHeight();
	    double zoom = Math.min(zoomH, zoomV);
	    
	    at.setToIdentity();
	    at.translate(screenWidth / 2, screenHeight / 2);
	    at.scale(zoom, zoom);
	    at.translate(-(screenWidth/2), -(screenHeight/2));
	    g2d.setTransform(at);
		g2d.setComposite(AlphaComposite.Src);
		logger.warn("worldWidth = " + worldWidth + " worldHeight = " + worldHeight );
		logger.warn("screenWidth = " + (screenWidth) + " (screenHeight) = " + (screenHeight) + " zoom = " + zoom);
		logger.warn("screenWidth/2 = " + (screenWidth/2) + " (screenHeight/2) = " + (screenHeight/2) + " zoom = " + zoom);

	}
	
/*	public void setMiniZoom() {
	    Dimension screenDim = getSize();
	    int worldWidth = (miniCols_ * BioFabricPanel.GRID_SIZE);
	    int worldHeight  = (miniRows_ * BioFabricPanel.GRID_SIZE);
	    worldRec_ = new Rectangle((int)center_.getX() - (worldWidth / 2), 
	                              (int)center_.getY() - (worldHeight / 2),
	                              worldWidth, worldHeight);   
	    clipRec_ = new Rectangle((int)(worldRec_.getX() - BioFabricPanel.GRID_SIZE), (int)(worldRec_.getY() - BioFabricPanel.GRID_SIZE),
	                              (int)(worldRec_.getWidth() + (BioFabricPanel.GRID_SIZE * 2)), (int)(worldRec_.getHeight() + (BioFabricPanel.GRID_SIZE * 2)));
	    double zoomH = screenDim.getWidth() / worldRec_.getWidth();
	    double zoomV = screenDim.getHeight() / worldRec_.getHeight();
	    double zoom = Math.min(zoomH, zoomV);

	    miniTrans_.setToIdentity();
	    miniTrans_.translate(screenDim.getWidth() / 2, screenDim.getHeight() / 2);
	    miniTrans_.scale(zoom, zoom);
	    miniTrans_.translate(-center_.getX(), -center_.getY());
	    bfo_.setMagnifyView(worldRec_);
	    return;
	  }*/

}


