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

package com.boofisher.app.cyBioFabric.internal.biofabric.layouts;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.CyNetworkView;
import com.boofisher.app.cyBioFabric.internal.biofabric.model.BioFabricNetwork;
import com.boofisher.app.cyBioFabric.internal.biofabric.model.FabricLink;
import com.boofisher.app.cyBioFabric.internal.tools.NodeNameSUIDPair;

/****************************************************************************
**
** This is the default layout algorithm
*/

public class DefaultLayout {
  
  ////////////////////////////////////////////////////////////////////////////
  //
  // PRIVATE INSTANCE MEMBERS
  //
  ////////////////////////////////////////////////////////////////////////////

  ////////////////////////////////////////////////////////////////////////////
  //
  // PUBLIC CONSTRUCTORS
  //
  ////////////////////////////////////////////////////////////////////////////

  /***************************************************************************
  **
  ** Constructor
  */

  public DefaultLayout() {

  }

  ////////////////////////////////////////////////////////////////////////////
  //
  // PUBLIC METHODS
  //
  ////////////////////////////////////////////////////////////////////////////
  
  /***************************************************************************
  **
  ** Relayout the network!
  */
  
  public void doLayout(BioFabricNetwork.RelayoutBuildData rbd, NodeSimilarityLayout.CRParams params) {   
    doNodeLayout(rbd, ((Params)params).startNodes);
    (new DefaultEdgeLayout()).layoutEdges(rbd);
    return;
  }
  
  /***************************************************************************
  **
  ** Relayout the network!
  ** startNodes is a list of node suid's
  */
  
  public List<NodeNameSUIDPair> doNodeLayout(BioFabricNetwork.RelayoutBuildData rbd, List<String> startNodes) {
    
	  
	//convert the startNode list into a list of node name and suid pairs  
	ArrayList<NodeNameSUIDPair> pairStartNodes = null;
	
	if(startNodes != null && !startNodes.isEmpty()){
		pairStartNodes = new ArrayList<NodeNameSUIDPair>();  
		for(String nodeName : startNodes){
			long nodeSUID = Long.parseLong(nodeName);
	    	CyNode node = rbd.networkView.getModel().getNode(nodeSUID);     	  
	    	
	        String name = rbd.networkView.getModel().getRow(node).get(CyNetwork.NAME, String.class);
	        pairStartNodes.add(new NodeNameSUIDPair(nodeSUID, name));
		}  
	}
	  
    List<NodeNameSUIDPair> targets = defaultNodeOrder(rbd.networkView, rbd.allLinks, rbd.loneNodes, pairStartNodes);       

    //
    // Now have the ordered list of targets we are going to display.
    // Build target->row maps and the inverse:
    //
    
    installNodeOrder(targets, rbd);
    return (targets);
  }
  
  /***************************************************************************
  **
  ** Install node orders
  */
  
/*
  private void fillNodesFromOrder(List targets, FabricColorGenerator colGen) {
    //
    // Now have the ordered list of targets we are going to display.
    // Build target->row maps and the inverse:
    //
    
    int numColors = colGen.getNumColors();

    int currRow = 0;
    Iterator trit = targets.iterator();
    while (trit.hasNext()) {
      String target = (String)trit.next();
      Integer rowObj = new Integer(currRow);
      rowToTarg_.put(rowObj, target);
      String colorKey = colGen.getGeneColor(currRow % numColors);  
      nodeDefs_.put(target, new NodeInfo(target, currRow++, colorKey));
    }
    rowCount_ = targets.size();
    return;
  }*/
  
  public void installNodeOrder(List<NodeNameSUIDPair> targets, BioFabricNetwork.RelayoutBuildData rbd) {
  
    int currRow = 0;
    HashMap<String, String> nodeOrder = new HashMap<String, String>();
    Iterator<NodeNameSUIDPair> trit = targets.iterator();
    while (trit.hasNext()) {
      String target = trit.next().getName();
      String rowTag = Integer.toString(currRow++);
      nodeOrder.put(target.toUpperCase(), rowTag);
      //System.out.println("ino " + target + " " + rowTag);
    }  
    rbd.setNodeOrder(nodeOrder);
    return;
  }

  /***************************************************************************
  ** 
  ** Calculate default node order
  */

