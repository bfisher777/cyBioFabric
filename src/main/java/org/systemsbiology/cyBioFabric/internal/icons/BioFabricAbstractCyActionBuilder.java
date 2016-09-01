package org.systemsbiology.cyBioFabric.internal.icons;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.ActionEnableSupport;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.work.ServiceProperties;
import org.systemsbiology.cyBioFabric.internal.biofabric.util.ResourceManager;

/*String title = ResourceManager.getManager().getString("command.CenterOnPreviousSelected");
String preferredMenu = null;
String largeIconURL = "/images/Back24.gif";
String smallIconURL = null;
String tooltip = title; 
String inMenuBar = Boolean.valueOf(false).toString(); 
String inToolBar = Boolean.valueOf(true).toString(); 
String insertSeparatorBefore = Boolean.valueOf(true).toString();
String insertSeparatorAfter = Boolean.valueOf(false).toString();
String enableFor = ActionEnableSupport.ENABLE_FOR_NETWORK_AND_VIEW; 
String accelerator = null; 
String menuGravity = null; 
String toolBarGravity = Float.valueOf(5.01).toString();*/
/*
 * Class is responsible for building the biofabric menu items and toolbar items
 * */
public class BioFabricAbstractCyActionBuilder {

	CyApplicationManager applicationManager;
	CyNetworkViewManager networkViewManager;
	TaskFactoryPredicate taskFactoryPredicate;
	
	public BioFabricAbstractCyActionBuilder(CyApplicationManager applicationManager,
			CyNetworkViewManager networkViewManager, TaskFactoryPredicate taskFactoryPredicate){

		this.applicationManager = applicationManager;
		this.networkViewManager = networkViewManager;
		this.taskFactoryPredicate = taskFactoryPredicate;		
	}
	
	/******************************************************************************************************************
	 * 
	 * Toolbar Items
	 * 
	 * **/
	public ToolBarZoomToCurrentAction buildAZoomToCurrentButton(ArrayList<BioFabricImageIcon> buttons){
		Map<String, String> configProps = new HashMap<String, String>();
		String title = ResourceManager.getManager().getString("command.ZoomToCurrentSelected");
		String preferredMenu = null;
		String largeIconURL = "/images/ZoomToFabricSelected24.gif";
		String smallIconURL = null;
		String tooltip = title; 
		String inMenuBar = Boolean.valueOf(false).toString(); 
		String inToolBar = Boolean.valueOf(true).toString(); 
		String insertSeparatorBefore = Boolean.valueOf(false).toString();
		String insertSeparatorAfter = Boolean.valueOf(false).toString();
		String enableFor = ActionEnableSupport.ENABLE_FOR_NETWORK_AND_VIEW; 
		String accelerator = null; 
		String menuGravity = null; 
		String toolBarGravity = Float.valueOf(5.02f).toString();
		
		configureZoomRectProperties(configProps, title, preferredMenu, largeIconURL, smallIconURL, tooltip, inMenuBar, inToolBar,
				insertSeparatorBefore, insertSeparatorAfter, enableFor, accelerator, menuGravity, toolBarGravity);
		ToolBarZoomToCurrentAction zoomToCurrentAction = new ToolBarZoomToCurrentAction(configProps, applicationManager, networkViewManager, taskFactoryPredicate);
		buttons.add(zoomToCurrentAction);
		return zoomToCurrentAction;	
	}
	
	public ToolBarCancel buildACancelButton(ArrayList<BioFabricImageIcon> buttons){
		Map<String, String> configProps = new HashMap<String, String>();
		String title = ResourceManager.getManager().getString("exportDialog.cancel");
		String preferredMenu = null;
		String largeIconURL = "/images/Stop24.gif";
		String smallIconURL = null;
		String tooltip = title; 
		String inMenuBar = Boolean.valueOf(false).toString(); 
		String inToolBar = Boolean.valueOf(true).toString(); 
		String insertSeparatorBefore = Boolean.valueOf(false).toString();
		String insertSeparatorAfter = Boolean.valueOf(false).toString();
		String enableFor = ActionEnableSupport.ENABLE_FOR_NETWORK_AND_VIEW; 
		String accelerator = null; 
		String menuGravity = null; 
		String toolBarGravity = Float.valueOf(5.02f).toString();
		
		configureZoomRectProperties(configProps, title, preferredMenu, largeIconURL, smallIconURL, tooltip, inMenuBar, inToolBar,
				insertSeparatorBefore, insertSeparatorAfter, enableFor, accelerator, menuGravity, toolBarGravity);
		ToolBarCancel cancelAction = new ToolBarCancel(configProps, applicationManager, networkViewManager, taskFactoryPredicate);
		buttons.add(cancelAction);
		return cancelAction;	
	}
	
