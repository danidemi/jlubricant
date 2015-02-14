package com.danidemi.jlubricant.embeddable.jetty;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FeatureSupport implements Iterable<Feature> {

	private List<Feature> features;
	
	public FeatureSupport() {
		this.features = new ArrayList<Feature>();
	}

	public void addFeature(Feature feature) {
		this.features.add(feature);
		
	}

	public List<Feature> getFeatures() {
		return new ArrayList<Feature>(features);
	}

	@Override
	public Iterator<Feature> iterator() {
		return features.iterator();
	}

}
