package com.danidemi.jlubricant.utils.file;

import java.io.File;

public abstract class FileUtils {
	
	private FileUtils() {
		throw new UnsupportedOperationException("Not intended to be instantiated.");
	}

	public static void mkdirs(File... dirs){
		for (File file : dirs) {
			file.mkdirs();
		}
	}
	
}