	public ToolBarClearSelectionsAction buildAClearSelectionsButton(ArrayList<BioFabricImageIcon> buttons){
		Map<String, String> configProps = new HashMap<String, String>();
		String title = ResourceManager.getManager().getString("command.ClearSel");
		String preferredMenu = null;
		String largeIconURL = "/images/ClearFabricSelected24.gif";
		String smallIconURL = null;
		String tooltip = title; 
		String inMenuBar = Boolean.valueOf(false).toString(); 
		String inToolBar = Boolean.valueOf(true).toString(); 
		String insertSeparatorBefore = Boolean.valueOf(false).toString();
		String insertSeparatorAfter = Boolean.valueOf(true).toString();
		String enableFor = ActionEnableSupport.ENABLE_FOR_NETWORK_AND_VIEW; 
		String accelerator = null; 
		String menuGravity = null; 
		String toolBarGravity = Float.valueOf(5.03f).toString();
		
		configureZoomRectProperties(configProps, title, preferredMenu, largeIconURL, smallIconURL, tooltip, inMenuBar, inToolBar,
				insertSeparatorBefore, insertSeparatorAfter, enableFor, accelerator, menuGravity, toolBarGravity);
		ToolBarClearSelectionsAction clearSelectionsAction = new ToolBarClearSelectionsAction(configProps, applicationManager, networkViewManager, taskFactoryPredicate);
		buttons.add(clearSelectionsAction);
		return clearSelectionsAction;	
	}
	
	public ToolBarCenterOnNextAction buildACenterOnNextButton(ArrayList<BioFabricImageIcon> buttons){
		Map<String, String> configProps = new HashMap<String, String>();
		String title = ResourceManager.getManager().getString("command.CenterOnNextSelected");
		String preferredMenu = null;
		String largeIconURL = "/images/Forward24.gif";
		String smallIconURL = null;
		String tooltip = title; 
		String inMenuBar = Boolean.valueOf(false).toString(); 
		String inToolBar = Boolean.valueOf(true).toString(); 
		String insertSeparatorBefore = Boolean.valueOf(false).toString();
		String insertSeparatorAfter = Boolean.valueOf(true).toString();
		String enableFor = ActionEnableSupport.ENABLE_FOR_NETWORK_AND_VIEW; 
		String accelerator = null; 
		String menuGravity = null; 
		String toolBarGravity = Float.valueOf(5.03f).toString();
		
		configureZoomRectProperties(configProps, title, preferredMenu, largeIconURL, smallIconURL, tooltip, inMenuBar, inToolBar,
				insertSeparatorBefore, insertSeparatorAfter, enableFor, accelerator, menuGravity, toolBarGravity);
		ToolBarCenterOnNextAction centerOnNextAction = new ToolBarCenterOnNextAction(configProps, applicationManager, networkViewManager, taskFactoryPredicate);
		buttons.add(centerOnNextAction);
		return centerOnNextAction;	
	}
	
	public ToolBarCenterOnPreviousAction buildACenterOnPreviousButton(ArrayList<BioFabricImageIcon> buttons){
		Map<String, String> configProps = new HashMap<String, String>();
		String title = ResourceManager.getManager().getString("command.CenterOnPreviousSelected");
		String preferredMenu = null;
		String largeIconURL = "/images/Back24.gif";
		String smallIconURL = null;
		String tooltip = title; 
		String inMenuBar = Boolean.valueOf(false).toString(); 
		String inToolBar = Boolean.valueOf(true).toString(); 
		String insertSeparatorBefore = Boolean.valueOf(true).toString();
		String insertSeparatorAfter = Boolean.valueOf(false).toString();
		String enableFor = ActionEnableSupport.ENABLE_FOR_NETWORK_AND_VIEW; 
		String accelerator = null; 
		String menuGravity = null; 
		String toolBarGravity = Float.valueOf(5.01f).toString();
		
		configureZoomRectProperties(configProps, title, preferredMenu, largeIconURL, smallIconURL, tooltip, inMenuBar, inToolBar,
				insertSeparatorBefore, insertSeparatorAfter, enableFor, accelerator, menuGravity, toolBarGravity);
		ToolBarCenterOnPreviousAction centerOnPrevAction = new ToolBarCenterOnPreviousAction(configProps, applicationManager, networkViewManager, taskFactoryPredicate);
		buttons.add(centerOnPrevAction);
		return centerOnPrevAction;	
	}
	
