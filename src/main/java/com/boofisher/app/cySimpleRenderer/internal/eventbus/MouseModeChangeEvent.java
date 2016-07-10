package com.boofisher.app.cySimpleRenderer.internal.eventbus;

import com.boofisher.app.cySimpleRenderer.internal.input.handler.MouseMode;

public class MouseModeChangeEvent {

	private final MouseMode mouseMode;

	public MouseModeChangeEvent(MouseMode mouseMode) {
		this.mouseMode = mouseMode;
	}
	
	public MouseMode getMouseMode() {
		return mouseMode;
	}
}
