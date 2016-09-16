/*
**    Copyright (C) 2003-2014 Institute for Systems Biology 
**                            Seattle, Washington, USA. 
**
**    This library is free software; you can redistribute it and/or
**    modify it under the terms of the GNU Lesser General Public
**    License as published by the Free Software Foundation; either
**    version 2.1 of the License, or (at your option) any later version.
**
**    This library is distributed in the hope that it will be useful,
**    but WITHOUT ANY WARRANTY; without even the implied warranty of
**    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
**    Lesser General Public License for more details.
**
**    You should have received a copy of the GNU Lesser General Public
**    License along with this library; if not, write to the Free Software
**    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/


package org.systemsbiology.cyBioFabric.internal.biofabric.app;

import java.awt.Dimension;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.WindowConstants;

import org.cytoscape.event.CyEventHelper;
import org.systemsbiology.cyBioFabric.internal.biofabric.cmd.CommandSet;
import org.systemsbiology.cyBioFabric.internal.biofabric.gaggle.DeadFabricGoose;
import org.systemsbiology.cyBioFabric.internal.biofabric.gaggle.FabricGooseInterface;
import org.systemsbiology.cyBioFabric.internal.biofabric.gaggle.FabricGooseManager;
import org.systemsbiology.cyBioFabric.internal.biofabric.ui.dialogs.UpdateJavaDialog;
import org.systemsbiology.cyBioFabric.internal.biofabric.util.ExceptionHandler;
import org.systemsbiology.cyBioFabric.internal.biofabric.util.UiUtil;

/****************************************************************************
**
** The top-level BioFabric Application
*/

public class BioFabricApplication {

  ////////////////////////////////////////////////////////////////////////////
  //
  // PUBLIC STATIC CONSTANTS
  //
  //////////////////////////////////////////////////////////////////////////// 
  
  ////////////////////////////////////////////////////////////////////////////
  //
  // PRIVATE VARIABLES
  //
  ////////////////////////////////////////////////////////////////////////////  
 
  private boolean forCyto_;
  private BioFabricWindow bfw_;
  private BioFabricWindow selectionWindow_;
  private int count; //used to increment class name in init CommandSet  
  private JComponent inputComponent;
//TODO added this to allow events to be fired in Cytoscape
  private CyEventHelper eventHelper_;
  
  ////////////////////////////////////////////////////////////////////////////
  //
  // PUBLIC CONSTRUCTORS
  //
  ////////////////////////////////////////////////////////////////////////////    
 
  /***************************************************************************
  **
  ** Now supporting Cytoscape App usage
  */
  
  public BioFabricApplication(boolean forCyto, int count, CyEventHelper eventHelper){
	  
    forCyto_ = forCyto;
    this.count = count;
    eventHelper_ = eventHelper;
  }
  
  ////////////////////////////////////////////////////////////////////////////
  //
  // PUBLIC METHODS
  //
  ////////////////////////////////////////////////////////////////////////////
    
  //TODO added this to allow app to access event helper
  public CyEventHelper getEventHelper(){
	  return eventHelper_;
  }
  
  public void attachInputComponent(JComponent inputComponent){
	  this.inputComponent = inputComponent;
  }
  
  /***************************************************************************
  **
  ** Shutdown operations
  */
  
  public void shutdownFabric() {
	  
	System.out.println("Shutting down biofabric application");
	
	//TODO: revisit this
    /*FabricGooseInterface goose = FabricGooseManager.getManager().getGoose();
    if ((goose != null) && goose.isActivated()) {
      goose.closeDown();
    }*/
	
    bfw_.stopBufferBuilding();
    bfw_.dispose();
    if (selectionWindow_ != null) {
      selectionWindow_.dispose();
    }   
  }
   
  /***************************************************************************
  **
  ** Close selection window
  */
  
  public void closeSelection() {
    bfw_.getFabricPanel().setSelectionPanel(null);
    selectionWindow_.setVisible(false);
    return;
  }
        
  /***************************************************************************
  **
  ** Launch selection window 
  */
     
  public BioFabricWindow launchSelection() {
    bfw_.getFabricPanel().setSelectionPanel(selectionWindow_.getFabricPanel());
    selectionWindow_.setSize((int)(bfw_.getWidth() * .80), (int)(bfw_.getHeight() * .80));
    selectionWindow_.setVisible(true);
    raiseSelection();
   // selectionWindow_.show();
    return (selectionWindow_);
  }
  
 /***************************************************************************
  **
  ** Raise existing selection window 
  */
     
