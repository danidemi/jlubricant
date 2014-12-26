package com.danidemi.jlubricant.embeddable.hsql.core;

import com.danidemi.jlubricant.embeddable.ServerException;

public interface Status {

	/** Invoked by {@link HsqlDbms} when it start. */
	void onStart() throws ServerException;

	/** Invoked by {@link HsqlDbms} when it stop. */
	void onStop() throws ServerException;

	/** Invoked by {@link HsqlDbms} when some properties which validity can depend on the status change. */ 
	void onPropertyChange();

}
