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

package org.systemsbiology.cyBioFabric.internal.biofabric.model;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.TreeSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.cytoscape.application.CyUserLog;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.systemsbiology.cyBioFabric.internal.biofabric.analysis.Link;
import org.systemsbiology.cyBioFabric.internal.biofabric.io.AttributeLoader;
import org.systemsbiology.cyBioFabric.internal.biofabric.io.FabricFactory;
import org.systemsbiology.cyBioFabric.internal.biofabric.layouts.DefaultEdgeLayout;
import org.systemsbiology.cyBioFabric.internal.biofabric.layouts.DefaultLayout;
import org.systemsbiology.cyBioFabric.internal.biofabric.model.FabricLink;
import org.systemsbiology.cyBioFabric.internal.biofabric.model.BioFabricNetwork.NodeInfo;
import org.systemsbiology.cyBioFabric.internal.biofabric.parser.AbstractFactoryClient;
import org.systemsbiology.cyBioFabric.internal.biofabric.parser.GlueStick;
import org.systemsbiology.cyBioFabric.internal.biofabric.ui.FabricColorGenerator;
import org.systemsbiology.cyBioFabric.internal.biofabric.ui.FabricDisplayOptions;
import org.systemsbiology.cyBioFabric.internal.biofabric.ui.FabricDisplayOptionsManager;
import org.systemsbiology.cyBioFabric.internal.biofabric.util.AttributeExtractor;
import org.systemsbiology.cyBioFabric.internal.biofabric.util.CharacterEntityMapper;
import org.systemsbiology.cyBioFabric.internal.biofabric.util.DataUtil;
import org.systemsbiology.cyBioFabric.internal.biofabric.util.Indenter;
import org.systemsbiology.cyBioFabric.internal.biofabric.util.MinMax;
import org.systemsbiology.cyBioFabric.internal.biofabric.util.UiUtil;
import org.systemsbiology.cyBioFabric.internal.cytoscape.view.BioFabricVisualLexicon;
import org.systemsbiology.cyBioFabric.internal.tools.NodeNameSUIDPair;
import org.xml.sax.Attributes;

/****************************************************************************
**
** This is the Network model
*/

public class BioFabricNetwork {
	
  ////////////////////////////////////////////////////////////////////////////
  //
  // PRIVATE CONSTANTS
  //
  //////////////////////////////////////////////////////////////////////////// 

  ////////////////////////////////////////////////////////////////////////////
  //
  // PUBLIC CONSTANTS
  //
  //////////////////////////////////////////////////////////////////////////// 
  
  public enum BuildMode {DEFAULT_LAYOUT,
                         REORDER_LAYOUT ,
                         CLUSTERED_LAYOUT ,
                         SHADOW_LINK_CHANGE,
                         LINK_GROUP_CHANGE ,
                         BUILD_FOR_SUBMODEL,
                         BUILD_FROM_XML ,
                         BUILD_FROM_SIF  ,
                         BUILD_FROM_GAGGLE ,
                         NODE_ATTRIB_LAYOUT ,
                         LINK_ATTRIB_LAYOUT,
                         NODE_CLUSTER_LAYOUT,
                         CONTROL_TOP_LAYOUT,
                         HIER_DAG_LAYOUT,
                         WORLD_BANK_LAYOUT
                        };
   
  ////////////////////////////////////////////////////////////////////////////
  //
  // PRIVATE INSTANCE MEMBERS
  //
  ////////////////////////////////////////////////////////////////////////////
   
  //
  // For mapping of selections:
  //
  
  public HashMap<Integer, String> rowToTarg_;
  public int rowCount_;
  
  //
  // Link and node definitions:
  //
  
  public TreeMap<Integer, LinkInfo> fullLinkDefs_;
  public TreeMap<Integer, Integer> nonShadowedLinkMap_;
  public HashMap<String, NodeInfo> nodeDefs_;
  
  //
  // Grouping for links:
  //
  
  public List<String> linkGrouping_;
  
  //
  // Columns assignments, shadow and non-shadow states:
  //
 
  public ColumnAssign normalCols_;
  public ColumnAssign shadowCols_;
  
  public FabricColorGenerator colGen_;
  
  public BuildData bd_; //added to pass to cytoscape renderer
  
  
  //hold a reference to the networkView
  public CyNetworkView networkView;
  
  ////////////////////////////////////////////////////////////////////////////
  //
  // PUBLIC CONSTRUCTORS
  //
  ////////////////////////////////////////////////////////////////////////////

  /***************************************************************************
  **
  ** Constructor
  */

  public BioFabricNetwork(BuildData bd) {
    BuildMode mode = bd.getMode();    
    bd_ = bd;
    switch (mode) {
      case DEFAULT_LAYOUT:  
      case REORDER_LAYOUT:
      case CLUSTERED_LAYOUT:
      case LINK_GROUP_CHANGE:
      case NODE_ATTRIB_LAYOUT:
      case LINK_ATTRIB_LAYOUT:
      case NODE_CLUSTER_LAYOUT:
      case CONTROL_TOP_LAYOUT: 
      case HIER_DAG_LAYOUT:
      case WORLD_BANK_LAYOUT:
        RelayoutBuildData rbd = (RelayoutBuildData)bd;
        normalCols_ = new ColumnAssign();
        shadowCols_ = new ColumnAssign();
        rowToTarg_ = new HashMap<Integer, String>(); 
        fullLinkDefs_ = new TreeMap<Integer, LinkInfo>();
        nonShadowedLinkMap_ = new TreeMap<Integer, Integer>();
        nodeDefs_ = new HashMap<String, NodeInfo>();
        linkGrouping_ = new ArrayList<String>();
        colGen_ = rbd.colGen;
        relayoutNetwork(rbd);
        break;
      case BUILD_FOR_SUBMODEL:
        SelectBuildData sbd = (SelectBuildData)bd;
        colGen_ = sbd.fullNet.colGen_;
        this.linkGrouping_ = new ArrayList<String>(sbd.fullNet.linkGrouping_);
        fillSubModel(sbd.fullNet, sbd.subNodes, sbd.subLinks);
        break;
      case BUILD_FROM_XML:
      case SHADOW_LINK_CHANGE:
        BioFabricNetwork built = ((PreBuiltBuildData)bd).bfn;
        this.normalCols_ = built.normalCols_;
        this.shadowCols_ = built.shadowCols_;
        this.rowToTarg_ = built.rowToTarg_; 
        this.fullLinkDefs_ = built.fullLinkDefs_;
        this.nonShadowedLinkMap_ = built.nonShadowedLinkMap_;
        this.nodeDefs_ = built.nodeDefs_;
        this.colGen_ = built.colGen_;
        this.rowCount_ = built.rowCount_;
        this.linkGrouping_ = built.linkGrouping_;
        break;
      case BUILD_FROM_SIF:
      case BUILD_FROM_GAGGLE:
        RelayoutBuildData obd = (RelayoutBuildData)bd;
        this.networkView = obd.networkView;
        normalCols_ = new ColumnAssign();
        shadowCols_ = new ColumnAssign();
        rowToTarg_ = new HashMap<Integer, String>(); 
        fullLinkDefs_ = new TreeMap<Integer, LinkInfo>();
        nonShadowedLinkMap_ = new TreeMap<Integer, Integer>();
        nodeDefs_ = new HashMap<String, NodeInfo>();
        linkGrouping_ = new ArrayList<String>();
        colGen_ = obd.colGen;
        processLinks(obd);
        break;
      default:
        throw new IllegalArgumentException();
    }
  }

  ////////////////////////////////////////////////////////////////////////////
  //
  // PUBLIC METHODS
  //
  ////////////////////////////////////////////////////////////////////////////

  /***************************************************************************
  ** 
  ** Install link grouping
  */

  public void installLinkGroups(List<String> linkTagList) {    
    linkGrouping_ = new ArrayList<String>(linkTagList);
    return;
  }
  
  /***************************************************************************
  ** 
  ** Get link grouping
  */

  public List<String> getLinkGroups() {    
    return (linkGrouping_);
  }
  
  /***************************************************************************
  ** 
  ** Given an attribute list giving node order, confirm it is valid:
  */

  public boolean checkNewNodeOrder(Map<AttributeLoader.AttributeKey, String> nodeRows) { 
    
    //
    // All existing targets must have a row, and all existing
    // rows need a target assigned!
    //
    
    HashSet<AttributeLoader.AttributeKey> asUpper = new HashSet<AttributeLoader.AttributeKey>();
    Iterator<String> rttvit = rowToTarg_.values().iterator();
    while (rttvit.hasNext()) {
      asUpper.add(new AttributeLoader.StringKey(rttvit.next().toUpperCase()));
    }
    if (!asUpper.equals(new HashSet<AttributeLoader.AttributeKey>(nodeRows.keySet()))) {
      return (false);
    }
    
    TreeSet<Integer> asInts = new TreeSet<Integer>();
    Iterator<String> nrvit = nodeRows.values().iterator();
    while (nrvit.hasNext()) {
      String asStr = nrvit.next();
      try {
        asInts.add(Integer.valueOf(asStr));
      } catch (NumberFormatException nfex) {
        return (false);
      }
    }
    if (!asInts.equals(new TreeSet<Integer>(rowToTarg_.keySet()))) {
      return (false);
    }
    
    return (true);
  }
  
  /***************************************************************************
  ** 
  ** Given an attribute list giving node order, confirm it is valid:
  */

  public boolean checkNodeClusterAssignment(Map<AttributeLoader.AttributeKey, String> nodeClusters) { 
    
    //
    // All nodes must be assigned to a cluster
    //
    
    HashSet<AttributeLoader.AttributeKey> asUpper = new HashSet<AttributeLoader.AttributeKey>();
    Iterator<String> rttvit = rowToTarg_.values().iterator();
    while (rttvit.hasNext()) {
      asUpper.add(new AttributeLoader.StringKey(rttvit.next().toUpperCase()));
    }
    UiUtil.fixMePrintout("Actually do somehitng");
 //   if (!asUpper.equals(new HashSet<AttributeLoader.AttributeKey>(nodeClusters.keySet()))) {
  //    return (false);
  //  }
   
    return (true);
  }
  
  /***************************************************************************
  ** 
  ** Given an attribute list giving link order, confirm it is valid, get back
  ** map to integer values with directed tag filled in!
  **
  ** FIX ME: A few problems need to be addressed.  First, there is no
  ** decent error messaging about what is missing or duplicated.  Second,
  ** we require shadow links to ALWAYS be specified, even if the user does
  ** not care.... 
  */

  public SortedMap<Integer, FabricLink> checkNewLinkOrder(Map<AttributeLoader.AttributeKey, String> linkRows) { 
    
    //
    // Recover the mapping that tells us what link relationships are
    // directed:
    //
    
    HashMap<FabricLink.AugRelation, Boolean> relDir = new HashMap<FabricLink.AugRelation, Boolean>();
    Set<FabricLink> allLinks = getAllLinks(true);
    Iterator<FabricLink> alit = allLinks.iterator();
    while (alit.hasNext()) {
      FabricLink link = alit.next();
      FabricLink.AugRelation rel = link.getAugRelation();
      boolean isDir = link.isDirected();
      Boolean myVal = new Boolean(isDir);
      Boolean currVal = relDir.get(rel);
      if (currVal != null) {
        if (!currVal.equals(myVal)) {
          throw new IllegalStateException();
        }
      } else {
        relDir.put(rel, myVal);
      }
    }
    
    //
    // Create a map that takes column Integer to a correctly
    // directed copy of the Fabric link:
    //
    
    TreeMap<Integer, FabricLink> dirMap = new TreeMap<Integer, FabricLink>();
    Iterator<AttributeLoader.AttributeKey> lrit = linkRows.keySet().iterator();
    while (lrit.hasNext()) {
      FabricLink link = (FabricLink)lrit.next();
      String colNumStr = linkRows.get(link);
      FabricLink dirCopy = link.clone();
      Boolean isDirected = relDir.get(dirCopy.getAugRelation());
      dirCopy.installDirection(isDirected);
      try {
        dirMap.put(Integer.valueOf(colNumStr), dirCopy);
      } catch (NumberFormatException nfex) {
        return (null);
      }
    }
    
    // Ordered set of all our existing links:
    TreeSet<FabricLink> alks = new TreeSet<FabricLink>(allLinks);
    // Ordered set of guys we have been handed:
    TreeSet<FabricLink> dmvs = new TreeSet<FabricLink>(dirMap.values());
    
    // Has to be the case that the link definitions are 1:1 and
    // onto, or we have an error:
    if (!alks.equals(dmvs)) {
      return (null);
    }
  
    //
    // Has to be the case that all columns are also 1:1 and onto:
    //
    
    TreeSet<Integer> ldks = new TreeSet<Integer>(fullLinkDefs_.keySet());
    TreeSet<Integer> dmks = new TreeSet<Integer>(dirMap.keySet());
    
    if (!ldks.equals(dmks)) {
      return (null);
    }
    
    return (dirMap);
  }
  
