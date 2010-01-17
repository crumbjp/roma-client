package jp.co.rakuten.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public abstract class UnmodifiableSetWrapper<T> implements Set<T>{
	protected Set<T> container;
	@Override
	public boolean add(T e) {
		return this.container.add(e);
	}
	@Override
	public boolean addAll(Collection<? extends T> c) {
		throw new UnsupportedOperationException("Operation has not been permitted .");
	}
	@Override
	public void clear() {
		throw new UnsupportedOperationException("Operation has not been permitted .");
	}
	@Override
	public boolean contains(Object o) {
		return this.container.contains(o);
	}
	@Override
	public boolean containsAll(Collection<?> c) {
		return this.container.containsAll(c);
	}
	@Override
	public boolean isEmpty() {
		return this.container.isEmpty();
	}
	@Override
	public Iterator<T> iterator() {
		return this.container.iterator();
	}
	@Override
	public boolean remove(Object o) {
		throw new UnsupportedOperationException("Operation has not been permitted .");
	}
	@Override
	public boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException("Operation has not been permitted .");
	}
	@Override
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException("Operation has not been permitted .");
	}
	@Override
	public int size() {
		return this.container.size();
	}
	@Override
	public Object[] toArray() {
		return this.container.toArray();
	}
	@Override
	public <T> T[] toArray(T[] a) {
		return this.container.toArray(a);
	};
}
