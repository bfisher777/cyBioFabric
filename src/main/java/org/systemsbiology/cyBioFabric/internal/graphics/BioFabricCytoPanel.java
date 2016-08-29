package org.systemsbiology.cyBioFabric.internal.graphics;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.print.DocFlavor.URL;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.systemsbiology.cyBioFabric.internal.biofabric.app.BioFabricApplication;
import org.systemsbiology.cyBioFabric.internal.biofabric.ui.display.BioFabricNavAndControl;
import org.systemsbiology.cyBioFabric.internal.biofabric.ui.display.FabricMagnifier;
import org.systemsbiology.cyBioFabric.internal.biofabric.util.ResourceManager;

public class BioFabricCytoPanel extends JPanel implements CytoPanelComponent {
			
	/**
	 * 
	 */
	private static final long serialVersionUID = -6060773788747535825L;
	private String name = "BioFabric Magnifier Control and Tour Control";	
	
	public BioFabricCytoPanel(){
		this.setLayout(new BorderLayout());
		this.setBorder(new LineBorder(Color.black, 2));
	    Font labelFont = new Font("SansSerif", Font.BOLD, 10);
	    JLabel magLab = new JLabel(ResourceManager.getManager().getString("cytoPanel.default"));
	    magLab.setBorder(new EmptyBorder(0, 5, 0, 0));
	    magLab.setOpaque(true);
	    magLab.setBackground(Color.white);
	    magLab.setFont(labelFont);
	    this.add(magLab, BorderLayout.NORTH);
	}
	
	/**
	 * Returns the Component to be added to the CytoPanel. 
	 * @return The Component to be added to the CytoPanel. 
	 */
	@Override
	public Component getComponent() {
		// TODO Auto-generated method stub
		return this;
	}

	/**
	 * Returns the name of the CytoPanel that this component should be added to.
	 * @return the name of the CytoPanel that this component should be added to.
	 */
	@Override
	public CytoPanelName getCytoPanelName() {
		 return CytoPanelName.WEST;
	}

	/**
	 * Returns the title of the tab within the CytoPanel for this component.
	 * @return the title of the tab within the CytoPanel for this component.
	 */
	@Override
	public String getTitle() {
		// TODO Auto-generated method stub
		return name;
	}

	/**
	 * Returns the Icon to be used along with the title in the tab for this
	 * this component. May be null!
	 * @return the Icon to be used along with the title in the tab for this
	 * this component. May be null!
	 */
	@Override
	public Icon getIcon() {
		//TODO: need to create a Thumbnail Biofabric JPanel to add to container			
		java.net.URL ugif = getClass().getResource("/images/BioFab16White.gif"); 
		ImageIcon myIcon = new ImageIcon(ugif);											
		
		Image scaledImage = getScaledImage(myIcon.getImage(), 15, 15);
		myIcon = new ImageIcon(scaledImage);
		//JLabel picLabel = new JLabel(myIcon);
		
		return myIcon;
	}
	
	/*
	 * Scale an Image
	 * http://stackoverflow.com/questions/6714045/how-to-resize-jlabel-imageicon
	 * 
	 * */
	private Image getScaledImage(Image srcImg, int w, int h){
		if(w <= 0 || h <= 0){  w = h = 15; }
		
	    BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
	    Graphics2D g2 = resizedImg.createGraphics();

	    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	    g2.drawImage(srcImg, 0, 0, w, h, null);
	    g2.dispose();

	    return resizedImg;
	}	
	
	public void setNavAndControl(BioFabricNavAndControl nac){
		this.removeAll();
		this.revalidate();
		this.add(nac);
		this.revalidate();
	}
}
