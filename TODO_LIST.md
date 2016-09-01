#cyBioFabric
##TODO
cyBioFabric list of things that need to be completed or sorted out.

- [ ] Add better comments
- [ ] Create test suite
- [ ] Refactor code... remove unused / reorganize (this would be much easier with test suite)
- [ ] Thumbnail view not resizing properly
- [ ] Move BioFabric task bar into Cytoscape as buttons and implement
- [x] Move BioFabric menu into Cytoscape as new menu and implement
- [ ] Need to add short cuts (cytoscape calls them accelerators, see BioFabricAbstractCyActionBuilder class) to menu items and buttons
- [ ] Add Tunables to retrieve users data when menu items or buttons are selected
- [ ] Add node added and node removed event listeners / handlers?
- [ ] Add edge added and edge removed event listeners / handlers?
- [ ] Add group node /group edges added and removed listeners / handlers?
- [ ] Rewrite a way to get view info from layout algorithm (currently in network view model, make new columns in network table?)
- [ ] Fix / implement layout algorithms
- [ ] Update visual property value node / edge selected
- [ ] Implement / disable choices in the select menu, there are alot
- [ ] Implement / disable cytoscape select all nodes and edges menu item
- [ ] Implement / disable cytoscape deselect all nodes and edges menu item
- [ ] Implement / disable cytoscape hide selected nodes and edges menu item
- [ ] Implement / disable cytoscape show selected nodes and edges menu item
- [ ] Implement / disable cytoscape show all nodes and edges menu item
- [ ] Update screen drawings when node / edge selected from table data
- [ ] Implement a way to export / import using CX data model, http://www.home.ndexbio.org/data-model/
- [x] Remove search button, since Cytoscape has better search?
- [ ] Right click add, remove, edit, select (Commands will not be supported)

