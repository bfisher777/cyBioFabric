package com.boofisher.app.cyBioFabric.internal.data;
import java.awt.Graphics;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;
import javax.swing.JPanel;
import com.boofisher.app.cyBioFabric.internal.cytoscape.edges.EdgeAnalyser;
import com.boofisher.app.cyBioFabric.internal.task.TaskFactoryListener;

import org.apache.log4j.Logger;
import com.boofisher.app.cyBioFabric.internal.data.GraphicsSelectionData;
import com.boofisher.app.cyBioFabric.internal.data.PickingData;
import org.cytoscape.application.CyUserLog;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.work.swing.DialogTaskManager;

import com.google.common.eventbus.EventBus;

/**
 * This class represents a data object in which data relevant to the renderer,
 * such as the position of the camera, the number of frames elapsed, the coordinates
 * of the current drag selection box, is stored. Information about the current state 
 * of the network is also stored in this object. 
 * 
 * This class is mostly responsible for storing the renderer's data, and allowing 
 * access to the data through getter and setter objects.
 * 
 * @author Paperwing (Yue Dong)
 */
public class GraphicsData {

	final Logger logger = Logger.getLogger(CyUserLog.NAME);
	/**
	 * This value controls distance scaling when converting from Cytoscape
	 * coordinates (such as from Ding) to the renderer's 3D coordinates
	 */
	public static final float DISTANCE_SCALE = 180.0f; 
	public static final float VERTICAL_VOF = 45.0f;
	public static final int MAX_ZOOM = 100;
	
	private final CyNetworkView networkView;
	private final EventBus eventBus;
	private final VisualLexicon visualLexicon;		
	
	private int mouseCurrentX;
	private int mouseCurrentY;
	private int screenHeight;
	private int screenWidth;	
	
	//private PixelConverter pixelConverter;//used in cy3d to convert between window units and pixel units.
	
	private final JComponent container;
	private final JComponent inputComponent;
	
	private TaskFactoryListener taskFactoryListener;
	private DialogTaskManager taskManager;
	private EdgeAnalyser edgeAnalyser;
	private PickingData pickingData;
	private GraphicsSelectionData selectionData;
	private AffineTransform aTransform;
	private Shape shape;
	
	private boolean showLabels = false;
	
	private int zoom;	
	
	private JPanel surface;
	private BufferedImage bufferedImage;
	private BufferedImage renderedImage;	
	
	private boolean isMain;	
	
	private Graphics myGraphics;
	
	public GraphicsData(CyNetworkView networkView, VisualLexicon visualLexicon, EventBus eventBus,
			JComponent container, JComponent inputComponent) {
		this.networkView = networkView;
		this.eventBus = eventBus;
		this.visualLexicon = visualLexicon;
		this.container = container;
		this.inputComponent = inputComponent;		
		this.aTransform = new AffineTransform();
		this.isMain = false;
				
		zoom = 0;
		selectionData = new GraphicsSelectionData();
		pickingData = new PickingData();
		edgeAnalyser = new EdgeAnalyser();
		bufferedImage = null;	
		renderedImage = null;
	}
	
	public void setATransform(AffineTransform aTransform) {
		this.aTransform = aTransform;
	}

	public AffineTransform getATransform() {
		return aTransform;
	}
	
	public GraphicsSelectionData getSelectionData() {
		return selectionData;
	}
	
	public PickingData getPickingData() {
		return pickingData;
	}
	
	public void setTaskFactoryListener(TaskFactoryListener taskFactoryListener) {
		this.taskFactoryListener = taskFactoryListener;
	}
	
	public void setTaskManager(DialogTaskManager taskManager) {
		this.taskManager = taskManager;
	}
	
	
	public CyNetworkView getNetworkView() {
		return networkView;
	}
	
	public VisualLexicon getVisualLexicon() {
		return visualLexicon;
	}	
	
	public void setScreenHeight(int screenHeight) {
		this.screenHeight = screenHeight;
	}

	public int getScreenHeight() {
		return screenHeight;
	}

	public void setScreenWidth(int screenWidth) {
		this.screenWidth = screenWidth;
	}	
	
	public void setZoomFactor(int scroll){
		this.zoom = scroll;
	}

	public void changeZoomFactor(int scroll) {
		int temp = this.zoom;		
		
		if(scroll >= 0  && (temp + scroll) < MAX_ZOOM){
			this.zoom += scroll;
		}else if(scroll < 0  && (temp + scroll) > 0){
			this.zoom += scroll;			
		}

	}

	public int getScreenWidth() {
		return screenWidth;
	}


	public JComponent getContainer() {
		return container;
	}

	public JComponent getInputComponent() {
		return inputComponent;
	}
	
	public TaskFactoryListener getTaskFactoryListener() {
		return taskFactoryListener;
	}

	public DialogTaskManager getTaskManager() {
		return taskManager;
	}

	public EdgeAnalyser getEdgeAnalyser() {
		return edgeAnalyser;
	}


	public void setMouseCurrentX(int x) {
		this.mouseCurrentX = x;
	}
	
	public int getMouseCurrentX() {
		return mouseCurrentX;
	}
	
	public void setMouseCurrentY(int y) {
		this.mouseCurrentY = y;
	}
	
	public int getMouseCurrentY() {
		return mouseCurrentY;
	}
	
	public EventBus getEventBus() {
		return eventBus;
	}

	public void setShowLabels(boolean showLabels) {
		this.showLabels = showLabels;
	}
	
/*	public PixelConverter getPixelConverter() {
		return pixelConverter;
	}
	
	public void setPixelConverter(PixelConverter pixelConverter) {
		this.pixelConverter = pixelConverter;
	}*/
	
	public boolean getShowLabels() {
		return showLabels;
	}
	
	public int getZoomFactor() {
		return zoom;
	}

	public BufferedImage getBufferedImage() {
		return bufferedImage;
	}
	
	public void setBufferedImage(BufferedImage bi) {
		if(bi != null){
			this.bufferedImage = bi;
		}
	}
	
	public BufferedImage getRenderedImage() {
		return renderedImage;
	}
	
	public void setRenderedImage(BufferedImage bi) {
		if(bi != null){
			this.renderedImage = bi;
		}
	}
	
	public boolean isMain() {
		return isMain;
	}
	
	public void setIsMain(boolean isMain) {
		this.isMain = isMain;
	}
	
	public JPanel getSurface() {
		return surface;
	}
	
	public void setSurface(JPanel surface) {
		this.surface = surface;
	}
	
	public Graphics getMyGraphics() {
		return myGraphics;
	}
	
	public void setMyGraphics(Graphics graphics) {
		this.myGraphics = graphics;
	}
	
	public Shape getMyShape() {
		return shape;
	}
	
	public void setMyShape(Shape shape) {
		this.shape = shape;
	}
}

