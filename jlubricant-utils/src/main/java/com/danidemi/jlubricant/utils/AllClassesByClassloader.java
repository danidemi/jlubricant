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
		
		final LinkedHashSet<Class> classes = new LinkedHashSet<>(0);
		
		new VisitableClassloader(classLoader).accept( new Visitor() {
			
			@Override
			public void onFoundClass(Class foundClass) {
				classes.add(foundClass);
			}
			
			@Override
			public void onError(String className) {
				// TODO Auto-generated method stub
				
			}
		} );
		
		return classes;
	}

}
