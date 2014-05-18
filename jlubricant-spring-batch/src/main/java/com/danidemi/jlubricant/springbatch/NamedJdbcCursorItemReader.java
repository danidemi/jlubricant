/*
 * Copyright 2006-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.danidemi.jlubricant.springbatch;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.batch.item.database.AbstractCursorItemReader;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.ExposedParsedSql;
import org.springframework.jdbc.core.namedparam.NamedParameterUtils;
import org.springframework.jdbc.core.namedparam.ParsedSql;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

/**
 * <p>
 * Simple item reader implementation that opens a JDBC cursor and continually retrieves the
 * next row in the ResultSet.
 * This is highly based on Spring Batch's JdbcCursorItemReader.
 * </p>
 *
 * <p>
 * The statement used to open the cursor is created with the 'READ_ONLY' option since a non read-only
 * cursor may unnecessarily lock tables or rows. It is also opened with updatableRecords to false option,
 * which in turns is likely to impose a 'TYPE_FORWARD_ONLY' option. 
 * 
 * Currently the cursor will be opened using a separate connection which means that it will not participate
 * in any transactions created as part of the step processing.
 * It is not currently possible to change this behaviour.
 * </p>
 *
 * <p>
 * Each call to {@link #read()} will call the provided RowMapper, passing in the
 * ResultSet.
 * </p>
 *
 * @author Lucas Ward
 * @author Peter Zozom
 * @author Robert Kasanicky
 * @author Thomas Risberg
 * @author Daniele Demichelis
 */
@SuppressWarnings("rawtypes")
public class NamedJdbcCursorItemReader<T> extends AbstractCursorItemReader<T> {

	PreparedStatement preparedStatement;

	PreparedStatementSetter preparedStatementSetter;

	String sql;

	RowMapper rowMapper;

	SqlParameterSource paramSource;

	/** Default maximum number of entries for this template's SQL cache: 256 */
	public static final int DEFAULT_CACHE_LIMIT = 256;
	private int cacheLimit = DEFAULT_CACHE_LIMIT;
	
	/** Cache of original SQL String to ParsedSql representation */
	private final Map<String, ExposedParsedSql> parsedSqlCache =
			new LinkedHashMap<String, ExposedParsedSql>(DEFAULT_CACHE_LIMIT, 0.75f, true) {
				@Override
				protected boolean removeEldestEntry(Map.Entry<String, ExposedParsedSql> eldest) {
					return size() > getCacheLimit();
				}
			};
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public void setParamSource(SqlParameterSource paramSource) {
		this.paramSource = paramSource;
	}
	
	
	
	
	
	/**
	 * Obtain a parsed representation of the given SQL statement.
	 * <p>The default implementation uses an LRU cache with an upper limit
	 * of 256 entries.
	 * @param sql the original SQL
	 * @return a representation of the parsed SQL statement
	 */
	protected ExposedParsedSql getParsedSql(String sql) {
		if (getCacheLimit() <= 0) {
			return new ExposedParsedSql( NamedParameterUtils.parseSqlStatement(sql) );
		}
		synchronized (this.parsedSqlCache) {
			ExposedParsedSql parsedSql = this.parsedSqlCache.get(sql);
			if (parsedSql == null) {
				parsedSql = new ExposedParsedSql( NamedParameterUtils.parseSqlStatement(sql) );
				this.parsedSqlCache.put(sql, parsedSql);
			}
			return parsedSql;
		}
	}
	
	/**
	 * Specify the maximum number of entries for this template's SQL cache.
	 * Default is 256.
	 */
	public void setCacheLimit(int cacheLimit) {
		this.cacheLimit = cacheLimit;
	}

	/**
	 * Return the maximum number of entries for this template's SQL cache.
	 */
	public int getCacheLimit() {
		return this.cacheLimit;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	public NamedJdbcCursorItemReader() {
		super();
		setName(ClassUtils.getShortName(AbstractCursorItemReader.class));
	}

	/**
	 * Set the RowMapper to be used for all calls to read().
	 *
	 * @param rowMapper
	 */
	public void setRowMapper(RowMapper rowMapper) {
		this.rowMapper = rowMapper;
	}

	/**
	 * Set the SQL statement to be used when creating the cursor. This statement
	 * should be a complete and valid SQL statement, as it will be run directly
	 * without any modification.
	 *
	 * @param sql
	 */
	public void setSql(String sql) {
		this.sql = sql;
	}

//	/**
//	 * Set the PreparedStatementSetter to use if any parameter values that need
//	 * to be set in the supplied query.
//	 *
//	 * @param preparedStatementSetter
//	 */
//	public void setPreparedStatementSetter(PreparedStatementSetter preparedStatementSetter) {
//		this.preparedStatementSetter = preparedStatementSetter;
//	}
	
	

	/**
	 * Assert that mandatory properties are set.
	 *
	 * @throws IllegalArgumentException if either data source or sql properties
	 * not set.
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		super.afterPropertiesSet();
		Assert.notNull(sql, "The SQL query must be provided");
		Assert.notNull(rowMapper, "RowMapper must be provided");
	}


	@Override
	protected void openCursor(Connection con) {
		
		//MyParsedSqlDel parsedSql = getParsedSql(sql);
		
		//String sqlToUse = NamedParameterUtils.substituteNamedParameters(parsedSql.getDelegate(), paramSource);
		
		//String theSql = sqlToUse;
		
		
		try {
//			if (isUseSharedExtendedConnection()) {
//				preparedStatement = con.prepareStatement(theSql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY,
//						ResultSet.HOLD_CURSORS_OVER_COMMIT);
//			}
//			else {
//				preparedStatement = con.prepareStatement(theSql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
//			}
//			applyStatementSettings(preparedStatement);
//			if (this.preparedStatementSetter != null) {
//				
//				
//				preparedStatementSetter.setValues(preparedStatement);
//				
//				
//				
//				
//				
//			}
//			this.rs = preparedStatement.executeQuery();
			
			ParsedSql parsedSql1 = this.getParsedSql(sql).getDelegate();
			String sqlToUse1 = NamedParameterUtils.substituteNamedParameters(parsedSql1, paramSource);
			Object[] params = NamedParameterUtils.buildValueArray(parsedSql1, paramSource, null);
			List<SqlParameter> declaredParameters = NamedParameterUtils.buildSqlParameterList(parsedSql1, paramSource);
			PreparedStatementCreatorFactory pscf = new PreparedStatementCreatorFactory(sqlToUse1, declaredParameters);
			pscf.setResultSetType( ResultSet.TYPE_FORWARD_ONLY );
			pscf.setUpdatableResults(false);
			PreparedStatementCreator preparedStatementCreator = pscf.newPreparedStatementCreator(params);
			
			//con.prepareStatement(theSql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			
			preparedStatement = preparedStatementCreator.createPreparedStatement(con);
			this.rs = preparedStatement.executeQuery();
			
			handleWarnings(preparedStatement);
		}
		catch (SQLException se) {
			close();
			throw getExceptionTranslator().translate("Executing query", getSql(), se);
		}

	}


	@Override
	@SuppressWarnings("unchecked")
	protected T readCursor(ResultSet rs, int currentRow) throws SQLException {
		return (T) rowMapper.mapRow(rs, currentRow);
	}

	/**
	 * Close the cursor and database connection.
	 */
	@Override
	protected void cleanupOnClose() throws Exception {
		JdbcUtils.closeStatement(this.preparedStatement);
	}

	@Override
	public String getSql() {
		return this.sql;
	}
}
