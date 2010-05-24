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
	 * <pre>
	 * Insert the element to first of this alist.
	 *   SimpleRomaClient.extension() returns Boolean
	 * </pre> 
	 * @param val the element-value of the alist.
	 * @return returns Roma extension object. Call SimpeRomaClient.extension() method as the argument. 
	 */
	public RomaExtensionAlist prepend(T val);
	/**
	 * <pre>
	 * Insert the element to first of this alist.
	 *   SimpleRomaClient.extension() returns Boolean<br>
	 * The list-size becomes the specified limit-size 
	 *  even if highest by cutting tails when this function returned. 
	 * </pre> 
	 * @param val the element-value of the alist.
	 * @param limitSize size of the alist.
	 * @return returns Roma extension object. Call SimpeRomaClient.extension() method as the argument. 
	 */
	public RomaExtensionAlist prepend(T val,Integer limitSize);
	/**
	 * <pre>
	 * Insert the element to end of this alist. 
	 *   SimpleRomaClient.extension() returns Boolean 
	 * </pre> 
	 * @param val the element-value of the alist.
	 * @return returns Roma extension object. Call SimpeRomaClient.extension() method as the argument. 
	 */
	public RomaExtensionAlist append(T val);
	/**
	 * <pre>
	 * Insert the element to end of this alist. 
	 *   SimpleRomaClient.extension() returns Boolean 
	 * The list-size becomes the specified limit-size 
	 *  even if highest by cutting tails when this function returned. 
	 * </pre> 
	 * @param val the element-value of the alist.
	 * @param limitSize size of the alist.
	 * @return returns Roma extension object. Call SimpeRomaClient.extension() method as the argument. 
	 */
	public RomaExtensionAlist append(T val,Integer limitSize);
	/**
	 * <pre>
	 * Insert the element to specific position of this alist. 
	 *   SimpleRomaClient.extension() returns Boolean 
	 * </pre> 
	 * @param index position.
	 * @param val the element-value of the alist.
	 * @return returns Roma extension object. Call SimpeRomaClient.extension() method as the argument. 
	 */
	public RomaExtensionAlist insert(Integer index,T val);
	/**
	 * <pre>
	 * Move the element to first of this alist. 
	 *   SimpleRomaClient.extension() returns Boolean 
	 * </pre> 
	 * @param val the element of the alist.
	 * @return returns Roma extension object. Call SimpeRomaClient.extension() method as the argument. 
	 */
	public RomaExtensionAlist insertOrMoveToFirst(T val);
	/**
	 * <pre>
	 * Insert the element to end of this alist. 
	 *   SimpleRomaClient.extension() returns Boolean 
	 * </pre> 
	 * @param val the element-value of the alist.
	 * @param expire The expire-time of micro second.
	 * @return returns Roma extension object. Call SimpeRomaClient.extension() method as the argument. 
	 */
	public RomaExtensionAlist insertOrMoveToFirst(T val,Long expire);
	/**
	 * <pre>
	 * Move the element to first of this alist. 
	 *   SimpleRomaClient.extension() returns Boolean 
	 * The list-size becomes the specified limit-size 
	 *  even if highest by cutting tails when this function returned. 
	 * </pre> 
	 * @param val the element of the alist.
	 * @param limitSize Capacity of the alist.
	 * @return returns Roma extension object. Call SimpeRomaClient.extension() method as the argument. 
	 */
	public RomaExtensionAlist insertOrMoveToFirst(T val,Integer limitSize);
	/**
	 * <pre>
	 * Move the element to first of this alist. 
	 *   SimpleRomaClient.extension() returns Boolean 
	 * The list-size becomes the specified limit-size 
	 *  even if highest by cutting tails when this function returned. 
	 * </pre> 
	 * @param val the element-value of the alist.
	 * @param limitSize size of the alist.
	 * @param expire The expire-time of micro second.
	 * @return returns Roma extension object. Call SimpeRomaClient.extension() method as the argument. 
	 */
	public RomaExtensionAlist insertOrMoveToFirst(T val,Integer limitSize,Long expire);
	/**
	 * <pre>
	 * Move the element to end of this alist. 
	 *   SimpleRomaClient.extension() returns Boolean 
	 * </pre> 
	 * @param val the element of the alist.
	 * @return returns Roma extension object. Call SimpeRomaClient.extension() method as the argument. 
	 */
	public RomaExtensionAlist insertOrMoveToLast(T val);
	/**
	 * <pre>
	 * Move the element to end of this alist. 
	 *   SimpleRomaClient.extension() returns Boolean 
	 * </pre> 
	 * @param val the element-value of the alist.
	 * @param expire The expire-time of micro second.
	 * @return returns Roma extension object. Call SimpeRomaClient.extension() method as the argument. 
	 */
	public RomaExtensionAlist insertOrMoveToLast(T val,Long expire);
	/**
	 * <pre>
	 * Move the element to end of this alist. 
	 *   SimpleRomaClient.extension() returns Boolean 
	 * </pre> 
	 * @param val the element of the alist.
	 * @param limitSize Capacity of the alist.
	 * @return returns Roma extension object. Call SimpeRomaClient.extension() method as the argument. 
	 */
	public RomaExtensionAlist insertOrMoveToLast(T val,Integer limitSize);
	/**
	 * <pre>
	 * Move the element to end of this alist. 
	 *   SimpleRomaClient.extension() returns Boolean 
	 * The list-size becomes the specified limit-size 
	 *  even if highest by cutting tails when this function returned. 
	 * </pre> 
	 * @param val the element-value of the alist.
	 * @param limitSize size of the alist.
	 * @param expire The expire-time of micro second.
	 * @return returns Roma extension object. Call SimpeRomaClient.extension() method as the argument. 
	 */
	public RomaExtensionAlist insertOrMoveToLast(T val,Integer limitSize,Long expire);
	/**
	 * <pre>
	 * Remove the element from this alist. 
	 *   SimpleRomaClient.extension() returns Boolean 
	 * </pre> 
	 * @param val the element of the alist.
	 * @return returns Roma extension object. Call SimpeRomaClient.extension() method as the argument. 
	 */
	public RomaExtensionAlist delete(T val);
	/**
	 * Remove the element from this alist. 
	 *   SimpleRomaClient.extension() returns Boolean 
	 * @param index the position of the element.
	 * @return returns Roma extension object. Call SimpeRomaClient.extension() method as the argument. 
	 */
	public RomaExtensionAlist deleteAt(Integer index);
	/**
	 * Remove all elements from this alist. 
	 *   SimpleRomaClient.extension() returns Boolean 
	 * @return returns Roma extension object. Call SimpeRomaClient.extension() method as the argument. 
	 */
	public RomaExtensionAlist deleteAll();
	/**
	 * Get all elements in this alist. 
	 *   SimpleRomaClient.extension() returns List<T> 
	 * @return returns Roma extension object. Call SimpeRomaClient.extension() method as the argument. 
	 */
	public RomaExtensionAlist getAll();
	/**
	 * Get all elements in this alist. 
	 *   SimpleRomaClient.extension() returns List<RomaAlistEntry> 
	 * @return returns Roma extension object. Call SimpeRomaClient.extension() method as the argument. 
	 */
	public RomaExtensionAlist getEntryAll();
	/**
	 * Get the element in this alist. 
	 *   SimpleRomaClient.extension() returns T
	 * @return returns Roma extension object. Call SimpeRomaClient.extension() method as the argument. 
	 */
	public RomaExtensionAlist get(Integer index);
	/**
	 * Get some elements in this alist. 
	 *   SimpleRomaClient.extension() returns List<T> 
	 * @return returns Roma extension object. Call SimpeRomaClient.extension() method as the argument. 
	 */
	public RomaExtensionAlist getSubList(Integer index,Integer len);
	/**
	 * Get some elements in this alist. 
	 *   SimpleRomaClient.extension() returns List<RomaAlistEntry> 
	 * @return returns Roma extension object. Call SimpeRomaClient.extension() method as the argument. 
	 */
	public RomaExtensionAlist getEntrySubList(Integer index,Integer len);
	/**
	 * Get the number of elements in this alist. 
	 *   SimpleRomaClient.extension() returns Integer
	 * @return returns Roma extension object. Call SimpeRomaClient.extension() method as the argument. 
	 */
	public RomaExtensionAlist size();
	/**
	 * Get and delete the element in this alist. 
	 *   SimpleRomaClient.extension() returns T
	 * @return returns Roma extension object. Call SimpeRomaClient.extension() method as the argument. 
	 */
	public RomaExtensionAlist getAndDeleteFirst();
	/**
	 * Get and delete the element in this alist. 
	 *   SimpleRomaClient.extension() returns T
	 * @return returns Roma extension object. Call SimpeRomaClient.extension() method as the argument. 
	 */
	public RomaExtensionAlist getAndDeleteLast();
	/**
	 * RomaAlist inner data structure.  
	 * @author hiroaki.kubota@mail.rakuten.co.jp
	 *
	 * @param <T> Data type of the alist
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
