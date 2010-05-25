package jp.co.rakuten.roma.client;

import java.util.Collection;
import java.util.Map;

import net.spy.memcached.CASResponse;
import net.spy.memcached.CASValue;
import net.spy.memcached.transcoders.Transcoder;

public interface SimpleRomaClient {
	
	/**
	 * Get the default transcoder that's in use.
	 */
	Transcoder<Object> getTranscoder();
	
	/**
	 * Get mklhash (Roma extention operation).
	 *
	 * @return Returns roma-mklhash 
	 */
	String mklhash();
	/**
	 * Get mklhash (Roma extention operation).
	 * 
	 * @param timeout operationTimeout (msec)
	 * @return Returns roma-mklhash 
	 */
	String mklhash(long timeout);
	/**
	 * Get routingdump (Roma extention operation).
	 *
	 * @return Returns roma-routingdump 
	 */
	String routingdump();
	/**
	 * Get routingdump (Roma extention operation).
	 *
	 * @param timeout operationTimeout (msec)
	 * @return Returns roma-routingdump 
	 */
	String routingdump(long timeout);
	/**
	 * Reconstruction node-info (Roma extention operation).
	 *
	 * @return returns True if success
	 */
	void reconstruction();
	/**
	 * Reconstruction node-info (Roma extention operation).
	 *
	 * @param timeout operationTimeout (msec)
	 */
	void reconstruction(long timeout);
	
