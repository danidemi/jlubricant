package com.danidemi.jlubricant.embeddable.hsql;

import com.danidemi.jlubricant.embeddable.ServerException;
import com.danidemi.jlubricant.embeddable.ServerStartException;


class StoppedStatus implements Status {

	private HsqlDbms hsqlDbms;

	public StoppedStatus(HsqlDbms hsqlDbms) {
		this.hsqlDbms = hsqlDbms;
	}

	@Override
	public void onStart() throws ServerException {
		
		hsqlDbms.startEngine();
		
		hsqlDbms.transitionTo( new StartedStatus(hsqlDbms) );
				
	}
	
	@Override
	public void onStop() {
		// nothing to do, already stopped
	}

	@Override
	public void onPropertyChange() {
		// when stoppes, all properties can be changed
	}

}
