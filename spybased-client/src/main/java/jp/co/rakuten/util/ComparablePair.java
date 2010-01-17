package jp.co.rakuten.util;

public class ComparablePair<K extends Comparable<K>,V> extends Pair<K,V> implements Comparable<ComparablePair<K,V>>{
	public ComparablePair(K k,V v) {
		super(k,v);
	}
	@Override
	public boolean equals(Object o) {
		return this.first.equals(((ComparablePair<K,V>)o).first);
	}
	public int compareTo(ComparablePair<K, V> o) {
		return this.first.compareTo(((ComparablePair<K,V>)o).first);
	};
}
