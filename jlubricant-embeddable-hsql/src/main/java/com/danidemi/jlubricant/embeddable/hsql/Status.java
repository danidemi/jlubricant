package com.danidemi.jlubricant.embeddable.hsql;

import com.danidemi.jlubricant.embeddable.ServerException;

public interface Status {

	void onStart() throws ServerException;

	void onStop() throws ServerException;

}