  /***************************************************************************
  **
  ** Get all links
  */
  
  public Set<FabricLink> getAllLinks(boolean withShadows) {  
    HashSet<FabricLink> allLinks = new HashSet<FabricLink>();
    Iterator<Integer> ldit = fullLinkDefs_.keySet().iterator();
    while (ldit.hasNext()) {
      Integer col = ldit.next();
      LinkInfo li = getLinkDefinition(col, true);  // just get everybody...
      FabricLink link = li.getLink();
      if (withShadows || !link.isShadow()) {
        allLinks.add(link);
      }
    }
    return (allLinks);
  }
   
  /***************************************************************************
  **
  ** Get ordered linkInfo iterator
  */
  
  public Iterator<Integer> getOrderedLinkInfo(boolean withShadows) {  
    return ((withShadows) ? fullLinkDefs_.keySet().iterator() : nonShadowedLinkMap_.keySet().iterator());
  }
  
  /***************************************************************************
  ** 
  ** Process a link set
  */

  private void processLinks(RelayoutBuildData rbd) { 
    //
    // Note the allLinks Set has pruned out duplicates and synonymous non-directional links
    //
       
    List<NodeNameSUIDPair> targets =  (new DefaultLayout()).doNodeLayout(rbd, null);    
    
    //
    // Now have the ordered list of targets we are going to display.
    //
    
    fillNodesFromOrder(rbd.networkView, targets, rbd.colGen, rbd.clustAssign);

    
    
    //
    // This now assigns the link to its column.  Note that we order them
    // so that the shortest vertical link is drawn first!
    //
    
    (new DefaultEdgeLayout()).layoutEdges(rbd);
    specifiedLinkToColumn(rbd.colGen, rbd.linkOrder, false);

    //
    // Determine the start & end of each target row needed to handle the incoming
    // and outgoing links:
    //
 
    trimTargetRows();
        
    //
    // For the lone nodes, they are assigned into the last column:
    //
    
    loneNodesToLastColumn(rbd.loneNodes);
    return;
  }
  
  /***************************************************************************
  **
  ** Relayout the network!
  */
  
  private void relayoutNetwork(RelayoutBuildData rbd) {
    BuildMode mode = rbd.getMode();
    List<String> linkGroups = (mode == BuildMode.LINK_GROUP_CHANGE) ? rbd.newLinkGroups : rbd.existingLinkGroups;    
    installLinkGroups(linkGroups);
    boolean specifiedNodeOrder = (mode == BuildMode.NODE_ATTRIB_LAYOUT) || 
                                 (mode == BuildMode.DEFAULT_LAYOUT) ||
                                 (mode == BuildMode.CONTROL_TOP_LAYOUT) ||
                                 (mode == BuildMode.HIER_DAG_LAYOUT) ||
                                 (mode == BuildMode.WORLD_BANK_LAYOUT) ||
                                 (mode == BuildMode.NODE_CLUSTER_LAYOUT) || 
                                 (mode == BuildMode.CLUSTERED_LAYOUT) || 
                                 (mode == BuildMode.REORDER_LAYOUT);          
    List<String> targets;
    if (specifiedNodeOrder) {
      targets = specifiedOrder(rbd.allNodes, rbd.nodeOrder);
    } else {       
      targets = rbd.existingOrder;
    }
   
    //
    // Now have the ordered list of targets we are going to display.
    // Build target->row maps and the inverse:
    //
    
    fillNodesFromOrder(targets, rbd.colGen, rbd.clustAssign);

    //
    // Ordering of links:
    //
    
    if ((rbd.linkOrder == null) || rbd.linkOrder.isEmpty() || (mode == BuildMode.LINK_GROUP_CHANGE)) {
      if ((rbd.nodeOrder == null) || rbd.nodeOrder.isEmpty()) {
        rbd.nodeOrder = new HashMap<String, String>();
        int numT = targets.size();
        for (int i = 0; i < numT; i++) {
          String targName = targets.get(i);
          rbd.nodeOrder.put(targName.toUpperCase(), Integer.toString(i));
        }
      }
      (new DefaultEdgeLayout()).layoutEdges(rbd);
    }

    //
    // This now assigns the link to its column, based on user specification
    //
    
    specifiedLinkToColumn(rbd.colGen, rbd.linkOrder, ((mode == BuildMode.LINK_ATTRIB_LAYOUT) || (mode == BuildMode.NODE_CLUSTER_LAYOUT)));
      
    //
    // Determine the start & end of each target row needed to handle the incoming
    // and outgoing links:
    //
 
    trimTargetRows();
        
    //
    // For the lone nodes, they are assigned into the last column:
    //

    loneNodesToLastColumn(rbd.loneNodes);
    return;
  }
  
  /***************************************************************************
  **
  ** Get specified node order list from attribute map
  */
  
  private List<String> specifiedOrder(Set<String> allNodes, Map<String, String> newOrder) { 
   
    TreeMap<Integer, String> forRetval = new TreeMap<Integer, String>();
    Iterator<String> rttvit = allNodes.iterator();
    while (rttvit.hasNext()) {
      String key = rttvit.next();
      String asUpperKey = key.toUpperCase();
      String valAsStr = newOrder.get(asUpperKey);
      try {
        Integer newRow = Integer.valueOf(valAsStr);
        forRetval.put(newRow, key);
      } catch (NumberFormatException nfex) {
        System.err.println("Bad Number: " + valAsStr + " >" + asUpperKey + "<");
        throw new IllegalStateException();
      }
    }
    return (new ArrayList<String>(forRetval.values()));
  }
 
  /***************************************************************************
  **
  ** Get existing order
  */
  
  public List<String> existingOrder() {  
    ArrayList<String> retval = new ArrayList<String>();
    Iterator<Integer> rtit = new TreeSet<Integer>(rowToTarg_.keySet()).iterator();
    while (rtit.hasNext()) {
      Integer row = rtit.next();
      String node = rowToTarg_.get(row);
      retval.add(node);
    }
    return (retval);
  }
  
  /***************************************************************************
  **
  ** Get existing link order
  */
  
  public SortedMap<Integer, FabricLink> getExistingLinkOrder() {  
    TreeMap<Integer, FabricLink> retval = new TreeMap<Integer, FabricLink>();
    Iterator<Integer> ldit = fullLinkDefs_.keySet().iterator();
    while (ldit.hasNext()) {
      Integer col = ldit.next();
      LinkInfo li = fullLinkDefs_.get(col);
      FabricLink link = li.getLink();
      retval.put(col, link);
    }
    return (retval);
  }
  
  /***************************************************************************
  **
  ** Get existing order
  */
  
  public Iterator<Integer> getRows() {
    return (rowToTarg_.keySet().iterator());
  }

  /***************************************************************************
  ** 
  ** Process a link set
  */

  private void specifiedLinkToColumn(FabricColorGenerator colGen, SortedMap<Integer, FabricLink> linkOrder, boolean userSpec) { 
     
    normalCols_.columnCount = 0;
    shadowCols_.columnCount = 0;
    int numColors = colGen.getNumColors();
    Iterator<Integer> frkit = linkOrder.keySet().iterator();
    while (frkit.hasNext()) {
      Integer nextCol = frkit.next();
      FabricLink nextLink = linkOrder.get(nextCol);
      Integer useForSDef = Integer.valueOf(shadowCols_.columnCount);
      Integer[] colCounts = addLinkDef(nextLink, numColors, normalCols_.columnCount, shadowCols_.columnCount, colGen);
      shadowCols_.columnCount = colCounts[0].intValue();
      if (colCounts[1] != null) {
        normalCols_.columnCount = colCounts[1].intValue();
      }
      
      LinkInfo linf = getLinkDefinition(useForSDef, true);
      String topNodeName = rowToTarg_.get(Integer.valueOf(linf.topRow()));
      String botNodeName = rowToTarg_.get(Integer.valueOf(linf.bottomRow()));
      
      NodeInfo nit = nodeDefs_.get(topNodeName);
      NodeInfo nib = nodeDefs_.get(botNodeName);
      
      //
      // When displaying shadows, drain zone of top node is only mod 
      // for non-shadow link values, and drain zone of bottom node is 
      // only mod with shadow link 
      //
      // For non-shadow display, top node drain is mod with non-shadow values
      //
      // Stated another way, non-shadows always affect top node drains.  Shadow
      // links only affect bottom node shadow drains:
      //

      if (!userSpec) {
        if (!linf.isShadow()) {       
          MinMax dzst = nit.getDrainZone(true);     
          if (dzst == null) {
            dzst = (new MinMax()).init();
            nit.setDrainZone(dzst, true);
          }
          dzst.update(linf.getUseColumn(true));

          MinMax dznt = nit.getDrainZone(false);     
          if (dznt == null) {
            dznt = (new MinMax()).init();
            nit.setDrainZone(dznt, false);
          }
          dznt.update(linf.getUseColumn(false));              
        } else {
          MinMax dzsb = nib.getDrainZone(true);
          if (dzsb == null) {
            dzsb = (new MinMax()).init();
            nib.setDrainZone(dzsb, true);
          }
          dzsb.update(linf.getUseColumn(true));
        }
      }
    }
    
    if (userSpec) {
      setDrainZonesByContig(true);     
      setDrainZonesByContig(false);    
    }
            
    return;
  }
  
  /***************************************************************************
  ** 
  ** Helper
  */

  private void updateContigs(String nodeName, HashMap<String, SortedMap<Integer, MinMax>> runsPerNode, 
                             Integer lastCol, Integer col) {
    int colVal = col.intValue();
    SortedMap<Integer, MinMax> runs = runsPerNode.get(nodeName);
    if (runs == null) {
      runs = new TreeMap<Integer, MinMax>();
      runsPerNode.put(nodeName, runs);        
    }
    MinMax currRun = runs.get(lastCol);
    if (currRun == null) {
      currRun = new MinMax(colVal);
      runs.put(col, currRun);        
    } else {
      currRun.update(colVal);
      runs.remove(lastCol);
      runs.put(col, currRun);
    }
    return;
  }
 
  /***************************************************************************
  ** 
  ** When user specified link order, things could get totally wild.  Set drain zones
  ** by leftmost largest contiguous set of links.
  */

  private void runsToDrain(HashMap<String, SortedMap<Integer, MinMax>> runsPerNode, boolean forShadow) {  
    Iterator<String> rpnit = runsPerNode.keySet().iterator();
    while (rpnit.hasNext()) {  
      String nodeName = rpnit.next();      
      SortedMap<Integer, MinMax> runs = runsPerNode.get(nodeName);
      MinMax maxRun = null;
      int maxSize = Integer.MIN_VALUE;
      Iterator<MinMax> rit = runs.values().iterator();
      while (rit.hasNext()) {  
        MinMax aRun = rit.next();
        int runLen = aRun.max - aRun.min + 1;
        if (runLen > maxSize) {
          maxSize = runLen;
          maxRun = aRun;
        } else if ((runLen == 1) && (maxSize == 1)) {
          maxRun = aRun;  // move this to the end if no contig run
        }
      }
      if (maxRun != null) {
        NodeInfo nit = nodeDefs_.get(nodeName);
        nit.setDrainZone(maxRun.clone(), forShadow);
      }
    }
    return;
  }
  
