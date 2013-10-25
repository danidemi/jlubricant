package org.springframework.jdbc.core.namedparam;

import java.util.List;

public class MyParsedSqlDel {

	private ParsedSql delegate;
	
	public MyParsedSqlDel(String originalSql) {
		this.delegate = new ParsedSql(originalSql);
	}
	
	public MyParsedSqlDel(ParsedSql parsedSql){
		this.delegate = parsedSql;
	}
	
	public ParsedSql getDelegate() {
		return delegate;
	}

	/**
	 * Return the SQL statement that is being parsed.
	 */
	public String getOriginalSql() {
		return delegate.getOriginalSql();
	}


	/**
	 * Add a named parameter parsed from this SQL statement.
	 * @param parameterName the name of the parameter
	 * @param startIndex the start index in the original SQL String
	 * @param endIndex the end index in the original SQL String
	 */
	public void addNamedParameter(String parameterName, int startIndex, int endIndex) {
		delegate.addNamedParameter(parameterName, startIndex, endIndex);
	}

	/**
	 * Return all of the parameters (bind variables) in the parsed SQL statement.
	 * Repeated occurences of the same parameter name are included here.
	 */
	public List<String> getParameterNames() {
		return delegate.getParameterNames();
	}

	/**
	 * Return the parameter indexes for the specified parameter.
	 * @param parameterPosition the position of the parameter
	 * (as index in the parameter names List)
	 * @return the start index and end index, combined into
	 * a int array of length 2
	 */
	public int[] getParameterIndexes(int parameterPosition) {
		return delegate.getParameterIndexes(parameterPosition);
	}

	/**
	 * Set the count of named parameters in the SQL statement.
	 * Each parameter name counts once; repeated occurences do not count here.
	 */
	public void setNamedParameterCount(int namedParameterCount) {
		delegate.setNamedParameterCount(namedParameterCount);
	}

	/**
	 * Return the count of named parameters in the SQL statement.
	 * Each parameter name counts once; repeated occurences do not count here.
	 */
	public int getNamedParameterCount() {
		return delegate.getNamedParameterCount();
	}

	/**
	 * Set the count of all of the unnamed parameters in the SQL statement.
	 */
	public void setUnnamedParameterCount(int unnamedParameterCount) {
		delegate.setUnnamedParameterCount(unnamedParameterCount);
	}

	/**
	 * Return the count of all of the unnamed parameters in the SQL statement.
	 */
	public int getUnnamedParameterCount() {
		return delegate.getUnnamedParameterCount();
	}

	/**
	 * Set the total count of all of the parameters in the SQL statement.
	 * Repeated occurences of the same parameter name do count here.
	 */
	public void setTotalParameterCount(int totalParameterCount) {
		delegate.setTotalParameterCount(totalParameterCount);
	}

	/**
	 * Return the total count of all of the parameters in the SQL statement.
	 * Repeated occurences of the same parameter name do count here.
	 */
	public int getTotalParameterCount() {
		return delegate.getTotalParameterCount();
	}


	/**
	 * Exposes the original SQL String.
	 */
	@Override
	public String toString() {
		return delegate.toString();
	}
	
}
