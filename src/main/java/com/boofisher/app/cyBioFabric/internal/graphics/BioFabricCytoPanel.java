package com.boofisher.app.cyBioFabric.internal.graphics;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JPanel;

import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;

public class BioFabricCytoPanel extends JPanel implements CytoPanelComponent {
			
			@Override
			public Component getComponent() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public CytoPanelName getCytoPanelName() {
				 return CytoPanelName.WEST;
			}

			@Override
			public String getTitle() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Icon getIcon() {
				// TODO Auto-generated method stub
				return null;
			}
	
}
