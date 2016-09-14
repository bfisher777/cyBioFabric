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

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import org.cytoscape.work.swing.DialogTaskManager;
import org.systemsbiology.cyBioFabric.internal.biofabric.cmd.CommandSet;
import org.systemsbiology.cyBioFabric.internal.biofabric.ui.display.BioFabricNavAndControl;
import org.systemsbiology.cyBioFabric.internal.biofabric.ui.display.BioFabricOverview;
import org.systemsbiology.cyBioFabric.internal.biofabric.ui.display.BioFabricPanel;
import org.systemsbiology.cyBioFabric.internal.biofabric.ui.display.FabricMagnifyingTool;
import org.systemsbiology.cyBioFabric.internal.biofabric.util.BackgroundWorkerControlManager;
import org.systemsbiology.cyBioFabric.internal.biofabric.util.ExceptionHandler;
import org.systemsbiology.cyBioFabric.internal.biofabric.util.FixedJComboBox;
import org.systemsbiology.cyBioFabric.internal.biofabric.util.ResourceManager;

/****************************************************************************
**
** This is the BioFabric Window!
*/

public class BioFabricWindow extends JInternalFrame implements BackgroundWorkerControlManager {
  
  ////////////////////////////////////////////////////////////////////////////
  //
  // PRIVATE INSTANCE MEMBERS
  //
  ////////////////////////////////////////////////////////////////////////////   
  private BioFabricPanel cp_;
  private BioFabricApplication bfa_;
  private FabricMagnifyingTool fmt_;
  private HashMap<Integer, Action> actionMap_;
  private BioFabricNavAndControl nac_;
  private BioFabricOverview thumbnailView_; //used to create Cytoscape thumbnail view
  private boolean doGaggle_;
  private boolean isMain_;
  private JButton gaggleInstallButton_;
  private JButton gaggleUpdateGooseButton_;
  private FixedJComboBox gaggleGooseCombo_;
  private JPanel hidingPanel_;
  private CardLayout myCard_;
  private JSplitPane sp_;//TODO can I remove this?
  private double savedSplitFrac_;
  private static final long serialVersionUID = 1L;
  public final String COMMAND_NAME;
  
  private DialogTaskManager dialogTaskManager_; //TODO added this to support the popmenu used in FabricPanel
   
  ////////////////////////////////////////////////////////////////////////////
  //
  // PUBLIC CONSTRUCTORS
  //
  ////////////////////////////////////////////////////////////////////////////

  /***************************************************************************
  **
  ** Constructor
  */

  public BioFabricWindow(Map<String, Object> args, BioFabricApplication bfa, boolean isMain, String commandName) {
    super((isMain) ? "BioFabric" : "BioFabric: Selected Submodel View");
    Boolean doGag = (Boolean)args.get("doGaggle");
    doGaggle_ = (doGag != null) && doGag.booleanValue();
    bfa_ = bfa;
    isMain_ = isMain;
    actionMap_ = new HashMap<Integer, Action>();
    COMMAND_NAME = commandName;
  }
    
  ////////////////////////////////////////////////////////////////////////////
  //
  // PUBLIC METHODS
  //
  ////////////////////////////////////////////////////////////////////////////

  /****************************************************************************
  **
  ** disable
  */  
  
  public void disableControls() {
    disableControls(CommandSet.GENERAL_PUSH, true);
    return;
  }
  
  /***************************************************************************
  **
  ** Disable the main controls
  */ 
  
  public void disableControls(int pushFlags, boolean displayToo) {
    CommandSet fc = CommandSet.getCmds(COMMAND_NAME);
    if (displayToo) {
      myCard_.show(hidingPanel_, "Hiding");
      fmt_.enableControls(false);
      nac_.getOverview().showView(false);
    }
    nac_.getNavTool().enableControls(false);
    if (gaggleGooseCombo_ != null) {
      gaggleGooseCombo_.setEnabled(false);
    }    
    getContentPane().validate();
    System.out.println("validate DC");
    fc.pushDisabled(pushFlags);
  }

  /****************************************************************************
  **
  ** enable
  */  
  
