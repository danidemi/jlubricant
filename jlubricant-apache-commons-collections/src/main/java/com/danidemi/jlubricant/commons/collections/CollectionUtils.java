package com.danidemi.jlubricant.commons.collections;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;

public class CollectionUtils {

	@SuppressWarnings("unchecked")
	public static <T> Collection<T> union(final Collection<T> a,
			final Collection<T> b) {
		return org.apache.commons.collections.CollectionUtils.union(a, b);
	}

	public static <T> Collection<T> intersection(final Collection<T> a, final Collection<T> b) {
		return org.apache.commons.collections.CollectionUtils.intersection(a, b);
	}

	public static <T> Collection<T> disjunction(final Collection<T> a, final Collection<T> b) {
		return org.apache.commons.collections.CollectionUtils.disjunction(a, b);
	}

	public static <T> Collection<T> subtract(final Collection<T> a, final Collection<T> b) {
		return org.apache.commons.collections.CollectionUtils.subtract(a, b);
	}

	public static <T> boolean containsAny(final Collection<T> coll1,final Collection<T> coll2) {
		return org.apache.commons.collections.CollectionUtils.containsAny(coll1, coll2);
	}

	public static <T> Map<T, Integer> getCardinalityMap(final Collection<T> coll) {
		return org.apache.commons.collections.CollectionUtils.getCardinalityMap(coll);
	}

	public static <T> boolean isSubCollection(final Collection<T> a, final Collection<T> b) {
		return org.apache.commons.collections.CollectionUtils.isSubCollection(a, b);
	}

	public static <T> boolean isProperSubCollection(final Collection<T> a,
			final Collection<T> b) {
		return org.apache.commons.collections.CollectionUtils.isProperSubCollection(a, b);
	}

	public static <T> boolean isEqualCollection(final Collection<T> a,
			final Collection<T> b) {
		return org.apache.commons.collections.CollectionUtils.isEqualCollection(a, b);
	}

	public static <T> int cardinality(T obj, final Collection<T> coll) {
		return org.apache.commons.collections.CollectionUtils.cardinality(obj, coll);
	}

	@SuppressWarnings("unchecked")
	public static <T> T find(Collection<T> collection, Predicate<T> predicate) {
		return (T) org.apache.commons.collections.CollectionUtils.find(collection, predicate.toCommonsPredicate());
	}

	public static <T> void forAllDo(Collection<T> collection, Closure<T> closure) {
		org.apache.commons.collections.CollectionUtils.forAllDo(collection, closure.toCommonsClosure());
	}

	public static <T> void filter(Collection<T> items, Predicate<T> predicate) {
		org.apache.commons.collections.CollectionUtils.filter(items,
				predicate.toCommonsPredicate());
	}

	public static <I,O> void transform(Collection<I> collection, Transformer<I,O> transformer) {
		org.apache.commons.collections.CollectionUtils.transformedCollection(collection, transformer.toCommonsTransformer());
	}

	public static <T> int countMatches(Collection<T> inputCollection,
			Predicate<T> predicate) {
		return org.apache.commons.collections.CollectionUtils.countMatches(inputCollection, predicate.toCommonsPredicate());
	}

	public static <T> boolean exists(Collection<T> collection, Predicate<T> predicate) {
		return org.apache.commons.collections.CollectionUtils.exists(collection, predicate.toCommonsPredicate());
	}

	@SuppressWarnings("unchecked")
	public static <T> Collection<T> select(Collection<T> inputCollection,
			Predicate<T> predicate) {
		return org.apache.commons.collections.CollectionUtils.select(inputCollection, predicate.toCommonsPredicate());
	}

	public static <T> void select(Collection<T> inputCollection, Predicate<T> predicate,
			Collection<T> outputCollection) {
		org.apache.commons.collections.CollectionUtils.select(inputCollection, predicate.toCommonsPredicate());
	}

	public static <T> Collection<T> selectRejected(Collection<T> inputCollection,
			Predicate<T> predicate) {
		return org.apache.commons.collections.CollectionUtils.selectRejected(inputCollection, predicate.toCommonsPredicate());
	}

	public static <T> void selectRejected(Collection<T> inputCollection,
			Predicate<T> predicate, Collection<T> outputCollection) {
		org.apache.commons.collections.CollectionUtils.selectRejected(inputCollection, predicate.toCommonsPredicate());
	}