  /***************************************************************************
  ** 
  ** When user specified link order, things could get totally wild.  Set drain zones
  ** by leftmost largest contiguous set of links.
  */

  private void setDrainZonesByContig(boolean withShadows) {
    
    HashMap<String, SortedMap<Integer, MinMax>> runsPerNode = new HashMap<String, SortedMap<Integer, MinMax>>();
         
    Iterator<Integer> olit = getOrderedLinkInfo(withShadows);
    Integer lastCol = Integer.valueOf(-1);
    while (olit.hasNext()) {
      Integer col = olit.next();
      LinkInfo linf = getLinkDefinition(col, withShadows);
      String topNodeName = rowToTarg_.get(Integer.valueOf(linf.topRow()));
      updateContigs(topNodeName, runsPerNode, lastCol, col);
      if (withShadows && linf.isShadow()) {
        String botNodeName = rowToTarg_.get(Integer.valueOf(linf.bottomRow()));     
        updateContigs(botNodeName, runsPerNode, lastCol, col);
      }
      lastCol = col;
    } 
    runsToDrain(runsPerNode, withShadows);
    return;
  }
   
  /***************************************************************************
  **
  ** Dump the network using XML
  */
  
  public void writeXML(PrintWriter out, Indenter ind) {    
    ind.indent();
    out.println("<BioFabric>");
    ind.up();
    
    colGen_.writeXML(out, ind);
    
    //
    // Display options:
    //
    
    FabricDisplayOptionsManager.getMgr().writeXML(out, ind);
       
    //
    // Dump the nodes, then the links:
    //  
    
    Iterator<Integer> r2tit = (new TreeSet<Integer>(rowToTarg_.keySet())).iterator();
    ind.indent();
    out.println("<nodes>");
    ind.up();
    while (r2tit.hasNext()) {
      Integer row = r2tit.next();
      String targ = rowToTarg_.get(row);
      NodeInfo ni = getNodeDefinition(targ);
      ni.writeXML(out, ind, row.intValue(), targ);
    }
    ind.down().indent();
    out.println("</nodes>");
  
    if (!linkGrouping_.isEmpty()) {
      Iterator<String> lgit = linkGrouping_.iterator();
      ind.indent();
      out.println("<linkGroups>");
      ind.up();
      while (lgit.hasNext()) {
        String grpTag = lgit.next();
        ind.indent();
        out.print("<linkGroup tag=\"");
        out.print(grpTag);
        out.println("\" />");
      }
      ind.down().indent();
      out.println("</linkGroups>");
    }
  
    HashMap<Integer, Integer> inverse = new HashMap<Integer, Integer>();
    Iterator<Integer> nsit = nonShadowedLinkMap_.keySet().iterator();
    while (nsit.hasNext()) {
      Integer key = nsit.next();
      inverse.put(nonShadowedLinkMap_.get(key), key);
    }
    
    
    Iterator<Integer> ldit = fullLinkDefs_.keySet().iterator();
    ind.indent();
    out.println("<links>");
    ind.up();
    while (ldit.hasNext()) {
      Integer col = ldit.next();
      LinkInfo li = getLinkDefinition(col, true);
      FabricLink link = li.getLink();
      ind.indent();
      out.print("<link src=\"");
      out.print(CharacterEntityMapper.mapEntities(link.getSrc(), false));
      out.print("\" trg=\"");
      out.print(CharacterEntityMapper.mapEntities(link.getTrg(), false));
      out.print("\" rel=\"");
      FabricLink.AugRelation augr = link.getAugRelation();
      out.print(CharacterEntityMapper.mapEntities(augr.relation, false));
      out.print("\" directed=\"");
      out.print(link.isDirected());
      out.print("\" shadow=\"");
      out.print(augr.isShadow);
      if (!augr.isShadow) {
        Integer nsCol = inverse.get(col); 
        out.print("\" column=\"");
        out.print(nsCol);
      }
      out.print("\" shadowCol=\"");
      out.print(col);     
      out.print("\" srcRow=\"");
      out.print(li.getStartRow());
      out.print("\" trgRow=\"");
      out.print(li.getEndRow());
      out.print("\" color=\"");
      out.print(li.getColorKey());
      out.println("\" />");
    }
    ind.down().indent();
    out.println("</links>");
    ind.down().indent();
    out.println("</BioFabric>");
    return;
  }
  
  /***************************************************************************
  **
  ** Dump a node attribute file with row assignments:
  */
  
  public void writeNOA(PrintWriter out) {    
    out.println("Node Row");
    Iterator<Integer> r2tit = new TreeSet<Integer>(rowToTarg_.keySet()).iterator();
    while (r2tit.hasNext()) {
      Integer row = r2tit.next();
      String targ = rowToTarg_.get(row);
      out.print(targ);
      out.print(" = ");
      out.println(row);
    } 
    return;
  }
  
  /***************************************************************************
  **
  ** Dump an edge attribute file with column assignments:
  */
  
  public void writeEDA(PrintWriter out) {    
    out.println("Link Column");
    Iterator<Integer> ldit = fullLinkDefs_.keySet().iterator();
    while (ldit.hasNext()) {
      Integer col = ldit.next();
      LinkInfo li = getLinkDefinition(col, true);
      FabricLink link = li.getLink();
      out.print(link.toEOAString());
      out.print(" = ");
      out.println(col);
    }
    return;
  }
    
  /***************************************************************************
  ** 
  ** Get Node Definition
  */

  public NodeInfo getNodeDefinition(String targ) {
    NodeInfo node = nodeDefs_.get(targ);
    return (node);
  }
  
  /***************************************************************************
  ** 
  ** Link definition for column.  If no link in column (may happen for selected networks),
  ** returns null
  */

  public LinkInfo getLinkDefinition(Integer colObj, boolean forShadow) {
    if (forShadow) {
      return (fullLinkDefs_.get(colObj));
    } else {
      Integer mapped = nonShadowedLinkMap_.get(colObj);
      if (mapped != null) {
        return (fullLinkDefs_.get(mapped));
      } else {
        return (null);
      }     
    }
  }
  
  /***************************************************************************
  ** 
  ** Get Target For Column
  */

  public String getTargetForColumn(Integer colVal, boolean forShadow) {
    ColumnAssign useCA = (forShadow) ? shadowCols_ : normalCols_;
    String target = useCA.columnToTarget.get(colVal);
    return (target);
  }
  
  /***************************************************************************
  ** 
  ** Get Drain zone For Column
  */

  public String getDrainForColumn(Integer colVal, boolean forShadow) {
    int col = colVal.intValue();
    ColumnAssign useCA = (forShadow) ? shadowCols_ : normalCols_;
    String target = useCA.columnToTarget.get(colVal);
    String source = useCA.columnToSource.get(colVal);
    if (target != null) {
      NodeInfo nit = nodeDefs_.get(target);
      MinMax tdz = nit.getDrainZone(forShadow);
      if (tdz != null) {
        if ((col >= tdz.min) && (col <= tdz.max)) {
          return (target);
        }
      }
    }
    if (source != null) {
      NodeInfo nis = nodeDefs_.get(source);
      MinMax sdz = nis.getDrainZone(forShadow);
      if (sdz != null) {
        if ((col >= sdz.min) && (col <= sdz.max)) {
          return (source);
        }
      }
    }
    return (null);
  }
 
  /***************************************************************************
  ** 
  ** Get Source For Column
  */

  public String getSourceForColumn(Integer colVal, boolean forShadow) {
    ColumnAssign useCA = (forShadow) ? shadowCols_ : normalCols_;
    String source = useCA.columnToSource.get(colVal);
    return (source);
  }
  
  /***************************************************************************
  ** 
  ** Get node for row
  */

  public String getNodeForRow(Integer rowObj) {
    String node = rowToTarg_.get(rowObj);
    return (node);
  }
  
  /***************************************************************************
  ** 
  ** Get link count
  */

  public int getLinkCount(boolean forShadow) {
    return ((forShadow) ? fullLinkDefs_.size() : nonShadowedLinkMap_.size());
  } 
  
  /***************************************************************************
  ** 
  ** Get column count
  */

  public int getColumnCount(boolean forShadow) {
    ColumnAssign useCA = (forShadow) ? shadowCols_ : normalCols_;
    return (useCA.columnCount);
  } 
  
  /***************************************************************************
  ** 
  ** Get Row Count
  */

  public int getRowCount() {
    return (rowCount_);
  } 
   
  /***************************************************************************
  ** 
  ** Get node defs
  */

  public List<NodeInfo> getNodeDefList() {
    return (new ArrayList<NodeInfo>(nodeDefs_.values()));
  } 
  
  /***************************************************************************
  ** 
  ** Get all node names
  */

  public Set<String> getNodeSet() {
    return (new HashSet<String>(nodeDefs_.keySet()));
  }  
  
  /***************************************************************************
  ** 
  ** Get link defs
  */

  public List<LinkInfo> getLinkDefList(boolean forShadow) {
    if (forShadow) {
      return (new ArrayList<LinkInfo>(fullLinkDefs_.values()));
    } else {
      ArrayList<LinkInfo> retval = new ArrayList<LinkInfo>();
      Iterator<Integer> nsit = nonShadowedLinkMap_.keySet().iterator();
      while (nsit.hasNext()) {
        Integer linkID = nsit.next();
        Integer mappedID = nonShadowedLinkMap_.get(linkID);
        retval.add(fullLinkDefs_.get(mappedID));
      }
      return (retval);
    }
  } 
  
  /***************************************************************************
  ** 
  ** Get node matches
  */

  public Set<String> nodeMatches(boolean fullMatch, String searchString) {
    HashSet<String> retval = new HashSet<String>();
    Iterator<String> nkit = nodeDefs_.keySet().iterator();
    while (nkit.hasNext()) {
      String nodeName = nkit.next();
      if (matches(searchString, nodeName, fullMatch)) {
        retval.add(nodeName);
      }
    }    
    return (retval);
  }
  
  /***************************************************************************
  ** 
  ** Get first neighbors of node, along with info blocks
  */

  public void getFirstNeighbors(String nodeName, Set<String> nodeSet, List<NodeInfo> nodes, List<LinkInfo> links) {
    Iterator<LinkInfo> ldit = fullLinkDefs_.values().iterator();
    while (ldit.hasNext()) {
      LinkInfo linf = ldit.next();
      if (linf.getSource().equals(nodeName)) {
        nodeSet.add(linf.getTarget());
        links.add(linf);
      } else if (linf.getTarget().equals(nodeName)) {
        nodeSet.add(linf.getSource());
        links.add(linf);
      }
    }
    Iterator<String> nsit = nodeSet.iterator();
    while (nsit.hasNext()) {
      String name = nsit.next();
      nodes.add(nodeDefs_.get(name));
    }
    return;
  }
  
  /***************************************************************************
  ** 
  ** Get first neighbors of node
  */

  public Set<String> getFirstNeighbors(String nodeName) {
    HashSet<String> nodeSet = new HashSet<String>();
    Iterator<LinkInfo> ldit = fullLinkDefs_.values().iterator();
    while (ldit.hasNext()) {
      LinkInfo linf = ldit.next();
      if (linf.getSource().equals(nodeName)) {
        nodeSet.add(linf.getTarget());
      } else if (linf.getTarget().equals(nodeName)) {
        nodeSet.add(linf.getSource());
      }
    }
    return (nodeSet);
  }
   
  /***************************************************************************
  ** 
  ** Add first neighbors of node set
  */

