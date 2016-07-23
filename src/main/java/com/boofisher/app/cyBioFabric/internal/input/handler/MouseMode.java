package com.boofisher.app.cyBioFabric.internal.input.handler;

public enum MouseMode {

	SELECT,
	CAMERA;
	
	public static MouseMode getDefault() {
		return CAMERA;
	}
}
