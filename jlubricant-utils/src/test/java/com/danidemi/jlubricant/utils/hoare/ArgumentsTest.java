package com.danidemi.jlubricant.utils.hoare;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;


public class ArgumentsTest {

	@Test(expected=IllegalArgumentException.class) public void shouldCheckNotNull(){
		
		Arguments.checkNotNull(null, "argument cannot be null");
		
	}
	
	@Test(expected=IllegalArgumentException.class) public void shouldCheckNotNullWithoutArguments(){
		
		Arguments.checkNotNull(null);
		
	}
	
	@Test(expected=IllegalArgumentException.class) public void shouldCheckNotNullWithFormat(){
		
		Arguments.checkNotNull(null, "message: %s", "cannot be null");
		
	}
	
	@Test public void shouldReturnTheArgumentWhenNotNull(){
		
		// given
		Object arg = new Object();
		
		// when
		Object result = Arguments.checkNotNull(arg, "message: %s", "cannot be null");
		
		// then
		assertThat( arg == result, is(true) );
		
	}
	
}
