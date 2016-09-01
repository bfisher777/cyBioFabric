/*
**    Copyright (C) 2003-2011 Institute for Systems Biology 
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

package org.systemsbiology.cyBioFabric.internal.biofabric.ui.display;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import javax.swing.JSplitPane;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import org.systemsbiology.cyBioFabric.internal.biofabric.cmd.CommandSet;
import org.systemsbiology.cyBioFabric.internal.biofabric.util.ResourceManager;


/****************************************************************************
**
** This is the BioFabric Control dashboard
*/

public class BioFabricNavAndControl extends JPanel {
  
  ////////////////////////////////////////////////////////////////////////////
  //
  // PRIVATE INSTANCE MEMBERS
  //
  //////////////////////////////////////////////////////////////////////////// 
   
  private FabricMagnifyingTool fmt_;
  private BioFabricOverview bfo_;
  private MouseOverView mvo_;
  private FabricNavTool fnt_;
  private FabricNavTool.LabeledFabricNavTool lfnt_;
  private FabricLocation floc_;
  private CardLayout clay_;
  private boolean collapsed_;
  private JPanel withControls_;
  private JSplitPane spot_;
  private double savedSplitFrac_;
  private static final long serialVersionUID = 1L;
  private JPanel fopan_;
  ////////////////////////////////////////////////////////////////////////////
  //
  // PUBLIC CONSTRUCTORS
  //
  ////////////////////////////////////////////////////////////////////////////

  /***************************************************************************
  **
  ** Constructor
  */

  public BioFabricNavAndControl(boolean isMain, JInternalFrame topWindow, String commandName) {

    floc_ = new FabricLocation();//Mouse Over Node Row, Mouse Over Link, Mouse Over Node Link Zone

    //CommandSet fc = CommandSet.getCmds((isMain) ? "mainWindow" : "selectionWindow");
    CommandSet fc = CommandSet.getCmds(commandName);
    fmt_ = new FabricMagnifyingTool(fc.getColorGenerator());
    fmt_.keyInstall((JPanel)topWindow.getContentPane());
    JPanel fmpan = new JPanel();
    fmpan.setLayout(new BorderLayout());
    fmpan.setBorder(new LineBorder(Color.black, 2));
    Font labelFont = new Font("SansSerif", Font.BOLD, 20);
    JLabel magLab = new JLabel(ResourceManager.getManager().getString("biofabric.magnifier"));
    magLab.setBorder(new EmptyBorder(0, 5, 0, 0));
    magLab.setOpaque(true);
    magLab.setBackground(Color.white);
    magLab.setFont(labelFont);
    fmpan.add(magLab, BorderLayout.NORTH);
    fmpan.add(fmt_, BorderLayout.CENTER);

    bfo_ = new BioFabricOverview();
    fmt_.setFabricOverview(bfo_);
    
    mvo_ = new MouseOverView();
    
    fopan_ = new JPanel();
    fopan_.setLayout(new BorderLayout());
    fopan_.setBorder(new LineBorder(Color.black, 2));
    JLabel overLab = new JLabel(ResourceManager.getManager().getString("biofabric.overview"));
    overLab.setBorder(new EmptyBorder(0, 5, 0, 0));
    overLab.setOpaque(true);
    overLab.setBackground(Color.white);
    overLab.setFont(labelFont);
    fopan_.add(overLab, BorderLayout.NORTH);
    fopan_.add(bfo_, BorderLayout.CENTER);
    
    lfnt_ = new FabricNavTool.LabeledFabricNavTool(topWindow, labelFont);
    fnt_ = lfnt_.getFabricNavTool();
     
    spot_ = new JSplitPane(JSplitPane.VERTICAL_SPLIT, null, lfnt_);//TODO changed this hor to vert
    //JPanel dmvo = new JPanel();
   // dmvo.setLayout(new GridLayout(1, 2));
   // dmvo.add(mvo_.getPanel(0));
   // dmvo.add(mvo_.getPanel(1));
  
    //spot_ = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, fopan_, dmvo);
    spot_.setBorder(new EmptyBorder(0,0,0,0));
    //spot_.setResizeWeight(1.0);

    JSplitPane sp = new JSplitPane(JSplitPane.VERTICAL_SPLIT, fmpan, spot_); //TODO changed this hor to vert
    sp.setResizeWeight(0.6);//TODO added this to give the magnifier more space in the CytoPanel
    withControls_ = new JPanel();
    withControls_.setLayout(new BorderLayout());
    withControls_.add(floc_, BorderLayout.NORTH);
    withControls_.add(sp, BorderLayout.CENTER);
    
    clay_ = new CardLayout();
    this.setLayout(clay_);
    this.add(withControls_, "cntrl");
    this.add(new JPanel(), "blank");
    clay_.show(this, "cntrl"); 
    collapsed_ = false;
    
    return;
  }
  
