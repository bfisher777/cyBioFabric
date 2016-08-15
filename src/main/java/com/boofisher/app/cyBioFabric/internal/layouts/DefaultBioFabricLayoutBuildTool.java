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

package com.boofisher.app.cyBioFabric.internal.layouts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;

import com.boofisher.app.cyBioFabric.internal.biofabric.ui.FabricColorGenerator;
import com.boofisher.app.cyBioFabric.internal.tools.NodeNameSUIDPair;
import com.boofisher.app.cyBioFabric.internal.biofabric.model.BioFabricNetwork;
import com.boofisher.app.cyBioFabric.internal.biofabric.model.FabricLink;


/****************************************************************************
**
** Build a biofabric network using default layout
*/

public class DefaultBioFabricLayoutBuildTool {
  
  ////////////////////////////////////////////////////////////////////////////
  //
  // PUBLIC CONSTANTS
  //
  //////////////////////////////////////////////////////////////////////////// 

  
  private FabricColorGenerator colorGenerator;  

  
  public DefaultBioFabricLayoutBuildTool(){
	  this.colorGenerator = new FabricColorGenerator();
	  this.colorGenerator.newColorModel();	  
  }
  
  /***************************************************************************
  **
  ** Common load operations.  Take your pick of input sources
  */ 
    
  public BioFabricNetwork loadDataFromCytoscape(CyNetworkView networkView) {  
    ArrayList<FabricLink> links = new ArrayList<FabricLink>();
    ArrayList<NodeNameSUIDPair> loneNodes = new ArrayList<NodeNameSUIDPair>();       
	  try { 
	    bioFabricDataLoader(networkView, links, loneNodes); 
	    return finishLoad(networkView, links, loneNodes);
	  } catch (OutOfMemoryError oom) {
	    //ExceptionHandler.getHandler().displayOutOfMemory(oom);
	    return null;  
	  }
      
  }

/***************************************************************************
 ** 
 ** Process a network
 */

 private void bioFabricDataLoader(CyNetworkView networkView, List<FabricLink> links, ArrayList<NodeNameSUIDPair> loneNodes) { 
	  		  
	  CyNetwork network = networkView.getModel();		  		 
	  Collection<View<CyNode>> nodeViews = networkView.getNodeViews();
	  Collection<View<CyEdge>> edgeViews = networkView.getEdgeViews();
	  
	  //get all edges and make a fabric link
	  for(View<CyEdge> edgeView : edgeViews){
		  
		  CyEdge edgeModel = edgeView.getModel();	
		  CyNode nodeSource = edgeView.getModel().getSource();
		  CyNode nodeTarget = edgeView.getModel().getTarget();
		  //set source, target and relationship
		  //String source = nodeSource.getSUID().toString();	
		  //String target = nodeTarget.getSUID().toString();
		  
		  String source = networkView.getModel().getRow(nodeSource).get(CyNetwork.NAME, String.class);
		  String target = networkView.getModel().getRow(nodeTarget).get(CyNetwork.NAME, String.class);
		  
		  String rel = network.getRow(edgeModel).get(CyEdge.INTERACTION, String.class);//name of the edge?		 
			  
		  FabricLink nextLink = new FabricLink(source, target, rel, false, edgeModel.getSUID(), nodeSource.getSUID(), nodeTarget.getSUID());
		  links.add(nextLink);
	      // We never create shadow feedback links
		  //TODO do new shadow links need to be added to the network
		  if (!source.equals(target)) {
			  //TODO create hidden edge and then add to network			   
			  //CyEdge newEdge = network.addEdge(nodeSource, nodeTarget, edgeView.getModel().isDirected());
			  //network.getRow(newEdge).set(CyNetwork.NAME, rel);
			  //fabricEdges.add(newEdge);
			  FabricLink nextShadowLink = new FabricLink(source, target, rel, true, edgeModel.getSUID(), nodeSource.getSUID(), nodeTarget.getSUID());
			  links.add(nextShadowLink);
	      }	
		  edgeView.setVisualProperty(BasicVisualLexicon.EDGE_LABEL, rel);
	  }	
	  
	  
	  //add nodes of degree 0 to list loneNodes
	  for(View<CyNode> nodeView : nodeViews){		  
		  if(networkView.getModel().getAdjacentEdgeList(nodeView.getModel(), CyEdge.Type.ANY).size() == 0){			  
			  //String loner = network.getRow(nodeView.getModel()).get(CyNetwork.NAME, String.class);
		  	  //loneNodes.add(nodeView.getModel().getSUID().toString());
			  loneNodes.add(new NodeNameSUIDPair(nodeView.getModel().getSUID(), nodeView.getVisualProperty(BasicVisualLexicon.NODE_LABEL)));
		  }
	  }		  
 }
   
