package com.danidemi.jlubricant.utils;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class AllClassesByJar implements ClassFinder {

	private JarFile jarFile;

	public AllClassesByJar(JarFile jarFile) {
		super();
		this.jarFile = jarFile;
	}

	@Override
	public Set<Class> findClasses() throws IOException {

		LinkedHashSet<Class> classes = new LinkedHashSet<>(0);

		Enumeration<JarEntry> entries = jarFile.entries();

		while (entries.hasMoreElements()) {
			JarEntry nextElement = entries.nextElement();

			if (nextElement.isDirectory())
				continue;

			String name = nextElement.getName();

			name = name.replace('/', '.');
			name = name.substring(0, name.length() - ".class".length());

			try {
				Class<?> forName = Class.forName(name);
				if (forName != null) {
					classes.add(forName);
				}
			} catch (ClassNotFoundException | NoClassDefFoundError e) {
				// not a class after all
			}
		}

		return classes;
	}

}
