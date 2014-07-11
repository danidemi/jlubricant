package com.danidemi.jlubricant.embeddable.h2;

/**
 * Implementations define how H2 can store database data. 
 * 
 * @author Daniele Demichelis
 */
public abstract class H2Storage {

    /** Implementations should return the storage specifier as expected by H2. */
    public abstract String getStorageSpecifier();
    
    /** Implementations should return whether this is a memory only mode, or not. */
    public abstract boolean isMemoryMode();

}
