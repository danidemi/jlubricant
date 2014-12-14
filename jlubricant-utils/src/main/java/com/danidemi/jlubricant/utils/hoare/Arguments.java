package com.danidemi.jlubricant.utils.hoare;

import static java.lang.String.format;

import java.util.Collection;

public abstract class Arguments {
	
	private Arguments() {
		throw new UnsupportedOperationException("Not intended to be instantiated.");
	}

	public static void checkNotBlank(String notBlank) {
		checkNotBlank(notBlank, "Invalid '" + notBlank + "'");
	}

	public static void checkNotBlank(String notBlank, String message) {
		if(isBlank(notBlank)){
			throw new IllegalArgumentException(message);
		}
	}
	
	public static void checkNotNull(Object cannotBeNull) {
		checkNotNull(cannotBeNull, "Invalid argument, could not be null");
	}

	public static void checkNotNull(Object cannotBeNull, String message) {
		if(cannotBeNull == null){
			throw new IllegalArgumentException(message);
		}
	}
	
	public static void checkNotNull(Object cannotBeNull, String format, Object... args) {
		String message = format(format, args);
		checkNotNull(cannotBeNull, message);
	}		

	public static boolean isBlank(String username2) {
		return username2 == null || username2.trim().length() == 0;
	}

	public static void checkNotEquals(Object argument,
			Object notEqualTo, String message) {
		boolean equal = (argument == null && notEqualTo == null) || (argument.equals(notEqualTo));
		if(equal) throw new IllegalArgumentException(message);
		
	}

	public static void checkNotEmpty(@SuppressWarnings("rawtypes") Collection dbs, String string) {
		if(dbs == null || dbs.size() == 0){
			throw new IllegalArgumentException(string);
		}
		
	}
	
}
