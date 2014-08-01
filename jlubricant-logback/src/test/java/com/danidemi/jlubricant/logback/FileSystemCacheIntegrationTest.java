package com.danidemi.jlubricant.logback;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.slf4j.LoggerFactory;

public class FileSystemCacheIntegrationTest {

	@Rule public TestName test = new TestName();
	
	@Test
	public void test() {
		ConfigureLogback.configure( getClass().getResourceAsStream(test.getMethodName() + ".xml") );
		org.slf4j.Logger log = LoggerFactory.getLogger(FileSystemCacheIntegrationTest.class);
		log.error("hello");
	}

}
