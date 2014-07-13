package com.danidemi.jlubricant.utils.classes;

import java.util.LinkedHashSet;
import java.util.Set;

public class ClassCollector implements Visitor {
	
	LinkedHashSet<Class> foundClasses;
	
	public ClassCollector() {
		foundClasses = new LinkedHashSet<>(0);
	}

	@Override
	public void onFoundClass(Class foundClass) {
		foundClasses.add(foundClass);

	}

	@Override
	public void onError(String className) {
		// nothing to do
	}
	
	public Set<Class> getClasses() {
		return foundClasses;
	}

}