  public void reenableControls() {
    CommandSet fc = CommandSet.getCmds(COMMAND_NAME);
    fc.popDisabled();
    myCard_.show(hidingPanel_, "SUPanel");
    fmt_.enableControls(true);
    nac_.getOverview().showView(true);
    nac_.getNavTool().enableControls(true);
  
    if (gaggleGooseCombo_ != null) {
      gaggleGooseCombo_.setEnabled(true);
    }    
    
    System.out.println("validate REC");
    getContentPane().validate();    
    
    //
    // Following background thread operations, sometimes we need to
    // get keyboard focus back to the network panel:
    //
    // We make this conditional to keep it from being called in normal operation as 
    // the genome is changed, which causes the time slider to lose focus EVERY 
    // TIME IT IS MOVED!!!!!!!
    
  //  if (withFocus) {
   //   sup_.requestFocus();
  //  }
    
    return;
  } 
  
  /****************************************************************************
  **
  ** also redraw....
  */  
  
  public void redraw() {
    cp_.repaint();
    return;
  } 
    
  /***************************************************************************
  **
  ** Get it up and running
  ** @param inputComponent pass in from CyBFRenderingEngine to BioFabricApplication
  ** represent the keyboard c
  */

  public void initWindow(Dimension dim, JComponent inputComponent) {
    JPanel cpane = (JPanel)getContentPane();
    inputComponent.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("ESCAPE"), "BioTapCancel");
    inputComponent.getActionMap().put("BioTapCancel", new AbstractAction() {
      private static final long serialVersionUID = 1L;
      public void actionPerformed(ActionEvent e) {
        try {
          AbstractAction aa = (AbstractAction)actionMap_.get(Integer.valueOf(CommandSet.CANCEL));
          aa.actionPerformed(null);
          return;
        } catch (Exception ex) {
          ExceptionHandler.getHandler().displayException(ex);
        }
      }
    });        
    CommandSet fc = CommandSet.getCmds(COMMAND_NAME);
    JToolBar toolBar = null;
    JMenu gaggleGooseChooseMenu = (doGaggle_) ? new JMenu(ResourceManager.getManager().getString("command.gooseChoose")) : null;    
    gaggleGooseCombo_ = (doGaggle_) ? new FixedJComboBox(250) : null;    
    fc.setGaggleElements(gaggleGooseChooseMenu, gaggleGooseCombo_);
      
    //TODO move menu to Cytoscape frame
    //menuInstall(fc, isMain_, gaggleGooseChooseMenu);
    toolBar = new JToolBar();
    stockActionMap(fc, isMain_);
    stockToolBar(toolBar, isMain_, fc);
       
    thumbnailView_ = new BioFabricOverview();
    nac_ = new BioFabricNavAndControl(isMain_, this, COMMAND_NAME);
    fmt_ = nac_.getFMT();
    cp_ = new BioFabricPanel(fc.getColorGenerator(), bfa_, fmt_, nac_.getOverview(), nac_.getNavTool(), isMain_, this);
    fc.setFabricPanel(cp_);
    nac_.setFabricPanel(cp_);
    cp_.setFabricLocation(nac_.getFabricLocation(), nac_.getMouseOverView());        
    cp_.setBackground(Color.white);
    
    JScrollPane jsp = new JScrollPane(cp_);
    jsp.getVerticalScrollBar().setUnitIncrement(16);
    jsp.getHorizontalScrollBar().setUnitIncrement(16);    
    cp_.setScroll(jsp);
    // GOTTA USE THIS ON MY LINUX BOX, BUT NOWHERE ELSE!!!!
    //jsp.getViewport().setScrollMode(JViewport.BACKINGSTORE_SCROLL_MODE);
    cp_.getZoomController().registerScrollPaneAndZoomTarget(jsp, cp_);
     
    cpane.setLayout(new BorderLayout());
    
    if (toolBar != null) {
      //TODO move tool bar into Cytoscape frame
      cpane.add(toolBar, BorderLayout.NORTH);
    }
        
    hidingPanel_ = new JPanel();
    myCard_ = new CardLayout();
    hidingPanel_.setLayout(myCard_);
    hidingPanel_.add(jsp, "SUPanel");
    JPanel blankPanel = new JPanel();
    blankPanel.setBackground(Color.white);
    hidingPanel_.add(blankPanel, "Hiding");

    sp_ = null;//new JSplitPane(JSplitPane.VERTICAL_SPLIT, hidingPanel_, nac_);TODO changed this
    //sp_.setDividerLocation((int)(dim.height * 0.50));       
    //sp_.setResizeWeight(1.0);


