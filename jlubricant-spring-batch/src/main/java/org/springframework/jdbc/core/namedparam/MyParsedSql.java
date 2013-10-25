package org.springframework.jdbc.core.namedparam;

import java.util.List;

public class MyParsedSql extends ParsedSql {
	
	MyParsedSql(String originalSql) {
		super(originalSql);
	}

	@Override
	public String getOriginalSql() {
		// TODO Auto-generated method stub
		return super.getOriginalSql();
	}

	@Override
	public void addNamedParameter(String parameterName, int startIndex, int endIndex) {
		// TODO Auto-generated method stub
		super.addNamedParameter(parameterName, startIndex, endIndex);
	}

	@Override
	public List<String> getParameterNames() {
		// TODO Auto-generated method stub
		return super.getParameterNames();
	}

	@Override
	public int[] getParameterIndexes(int parameterPosition) {
		// TODO Auto-generated method stub
		return super.getParameterIndexes(parameterPosition);
	}

	@Override
	public void setNamedParameterCount(int namedParameterCount) {
		// TODO Auto-generated method stub
		super.setNamedParameterCount(namedParameterCount);
	}

	@Override
	public int getNamedParameterCount() {
		// TODO Auto-generated method stub
		return super.getNamedParameterCount();
	}

	@Override
	public void setUnnamedParameterCount(int unnamedParameterCount) {
		// TODO Auto-generated method stub
		super.setUnnamedParameterCount(unnamedParameterCount);
	}

	@Override
	public int getUnnamedParameterCount() {
		// TODO Auto-generated method stub
		return super.getUnnamedParameterCount();
	}

	@Override
	public void setTotalParameterCount(int totalParameterCount) {
		// TODO Auto-generated method stub
		super.setTotalParameterCount(totalParameterCount);
	}

	@Override
	public int getTotalParameterCount() {
		// TODO Auto-generated method stub
		return super.getTotalParameterCount();
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}
	
}
