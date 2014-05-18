package com.danidemi.jlubricant.springbatch;


public class NamedJdbcCursorItemReaderTest {
	
//	HsqlDbms testDb;
//	
//	@BeforeClass
//	public static void setUpDb() {
//	}
//	
//	@Before
//	public void setUpTables() throws Exception {
//		HsqlDatabase db = new HsqlDatabase();
//		db.setDbName("test-db");
//		db.setStorage(new InProcessInMemory());
//		testDb = new HsqlDbms();
//		testDb.add( db );
//		testDb.start();
//		db.
//			.executeBatch(
//					"CREATE TABLE PEOPLE (firstName VARCHAR(64), lastName VARCHAR(64))",
//					"INSERT INTO PEOPLE (firstName, lastName) VALUES ('john', 'doe')",
//					"INSERT INTO PEOPLE (firstName, lastName) VALUES ('susie', 'wong')",
//					"INSERT INTO PEOPLE (firstName, lastName) VALUES ('zon', 'gong')",
//					"CREATE TABLE EMPTY (a_field VARCHAR(32))"
//			);
//	}
//	
//	@AfterClass
//	public static void tearDownDb() {
//		testDb.stop();
//	}
//
//	@Test public void playAround() throws Exception {
//		
//		// given
//		DriverManagerDataSource ds = new DriverManagerDataSource();
//		ds.setDriverClassName(testDb.getDriverClassName());
//		ds.setPassword(testDb.getPassword());
//		ds.setUsername(testDb.getUsername());
//		ds.setUrl(testDb.getConnectionString());
//		
//		
//		NamedJdbcCursorItemReader<Map<String, Object>> reader = new NamedJdbcCursorItemReader<Map<String, Object>>();
//		reader.setDataSource(ds);
//		reader.setSql("SELECT * FROM PEOPLE WHERE firstName = :FIELD");
//		reader.setParamSource( new MapSqlParameterSource("FIELD", "john") );
//		reader.setRowMapper(new ColumnMapRowMapper());
//		reader.afterPropertiesSet();
//		
//		// when
//		reader.open(new ExecutionContext());
//		Map<String, Object> read = reader.read();
//		reader.close();
//		
//		// then
//		assertNotNull( read );
//		assertEquals( "john", read.get("firstName") );
//		assertEquals( "doe", read.get("lastName") );
//		
//	}
	
}

