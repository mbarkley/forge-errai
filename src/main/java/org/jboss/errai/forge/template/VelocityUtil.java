/**
 * Copyright (C) 2012 Sandro Sonntag sso@adorsys.de
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.errai.forge.template;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.apache.commons.lang3.StringUtils;

/**
 * 
 * @author Sandro Sonntag
 */
public class VelocityUtil {
	
	public String capitalize(String orgString) {
		return StringUtils.capitalize(orgString);
		
	}
	
	public String uncapitalize(String orgString) {
		return StringUtils.uncapitalize(orgString);
	}
	
	/**
	 * 
	 * Helper methods for errai functions 
	 * 
	 */
	
	public void extractRemoteFromService(File file) {
		Object obj = deserializeObjectFromFile(file);
		Method[] methods = obj.getClass().getDeclaredMethods();
		for (int i = 0; i < methods.length; i++) {
			Method method = methods[i];
			if(Modifier.isPublic(method.getModifiers())){
				method.getReturnType().getName();
			}
			
		}
		
	}
	
	private Object deserializeObjectFromFile(File file){
		try {
		    // Deserialize from a file
		    ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
		    // Deserialize the object
		    Object obj = in.readObject();
		    in.close();
		    return obj;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
		
	}
	
	

}
