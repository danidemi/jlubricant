package com.danidemi.jlubricant.embeddable.h2;

import org.junit.Test;
import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.*;

public class H2DatabaseDescriptionTest {

	@Test
	public void shouldReturnDriverClassName() {
		
		assertThat( new H2DatabaseDescription("a-db").getDriverClassName(), equalTo( "org.h2.Driver" ) );
		
	}
	
}
