package jp.co.rakuten.util.multiindex;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class TreedUniqueIndex<K,T> extends UniqueIndex<K,T>{
	public TreedUniqueIndex(Field field) {
		super(field);
	}
	@Override
	protected Map<K, Container<T>> createContainer(List<Container<T>> origin,Integer size) {
		return new TreeMap<K,Container<T>>();
	}
}