  ////////////////////////////////////////////////////////////////////////////
  //
  // PUBLIC METHODS
  //
  ////////////////////////////////////////////////////////////////////////////

  /***************************************************************************
  **
  ** Sizing
  */
  
  @Override
  public Dimension getPreferredSize() {
    if (collapsed_) {
      return (new Dimension(0, 0));    
    } else {
      return (withControls_.getPreferredSize());
    } 
  }

  @Override
  public Dimension getMinimumSize() {
    if (collapsed_) {
      return (new Dimension(0, 0));    
    } else {
      return (withControls_.getMinimumSize());
    } 
  }
  
  @Override
  public Dimension getMaximumSize() {
    if (collapsed_) {
      return (new Dimension(0, 0));    
    } else {
      return (withControls_.getMaximumSize());
    } 
  }

  /***************************************************************************
  **
  ** Set bounds
  */

  @Override
  public void setBounds(int x, int y, int width, int height) {
    super.setBounds(x, y, width, height);
    repaint();
    return;
  }

  /***************************************************************************
  **
  ** Hide/show nav and controls
  */
  
  public boolean showTour(boolean show) {
    if (show) {
      spot_.setEnabled(true);
      lfnt_.setToBlank(!show);
      double need = (double)(spot_.getWidth() - lfnt_.getMinimumSize().width) / (double)spot_.getWidth();
      System.out.println("ret " + savedSplitFrac_ + " " + spot_.getWidth() + " " + need + " " + lfnt_.getMinimumSize().width);
      spot_.setDividerLocation(Math.min(savedSplitFrac_, need));
      if (lfnt_.getMinimumSize().height > this.getHeight()) {
        return (true);
      }
    } else {
      lfnt_.setToBlank(!show);
      int lastLoc = spot_.getDividerLocation();
      savedSplitFrac_ = (double)lastLoc / (double)spot_.getWidth();
      System.out.println(lastLoc + " " + spot_.getWidth() + " " + savedSplitFrac_);
      spot_.setDividerLocation(1.0);
      spot_.setEnabled(false);
    }
    return (false);
  }
  
  /***************************************************************************
  **
  ** Get the FabricNavTool
  */

  public FabricNavTool getNavTool() {
    return (fnt_);
  }  
  
  /***************************************************************************
  **
  ** Set to blank or populated
  */

  public void setToBlank(boolean val) {
    clay_.show(this, (val) ? "blank" : "cntrl");
    collapsed_ = val;
    return;
  }  
    
  /***************************************************************************
  **
  ** Get the FabricLocation
  */

  public FabricLocation getFabricLocation() {
    return (floc_);
  }  
  
  /***************************************************************************
  **
  ** Get the Mouseover view:
  */

  public MouseOverView getMouseOverView() {
    return (mvo_);
  }  
   
  /***************************************************************************
  **
  ** Get the FMT
  */

  public FabricMagnifyingTool getFMT() {
    return (fmt_);
  }
  
  /***************************************************************************
  **
  ** Get the Overview
  */

  public BioFabricOverview getOverview() {
    return bfo_;
  }
   
  /***************************************************************************
  **
  ** Set the fabric panel
  */

  public void setFabricPanel(BioFabricPanel cp) {
    fnt_.setFabricPanel(cp);
    return;
  }
  
  public JPanel getFabricOverviewPanel(){
	  return fopan_;
  }
}