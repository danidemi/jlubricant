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

	private Set<Class> findClasses(File root, File current) {
		LinkedHashSet<Class> classes = new LinkedHashSet<>(0);
		if (current.isDirectory()) {

			File[] listFiles = current.listFiles();
			for (File file : listFiles) {
				classes.addAll(findClasses(root, file));
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
			} catch (ClassNotFoundException e) {
				//not a class after all
			}
			classes.add(forName);
		}
		return classes;
	}

	@Override
	public Set<Class> findClasses() throws IOException {
		LinkedHashSet<Class> foundClasses = new LinkedHashSet<>(0);

		Enumeration<URL> resources = classLoader.getResources("");

		while (resources.hasMoreElements()) {
			URL nextElement = resources.nextElement();
			String protocol = nextElement.getProtocol();

			if ("file".equals(protocol)) {
				String root = nextElement.getFile();
				foundClasses
						.addAll(findClasses(new File(root), new File(root)));
			} else {
				throw new UnsupportedOperationException("unsupported "
						+ nextElement);
			}

		}
		return foundClasses;
	}

}
