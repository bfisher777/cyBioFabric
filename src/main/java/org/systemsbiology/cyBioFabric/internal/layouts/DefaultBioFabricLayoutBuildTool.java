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

package org.systemsbiology.cyBioFabric.internal.layouts;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.systemsbiology.cyBioFabric.internal.biofabric.model.BioFabricNetwork;
import org.systemsbiology.cyBioFabric.internal.biofabric.model.FabricLink;
import org.systemsbiology.cyBioFabric.internal.biofabric.model.MasterBioFabricNetwork;
import org.systemsbiology.cyBioFabric.internal.biofabric.model.BioFabricNetwork.BuildData;
import org.systemsbiology.cyBioFabric.internal.biofabric.model.BioFabricNetwork.LinkInfo;
import org.systemsbiology.cyBioFabric.internal.biofabric.model.BioFabricNetwork.NodeInfo;
import org.systemsbiology.cyBioFabric.internal.biofabric.ui.FabricColorGenerator;
import org.systemsbiology.cyBioFabric.internal.tools.NodeNameSUIDPair;


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
		  //TODO: does this work with directed networks?
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
	  //MasterBioFabricNetwork masterBfn = null;
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
	      
	      //for testing 
	      /*  
	      Set singleNodes = new HashSet<String>();
	      
	      for(NodeNameSUIDPair pair : loneNodes){
	    	  singleNodes.add(pair.getName());
	      }
	      System.out.println("\tHas lone nodes set changed? loneNodes.size == singleNodes.size: " + (loneNodes.size() == singleNodes.size()));	      	      
	      MasterBioFabricNetwork.OrigBuildData masterbfnOD = new MasterBioFabricNetwork.OrigBuildData(reducedLinks, singleNodes, this.colorGenerator, MasterBioFabricNetwork.BUILD_FROM_SIF);
	      masterBfn = new MasterBioFabricNetwork(masterbfnOD); 	      
	      compareNetworks(bfn, masterBfn, masterbfnOD);
	      */
	      
    } catch (OutOfMemoryError oom) {
      System.err.println("Out Of Memomry: " + oom.toString());
      return null;  
    }
    
    return bfn;
  } 
  
  
  public void compareNetworks(BioFabricNetwork bfn, MasterBioFabricNetwork mbfn, MasterBioFabricNetwork.OrigBuildData origBD){
	  List<String> lines = new ArrayList<String>();
	  
	  lines.add("BINDYeast Comparing networks:");
	  
	  HashMap masterRowToTarg_ = mbfn.rowToTarg_;
	  HashMap<Integer, String> rowToTarg_ = bfn.rowToTarg_;
	  //assert(masterRowToTarg_.size() == rowToTarg_.size());	  
	  lines.add("masterRowToTarg_.size() == rowToTarg_.size(): " + (masterRowToTarg_.size() == rowToTarg_.size()) + "\nmasterRowToTarg_.size(): " + masterRowToTarg_.size() + ", rowToTarg_.size(): " + rowToTarg_.size());
	  
	  lines.add("Comparing rowToTarg_\n");
	  for (Map.Entry<Integer, String> entry : rowToTarg_.entrySet())
	  {		  
	      lines.add("Row: " + entry.getKey() + "\t" + ((String)masterRowToTarg_.get(entry.getKey())) + "\t\t" +( rowToTarg_.get(entry.getKey())));	      
	  }
	  lines.add("Ending compare rowToTarg_\n");
	  
	  
	  int masterRowCount_ = mbfn.rowCount_;
	  int rowCount_ = bfn.rowCount_;
	  //assert(rowCount_ == masterRowCount_);
	  lines.add("rowCount_ == masterRowCount_: " + (rowCount_ == masterRowCount_) + "\nrowCount_: " + rowCount_ + ", masterRowCount_: " + masterRowCount_);
	  //
	  // Link and node definitions:
	  //
	  
	  TreeMap masterFullLinkDefs_ = mbfn.fullLinkDefs_;
	  TreeMap<Integer, LinkInfo> fullLinkDefs_ = bfn.fullLinkDefs_;
	  //assert(masterFullLinkDefs_.size() == fullLinkDefs_.size());
	  lines.add("masterFullLinkDefs_.size() == fullLinkDefs_.size(): " + (masterFullLinkDefs_.size() == fullLinkDefs_.size()) + "\nmasterFullLinkDefs_.size(): " + masterFullLinkDefs_.size() + ", fullLinkDefs_.size(): " + fullLinkDefs_.size());
	  /*for (Map.Entry<Integer, LinkInfo> entry : fullLinkDefs_.entrySet())
	  {		  
	      if(!((MasterBioFabricNetwork.LinkInfo)(masterFullLinkDefs_.get(entry.getKey()))).getSource().equals( fullLinkDefs_.get(entry.getKey()).getSource())){
	        lines.add("(MasterBioFabricNetwork.LinkInfo)masterRowToTarg_.get(entry.getKey())).getSource().equals( entry.getValue().getSource(): " + ((MasterBioFabricNetwork.LinkInfo)(masterFullLinkDefs_.get(entry.getKey()))).getSource().equals( fullLinkDefs_.get(entry.getKey()).getSource()));
	        lines.add(((MasterBioFabricNetwork.LinkInfo)(masterFullLinkDefs_.get(entry.getKey()))).getSource() + " " +  fullLinkDefs_.get(entry.getKey()).getSource());
	      }
	      
	      if(!((MasterBioFabricNetwork.LinkInfo)(masterFullLinkDefs_.get(entry.getKey()))).getTarget().equals( entry.getValue().getTarget())){
	    	  lines.add("(MasterBioFabricNetwork.LinkInfo)masterRowToTarg_.get(entry.getKey())).getTarget().equals( entry.getValue().getTarget(): " + ((MasterBioFabricNetwork.LinkInfo)(masterFullLinkDefs_.get(entry.getKey()))).getTarget().equals( fullLinkDefs_.get(entry.getKey()).getTarget()));
	    	  lines.add(((MasterBioFabricNetwork.LinkInfo)(masterFullLinkDefs_.get(entry.getKey()))).getTarget() + " " +  fullLinkDefs_.get(entry.getKey()).getTarget());
	      }
	      
	      if(((MasterBioFabricNetwork.LinkInfo)(masterFullLinkDefs_.get(entry.getKey()))).getStartRow() != fullLinkDefs_.get(entry.getKey()).getStartRow()){
	    	  lines.add("((MasterBioFabricNetwork.LinkInfo)(masterFullLinkDefs_.get(entry.getKey()))).getStartRow() != entry.getValue().getStartRow(): " + (((MasterBioFabricNetwork.LinkInfo)(masterFullLinkDefs_.get(entry.getKey()))).getStartRow() != fullLinkDefs_.get(entry.getKey()).getStartRow()));
	    	  lines.add(((MasterBioFabricNetwork.LinkInfo)(masterFullLinkDefs_.get(entry.getKey()))).getStartRow() + " " + fullLinkDefs_.get(entry.getKey()).getStartRow());
	      }
	      
	      if(((MasterBioFabricNetwork.LinkInfo)(masterFullLinkDefs_.get(entry.getKey()))).getEndRow() != fullLinkDefs_.get(entry.getKey()).getEndRow()){
	    	  lines.add("((MasterBioFabricNetwork.LinkInfo)masterRowToTarg_.get(entry.getKey())).getEndRow() == entry.getValue().getEndRow(): " + (((MasterBioFabricNetwork.LinkInfo)(masterFullLinkDefs_.get(entry.getKey()))).getEndRow() == fullLinkDefs_.get(entry.getKey()).getEndRow()));
	    	  lines.add(((MasterBioFabricNetwork.LinkInfo)(masterFullLinkDefs_.get(entry.getKey()))).getTarget() + " " +  fullLinkDefs_.get(entry.getKey()).getEndRow());
	      }
	  }*/
	  
	  
	  TreeMap masterNonShadowedLinkMap_ = mbfn.nonShadowedLinkMap_;
	  TreeMap<Integer, Integer> nonShadowedLinkMap_ = bfn.nonShadowedLinkMap_;
	  //assert(masterNonShadowedLinkMap_.size() == nonShadowedLinkMap_.size());
	  lines.add("masterNonShadowedLinkMap_.size() == nonShadowedLinkMap_.size(): " + (masterNonShadowedLinkMap_.size() == nonShadowedLinkMap_.size()) + "\nmasterNonShadowedLinkMap_.size(): " + masterNonShadowedLinkMap_.size() + ", nonShadowedLinkMap_.size(): " + nonShadowedLinkMap_.size());	  
	  
	  HashMap masterNodeDefs_ = mbfn.nodeDefs_;	  	 
	  HashMap<String, NodeInfo> nodeDefs_ = bfn.nodeDefs_;
	  //assert(masterNodeDefs_.size() == nodeDefs_.size());
	  lines.add("masterNodeDefs_.size() == nodeDefs_.size(): " + (masterNodeDefs_.size() == nodeDefs_.size()) + "\nmasterNodeDefs_.size(): " + masterNodeDefs_.size() + "nodeDefs_.size(): " + nodeDefs_.size());
	  /*for (Map.Entry<String, NodeInfo> entry : nodeDefs_.entrySet())
	  {		  
	      assert(((NodeInfo)masterNodeDefs_.get(entry.getKey())).nodeName.equals( entry.getValue().nodeName));
	      assert(((NodeInfo)masterNodeDefs_.get(entry.getKey())).nodeRow == entry.getValue().nodeRow);	      
	  }*/
	  
	  
	  List masterLinkGrouping_ = mbfn.linkGrouping_;
	  List<String> linkGrouping_ = bfn.linkGrouping_;
	  //assert(masterLinkGrouping_.size() == linkGrouping_.size());
	  lines.add("masterLinkGrouping_.size() == linkGrouping_.size(): " + (masterLinkGrouping_.size() == linkGrouping_.size()) + "\nmasterLinkGrouping_.size(): " + masterLinkGrouping_.size() + ", linkGrouping_.size(): " + linkGrouping_.size());
	  /*for(int i = 0; i < linkGrouping_.size(); ++i){
		  assert( linkGrouping_.get(i).equals(masterLinkGrouping_.get(i) ));
	  }*/
	 
	  MasterBioFabricNetwork.ColumnAssign masterNormalCols_ = mbfn.normalCols_;
	  BioFabricNetwork.ColumnAssign normalCols_ = bfn.normalCols_;
	  //assert(masterNormalCols_.columnCount == normalCols_.columnCount);
	  lines.add("masterNormalCols_.columnCount == normalCols_.columnCount: " + (masterNormalCols_.columnCount == normalCols_.columnCount) + "\nmasterNormalCols_.columnCount: " + masterNormalCols_.columnCount  + ", normalCols_.columnCount: " + normalCols_.columnCount);
	  
	  //assert(masterNormalCols_.columnToSource.size() == normalCols_.columnToSource.size());
	  lines.add("masterNormalCols_.columnToSource.size() == normalCols_.columnToSource.size(): " + (masterNormalCols_.columnToSource.size() == normalCols_.columnToSource.size()) + "\nmasterNormalCols_.columnToSource.size(): " + masterNormalCols_.columnToSource.size() + ", normalCols_.columnToSource.size(): " + normalCols_.columnToSource.size());
	  
	  //assert(masterNormalCols_.columnToTarget.size() == normalCols_.columnToTarget.size());
	  lines.add("masterNormalCols_.columnToTarget.size() == normalCols_.columnToTarget.size(): " + (masterNormalCols_.columnToTarget.size() == normalCols_.columnToTarget.size()) + "\nmasterNormalCols_.columnToTarget.size(): " + masterNormalCols_.columnToTarget.size() + ", normalCols_.columnToTarget.size(): " + normalCols_.columnToTarget.size());
	  
	  MasterBioFabricNetwork.ColumnAssign masterShadowCols_ = mbfn.shadowCols_;
	  BioFabricNetwork.ColumnAssign shadowCols_ = bfn.shadowCols_;
	  //assert(masterShadowCols_.columnCount == shadowCols_.columnCount);
	  lines.add("masterShadowCols_.columnCount == shadowCols_.columnCount: " + (masterShadowCols_.columnCount == shadowCols_.columnCount) + "\nmasterShadowCols_.columnCount: " + masterShadowCols_.columnCount + ", shadowCols_.columnCount: " + shadowCols_.columnCount);
	  
	  FabricColorGenerator masterColGen_ = mbfn.colGen_;	  	  	  
	  FabricColorGenerator colGen_ = bfn.colGen_;
	 
	  
	  BuildData bd_ = bfn.bd_; //added to pass to cytoscape renderer
	  
	  
	  Path file = Paths.get("C:\\Users\\Ben\\Desktop\\BioFabric100\\galYeastbioFabricDebugDump.txt");
	  try {
		Files.write(file, lines, Charset.forName("UTF-8"));
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
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