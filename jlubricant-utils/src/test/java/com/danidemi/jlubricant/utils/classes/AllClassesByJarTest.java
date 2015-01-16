package com.danidemi.jlubricant.utils.classes;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.jar.JarFile;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class AllClassesByJarTest {

	@Rule public TemporaryFolder tmp = new TemporaryFolder();
	
	@Test public void shouldBrowseClasses() throws IOException{
		
		// given
		JarFile jarFile = aJarFileInTheClasspath();
		
		// when
		AllClassesByJar allClassesByJar = new AllClassesByJar(jarFile);
		Set<Class> allAvailableClasses = allClassesByJar.allAvailableClasses();
						
		// then
		assertThat( allAvailableClasses, hasItem((Class)org.junit.Assert.class) );
		assertThat( allAvailableClasses, not(hasItem((Class)java.lang.String.class)) );
		
	}
	
	@Test public void shouldComplainWhenTheJarIsNotInTheClasspath() throws IOException{
		
		// given
		JarFile jarFile = aJarFileNotInClasspath();
		
		// when
		Exception e = null;
		try{
			AllClassesByJar allClassesByJar = new AllClassesByJar(jarFile);
			Set<Class> allAvailableClasses = allClassesByJar.allAvailableClasses();
			fail();
		}catch(IllegalArgumentException iae){
			e= iae;
		}
		
		// then
		assertThat( e.getMessage(), containsString("smalljar.jar") );
		
	}

	private JarFile aJarFileNotInClasspath() throws IOException,
			FileNotFoundException {
		File smallJar = new File(tmp.getRoot(), "smalljar.jar");
		IOUtils.copy( getClass().getResourceAsStream("smalljar.jar") , new FileOutputStream(smallJar));
		JarFile jarFile = new JarFile( smallJar );
		return jarFile;
	}
	
	private JarFile aJarFileInTheClasspath() throws IOException {
		List<String> split = Arrays.asList( System.getProperty( "java.class.path" ).split( File.pathSeparator ) );
		String jar = CollectionUtils.select(split, new org.apache.commons.collections4.Predicate<String>() {

			@Override
			public boolean evaluate(String arg0) {
				return arg0.endsWith(".jar") && arg0.contains("junit");
			}
		}).iterator().next();
		JarFile jarFile = new JarFile( new File(jar) );
		return jarFile;
	}	
	
}
