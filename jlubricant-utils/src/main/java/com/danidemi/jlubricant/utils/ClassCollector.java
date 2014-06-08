package com.danidemi.jlubricant.utils;

import java.util.LinkedHashSet;

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

}
