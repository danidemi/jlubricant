package com.danidemi.jlubricant.embeddable.h2;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class H2DatabaseDescriptionTest {

	@Test
	public void shouldReturnDriverClassName() {
		
		assertThat( new H2DatabaseDescription("a-db").getDriverClassName(), equalTo( "org.h2.Driver" ) );
		
	}
	
}