  public List<NodeNameSUIDPair> defaultNodeOrder(CyNetworkView networkView, Set<FabricLink> allLinks, ArrayList<NodeNameSUIDPair> loneNodes, List<NodeNameSUIDPair> startNodes) {    
    //
    // Note the allLinks Set has pruned out duplicates and synonymous non-directional links
    //
    //
    // Build a target list, top to bottom, that adds the node with the most
    // links first, and adds those link targets ASAP. If caller supplies a start node,
    // we go there first:
    // 	  	  
    
	  
	  
    HashMap<NodeNameSUIDPair, Integer> linkCounts = new HashMap<NodeNameSUIDPair, Integer>();
    HashMap<NodeNameSUIDPair, Set<NodeNameSUIDPair>> targsPerSource = new HashMap<NodeNameSUIDPair, Set<NodeNameSUIDPair>>();
    ArrayList<NodeNameSUIDPair> targets = new ArrayList<NodeNameSUIDPair>();
                 
    HashSet<NodeNameSUIDPair> targsToGo = new HashSet<NodeNameSUIDPair>();
    Iterator<FabricLink> alit = allLinks.iterator();
    while (alit.hasNext()) {
      FabricLink nextLink = alit.next();
      NodeNameSUIDPair source = new NodeNameSUIDPair(nextLink.getSrcModelSUID(), nextLink.getSrc());
      NodeNameSUIDPair target = new NodeNameSUIDPair(nextLink.getTargetModelSUID(), nextLink.getTrg());
      Set<NodeNameSUIDPair> targs = targsPerSource.get(source);
      if (targs == null) {
        targs = new HashSet<NodeNameSUIDPair>();
        targsPerSource.put(source, targs);
      }
      targs.add(target);
      targs = targsPerSource.get(target);
      if (targs == null) {
        targs = new HashSet<NodeNameSUIDPair>();
        targsPerSource.put(target, targs);
      }
      targs.add(source);
      targsToGo.add(source);
      targsToGo.add(target);        
      Integer srcCount = linkCounts.get(source);
      linkCounts.put(source, (srcCount == null) ? Integer.valueOf(1) : Integer.valueOf(srcCount.intValue() + 1));
      Integer trgCount = linkCounts.get(target);
      linkCounts.put(target, (trgCount == null) ? Integer.valueOf(1) : Integer.valueOf(trgCount.intValue() + 1));
    }    
    
    //
    // Rank the nodes by link count:
    //
    
    TreeMap<Integer, SortedSet<NodeNameSUIDPair>> countRank = new TreeMap<Integer, SortedSet<NodeNameSUIDPair>>(Collections.reverseOrder());
    Iterator<NodeNameSUIDPair> lcit = linkCounts.keySet().iterator();
    while (lcit.hasNext()) {
    	NodeNameSUIDPair src = lcit.next();
      Integer count = linkCounts.get(src);
      SortedSet<NodeNameSUIDPair> perCount = countRank.get(count);
      if (perCount == null) {
        perCount = new TreeSet<NodeNameSUIDPair>();
        countRank.put(count, perCount);
      }
      perCount.add(src);
    }
          
    
    //
    // Handle the specified starting nodes case:
    // TODO: understand this
    //
    
    if ((startNodes != null) && !startNodes.isEmpty()) {
      ArrayList<NodeNameSUIDPair> queue = new ArrayList<NodeNameSUIDPair>();
      targsToGo.removeAll(startNodes);
      targets.addAll(startNodes);
      queue.addAll(startNodes);
      System.out.println("added all " + startNodes);
      flushQueue(targets, targsPerSource, linkCounts, targsToGo, queue);
    }   
    
    //
    // Get all kids added in.  Now doing this without recursion; seeing blown
    // stacks for huge networks!
    //
 
    while (!targsToGo.isEmpty()) {
      Iterator<Integer> crit = countRank.keySet().iterator();
      while (crit.hasNext()) {
        Integer key = crit.next();
        SortedSet<NodeNameSUIDPair> perCount = countRank.get(key);
        Iterator<NodeNameSUIDPair> pcit = perCount.iterator();                
        
        while (pcit.hasNext()) {
          NodeNameSUIDPair node = pcit.next();    
          if (targsToGo.contains(node)) {
            ArrayList<NodeNameSUIDPair> queue = new ArrayList<NodeNameSUIDPair>();
            targsToGo.remove(node);
            targets.add(node);
            addMyKidsNR(targets, targsPerSource, linkCounts, targsToGo, node, queue);
          }          
        }        
      }
    }   
    
    //create a list of name suid pairs
/*    ArrayList<NodeNameSUIDPair> targetPairs = new ArrayList<NodeNameSUIDPair>(); 
    Iterator<String> edgeNodesItr = targets.iterator();
    while(edgeNodesItr.hasNext()){
    	long nodeSuid = Long.parseLong(edgeNodesItr.next());
    	CyNode node = networkView.getModel().getNode(nodeSuid); 
    	String name = networkView.getModel().getRow(node).get(CyNetwork.NAME, String.class);
    	targetPairs.add(new NodeNameSUIDPair(nodeSuid, name));
    }        
    */
    //
    //
    // Tag on lone nodes.  If a node is by itself, but also shows up in the links,
    // we drop it:
    //
    loneNodes.removeAll(targets);
    //HashSet<String> remains = new HashSet<String>(loneNodes);
    //remains.removeAll(targets);
    //targets.addAll(new TreeSet<String>(loneNodes));
    
    targets.addAll(loneNodes);
    
    return (targets);
  }
        
