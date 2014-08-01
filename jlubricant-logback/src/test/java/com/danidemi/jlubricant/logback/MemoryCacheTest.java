package com.danidemi.jlubricant.logback;

import static org.junit.Assert.assertEquals;

import org.apache.commons.lang3.SerializationUtils;
import org.junit.Test;

public class MemoryCacheTest {

	@Test public void isSerializable() {
		
		// given
		
		// a memory cache...
		MemoryCache original = new MemoryCache();
		original.setMaxSize(10);
		original.put("hello", 100L);
		original.put("world", 200L);
		
		// a cloned copy
		MemoryCache tested = SerializationUtils.clone(original);
		
		
		
		// then
		// ...clone shoule be equal to the original one
		assertEquals( original.itemsInCache(), tested.itemsInCache() );
		assertEquals( original.timestampOfLastOccurence("hello"), tested.timestampOfLastOccurence("hello") );
		assertEquals( original.timestampOfLastOccurence("heaven"), tested.timestampOfLastOccurence("heaven") );
		
	}
	
}
