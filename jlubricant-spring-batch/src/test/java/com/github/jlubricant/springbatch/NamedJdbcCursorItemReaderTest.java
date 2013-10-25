package com.github.jlubricant.springbatch;

import static org.junit.Assert.*;

import java.util.Map;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import com.github.jlubricant.test.TestDb;

public class NamedJdbcCursorItemReaderTest {
	
	private static TestDb testDb;

	@BeforeClass
	public static void setUpDb() {
		testDb = new TestDb(NamedJdbcCursorItemReaderTest.class);
		testDb.start();
	}
	
	@Before
	public void setUpTables() throws Exception {
		testDb.truncate();
		testDb
			.executeBatch(
					"CREATE TABLE PEOPLE (firstName VARCHAR(64), lastName VARCHAR(64))",
					"INSERT INTO PEOPLE (firstName, lastName) VALUES ('john', 'doe')",
					"INSERT INTO PEOPLE (firstName, lastName) VALUES ('susie', 'wong')",
					"INSERT INTO PEOPLE (firstName, lastName) VALUES ('zon', 'gong')",
					"CREATE TABLE EMPTY (a_field VARCHAR(32))"
			);
			
	}
	
	@AfterClass
	public static void tearDownDb() {
		testDb.stop();
	}

	@Test public void playAround() throws Exception {
		
		// given
		DriverManagerDataSource ds = new DriverManagerDataSource();
		ds.setDriverClassName(testDb.getJDBCDriver());
		ds.setPassword(testDb.getPassword());
		ds.setUsername(testDb.getUsername());
		ds.setUrl(testDb.getJDBCUrl());
		
		
		NamedJdbcCursorItemReader<Map<String, Object>> reader = new NamedJdbcCursorItemReader<Map<String, Object>>();
		reader.setDataSource(ds);
		reader.setSql("SELECT * FROM PEOPLE WHERE firstName = :FIELD");
		reader.setParamSource( new MapSqlParameterSource("FIELD", "john") );
		reader.setRowMapper(new ColumnMapRowMapper());
		reader.afterPropertiesSet();
		
		// when
		reader.open(new ExecutionContext());
		Map<String, Object> read = reader.read();
		reader.close();
		
		// then
		assertNotNull( read );
		assertEquals( "john", read.get("firstName") );
		assertEquals( "doe", read.get("lastName") );
		
	}
	
}