    cpane.add(hidingPanel_, BorderLayout.CENTER);//TODO changed this    
    //cpane.add(nac_, BorderLayout.SOUTH);    
    URL ugif = getClass().getResource("/images/BioFab16White.gif"); 
    Icon icon = new ImageIcon(ugif);
    this.setFrameIcon(icon);
    setResizable(true);
    fc.checkForChanges();
    return;
  } 
  
  /***************************************************************************
  **
  ** Call to let us know new gaggle commands are available
  */    
  
  public void haveInboundGaggleCommands() {
    CommandSet fc = CommandSet.getCmds(COMMAND_NAME);
    fc.triggerGaggleState(CommandSet.GAGGLE_PROCESS_INBOUND, true);
    return;
  }
  
  /***************************************************************************
  **
  ** Call to let us know gaggle geese have changed
  */    
  
  public void haveGaggleGooseChange() {
    CommandSet fc = CommandSet.getCmds(COMMAND_NAME);
    fc.triggerGaggleState(CommandSet.GAGGLE_GOOSE_UPDATE, true);
    return;
  }  
  
  /***************************************************************************
  **
  ** Call to let us know gaggle geese have changed
  */    
  
  public void connectedToGaggle(boolean connected) {
    CommandSet fc = CommandSet.getCmds(COMMAND_NAME);
    fc.getAction(CommandSet.GAGGLE_CONNECT, true, null).setEnabled(!connected);
    fc.getAction(CommandSet.GAGGLE_DISCONNECT, true, null).setEnabled(connected);
    fc.getAction(CommandSet.GAGGLE_CONNECT, false, null).setEnabled(!connected);
    fc.getAction(CommandSet.GAGGLE_DISCONNECT, false, null).setEnabled(connected);
    return;
  }  

 /***************************************************************************
  **
  ** Drawing core
  */
  
  public void stopBufferBuilding() {
    cp_.shutdown();
    return;
  }
 
  /***************************************************************************
  **
  ** Drawing core
  */
  
  public BioFabricApplication getApplication() {
    return (bfa_);
  }
   
  /***************************************************************************
  **
  ** Get fabric panel
  */
  
  public BioFabricPanel getFabricPanel() {
    return (cp_);
  }
  
  /***************************************************************************
  **
  ** Get overvoew panel
  */
  
  public BioFabricOverview getOverview() {
    return (nac_.getOverview());
  }
  
  /***************************************************************************
  **
  ** Hide/show nav and controls
  */
  
  public void showNavAndControl(boolean show) {//TODO can I remove this?
    if (show && sp_ != null) {
      sp_.setEnabled(true);
      nac_.setToBlank(!show);
      sp_.setDividerLocation(savedSplitFrac_);
    } else if(sp_ != null){
      nac_.setToBlank(!show);
      int lastLoc = sp_.getDividerLocation();
      savedSplitFrac_ = (double)lastLoc / (double)sp_.getHeight();
      sp_.setDividerLocation(1.0);
      sp_.setEnabled(false);
    }
    return;
  }
  
  /***************************************************************************
  **
  ** Hide/show nav and controls
  */
  
  public void showTour(boolean show) {//TODO can I remove this?
    if (nac_.showTour(show) && sp_ != null) {
      sp_.resetToPreferredSizes();
    }
    return;
  }

  /***************************************************************************
  **
  ** Menu install
  */
  
  private void menuInstall(CommandSet fc, boolean isMain, JMenu gaggleGooseChooseMenu) {
    ResourceManager rMan = ResourceManager.getManager();
    JMenuBar menuBar = new JMenuBar();

    if (isMain) {
      JMenu fMenu = new JMenu(rMan.getString("command.File"));
      fMenu.setMnemonic(rMan.getChar("command.FileMnem"));
      menuBar.add(fMenu);
      fMenu.add(fc.getAction(CommandSet.LOAD_XML, false, null));
      fMenu.add(fc.getAction(CommandSet.SAVE, false, null));
      fMenu.add(fc.getAction(CommandSet.SAVE_AS, false, null));
      fMenu.add(new JSeparator());
      JMenu importMenu = new JMenu(rMan.getString("command.importMenu"));
      importMenu.setMnemonic(rMan.getChar("command.importMenuMnem"));
      fMenu.add(importMenu);    
      importMenu.add(fc.getAction(CommandSet.LOAD, false, null)); 
      importMenu.add(fc.getAction(CommandSet.LOAD_WITH_NODE_ATTRIBUTES, false, null));       
      importMenu.add(fc.getAction(CommandSet.LOAD_WITH_EDGE_WEIGHTS, false, null));       
      JMenu exportMenu = new JMenu(rMan.getString("command.exportMenu"));
      exportMenu.setMnemonic(rMan.getChar("command.exportMenuMnem"));
      fMenu.add(exportMenu);    
      exportMenu.add(fc.getAction(CommandSet.EXPORT_IMAGE, false, null)); 
      exportMenu.add(fc.getAction(CommandSet.EXPORT_IMAGE_PUBLISH, false, null));      
      exportMenu.add(new JSeparator());
      exportMenu.add(fc.getAction(CommandSet.EXPORT_NODE_ORDER, false, null)); 
      exportMenu.add(fc.getAction(CommandSet.EXPORT_LINK_ORDER, false, null)); 
      exportMenu.add(new JSeparator()); 
      exportMenu.add(fc.getAction(CommandSet.EXPORT_SELECTED_NODES, false, null));       
      fMenu.add(new JSeparator());
      fMenu.add(fc.getAction(CommandSet.EMPTY_NETWORK, false, null));
      fMenu.add(new JSeparator());
      fMenu.add(fc.getAction(CommandSet.PRINT, false, null));
      fMenu.add(new JSeparator());
      fMenu.add(fc.getAction(CommandSet.PRINT_PDF, false, null));
      fMenu.add(new JSeparator());
      fMenu.add(fc.getAction(CommandSet.CLOSE, false, null));
    } else {
      JMenu fMenu = new JMenu(rMan.getString("command.File"));
      fMenu.setMnemonic(rMan.getChar("command.FileMnem"));
      menuBar.add(fMenu);
      JMenu exportMenu = new JMenu(rMan.getString("command.exportMenu"));
      exportMenu.setMnemonic(rMan.getChar("command.exportMenuMnem"));
      fMenu.add(exportMenu);    
      exportMenu.add(fc.getAction(CommandSet.EXPORT_IMAGE, false, null)); 
      exportMenu.add(fc.getAction(CommandSet.EXPORT_IMAGE_PUBLISH, false, null));
      exportMenu.add(new JSeparator());
      exportMenu.add(fc.getAction(CommandSet.EXPORT_SELECTED_NODES, false, null));       
    }
    
    JMenu eMenu = new JMenu(rMan.getString("command.Edit"));
    eMenu.setMnemonic(rMan.getChar("command.EditMnem"));
    menuBar.add(eMenu);
    eMenu.add(fc.getAction(CommandSet.CLEAR_SELECTIONS, false, null));    
    eMenu.add(fc.getAction(CommandSet.ADD_FIRST_NEIGHBORS, false, null));
    if (isMain) {
      eMenu.add(fc.getAction(CommandSet.PROPAGATE_DOWN, false, null));
    }
    Action bsa = fc.getAction(CommandSet.BUILD_SELECT, false, null);
    JCheckBoxMenuItem jcb = new JCheckBoxMenuItem(bsa);
    jcb.setSelected(true);
    eMenu.add(jcb);
    eMenu.add(new JSeparator());    
    eMenu.add(fc.getAction(CommandSet.SET_DISPLAY_OPTIONS, false, null));

    JMenu vMenu = new JMenu(rMan.getString("command.View"));
    vMenu.setMnemonic(rMan.getChar("command.ViewMnem"));
    menuBar.add(vMenu);
    vMenu.add(fc.getAction(CommandSet.ZOOM_OUT, false, null));    
    vMenu.add(fc.getAction(CommandSet.ZOOM_IN, false, null));
    vMenu.add(fc.getAction(CommandSet.ZOOM_TO_MODEL, false, null)); 
    vMenu.add(fc.getAction(CommandSet.ZOOM_TO_RECT, false, null));   
    vMenu.add(fc.getAction(CommandSet.ZOOM_TO_CURRENT_MOUSE, false, null));
    vMenu.add(fc.getAction(CommandSet.ZOOM_TO_CURRENT_MAGNIFY, false, null));
    vMenu.add(fc.getAction(CommandSet.ZOOM_TO_SELECTIONS, false, null));
    vMenu.add(new JSeparator());    
    vMenu.add(fc.getAction(CommandSet.CENTER_ON_PREVIOUS_SELECTION, false, null));
    vMenu.add(fc.getAction(CommandSet.ZOOM_TO_CURRENT_SELECTION, false, null));
    vMenu.add(fc.getAction(CommandSet.CENTER_ON_NEXT_SELECTION, false, null));
    
    //
    // Tools Menu
    //
    
    JMenu sMenu = new JMenu(rMan.getString("command.Tools"));
    sMenu.setMnemonic(rMan.getChar("command.ToolsMnem"));
    menuBar.add(sMenu);
    sMenu.add(fc.getAction(CommandSet.SEARCH, false, null));
    sMenu.add(fc.getAction(CommandSet.COMPARE_NODES, false, null));
    
    //
    // Layout Menu
    //
    
    JMenu lMenu = new JMenu(rMan.getString("command.Layout"));
    lMenu.setMnemonic(rMan.getChar("command.LayoutMnem"));
    menuBar.add(lMenu);
    lMenu.add(fc.getAction(CommandSet.DEFAULT_LAYOUT, false, null));
    lMenu.add(fc.getAction(CommandSet.RELAYOUT_USING_CONNECTIVITY, false, null));
    lMenu.add(fc.getAction(CommandSet.RELAYOUT_USING_SHAPE_MATCH, false, null));
    lMenu.add(fc.getAction(CommandSet.LAYOUT_NODES_VIA_ATTRIBUTES, false, null));
    lMenu.add(fc.getAction(CommandSet.LAYOUT_LINKS_VIA_ATTRIBUTES, false, null));
    lMenu.add(fc.getAction(CommandSet.LAYOUT_VIA_NODE_CLUSTER_ASSIGN, false, null));   
    lMenu.add(fc.getAction(CommandSet.LAYOUT_TOP_CONTROL, false, null)); 
    lMenu.add(fc.getAction(CommandSet.HIER_DAG_LAYOUT, false, null)); 
    lMenu.add(fc.getAction(CommandSet.WORLD_BANK_LAYOUT, false, null)); 
    lMenu.add(fc.getAction(CommandSet.SET_LINK_GROUPS, false, null));
 
    //
    // Windows Menu
    //
    
    JMenu wMenu = new JMenu(rMan.getString("command.Windows"));
    wMenu.setMnemonic(rMan.getChar("command.ToolsMnem"));
    menuBar.add(wMenu);
    JCheckBoxMenuItem jcbS = new JCheckBoxMenuItem(fc.getAction(CommandSet.SHOW_NAV_PANEL, false, null));
    jcbS.setSelected(true);
    wMenu.add(jcbS);
    JCheckBoxMenuItem jcbT = new JCheckBoxMenuItem(fc.getAction(CommandSet.SHOW_TOUR, false, null));
    jcbT.setSelected(true);
    wMenu.add(jcbT);
  
    //
    // Gaggle Menu
    //
    
    if (doGaggle_) {
      JMenu gMenu = new JMenu(rMan.getString("command.Gaggle"));
      gMenu.setMnemonic(rMan.getChar("command.GaggleMnem"));
      menuBar.add(gMenu);
      gMenu.add(gaggleGooseChooseMenu);
      gMenu.add(fc.getAction(CommandSet.GAGGLE_GOOSE_UPDATE, false, null));
      gMenu.add(fc.getAction(CommandSet.GAGGLE_RAISE_GOOSE, false, null));
      gMenu.add(fc.getAction(CommandSet.GAGGLE_LOWER_GOOSE, false, null));
      gMenu.add(fc.getAction(CommandSet.GAGGLE_SEND_NETWORK, false, null));
      gMenu.add(fc.getAction(CommandSet.GAGGLE_SEND_NAMELIST, false, null));    
      gMenu.add(fc.getAction(CommandSet.GAGGLE_PROCESS_INBOUND, false, null));          
      gMenu.add(fc.getAction(CommandSet.GAGGLE_CONNECT, false, null));
      gMenu.add(fc.getAction(CommandSet.GAGGLE_DISCONNECT, false, null));
    }
    
    JMenu hMenu = new JMenu(rMan.getString("command.Help"));
    hMenu.setMnemonic(rMan.getChar("command.HelpMnem"));
    menuBar.add(hMenu);
    hMenu.add(fc.getAction(CommandSet.ABOUT, false, null));
    
    setJMenuBar(menuBar);
    return;
  }
    
  /***************************************************************************
  **
  ** Stock the action map
  */ 
  
  private void stockActionMap(CommandSet fc, boolean isMain) {  
    actionMap_.put(Integer.valueOf(CommandSet.SEARCH), fc.getAction(CommandSet.SEARCH, true, null));
    actionMap_.put(Integer.valueOf(CommandSet.ZOOM_OUT), fc.getAction(CommandSet.ZOOM_OUT, true, null));
    actionMap_.put(Integer.valueOf(CommandSet.ZOOM_IN), fc.getAction(CommandSet.ZOOM_IN, true, null));
    actionMap_.put(Integer.valueOf(CommandSet.ADD_FIRST_NEIGHBORS), fc.getAction(CommandSet.ADD_FIRST_NEIGHBORS, true, null));
    actionMap_.put(Integer.valueOf(CommandSet.CLEAR_SELECTIONS), fc.getAction(CommandSet.CLEAR_SELECTIONS, true, null));
    actionMap_.put(Integer.valueOf(CommandSet.ZOOM_TO_MODEL), fc.getAction(CommandSet.ZOOM_TO_MODEL, true, null));
    actionMap_.put(Integer.valueOf(CommandSet.ZOOM_TO_SELECTIONS), fc.getAction(CommandSet.ZOOM_TO_SELECTIONS, true, null));
    actionMap_.put(Integer.valueOf(CommandSet.ZOOM_TO_RECT), fc.getAction(CommandSet.ZOOM_TO_RECT, true, null));
    actionMap_.put(Integer.valueOf(CommandSet.CANCEL), fc.getAction(CommandSet.CANCEL, true, null));
    actionMap_.put(Integer.valueOf(CommandSet.ZOOM_TO_CURRENT_SELECTION), fc.getAction(CommandSet.ZOOM_TO_CURRENT_SELECTION, true, null));
    actionMap_.put(Integer.valueOf(CommandSet.CENTER_ON_NEXT_SELECTION), fc.getAction(CommandSet.CENTER_ON_NEXT_SELECTION, true, null));
    actionMap_.put(Integer.valueOf(CommandSet.CENTER_ON_PREVIOUS_SELECTION), fc.getAction(CommandSet.CENTER_ON_PREVIOUS_SELECTION, true, null));
    
    if (isMain) {
      actionMap_.put(Integer.valueOf(CommandSet.PROPAGATE_DOWN), fc.getAction(CommandSet.PROPAGATE_DOWN, true, null));
    }    
    if (doGaggle_) {
      actionMap_.put(Integer.valueOf(CommandSet.GAGGLE_GOOSE_UPDATE), fc.getAction(CommandSet.GAGGLE_GOOSE_UPDATE, true, null));
      actionMap_.put(Integer.valueOf(CommandSet.GAGGLE_RAISE_GOOSE), fc.getAction(CommandSet.GAGGLE_RAISE_GOOSE, true, null));
      actionMap_.put(Integer.valueOf(CommandSet.GAGGLE_LOWER_GOOSE), fc.getAction(CommandSet.GAGGLE_LOWER_GOOSE, true, null));
      actionMap_.put(Integer.valueOf(CommandSet.GAGGLE_SEND_NETWORK), fc.getAction(CommandSet.GAGGLE_SEND_NETWORK, true, null));
      actionMap_.put(Integer.valueOf(CommandSet.GAGGLE_SEND_NAMELIST), fc.getAction(CommandSet.GAGGLE_SEND_NAMELIST, true, null));
      actionMap_.put(Integer.valueOf(CommandSet.GAGGLE_PROCESS_INBOUND), fc.getAction(CommandSet.GAGGLE_PROCESS_INBOUND, true, null));
      actionMap_.put(Integer.valueOf(CommandSet.GAGGLE_CONNECT), fc.getAction(CommandSet.GAGGLE_CONNECT, true, null));
      actionMap_.put(Integer.valueOf(CommandSet.GAGGLE_DISCONNECT), fc.getAction(CommandSet.GAGGLE_DISCONNECT, true, null));
    }    
    return;
  }

  /***************************************************************************
  ** TODO: Transfer to cytoscape 
  ** Stock the tool bar
  */ 
  
  private void stockToolBar(JToolBar toolBar, boolean isMain, CommandSet fc) {
    toolBar.removeAll();  
    //toolBar.add(actionMap_.get(Integer.valueOf(CommandSet.ZOOM_OUT)));
    //toolBar.add(actionMap_.get(Integer.valueOf(CommandSet.ZOOM_IN)));
    //toolBar.add(actionMap_.get(Integer.valueOf(CommandSet.ZOOM_TO_MODEL)));
    //toolBar.add(actionMap_.get(Integer.valueOf(CommandSet.ZOOM_TO_RECT)));    
    //toolBar.add(actionMap_.get(Integer.valueOf(CommandSet.ZOOM_TO_SELECTIONS)));
    //toolBar.addSeparator();
    //toolBar.add(actionMap_.get(Integer.valueOf(CommandSet.CENTER_ON_PREVIOUS_SELECTION)));
    //toolBar.add(actionMap_.get(Integer.valueOf(CommandSet.ZOOM_TO_CURRENT_SELECTION)));
    //toolBar.add(actionMap_.get(Integer.valueOf(CommandSet.CENTER_ON_NEXT_SELECTION)));
    //toolBar.addSeparator();        
    toolBar.add(actionMap_.get(Integer.valueOf(CommandSet.ADD_FIRST_NEIGHBORS)));
    //toolBar.add(actionMap_.get(Integer.valueOf(CommandSet.CLEAR_SELECTIONS)));
    //toolBar.addSeparator();        
    //toolBar.add(actionMap_.get(Integer.valueOf(CommandSet.CANCEL)));
    //toolBar.addSeparator();    
    //toolBar.add(actionMap_.get(Integer.valueOf(CommandSet.SEARCH)));
    if (isMain) {
      toolBar.addSeparator();  
      toolBar.add(actionMap_.get(Integer.valueOf(CommandSet.PROPAGATE_DOWN)));
    }
    
    if (doGaggle_) {
      boolean updateMcmd = false;
      toolBar.addSeparator();
      if (gaggleUpdateGooseButton_ == null) {
        Action gaggleUpdate = actionMap_.get(Integer.valueOf(CommandSet.GAGGLE_GOOSE_UPDATE));
        gaggleUpdateGooseButton_ = toolBar.add(gaggleUpdate);
        updateMcmd = true;
      } else {
        toolBar.add(gaggleUpdateGooseButton_);
      }
      toolBar.add(gaggleGooseCombo_);
      toolBar.add(actionMap_.get(Integer.valueOf(CommandSet.GAGGLE_RAISE_GOOSE)));
      toolBar.add(actionMap_.get(Integer.valueOf(CommandSet.GAGGLE_LOWER_GOOSE)));
      toolBar.add(actionMap_.get(Integer.valueOf(CommandSet.GAGGLE_SEND_NETWORK)));
      toolBar.add(actionMap_.get(Integer.valueOf(CommandSet.GAGGLE_SEND_NAMELIST))); 
      toolBar.add(actionMap_.get(Integer.valueOf(CommandSet.GAGGLE_CONNECT))); 
      toolBar.add(actionMap_.get(Integer.valueOf(CommandSet.GAGGLE_DISCONNECT))); 
      if (gaggleInstallButton_ == null) {
        AbstractAction gaggleInstall = (AbstractAction)actionMap_.get(Integer.valueOf(CommandSet.GAGGLE_PROCESS_INBOUND));
        gaggleInstallButton_ = toolBar.add(gaggleInstall);
        updateMcmd = true;
      } else {
        toolBar.add(gaggleInstallButton_);
      }
      if (updateMcmd) {
        fc.setGaggleButtons(gaggleInstallButton_, gaggleUpdateGooseButton_, gaggleInstallButton_.getBackground());
      }      
    } 
    return;
  }
  
  public BioFabricNavAndControl getNAC(){
	  return nac_;
  }
  
  public BioFabricOverview getThumbnailView(){ 
	  return thumbnailView_;
  }  
  
  public void setDialogTaskManager(DialogTaskManager dialogTaskManager){
	  this.dialogTaskManager_ = dialogTaskManager;
  }
  
  public DialogTaskManager getDialogTaskManager(){
	  return this.dialogTaskManager_;
  }
}