- [ ] Bug can zoom out too far on magnifier using z shortcut. 
- [ ] Bug when zooming on large networks the minus zoom button sometimes breaks
- [ ] Bug index out of bounds exceptions with zoom to current selection action (not easy to reproduce)
- [ ] Bug index out of bounds for a Tabbed Pane on closing application (not easy to reproduce)

		Exception in thread "AWT-EventQueue-0" java.lang.IndexOutOfBoundsException: Index: 1, Size: 1
        at java.util.ArrayList.rangeCheck(ArrayList.java:653)
        at java.util.ArrayList.get(ArrayList.java:429)
        at javax.swing.JTabbedPane.getTabComponentAt(JTabbedPane.java:2395)
        at javax.swing.plaf.basic.BasicTabbedPaneUI.calculateTabHeight(BasicTabbedPaneUI.java:1713)
        at javax.swing.plaf.basic.BasicTabbedPaneUI.calculateMaxTabHeight(BasicTabbedPaneUI.java:1742)
        at javax.swing.plaf.basic.BasicTabbedPaneUI$TabbedPaneScrollLayout.calculateTabRects(BasicTabbedPaneUI.java:3172)
        at javax.swing.plaf.basic.BasicTabbedPaneUI$TabbedPaneLayout.calculateLayoutInfo(BasicTabbedPaneUI.java:2512)
        at javax.swing.plaf.basic.BasicTabbedPaneUI$TabbedPaneScrollLayout.layoutContainer(BasicTabbedPaneUI.java:2916)
        at java.awt.Container.layout(Container.java:1510)
        at java.awt.Container.doLayout(Container.java:1499)
        at java.awt.Container.validateTree(Container.java:1695)
        at java.awt.Container.validateTree(Container.java:1704)
        at java.awt.Container.validateTree(Container.java:1704)
        at java.awt.Container.validateTree(Container.java:1704)
        at java.awt.Container.validate(Container.java:1630)
        at javax.swing.RepaintManager$3.run(RepaintManager.java:711)
        at javax.swing.RepaintManager$3.run(RepaintManager.java:709)
        at java.security.AccessController.doPrivileged(Native Method)
        at java.security.ProtectionDomain$JavaSecurityAccessImpl.doIntersectionPrivilege(ProtectionDomain.java:76)
        at javax.swing.RepaintManager.validateInvalidComponents(RepaintManager.java:708)
        at javax.swing.RepaintManager$ProcessingRunnable.run(RepaintManager.java:1731)
        at java.awt.event.InvocationEvent.dispatch(InvocationEvent.java:311)
        at java.awt.EventQueue.dispatchEventImpl(EventQueue.java:756)
        at java.awt.EventQueue.access$500(EventQueue.java:97)
        at java.awt.EventQueue$3.run(EventQueue.java:709)
        at java.awt.EventQueue$3.run(EventQueue.java:703)
        at java.security.AccessController.doPrivileged(Native Method)
        at java.security.ProtectionDomain$JavaSecurityAccessImpl.doIntersectionPrivilege(ProtectionDomain.java:76)
        at java.awt.EventQueue.dispatchEvent(EventQueue.java:726)
        at java.awt.EventDispatchThread.pumpOneEventForFilters(EventDispatchThread.java:201)
        at java.awt.EventDispatchThread.pumpEventsForFilter(EventDispatchThread.java:116)
        at java.awt.EventDispatchThread.pumpEventsForHierarchy(EventDispatchThread.java:105)
        at java.awt.EventDispatchThread.pumpEvents(EventDispatchThread.java:101)
        at java.awt.EventDispatchThread.pumpEvents(EventDispatchThread.java:93)
        at java.awt.EventDispatchThread.run(EventDispatchThread.java:82)
        
		Exception in thread "AWT-EventQueue-0" java.lang.ArrayIndexOutOfBoundsException: No such child: 22
	        at java.awt.Container.getComponent(Container.java:334)
	        at javax.swing.JComponent.rectangleIsObscured(JComponent.java:4390)
	        at javax.swing.JComponent.paint(JComponent.java:1054)
	        at javax.swing.JComponent.paintToOffscreen(JComponent.java:5210)
	        at javax.swing.RepaintManager$PaintManager.paintDoubleBuffered(RepaintManager.java:1579)
	        at javax.swing.RepaintManager$PaintManager.paint(RepaintManager.java:1502)
	        at javax.swing.RepaintManager.paint(RepaintManager.java:1272)
	        at javax.swing.JComponent._paintImmediately(JComponent.java:5158)
	        at javax.swing.JComponent.paintImmediately(JComponent.java:4969)
	        at javax.swing.RepaintManager$4.run(RepaintManager.java:831)
	        at javax.swing.RepaintManager$4.run(RepaintManager.java:814)
	        at java.security.AccessController.doPrivileged(Native Method)
	        at java.security.ProtectionDomain$JavaSecurityAccessImpl.doIntersectionPrivilege(ProtectionDomain.java:76)
	        at javax.swing.RepaintManager.paintDirtyRegions(RepaintManager.java:814)
	        at javax.swing.RepaintManager.paintDirtyRegions(RepaintManager.java:789)
	        at javax.swing.RepaintManager.prePaintDirtyRegions(RepaintManager.java:738)
	        at javax.swing.RepaintManager.access$1200(RepaintManager.java:64)
	        at javax.swing.RepaintManager$ProcessingRunnable.run(RepaintManager.java:1732)
	        at java.awt.event.InvocationEvent.dispatch(InvocationEvent.java:311)
	        at java.awt.EventQueue.dispatchEventImpl(EventQueue.java:756)
	        at java.awt.EventQueue.access$500(EventQueue.java:97)
	        at java.awt.EventQueue$3.run(EventQueue.java:709)
	        at java.awt.EventQueue$3.run(EventQueue.java:703)
	        at java.security.AccessController.doPrivileged(Native Method)
	        at java.security.ProtectionDomain$JavaSecurityAccessImpl.doIntersectionPrivilege(ProtectionDomain.java:76)
	        at java.awt.EventQueue.dispatchEvent(EventQueue.java:726)
	        at java.awt.EventDispatchThread.pumpOneEventForFilters(EventDispatchThread.java:201)
	        at java.awt.EventDispatchThread.pumpEventsForFilter(EventDispatchThread.java:116)
	        at java.awt.EventDispatchThread.pumpEventsForHierarchy(EventDispatchThread.java:105)
	        at java.awt.EventDispatchThread.pumpEvents(EventDispatchThread.java:101)
	        at java.awt.EventDispatchThread.pumpEvents(EventDispatchThread.java:93)
	        at java.awt.EventDispatchThread.run(EventDispatchThread.java:82)