	public static <I,O> Collection<O> collect(Collection<I> inputCollection,
			Transformer<I,O> transformer) {
		return org.apache.commons.collections.CollectionUtils.collect(inputCollection, transformer.toCommonsTransformer());
	}

//	public static <I,O> Collection<O> collect(Iterator<I> inputIterator,
//			Transformer<I,O> transformer) {
//		throw new UnsupportedOperationException();
//	}

	public static <I,O> Collection<O> collect(Collection<I> inputCollection,
			final Transformer<I,O> transformer, final Collection<O> outputCollection) {
		return org.apache.commons.collections.CollectionUtils.collect(inputCollection, transformer.toCommonsTransformer());
	}

//	public static <I,O> Collection<I,O> collect(Iterator<I> inputIterator,
//			final Transformer transformer<I,O>, final Collection<O> outputCollection) {
//		throw new UnsupportedOperationException();
//	}

	public static <T> boolean addIgnoreNull(Collection<T> collection, T object) {
		return org.apache.commons.collections.CollectionUtils.addIgnoreNull(collection, object);
	}

	public static <T> void addAll(Collection<T> collection, Iterator<T> iterator) {
		org.apache.commons.collections.CollectionUtils.addAll(collection, iterator);
	}

	public static <T> void addAll(Collection<T> collection, Enumeration<T> enumeration) {
		org.apache.commons.collections.CollectionUtils.addAll(collection, enumeration);
	}

	public static <T> void addAll(Collection<T> collection, T[] elements) {
		org.apache.commons.collections.CollectionUtils.addAll(collection, elements);
	}

	private static <T> T index(Iterator<T> iterator, int idx) {
		return (T) org.apache.commons.collections.CollectionUtils.index(iterator, idx);
	}

	public static <T> T get(T object, int index) {
		return (T) org.apache.commons.collections.CollectionUtils.get(object, index);
	}

	public static int size(Object object) {
		return org.apache.commons.collections.CollectionUtils.size(object);
	}

	public static boolean sizeIsEmpty(Object object) {
		return org.apache.commons.collections.CollectionUtils.sizeIsEmpty(object);
	}

	public static <T> boolean isEmpty(Collection<T> coll) {
		return org.apache.commons.collections.CollectionUtils.isEmpty(coll);
	}

	public static <T> boolean isNotEmpty(Collection<T> coll) {
		return org.apache.commons.collections.CollectionUtils.isNotEmpty(coll);
	}

	public static <T> void reverseArray(T[] array) {
		org.apache.commons.collections.CollectionUtils.reverseArray(array);
	}
	
	public static <T> boolean isFull(Collection<T> coll) {
		return org.apache.commons.collections.CollectionUtils.isFull(coll);
	}

	public static <T> int maxSize(Collection<T> coll) {
		return org.apache.commons.collections.CollectionUtils.maxSize(coll);
	}

	public static <T> Collection retainAll(Collection<T> collection, Collection<T> retain) {
		return org.apache.commons.collections.CollectionUtils.retainAll(collection, retain);
	}

	public static <T> Collection removeAll(Collection<T> collection, Collection<T> remove) {
		return org.apache.commons.collections.CollectionUtils.removeAll(collection, remove);
	}

	public static <T> Collection<T> synchronizedCollection(Collection<T> collection) {
		return org.apache.commons.collections.CollectionUtils.synchronizedCollection(collection);
	}

	public static <T> Collection<T> unmodifiableCollection(Collection<T> collection) {
		return org.apache.commons.collections.CollectionUtils.unmodifiableCollection(collection);
	}

	public static <T> Collection predicatedCollection(Collection<T> collection,
			Predicate<T> predicate) {
		return org.apache.commons.collections.CollectionUtils.predicatedCollection(collection, predicate.toCommonsPredicate());
	}

//	public static <T> Collection<T> typedCollection(Collection<T> collection, Class type) {
//		throw new UnsupportedOperationException();
//	}

	public static <I,O> Collection transformedCollection(Collection<I> collection, Transformer<I,O> transformer) {
		return org.apache.commons.collections.CollectionUtils.transformedCollection(collection, transformer.toCommonsTransformer());
	}

}