  public void addFirstNeighbors(Set<String> nodeSet, Set<Integer> columnSet, Set<FabricLink> linkSet, boolean forShadow) {
    HashSet<String> newNodes = new HashSet<String>();
    Iterator<LinkInfo> ldit = fullLinkDefs_.values().iterator();
    while (ldit.hasNext()) {
      LinkInfo linf = ldit.next();
      if (!forShadow && linf.isShadow()) {
        continue;
      }
      if (nodeSet.contains(linf.getSource())) {
        newNodes.add(linf.getTarget());
        columnSet.add(Integer.valueOf(linf.getUseColumn(forShadow)));
        linkSet.add(linf.getLink());
      }
      if (nodeSet.contains(linf.getTarget())) {
        newNodes.add(linf.getSource());
        columnSet.add(Integer.valueOf(linf.getUseColumn(forShadow)));
        linkSet.add(linf.getLink());
      }
    }
    nodeSet.addAll(newNodes);
    
    for(String node : nodeSet){//TODO added selected nodes here make sure this is correct
		NodeInfo nodeInfo = getNodeDefinition(node);		
		
		if(networkView != null){
			long nodeSUID = nodeInfo.getNodeSUID();
			CyNode cyNode = networkView.getModel().getNode(nodeSUID); 
	    	  
	    	Boolean isSelected = networkView.getModel().getRow(cyNode).get(CyNetwork.SELECTED, Boolean.class);
	    	
	    	if(!isSelected){
	    		//set node row table value
		    	networkView.getModel().getRow(cyNode).set(CyNetwork.SELECTED, true);      	
		    	//Set view model
		    	View<CyNode> view = networkView.getNodeView(cyNode);
		    	view.setVisualProperty(BioFabricVisualLexicon.NODE_SELECTED, true);
	    	}
		}
    }
		
	for(FabricLink edge : linkSet){//TODO added selected edges here make sure this is correct
		
		if(networkView != null){
			long edgeSUID = edge.getEdgeModelSUID();
			CyEdge cyEdge = networkView.getModel().getEdge(edgeSUID); 
	    	  
	    	Boolean isSelected = networkView.getModel().getRow(cyEdge).get(CyNetwork.SELECTED, Boolean.class);
	    	
	    	if(!isSelected){
	    		//set node row table value
		    	networkView.getModel().getRow(cyEdge).set(CyNetwork.SELECTED, true);      	
		    	//Set view model
		    	View<CyEdge> view = networkView.getEdgeView(cyEdge);
		    	view.setVisualProperty(BioFabricVisualLexicon.NODE_SELECTED, true);
	    	}
		}
	}
    
    return;
  }

  /***************************************************************************
  **
  ** Do the match
  ** 
  */
 
  private boolean matches(String searchString, String nodeName, boolean isFull) {
    String canonicalNodeName = DataUtil.normKey(nodeName);
    if (isFull) {
      return (canonicalNodeName.equals(searchString));
    } else {
      return (canonicalNodeName.indexOf(searchString) != -1);
    }
  }

  
  /***************************************************************************
  **
  ** Count of links per targ:
  */

  private Map<String, Integer> linkCountPerTarg(Set<String> targets, List<LinkInfo> linkList) {
    HashMap<String, Integer> retval = new HashMap<String, Integer>();
    Iterator<LinkInfo> lcit = linkList.iterator();
    while (lcit.hasNext()) {
      LinkInfo linf = lcit.next();
      String src = linf.getSource();
      String trg = linf.getTarget();      
      if (targets.contains(src)) {
        Integer count = retval.get(src);
        if (count == null) {
          retval.put(src, Integer.valueOf(1));
        } else {
          retval.put(src, Integer.valueOf(count.intValue() + 1));
        }
      }
      if (!src.equals(trg) && targets.contains(trg)) {
        Integer count = retval.get(trg);
        if (count == null) {
          retval.put(trg, Integer.valueOf(1));
        } else {
          retval.put(trg, Integer.valueOf(count.intValue() + 1));
        }
      }
    }
    return (retval);
  }
   
  /***************************************************************************
  **
  ** When creating submodel and shadow links are being displayed, we can choose
  ** to only show one copy of a link: either the main link, or the shadow link, unless
  ** both endpoint drains are on display, then both will be shown.
  */

  private List<LinkInfo> pruneToMinSubModel(BioFabricNetwork bfn, List<NodeInfo> targetList, List<LinkInfo> linkList) {
          
    HashSet<String> targSet = new HashSet<String>();
    Iterator<NodeInfo> tlit = targetList.iterator();
    while (tlit.hasNext()) {
      NodeInfo ninf = tlit.next();
      targSet.add(ninf.nodeName);
    }   

    Map<String, Integer> subCounts = linkCountPerTarg(targSet, linkList);
    Map<String, Integer> fullCounts = linkCountPerTarg(bfn.getNodeSet(), bfn.getLinkDefList(true));
    
    HashSet<Integer> skipThem = new HashSet<Integer>();
    HashSet<Integer> ditchThem = new HashSet<Integer>();
    Iterator<LinkInfo> lcit = linkList.iterator();
    while (lcit.hasNext()) {
      LinkInfo linf = lcit.next();
      String topNode = bfn.getNodeForRow(Integer.valueOf(linf.topRow()));
      String botNode = bfn.getNodeForRow(Integer.valueOf(linf.bottomRow()));
      boolean topStays = subCounts.get(topNode).equals(fullCounts.get(topNode));
      boolean botStays = subCounts.get(botNode).equals(fullCounts.get(botNode));
      if ((topStays && botStays) || (!topStays && !botStays)) {  // Nobody gets ditched!
        continue;
      }
      FabricLink link1 = linf.getLink();
      int col1 = linf.getUseColumn(true);
      skipThem.add(Integer.valueOf(col1));
      Iterator<LinkInfo> lc2it = linkList.iterator();
      while (lc2it.hasNext()) {
        LinkInfo linf2 = lc2it.next();
        int col2 = linf2.getUseColumn(true);
        if (skipThem.contains(Integer.valueOf(col2))) {
          continue;
        }
        if (linf2.getLink().shadowPair(link1)) { // got a shadow pair! 
          int maxLink = Math.max(col1, col2);
          int minLink = Math.min(col1, col2);
          ditchThem.add(Integer.valueOf((topStays) ? maxLink : minLink));
          break;
        }
      }
    } 
    
    ArrayList<LinkInfo> retval = new ArrayList<LinkInfo>();
    Iterator<LinkInfo> lcit3 = linkList.iterator();
    while (lcit3.hasNext()) {
      LinkInfo linf = lcit3.next();
      int col = linf.getUseColumn(true);
      if (!ditchThem.contains(Integer.valueOf(col))) {
        retval.add(linf);
      }
    }
    return (retval);
  }

  /***************************************************************************
  **
  ** Fill out model with submodel data
  ** TODO: counting on this being used in a new biofabric network and not the main network
  */

