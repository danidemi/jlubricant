package com.danidemi.jlubricant.utils;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

public class VisitableFolder {

	private File home;

	public VisitableFolder(File home) {
		this.home = home;
	}

	private Set<Class> findClasses(File root, File current, Visitor visitor) {
		LinkedHashSet<Class> classes = new LinkedHashSet<>(0);
		if (current.isDirectory()) {

			File[] listFiles = current.listFiles();
			for (File file : listFiles) {
				classes.addAll(findClasses(root, file, visitor));
			}
			return classes;
		} else if (current.isFile()) {

			String replace = current.getAbsolutePath().replace(
					root.getAbsolutePath(), "");

			if (replace.startsWith(File.separator)) {
				replace = replace.substring(File.separator.length());
			}
			if (replace.endsWith(".class")) {
				replace = replace.substring(0,
						replace.length() - ".class".length());
			}
			replace = replace.replace(File.separatorChar, '.');

			Class<?> forName = null;
			try {
				forName = Class.forName(replace);
				visitor.onFoundClass(forName);
			} catch (ClassNotFoundException e) {
				visitor.onError(replace);
			}
			
		}
		return classes;
	}

	public void accept(Visitor visitor) {
		findClasses(home, home, visitor);
	}

	
}
