package com.danidemi.jlubricant.utils;

import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class VisitableJar {

	private JarFile jarFile;

	public VisitableJar(JarFile jarFile) {
		super();
		this.jarFile = jarFile;
	}


	public void accept(Visitor visitor) {

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
					visitor.onFoundClass(forName);
				}
			} catch (ClassNotFoundException | NoClassDefFoundError e) {
				visitor.onError(name);
			}
		}

	}
	
}
