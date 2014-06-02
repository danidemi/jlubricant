package com.danidemi.jlubricant.utils;

import java.io.IOException;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.Test;


public class AllClassesTest {
	
	@Test
	public void findAllBySystemClasspath() throws IOException {

		new AllClassesBySystemClasspath().findClasses();
		
	}

	@Test
	public void findClassesFromSystemClassLoader() throws IOException {

		new AllClassesByClassloader(ClassLoader.getSystemClassLoader()).findClasses();
		
	}
	
	@Test
	public void findClassesFromClassLoader() throws IOException {
		
		new AllClassesByClassloader(ArrayUtils.class.getClassLoader()).findClasses();
		
	}	

}
