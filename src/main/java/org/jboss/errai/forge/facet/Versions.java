package org.jboss.errai.forge.facet;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Versions {
	
	private String errai_version;
	private String gwt_version;
	private String javaee_version;
	
	private static Versions instance;
	
	private Versions() {
		Properties configProp = new Properties();
		InputStream in = this.getClass().getResourceAsStream("/org/jboss/errai/forge/facet/versions.properties");
        try {
            configProp.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
		this.errai_version = configProp.getProperty("errai.version");
		this.gwt_version = configProp.getProperty("gwt.version");
		this.javaee_version = configProp.getProperty("javaee.version");
	}

	public static Versions getInstance(){
		if(instance == null)
			instance = new Versions();
		return instance;
	}
	
	public String getErrai_version() {
		return errai_version;
	}

	public String getGwt_version() {
		return gwt_version;
	}

	public String getJavaee_version() {
		return javaee_version;
	}
}
