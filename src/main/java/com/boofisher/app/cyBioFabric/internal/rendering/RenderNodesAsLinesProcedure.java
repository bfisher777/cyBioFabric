package com.boofisher.app.cyBioFabric.internal.rendering;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.JComponent;

import org.apache.log4j.Logger;
import org.cytoscape.application.CyUserLog;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.cytoscape.view.presentation.property.NodeShapeVisualProperty;

import com.boofisher.app.cyBioFabric.BioFabricNetwork;
import com.boofisher.app.cyBioFabric.BufferBuilder;
import com.boofisher.app.cyBioFabric.PaintCache;
import com.boofisher.app.cyBioFabric.internal.CyActivator;
import com.boofisher.app.cyBioFabric.internal.data.GraphicsData;
import com.boofisher.app.cyBioFabric.internal.tools.NetworkToolkit;
import com.boofisher.app.cyBioFabric.internal.tools.PairIdentifier;


/********************************************************************************
 * Class will be responsible for rendering the network's nodes and edges
 *  BioFabricPanel 
 *  PaintCache
 *  painter_ = new PaintCache(colGen);
 *  private FabricMagnifyingTool fmt_;
 *  fmt_.setPainters(painter_, selectionPainter_);
 *  painter_.buildObjCache(bfn_.getNodeDefList(), bfn_.getLinkDefList(showShadows), shadeNodes, showShadows, new HashMap(), new HashMap());
 * */

public class RenderNodesAsLinesProcedure implements GraphicsProcedure {
	final Logger logger = Logger.getLogger(CyUserLog.NAME);
	GraphicsData graphicsData;
	boolean isMain;
	
	
	@Override
	public void initialize(GraphicsData graphicsData) {
		this.graphicsData = graphicsData;		
	}
	
	
	
	@Override
	public void execute(GraphicsData graphicsData) {
				
		BioFabricNetwork bfn = CyActivator.bfn;
					
		//Houston we have a problem
		if(CyActivator.bfn == null){
			System.out.println(getClass().getName() + ": BFN is null");
			return;
		}
		
		PaintCache painter  = new PaintCache(bfn.getColorGenerator());
		//TODO understand when PaintCache is used					
		Graphics2D g2 = (Graphics2D)graphicsData.getMyGraphics();   
	    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	    g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);	    
	    
	    //TODO need to add a button for this
	    boolean shadeNodes = true;
	    boolean showShadows = false;
	    painter.buildObjCache(bfn.getNodeDefList(), bfn.getLinkDefList(showShadows), shadeNodes, 
	                           showShadows, new HashMap<String, Rectangle2D>(), new HashMap<String, Rectangle2D>());
	    paintIt(g2, bfn, painter, graphicsData);
	    
	}
	
	/***************************************************************************
	  **
	  ** Drawing routine
	  */	  	  
	  public void paintIt(Graphics2D g, BioFabricNetwork bfn, PaintCache painter, GraphicsData graphicsData) {	      		
	    if (bfn == null) {
	      return;
	    }	    	    
	    
	    //TODO need to fix this
	    Rectangle2D viewInWorld = new Rectangle2D.Double(0, 0, graphicsData.getScreenWidth(), graphicsData.getScreenHeight());
	 
	    //
	    // When we zoom in far enough, we start to draw it instead:
	    //
	    
        Graphics2D g2 = (Graphics2D)g;
        drawingGuts(g2, viewInWorld, painter, graphicsData);

	  }
	  
	  /***************************************************************************
	  **
	  ** Drawing core
	  */
	  
	  private void drawingGuts(Graphics g, Rectangle2D viewRect, PaintCache painter, GraphicsData graphicsData) {
	    Graphics2D g2 = (Graphics2D)g;   
	    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	    g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
	    g2.getTransform();   
	    Rectangle clip = new Rectangle((int)viewRect.getX(), (int)viewRect.getY(), (int)viewRect.getWidth(), (int)viewRect.getHeight());
	    //g2.transform(graphicsData.getATransform());
	    BasicStroke selectedStroke = new BasicStroke(PaintCache.STROKE_SIZE, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER);    
	    g2.setStroke(selectedStroke);
	    
	    // USE THIS INSTEAD???
	    //g2.setBackground(Color.WHITE);
	    //g2.clearRect(clip.x, clip.y, clip.width, clip.height);
	    
	    
	    g2.setPaint(Color.WHITE);
	    g2.drawRect(clip.x, clip.y, clip.width, clip.height); 
	    painter.paintIt(g2, true, clip, false);	   
	    
	    return;
	  }
	  
	  /***************************************************************************
	   **
	   ** Draw Object
	   */  
	   
	   private static class ImageToUse {
	     ImageToUse(BufferedImage image, int stX, int stY) {
	     }
	   } 
}
