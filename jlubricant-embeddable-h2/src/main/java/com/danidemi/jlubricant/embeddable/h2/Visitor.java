package com.danidemi.jlubricant.embeddable.h2;

public interface Visitor {

	void visit(H2DatabaseDescription h2DatabaseDescription);

	void visit(H2Storage h2Storage);

}
