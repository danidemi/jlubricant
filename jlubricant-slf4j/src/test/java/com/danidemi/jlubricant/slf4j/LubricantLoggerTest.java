package com.danidemi.jlubricant.slf4j;

import org.junit.Test;


public class LubricantLoggerTest {

	@Test public void shouldDo() {
		
		// given
		LubricantLogger logger = LoggerFactory.getLogger("logger");
		
		// when
		logger.debug(LubricantLogger.ERROR, "hello");
		
	}
	
}
