package com.danidemi.jlubricant.utils.classes;

import com.danidemi.jlubricant.utils.classes.AllClassesByClassloader;
import com.danidemi.jlubricant.utils.classes.AllClassesBySystemClasspath;
import java.io.IOException;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.Test;


public class AllClassesTest {
	
	@Test
	public void findAllBySystemClasspath() throws IOException {

		new AllClassesBySystemClasspath().allAvailableClasses();
		
	}

	@Test
	public void findClassesFromSystemClassLoader() throws IOException {

		new AllClassesByClassloader(ClassLoader.getSystemClassLoader()).allAvailableClasses();
		
	}
	
	@Test
	public void findClassesFromClassLoader() throws IOException {
		
		new AllClassesByClassloader(ArrayUtils.class.getClassLoader()).allAvailableClasses();
		
	}	

}