  private void fillSubModel(BioFabricNetwork bfn, List<NodeInfo> targetList, List<LinkInfo> linkList) {
    
    boolean doPrune = FabricDisplayOptionsManager.getMgr().getDisplayOptions().getMinShadowSubmodelLinks();
    if (doPrune) {
      linkList = pruneToMinSubModel(bfn, targetList, linkList);
    }
      
    HashMap<String, Integer> lastColForNode = new HashMap<String, Integer>();
    HashMap<String, Integer> lastShadColForNode = new HashMap<String, Integer>();
    Iterator<LinkInfo> lcit = linkList.iterator();
    while (lcit.hasNext()) {
      LinkInfo linf = lcit.next();
      if (!linf.isShadow()) {
        Integer lastCol = lastColForNode.get(linf.getTarget());
        if ((lastCol == null) || (lastCol.intValue() < linf.getUseColumn(false))) {
          lastColForNode.put(linf.getTarget(), Integer.valueOf(linf.getUseColumn(false)));
        }
        lastCol = lastColForNode.get(linf.getSource());
        if ((lastCol == null) || (lastCol.intValue() < linf.getUseColumn(false))) {
          lastColForNode.put(linf.getSource(), Integer.valueOf(linf.getUseColumn(false)));
        }
      }
      Integer lastColShad = lastShadColForNode.get(linf.getTarget());
      if ((lastColShad == null) || (lastColShad.intValue() < linf.getUseColumn(true))) {
        lastShadColForNode.put(linf.getTarget(), Integer.valueOf(linf.getUseColumn(true)));
      }
      lastColShad = lastShadColForNode.get(linf.getSource());
      if ((lastColShad == null) || (lastColShad.intValue() < linf.getUseColumn(true))) {
        lastShadColForNode.put(linf.getSource(), Integer.valueOf(linf.getUseColumn(true)));
      }
    }

    //
    // Need to compress the rows and columns, throwing away all empty slots
    // First record the "full scale" entries:
    //

    TreeSet<Integer> needRows = new TreeSet<Integer>();
    TreeSet<Integer> needColumns = new TreeSet<Integer>();   
    TreeSet<Integer> needColumnsShad = new TreeSet<Integer>();   

    Iterator<NodeInfo> tgit = targetList.iterator();
    while (tgit.hasNext()) {
      NodeInfo targetInf = tgit.next();
      needRows.add(Integer.valueOf(targetInf.nodeRow));
      needColumns.add(Integer.valueOf(targetInf.getColRange(false).min));
      needColumnsShad.add(Integer.valueOf(targetInf.getColRange(true).min));
    }

    Iterator<LinkInfo> cit = linkList.iterator();
    while (cit.hasNext()) {
      LinkInfo linf = cit.next();
      needRows.add(Integer.valueOf(linf.getStartRow()));
      needRows.add(Integer.valueOf(linf.getEndRow()));
      if (!linf.isShadow()) {
        needColumns.add(Integer.valueOf(linf.getUseColumn(false)));
      }
      needColumnsShad.add(Integer.valueOf(linf.getUseColumn(true)));
    }

    //
    // Create full-scale to mini-scale mappings:
    //

    TreeMap<Integer, Integer> rowMap = new TreeMap<Integer, Integer>();
    TreeMap<Integer, Integer> columnMap = new TreeMap<Integer, Integer>();
    TreeMap<Integer, Integer> shadColumnMap = new TreeMap<Integer, Integer>();

    int rowCount = 0;
    Iterator<Integer> mrit = needRows.iterator();
    while (mrit.hasNext()) {
      Integer fullRow = mrit.next();
      rowMap.put(fullRow, Integer.valueOf(rowCount++));
    }

    int colCount = 0;
    Iterator<Integer> mcit = needColumns.iterator();
    while (mcit.hasNext()) {
      Integer fullCol = mcit.next();
      columnMap.put(fullCol, Integer.valueOf(colCount++));
    }
    
    int shadColCount = 0;
    Iterator<Integer> ncsit = needColumnsShad.iterator();
    while (ncsit.hasNext()) {
      Integer fullCol = ncsit.next();
      shadColumnMap.put(fullCol, Integer.valueOf(shadColCount++));
    }
  
    //
    // Create modified copies of the node and link info with
    // compressed rows and columns:
    //

    ArrayList<NodeInfo> modTargetList = new ArrayList<NodeInfo>();
    ArrayList<LinkInfo> modLinkList = new ArrayList<LinkInfo>();

    int maxShadLinkCol = Integer.MIN_VALUE;
    int maxLinkCol = Integer.MIN_VALUE;
    cit = linkList.iterator();
    while (cit.hasNext()) {
      BioFabricNetwork.LinkInfo linf = cit.next();
      Integer startRowObj = rowMap.get(Integer.valueOf(linf.getStartRow()));
      Integer endRowObj = rowMap.get(Integer.valueOf(linf.getEndRow()));
      Integer miniColObj = (linf.isShadow()) ? Integer.valueOf(Integer.MIN_VALUE) : columnMap.get(Integer.valueOf(linf.getUseColumn(false)));
      Integer miniColShadObj = shadColumnMap.get(Integer.valueOf(linf.getUseColumn(true)));
      int miniColVal = miniColObj.intValue();
      if (miniColVal > maxLinkCol) {
        maxLinkCol = miniColVal;
      }
      int miniColShadVal = miniColShadObj.intValue();
      if (miniColShadVal > maxShadLinkCol) {
        maxShadLinkCol = miniColShadVal;
      }
      
      LinkInfo miniLinf = new LinkInfo(linf.getLink(), startRowObj.intValue(), endRowObj.intValue(), 
                                       miniColObj.intValue(), miniColShadObj.intValue(), linf.getColorKey());
      modLinkList.add(miniLinf);
    }
    if (maxLinkCol == Integer.MIN_VALUE) {
      maxLinkCol = 1;
    } else {
      maxLinkCol++;
    }
    if (maxShadLinkCol == Integer.MIN_VALUE) {
      maxShadLinkCol = 1;
    } else {
      maxShadLinkCol++;
    }
 
    int minTrgCol = Integer.MAX_VALUE;
    int minShadTrgCol = Integer.MAX_VALUE;
    tgit = targetList.iterator();
    while (tgit.hasNext()) {
      NodeInfo infoFull = tgit.next();
      Integer miniRowObj = rowMap.get(Integer.valueOf(infoFull.nodeRow));
      NodeInfo infoMini = new NodeInfo(infoFull.nodeName, miniRowObj.intValue(), infoFull.colorKey);

      Integer minCol = columnMap.get(Integer.valueOf(infoFull.getColRange(false).min));
      infoMini.updateMinMaxCol(minCol.intValue(), false);
      int miniColVal = minCol.intValue();
      if (miniColVal < minTrgCol) {
        minTrgCol = miniColVal;
      }
      
      Integer minShadCol = shadColumnMap.get(Integer.valueOf(infoFull.getColRange(true).min));
      infoMini.updateMinMaxCol(minShadCol.intValue(), true);
      int miniShadColVal = minShadCol.intValue();
      if (miniShadColVal < minShadTrgCol) {
        minShadTrgCol = miniShadColVal;
      }
   
      int maxCol;        
      Integer lastCol = lastColForNode.get(infoFull.nodeName);
      if (lastCol == null) {
        maxCol = maxLinkCol;
      } else {
        Integer maxColObj = columnMap.get(lastCol);     
        maxCol = maxColObj.intValue();
      }
      infoMini.updateMinMaxCol(maxCol, false);
      
      int maxShadCol;        
      Integer lastShadCol = lastShadColForNode.get(infoFull.nodeName);
      if (lastShadCol == null) {
        maxShadCol = maxShadLinkCol;
      } else {
        Integer maxShadColObj = shadColumnMap.get(lastShadCol);     
        maxShadCol = maxShadColObj.intValue();
      }
      infoMini.updateMinMaxCol(maxShadCol, true);
  
      modTargetList.add(infoMini);
    }
    if (minTrgCol == Integer.MAX_VALUE) {
      minTrgCol = 0;
    }
    
    rowToTarg_ = new HashMap<Integer, String>();
    nodeDefs_ = new HashMap<String, NodeInfo>();
    rowCount_ = modTargetList.size();
    for (int i = 0; i < rowCount_; i++) {
      NodeInfo infoMini = modTargetList.get(i);
      rowToTarg_.put(Integer.valueOf(infoMini.nodeRow), infoMini.nodeName);
      nodeDefs_.put(infoMini.nodeName, infoMini);
    }
    
    normalCols_ = new ColumnAssign();
    shadowCols_ = new ColumnAssign();
    fullLinkDefs_ = new TreeMap<Integer, LinkInfo>();
    nonShadowedLinkMap_ = new TreeMap<Integer, Integer>();
    
    int numMll = modLinkList.size();
    for (int i = 0; i < numMll; i++) {
      BioFabricNetwork.LinkInfo infoMini = modLinkList.get(i);
      
      Integer useShadCol = Integer.valueOf(infoMini.getUseColumn(true));
      fullLinkDefs_.put(useShadCol, infoMini);      
      shadowCols_.columnToSource.put(useShadCol, infoMini.getSource());   
      shadowCols_.columnToTarget.put(useShadCol, infoMini.getTarget());
           
      if (!infoMini.isShadow()) {
        Integer useCol = Integer.valueOf(infoMini.getUseColumn(false));
        nonShadowedLinkMap_.put(useCol, useShadCol);      
        normalCols_.columnToSource.put(useCol, infoMini.getSource());   
        normalCols_.columnToTarget.put(useCol, infoMini.getTarget());
      }
    }
    
    normalCols_.columnCount = maxLinkCol - minTrgCol;
    shadowCols_.columnCount = maxShadLinkCol - minShadTrgCol;
    
    //
    // Need to build drain zones.  For each node, start at max col and back up.
    // Drain starts at max IF the source is the top node.  Continues until
    // we stop having nodes in the next slot that are the top nodes.
    //
    // We only keep a drain zone when it is at the top of the display.  We can
    // check this by seeing if the last link for the node has the node at the top
    // (keep drain) or the bottom (drop drain) of the link.
    //
    // That logic only applies with non-shadow presentation.  For shadowed, everybody 
    // with a link has a drain zone
    //
    
    Iterator<String> ndkit = nodeDefs_.keySet().iterator();
    while (ndkit.hasNext()) {
      String node = ndkit.next();
      NodeInfo srcNI = nodeDefs_.get(node);
      
      //
      // Non-shadow calcs:
      //
      MinMax srcDrain = null;
      MinMax range = srcNI.getColRange(false);
      int startCol = range.max;
      int endCol = range.min;
      for (int i = startCol; i >= endCol; i--) {
        LinkInfo linf = getLinkDefinition(Integer.valueOf(i), false);
        // done when no longer contiguous:
        if ((linf == null) || (!linf.getSource().equals(node) && !linf.getTarget().equals(node))) {
          break;
        }
        // Second case allows feedback:
        if ((linf.bottomRow() == srcNI.nodeRow) && (linf.topRow() != srcNI.nodeRow)) {
          break;
        }
        if (linf.topRow() == srcNI.nodeRow) {
          if (srcDrain == null) {
            srcDrain = new MinMax();
            srcDrain.init();
          }
          srcDrain.update(i); 
        }        
      }
      if (srcDrain != null) {
        srcNI.setDrainZone(srcDrain, false);
      }
      
      //
      // Shadow calcs:
      //
      
      MinMax shadowSrcDrain = null;
      MinMax shadowRange = srcNI.getColRange(true);
      int startColShad = shadowRange.min;
      int endColShad = shadowRange.max;
      for (int i = startColShad; i <= endColShad; i++) {
        LinkInfo linf = getLinkDefinition(Integer.valueOf(i), true);
        if (linf == null) {
          continue;
        }
        boolean isShadow = ((linf != null) && linf.isShadow());
   //     if (!isShadow && (shadowSrcDrain == null)) {
    //      continue;
    //    }
        // done when no longer contiguous:
        if (shadowSrcDrain != null) {
          if ((linf == null) || (!linf.getSource().equals(node) && !linf.getTarget().equals(node))) {
            break;
          }
        }
        if (((linf.topRow() == srcNI.nodeRow) && !isShadow) || 
             (linf.bottomRow() == srcNI.nodeRow) && isShadow) {
          if (shadowSrcDrain == null) {
            shadowSrcDrain = new MinMax();
            shadowSrcDrain.init();
          }
          shadowSrcDrain.update(i); 
        }        
      }
      if (shadowSrcDrain != null) {
        srcNI.setDrainZone(shadowSrcDrain, true);
      }
    }
 
    return;
  }

  
  /***************************************************************************
   ** 
   ** Fill out node info from order
   ** Overloaded
   */

   private void fillNodesFromOrder(List<String> targets, FabricColorGenerator colGen, Map<String, String> clustAssign) {
     //
     // Now have the ordered list of targets we are going to display.
     // Build target->row maps and the inverse:
     //
     
     int numColors = colGen.getNumColors();

     int currRow = 0;
     Iterator<String> trit = targets.iterator();
     while (trit.hasNext()) {
       String target = trit.next();
       Integer rowObj = Integer.valueOf(currRow);
       rowToTarg_.put(rowObj, target);
       String colorKey = colGen.getGeneColor(currRow % numColors);
       NodeInfo nextNI = new NodeInfo(target, currRow++, colorKey);
       if (clustAssign != null) {
       	nextNI.setCluster(clustAssign.get(target.toUpperCase()));
       }
       nodeDefs_.put(target, nextNI);
     }
     rowCount_ = targets.size();
     return;
   }

  /***************************************************************************
  ** 
  ** Fill out node info from order
  ** TODO: Check if broken
  *
  */

  private void fillNodesFromOrder(CyNetworkView networkView, List<NodeNameSUIDPair> targets, FabricColorGenerator colGen, Map<String, String> clustAssign) {
    //
    // Now have the ordered list of targets we are going to display.
    // Build target->row maps and the inverse:
    //
    
    int numColors = colGen.getNumColors();

    int currRow = 0;
    Iterator<NodeNameSUIDPair> trit = targets.iterator();
    while (trit.hasNext()) {
      NodeNameSUIDPair pair = trit.next();
      String target = pair.getSUID().toString();
      String name = pair.getName();
      Integer rowObj = Integer.valueOf(currRow);
      rowToTarg_.put(rowObj, name);//add name
      String colorKey = colGen.getGeneColor(currRow % numColors);
      NodeInfo nextNI = new NodeInfo(networkView, target, currRow++, colorKey);
      if (clustAssign != null) {
      	nextNI.setCluster(clustAssign.get(name.toUpperCase()));
      }
      nodeDefs_.put(name, nextNI);
    }
    rowCount_ = targets.size();
    return;
  }
  
  /***************************************************************************
  **
  ** Add a node def
  */
  
  void addNodeInfoForIO(NodeInfo nif) {
    nodeDefs_.put(nif.nodeName, nif);
    rowCount_ = nodeDefs_.size();   
    rowToTarg_.put(Integer.valueOf(nif.nodeRow), nif.nodeName);
    return;
  }
  
  /***************************************************************************
  ** 
  ** Determine the start & end of each target row needed to handle the incoming
  ** and outgoing links:
  */

  private void trimTargetRows() {
    Iterator<Integer> fldit = fullLinkDefs_.keySet().iterator();
    while (fldit.hasNext()) {
      Integer colNum = fldit.next();
      LinkInfo li = fullLinkDefs_.get(colNum);
      shadowCols_.columnToSource.put(colNum, li.getSource());
      shadowCols_.columnToTarget.put(colNum, li.getTarget());
      NodeInfo srcNI = nodeDefs_.get(li.getSource());    
      NodeInfo trgNI = nodeDefs_.get(li.getTarget());    
      srcNI.updateMinMaxCol(colNum.intValue(), true);
      trgNI.updateMinMaxCol(colNum.intValue(), true);
    }
    Iterator<Integer> nslit = nonShadowedLinkMap_.keySet().iterator();
    while (nslit.hasNext()) {
      Integer colNum = nslit.next();
      Integer mappedCol = nonShadowedLinkMap_.get(colNum);
      LinkInfo li = fullLinkDefs_.get(mappedCol);
      normalCols_.columnToSource.put(colNum, li.getSource());
      normalCols_.columnToTarget.put(colNum, li.getTarget());
      NodeInfo srcNI = nodeDefs_.get(li.getSource());    
      NodeInfo trgNI = nodeDefs_.get(li.getTarget());    
      srcNI.updateMinMaxCol(colNum.intValue(), false);
      trgNI.updateMinMaxCol(colNum.intValue(), false);
    }
    return;
  }
  
  /***************************************************************************
  ** 
  ** For the lone nodes, they are assigned into the last column:
  */

