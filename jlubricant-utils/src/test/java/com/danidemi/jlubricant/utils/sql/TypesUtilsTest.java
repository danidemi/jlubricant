package com.danidemi.jlubricant.utils.sql;

import org.junit.Test;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class TypesUtilsTest {

	@Test public void shouldRetrieveTheCorrectTypes() {
		
		assertThat( TypesUtils.typeNameOf( java.sql.Types.TIMESTAMP ), equalTo("TIMESTAMP"));
		assertThat( TypesUtils.typeNameOf( java.sql.Types.VARCHAR ), equalTo("VARCHAR"));
		assertThat( TypesUtils.typeNameOf( java.sql.Types.FLOAT ), equalTo("FLOAT"));

		
	}
	
}
