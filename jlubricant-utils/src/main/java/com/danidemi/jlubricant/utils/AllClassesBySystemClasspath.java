package com.danidemi.jlubricant.utils;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.jar.JarFile;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

public class AllClassesBySystemClasspath implements ClassFinder {

	@Override
	public Set<Class> findClasses() throws IOException {
		
		LinkedHashSet<Class> found = new LinkedHashSet<>(0);
		
		String cp = System.getProperty("java.class.path");
		String[] split = StringUtils.split(cp, File.pathSeparator);
		for (String string : split) {
			File file = new File(string);
			if(file.isDirectory()){
				found.addAll( new AllClassesByFolder(file).findClasses() );
			}else if(file.isFile() && "jar".equals( FilenameUtils.getExtension(file.getAbsolutePath()) )){				
				AllClassesByJar allClassesByJar = new AllClassesByJar( new JarFile( file ) );
				found.addAll( allClassesByJar.findClasses() );
			}else{
				throw new RuntimeException("Unsupported " + file);
			}
		}
		
		return found;
	}

}
