package com.danidemi.jlubricant.test;

import java.util.concurrent.TimeUnit;

import org.junit.Test;
import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.*;
public class WaitForTest {

	@Test
	public void shouldWaitFor() {
		
		// when
		long start = System.currentTimeMillis();
		WaitFor.waitFor(2, TimeUnit.SECONDS);
		long end = System.currentTimeMillis();
		
		// then
		assertThat(end - start, greaterThan(2000L));
		
	}
	
}
