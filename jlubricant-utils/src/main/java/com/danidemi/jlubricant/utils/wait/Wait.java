package com.danidemi.jlubricant.utils.wait;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Various methods to wait for something.
 */
public abstract class Wait {
	
	private static final Logger log = LoggerFactory.getLogger(Wait.class);

	public static void forever(){
		while(true){
			try {
				Thread.sleep(Long.MAX_VALUE);
			} catch (InterruptedException e) {
				log.trace("An exception occurred while waiting.", e);
			}			
		}
	}
	
	public static void untilShutdown() {
		
		final Object lock = new Object();
		final AtomicBoolean goOn = new AtomicBoolean(true);
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				goOn.set(false);
				synchronized (lock) {
					lock.notify();
				}
			}
		});
		
		while(goOn.get() == true){
			synchronized (lock) {
				try {
					lock.wait();
				} catch (InterruptedException e) {
					if(goOn.get()){
						log.trace("An exception occurred while waiting.", e);						
					}
				}
			}
		}
		
		
		
	}

	public static void waitForMillis(long milliseconds) {
		
		long start = System.currentTimeMillis();
		long target = start + milliseconds;
		
		while(System.currentTimeMillis() < target){
			try {
				Thread.sleep( Math.abs( target - System.currentTimeMillis() ));
			} catch (InterruptedException e) {
				// ok!
			}			
		}
	}
	
}
