package jp.co.rakuten.roma.client;

import javax.naming.LimitExceededException;

import net.spy.memcached.transcoders.Transcoder;

/**
 * Roma extension.
 * 
 * @author hiroaki.kubota@mail.rakuten.co.jp
 *
 * @param <T> Data type of the list
 */
public interface RomaAlist<T> {
	/**
	 * Set data transcoder.
	 * @param tc transcoder
	 */
	public void setTranscoder(Transcoder<Object> tc);
	/**
	 * Set the operation date expire.
	 * @param exp the expiration of this object
	 */
	public void setExpire(long exp);
	/**
	 * Insert the element to first of this list.
	 *   SimpleRomaClient.extension() returns Boolean 
	 * @param val the element-value of the list.
	 * @return returns Roma extension object. Call SimpeRomaClient.extension() method as the argument. 
	 */
	public RomaExtensionAlist prepend(T val);
	/**
	 * Insert the element to first of this list.
	 *   SimpleRomaClient.extension() returns Boolean 
	 * @param val the element-value of the list.
	 * @param limitSize Capacity of the list.
	 * @return returns Roma extension object. Call SimpeRomaClient.extension() method as the argument. 
	 */
	public RomaExtensionAlist prepend(T val,Integer limitSize);
	/**
	 * Insert the element to end of this list. 
	 *   SimpleRomaClient.extension() returns Boolean 
	 * @param val the element-value of the list.
	 * @return returns Roma extension object. Call SimpeRomaClient.extension() method as the argument. 
	 */
	public RomaExtensionAlist append(T val);
	/**
	 * Insert the element to end of this list. 
	 *   SimpleRomaClient.extension() returns Boolean 
	 * @param val the element-value of the list.
	 * @param limitSize Capacity of the list.
	 * @return returns Roma extension object. Call SimpeRomaClient.extension() method as the argument. 
	 */
	public RomaExtensionAlist append(T val,Integer limitSize);
	/**
	 * Insert the element to specific position of this list. 
	 *   SimpleRomaClient.extension() returns Boolean 
	 * @param index position.
	 * @param val the element-value of the list.
	 * @return returns Roma extension object. Call SimpeRomaClient.extension() method as the argument. 
	 */
	public RomaExtensionAlist insert(Integer index,T val);
	/**
	 * Move the element to first of this list. 
	 *   SimpleRomaClient.extension() returns Boolean 
	 * @param val the element of the list.
	 * @return returns Roma extension object. Call SimpeRomaClient.extension() method as the argument. 
	 */
	public RomaExtensionAlist insertOrMoveToFirst(T val);
	/**
	 * Move the element to first of this list. 
	 *   SimpleRomaClient.extension() returns Boolean 
	 * @param val the element of the list.
	 * @param limitSize Capacity of the list.
	 * @return returns Roma extension object. Call SimpeRomaClient.extension() method as the argument. 
	 */
	public RomaExtensionAlist insertOrMoveToFirst(T val,Integer limitSize);
	/**
	 * Move the element to end of this list. 
	 *   SimpleRomaClient.extension() returns Boolean 
	 * @param val the element of the list.
	 * @return returns Roma extension object. Call SimpeRomaClient.extension() method as the argument. 
	 */
	public RomaExtensionAlist insertOrMoveToLast(T val);
	/**
	 * Move the element to end of this list. 
	 *   SimpleRomaClient.extension() returns Boolean 
	 * @param val the element of the list.
	 * @param limitSize Capacity of the list.
	 * @return returns Roma extension object. Call SimpeRomaClient.extension() method as the argument. 
	 */
	public RomaExtensionAlist insertOrMoveToLast(T val,Integer limitSize);
	/**
	 * Remove the element from this list. 
	 *   SimpleRomaClient.extension() returns Boolean 
	 * @param val the element of the list.
	 * @return returns Roma extension object. Call SimpeRomaClient.extension() method as the argument. 
	 */
	public RomaExtensionAlist delete(T val);
	/**
	 * Remove the element from this list. 
	 *   SimpleRomaClient.extension() returns Boolean 
	 * @param index the position of the element.
	 * @return returns Roma extension object. Call SimpeRomaClient.extension() method as the argument. 
	 */
	public RomaExtensionAlist deleteAt(Integer index);
	/**
	 * Remove all elements from this list. 
	 *   SimpleRomaClient.extension() returns Boolean 
	 * @return returns Roma extension object. Call SimpeRomaClient.extension() method as the argument. 
	 */
	public RomaExtensionAlist deleteAll();
	/**
	 * Get all elements in this list. 
	 *   SimpleRomaClient.extension() returns List<T> 
	 * @return returns Roma extension object. Call SimpeRomaClient.extension() method as the argument. 
	 */
	public RomaExtensionAlist getAll();
	/**
	 * Get all elements in this list. 
	 *   SimpleRomaClient.extension() returns List<RomaAlistEntry> 
	 * @return returns Roma extension object. Call SimpeRomaClient.extension() method as the argument. 
	 */
	public RomaExtensionAlist getEntryAll();
	/**
	 * Get the element in this list. 
	 *   SimpleRomaClient.extension() returns T
	 * @return returns Roma extension object. Call SimpeRomaClient.extension() method as the argument. 
	 */
	public RomaExtensionAlist get(Integer index);
	/**
	 * Get some elements in this list. 
	 *   SimpleRomaClient.extension() returns List<T> 
	 * @return returns Roma extension object. Call SimpeRomaClient.extension() method as the argument. 
	 */
	public RomaExtensionAlist getSubList(Integer index,Integer len);
	/**
	 * Get some elements in this list. 
	 *   SimpleRomaClient.extension() returns List<RomaAlistEntry> 
	 * @return returns Roma extension object. Call SimpeRomaClient.extension() method as the argument. 
	 */
	public RomaExtensionAlist getEntrySubList(Integer index,Integer len);
	/**
	 * Get the number of elements in this list. 
	 *   SimpleRomaClient.extension() returns Integer
	 * @return returns Roma extension object. Call SimpeRomaClient.extension() method as the argument. 
	 */
	public RomaExtensionAlist size();
	/**
	 * Get and delete the element in this list. 
	 *   SimpleRomaClient.extension() returns T
	 * @return returns Roma extension object. Call SimpeRomaClient.extension() method as the argument. 
	 */
	public RomaExtensionAlist getAndDeleteFirst();
	/**
	 * Get and delete the element in this list. 
	 *   SimpleRomaClient.extension() returns T
	 * @return returns Roma extension object. Call SimpeRomaClient.extension() method as the argument. 
	 */
	public RomaExtensionAlist getAndDeleteLast();
	/**
	 * RomaAlist inner data structure.  
	 * @author hiroaki.kubota@mail.rakuten.co.jp
	 *
	 * @param <T> Data type of the list
	 */
	public static interface RomaAlistEntry<T> {
		/**
		 * Value getter.
		 * @return returns the value of element.
		 */
		public T getValue();
		/**
		 * Time of insertion getter.
		 * @return returns the time of insertion the data.
		 */
		public Long getTime();
	}
}
