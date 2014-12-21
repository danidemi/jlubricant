package com.danidemi.jlubricant.embeddable.h2;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.danidemi.jlubricant.embeddable.JdbcDatabaseDescriptor;
import com.danidemi.jlubricant.embeddable.ServerException;
import com.danidemi.jlubricant.embeddable.ServerStartException;
import com.danidemi.jlubricant.embeddable.ServerStopException;

public class H2DbmsTest {
	
	public @Rule TemporaryFolder tmp = new TemporaryFolder(); 

	@Test
	public void shouldStartAndStop() throws ServerException, IOException {
		H2Dbms h2Ddms = new H2Dbms();
		h2Ddms.setBaseDir(tmp.newFolder());
		
		h2Ddms.start();
		h2Ddms.stop();
	}
        
        @Test
        public void shouldAddAMemoryDatabase() throws ServerStartException, SQLException, IOException, ServerStopException {

            H2Dbms tested = new H2Dbms();
            tested.setBaseDir(tmp.newFolder());
            H2Storage filestorage = new FileStorage();

            H2DatabaseDescription db = new H2DatabaseDescription("test", filestorage);
            tested.add(db);
            tested.start();
            
            perfromTest("test", tested);
            
            tested.stop();

        }
	
	@Test
	public void shouldAddAMemoryDatabaseToTheDbms() throws Exception {
	
            H2Dbms tested = new H2Dbms();
            tested.setBaseDir(tmp.newFolder());
            
            H2DatabaseDescription db = new H2DatabaseDescription("test");
            tested.add(db);
            tested.start();            

            perfromTest("test", tested);

            tested.stop();
		
	}

    private void perfromTest(final String test, H2Dbms tested) throws ServerStartException, SQLException {
        
        Connection conn = tested.dbByName(test).getConnection();
        
        Statement stm = conn.createStatement();
        stm.execute("CREATE TABLE PEOPLE(NAME CHAR(64))");
        stm.execute("INSERT INTO PEOPLE(NAME) VALUES('John')");
        ResultSet executeQuery = stm.executeQuery("SELECT COUNT(*) AS C FROM PEOPLE");
        executeQuery.next();
        int int1 = executeQuery.getInt(1);
        
        conn.close();
        
        JdbcDatabaseDescriptor dbByName = tested.dbByName(test);
		conn = dbByName.getConnection();
        
        stm = conn.createStatement();
        executeQuery = stm.executeQuery("SELECT COUNT(*) AS C FROM PEOPLE");
        executeQuery.next();
        int int2 = executeQuery.getInt(1);
        
        conn.close();
        
        assertEquals(int1, int2);
    }

}
