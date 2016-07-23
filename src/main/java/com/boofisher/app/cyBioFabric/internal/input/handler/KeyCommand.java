package com.boofisher.app.cyBioFabric.internal.input.handler;

import com.boofisher.app.cyBioFabric.internal.input.handler.KeyCommand;

public interface KeyCommand {

	public void up();
	
	public void down();
	
	public void left();
	
	public void right();
	
	
	public static KeyCommand EMPTY = new KeyCommand() {
		public void up() { }
		public void right() { }
		public void left() { }
		public void down() { }
	};
	
}
