package com.danidemi.jlubricant.embeddable.hsql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;
import javax.swing.tree.RowMapper;
import javax.swing.tree.TreePath;

import org.slf4j.Logger;

import com.danidemi.jlubricant.slf4j.LoggerFactory;


public class DatasourceTemplate {
	
	public static interface RowMapper<T> {

		T map(ResultSet rs) throws SQLException;
		
	}

	private static final Logger logger = LoggerFactory.getLogger(DatasourceTemplate.class);
	private DataSource ds;

	public DatasourceTemplate(DataSource datasource) {
		this.ds = datasource;
	}

	public void execute(String statement) throws SQLException {
		if (logger.isDebugEnabled()) {
			logger.debug("Executing SQL statement [" + statement + "]");
		}
		
		Connection connection = null;
		Statement stm = null;
		
		try{
			connection = ds.getConnection();
			stm = connection.createStatement();			
			stm.execute(statement);
		} catch (SQLException e) {
			throw e;
		}finally{
			if(stm!=null) stm.close();
			if(connection!=null) connection.close();			
		}
				
	}
	
	public int queryForInt(String sql) throws SQLException {
		Number number = queryForObject(sql, Integer.class);
		return (number != null ? number.intValue() : 0);
	}	
	
	public <T> T queryForObject(String sql, Class<T> requiredType) throws SQLException {
		return queryForObject(sql, getSingleColumnRowMapper(requiredType));
	}	
	
	public <T> RowMapper<T> getSingleColumnRowMapper(Class<T> requiredType){
		return new RowMapper<T>() {
			
			@Override
			public T map(ResultSet rs) throws SQLException {
				return (T) rs.getObject(1);
			}
		};
	}
	
	public <T> T queryForObject(String sql, RowMapper<T> rowMapper) throws SQLException  {
		return (T) query(sql, rowMapper);
	}	
	
	public <T> List<T> query(String sql, RowMapper<T> rowMapper) throws SQLException{
		if (logger.isDebugEnabled()) {
			logger.debug("Executing SQL statement [" + sql + "]");
		}
		
		Connection connection = null;
		Statement stm = null;
		ResultSet rs = null;
		
		ArrayList<T> result = new ArrayList<T>();
		
		try{
			connection = ds.getConnection();
			stm = connection.createStatement();			
			rs = stm.executeQuery(sql);
			
			while(rs.next()){
				result.add( rowMapper.map(rs) );
			}
			
		} catch (SQLException e) {
			throw e;
		}finally{
			if(rs!=null) rs.close();
			if(stm!=null) stm.close();
			if(connection!=null) connection.close();			
		}
		
		return result;
	}

	
}
