package com.danidemi.jlubricant.embeddable.database.utils;

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

/**
 * Utility to quickly execute common operations on database. 
 * Not intended to be a public API, just a tool to be used internally when a sql command should be executed.
 * Highly inspired by Spring's JdbcTemplate.
 */
public class DatasourceTemplate {
	
	public static interface RowMapper<T> {
		T map(ResultSet rs) throws SQLException;
	}

	private static final Logger logger = LoggerFactory.getLogger(DatasourceTemplate.class);
	private DataSource ds;

	public DatasourceTemplate(DataSource datasource) {
		this.ds = datasource;
	}

	/**
	 * Execute a statement.
	 */
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
	
	/**
	 * Execute a query that returns a single int field.
	 */
	public int queryForInt(String sql) throws SQLException {
		Number number = queryForObject(sql, Integer.class);
		return (number != null ? number.intValue() : 0);
	}	
	
	/**
	 * Execute a query that returns a single value that can be cast to the given type.
	 */
	public <T> T queryForObject(String sql, Class<T> requiredType) throws SQLException {
		return queryForObject(sql, getSingleColumnRowMapper(requiredType));
	}	
	
	/**
	 * Execute a query that return a single result obtained allowing the current row to be mapped through
	 * the provided {@link RowMapper}.
	 * @throws SQLException
	 */
	public <T> T queryForObject(String sql, RowMapper<T> rowMapper) throws SQLException  {
		return (T) query(sql, rowMapper);
	}
	
	/**
	 * A {@link RowMapper} that returns the object contained in the first field.
	 */
	public <T> RowMapper<T> getSingleColumnRowMapper(Class<T> requiredType){
		return new RowMapper<T>() {
			
			@Override
			public T map(ResultSet rs) throws SQLException {
				return (T) rs.getObject(1);
			}
		};
	}
	
	/**
	 * Return a list of objects obtained applying the given {@link RowMapper} to each record.
	 * @return	A not null empty list when the result set is empty.
	 * @throws SQLException
	 */
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