	public ToolBarZoomToRectAction buildAZoomRectButton(ArrayList<BioFabricImageIcon> buttons){
		Map<String, String> configProps = new HashMap<String, String>();
		String title = ResourceManager.getManager().getString("command.ZoomToRect");
		String preferredMenu = null;
		String largeIconURL = "/images/ZoomToFabricRect24.gif";
		String smallIconURL = null;
		String tooltip = title; 
		String inMenuBar = Boolean.valueOf(false).toString(); 
		String inToolBar = Boolean.valueOf(true).toString(); 
		String insertSeparatorBefore = Boolean.valueOf(false).toString();
		String insertSeparatorAfter = Boolean.valueOf(false).toString();
		String enableFor = ActionEnableSupport.ENABLE_FOR_NETWORK_AND_VIEW; 
		String accelerator = null; 
		String menuGravity = null; 
		String toolBarGravity = Float.valueOf(5.00f).toString();
		
		configureZoomRectProperties(configProps, title, preferredMenu, largeIconURL, smallIconURL, tooltip, inMenuBar, inToolBar,
				insertSeparatorBefore, insertSeparatorAfter, enableFor, accelerator, menuGravity, toolBarGravity);
		ToolBarZoomToRectAction zoomToRectAction = new ToolBarZoomToRectAction(configProps, applicationManager, networkViewManager, taskFactoryPredicate);
		buttons.add(zoomToRectAction);
		return zoomToRectAction;	
	}
	
	public ToolBarSearchAction buildASearchButton(ArrayList<BioFabricImageIcon> buttons){
		Map<String, String> configProps = new HashMap<String, String>();
		String title = ResourceManager.getManager().getString("command.Search");
		String preferredMenu = null;
		String largeIconURL = "/images/Find24.gif";
		String smallIconURL = null;
		String tooltip = title; 
		String inMenuBar = Boolean.valueOf(false).toString(); 
		String inToolBar = Boolean.valueOf(true).toString(); 
		String insertSeparatorBefore = Boolean.valueOf(true).toString();
		String insertSeparatorAfter = Boolean.valueOf(true).toString();
		String enableFor = ActionEnableSupport.ENABLE_FOR_NETWORK_AND_VIEW; 
		String accelerator = null; 
		String menuGravity = null; 
		String toolBarGravity = Float.valueOf(5.04f).toString();
		
		configureZoomRectProperties(configProps, title, preferredMenu, largeIconURL, smallIconURL, tooltip, inMenuBar, inToolBar,
				insertSeparatorBefore, insertSeparatorAfter, enableFor, accelerator, menuGravity, toolBarGravity);
		ToolBarSearchAction searchAction = new ToolBarSearchAction(configProps, applicationManager, networkViewManager, taskFactoryPredicate);
		buttons.add(searchAction);
		return searchAction;	
	}
	
	/******************************************************************************************************************
	 * 
	 * Import Sub Menu
	 * 
	 * **/
	
