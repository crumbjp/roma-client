package jp.co.rakuten.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.TreeSet;

class MultiSetIterator<T extends Comparable<T>> implements Iterator<T> {
	private final MultiSet<T> origin;
	private final Iterator<List<T>> iterator;
	private Integer currentCounter = 0;
	private List<T> currentList = null;
	enum Order{
		ASC,
		DESC
	}
	private final Order order;
	public MultiSetIterator(MultiSet<T> origin , Iterator<List<T> > iterator , Order order) {
		this.origin = origin;
		this.iterator = iterator;
		this.order = order;
	}
	@Override
	public boolean hasNext() {
		if ( order == Order.ASC ) { 
			if ( (currentList != null && ( currentList.size() > (currentCounter+1))) || 
					iterator.hasNext()	){
				return true;
			}
			return false;
		}else {
			if ( (currentList != null && ( currentCounter > 0)) || 
					iterator.hasNext()	){
				return true;
			}
			return false;
		}
	}
	@Override
	public T next() {
		if ( order == Order.ASC ) { 
			if ( currentList != null && ( currentList.size() > (currentCounter+1) ) ) {
				currentCounter++;
			}else {
				currentList = iterator.next();
				currentCounter = 0;
			}
			return currentList.get(currentCounter);
		}else {
			if ( currentList != null && ( currentCounter > 0 )) {
				currentCounter--;
			}else {
				currentList = iterator.next();
				currentCounter = currentList.size() - 1;
			}
			return currentList.get(currentCounter);
		}
	}
	@Override
	public void remove() {
		if ( currentList != null ) {
			currentList.remove(currentCounter.intValue());
			if ( order == Order.ASC) {
				currentCounter--;
			} else { 
				;
			} 
			if ( currentList.size() == 0 ) {
				iterator.remove();
				currentList = null;
				currentCounter = 0;
			}
			origin.size--;
		}
	}
	
}

public class MultiSet<T extends Comparable<T> > implements Iterable<T> {
	static class TempList<T extends Comparable<T>> implements List<T>{
		private T e;
		public TempList(T e) {
			this.e = e;
		}
		@Override
		public T get(int index) {
			return e;
		}
		@Override
		public int indexOf(Object o) {
			return 0;
		}
		@Override
		public int size() {
			return 1;
		}
		@Override
		public int lastIndexOf(Object o) {
			return 0;
		}
		@Override
		public boolean add(T e) {
			throw new UnsupportedOperationException("Operation has not been permitted .");
		}
		@Override
		public void add(int index, T element) {
			throw new UnsupportedOperationException("Operation has not been permitted .");
		}
		@Override
		public boolean addAll(Collection<? extends T> c) {
			throw new UnsupportedOperationException("Operation has not been permitted .");
		}
		@Override
		public boolean addAll(int index, Collection<? extends T> c) {
			throw new UnsupportedOperationException("Operation has not been permitted .");
		}
		@Override
		public void clear() {
			throw new UnsupportedOperationException("Operation has not been permitted .");
		}
		@Override
		public boolean contains(Object o) {
			throw new UnsupportedOperationException("Operation has not been permitted .");
		}
		@Override
		public boolean containsAll(Collection<?> c) {
			throw new UnsupportedOperationException("Operation has not been permitted .");
		}
		@Override
		public boolean isEmpty() {
			return false;
		}
		@Override
		public Iterator<T> iterator() {
			throw new UnsupportedOperationException("Operation has not been permitted .");
		}
		@Override
		public ListIterator<T> listIterator() {
			throw new UnsupportedOperationException("Operation has not been permitted .");
		}
		@Override
		public ListIterator<T> listIterator(int index) {
			throw new UnsupportedOperationException("Operation has not been permitted .");
		}
		@Override
		public boolean remove(Object o) {
			throw new UnsupportedOperationException("Operation has not been permitted .");
		}
		@Override
		public T remove(int index) {
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
		public T set(int index, T element) {
			throw new UnsupportedOperationException("Operation has not been permitted .");
		}
		@Override
		public List<T> subList(int fromIndex, int toIndex) {
			throw new UnsupportedOperationException("Operation has not been permitted .");
		}
		@Override
		public Object[] toArray() {
			throw new UnsupportedOperationException("Operation has not been permitted .");
		}
		@Override
		public <T> T[] toArray(T[] a) {
			throw new UnsupportedOperationException("Operation has not been permitted .");
		}
		
	}
	final TreeSet<List<T>> container = new TreeSet<List<T>> (new Comparator<List<T>>(){
		@Override
		public int compare(List<T> o1, List<T> o2) {
			return o1.get(0).compareTo(o2.get(0));
		}
	});
	int size = 0;