  /***************************************************************************
  **
  ** Common load operations.
  ** Method may return null.
  */ 
    
  private BioFabricNetwork finishLoad(CyNetworkView networkView, List<FabricLink> links, ArrayList<NodeNameSUIDPair> loneNodes) {
	  BioFabricNetwork bfn = null;
	  
	  try {	      
	      SortedMap<FabricLink.AugRelation, Boolean> relaMap = BioFabricNetwork.extractRelations(links);               	 
	                  
	      assignDirections(links, relaMap);
	      HashSet<FabricLink> reducedLinks = new HashSet<FabricLink>();
	      HashSet<FabricLink> culledLinks = new HashSet<FabricLink>();
	      preprocessLinks(links, reducedLinks, culledLinks);

	      BioFabricNetwork.RelayoutBuildData bfnbd = new BioFabricNetwork.RelayoutBuildData(networkView, reducedLinks, loneNodes, this.colorGenerator, 
	                                                                                      BioFabricNetwork.BuildMode.BUILD_FROM_SIF);
	       
	      // Possibly expensive network analysis preparation:
	      bfn = new BioFabricNetwork(bfnbd);	      
	      
    } catch (OutOfMemoryError oom) {
      System.err.println("Out Of Memomry: " + oom.toString());
      return null;  
    }
    System.out.println("Finished building BioFabricNetwork");
    return bfn;
  } 
  
  /***************************************************************************
   ** 
   ** Process a link set that has not had directionality established
   */

   public static void assignDirections(List<FabricLink> allLinks, Map<FabricLink.AugRelation, Boolean> relMap) {    
     Iterator<FabricLink> alit = allLinks.iterator();
     while (alit.hasNext()) {
       FabricLink nextLink = (FabricLink)alit.next();
       FabricLink.AugRelation rel = nextLink.getAugRelation();
       Boolean isDir = (Boolean)relMap.get(rel);
       nextLink.installDirection(isDir);
     }
     return;
   }  
   
   /***************************************************************************
    ** 
    ** This culls a set of links to remove non-directional synonymous and
    ** duplicate links.  Note that shadow links have already been created
    ** and added to the allLinks list. 
    */

    public static void preprocessLinks(List<FabricLink> allLinks, Set<FabricLink> retval, Set<FabricLink> culled) {
      Iterator<FabricLink> alit = allLinks.iterator();
      while (alit.hasNext()) {
        FabricLink nextLink = (FabricLink)alit.next();
        if (retval.contains(nextLink)) {
          culled.add(nextLink);
        } else if (!nextLink.isDirected()) {
          if (!nextLink.isFeedback()) {
            FabricLink flipLink = nextLink.flipped();
            if (retval.contains(flipLink)) {
              // Make the order consistent for a given src & pair!
              if (nextLink.compareTo(flipLink) < 0) {
                retval.remove(flipLink);
                culled.add(flipLink);
                retval.add(nextLink);
              } else {
                culled.add(nextLink);              
              }  
            } else {
              retval.add(nextLink);
            }
          } else {
            retval.add(nextLink);
          }
        } else {
          retval.add(nextLink);
        }
      }
      return;
    }            
   
}