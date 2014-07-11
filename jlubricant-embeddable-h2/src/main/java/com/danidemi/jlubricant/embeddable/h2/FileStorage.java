package com.danidemi.jlubricant.embeddable.h2;

/**
 *
 * @author danidemi
 */
public class FileStorage extends H2Storage {

    @Override
    public String getStorageSpecifier() {
        return "file";
    }

	@Override
	public boolean isMemoryMode() {
		return false;
	}
    

}