  private void loneNodesToLastColumn(ArrayList<NodeNameSUIDPair> loneNodes) {
    Iterator<NodeNameSUIDPair> lnit = loneNodes.iterator();
    while (lnit.hasNext()) {
      NodeNameSUIDPair lone = lnit.next();
      NodeInfo loneNI = nodeDefs_.get(lone.getName());     
      loneNI.updateMinMaxCol(normalCols_.columnCount - 1, false);
      loneNI.updateMinMaxCol(shadowCols_.columnCount - 1, true);
    }    
    return;
  }
  
  /***************************************************************************
  ** 
  ** Pretty icky hack:
  */

  private ArrayList<NodeNameSUIDPair> getLoneNodes() {
	ArrayList<NodeNameSUIDPair> retval = new ArrayList<NodeNameSUIDPair>();
    Iterator<String> lnit = nodeDefs_.keySet().iterator();
    boolean checkDone = false;
    while (lnit.hasNext()) {
      String lone = lnit.next();
      NodeInfo loneNI = nodeDefs_.get(lone);
      int min = loneNI.getColRange(true).min;
      int max = loneNI.getColRange(true).max;
      
      if ((min == max) && (min == (shadowCols_.columnCount - 1))) {
        if (!checkDone) {
          if (shadowCols_.columnToSource.get(Integer.valueOf(min)) != null) {
            return (retval);
          } else {
            checkDone = true;
          }
        }
        retval.add(new NodeNameSUIDPair(loneNI.nodeSUID, loneNI.nodeName));
      }    
    }    
    return (retval);
  }
 
 
 
  /***************************************************************************
  **
  ** Add a link def
  */
  
  private Integer[] addLinkDef(FabricLink nextLink, int numColors, int noShadowCol, int shadowCol, FabricColorGenerator colGen) {
    Integer[] retval = new Integer[2]; 
    String key = colGen.getGeneColor(shadowCol % numColors);
    int srcRow = nodeDefs_.get(nextLink.getSrc()).nodeRow;
    int trgRow = nodeDefs_.get(nextLink.getTrg()).nodeRow;
    LinkInfo linf = new LinkInfo(nextLink, srcRow, trgRow, noShadowCol, shadowCol, key);
    Integer shadowKey = Integer.valueOf(shadowCol);
    fullLinkDefs_.put(shadowKey, linf);
    retval[0] = Integer.valueOf(shadowCol + 1); 
    if (!linf.isShadow()) {
      nonShadowedLinkMap_.put(Integer.valueOf(noShadowCol), shadowKey);
      retval[1] = Integer.valueOf(noShadowCol + 1); 
    }
    return (retval);
  }
  
  /***************************************************************************
  **
  ** Add a link def
  */
  
  void addLinkInfoForIO(LinkInfo linf) {
    int useColVal = linf.getUseColumn(true);
    Integer useCol = Integer.valueOf(useColVal);
    fullLinkDefs_.put(useCol, linf);    
    if (useColVal > shadowCols_.columnCount) {
      shadowCols_.columnCount = useColVal;
    }
    shadowCols_.columnToSource.put(useCol, linf.getSource());   
    shadowCols_.columnToTarget.put(useCol, linf.getTarget());
    
    if (!linf.isShadow()) {
      int useNColVal = linf.getUseColumn(false);
      Integer useNCol = Integer.valueOf(useNColVal);
      nonShadowedLinkMap_.put(useNCol, useCol);    
      if (useNColVal > normalCols_.columnCount) {
        normalCols_.columnCount = useNColVal;
      }
      normalCols_.columnToSource.put(useNCol, linf.getSource());   
      normalCols_.columnToTarget.put(useNCol, linf.getTarget());
    }
    return;
  }
  
  /***************************************************************************
  **
  ** Add a link group tag
  */
  
  void addLinkGroupForIO(String tag) {
    this.linkGrouping_.add(tag);
    return;
  }
 
  /***************************************************************************
  **
  ** Set color generator (I/0)
  */
  
  void setColorGenerator(FabricColorGenerator fcg) {
    colGen_ = fcg;
    return;
  } 
  
  /***************************************************************************
  **
  ** Answer if we have node cluster assignments
  */
  
  public boolean nodeClustersAssigned() {
  	if (nodeDefs_.isEmpty()) {
  		return (false);
  	}
    NodeInfo ni = nodeDefs_.values().iterator().next();
    return (ni.getCluster() != null);
  } 
  
  /***************************************************************************
  **
  ** Return node cluster assignment
  */
  
  public Map<String, String> nodeClusterAssigment() {
  	HashMap<String, String> retval = new HashMap<String, String>();
  	if (nodeDefs_.isEmpty()) {
  		return (retval);
  	}
    for (NodeInfo ni : nodeDefs_.values()) {
    	String cluster = ni.getCluster();
    	if (cluster == null) {
    		throw new IllegalStateException();
    	}
    	retval.put(ni.nodeName.toUpperCase(), cluster);
    }
    return (retval);    
  } 
  
  /***************************************************************************
  **
  ** Set node cluster assignment
  */
  
  public void setNodeClusterAssigment(Map<String, String> assign) {
  	if (nodeDefs_.isEmpty()) {
  		throw new IllegalStateException();
  	}
    for (NodeInfo ni : nodeDefs_.values()) {
    	String cluster = assign.get(ni.nodeName.toUpperCase());
    	if (cluster == null) {
    		throw new IllegalArgumentException();
    	}
    	ni.setCluster(cluster);
    }
    return;    
  } 
  

  
  /***************************************************************************
  **
  ** Constructor for I/O only
  */

  BioFabricNetwork() { 
    normalCols_ = new ColumnAssign();
    shadowCols_ = new ColumnAssign();
    rowToTarg_ = new HashMap<Integer, String>(); 
    fullLinkDefs_ = new TreeMap<Integer, LinkInfo>();
    nonShadowedLinkMap_ = new TreeMap<Integer, Integer>();
    nodeDefs_ = new HashMap<String, NodeInfo>();
    linkGrouping_ = new ArrayList<String>();
    colGen_ = null;
  }
  
  /***************************************************************************
  **
  ** Info for a link
  */  
  
  public static class LinkInfo {
    private FabricLink myLink_;
    private int startRow_;
    private int endRow_;
    private int noShadowColumn_;
    private int shadowColumn_;
    private String colorKey_;
    private CyNetworkView networkView_;//TODO don't need to store reference to network if bfn stores the reference
    private long edgeModelSUID_;
    
    public LinkInfo(FabricLink flink, int startRow, int endRow, int noShadowColumn, int shadowColumn, String colorKey) {
      myLink_ = flink.clone();
      startRow_ = startRow;
      endRow_ = endRow;
      noShadowColumn_ = noShadowColumn;
      shadowColumn_ = shadowColumn;
      colorKey_ = colorKey;
    }
    
    public LinkInfo(CyNetworkView networkView, FabricLink flink, int startRow, int endRow, int noShadowColumn, int shadowColumn, String colorKey) {
        this.networkView_ = networkView;
        myLink_ = flink.clone();
        startRow_ = startRow;
        endRow_ = endRow;
        noShadowColumn_ = noShadowColumn;
        shadowColumn_ = shadowColumn;
        colorKey_ = colorKey;
        edgeModelSUID_ = flink.getEdgeModelSUID();
        
        if(this.networkView_ != null){    	  
      	  CyEdge edge = networkView_.getModel().getEdge(edgeModelSUID_);     	  
        
      	  View<CyEdge> view = networkView.getEdgeView(edge);
      	  view.setVisualProperty(BioFabricVisualLexicon.LINK_INFO_START_ROW, startRow);
      	  view.setVisualProperty(BioFabricVisualLexicon.LINK_INFO_END_ROW, endRow);
      	  view.setVisualProperty(BioFabricVisualLexicon.LINK_INFO_SHADOW_COL_NUMBER, shadowColumn_);
      	  view.setVisualProperty(BioFabricVisualLexicon.LINK_INFO_COL_NUMBER, noShadowColumn_);
      	  view.setVisualProperty(BioFabricVisualLexicon.LINK_INFO_COLOR_KEY, colorKey_);
      	  
      	  view.setVisualProperty(BioFabricVisualLexicon.FABRIC_LINK_DIRECTED, myLink_.isDirected());
      	  view.setVisualProperty(BioFabricVisualLexicon.FABRIC_LINK_IS_SHADOW, myLink_.isShadow());
      	  view.setVisualProperty(BioFabricVisualLexicon.FABRIC_LINK_IS_FEED_BACK, myLink_.isFeedback());
        }
    }
    
    public int getStartRow() {
      return (startRow_);
    }  
    
    public int getEndRow() {
      return (endRow_);
    }  
    
    public int getUseColumn(boolean shadowEnabled) {
      if (!shadowEnabled && myLink_.isShadow()) {
        // FIX ME: Seen this case with submodel creation?
        throw new IllegalStateException();
      }
      return ((shadowEnabled) ? shadowColumn_ : noShadowColumn_);
    }  
    
    public String getColorKey() {
      return (colorKey_);
    }  
       
    public FabricLink getLink() {
      return (myLink_);
    }    
    
    public String getSource() {
      return (myLink_.getSrc());
    }
    
    public String getTarget() {
      return (myLink_.getTrg());
    }
    
    public FabricLink.AugRelation getAugRelation() {
      return (myLink_.getAugRelation());
    }
    
    public boolean isShadow() {
      return (myLink_.isShadow());
    }
    
    public boolean isDirected() {
      return (myLink_.isDirected());
    }
        
    public int bottomRow() {
      return (Math.max(startRow_, endRow_));
    } 
    
    public int topRow() {
      return (Math.min(startRow_, endRow_));
    } 
    
    public boolean inLinkRowRange(int row) {
      return ((row >= topRow()) && (row <= bottomRow()));
    } 
  }  
  
  /***************************************************************************
  **
  ** Node info
  */  
  
  public static class NodeInfo {
	  final Logger logger = Logger.getLogger(CyUserLog.NAME);
	  
    public String nodeName;
    public int nodeRow;
    public String colorKey;
    private String cluster_;
    private long nodeSUID;
    private CyNetworkView networkView;//TODO don't need to store reference to network if bfn stores the reference
        
    private MinMax colRangeSha_;
    private MinMax colRangePln_;
    private MinMax plainDrainZone_;
    private MinMax shadowDrainZone_;
    
    NodeInfo(String nodeName, int nodeRow, String colorKey) {
      this.nodeName = nodeName;
      this.nodeRow = nodeRow;
      this.colorKey = colorKey;
      colRangeSha_ = new MinMax();
      colRangeSha_.init();
      colRangePln_ = new MinMax();
      colRangePln_.init();
      plainDrainZone_ = null;
      shadowDrainZone_ = null;
      cluster_ = null;
    }
    
    NodeInfo(CyNetworkView networkView, String nodeName, int nodeRow, String colorKey) {
        this.networkView = networkView;

        this.nodeRow = nodeRow;
        this.colorKey = colorKey;
        colRangeSha_ = new MinMax();
        colRangeSha_.init();
        colRangePln_ = new MinMax();
        colRangePln_.init();
        plainDrainZone_ = null;
        shadowDrainZone_ = null;
        cluster_ = null;
        
        if(this.networkView != null){
      	  nodeSUID = Long.parseLong(nodeName);
      	  CyNode node = networkView.getModel().getNode(nodeSUID); 
      	  
      	  this.nodeName = networkView.getModel().getRow(node).get(CyNetwork.NAME, String.class);
      	  
      	  View<CyNode> view = networkView.getNodeView(node);
      	  view.setVisualProperty(BioFabricVisualLexicon.NODE_NAME, this.nodeName);
      	  view.setVisualProperty(BioFabricVisualLexicon.NODE_COLOR_KEY, colorKey);
      	  view.setVisualProperty(BioFabricVisualLexicon.NODE_ROW, nodeRow);
      	  view.setVisualProperty(BioFabricVisualLexicon.COL_RANGE_HIGH, colRangePln_.max);
      	  view.setVisualProperty(BioFabricVisualLexicon.COL_RANGE_LOW, colRangePln_.min);
      	  view.setVisualProperty(BioFabricVisualLexicon.SHADOW_COL_RANGE_HIGH, colRangeSha_.max);
      	  view.setVisualProperty(BioFabricVisualLexicon.SHADOW_COL_RANGE_LOW, colRangeSha_.min);    	  
        }else{        	
      	  this.nodeName = nodeName;
        }
      }
    
