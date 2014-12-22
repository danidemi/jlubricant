package com.danidemi.jlubricant.utils.classes;

import java.io.IOException;
import java.util.Set;
import java.util.jar.JarFile;

/**
 * Return all classes available in a Jar file.
 */
public class AllClassesByJar implements ClassFinder {

	private JarFile jarFile;

	public AllClassesByJar(JarFile jarFile) {
		super();
		this.jarFile = jarFile;
	}
	
	
	public Set<Class> allAvailableClasses() throws IOException {
		ClassCollector collector = new ClassCollector();
		new VisitableJar(jarFile).accept( collector );
		return collector.getClasses();
	}

}
