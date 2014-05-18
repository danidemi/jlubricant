package com.danidemi.jlubricant.apache.commons.collections;


public class Predicate<T> {

	public boolean evaluate(T object) {
		return false;
	}

	org.apache.commons.collections.Predicate toCommonsPredicate() {
		return new org.apache.commons.collections.Predicate(){

			@Override
			public boolean evaluate(Object object) {
				T item = (T) object;
				return Predicate.this.evaluate(item);
			}
			
		};
	}
	
}
