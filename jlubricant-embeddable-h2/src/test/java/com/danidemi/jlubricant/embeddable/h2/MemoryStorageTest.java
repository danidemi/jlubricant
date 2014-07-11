package com.danidemi.jlubricant.embeddable.h2;

import org.junit.Test;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class MemoryStorageTest {

	@Test public void shouldBeAMemoryMode() {
		
		assertThat( new MemoryStorage().isMemoryMode(), is(true) );
		
	}
	
}
