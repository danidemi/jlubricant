package com.danidemi.jlubricant.embeddable.h2;

public class MemoryStorage extends H2Storage {

	@Override
	public String getStorageSpecifier() {
		return "mem";
	}

}
