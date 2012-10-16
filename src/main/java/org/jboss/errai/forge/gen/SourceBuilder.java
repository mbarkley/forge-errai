package org.jboss.errai.forge.gen;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class SourceBuilder {
	
	public List<String> readSource(File file) {
		List<String> sourceLines = new ArrayList<String>(); 
		DataInputStream in = null;
		try {
		  FileInputStream fstream = new FileInputStream(file);
		  in = new DataInputStream(fstream);
		  BufferedReader br = new BufferedReader(new InputStreamReader(in));
		  String strLine;
		  while ((strLine = br.readLine()) != null)   {		
			  sourceLines.add(strLine);
		  }
		}
		catch (Exception e) {
		  System.err.println("Error: " + e.getMessage());
	    }
		finally {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sourceLines;
	}
	
	public void writeSource(List<String> sourceLines, File file) {
		BufferedWriter writer = null;
		try{
		  // Create file 
		  FileWriter fstream = new FileWriter(file);
		  writer = new BufferedWriter(fstream);
	      for(String line : sourceLines){
	        writer.write(line);
	        writer.newLine();
	      }
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
