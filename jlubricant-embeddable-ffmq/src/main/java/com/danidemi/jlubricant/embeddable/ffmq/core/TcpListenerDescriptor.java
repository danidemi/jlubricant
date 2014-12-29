package com.danidemi.jlubricant.embeddable.ffmq.core;

import net.timewalker.ffmq3.listeners.tcp.io.TcpListener;
import net.timewalker.ffmq3.local.FFMQEngine;
import net.timewalker.ffmq3.utils.Settings;

public class TcpListenerDescriptor {
	
	private String listenAddr;
	private int port;
	private Settings settings;

	public TcpListenerDescriptor(String listenAddr, int port, Settings settings) {
		super();
		this.listenAddr = listenAddr;
		this.port = port;
		this.settings = settings;
	}

	TcpListener build(FFMQEngine engine) {
		return new TcpListener(engine, listenAddr, port, settings);
		
	}
}