    //TODO Added these two methods to allow NodeInfo objects to access and update table property values
    public long getNodeSUID(){ return nodeSUID; }
    public CyNetworkView getNetworkView(){ return networkView; }
    
    public MinMax getDrainZone(boolean forShadow) { 
      return (forShadow) ? shadowDrainZone_ : plainDrainZone_;
    }
    
    public void setDrainZone(MinMax zone, boolean forShadow) { 
      

        View<CyNode> view = null;
        if(this.networkView != null){  	    
    	    CyNode node = networkView.getModel().getNode(nodeSUID);   	    
    	    view = networkView.getNodeView(node);
  	  }
      	
        if (forShadow) {
          shadowDrainZone_ = zone;
          
          if(view != null){
            view.setVisualProperty(BioFabricVisualLexicon.SHADOW_DRAIN_ZONE_HIGH, shadowDrainZone_.max);
    	      view.setVisualProperty(BioFabricVisualLexicon.SHADOW_DRAIN_ZONE_LOW, shadowDrainZone_.min);
          }
        } else {    	  
          plainDrainZone_ = zone;
          
          if(view != null){
            view.setVisualProperty(BioFabricVisualLexicon.PLAIN_DRAIN_ZONE_HIGH, plainDrainZone_.max);
    	      view.setVisualProperty(BioFabricVisualLexicon.PLAIN_DRAIN_ZONE_LOW, plainDrainZone_.min);
          }
        }
        return;
    }
  
    public MinMax getColRange(boolean forShadow) { 
      return (forShadow) ? colRangeSha_ : colRangePln_;
    }
      
    void updateMinMaxCol(int i, boolean forShadow) {
    	View<CyNode> view = null;
        if(this.networkView != null){  	    
  	    CyNode node = networkView.getModel().getNode(nodeSUID);   	    
  	    view = networkView.getNodeView(node);
    	  }
        if (forShadow){    	      	  
      	  colRangeSha_.update(i);
      	  if(view != null){
      		  view.setVisualProperty(BioFabricVisualLexicon.SHADOW_COL_RANGE_HIGH, colRangeSha_.max);
          	  view.setVisualProperty(BioFabricVisualLexicon.SHADOW_COL_RANGE_LOW, colRangeSha_.min);
            }
        } else{
      	  colRangePln_.update(i);
      	  if(view != null){
      		  view.setVisualProperty(BioFabricVisualLexicon.COL_RANGE_HIGH, colRangePln_.max);
          	  view.setVisualProperty(BioFabricVisualLexicon.COL_RANGE_LOW, colRangePln_.min);
            }
        }
        return;
    }
    
    public void setCluster(String cluster) {
      cluster_ = cluster;
      return;
    }
    
    public String getCluster() {
      return (cluster_);
    }
    
    /***************************************************************************
    **
    ** Dump the node using XML
    */
  
	  public void writeXML(PrintWriter out, Indenter ind, int row, String targ) {
	    ind.indent();
	    out.print("<node name=\"");
	    out.print(CharacterEntityMapper.mapEntities(targ, false));
	    out.print("\" row=\"");
	    out.print(row);
	    MinMax nsCols = getColRange(false);
	    out.print("\" minCol=\"");
	    out.print(nsCols.min);
	    out.print("\" maxCol=\"");
	    out.print(nsCols.max);
	    MinMax sCols = getColRange(true);
	    out.print("\" minColSha=\"");
	    out.print(sCols.min);
	    out.print("\" maxColSha=\"");
	    out.print(sCols.max);
	    MinMax nDrain = getDrainZone(false);
	    if (nDrain != null) {
	      out.print("\" drainMin=\"");
	      out.print(nDrain.min);
	      out.print("\" drainMax=\"");
	      out.print(nDrain.max);
	    }
	    MinMax sDrain = getDrainZone(true);
	    if (sDrain != null) {
	      out.print("\" drainMinSha=\"");
	      out.print(sDrain.min);
	      out.print("\" drainMaxSha=\"");
	      out.print(sDrain.max);
	    }
	    out.print("\" color=\"");
	    out.print(colorKey);
	    String clust = getCluster();
	    if (clust != null) {
	      out.print("\" cluster=\"");
	      out.print(CharacterEntityMapper.mapEntities(clust, false));
	    }
	    out.println("\" />");
	  }
  } 
  
  /***************************************************************************
  **
  ** For passing around build data
  */  
  
  public static abstract class BuildData {
    protected BuildMode mode;
    
    public BuildData(BuildMode mode) {
      this.mode = mode;
    }
       
    public BuildMode getMode() {
      return (mode);
    }  
    
    public boolean canRestore() {
      return (true);
    }  
  }

  /***************************************************************************
  **
  ** For passing around build data
  */  
  
  public static class PreBuiltBuildData extends BuildData {
    BioFabricNetwork bfn;
  
    public PreBuiltBuildData(BioFabricNetwork bfn, BuildMode mode) {
      super(mode);
      this.bfn = bfn;
    } 
  }
  
  /***************************************************************************
  **
  ** For passing around build data
  */  
  
  public static class RelayoutBuildData extends BuildData {
	public CyNetworkView networkView;
    public BioFabricNetwork bfn;
    public Set<FabricLink> allLinks;
    public ArrayList<NodeNameSUIDPair> loneNodes;
    public FabricColorGenerator colGen;
    public Map<String, String> nodeOrder;
    public List<String> existingOrder;
    public SortedMap<Integer, FabricLink> linkOrder;
    public List<String> existingLinkGroups;
    public List<String> newLinkGroups;
    public Set<String> allNodes;
    public Map<String, String> clustAssign;
    
    public RelayoutBuildData(BioFabricNetwork fullNet, BuildMode mode) {
      super(mode);
      this.bfn = fullNet;
      this.allLinks = fullNet.getAllLinks(true);
      this.colGen = fullNet.colGen_;
      this.nodeOrder = null;
      this.existingOrder = fullNet.existingOrder();
      this.linkOrder = null;
      this.existingLinkGroups = fullNet.linkGrouping_;
      this.loneNodes = fullNet.getLoneNodes();
      this.allNodes = fullNet.nodeDefs_.keySet();
      this.clustAssign = (fullNet.nodeClustersAssigned()) ? fullNet.nodeClusterAssigment() : null;
    }
    
    public RelayoutBuildData(CyNetworkView networkView, Set<FabricLink> allLinks, ArrayList<NodeNameSUIDPair> loneNodes, FabricColorGenerator colGen, BuildMode mode) {
        super(mode);
        this.bfn = null;
        this.allLinks = allLinks;
        this.colGen = colGen;
        this.nodeOrder = null;
        this.existingOrder = null;
        this.linkOrder = null;
        this.existingLinkGroups = new ArrayList<String>();
        this.loneNodes = loneNodes;
        this.allNodes = null;
        this.networkView = networkView;
      } 
    
    public RelayoutBuildData(HashSet<FabricLink> allLinks, ArrayList<NodeNameSUIDPair> arrayList, FabricColorGenerator colGen, BuildMode mode) {
      super(mode);
      this.bfn = null;
      this.allLinks = allLinks;
      this.colGen = colGen;
      this.nodeOrder = null;
      this.existingOrder = null;
      this.linkOrder = null;
      this.existingLinkGroups = new ArrayList<String>();
      this.loneNodes = arrayList;
      this.allNodes = null;
    } 

	public void setNodeOrderFromAttrib(Map<AttributeLoader.AttributeKey, String> nodeOrder) {
      this.nodeOrder = new HashMap<String, String>();
      for (AttributeLoader.AttributeKey key : nodeOrder.keySet()) {
        this.nodeOrder.put(((AttributeLoader.StringKey)key).key, nodeOrder.get(key));
      }
      return;
    }
    
    public void setNodeOrder(Map<String, String> nodeOrder) {
      this.nodeOrder = nodeOrder;
      return;
    }

    public void setLinkOrder(SortedMap<Integer, FabricLink> linkOrder) {
      this.linkOrder = linkOrder;
      return;
    }
               
    public void setLinkGroups(List<String> linkGroups) {
      this.newLinkGroups = linkGroups;
      return;
    }
  }
 
  /***************************************************************************
  ** TODO understand this
  ** For passing around build data
  */  
  
  public static class SelectBuildData extends BuildData {
     BioFabricNetwork fullNet;
     List<NodeInfo> subNodes;
     List<LinkInfo> subLinks;

    public SelectBuildData(BioFabricNetwork fullNet, List<NodeInfo> subNodes, List<LinkInfo> subLinks) {
      super(BuildMode.BUILD_FOR_SUBMODEL);
      this.fullNet = fullNet;
      this.subNodes = subNodes;
      this.subLinks = subLinks;
    } 
  }
  
  /***************************************************************************
  **
  ** For passing around ranked nodes
  */  
  
  static class DoubleRanked  {
     double rank;
     String id;
     Link byLink;

    DoubleRanked(double rank, String id, Link byLink) {
      this.rank = rank;
      this.id = id;
      this.byLink = byLink;
    } 
  }
  
  /***************************************************************************
  **
  ** For storing column assignments
  */  
  
  public static class ColumnAssign  {
    public HashMap<Integer, String> columnToSource;
    public HashMap<Integer, String> columnToTarget;
    public int columnCount;

    ColumnAssign() {
      this.columnToSource = new HashMap<Integer, String>();
      this.columnToTarget = new HashMap<Integer, String>();
      this.columnCount = 0;
    } 
  }
 
  ////////////////////////////////////////////////////////////////////////////
  //
  // PUBLIC STATIC METHODS
  //
  ////////////////////////////////////////////////////////////////////////////
 
  /***************************************************************************
  ** 
  ** Extract relations
  */
  public static SortedMap extractRelations(List allLinks) {
	    HashSet flipSet = new HashSet();
	    HashSet flipRels = new HashSet();
	    HashSet rels = new HashSet(); 
	    Iterator alit = allLinks.iterator();
	    while (alit.hasNext()) {
	      FabricLink nextLink = (FabricLink)alit.next();
	      FabricLink.AugRelation relation = nextLink.getAugRelation();
	      if (!nextLink.isFeedback()) {  // Autofeedback not flippable
	        FabricLink flipLink = nextLink.flipped();
	        if (flipSet.contains(flipLink)) {
	          flipRels.add(relation);
	        } else {
	          flipSet.add(flipLink);
	        }
	      }
	      rels.add(relation);
	    } 
	    
	    //
	    // We have a hint that something is signed if there are two
	    // separate links running in opposite directions!
	    //
	        
	    TreeMap relMap = new TreeMap();
	    Boolean noDir = new Boolean(false);
	    Boolean haveDir = new Boolean(true);
	    Iterator rit = rels.iterator();
	    while (rit.hasNext()) {
	      FabricLink.AugRelation rel = (FabricLink.AugRelation)rit.next();
	      relMap.put(rel, (flipRels.contains(rel)) ? haveDir : noDir);
	    }    
	    return (relMap);
	  }
/*
  public static SortedMap<FabricLink.AugRelation, Boolean> extractRelations(List<FabricLink> allLinks) {
    HashSet<FabricLink> flipSet = new HashSet<FabricLink>();
    HashSet<FabricLink.AugRelation> flipRels = new HashSet<FabricLink.AugRelation>();
    HashSet<FabricLink.AugRelation> rels = new HashSet<FabricLink.AugRelation>(); 
    Iterator<FabricLink> alit = allLinks.iterator();
    while (alit.hasNext()) {
      FabricLink nextLink = alit.next();
      FabricLink.AugRelation relation = nextLink.getAugRelation();
      if (!nextLink.isFeedback()) {  // Autofeedback not flippable
        FabricLink flipLink = nextLink.flipped();
        if (flipSet.contains(flipLink)) {
          flipRels.add(relation);
        } else {
          flipSet.add(flipLink);
        }
      }
      rels.add(relation);
    } 
    
    //
    // We have a hint that something is signed if there are two
    // separate links running in opposite directions!
    //
        
    TreeMap<FabricLink.AugRelation, Boolean> relMap = new TreeMap<FabricLink.AugRelation, Boolean>();
    Boolean noDir = new Boolean(false);
    Boolean haveDir = new Boolean(true);
    Iterator<FabricLink.AugRelation> rit = rels.iterator();
    while (rit.hasNext()) {
      FabricLink.AugRelation rel = rit.next();
      relMap.put(rel, (flipRels.contains(rel)) ? haveDir : noDir);
    }    
    return (relMap);
  }
*/  
  /***************************************************************************
  ** 
  ** Process a link set that has not had directionality established
  */

