package com.danidemi.jlubricant.utils.classes;

import com.danidemi.jlubricant.utils.hoare.Preconditions;
import java.io.IOException;
import java.util.Set;

/**
 * Browse all classes available to a class loader.
 */
public class AllClassesByClassloader implements ClassFinder {

	private final ClassLoader classLoader;
	
	public AllClassesByClassloader(ClassLoader classLoader) {
            
            Preconditions.paramNotNull("classLoader cannot be null", classLoader);
            this.classLoader = classLoader;
            
	}
	
        /** Return all classes found through the {@link ClassLoader} specified ad instantiation. */
	@Override
	public Set<Class> allAvailableClasses() throws IOException {
		
		ClassCollector visitor = new ClassCollector();
		new VisitableClassloader(classLoader).accept( visitor );
		return visitor.getClasses();
	}

}
