package jp.co.rakuten.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

public abstract class IterableMap<K extends Comparable<K>,V> implements Map<K,V> , Iterable<Pair<K,V>>{
	protected Set<ComparablePair<K,V>> container;

	@Override
	public Iterator<ComparablePair<K, V>> iterator() {
		return container.iterator();
	}
	@Override
	public void clear() {
		this.container.clear();
	}
	@Override
	public boolean containsKey(Object key) {
		return this.container.contains(key);
	}
	@Override
	public boolean containsValue(Object value) {
		return this.container.containsValue(value);
	}
	@Override
	public Set<java.util.Map.Entry<K, V>> entrySet() {
		return this.container.entrySet();
	}
	@Override
	public V get(Object key) {
		return this.container.get(key);
	}
	@Override
	public boolean isEmpty() {
		return this.container.isEmpty();
	}
	@Override
	public Set<K> keySet() {
		return this.container.keySet();
	}
	@Override
	public V put(K key, V value) {
		return this.container.put(key, value);
	}
	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		this.container.putAll(m);
	}
	@Override
	public V remove(Object key) {
		return this.container.remove(key);
	}
	@Override
	public int size() {
		return this.container.size();
	}
	@Override
	public Collection<V> values() {
		return this.container.values();
	}
}
