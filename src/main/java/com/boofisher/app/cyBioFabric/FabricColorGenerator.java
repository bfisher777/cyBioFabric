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

package com.boofisher.app.cyBioFabric;

import java.awt.Color;
import java.util.HashMap;


/****************************************************************************
**
** Color generator
*/

public class FabricColorGenerator {
  
  ////////////////////////////////////////////////////////////////////////////
  //
  // PUBLIC CONSTANTS
  //
  ////////////////////////////////////////////////////////////////////////////

  public final static int UNCHANGED = 0;
  public final static int BRIGHTER  = 1;
  public final static int DARKER    = 2;
      
  ////////////////////////////////////////////////////////////////////////////
  //
  // PRIVATE MEMBERS
  //
  ////////////////////////////////////////////////////////////////////////////

  private ColorGenerator myColGen_;
  private HashMap<String, Color> brighter_;
  private HashMap<String, Color> darker_;

  ////////////////////////////////////////////////////////////////////////////
  //
  // PUBLIC CONSTRUCTORS
  //
  ////////////////////////////////////////////////////////////////////////////

  /***************************************************************************
  **
  ** Null constructor
  */

  public FabricColorGenerator() {
    myColGen_ = new ColorGenerator();
    brighter_ = new HashMap<String, Color>();
    darker_ = new HashMap<String, Color>();
  }

  ////////////////////////////////////////////////////////////////////////////
  //
  // PUBLIC METHODS
  //
  ////////////////////////////////////////////////////////////////////////////
  
  /***************************************************************************
  ** 
  ** Start a new model
  */

  public void newColorModel() {
    boolean useHSV = false;
    myColGen_.newColorModel();
    /*FabricDisplayOptions fdo = FabricDisplayOptionsManager.getMgr().getDisplayOptions();
    double light = fdo.getNodeLighterLevel() + 1.0;
    double dark = 1.0 / (fdo.getLinkDarkerLevel() + 1.0);*/
    //TODO this has been altered
    double light =  1.0;
    double dark = 1.0;
    float[] hsbvals = new float[3];
    int numCol = myColGen_.getNumColors();
    for (int i = 0; i < numCol; i++) {
      String gckey = myColGen_.getGeneColor(i);
      NamedColor nc = myColGen_.getNamedColor(gckey);
      int baseR = nc.color.getRed();    
      int baseG = nc.color.getGreen();  
      int baseB = nc.color.getBlue();
      if (useHSV) {
        Color.RGBtoHSB(baseR, baseG, baseB, hsbvals);
        float lb = Math.min(1.0F, hsbvals[2] * (float)light);
        brighter_.put(gckey, Color.getHSBColor(hsbvals[0], hsbvals[1], lb));
        float db = Math.min(1.0F, hsbvals[2] * (float)dark);
        darker_.put(gckey, Color.getHSBColor(hsbvals[0], hsbvals[1], db));
      } else {
      	if ((nc.name.indexOf("Gold") == -1) && (nc.name.indexOf("Dark Wheat") == -1) &&  (nc.name.indexOf("Pale Goldenrod") == -1)) {
	        int rb = Math.min(255, (int)Math.round((double)baseR * light));
	        int gb = Math.min(255, (int)Math.round((double)baseG * light));
	        int bb = Math.min(255, (int)Math.round((double)baseB * light));       
	        brighter_.put(gckey, new Color(rb, gb, bb));
      	} else {
      		System.out.println("Not light on " + nc.name);
      		brighter_.put(gckey, nc.color);
      	}
        int rd = Math.min(255, (int)Math.round((double)baseR * dark));
        int gd = Math.min(255, (int)Math.round((double)baseG * dark));
        int bd = Math.min(255, (int)Math.round((double)baseB * dark));
        darker_.put(gckey, new Color(rd, gd, bd));
      }
    }
  }   
  
  /***************************************************************************
  ** 
  ** Drop everything
  */

  public void dropColors() {
    myColGen_.dropColors();
    brighter_.clear();
    darker_.clear();
    return;
  }
  
  /***************************************************************************
  ** 
  ** Add color for IO
  */

  void addColorForIO(int target, NamedColor color) {
    switch (target) {
      case BRIGHTER:
        brighter_.put(color.name, color.color);
        break;
      case DARKER:
        darker_.put(color.name, color.color);
        break;
      case UNCHANGED:
      default:
        throw new IllegalArgumentException();
    }
    return;
  }
  
  /***************************************************************************
  ** 
  ** Get the modified color
  */

  public Color getModifiedColor(String colorKey, int which) {
    Color paintCol;
    switch (which) { 
      case UNCHANGED:
        NamedColor nc = myColGen_.getNamedColor(colorKey);
        paintCol = ((nc == null) || (nc.color == null)) ? null : nc.color;
        break;
      case DARKER:
        paintCol = darker_.get(colorKey);
        break;
      case BRIGHTER:
        paintCol = brighter_.get(colorKey);
        break;
      default:
        throw new IllegalArgumentException();
    }
    if (paintCol == null) {
      paintCol = Color.BLACK;
    }
    return (paintCol);
  }  

  /***************************************************************************
  **
  ** Get Ith available color
  */
  
  public String getGeneColor(int i) {
    return (myColGen_.getGeneColor(i));
  }   
  
  /***************************************************************************
  **
  ** Get number of colors
  */
  
  public int getNumColors() {
    return (myColGen_.getNumColors());
  }
 
   
 
}