  /***************************************************************************
  **
  ** Node ordering
  */
  
  private ArrayList<NodeNameSUIDPair> orderMyKids(Map<NodeNameSUIDPair, Set<NodeNameSUIDPair>> targsPerSource, Map<NodeNameSUIDPair, Integer> linkCounts, 
                                        HashSet<NodeNameSUIDPair> targsToGo, NodeNameSUIDPair node) {
    Set<NodeNameSUIDPair> targs = targsPerSource.get(node);
    if (targs == null) {
    	System.out.println("no kids for " + node);
    	return (new ArrayList<NodeNameSUIDPair>());
    }
    TreeMap<Integer, SortedSet<NodeNameSUIDPair>> kidMap = new TreeMap<Integer, SortedSet<NodeNameSUIDPair>>(Collections.reverseOrder());
    Iterator<NodeNameSUIDPair> tait = targs.iterator();
    while (tait.hasNext()) {  
      NodeNameSUIDPair nextTarg = tait.next(); 
      Integer count = linkCounts.get(nextTarg);
      SortedSet<NodeNameSUIDPair> perCount = kidMap.get(count);
      if (perCount == null) {
        perCount = new TreeSet<NodeNameSUIDPair>();
        kidMap.put(count, perCount);
      }
      perCount.add(nextTarg);
    }
    
    ArrayList<NodeNameSUIDPair> myKidsToProc = new ArrayList<NodeNameSUIDPair>();
    Iterator<SortedSet<NodeNameSUIDPair>> kmit = kidMap.values().iterator();
    while (kmit.hasNext()) {  
      SortedSet<NodeNameSUIDPair> perCount = kmit.next(); 
      Iterator<NodeNameSUIDPair> pcit = perCount.iterator();
      while (pcit.hasNext()) {  
    	NodeNameSUIDPair kid = pcit.next();
        if (targsToGo.contains(kid)) { 
          myKidsToProc.add(kid);
        }
      }
    }
    return (myKidsToProc);
  }    
  
  /***************************************************************************
  **
  ** Node ordering, non-recursive:
  */
  
  private void addMyKidsNR(ArrayList<NodeNameSUIDPair> targets, Map<NodeNameSUIDPair, Set<NodeNameSUIDPair>> targsPerSource, 
                           Map<NodeNameSUIDPair, Integer> linkCounts, 
                           HashSet<NodeNameSUIDPair> targsToGo, NodeNameSUIDPair node, ArrayList<NodeNameSUIDPair> queue) {
    queue.add(node);
    flushQueue(targets, targsPerSource, linkCounts, targsToGo, queue);
    return;
  }
  
  /***************************************************************************
  **
  ** Node ordering, non-recursive:
  */
  
  private void flushQueue(ArrayList<NodeNameSUIDPair> targets, Map<NodeNameSUIDPair, Set<NodeNameSUIDPair>> targsPerSource, 
                           Map<NodeNameSUIDPair, Integer> linkCounts, 
                           HashSet<NodeNameSUIDPair> targsToGo, ArrayList<NodeNameSUIDPair> queue) {
    while (!queue.isEmpty()) {
      NodeNameSUIDPair node = queue.remove(0);
      ArrayList<NodeNameSUIDPair> myKids = orderMyKids(targsPerSource, linkCounts, targsToGo, node);
      Iterator<NodeNameSUIDPair> ktpit = myKids.iterator(); 
      while (ktpit.hasNext()) {  
    	NodeNameSUIDPair kid = ktpit.next();
        if (targsToGo.contains(kid)) {
          targsToGo.remove(kid);
          targets.add(kid);
          queue.add(kid);
        }
      }
    }
    return;
  }

  
  /***************************************************************************
  **
  ** For passing around layout params
  */  
  
  public static class Params implements NodeSimilarityLayout.CRParams {
        
    public List<String> startNodes;  //start nodes is a list of node SUID strings

    public Params(List<String> startNodes) {
      this.startNodes = startNodes;
    } 
  }
}
