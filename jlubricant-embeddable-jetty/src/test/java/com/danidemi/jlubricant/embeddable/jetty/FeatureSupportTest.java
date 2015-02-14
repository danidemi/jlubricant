package com.danidemi.jlubricant.embeddable.jetty;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import java.util.Iterator;

import org.hamcrest.core.IsNull;
import org.junit.Test;


public class FeatureSupportTest {

	@Test public void shouldAllowAddingFeatures() {
		
		// given
		FeatureSupport tested = new FeatureSupport();
		Feature feature = mock(Feature.class);
		
		// when
		tested.addFeature( feature );
		
		// then
		assertThat( tested.getFeatures() , contains(feature));
		
	}
	
	@Test public void shouldBeIterable() {
		
		// given
		FeatureSupport tested = new FeatureSupport();
		Feature feature1 = mock(Feature.class);
		Feature feature2 = mock(Feature.class);
		Feature feature3 = mock(Feature.class);
		
		// when
		tested.addFeature( feature1 );
		tested.addFeature( feature2 );
		tested.addFeature( feature3 );
		
		// then
		Iterator<Feature> iterator = tested.iterator();
		assertThat( iterator.next() , is(feature1));		
		assertThat( iterator.next() , is(feature2));
		assertThat( iterator.next() , is(feature3));
		assertThat( iterator.hasNext() , is(false));
		
	}
	
}