	public ImportWithNodeAttributes buildAImportWithNodeAttributesMenuItem(ArrayList<BioFabricImageIcon> buttons){
		Map<String, String> configProps = new HashMap<String, String>();
		String title = ResourceManager.getManager().getString("command.LoadSIFWithNodeAttributes");
		String preferredMenu = "Apps.CyBioFabric.Import";
		String largeIconURL = null;
		String smallIconURL = null;
		String tooltip = title; 
		String inMenuBar = Boolean.valueOf(true).toString(); 
		String inToolBar = Boolean.valueOf(false).toString(); 
		String insertSeparatorBefore = Boolean.valueOf(false).toString();
		String insertSeparatorAfter = Boolean.valueOf(false).toString();
		String enableFor = ActionEnableSupport.ENABLE_FOR_NETWORK_AND_VIEW; 
		String accelerator = null; 
		String menuGravity = Float.valueOf(30f).toString(); 
		String toolBarGravity = null;
		
		configureZoomRectProperties(configProps, title, preferredMenu, largeIconURL, smallIconURL, tooltip, inMenuBar, inToolBar,
				insertSeparatorBefore, insertSeparatorAfter, enableFor, accelerator, menuGravity, toolBarGravity);
		ImportWithNodeAttributes importWithNodeAttributesMenuItem = new ImportWithNodeAttributes(configProps, applicationManager, networkViewManager, taskFactoryPredicate);		
		buttons.add(importWithNodeAttributesMenuItem);
		return importWithNodeAttributesMenuItem;	
	}
	
	public ImportWithEdgeWeights buildAImportWithEdgeWeightsMenuItem(ArrayList<BioFabricImageIcon> buttons){
		Map<String, String> configProps = new HashMap<String, String>();
		String title = ResourceManager.getManager().getString("command.LoadSIFWithWeights");
		String preferredMenu = "Apps.CyBioFabric.Import";
		String largeIconURL = null;
		String smallIconURL = null;
		String tooltip = title; 
		String inMenuBar = Boolean.valueOf(true).toString(); 
		String inToolBar = Boolean.valueOf(false).toString(); 
		String insertSeparatorBefore = Boolean.valueOf(false).toString();
		String insertSeparatorAfter = Boolean.valueOf(false).toString();
		String enableFor = ActionEnableSupport.ENABLE_FOR_NETWORK_AND_VIEW; 
		String accelerator = null; 
		String menuGravity = Float.valueOf(30f).toString(); 
		String toolBarGravity = null;
		
		configureZoomRectProperties(configProps, title, preferredMenu, largeIconURL, smallIconURL, tooltip, inMenuBar, inToolBar,
				insertSeparatorBefore, insertSeparatorAfter, enableFor, accelerator, menuGravity, toolBarGravity);
		ImportWithEdgeWeights importWithEdgeWeightsMenuItem = new ImportWithEdgeWeights(configProps, applicationManager, networkViewManager, taskFactoryPredicate);		
		buttons.add(importWithEdgeWeightsMenuItem);
		return importWithEdgeWeightsMenuItem;	
	}
	
	/******************************************************************************************************************
	 * 
	 * Edit Sub Menu
	 * 
	 * **/
	public EditSetDisplayOptions buildAEditSetDisplayOptionsMenuItem(ArrayList<BioFabricImageIcon> buttons){
		Map<String, String> configProps = new HashMap<String, String>();
		String title = ResourceManager.getManager().getString("displayOptions.title");
		String preferredMenu = "Apps.CyBioFabric.Edit";
		String largeIconURL = null;
		String smallIconURL = null;
		String tooltip = title; 
		String inMenuBar = Boolean.valueOf(true).toString(); 
		String inToolBar = Boolean.valueOf(false).toString(); 
		String insertSeparatorBefore = Boolean.valueOf(false).toString();
		String insertSeparatorAfter = Boolean.valueOf(false).toString();
		String enableFor = ActionEnableSupport.ENABLE_FOR_NETWORK_AND_VIEW; 
		String accelerator = null; 
		String menuGravity = Float.valueOf(30f).toString(); 
		String toolBarGravity = null;
		
		configureZoomRectProperties(configProps, title, preferredMenu, largeIconURL, smallIconURL, tooltip, inMenuBar, inToolBar,
				insertSeparatorBefore, insertSeparatorAfter, enableFor, accelerator, menuGravity, toolBarGravity);
		EditSetDisplayOptions editSetDisplayOptionsMenuItem = new EditSetDisplayOptions(configProps, applicationManager, networkViewManager, taskFactoryPredicate);		
		buttons.add(editSetDisplayOptionsMenuItem);
		return editSetDisplayOptionsMenuItem;	
	}
	
