package com.danidemi.jlubricant.embeddable.hsql;

import com.danidemi.jlubricant.embeddable.ServerStopException;

public class StartedStatus implements Status {

	private HsqlDbms hsqlDbms;

	public StartedStatus(HsqlDbms hsqlDbms) {
		this.hsqlDbms = hsqlDbms;
	}

	@Override
	public void onStart() {
		// nothing to do, already started.
	}
	
	@Override
	public void onStop() throws ServerStopException {
		
		hsqlDbms.stopEngine();
		
		hsqlDbms.transitionTo(new StoppedStatus(hsqlDbms));
		
	}

}
