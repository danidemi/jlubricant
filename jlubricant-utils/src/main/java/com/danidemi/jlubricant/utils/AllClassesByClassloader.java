package com.danidemi.jlubricant.utils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;

public class AllClassesByClassloader implements ClassFinder {

	private ClassLoader classLoader;
	
	public AllClassesByClassloader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}
	
	@Override
	public Set<Class> findClasses() throws IOException {
		
		ClassCollector visitor = new ClassCollector();
		new VisitableClassloader(classLoader).accept( visitor );
		return visitor.getClasses();
	}

}
