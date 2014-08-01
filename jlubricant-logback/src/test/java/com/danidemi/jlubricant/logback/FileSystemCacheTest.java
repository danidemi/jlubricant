package com.danidemi.jlubricant.logback;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.io.File;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class FileSystemCacheTest {
	
	public @Rule TemporaryFolder tmp = new TemporaryFolder();
	
	@Test
	public void shouldRetrieveCacheInfoFromPreviousRun() throws Exception {
		
		// given
		FileSystemCache cache1 = new FileSystemCache();
		File file = tmp.newFile();
		cache1.setFile(file);
		cache1.setMaxSize(10);
		
		// ...let write some messages
		cache1.put("bad", 100L);
		cache1.put("girls", 200L);
		cache1.put("go", 300L);
		cache1.put("to", 400L);
		cache1.put("amsterdam", 500L);

		// when
		// ...another cache is created, to the same file
		FileSystemCache cache2 = new FileSystemCache();
		cache2.setFile(file);
		
		// then
		assertThat(cache2.itemsInCache(), equalTo(5));
		assertThat(cache2.timestampOfLastOccurence("amsterdam"), equalTo(500L));
		assertThat(cache2.timestampOfLastOccurence("girls"), equalTo(200L));
		
	}	

	@Test
	public void shouldPutItemsInTheSameCache() throws Exception {
		
		// given
		FileSystemCache cache = new FileSystemCache();
		cache.setFile(tmp.newFile());
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

}
