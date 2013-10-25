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

package com.github.jlubricant.springbatch;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.batch.item.database.AbstractCursorItemReader;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MyParsedSql;
import org.springframework.jdbc.core.namedparam.MyParsedSqlDel;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
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
 * </p>
 *
 * <p>
 * The statement used to open the cursor is created with the 'READ_ONLY' option since a non read-only
 * cursor may unnecessarily lock tables or rows. It is also opened with 'TYPE_FORWARD_ONLY' option.
 * By default the cursor will be opened using a separate connection which means that it will not participate
 * in any transactions created as part of the step processing.
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
 */
@SuppressWarnings("rawtypes")
public class NamedJdbcCursorItemReader<T> extends AbstractCursorItemReader<T> {


	PreparedStatement preparedStatement;

	PreparedStatementSetter preparedStatementSetter;

	String sql;

	RowMapper rowMapper;

	SqlParameterSource paramSource;

//	/** Default maximum number of entries for this template's SQL cache: 256 */
//	public static final int DEFAULT_CACHE_LIMIT = 256;
//	private int cacheLimit = DEFAULT_CACHE_LIMIT;
//	
//	/** Cache of original SQL String to ParsedSql representation */
//	private final Map<String, ParsedSql> parsedSqlCache =
//			new LinkedHashMap<String, ParsedSql>(DEFAULT_CACHE_LIMIT, 0.75f, true) {
//				@Override
//				protected boolean removeEldestEntry(Map.Entry<String, ParsedSql> eldest) {
//					return size() > getCacheLimit();
//				}
//			};
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public void setParamSource(SqlParameterSource paramSource) {
		this.paramSource = paramSource;
	}
	
	
	
	
	
	/**
	 * Build a PreparedStatementCreator based on the given SQL and named parameters.
	 * <p>Note: Not used for the <code>update</code> variant with generated key handling.
	 * @param sql SQL to execute
	 * @param paramSource container of arguments to bind
	 * @return the corresponding PreparedStatementCreator
	 */
	protected PreparedStatementCreator getPreparedStatementCreator(String sql, SqlParameterSource paramSource) {
		ParsedSql parsedSql = getParsedSql(sql).getDelegate();
		String sqlToUse = NamedParameterUtils.substituteNamedParameters(parsedSql, paramSource);
		Object[] params = NamedParameterUtils.buildValueArray(parsedSql, paramSource, null);
		List<SqlParameter> declaredParameters = NamedParameterUtils.buildSqlParameterList(parsedSql, paramSource);
		PreparedStatementCreatorFactory pscf = new PreparedStatementCreatorFactory(sqlToUse, declaredParameters);
		return pscf.newPreparedStatementCreator(params);
	}

	/**
	 * Obtain a parsed representation of the given SQL statement.
	 * <p>The default implementation uses an LRU cache with an upper limit
	 * of 256 entries.
	 * @param sql the original SQL
	 * @return a representation of the parsed SQL statement
	 */
	protected MyParsedSqlDel getParsedSql(String sql) {
		return new MyParsedSqlDel( NamedParameterUtils.parseSqlStatement(sql) );
//		if (getCacheLimit() <= 0) {
//			return new MyParsedSqlDel( NamedParameterUtils.parseSqlStatement(sql) );
//		}
//		synchronized (this.parsedSqlCache) {
//			ParsedSql parsedSql = this.parsedSqlCache.get(sql);
//			if (parsedSql == null) {
//				parsedSql = NamedParameterUtils.parseSqlStatement(sql);
//				this.parsedSqlCache.put(sql, parsedSql);
//			}
//			return parsedSql;
//		}
	}
	
//	/**
//	 * Specify the maximum number of entries for this template's SQL cache.
//	 * Default is 256.
//	 */
//	public void setCacheLimit(int cacheLimit) {
//		this.cacheLimit = cacheLimit;
//	}
//
//	/**
//	 * Return the maximum number of entries for this template's SQL cache.
//	 */
//	public int getCacheLimit() {
//		return this.cacheLimit;
//	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

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
		
		MyParsedSqlDel parsedSql = getParsedSql(sql);
		
		String sqlToUse = NamedParameterUtils. substituteNamedParameters(parsedSql.getDelegate(), paramSource);
		
		String theSql = sqlToUse;
		
		
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
			
			PreparedStatementCreator preparedStatementCreator = this.getPreparedStatementCreator(sql, paramSource);
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
