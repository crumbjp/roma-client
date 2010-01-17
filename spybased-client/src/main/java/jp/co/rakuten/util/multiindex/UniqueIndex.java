package jp.co.rakuten.util.multiindex;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.List;

import jp.co.rakuten.util.UnmodifiableMapWrapper;


public abstract class UniqueIndex<K,T> extends UnmodifiableMapWrapper<K,Container<T>> implements Index<T> , Map<K,Container<T>>{
	private Field field;
	protected abstract Map<K,Container<T>> createContainer(List<Container<T>> origin,Integer size); 
	public UniqueIndex(Field field) {
		this.field = field;
	}
	public void opInit   (List<Container<T>> origin,Integer size){
		this.container = createContainer(origin,size);
	}
	@SuppressWarnings("unchecked")
	private K getKey(T t) {
		try {
			return (K)this.field.get(t);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
	public void opAdd(Container<T> c) {
		K key = this.getKey(c.pair.second);
		if ( this.container.put(key, c) != null )
			throw new RuntimeException("ADD : Unique-index is specified conflicting key !");
	}
	public void opRemove(Container<T> c){
		K key = this.getKey(c.pair.second);
		this.container.remove(key);
	}
	public void opModify(Container<T> c, T t){
		K oldKey = this.getKey(c.pair.second);
		K newKey = this.getKey(t);
		if (! oldKey.equals(newKey) ) {
			this.container.remove(oldKey); 
			if ( this.container.put(newKey,c) != null ) 
				throw new RuntimeException("MODIFY : Unique-index is specified conflicting key !");
		}
	}
	public boolean opExist(T t) {
		K key = this.getKey(t);
		return this.container.containsKey(key);
	}
	public boolean opCheckModify(Container<T> c, T t) {
		K oldKey = this.getKey(c.pair.second);
		K newKey = this.getKey(t);
		if (! oldKey.equals(newKey) ) {
			return ! this.containsKey(newKey);
		}
		return true;
	}
}