  public static void assignDirections(List<FabricLink> allLinks, Map<FabricLink.AugRelation, Boolean> relMap) {    
    Iterator<FabricLink> alit = allLinks.iterator();
    while (alit.hasNext()) {
      FabricLink nextLink = alit.next();
      FabricLink.AugRelation rel = nextLink.getAugRelation();
      Boolean isDir = relMap.get(rel);
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
      FabricLink nextLink = alit.next();
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
  
  /***************************************************************************
  **
  ** For XML I/O
  ** Not used in application
  */  
      
  public static class NetworkDataWorker extends AbstractFactoryClient {
    
    public NetworkDataWorker(FabricFactory.FactoryWhiteboard whiteboard) {
      super(whiteboard);
      myKeys_.add("BioFabric");
      installWorker(new FabricColorGenerator.ColorSetWorker(whiteboard), new MyColorSetGlue());
      installWorker(new FabricDisplayOptions.FabricDisplayOptionsWorker(whiteboard), null);
      installWorker(new NodeInfoWorker(whiteboard), new MyNodeGlue());
      installWorker(new LinkInfoWorker(whiteboard), new MyLinkGlue());
      installWorker(new LinkGroupTagWorker(whiteboard), new MyGroupGlue());
    }
    
    protected Object localProcessElement(String elemName, Attributes attrs) throws IOException {
      Object retval = null;
      if (elemName.equals("BioFabric")) {
        FabricFactory.FactoryWhiteboard board = (FabricFactory.FactoryWhiteboard)this.sharedWhiteboard_;
        board.bfn = new BioFabricNetwork();
        retval = board.bfn;
      }
      return (retval);     
    }
  }
  
  public static class MyColorSetGlue implements GlueStick {
    public Object glueKidToParent(Object kidObj, AbstractFactoryClient parentWorker, 
                                  Object optionalArgs) throws IOException {
      FabricFactory.FactoryWhiteboard board = (FabricFactory.FactoryWhiteboard)optionalArgs;
      board.bfn.setColorGenerator(board.fcg);
      return (null);
    }
  }  
  
  public static class MyNodeGlue implements GlueStick {
    public Object glueKidToParent(Object kidObj, AbstractFactoryClient parentWorker, 
                                  Object optionalArgs) throws IOException {
      FabricFactory.FactoryWhiteboard board = (FabricFactory.FactoryWhiteboard)optionalArgs;
      board.bfn.addNodeInfoForIO(board.nodeInfo);
      return (null);
    }
  }  
  
  public static class MyLinkGlue implements GlueStick {
    public Object glueKidToParent(Object kidObj, AbstractFactoryClient parentWorker, 
                                  Object optionalArgs) throws IOException {
      FabricFactory.FactoryWhiteboard board = (FabricFactory.FactoryWhiteboard)optionalArgs;
      board.bfn.addLinkInfoForIO(board.linkInfo);
      return (null);
    }
  } 
  
  public static class MyGroupGlue implements GlueStick {
    public Object glueKidToParent(Object kidObj, AbstractFactoryClient parentWorker, 
                                  Object optionalArgs) throws IOException {
      FabricFactory.FactoryWhiteboard board = (FabricFactory.FactoryWhiteboard)optionalArgs;
      board.bfn.addLinkGroupForIO(board.groupTag);
      return (null);
    }
  } 
   
  /***************************************************************************
  **
  ** For XML I/O
  */  
      
  public static class LinkInfoWorker extends AbstractFactoryClient {
    
    public LinkInfoWorker(FabricFactory.FactoryWhiteboard board) {
      super(board);
      myKeys_.add("link");
    }
    
    protected Object localProcessElement(String elemName, Attributes attrs) throws IOException {
      Object retval = null;
      FabricFactory.FactoryWhiteboard board = (FabricFactory.FactoryWhiteboard)this.sharedWhiteboard_;    
      board.linkInfo = buildFromXML(elemName, attrs);
      retval = board.linkInfo;
      return (retval);     
    }  
    
    private LinkInfo buildFromXML(String elemName, Attributes attrs) throws IOException { 
      String src = AttributeExtractor.extractAttribute(elemName, attrs, "link", "src", true);
      src = CharacterEntityMapper.unmapEntities(src, false);
      String trg = AttributeExtractor.extractAttribute(elemName, attrs, "link", "trg", true);
      trg = CharacterEntityMapper.unmapEntities(trg, false);
      String rel = AttributeExtractor.extractAttribute(elemName, attrs, "link", "rel", true);
      rel = CharacterEntityMapper.unmapEntities(rel, false);
      String directed = AttributeExtractor.extractAttribute(elemName, attrs, "link", "directed", true);
      Boolean dirObj = Boolean.valueOf(directed);
      String shadow = AttributeExtractor.extractAttribute(elemName, attrs, "link", "shadow", true);
      Boolean shadObj = Boolean.valueOf(shadow);
      FabricLink flink = new FabricLink(src, trg, rel, shadObj.booleanValue(), dirObj);
      String col = AttributeExtractor.extractAttribute(elemName, attrs, "link", "column", false);
      String shadowCol = AttributeExtractor.extractAttribute(elemName, attrs, "link", "shadowCol", true);
      String srcRow = AttributeExtractor.extractAttribute(elemName, attrs, "link", "srcRow", true);
      String trgRow = AttributeExtractor.extractAttribute(elemName, attrs, "link", "trgRow", true);
      String color = AttributeExtractor.extractAttribute(elemName, attrs, "link", "color", true);
      
      if (!shadObj.booleanValue() && (col == null)) {
        throw new IOException();
      }
         
      LinkInfo retval;
      try {
        int useColumn = (col == null) ? Integer.MIN_VALUE : Integer.valueOf(col).intValue();
        int shadowColumn = Integer.valueOf(shadowCol).intValue();
        int srcRowVal = Integer.valueOf(srcRow).intValue();
        int trgRowVal = Integer.valueOf(trgRow).intValue();
        retval = new LinkInfo(flink, srcRowVal, trgRowVal, useColumn, shadowColumn, color);
      } catch (NumberFormatException nfex) {
        throw new IOException();
      }
      return (retval);
    }
  }
  
   /***************************************************************************
  **
  ** For XML I/O
  */  
      
  public static class LinkGroupTagWorker extends AbstractFactoryClient {
    
    public LinkGroupTagWorker(FabricFactory.FactoryWhiteboard board) {
      super(board);
      myKeys_.add("linkGroup");
    }
    
    protected Object localProcessElement(String elemName, Attributes attrs) throws IOException {
      Object retval = null;
      FabricFactory.FactoryWhiteboard board = (FabricFactory.FactoryWhiteboard)this.sharedWhiteboard_;
      board.groupTag = buildFromXML(elemName, attrs);
      retval = board.groupTag;
      return (retval);     
    }  
    
    private String buildFromXML(String elemName, Attributes attrs) throws IOException { 
      String tag = AttributeExtractor.extractAttribute(elemName, attrs, "linkGroup", "tag", true);
      return (tag);
    }
  }
  
  /***************************************************************************
  **
  ** For XML I/O
  */  
      
  public static class NodeInfoWorker extends AbstractFactoryClient {
    
    public NodeInfoWorker(FabricFactory.FactoryWhiteboard board) {
      super(board);
      myKeys_.add("node");
    }
    
    protected Object localProcessElement(String elemName, Attributes attrs) throws IOException {
      Object retval = null;
      FabricFactory.FactoryWhiteboard board = (FabricFactory.FactoryWhiteboard)this.sharedWhiteboard_;
      board.nodeInfo = buildFromXML(elemName, attrs);
      retval = board.nodeInfo;
      return (retval);     
    }  
    
    private NodeInfo buildFromXML(String elemName, Attributes attrs) throws IOException { 
      String name = AttributeExtractor.extractAttribute(elemName, attrs, "node", "name", true);
      name = CharacterEntityMapper.unmapEntities(name, false);
      String row = AttributeExtractor.extractAttribute(elemName, attrs, "node", "row", true);
      String minCol = AttributeExtractor.extractAttribute(elemName, attrs, "node", "minCol", true);
      String maxCol = AttributeExtractor.extractAttribute(elemName, attrs, "node", "maxCol", true);
      String minColSha = AttributeExtractor.extractAttribute(elemName, attrs, "node", "minColSha", true);
      String maxColSha = AttributeExtractor.extractAttribute(elemName, attrs, "node", "maxColSha", true);
      String minDrain = AttributeExtractor.extractAttribute(elemName, attrs, "node", "drainMin", false);
      String maxDrain = AttributeExtractor.extractAttribute(elemName, attrs, "node", "drainMax", false);
      String minDrainSha = AttributeExtractor.extractAttribute(elemName, attrs, "node", "drainMinSha", false);
      String maxDrainSha = AttributeExtractor.extractAttribute(elemName, attrs, "node", "drainMaxSha", false);
      String color = AttributeExtractor.extractAttribute(elemName, attrs, "node", "color", true);
      String cluster = AttributeExtractor.extractAttribute(elemName, attrs, "node", "cluster", false);
      cluster = CharacterEntityMapper.unmapEntities(cluster, false);
      
      NodeInfo retval;
      try {
        int nodeRow = Integer.valueOf(row).intValue();
        retval = new NodeInfo(name, nodeRow, color);
        if (cluster != null) {
        	UiUtil.fixMePrintout("Make cluster assign a list");
        	retval.setCluster(cluster);
        }
        
        int min = Integer.valueOf(minCol).intValue();
        int max = Integer.valueOf(maxCol).intValue();
        retval.updateMinMaxCol(min, false);
        retval.updateMinMaxCol(max, false);
        
        int minSha = Integer.valueOf(minColSha).intValue();
        int maxSha = Integer.valueOf(maxColSha).intValue();
        retval.updateMinMaxCol(minSha, true);
        retval.updateMinMaxCol(maxSha, true);
  
        if (minDrain != null) {
          int minDrVal = Integer.valueOf(minDrain).intValue();
          int maxDrVal = Integer.valueOf(maxDrain).intValue();
          retval.setDrainZone(new MinMax(minDrVal, maxDrVal), false);
        }
        if (minDrainSha != null) {
          int minDrValSha = Integer.valueOf(minDrainSha).intValue();
          int maxDrValSha = Integer.valueOf(maxDrainSha).intValue();
          retval.setDrainZone(new MinMax(minDrValSha, maxDrValSha), true);
        }
      } catch (NumberFormatException nfex) {
        throw new IOException();
      }
      return (retval);
    }
  }
  
  /***************************************************************************
   **
   ** Get ColorGenerator
   */    
   public FabricColorGenerator getColorGenerator() {
     return (colGen_);
   }
   
   /***************************************************************************
    **
    ** Get BuildData
    */ 
   public BuildData getBuildData(){
	   return bd_;
   }
}
