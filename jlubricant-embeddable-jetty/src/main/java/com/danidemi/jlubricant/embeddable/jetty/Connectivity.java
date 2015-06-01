package com.danidemi.jlubricant.embeddable.jetty;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;

public interface Connectivity {

	Connector doInstall(Server server);

}
