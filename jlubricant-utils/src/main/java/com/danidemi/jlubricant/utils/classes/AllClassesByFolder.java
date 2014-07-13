package com.danidemi.jlubricant.utils.classes;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

public class AllClassesByFolder implements ClassFinder {

	private File home;

	public AllClassesByFolder(File home) {
		this.home = home;
	}

	@Override
	public Set<Class> allAvailableClasses() throws IOException {
		ClassCollector visitor = new ClassCollector();
		new VisitableFolder( home ).accept( visitor );
		return visitor.getClasses();
	}

}
