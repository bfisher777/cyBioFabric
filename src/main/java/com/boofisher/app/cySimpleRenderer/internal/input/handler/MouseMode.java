package com.boofisher.app.cySimpleRenderer.internal.input.handler;

public enum MouseMode {

	SELECT,
	CAMERA;
	
	public static MouseMode getDefault() {
		return CAMERA;
	}
}
