package com.danidemi.jlubricant.commons.collections;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.danidemi.jlubricant.commons.collections.CollectionUtils;
import com.danidemi.jlubricant.commons.collections.Predicate;

public class CollectionsUtilsTest {
		
	@Test
	public void filter() {
		
		List<String> items = new ArrayList<String>( Arrays.asList(new String[]{"Ares", "Atlas", "Bert"}) );
		
		CollectionUtils.filter(items, new Predicate<String>() {
			
			@Override
			public boolean evaluate(String arg0) {
				return arg0.startsWith("A");
			}
		});
		
		assertThat( items, contains("Ares", "Atlas"));
		assertThat( items, not(contains("Bert")));
				
	}	

}
