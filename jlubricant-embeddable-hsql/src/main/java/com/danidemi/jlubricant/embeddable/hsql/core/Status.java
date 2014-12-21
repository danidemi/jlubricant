package com.danidemi.jlubricant.embeddable.hsql.core;

import com.danidemi.jlubricant.embeddable.ServerException;

public interface Status {

	void onStart() throws ServerException;

	void onStop() throws ServerException;

	void onPropertyChange();

}
