package com.danidemi.jlubricant.utils.classes;

import static java.lang.String.format;

import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.danidemi.jlubricant.utils.hoare.Preconditions;

/**
 * Allows to browse all classes in a jar file through a {@link Visitor}.
 * The jarFile has to be in the classpath. 
 */
public class VisitableJar {

	private JarFile jarFile;

	public VisitableJar(JarFile jarFileInClasspath) {
		super();
		
		String property = System.getProperty( "java.class.path" );
		String name = jarFileInClasspath.getName();
		Preconditions.condition( 
				format("Jar File %s is not in the classpath %s", 
						jarFileInClasspath.getName(), 
						property), 
				property.contains(name) );
		if(property.contains(name)){
		}
		
		this.jarFile = jarFileInClasspath;
	}

	public void accept(Visitor visitor) {

		Enumeration<JarEntry> entries = jarFile.entries();

		while (entries.hasMoreElements()) {
			JarEntry nextElement = entries.nextElement();

			if (nextElement.isDirectory())
				continue;

			String name = nextElement.getName();

			name = name.replace('/', '.');
			name = name.substring(0, name.length() - ".class".length());

			try {
				Class<?> forName = Class.forName(name);
				if (forName != null) {
					visitor.onFoundClass(forName);
				}
			} catch (ClassNotFoundException | NoClassDefFoundError e) {
				visitor.onError(name);
			}
		}

	}
	
}