	public EditAccumulateSelections buildAEditAccumulateSelectionsMenuItem(ArrayList<BioFabricImageIcon> buttons){
		Map<String, String> configProps = new HashMap<String, String>();
		//String title = ResourceManager.getManager().getString("command.BuildSelect");
		String title = ResourceManager.getManager().getString("cytoMenuItemTitle.unAccumulate");
		String preferredMenu = "Apps.CyBioFabric.Edit";
		String largeIconURL = null;
		String smallIconURL = null;
		String tooltip = ResourceManager.getManager().getString("cytoMenuItemTitle.unAccumulateTooltip"); 
		String inMenuBar = Boolean.valueOf(true).toString(); 
		String inToolBar = Boolean.valueOf(false).toString(); 
		String insertSeparatorBefore = Boolean.valueOf(false).toString();
		String insertSeparatorAfter = Boolean.valueOf(false).toString();
		String enableFor = ActionEnableSupport.ENABLE_FOR_NETWORK_AND_VIEW; 
		String accelerator = null; 
		String menuGravity = Float.valueOf(30f).toString(); 
		String toolBarGravity = null;
				
		configureZoomRectProperties(configProps, title, preferredMenu, largeIconURL, smallIconURL, tooltip, inMenuBar, inToolBar,
				insertSeparatorBefore, insertSeparatorAfter, enableFor, accelerator, menuGravity, toolBarGravity);
		EditAccumulateSelections editAccumulateSelectionsMenuItem = new EditAccumulateSelections(configProps, applicationManager, networkViewManager, taskFactoryPredicate);		
		buttons.add(editAccumulateSelectionsMenuItem);
		return editAccumulateSelectionsMenuItem;	
	}
	
	
	/******************************************************************************************************************
	 * 
	 * Tools Sub Menu
	 * 
	 * **/
	public ToolsSearchForNodes buildAToolsSearchForNodesMenuItem(ArrayList<BioFabricImageIcon> buttons){
		Map<String, String> configProps = new HashMap<String, String>();
		String title = ResourceManager.getManager().getString("command.Search");
		String preferredMenu = "Apps.CyBioFabric.Tools";
		String largeIconURL = null;
		String smallIconURL = null;
		String tooltip = title; 
		String inMenuBar = Boolean.valueOf(true).toString(); 
		String inToolBar = Boolean.valueOf(false).toString(); 
		String insertSeparatorBefore = Boolean.valueOf(false).toString();
		String insertSeparatorAfter = Boolean.valueOf(false).toString();
		String enableFor = ActionEnableSupport.ENABLE_FOR_NETWORK_AND_VIEW; 
		String accelerator = null; 
		String menuGravity = Float.valueOf(30f).toString(); 
		String toolBarGravity = null;
				
		configureZoomRectProperties(configProps, title, preferredMenu, largeIconURL, smallIconURL, tooltip, inMenuBar, inToolBar,
				insertSeparatorBefore, insertSeparatorAfter, enableFor, accelerator, menuGravity, toolBarGravity);
		ToolsSearchForNodes toolsSearchForNodesMenuItem = new ToolsSearchForNodes(configProps, applicationManager, networkViewManager, taskFactoryPredicate);		
		buttons.add(toolsSearchForNodesMenuItem);
		return toolsSearchForNodesMenuItem;	
	}
	
	public ToolsCompareMultipleNodes buildAToolsCompareMultipleNodesMenuItem(ArrayList<BioFabricImageIcon> buttons){
		Map<String, String> configProps = new HashMap<String, String>();
		String title = ResourceManager.getManager().getString("command.CompareNodes");
		String preferredMenu = "Apps.CyBioFabric.Tools";
		String largeIconURL = null;
		String smallIconURL = null;
		String tooltip = title; 
		String inMenuBar = Boolean.valueOf(true).toString(); 
		String inToolBar = Boolean.valueOf(false).toString(); 
		String insertSeparatorBefore = Boolean.valueOf(false).toString();
		String insertSeparatorAfter = Boolean.valueOf(false).toString();
		String enableFor = ActionEnableSupport.ENABLE_FOR_NETWORK_AND_VIEW; 
		String accelerator = null; 
		String menuGravity = Float.valueOf(30f).toString(); 
		String toolBarGravity = null;
				
		configureZoomRectProperties(configProps, title, preferredMenu, largeIconURL, smallIconURL, tooltip, inMenuBar, inToolBar,
				insertSeparatorBefore, insertSeparatorAfter, enableFor, accelerator, menuGravity, toolBarGravity);
		ToolsCompareMultipleNodes toolsCompareMultipleNodesMenuItem = new ToolsCompareMultipleNodes(configProps, applicationManager, networkViewManager, taskFactoryPredicate);		
		buttons.add(toolsCompareMultipleNodesMenuItem);
		return toolsCompareMultipleNodesMenuItem;	
	}
	
