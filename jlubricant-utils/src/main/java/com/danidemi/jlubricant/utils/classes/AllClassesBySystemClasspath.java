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
		
		ClassCollector cc = new ClassCollector();
		new VisitableSystemClasspath().accept( cc );
		return cc.getClasses();
	}
	
}
