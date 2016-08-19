package com.boofisher.app.cyBioFabric.internal.graphics;

//import javax.media.opengl.GL2;
import javax.swing.JComponent;

import  com.boofisher.app.cyBioFabric.internal.graphics.GraphicsConfiguration;

public abstract class AbstractGraphicsConfiguration implements GraphicsConfiguration {
		
	
	@Override
	public void drawScene() {
			
	}
	
	@Override
	public void update() {
	}
	
	@Override
	public void dispose() {
	}
	
	@Override
	public void initializeFrame(JComponent container, JComponent inputComponent) {
	}

}