	/******************************************************************************************************************
	 * 
	 * View Sub Menu
	 * 
	 * **/
	public ViewSelectARectangleAndZoom buildAViewSelectARectangleAndZoomMenuItem(ArrayList<BioFabricImageIcon> buttons){
		Map<String, String> configProps = new HashMap<String, String>();
		String title = ResourceManager.getManager().getString("command.ZoomToRect");
		String preferredMenu = "Apps.CyBioFabric.View";
		String largeIconURL = null;
		String smallIconURL = null;
		String tooltip = title; 
		String inMenuBar = Boolean.valueOf(true).toString(); 
		String inToolBar = Boolean.valueOf(false).toString(); 
		String insertSeparatorBefore = Boolean.valueOf(false).toString();
		String insertSeparatorAfter = Boolean.valueOf(false).toString();
		String enableFor = ActionEnableSupport.ENABLE_FOR_NETWORK_AND_VIEW; 
		String accelerator = null; 
		String menuGravity = Float.valueOf(30f).toString(); 
		String toolBarGravity = null;
				
		configureZoomRectProperties(configProps, title, preferredMenu, largeIconURL, smallIconURL, tooltip, inMenuBar, inToolBar,
				insertSeparatorBefore, insertSeparatorAfter, enableFor, accelerator, menuGravity, toolBarGravity);
		ViewSelectARectangleAndZoom viewSelectARectangleAndZoomMenuItem = new ViewSelectARectangleAndZoom(configProps, applicationManager, networkViewManager, taskFactoryPredicate);		
		buttons.add(viewSelectARectangleAndZoomMenuItem);
		return viewSelectARectangleAndZoomMenuItem;	
	}
	
	public ViewZoomToMousePosition buildAViewZoomToMousePositionMenuItem(ArrayList<BioFabricImageIcon> buttons){
		Map<String, String> configProps = new HashMap<String, String>();
		String title = ResourceManager.getManager().getString("command.ZoomToCurrentMouse");
		String preferredMenu = "Apps.CyBioFabric.View";
		String largeIconURL = null;
		String smallIconURL = null;
		String tooltip = title; 
		String inMenuBar = Boolean.valueOf(true).toString(); 
		String inToolBar = Boolean.valueOf(false).toString(); 
		String insertSeparatorBefore = Boolean.valueOf(false).toString();
		String insertSeparatorAfter = Boolean.valueOf(false).toString();
		String enableFor = ActionEnableSupport.ENABLE_FOR_NETWORK_AND_VIEW; 
		String accelerator = null; 
		String menuGravity = Float.valueOf(30f).toString(); 
		String toolBarGravity = null;
				
		configureZoomRectProperties(configProps, title, preferredMenu, largeIconURL, smallIconURL, tooltip, inMenuBar, inToolBar,
				insertSeparatorBefore, insertSeparatorAfter, enableFor, accelerator, menuGravity, toolBarGravity);
		ViewZoomToMousePosition viewZoomToMousePositionMenuItem = new ViewZoomToMousePosition(configProps, applicationManager, networkViewManager, taskFactoryPredicate);		
		buttons.add(viewZoomToMousePositionMenuItem);
		return viewZoomToMousePositionMenuItem;	
	}
	
