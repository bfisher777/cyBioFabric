package org.systemsbiology.cyBioFabric.internal.events;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.view.model.CyNetworkView;
import org.systemsbiology.cyBioFabric.internal.biofabric.util.ResourceManager;
import org.systemsbiology.cyBioFabric.internal.cytoscape.view.CyBFNetworkView;
import org.systemsbiology.cyBioFabric.internal.graphics.BioFabricCytoPanel;
import org.systemsbiology.cyBioFabric.internal.icons.BioFabricImageIcon;
import org.systemsbiology.cyBioFabric.internal.icons.TaskFactoryPredicate;

public class BioFabricSetCurrentViewHandler {	
	
	BioFabricCytoPanel panel;
	List<BioFabricImageIcon> imageIcons;
	TaskFactoryPredicate taskFactoryPredicate;
	
	public BioFabricSetCurrentViewHandler(BioFabricCytoPanel panel, List<BioFabricImageIcon> imageIcons, TaskFactoryPredicate taskFactoryPredicate){
		this.panel = panel;
		this.imageIcons = imageIcons;
		this.taskFactoryPredicate = taskFactoryPredicate;
	}
	
	/* 
	** Sets the correct BioFabricApplication NavAndControl in the BioFabricCytoPanel
	** Registers the command set name with the image icon
	** Enables and disables biofabric buttons 
	** @param id */
	public void handleEvent(CyNetworkView view){
		
		CyBFNetworkView bfNetworkView = null;
		Dimension size = panel.getSize();
		if((view instanceof CyBFNetworkView)){
			
			bfNetworkView = (CyBFNetworkView)view;
			panel.setNavAndControl(bfNetworkView.getBioFabricApplication().getBioFabricWindow().getNAC());
			
			taskFactoryPredicate.setIsReady(true);
		}else{
			
			panel.removeAll();
			panel.revalidate();
			panel.setSize(size);
			panel.setBorder(new LineBorder(Color.black, 2));
		    Font labelFont = new Font("SansSerif", Font.BOLD, 20);
		    JLabel magLab = new JLabel(ResourceManager.getManager().getString("cytoPanel.default"));
		    magLab.setBorder(new EmptyBorder(0, 5, 0, 0));
		    magLab.setOpaque(true);
		    magLab.setBackground(Color.white);
		    magLab.setFont(labelFont);
		    panel.add(magLab, BorderLayout.NORTH);	
		    
		    taskFactoryPredicate.setIsReady(false);
		}
		
		for(BioFabricImageIcon icon : imageIcons){

			if((view instanceof CyBFNetworkView)){							
								
				icon.registerCommandSetName(bfNetworkView.getBioFabricApplication().getBioFabricWindow().COMMAND_NAME);
			}			
		}		
	}	
}
