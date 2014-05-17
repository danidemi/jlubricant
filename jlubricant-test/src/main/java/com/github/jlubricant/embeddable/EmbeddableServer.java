package com.github.jlubricant.embeddable;

import java.io.IOException;

public interface EmbeddableServer {

	void start() throws IOException;

	void stop() throws InterruptedException;

}
