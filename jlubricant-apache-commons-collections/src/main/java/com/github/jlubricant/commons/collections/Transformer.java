package com.github.jlubricant.commons.collections;

public class Transformer<T,O> {
	
	protected O transform(T input) {
		return null;
	}
	
	org.apache.commons.collections.Transformer toCommonsTransformer() {
		return new org.apache.commons.collections.Transformer() {

			@Override
			public Object transform(Object input) {
				return Transformer.this.transform((T)input);
			}
			
		};
	}

}
