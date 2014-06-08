package com.danidemi.jlubricant.utils;

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
	public Set<Class> findClasses() throws IOException {
		ClassCollector visitor = new ClassCollector();
		new VisitableFolder( home ).accept( visitor );
		return visitor.getClasses();
	}

//	private Set<Class> findClasses(File root, File current) {
//		LinkedHashSet<Class> classes = new LinkedHashSet<>(0);
//		if (current.isDirectory()) {
//
//			File[] listFiles = current.listFiles();
//			for (File file : listFiles) {
//				classes.addAll(findClasses(root, file));
//			}
//			return classes;
//		} else if (current.isFile()) {
//
//			String replace = current.getAbsolutePath().replace(
//					root.getAbsolutePath(), "");
//
//			if (replace.startsWith(File.separator)) {
//				replace = replace.substring(File.separator.length());
//			}
//			if (replace.endsWith(".class")) {
//				replace = replace.substring(0,
//						replace.length() - ".class".length());
//			}
//			replace = replace.replace(File.separatorChar, '.');
//
//			Class<?> forName = null;
//			try {
//				forName = Class.forName(replace);
//			} catch (ClassNotFoundException e) {
//				//not a class after all
//			}
//			classes.add(forName);
//		}
//		return classes;
//	}
//
//	@Override
//	public Set<Class> findClasses() throws IOException {
//		return findClasses(home, home);
//	}

}
