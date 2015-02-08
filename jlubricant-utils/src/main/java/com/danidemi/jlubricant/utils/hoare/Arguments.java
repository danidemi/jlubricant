package com.danidemi.jlubricant.utils.hoare;

import static java.lang.String.format;

import java.util.Collection;

public abstract class Arguments {
	
	private Arguments() {
		throw new UnsupportedOperationException("Not intended to be instantiated.");
	}

	/**
	 * Check the provided string and throws an excpetion if blank (null or empty).
	 * @param notBlank	String that should not be blank.
	 */
	public static void checkNotBlank(String notBlank) {
		checkNotBlank(notBlank, "Invalid '" + notBlank + "'");
	}

	public static void checkNotBlank(String notBlank, String message) {
		if(isBlank(notBlank)){
			throw new IllegalArgumentException(message);
		}
	}
	
	/**
	 * The first line of a constructor should be <code>this()</code> or <code>super()</code>.
	 * So, you cannot check if a parameter is null before invoking the constructor. I.e. you cannot write something like:
	 * <pre>
	 * Thing(String param){
	 *   if(param==null) throw new IllegalArgumentException();
	 *   this(param, 2);
	 * }
	 * </pre>
	 * But you can 'inline' the check with this method, like that:
	 * <pre>
	 * Thing(String param){
	 *   this( checkNotNull(param) , 2);
	 * }
	 * </pre> 
	 * @param cannotBeNull
	 */
	public static <T> T checkNotNull(T cannotBeNull) {
		return checkNotNull(cannotBeNull, "Invalid argument, could not be null");
	}

	public static <T> T checkNotNull(T cannotBeNull, String message) {
		if(cannotBeNull == null) throw new IllegalArgumentException(message);
		return cannotBeNull;
	}
	
	public static <T> T checkNotNull(T cannotBeNull, String format, Object... args) {
		String message = format(format, args);
		return checkNotNull(cannotBeNull, message);
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
