package com.github.jlubricant.commons.collections;

public class Closure<T> {
	
	public void execute(T input) {
		
	}

	org.apache.commons.collections.Closure toCommonsClosure(){
		return new org.apache.commons.collections.Closure(){

			@SuppressWarnings("unchecked")
			@Override
			public void execute(Object input) {
				Closure.this.execute((T)input);
			}
			
		};
	}
	
}
