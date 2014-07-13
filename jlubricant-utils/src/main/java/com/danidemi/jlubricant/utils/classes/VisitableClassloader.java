package com.danidemi.jlubricant.utils.classes;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;

public class VisitableClassloader {

	private ClassLoader classLoader;

	public VisitableClassloader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}
	
	private Set<Class> findClasses(File root, File current, Visitor visitor) {
		LinkedHashSet<Class> classes = new LinkedHashSet<>(0);
		if (current.isDirectory()) {

			File[] listFiles = current.listFiles();
			for (File file : listFiles) {
				findClasses(root, file, visitor);
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

	
	public void accept(Visitor visitor) throws IOException {
		
		Enumeration<URL> resources = classLoader.getResources("");

		while (resources.hasMoreElements()) {
			URL nextElement = resources.nextElement();
			String protocol = nextElement.getProtocol();

			if ("file".equals(protocol)) {
				String root = nextElement.getFile();
				findClasses(new File(root), new File(root), visitor);
			} else {
				throw new UnsupportedOperationException("unsupported "
						+ nextElement);
			}

		}
	}
	
}