  public void raiseSelection() {    
    selectionWindow_.toFront();
    return;
  }

    /***************************************************************************
  **
  ** Launch operations. Now public to support Cytoscape App usage
  *  Added a number "count" to the end of the string para to allow multiple CommandSets
  */
   
  public BioFabricWindow launch(Map<String, Object> args) { 		  
	
    boolean isAMac = System.getProperty("os.name").toLowerCase().startsWith("mac os x");
    if (isAMac) {
      String verNum = System.getProperty("java.version").toLowerCase();
      if ((verNum.indexOf("1.4") == 0) || (verNum.indexOf("1.5") == 0)) {
        UpdateJavaDialog ujw = new UpdateJavaDialog();
        ujw.setVisible(true);
        if (!ujw.keepGoingAnyway()) {
          return (null);
       } 
       }
    }
    //Moved this call into CyActivator
    //ResourceManager.initManager("org.systemsbiology.cyBioFabric.internal.biofabric.props.BioFabric");
    
    String commandName = "mainWindow_" + count;
    bfw_ = new BioFabricWindow(args, this, true, commandName);
    ExceptionHandler.getHandler().initialize(bfw_);
    Dimension cbf = UiUtil.centerBigFrame(bfw_, 1600, 1200, 1.0, 0);
    bfw_.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);   
        
    CommandSet.initCmds(commandName, this, bfw_, true);
    bfw_.initWindow(cbf, inputComponent);
    bfw_.setVisible(true);
    initSelection();
    Boolean doGag = (Boolean)args.get("doGaggle");
    
    //TODO is the goose code required?
    //gooseLaunch(bfw_, (doGag != null) && doGag.booleanValue());    
        
    return (bfw_);
  }
    
  ////////////////////////////////////////////////////////////////////////////
  //
  // PUBLIC STATIC METHODS
  //
  ////////////////////////////////////////////////////////////////////////////
  
  /***************************************************************************
  ** 
  ** Main entry point
  */

  /*public static void main(String argv[]) {
    final HashMap<String, Object> args = new HashMap<String, Object>();
    if ((argv.length > 0) && argv[0].equalsIgnoreCase("-gaggle")) {
      args.put("doGaggle", new Boolean(true));
    }    
    final BioFabricApplication su = new BioFabricApplication(false);    
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {    
        su.launch(args);
      }
    });
  }*/
  
  ////////////////////////////////////////////////////////////////////////////
  //
  // PRIVATE METHODS
  //
  ////////////////////////////////////////////////////////////////////////////    
  
  /***************************************************************************
  **
  ** init selection window 
  *  Added a number to the end of the string para to allow multi CommandSets
  */
     
  private void initSelection() { 
	//create a unique command name
	String commandName = "selectionWindow_" + count;  	
    selectionWindow_ = new BioFabricWindow(new HashMap<String, Object>(), this, false, commandName);
    Dimension swDim = new Dimension((int)(bfw_.getWidth() * .80), (int)(bfw_.getHeight() * .80));
    selectionWindow_.setSize(swDim.width, swDim.height);
    selectionWindow_.setLocation(bfw_.getLocation().x, bfw_.getLocation().y);
    selectionWindow_.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);   
    
    CommandSet.initCmds(commandName, this, selectionWindow_, false);
    selectionWindow_.initWindow(swDim, inputComponent);
    return;
  }
  
 /***************************************************************************
  **
  ** Drawing core
  */
  
  private void gooseLaunch(BioFabricWindow frame, boolean doGaggle) {   
    if (doGaggle) {
      try {
        Class gooseClass = Class.forName("org.systemsbiology.biotapestry.biofabric.FabricGoose");
        FabricGooseInterface liveGoose = (FabricGooseInterface)gooseClass.newInstance();
        liveGoose.setParameters(frame, "unknown");
        liveGoose.activate();
        FabricGooseManager.getManager().setGoose(liveGoose);
      } catch (ClassNotFoundException cnfex) {
        System.err.println("BTGoose class not found");
        FabricGooseManager.getManager().setGoose(new DeadFabricGoose());     
      } catch (InstantiationException iex) {
        System.err.println("BTGoose class not instantiated");
      } catch (IllegalAccessException iex) {
        System.err.println("BTGoose class not instantiated");
      }
    } else {
      FabricGooseManager.getManager().setGoose(new DeadFabricGoose());
    }
    return;
  }  
  
  public BioFabricWindow getBioFabricWindow(){
	  return bfw_;
  }
  
  public BioFabricWindow getSelectionWindow(){
	  return selectionWindow_;
  }
    
}
