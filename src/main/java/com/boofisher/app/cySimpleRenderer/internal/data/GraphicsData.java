package com.boofisher.app.cySimpleRenderer.internal.data;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;
import javax.swing.JScrollPane;

import com.boofisher.app.cySimpleRenderer.internal.cytoscape.edges.EdgeAnalyser;
import com.boofisher.app.cySimpleRenderer.internal.task.TaskFactoryListener;

import org.apache.log4j.Logger;
import org.cytoscape.application.CyUserLog;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
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
	
	
	private CyNetworkView networkView;
	private EventBus eventBus;
	private VisualLexicon visualLexicon;	
	private final int MAX_ZOOM = 100;
	
	private int mouseCurrentX;
	private int mouseCurrentY;
	private int screenHeight;
	private int screenWidth;
	
	//private PixelConverter pixelConverter;//used in cy3d to convert between window units and pixel units.
	
	private JComponent container;
	private JComponent inputComponent;
	
	private TaskFactoryListener taskFactoryListener;
	private DialogTaskManager taskManager;
	private EdgeAnalyser edgeAnalyser;
	
	private boolean showLabels = false;
	
	private int mainZoom;
	private int birdZoom;
	
	private BufferedImage mainBI;
	private BufferedImage birdsEyeBI;
	
	private JScrollPane scrollPane;
	private boolean isMain;
	
	public GraphicsData(){
		this.networkView = null;
		this.eventBus = null;
		this.visualLexicon = null;
		this.container = null;
		this.inputComponent = null;
		this.scrollPane = null;
		this.isMain = false;
		
		//needs to be initialized to zero to trigger fit in view event in graphics configuration
		mainZoom = 0;
		birdZoom = 0;
		
		edgeAnalyser = new EdgeAnalyser();
		mainBI = null;
		birdsEyeBI = null;
	}
	
	public GraphicsData(CyNetworkView networkView, VisualLexicon visualLexicon, EventBus eventBus, JComponent container, JComponent inputComponent, JScrollPane scrollPane, boolean isMain) {
		this.networkView = networkView;
		this.eventBus = eventBus;
		this.visualLexicon = visualLexicon;
		this.container = container;
		this.inputComponent = inputComponent;
		this.scrollPane = scrollPane;
		this.isMain = isMain;
		
		//needs to be initialized to zero to trigger fit in view event in graphics configuration
		mainZoom = 0;
		birdZoom = 0;
		
		edgeAnalyser = new EdgeAnalyser();
		mainBI = null;
		birdsEyeBI = null;				
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
	
	public void setNetworkView(CyNetworkView networkView) {
		this.networkView = networkView;
	}

	
	public VisualLexicon getVisualLexicon() {
		return visualLexicon;
	}	
	
	public void setVisualLexicon(VisualLexicon visualLexicon ) {
		this.visualLexicon = visualLexicon;
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
	
	public void setMainZoom(int scroll){
		this.mainZoom = scroll;
	}
	
	public void setBirdsEyeZoom(int scroll){
		this.birdZoom = scroll;
	}
	
	public void changeMainZoom(int scroll) {
		int temp = this.mainZoom;
		
		logger.warn("Scroll is " + scroll );
		
		if(scroll >= 0  && (temp + scroll) < MAX_ZOOM){
			this.mainZoom += scroll;
		}else if(scroll < 0  && (temp + scroll) > 0){
			this.mainZoom += scroll;			
		}
		logger.warn("Main panel zoom is now set to: " + mainZoom);
	}
	
	public void changeBirdsEyeZoom(int scroll) {
		int temp = this.birdZoom;
		
		if(scroll >= 0  && (temp + scroll) < MAX_ZOOM){
			this.birdZoom += scroll;
		}else if(scroll < 0  && (temp + scroll) > 0){
			this.birdZoom += scroll;			
		}
	}

	public int getScreenWidth() {
		return screenWidth;
	}


	public JComponent getContainer() {
		return container;
	}
	
	public void setContainer(JComponent container) {
		this.container = container;
	}

	public JComponent getInputComponent() {
		return inputComponent;
	}
	
	public void setInputComponent(JComponent inputComponent) {
		this.inputComponent = inputComponent;
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
	
	public void setEventBus(EventBus eventBus) {
		this.eventBus = eventBus;
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
	
	public int getMainZoom() {
		return mainZoom;
	}
	
	public int getBirdZoom() {
		return birdZoom;
	}

	public BufferedImage getMainBufferedImage() {
		return mainBI;
	}
	
	public BufferedImage getBirdsEyeBufferedImage() {
		return birdsEyeBI;
	}
	
	public void setBirdsEyeBufferedImage(BufferedImage bi) {
		if(bi != null){
			birdsEyeBI = bi;
		}
	}
	
	public void setMainBufferedImage(BufferedImage bi) {
		if(bi != null){
			mainBI = bi;
		}
	}
	
	public JScrollPane getScrollPane() {
		return scrollPane;
	}
	
	public void setScrollPane(JScrollPane scrollPane) {
		if(scrollPane != null){
			this.scrollPane = scrollPane;
		}
	}
	
	public boolean isMain() {
		return isMain;
	}
	
	public void setIsMain(boolean isMain) {
		this.isMain = isMain;
	}
}

