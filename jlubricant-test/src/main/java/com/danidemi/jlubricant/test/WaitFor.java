package com.danidemi.jlubricant.test;

import java.util.concurrent.TimeUnit;

/**
 * Wait for a given amount of time, regardless of any interruption.
 * @author Daniele Demichelis
 */
public class WaitFor {

	private long amount;

	private WaitFor(long millis) {
		this.amount = millis;
	}
	
	private void waitForAmount(){
		long now = System.currentTimeMillis();
		do{
			try {
				Thread.sleep(amount);
			} catch (InterruptedException e) {
				
			}			
		}while(System.currentTimeMillis() - now < amount);
	}
	
	public static void waitFor(long amount, TimeUnit unit){
		new WaitFor( TimeUnit.MILLISECONDS.convert(amount, unit) ).waitForAmount();
	}
	
}
