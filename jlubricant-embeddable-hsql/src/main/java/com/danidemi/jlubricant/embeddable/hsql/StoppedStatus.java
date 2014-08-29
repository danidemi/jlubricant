package com.danidemi.jlubricant.embeddable.hsql;


class StoppedStatus implements Status {

	private HsqlDbms hsqlDbms;

	public StoppedStatus(HsqlDbms hsqlDbms) {
		this.hsqlDbms = hsqlDbms;
	}

	@Override
	public void onStart() {
		
		hsqlDbms.startEngine();
		
		hsqlDbms.transitionTo( new StartedStatus(hsqlDbms) );
				
	}
	
	@Override
	public void onStop() {
	
		// nothing to do, already stopped
		
	}

}
