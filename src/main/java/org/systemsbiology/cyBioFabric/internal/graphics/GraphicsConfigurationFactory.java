package org.systemsbiology.cyBioFabric.internal.graphics;


/**
 * If I were using Guice I would just use a Provider.
 * 
 * Made this an enum just because it requires less code.
 * 
 * @author mkucera
 */
public enum GraphicsConfigurationFactory {
	
	MAIN_FACTORY {
		public MainGraphicsConfiguration createGraphicsConfiguration() {
			return new MainGraphicsConfiguration();
		}
	},
	
	BIRDS_EYE_FACTORY {
		public BirdsEyeGraphicsConfiguration createGraphicsConfiguration() {
			return new BirdsEyeGraphicsConfiguration();
		}
	},
	
	THUMBNAIL_FACTORY {
		public ThumbnailGraphicsConfiguration createGraphicsConfiguration() {
			return new ThumbnailGraphicsConfiguration();
		}
	};
	
	public abstract GraphicsConfiguration createGraphicsConfiguration();
}
