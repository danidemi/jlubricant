package com.danidemi.jlubricant.embeddable.h2;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class MemoryStorageTest {

	@Test public void shouldBeAMemoryMode() {
		
		assertThat( new MemoryStorage().isMemoryMode(), is(true) );
		
	}
	
}