	/**
	 * Do an roma-extention command.
	 * 
	 * @param extention Roma extention function
	 * @return extention-result.
	 */
	Object extension(RomaExtension extention);
	/**
	 * Append to an existing value in the cache.
	 *
	 * @param cas cas identifier (ignored in the ascii protocol)
	 * @param key the key to whose value will be appended
	 * @param val the value to append
	 * @return returns True if success
	 * @throws IllegalStateException in the rare circumstance where queue
	 *         is too full to accept any more requests
	 *         
	 * @Deprecated The cas value is ignored on ascii protocol.
	 */
	Boolean append(long cas, String key, Object val);
	/**
	 * Append to an existing value in the cache.
	 *
	 * @param cas cas identifier (ignored in the ascii protocol)
	 * @param key the key to whose value will be appended
	 * @param val the value to append
	 * @param tc the transcoder to serialize and unserialize the value
	 * @return returns True if success
	 * @throws IllegalStateException in the rare circumstance where queue
	 *         is too full to accept any more requests
	 *         
	 * @Deprecated The cas value is ignored on ascii protocol.
	 */
	Boolean append(long cas, String key, Object val, long timeout);
	/**
	 * Prepend to an existing value in the cache.
	 *
	 * @param cas cas identifier (ignored in the ascii protocol)
	 * @param key the key to whose value will be prepended
	 * @param val the value to append
	 * @return returns True if success
	 * @throws IllegalStateException in the rare circumstance where queue
	 *         is too full to accept any more requests
	 *         
	 * @Deprecated The cas value is ignored on ascii protocol.
	 */
	Boolean prepend(long cas, String key, Object val);
	/**
	 * Prepend to an existing value in the cache.
	 *
	 * @param cas cas identifier (ignored in the ascii protocol)
	 * @param key the key to whose value will be prepended
	 * @param val the value to append
	 * @param timeout operationTimeout (msec)
	 * @return returns True if success
	 * @throws IllegalStateException in the rare circumstance where queue
	 *         is too full to accept any more requests
	 *         
	 * @Deprecated The cas value is ignored on ascii protocol.
	 */
	Boolean prepend(long cas, String key, Object val, long timeout);
	/**
	 * Perform a synchronous CAS operation with the default transcoder.
	 *
	 * @param key the key
	 * @param casId the CAS identifier (from a gets operation)
	 * @param value the new value
	 * @return a CASResponse
	 * @throws OperationTimeoutException if the global operation timeout is
	 *		   exceeded
	 * @throws IllegalStateException in the rare circumstance where queue
	 *         is too full to accept any more requests
	 */
	CASResponse cas(String key, long casId, Object value);
	/**
	 * Perform a synchronous CAS operation with the default transcoder.
	 *
	 * @param key the key
	 * @param casId the CAS identifier (from a gets operation)
	 * @param value the new value
	 * @param timeout operationTimeout (msec)
	 * @return a CASResponse
	 * @throws OperationTimeoutException if the global operation timeout is
	 *		   exceeded
	 * @throws IllegalStateException in the rare circumstance where queue
	 *         is too full to accept any more requests
	 */
	CASResponse cas(String key, long casId, Object value, long timeout);
	/**
	 * Add an object to the cache (using the default transcoder)
	 * iff it does not exist already.
	 *
	 * <p>
	 * The <code>exp</code> value is passed along to memcached exactly as
	 * given, and will be processed per the memcached protocol specification:
	 * </p>
	 *
	 * <blockquote>
	 * <p>
	 * The actual value sent may either be
	 * Unix time (number of seconds since January 1, 1970, as a 32-bit
	 * value), or a number of seconds starting from current time. In the
	 * latter case, this number of seconds may not exceed 60*60*24*30 (number
	 * of seconds in 30 days); if the number sent by a client is larger than
	 * that, the server will consider it to be real Unix time value rather
	 * than an offset from current time.
	 * </p>
	 * </blockquote>
	 *
	 * @param key the key under which this object should be added.
	 * @param exp the expiration of this object
	 * @param o the object to store
	 * @return returns True if success
	 * @throws IllegalStateException in the rare circumstance where queue
	 *         is too full to accept any more requests
	 */
	Boolean add(String key, int exp, Object o);
	/**
	 * Add an object to the cache (using the default transcoder)
	 * iff it does not exist already.
	 *
	 * <p>
	 * The <code>exp</code> value is passed along to memcached exactly as
	 * given, and will be processed per the memcached protocol specification:
	 * </p>
	 *
	 * <blockquote>
	 * <p>
	 * The actual value sent may either be
	 * Unix time (number of seconds since January 1, 1970, as a 32-bit
	 * value), or a number of seconds starting from current time. In the
	 * latter case, this number of seconds may not exceed 60*60*24*30 (number
	 * of seconds in 30 days); if the number sent by a client is larger than
	 * that, the server will consider it to be real Unix time value rather
	 * than an offset from current time.
	 * </p>
	 * </blockquote>
	 *
	 * @param key the key under which this object should be added.
	 * @param exp the expiration of this object
	 * @param o the object to store
	 * @param timeout operationTimeout (msec)
	 * @return returns True if success
	 * @throws IllegalStateException in the rare circumstance where queue
	 *         is too full to accept any more requests
	 */
	Boolean add(String key, int exp, Object o, long timeout);
	/**
	 * Set an object in the cache (using the default transcoder)
	 * regardless of any existing value.
	 *
	 * <p>
	 * The <code>exp</code> value is passed along to memcached exactly as
	 * given, and will be processed per the memcached protocol specification:
	 * </p>
	 *
	 * <blockquote>
	 * <p>
	 * The actual value sent may either be
	 * Unix time (number of seconds since January 1, 1970, as a 32-bit
	 * value), or a number of seconds starting from current time. In the
	 * latter case, this number of seconds may not exceed 60*60*24*30 (number
	 * of seconds in 30 days); if the number sent by a client is larger than
	 * that, the server will consider it to be real Unix time value rather
	 * than an offset from current time.
	 * </p>
	 * </blockquote>
	 *
	 * @param key the key under which this object should be added.
	 * @param exp the expiration of this object
	 * @param o the object to store
	 * @return returns True if success
	 * @throws IllegalStateException in the rare circumstance where queue
	 *         is too full to accept any more requests
	 */
	Boolean set(String key, int exp, Object o);
	/**
	 * Set an object in the cache (using the default transcoder)
	 * regardless of any existing value.
	 *
	 * <p>
	 * The <code>exp</code> value is passed along to memcached exactly as
	 * given, and will be processed per the memcached protocol specification:
	 * </p>
	 *
	 * <blockquote>
	 * <p>
	 * The actual value sent may either be
	 * Unix time (number of seconds since January 1, 1970, as a 32-bit
	 * value), or a number of seconds starting from current time. In the
	 * latter case, this number of seconds may not exceed 60*60*24*30 (number
	 * of seconds in 30 days); if the number sent by a client is larger than
	 * that, the server will consider it to be real Unix time value rather
	 * than an offset from current time.
	 * </p>
	 * </blockquote>
	 *
	 * @param key the key under which this object should be added.
	 * @param exp the expiration of this object
	 * @param o the object to store
	 * @param timeout operationTimeout (msec)
	 * @return returns True if success
	 * @throws IllegalStateException in the rare circumstance where queue
	 *         is too full to accept any more requests
	 */
	Boolean set(String key, int exp, Object o, long timeout);
	/**
	 * Replace an object with the given value (transcoded with the default
	 * transcoder) iff there is already a value for the given key.
	 *
	 * <p>
	 * The <code>exp</code> value is passed along to memcached exactly as
	 * given, and will be processed per the memcached protocol specification:
	 * </p>
	 *
	 * <blockquote>
	 * <p>
	 * The actual value sent may either be
	 * Unix time (number of seconds since January 1, 1970, as a 32-bit
	 * value), or a number of seconds starting from current time. In the
	 * latter case, this number of seconds may not exceed 60*60*24*30 (number
	 * of seconds in 30 days); if the number sent by a client is larger than
	 * that, the server will consider it to be real Unix time value rather
	 * than an offset from current time.
	 * </p>
	 * </blockquote>
	 *
	 * @param key the key under which this object should be added.
	 * @param exp the expiration of this object
	 * @param o the object to store
	 * @return returns True if success
	 * @throws IllegalStateException in the rare circumstance where queue
	 *         is too full to accept any more requests
	 */
	Boolean replace(String key, int exp, Object o);
	/**
	 * Replace an object with the given value (transcoded with the default
	 * transcoder) iff there is already a value for the given key.
	 *
	 * <p>
	 * The <code>exp</code> value is passed along to memcached exactly as
	 * given, and will be processed per the memcached protocol specification:
	 * </p>
	 *
	 * <blockquote>
	 * <p>
	 * The actual value sent may either be
	 * Unix time (number of seconds since January 1, 1970, as a 32-bit
	 * value), or a number of seconds starting from current time. In the
	 * latter case, this number of seconds may not exceed 60*60*24*30 (number
	 * of seconds in 30 days); if the number sent by a client is larger than
	 * that, the server will consider it to be real Unix time value rather
	 * than an offset from current time.
	 * </p>
	 * </blockquote>
	 *
	 * @param key the key under which this object should be added.
	 * @param exp the expiration of this object
	 * @param o the object to store
	 * @param timeout operationTimeout (msec)
	 * @return returns True if success
	 * @throws IllegalStateException in the rare circumstance where queue
	 *         is too full to accept any more requests
	 */
	Boolean replace(String key, int exp, Object o, long timeout);
	/**
	 * Gets (with CAS support) with a single key using the default transcoder.
	 *
	 * @param key the key to get
	 * @return the result from the cache and CAS id (null if there is none)
	 * @throws OperationTimeoutException if the global operation timeout is
	 *		   exceeded
	 * @throws IllegalStateException in the rare circumstance where queue
	 *         is too full to accept any more requests
	 */
	CASValue<Object> gets(String key);
	/**
	 * Gets (with CAS support) with a single key using the default transcoder.
	 *
	 * @param key the key to get
	 * @param timeout operationTimeout (msec)
	 * @return the result from the cache and CAS id (null if there is none)
	 * @throws OperationTimeoutException if the global operation timeout is
	 *		   exceeded
	 * @throws IllegalStateException in the rare circumstance where queue
	 *         is too full to accept any more requests
	 */
	CASValue<Object> gets(String key, long timeout);
	/**
	 * Get with a single key and decode using the default transcoder.
	 *
	 * @param key the key to get
	 * @return the result from the cache (null if there is none)
	 * @throws OperationTimeoutException if the global operation timeout is
	 *		   exceeded
	 * @throws IllegalStateException in the rare circumstance where queue
	 *         is too full to accept any more requests
	 */
	Object get(String key);
	/**
	 * Get with a single key and decode using the default transcoder.
	 *
	 * @param key the key to get
	 * @param timeout operationTimeout (msec)
	 * @return the result from the cache (null if there is none)
	 * @throws OperationTimeoutException if the global operation timeout is
	 *		   exceeded
	 * @throws IllegalStateException in the rare circumstance where queue
	 *         is too full to accept any more requests
	 */
	Object get(String key, long timeout);
	/**
	 * Get the values for multiple keys from the cache.
	 *
	 * @param keys the keys
	 * @return a map of the values (for each value that exists)
	 * @throws OperationTimeoutException if the global operation timeout is
	 *		   exceeded
	 * @throws IllegalStateException in the rare circumstance where queue
	 *         is too full to accept any more requests
	 */
	Map<String, Object> getBulk(Collection<String> keys);
	/**
	 * Get the values for multiple keys from the cache.
	 *
	 * @param keys the keys
	 * @param timeout operationTimeout (msec)
	 * @return a map of the values (for each value that exists)
	 * @throws OperationTimeoutException if the global operation timeout is
	 *		   exceeded
	 * @throws IllegalStateException in the rare circumstance where queue
	 *         is too full to accept any more requests
	 */
	Map<String, Object> getBulk(Collection<String> keys, long timeout);
	/**
	 * Get the values for multiple keys from the cache.
	 *
	 * @param keys the keys
	 * @return a map of the values (for each value that exists)
	 * @throws OperationTimeoutException if the global operation timeout is
	 *		   exceeded
	 * @throws IllegalStateException in the rare circumstance where queue
	 *         is too full to accept any more requests
	 */
	Map<String, Object> getBulk(String... keys);
	/**
	 * Increment the given key by the given amount.
	 *
	 * @param key the key
	 * @param by the amount to increment
	 * @return the new value (-1 if the key doesn't exist)
	 * @throws OperationTimeoutException if the global operation timeout is
	 *		   exceeded
	 * @throws IllegalStateException in the rare circumstance where queue
	 *         is too full to accept any more requests
	 */
	long incr(String key, int by);
	/**
	 * Increment the given counter, returning the new value.
	 *
	 * @param key the key
	 * @param by the amount to increment
	 * @param def the default value (if the counter does not exist)
	 * @return the new value, or -1 if we were unable to increment or add
	 * @throws OperationTimeoutException if the global operation timeout is
	 *		   exceeded
	 * @throws IllegalStateException in the rare circumstance where queue
	 *         is too full to accept any more requests
	 */
	long incr(String key, int by, long def);
	/**
	 * Increment the given counter, returning the new value.
	 *
	 * @param key the key
	 * @param by the amount to increment
	 * @param def the default value (if the counter does not exist)
	 * @param timeout operationTimeout (msec)
	 * @return the new value, or -1 if we were unable to increment or add
	 * @throws OperationTimeoutException if the global operation timeout is
	 *		   exceeded
	 * @throws IllegalStateException in the rare circumstance where queue
	 *         is too full to accept any more requests
	 */
	long incr(String key, int by, long def, long timeout);
	/**
	 * Decrement the given key by the given value.
	 *
	 * @param key the key
	 * @param by the value
	 * @return the new value (-1 if the key doesn't exist)
	 * @throws OperationTimeoutException if the global operation timeout is
	 *		   exceeded
	 * @throws IllegalStateException in the rare circumstance where queue
	 *         is too full to accept any more requests
	 */
	long decr(String key, int by);
	/**
	 * Decrement the given counter, returning the new value.
	 *
	 * @param key the key
	 * @param by the amount to decrement
	 * @param def the default value (if the counter does not exist)
	 * @return the new value, or -1 if we were unable to decrement or add
	 * @throws OperationTimeoutException if the global operation timeout is
	 *		   exceeded
	 * @throws IllegalStateException in the rare circumstance where queue
	 *         is too full to accept any more requests
	 */
	long decr(String key, int by, long def);
	/**
	 * Decrement the given counter, returning the new value.
	 *
	 * @param key the key
	 * @param by the amount to decrement
	 * @param def the default value (if the counter does not exist)
	 * @param timeout operationTimeout (msec)
	 * @return the new value, or -1 if we were unable to decrement or add
	 * @throws OperationTimeoutException if the global operation timeout is
	 *		   exceeded
	 * @throws IllegalStateException in the rare circumstance where queue
	 *         is too full to accept any more requests
	 */
	long decr(String key, int by, long def, long timeout);
	/**
	 * Delete the given key from the cache.
	 *
	 * <p>
	 * The hold argument specifies the amount of time in seconds (or Unix time
	 * until which) the client wishes the server to refuse "add" and "replace"
	 * commands with this key. For this amount of item, the item is put into a
	 * delete queue, which means that it won't possible to retrieve it by the
	 * "get" command, but "add" and "replace" command with this key will also
	 * fail (the "set" command will succeed, however). After the time passes,
	 * the item is finally deleted from server memory.
	 * </p>
	 *
	 * @param key the key to delete
	 * @param hold how long the key should be unavailable to add commands
	 * @return returns True if success
	 *
	 * @deprecated Hold values are no longer honored.
	 */
	Boolean delete(String key, int hold);
	/**
	 * Delete the given key from the cache.
	 *
	 * @param key the key to delete
	 * @return returns True if success
	 * @throws IllegalStateException in the rare circumstance where queue
	 *         is too full to accept any more requests
	 */
	Boolean delete(String key);
	/**
	 * Delete the given key from the cache.
	 *
	 * @param key the key to delete
	 * @param timeout operationTimeout (msec)
	 * @return returns True if success
	 * @throws IllegalStateException in the rare circumstance where queue
	 *         is too full to accept any more requests
	 *         
	 */
	Boolean delete(String key, long timeout);
	/**
	 * Flush all caches from all servers with a delay of application.
	 * @param delay flush_all commands argument. (See protocol.txt...)
	 * @return returns True if success
	 * @throws IllegalStateException in the rare circumstance where queue
	 *         is too full to accept any more requests
	 *         
	 * @deprecated Not supported in roma.
	 */
	Boolean flush(final int delay);
	/**
	 * Flush all caches from all servers with a delay of application.
	 * 
	 * @param delay flush_all commands argument. (See protocol.txt...)
	 * @param timeout operationTimeout (msec)
	 * @return returns True if success
	 * @throws IllegalStateException in the rare circumstance where queue
	 *         is too full to accept any more requests
	 *         
	 * @deprecated Not supported in roma.
	 */
	Boolean flush(final int delay, long timeout);
	/**
	 * Flush all caches from all servers immediately.
	 * @return returns True if success
	 * @throws IllegalStateException in the rare circumstance where queue
	 *         is too full to accept any more requests
	 *         
	 * @deprecated Not supported in roma.
	 */
	Boolean flush();
	/**
	 * Flush all caches from all servers immediately.
	 * 
	 * @param timeout operationTimeout (msec)
	 * @return returns True if success
	 * @throws IllegalStateException in the rare circumstance where queue
	 *         is too full to accept any more requests
	 *         
	 * @deprecated Not supported in roma.
	 */
	Boolean flush(long timeout);
	/**
	 * Shut down immediately.
	 */
	void shutdown();
	/**
	 * Shut down this client gracefully.
	 * @param timeout operationTimeout (msec)
	 */
	void shutdown(long timeout);
}
