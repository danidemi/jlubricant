package com.danidemi.jlubricant.logback;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class FileSystemCacheTest {

	@Test
	public void testCacheEvict() throws Exception {
		throw new RuntimeException("not yet implemented");
	}

	@Test
	public void testPut() throws Exception {
		// given
		FileSystemCache cache = new FileSystemCache();
		cache.setMaxSize(10);
		
		// when
		int count1 = cache.itemsInCache();
		cache.put("message", 100L);
		int count2 = cache.itemsInCache();
		cache.put("message", 200L);
		int count3 = cache.itemsInCache();
		
		// then
		assertThat(count2 - count1, equalTo(1));
		assertThat(count3 - count2, equalTo(0));
	}

	@Test
	public void testTimestampOfLastOccurence() throws Exception {
		throw new RuntimeException("not yet implemented");
	}

	@Test
	public void testItemsInCache() throws Exception {
		throw new RuntimeException("not yet implemented");
	}

	@Test
	public void testClear() throws Exception {
		throw new RuntimeException("not yet implemented");
	}

	@Test
	public void testSetMaxSize() throws Exception {
		throw new RuntimeException("not yet implemented");
	}

}