	public ViewZoomToNetworkMagPosition buildAViewZoomToNetworkMagPositionMenuItem(ArrayList<BioFabricImageIcon> buttons){
		Map<String, String> configProps = new HashMap<String, String>();
		String title = ResourceManager.getManager().getString("command.ZoomToCurrentMagnify");
		String preferredMenu = "Apps.CyBioFabric.View";
		String largeIconURL = null;
		String smallIconURL = null;
		String tooltip = title; 
		String inMenuBar = Boolean.valueOf(true).toString(); 
		String inToolBar = Boolean.valueOf(false).toString(); 
		String insertSeparatorBefore = Boolean.valueOf(false).toString();
		String insertSeparatorAfter = Boolean.valueOf(false).toString();
		String enableFor = ActionEnableSupport.ENABLE_FOR_NETWORK_AND_VIEW; 
		String accelerator = null; 
		String menuGravity = Float.valueOf(30f).toString(); 
		String toolBarGravity = null;
				
		configureZoomRectProperties(configProps, title, preferredMenu, largeIconURL, smallIconURL, tooltip, inMenuBar, inToolBar,
				insertSeparatorBefore, insertSeparatorAfter, enableFor, accelerator, menuGravity, toolBarGravity);
		ViewZoomToNetworkMagPosition viewZoomToNetworkMagPositionMenuItem = new ViewZoomToNetworkMagPosition(configProps, applicationManager, networkViewManager, taskFactoryPredicate);		
		buttons.add(viewZoomToNetworkMagPositionMenuItem);
		return viewZoomToNetworkMagPositionMenuItem;	
	}
	
	public ViewCenterOnPreviousSelection buildAViewCenterOnPreviousSelectionMenuItem(ArrayList<BioFabricImageIcon> buttons){
		Map<String, String> configProps = new HashMap<String, String>();
		String title = ResourceManager.getManager().getString("command.CenterOnPreviousSelected");
		String preferredMenu = "Apps.CyBioFabric.View";
		String largeIconURL = null;
		String smallIconURL = null;
		String tooltip = title; 
		String inMenuBar = Boolean.valueOf(true).toString(); 
		String inToolBar = Boolean.valueOf(false).toString(); 
		String insertSeparatorBefore = Boolean.valueOf(true).toString();
		String insertSeparatorAfter = Boolean.valueOf(false).toString();
		String enableFor = ActionEnableSupport.ENABLE_FOR_NETWORK_AND_VIEW; 
		String accelerator = null; 
		String menuGravity = Float.valueOf(31f).toString(); 
		String toolBarGravity = null;
				
		configureZoomRectProperties(configProps, title, preferredMenu, largeIconURL, smallIconURL, tooltip, inMenuBar, inToolBar,
				insertSeparatorBefore, insertSeparatorAfter, enableFor, accelerator, menuGravity, toolBarGravity);
		ViewCenterOnPreviousSelection viewCenterOnPreviousSelectionMenuItem = new ViewCenterOnPreviousSelection(configProps, applicationManager, networkViewManager, taskFactoryPredicate);		
		buttons.add(viewCenterOnPreviousSelectionMenuItem);
		return viewCenterOnPreviousSelectionMenuItem;	
	}
	
	public ViewZoomToCurrentSelection buildAViewZoomToCurrentSelectionMenuItem(ArrayList<BioFabricImageIcon> buttons){
		Map<String, String> configProps = new HashMap<String, String>();
		String title = ResourceManager.getManager().getString("command.ZoomToCurrentSelected");
		String preferredMenu = "Apps.CyBioFabric.View";
		String largeIconURL = null;
		String smallIconURL = null;
		String tooltip = title; 
		String inMenuBar = Boolean.valueOf(true).toString(); 
		String inToolBar = Boolean.valueOf(false).toString(); 
		String insertSeparatorBefore = Boolean.valueOf(false).toString();
		String insertSeparatorAfter = Boolean.valueOf(false).toString();
		String enableFor = ActionEnableSupport.ENABLE_FOR_NETWORK_AND_VIEW; 
		String accelerator = null; 
		String menuGravity = Float.valueOf(32f).toString(); 
		String toolBarGravity = null;
				
		configureZoomRectProperties(configProps, title, preferredMenu, largeIconURL, smallIconURL, tooltip, inMenuBar, inToolBar,
				insertSeparatorBefore, insertSeparatorAfter, enableFor, accelerator, menuGravity, toolBarGravity);
		ViewZoomToCurrentSelection viewZoomToCurrentSelectionMenuItem = new ViewZoomToCurrentSelection(configProps, applicationManager, networkViewManager, taskFactoryPredicate);		
		buttons.add(viewZoomToCurrentSelectionMenuItem);
		return viewZoomToCurrentSelectionMenuItem;	
	}
	
