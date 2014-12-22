package com.danidemi.jlubricant.utils.classes;

import java.io.IOException;
import java.util.Set;

public class AllClassesBySystemClasspath implements ClassFinder {

	@Override
	public Set<Class> allAvailableClasses() throws IOException {
		
		ClassCollector cc = new ClassCollector();
		new VisitableSystemClasspath().accept( cc );
		return cc.getClasses();
	}
	
}
