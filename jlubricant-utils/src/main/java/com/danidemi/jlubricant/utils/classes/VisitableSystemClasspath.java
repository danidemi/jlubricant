package com.danidemi.jlubricant.utils;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.jar.JarFile;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

public class VisitableSystemClasspath  {

	public void accept(Visitor visitor) throws IOException {
		
		String cp = System.getProperty("java.class.path");
		String[] split = StringUtils.split(cp, File.pathSeparator);
		for (String string : split) {
			File file = new File(string);
			if(file.isDirectory()){
				
				new VisitableFolder( file ).accept( visitor );
				
			}else if(file.isFile() && "jar".equals( FilenameUtils.getExtension(file.getAbsolutePath()) )){
				
				new VisitableJar( new JarFile( file ) ).accept( visitor );
				
			}else{
				throw new RuntimeException("Unsupported " + file);
			}
		}
		
		
	}

}