	public ViewCenterOnNextSelection buildAViewCenterOnNextSelectionMenuItem(ArrayList<BioFabricImageIcon> buttons){
		Map<String, String> configProps = new HashMap<String, String>();
		String title = ResourceManager.getManager().getString("command.CenterOnNextSelected");
		String preferredMenu = "Apps.CyBioFabric.View";
		String largeIconURL = null;
		String smallIconURL = null;
		String tooltip = title; 
		String inMenuBar = Boolean.valueOf(true).toString(); 
		String inToolBar = Boolean.valueOf(false).toString(); 
		String insertSeparatorBefore = Boolean.valueOf(false).toString();
		String insertSeparatorAfter = Boolean.valueOf(false).toString();
		String enableFor = ActionEnableSupport.ENABLE_FOR_NETWORK_AND_VIEW; 
		String accelerator = null; 
		String menuGravity = Float.valueOf(33f).toString(); 
		String toolBarGravity = null;
				
		configureZoomRectProperties(configProps, title, preferredMenu, largeIconURL, smallIconURL, tooltip, inMenuBar, inToolBar,
				insertSeparatorBefore, insertSeparatorAfter, enableFor, accelerator, menuGravity, toolBarGravity);
		ViewCenterOnNextSelection viewCenterOnNextSelectionMenuItem = new ViewCenterOnNextSelection(configProps, applicationManager, networkViewManager, taskFactoryPredicate);		
		buttons.add(viewCenterOnNextSelectionMenuItem);
		return viewCenterOnNextSelectionMenuItem;	
	}
	/*title - (The title of the menu.)
	preferredMenu - (The preferred menu for the action.)
	largeIconURL - (The icon to be used for the toolbar.)
	smallIconURL - (The icon to be used for the menu.)
	tooltip - (The toolbar or menu tooltip.)
	inToolBar - (Whether the action should be in the toolbar.)
	inMenuBar - (Whether the action should be in a menu.)
	insertSeparatorBefore - (Whether a separator should be inserted before this menu item.)
	insertSeparatorAfter - (Whether a separator should be inserted after this menu item.)
	enableFor - (System state that the action should be enabled for. See {@link ActionEnableSupport} for more detail.)
	accelerator - (Accelerator key bindings.)
	menuGravity - (Float value with 0.0 representing the top and larger values moving towards the bottom of the menu.)
	toolBarGravity - (Float value with 0.0 representing the top and larger values moving towards the bottom of the toolbar.)*/
	private void configureZoomRectProperties(Map<String, String> configProps, String title, String preferredMenu,
			String largeIconURL, String smallIconURL, String tooltip, String inMenuBar, String inToolBar, String insertSeparatorBefore,
			String insertSeparatorAfter, String enableFor, String accelerator, String menuGravity, String toolBarGravity){		
		
		if(title != null){
			configProps.put(ServiceProperties.TITLE, title);
		}
		
		if(preferredMenu != null){
			configProps.put(ServiceProperties.PREFERRED_MENU, preferredMenu);
		}
		
		if(largeIconURL != null){
			configProps.put(ServiceProperties.LARGE_ICON_URL, largeIconURL);
		}
		
		if(largeIconURL != null){
			configProps.put(ServiceProperties.SMALL_ICON_URL, smallIconURL);
		}
		
		if(tooltip != null){
			configProps.put(ServiceProperties.TOOLTIP, tooltip);
		}
		
		if(inToolBar != null){
			configProps.put(ServiceProperties.IN_TOOL_BAR, inToolBar);
		}
		
		if(inMenuBar != null){
			configProps.put(ServiceProperties.IN_MENU_BAR, inMenuBar);
		}
		
		if(insertSeparatorBefore != null){
			configProps.put(ServiceProperties.INSERT_SEPARATOR_BEFORE, insertSeparatorBefore);
		}
		
		if(insertSeparatorAfter != null){
			configProps.put(ServiceProperties.INSERT_SEPARATOR_AFTER, insertSeparatorAfter);
		}
		
		if(enableFor != null){
			configProps.put(ServiceProperties.ENABLE_FOR, enableFor);
		}
		
		if(accelerator != null){
			configProps.put(ServiceProperties.ACCELERATOR, accelerator);//custom key binding
		}
		
		if(menuGravity != null){
			configProps.put(ServiceProperties.MENU_GRAVITY, menuGravity);
		}
		
		if(toolBarGravity != null){
			configProps.put(ServiceProperties.TOOL_BAR_GRAVITY, toolBarGravity);
		}
	}		
}