	@Override
	public Iterator<T> iterator() {
		return new MultiSetIterator<T>(this,container.iterator(),MultiSetIterator.Order.ASC);
	}
	public Iterator<T> descendingIterator() {
		return new MultiSetIterator<T>(this,container.descendingIterator(),MultiSetIterator.Order.DESC);
	}


	public int size() {
		return size;
	}

	
	public void clear() {
		this.container.clear();
		this.size = 0;
	}

	public boolean contains(Object e) {
		return this.container.contains(new TempList<T>((T)e));
	}
	public boolean add(T e) {
		TempList<T> t = new TempList<T>((T)e);
		List<T> d ;
		if ( this.container.contains(t) ) {
			d = this.container.ceiling(t);
			d.add(e);
		}else {
			d = new ArrayList<T>();
			d.add(e);
			this.container.add(d);
		}
		size++;
		return true;
	}
	public boolean addAll(Collection<? extends T> c) {
		// Todo: @@@ more effective !!!
		for ( T e : c ) {
			this.add(e);
		}
		return true;
	}
	public boolean containsAll(Collection<?> c) {
		// Todo: @@@ more effective !!!
		for ( T e : (Collection<? extends T>) c ) {
			if ( ! this.contains(e) ) {
				return false;
			}
		}
		return true;
	}
	public T first() {
		List<T> d = this.container.first(); 
		return (d == null)?null:d.get(0);
	}
	public T last() {
		List<T> d = this.container.last(); 
		return (d == null)?null:d.get(d.size()-1);
	}
	public T ceiling(T e) {
		TempList<T> t = new TempList<T>((T)e);
		List<T> d = this.container.ceiling(t);
		return (d == null)?null:d.get(0);
	};
	public T floor(T e) {
		TempList<T> t = new TempList<T>((T)e);
		List<T> d = this.container.floor(t); 
		return (d == null)?null:d.get(d.size()-1);
	};
	public T higher(T e) {
		TempList<T> t = new TempList<T>((T)e);
		List<T> d = this.container.higher(t);
		return (d == null)?null:d.get(0);
	};
	public T lower(T e) {
		TempList<T> t = new TempList<T>((T)e);
		List<T> d = this.container.lower(t);
		return (d == null)?null:d.get(d.size()-1);
	};
	public boolean isEmpty() {
		return this.container.isEmpty();
	}
	public T pollFirst() {
		List<T> d = this.container.first();
		if (d.size() == 1 ){
			return this.container.pollFirst().get(0);
		} else {
			return d.remove(0);
		}
	}
	public T pollLast() {
		List<T> d = this.container.last();
		if (d.size() == 1 ){
			return this.container.pollLast().get(0);
		} else {
			return d.remove(d.size()-1);
		}
	};
	public void remove(MultiSetIterator<T> it) {
		it.remove();
	}
	public Iterator<T> equalRange( T e ) {
		// Todo: @@@ more effective !!!
		TempList<T> t = new TempList<T>((T)e);
		return new MultiSetIterator<T>(this,this.container.subSet(t,true, t,true).iterator(),MultiSetIterator.Order.ASC);
	}
	public Iterator<T> equalRange( T e1 , T e2) {
		TempList<T> t1 = new TempList<T>((T)e1);
		TempList<T> t2 = new TempList<T>((T)e2);
		return new MultiSetIterator<T>(this,this.container.subSet(t1,true, t2,true).iterator(),MultiSetIterator.Order.ASC);
	}
	
	public static void main (String args[]) {
		TreeSet<Integer> ms = new TreeSet<Integer>();
		ms.add(1);
		ms.add(2);
		ms.add(3);
		ms.add(4);
		ms.add(5);
		for ( Integer i : ms ) {
			System.out.println(i);
		}
		System.out.println("----");
		Iterator<Integer> it = ms.iterator();
		it.next();
		it.next();
		it.remove();
		System.out.println(it.next());
		it.next();
		it.remove();
		System.out.println("----");
		for ( Integer i : ms ) {
			System.out.println(i);
		}
	}
}
