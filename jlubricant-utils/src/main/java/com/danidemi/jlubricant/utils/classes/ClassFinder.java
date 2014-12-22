package com.danidemi.jlubricant.utils.classes;

import java.io.IOException;
import java.util.Set;

/**
 * Implementations are able to search for all available classes in a given context.
 * It's up to the implementation to really tell what this context is.
 */
public interface ClassFinder {

    /** Returns all the classes that are available in the context */
    Set<Class> allAvailableClasses() throws IOException;
	
}
