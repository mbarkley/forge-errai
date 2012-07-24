package org.jboss.errai.forge.facet;


/**
 * @author pslegr
 */
public class ErraiInstalled {
	
	private static ErraiInstalled instance;
	private boolean isInstalled;
	
	public boolean isInstalled() {
		return isInstalled;
	}

	public void setInstalled(boolean isInstalled) {
		this.isInstalled = isInstalled;
	}

	private ErraiInstalled() {	}
	
	public static ErraiInstalled getInstance() {
		if(instance == null) {
			instance = new ErraiInstalled();
		}
		return instance;
	}
}
