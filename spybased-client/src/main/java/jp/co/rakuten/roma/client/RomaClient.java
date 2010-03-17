// Copyright (c) 2006  Dustin Sallings <dustin@spy.net>

package jp.co.rakuten.roma.client;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import net.spy.memcached.CASResponse;
import net.spy.memcached.CASValue;
import net.spy.memcached.ConnectionObserver;
import net.spy.memcached.MemcachedClientIF;
import net.spy.memcached.NodeLocator;
import net.spy.memcached.OperationTimeoutException;
import net.spy.memcached.transcoders.Transcoder;

/**
 * Client to a memcached server.
 *
 * <h2>Basic usage</h2>
 *
 * <pre>
 *	MemcachedClient c=new MemcachedClient(
 *		new InetSocketAddress("hostname", portNum));
 *
 *	// Store a value (async) for one hour
 *	c.set("someKey", 3600, someObject);
 *	// Retrieve a value.
 *	Object myObject=c.get("someKey");
 *	</pre>
 *
 *	<h2>Advanced Usage</h2>
 *
 *	<p>
 *	 MemcachedClient may be processing a great deal of asynchronous messages or
 *	 possibly dealing with an unreachable memcached, which may delay processing.
 *	 If a memcached is disabled, for example, MemcachedConnection will continue
 *	 to attempt to reconnect and replay pending operations until it comes back
 *	 up.  To prevent this from causing your application to hang, you can use
 *	 one of the asynchronous mechanisms to time out a request and cancel the
 *	 operation to the server.
 *	</p>
 *
 *	<pre>
 *      // Get a memcached client connected to several servers
 *      // over the binary protocol
 *      MemcachedClient c = new MemcachedClient(new BinaryConnectionFactory(),
 *              AddrUtil.getAddresses("server1:11211 server2:11211"));
 *
 *      // Try to get a value, for up to 5 seconds, and cancel if it
 *      // doesn't return
 *      Object myObj = null;
 *      Future&lt;Object&gt; f = c.asyncGet("someKey");
 *      try {
 *          myObj = f.get(5, TimeUnit.SECONDS);
 *      // throws expecting InterruptedException, ExecutionException
 *      // or TimeoutException
 *      } catch (Exception e) {  /*  /
 *          // Since we don't need this, go ahead and cancel the operation.
 *          // This is not strictly necessary, but it'll save some work on
 *          // the server.  It is okay to cancel it if running.
 *          f.cancel(true);
 *          // Do other timeout related stuff
 *      }
 * </pre>
 */
public interface RomaClient extends MemcachedClientIF {
	/**
	 * Get the addresses of available servers.
	 *
	 * <p>
	 * This is based on a snapshot in time so shouldn't be considered
	 * completely accurate, but is a useful for getting a feel for what's
	 * working and what's not working.
	 * </p>
	 */
	@Override
	Collection<SocketAddress> getAvailableServers();
	/**
	 * Get the addresses of unavailable servers.
	 *
	 * <p>
	 * This is based on a snapshot in time so shouldn't be considered
	 * completely accurate, but is a useful for getting a feel for what's
	 * working and what's not working.
	 * </p>
	 */
	@Override
	Collection<SocketAddress> getUnavailableServers();
	/**
	 * Get a read-only wrapper around the node locator wrapping this instance.
	 */
	@Override
	NodeLocator getNodeLocator();
	/**
	 * Get the default transcoder that's in use.
	 */
	@Override
	Transcoder<Object> getTranscoder();
	/**
	 * Reset the default transcoder.
	 */
	void setTranscoder(final Transcoder<Object> tc);
	/**
	 * Get the default operation timeout.
	 * 
	 */
	long getOperationTimeout();
	/**
	 * Reset the default operation timeout.
	 * 
	 * @param operationTimeout
	 */
	void setOperationTimeout(long operationTimeout);

	/**
	 * Append to an existing value in the cache.
	 *
	 * @param cas cas identifier (ignored in the ascii protocol)
	 * @param key the key to whose value will be appended
	 * @param val the value to append
	 * @return a future indicating success
	 * @throws IllegalStateException in the rare circumstance where queue
	 *         is too full to accept any more requests
	 */
	@Override
	Future<Boolean> append(long cas, String key, Object val);
	/**
	 * Append to an existing value in the cache.
	 *
	 * @param cas cas identifier (ignored in the ascii protocol)
	 * @param key the key to whose value will be appended
	 * @param val the value to append
	 * @param timeout operationTimeout (msec)
	 * @return a future indicating success
	 * @throws IllegalStateException in the rare circumstance where queue
	 *         is too full to accept any more requests
	 */
	Future<Boolean> append(long cas, String key, Object val, long timeout);
	/**
	 * Append to an existing value in the cache.
	 *
	 * @param cas cas identifier (ignored in the ascii protocol)
	 * @param key the key to whose value will be appended
	 * @param val the value to append
	 * @param tc the transcoder to serialize and unserialize the value
	 * @return a future indicating success
	 * @throws IllegalStateException in the rare circumstance where queue
	 *         is too full to accept any more requests
	 */
	@Override
	<T> Future<Boolean> append(long cas, String key, T val,Transcoder<T> tc);
	/**
	 * Append to an existing value in the cache.
	 *
	 * @param cas cas identifier (ignored in the ascii protocol)
	 * @param key the key to whose value will be appended
	 * @param val the value to append
	 * @param tc the transcoder to serialize and unserialize the value
	 * @param timeout operationTimeout (msec)
	 * @return a future indicating success
	 * @throws IllegalStateException in the rare circumstance where queue
	 *         is too full to accept any more requests
	 */
	<T> Future<Boolean> append(long cas, String key, T val,Transcoder<T> tc, long timeout);
	/**
	 * Prepend to an existing value in the cache.
	 *
	 * @param cas cas identifier (ignored in the ascii protocol)
	 * @param key the key to whose value will be prepended
	 * @param val the value to append
	 * @return a future indicating success
	 * @throws IllegalStateException in the rare circumstance where queue
	 *         is too full to accept any more requests
	 */
	@Override
	Future<Boolean> prepend(long cas, String key, Object val);
	/**
	 * Prepend to an existing value in the cache.
	 *
	 * @param cas cas identifier (ignored in the ascii protocol)
	 * @param key the key to whose value will be prepended
	 * @param val the value to append
	 * @param timeout operationTimeout (msec)
	 * @return a future indicating success
	 * @throws IllegalStateException in the rare circumstance where queue
	 *         is too full to accept any more requests
	 */
	Future<Boolean> prepend(long cas, String key, Object val, long timeout);
	/**
	 * Prepend to an existing value in the cache.
	 *
	 * @param cas cas identifier (ignored in the ascii protocol)
	 * @param key the key to whose value will be prepended
	 * @param val the value to append
	 * @param tc the transcoder to serialize and unserialize the value
	 * @return a future indicating success
	 * @throws IllegalStateException in the rare circumstance where queue
	 *         is too full to accept any more requests
	 */
	@Override
	<T> Future<Boolean> prepend(long cas, String key, T val,Transcoder<T> tc);
	/**
	 * Prepend to an existing value in the cache.
	 *
	 * @param cas cas identifier (ignored in the ascii protocol)
	 * @param key the key to whose value will be prepended
	 * @param val the value to append
	 * @param tc the transcoder to serialize and unserialize the value
	 * @param timeout operationTimeout (msec)
	 * @return a future indicating success
	 * @throws IllegalStateException in the rare circumstance where queue
	 *         is too full to accept any more requests
	 */
	<T> Future<Boolean> prepend(long cas, String key, T val,Transcoder<T> tc, long timeout);
	/**
     * Asynchronous CAS operation.
      *
     * @param key the key
     * @param casId the CAS identifier (from a gets operation)
     * @param value the new value
     * @param tc the transcoder to serialize and unserialize the value
     * @return a future that will indicate the status of the CAS
     * @throws IllegalStateException in the rare circumstance where queue
     *         is too full to accept any more requests
     */
	@Override
    <T> Future<CASResponse> asyncCAS(String key, long casId, T value, Transcoder<T> tc);
	/**
     * Asynchronous CAS operation.
      *
     * @param key the key
     * @param casId the CAS identifier (from a gets operation)
     * @param value the new value
     * @param tc the transcoder to serialize and unserialize the value
     * @param timeout operationTimeout (msec)
     * @return a future that will indicate the status of the CAS
     * @throws IllegalStateException in the rare circumstance where queue
     *         is too full to accept any more requests
     */
    <T> Future<CASResponse> asyncCAS(String key, long casId, T value, Transcoder<T> tc, long timeout);
	/**
	 * Asynchronous CAS operation.
	 *
	 * @param key the key
	 * @param casId the CAS identifier (from a gets operation)
	 * @param exp the expiration of this object
	 * @param value the new value
	 * @param tc the transcoder to serialize and unserialize the value
	 * @return a future that will indicate the status of the CAS
	 * @throws IllegalStateException in the rare circumstance where queue
	 *         is too full to accept any more requests
	 */
	<T> Future<CASResponse> asyncCAS(String key, long casId, int exp, T value,Transcoder<T> tc);
	/**
	 * Asynchronous CAS operation.
	 *
	 * @param key the key
	 * @param casId the CAS identifier (from a gets operation)
	 * @param exp the expiration of this object
	 * @param value the new value
	 * @param tc the transcoder to serialize and unserialize the value
	 * @param timeout operationTimeout (msec)
	 * @return a future that will indicate the status of the CAS
	 * @throws IllegalStateException in the rare circumstance where queue
	 *         is too full to accept any more requests
	 */
	<T> Future<CASResponse> asyncCAS(String key, long casId, int exp, T value,Transcoder<T> tc, long timeout);
	/**
	 * Asynchronous CAS operation using the default transcoder.
	 *
	 * @param key the key
	 * @param casId the CAS identifier (from a gets operation)
	 * @param value the new value
	 * @return a future that will indicate the status of the CAS
	 * @throws IllegalStateException in the rare circumstance where queue
	 *         is too full to accept any more requests
	 */
	@Override
	Future<CASResponse> asyncCAS(String key, long casId, Object value);
	/**
	 * Asynchronous CAS operation using the default transcoder.
	 *
	 * @param key the key
	 * @param casId the CAS identifier (from a gets operation)
	 * @param value the new value
	 * @param timeout operationTimeout (msec)
	 * @return a future that will indicate the status of the CAS
	 * @throws IllegalStateException in the rare circumstance where queue
	 *         is too full to accept any more requests
	 */
	Future<CASResponse> asyncCAS(String key, long casId, Object value, long timeout);
	/**
	 * Perform a synchronous CAS operation.
	 *
	 * @param key the key
	 * @param casId the CAS identifier (from a gets operation)
	 * @param value the new value
	 * @param tc the transcoder to serialize and unserialize the value
	 * @return a CASResponse
	 * @throws OperationTimeoutException if global operation timeout is
	 *         exceeded
	 * @throws IllegalStateException in the rare circumstance where queue
	 *         is too full to accept any more requests
     */
	@Override
    <T> CASResponse cas(String key, long casId, T value, Transcoder<T> tc);
	/**
	 * Perform a synchronous CAS operation.
	 *
	 * @param key the key
	 * @param casId the CAS identifier (from a gets operation)
	 * @param value the new value
	 * @param tc the transcoder to serialize and unserialize the value
	 * @param timeout operationTimeout (msec)
	 * @return a CASResponse
	 * @throws OperationTimeoutException if global operation timeout is
	 *         exceeded
	 * @throws IllegalStateException in the rare circumstance where queue
	 *         is too full to accept any more requests
     */
	<T> CASResponse cas(String key, long casId, T value, Transcoder<T> tc, long timeout);
	/**
	 * Perform a synchronous CAS operation.
	 *
	 * @param key the key
	 * @param casId the CAS identifier (from a gets operation)
	 * @param exp the expiration of this object
	 * @param value the new value
	 * @param tc the transcoder to serialize and unserialize the value
	 * @return a CASResponse
	 * @throws OperationTimeoutException if global operation timeout is
	 *         exceeded
	 * @throws IllegalStateException in the rare circumstance where queue
	 *         is too full to accept any more requests
	 */
	<T> CASResponse cas(String key, long casId, int exp, T value,Transcoder<T> tc);
	/**
	 * Perform a synchronous CAS operation.
	 *
	 * @param key the key
	 * @param casId the CAS identifier (from a gets operation)
	 * @param exp the expiration of this object
	 * @param value the new value
	 * @param tc the transcoder to serialize and unserialize the value
	 * @param timeout operationTimeout (msec)
	 * @return a CASResponse
	 * @throws OperationTimeoutException if global operation timeout is
	 *         exceeded
	 * @throws IllegalStateException in the rare circumstance where queue
	 *         is too full to accept any more requests
	 */
	<T> CASResponse cas(String key, long casId, int exp, T value,Transcoder<T> tc, long timeout);
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
	@Override
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
	 * Add an object to the cache iff it does not exist already.
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
	 * @param tc the transcoder to serialize and unserialize the value
	 * @return a future representing the processing of this operation
	 * @throws IllegalStateException in the rare circumstance where queue
	 *         is too full to accept any more requests
	 */
	@Override
	<T> Future<Boolean> add(String key, int exp, T o, Transcoder<T> tc);
	/**
	 * Add an object to the cache iff it does not exist already.
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
	 * @param tc the transcoder to serialize and unserialize the value
	 * @param timeout operationTimeout (msec)
	 * @return a future representing the processing of this operation
	 * @throws IllegalStateException in the rare circumstance where queue
	 *         is too full to accept any more requests
	 */
	<T> Future<Boolean> add(String key, int exp, T o, Transcoder<T> tc, long timeout);
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
	 * @return a future representing the processing of this operation
	 * @throws IllegalStateException in the rare circumstance where queue
	 *         is too full to accept any more requests
	 */
	@Override
	Future<Boolean> add(String key, int exp, Object o);
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
	 * @return a future representing the processing of this operation
	 * @throws IllegalStateException in the rare circumstance where queue
	 *         is too full to accept any more requests
	 */
	Future<Boolean> add(String key, int exp, Object o, long timeout);
	/**
	 * Set an object in the cache regardless of any existing value.
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
	 * @param tc the transcoder to serialize and unserialize the value
	 * @return a future representing the processing of this operation
	 * @throws IllegalStateException in the rare circumstance where queue
	 *         is too full to accept any more requests
	 */
	@Override
	<T> Future<Boolean> set(String key, int exp, T o, Transcoder<T> tc);
	/**
	 * Set an object in the cache regardless of any existing value.
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
	 * @param tc the transcoder to serialize and unserialize the value
	 * @param timeout operationTimeout (msec)
	 * @return a future representing the processing of this operation
	 * @throws IllegalStateException in the rare circumstance where queue
	 *         is too full to accept any more requests
	 */
	<T> Future<Boolean> set(String key, int exp, T o, Transcoder<T> tc, long timeout);
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
	 * @return a future representing the processing of this operation
	 * @throws IllegalStateException in the rare circumstance where queue
	 *         is too full to accept any more requests
	 */
	@Override
	Future<Boolean> set(String key, int exp, Object o);
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
	 * @return a future representing the processing of this operation
	 * @throws IllegalStateException in the rare circumstance where queue
	 *         is too full to accept any more requests
	 */
	Future<Boolean> set(String key, int exp, Object o, long timeout);
	/**
	 * Replace an object with the given value iff there is already a value
	 * for the given key.
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
	 * @param tc the transcoder to serialize and unserialize the value
	 * @return a future representing the processing of this operation
	 * @throws IllegalStateException in the rare circumstance where queue
	 *         is too full to accept any more requests
	 */
	@Override
	<T> Future<Boolean> replace(String key, int exp, T o,Transcoder<T> tc);
	/**
	 * Replace an object with the given value iff there is already a value
	 * for the given key.
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
	 * @param tc the transcoder to serialize and unserialize the value
	 * @param timeout operationTimeout (msec)
	 * @return a future representing the processing of this operation
	 * @throws IllegalStateException in the rare circumstance where queue
	 *         is too full to accept any more requests
	 */
	<T> Future<Boolean> replace(String key, int exp, T o,Transcoder<T> tc, long timeout);
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
	 * @return a future representing the processing of this operation
	 * @throws IllegalStateException in the rare circumstance where queue
	 *         is too full to accept any more requests
	 */
	@Override
	Future<Boolean> replace(String key, int exp, Object o);
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
	 * @return a future representing the processing of this operation
	 * @throws IllegalStateException in the rare circumstance where queue
	 *         is too full to accept any more requests
	 */
	Future<Boolean> replace(String key, int exp, Object o, long timeout);
	/**
	 * Get the given key asynchronously.
	 *
	 * @param key the key to fetch
	 * @param tc the transcoder to serialize and unserialize value
	 * @return a future that will hold the return value of the fetch
	 * @throws IllegalStateException in the rare circumstance where queue
	 *         is too full to accept any more requests
	 */
	@Override
	<T> Future<T> asyncGet(final String key, final Transcoder<T> tc);
	/**
	 * Get the given key asynchronously.
	 *
	 * @param key the key to fetch
	 * @param tc the transcoder to serialize and unserialize value
	 * @param timeout operationTimeout (msec)
	 * @return a future that will hold the return value of the fetch
	 * @throws IllegalStateException in the rare circumstance where queue
	 *         is too full to accept any more requests
	 */
	<T> Future<T> asyncGet(final String key, final Transcoder<T> tc, long timeout);
	/**
	 * Get mklhash (Roma extention operation).
	 *
	 * @return Returns roma-mklhash 
	 */
	Future<String> asyncMklhash();
	/**
	 * Get mklhash asynchronously (Roma extention operation).
	 * 
	 * @param timeout operationTimeout (msec)
	 * @return Returns roma-mklhash 
	 */
	Future<String> asyncMklhash(long timeout);
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
	 * Get routingdump asynchronously (Roma extention operation).
	 *
	 * @return Returns roma-routingdump 
	 */
	Future<String> asyncRoutingdump();
	/**
	 * Get routingdump asynchronously (Roma extention operation).
	 *
	 * @param timeout operationTimeout (msec)
	 * @return Returns roma-routingdump 
	 */
	Future<String> asyncRoutingdump(long timeout);
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
	 * Reconstruction node-info asynchronously (Roma extention operation).
	 *
	 * @return a future indicating success
	 */
	Future<Boolean> asyncReconstruction();
	/**
	 * Reconstruction node-info asynchronously (Roma extention operation).
	 *
	 * @param timeout operationTimeout (msec)
	 * @return a future indicating success
	 */
	Future<Boolean> asyncReconstruction(long timeout);
	/**
	 * Reconstruction node-info (Roma extention operation).
	 *
	 * @return a future indicating success
	 */
	void reconstruction();
	/**
	 * Reconstruction node-info (Roma extention operation).
	 *
	 * @param timeout operationTimeout (msec)
	 * @return a future indicating success
	 */
	void reconstruction(long timeout);
	/**
	 * Get the given key asynchronously and decode with the default
	 * transcoder.
	 *
	 * @param key the key to fetch
	 * @return a future that will hold the return value of the fetch
	 * @throws IllegalStateException in the rare circumstance where queue
	 *         is too full to accept any more requests
	 */
	@Override
	Future<Object> asyncGet(final String key);
	/**
	 * Get the given key asynchronously and decode with the default
	 * transcoder.
	 *
	 * @param key the key to fetch
	 * @param timeout operationTimeout (msec)
	 * @return a future that will hold the return value of the fetch
	 * @throws IllegalStateException in the rare circumstance where queue
	 *         is too full to accept any more requests
	 */
	Future<Object> asyncGet(final String key, long timeout);
	/**
	 * Gets (with CAS support) the given key asynchronously.
	 *
	 * @param key the key to fetch
	 * @param tc the transcoder to serialize and unserialize value
	 * @return a future that will hold the return value of the fetch
	 * @throws IllegalStateException in the rare circumstance where queue
	 *         is too full to accept any more requests
	 */
	@Override
	<T> Future<CASValue<T>> asyncGets(final String key,final Transcoder<T> tc);
	/**
	 * Gets (with CAS support) the given key asynchronously.
	 *
	 * @param key the key to fetch
	 * @param tc the transcoder to serialize and unserialize value
	 * @param timeout operationTimeout (msec)
	 * @return a future that will hold the return value of the fetch
	 * @throws IllegalStateException in the rare circumstance where queue
	 *         is too full to accept any more requests
	 */
	<T> Future<CASValue<T>> asyncGets(final String key,final Transcoder<T> tc, long timeout);
	/**
	 * Gets (with CAS support) the given key asynchronously and decode using
	 * the default transcoder.
	 *
	 * @param key the key to fetch
	 * @return a future that will hold the return value of the fetch
	 * @throws IllegalStateException in the rare circumstance where queue
	 *         is too full to accept any more requests
	 */
	@Override
	Future<CASValue<Object>> asyncGets(final String key);
	/**
	 * Gets (with CAS support) the given key asynchronously and decode using
	 * the default transcoder.
	 *
	 * @param key the key to fetch
	 * @param timeout operationTimeout (msec)
	 * @return a future that will hold the return value of the fetch
	 * @throws IllegalStateException in the rare circumstance where queue
	 *         is too full to accept any more requests
	 */
	Future<CASValue<Object>> asyncGets(final String key, long timeout);
	/**
	 * Gets (with CAS support) with a single key.
	 *
	 * @param key the key to get
	 * @param tc the transcoder to serialize and unserialize value
	 * @return the result from the cache and CAS id (null if there is none)
	 * @throws OperationTimeoutException if global operation timeout is
	 * 		   exceeded
	 * @throws IllegalStateException in the rare circumstance where queue
	 *         is too full to accept any more requests
	 */
	@Override
	<T> CASValue<T> gets(String key, Transcoder<T> tc);
	/**
	 * Gets (with CAS support) with a single key.
	 *
	 * @param key the key to get
	 * @param tc the transcoder to serialize and unserialize value
	 * @param timeout operationTimeout (msec)
	 * @return the result from the cache and CAS id (null if there is none)
	 * @throws OperationTimeoutException if global operation timeout is
	 * 		   exceeded
	 * @throws IllegalStateException in the rare circumstance where queue
	 *         is too full to accept any more requests
	 */
	<T> CASValue<T> gets(String key, Transcoder<T> tc, long timeout);
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
	@Override
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
	 * Get with a single key.
	 *
	 * @param key the key to get
	 * @param tc the transcoder to serialize and unserialize value
	 * @return the result from the cache (null if there is none)
	 * @throws OperationTimeoutException if the global operation timeout is
	 *		   exceeded
	 * @throws IllegalStateException in the rare circumstance where queue
	 *         is too full to accept any more requests
	 */
	@Override
	<T> T get(String key, Transcoder<T> tc);
	/**
	 * Get with a single key.
	 *
	 * @param key the key to get
	 * @param tc the transcoder to serialize and unserialize value
	 * @param timeout operationTimeout (msec)
	 * @return the result from the cache (null if there is none)
	 * @throws OperationTimeoutException if the global operation timeout is
	 *		   exceeded
	 * @throws IllegalStateException in the rare circumstance where queue
	 *         is too full to accept any more requests
	 */
	<T> T get(String key, Transcoder<T> tc, long timeout);
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
	@Override
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
	 * Asynchronously get a bunch of objects from the cache.
	 *
	 * @param keys the keys to request
	 * @param tc the transcoder to serialize and unserialize value
	 * @return a Future result of that fetch
	 * @throws IllegalStateException in the rare circumstance where queue
	 *         is too full to accept any more requests
	 */
	@Override
	<T> Future<Map<String, T>> asyncGetBulk(Collection<String> keys,final Transcoder<T> tc);
	/**
	 * Asynchronously get a bunch of objects from the cache.
	 *
	 * @param keys the keys to request
	 * @param tc the transcoder to serialize and unserialize value
	 * @param timeout operationTimeout (msec)
	 * @return a Future result of that fetch
	 * @throws IllegalStateException in the rare circumstance where queue
	 *         is too full to accept any more requests
	 */
	<T> Future<Map<String, T>> asyncGetBulk(Collection<String> keys,final Transcoder<T> tc, long timeout);
	/**
	 * Asynchronously get a bunch of objects from the cache and decode them
	 * with the given transcoder.
	 *
	 * @param keys the keys to request
	 * @return a Future result of that fetch
	 * @throws IllegalStateException in the rare circumstance where queue
	 *         is too full to accept any more requests
	 */
	@Override
	Future<Map<String, Object>> asyncGetBulk(Collection<String> keys);
	/**
	 * Asynchronously get a bunch of objects from the cache and decode them
	 * with the given transcoder.
	 *
	 * @param keys the keys to request
	 * @param timeout operationTimeout (msec)
	 * @return a Future result of that fetch
	 * @throws IllegalStateException in the rare circumstance where queue
	 *         is too full to accept any more requests
	 */
	Future<Map<String, Object>> asyncGetBulk(Collection<String> keys, long timeout);
	/**
	 * Varargs wrapper for asynchronous bulk gets.
	 *
	 * @param tc the transcoder to serialize and unserialize value
	 * @param keys one more more keys to get
	 * @return the future values of those keys
	 * @throws IllegalStateException in the rare circumstance where queue
	 *         is too full to accept any more requests
	 */
	@Override
	<T> Future<Map<String, T>> asyncGetBulk(Transcoder<T> tc,String... keys);
	/**
	 * Varargs wrapper for asynchronous bulk gets with the default transcoder.
	 *
	 * @param keys one more more keys to get
	 * @return the future values of those keys
	 * @throws IllegalStateException in the rare circumstance where queue
	 *         is too full to accept any more requests
	 */
	@Override
	Future<Map<String, Object>> asyncGetBulk(String... keys);
	/**
	 * Get the values for multiple keys from the cache.
	 *
	 * @param keys the keys
	 * @param tc the transcoder to serialize and unserialize value
	 * @return a map of the values (for each value that exists)
	 * @throws OperationTimeoutException if the global operation timeout is
	 *		   exceeded
	 * @throws IllegalStateException in the rare circumstance where queue
	 *         is too full to accept any more requests
	 */
	@Override
	<T> Map<String, T> getBulk(Collection<String> keys,Transcoder<T> tc);
	/**
	 * Get the values for multiple keys from the cache.
	 *
	 * @param keys the keys
	 * @param tc the transcoder to serialize and unserialize value
	 * @param timeout operationTimeout (msec)
	 * @return a map of the values (for each value that exists)
	 * @throws OperationTimeoutException if the global operation timeout is
	 *		   exceeded
	 * @throws IllegalStateException in the rare circumstance where queue
	 *         is too full to accept any more requests
	 */
	<T> Map<String, T> getBulk(Collection<String> keys,Transcoder<T> tc, long timeout);
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
	@Override
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
	 * @param tc the transcoder to serialize and unserialize value
	 * @param keys the keys
	 * @return a map of the values (for each value that exists)
	 * @throws OperationTimeoutException if the global operation timeout is
	 *		   exceeded
	 * @throws IllegalStateException in the rare circumstance where queue
	 *         is too full to accept any more requests
	 */
	@Override
	<T> Map<String, T> getBulk(Transcoder<T> tc, String... keys);
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
	@Override
	Map<String, Object> getBulk(String... keys);
	/**
	 * Get the versions of all of the connected memcacheds.
	 * @throws IllegalStateException in the rare circumstance where queue
	 *         is too full to accept any more requests
	 */
	@Override
	Map<SocketAddress, String> getVersions();
	/**
	 * Get the versions of all of the connected memcacheds.
	 * @param timeout operationTimeout (msec)
	 * @throws IllegalStateException in the rare circumstance where queue
	 *         is too full to accept any more requests
	 */
	Map<SocketAddress, String> getVersions(long timeout);
	/**
	 * Get all of the stats from all of the connections.
	 * @throws IllegalStateException in the rare circumstance where queue
	 *         is too full to accept any more requests
	 */
	@Override
	Map<SocketAddress, Map<String, String>> getStats();
	/**
	 * Get all of the stats from all of the connections.
	 * @param timeout operationTimeout (msec)
	 * @throws IllegalStateException in the rare circumstance where queue
	 *         is too full to accept any more requests
	 */
	Map<SocketAddress, Map<String, String>> getStats(long timeout);
	/**
	 * Get a set of stats from all connections.
	 *
	 * @param arg which stats to get
	 * @return a Map of the server SocketAddress to a map of String stat
	 *		   keys to String stat values.
	 * @throws IllegalStateException in the rare circumstance where queue
	 *         is too full to accept any more requests
	 */
	@Override
	Map<SocketAddress, Map<String, String>> getStats(final String arg);
	/**
	 * Get a set of stats from all connections.
	 *
	 * @param arg which stats to get
	 * @param timeout operationTimeout (msec)
	 * @return a Map of the server SocketAddress to a map of String stat
	 *		   keys to String stat values.
	 * @throws IllegalStateException in the rare circumstance where queue
	 *         is too full to accept any more requests
	 */
	Map<SocketAddress, Map<String, String>> getStats(final String arg, long timeout);
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
	@Override
	long incr(String key, int by);
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
	@Override
	long decr(String key, int by);
	/**
	 * Increment the given counter, returning the new value.
	 *
	 * @param key the key
	 * @param by the amount to increment
	 * @param def the default value (if the counter does not exist)
	 * @param exp the expiration of this object
	 * @return the new value, or -1 if we were unable to increment or add
	 * @throws OperationTimeoutException if the global operation timeout is
	 *		   exceeded
	 * @throws IllegalStateException in the rare circumstance where queue
	 *         is too full to accept any more requests
	 */
	@Override
	long incr(String key, int by, long def, int exp);
	/**
	 * Increment the given counter, returning the new value.
	 *
	 * @param key the key
	 * @param by the amount to increment
	 * @param def the default value (if the counter does not exist)
	 * @param exp the expiration of this object
	 * @param timeout operationTimeout (msec)
	 * @return the new value, or -1 if we were unable to increment or add
	 * @throws OperationTimeoutException if the global operation timeout is
	 *		   exceeded
	 * @throws IllegalStateException in the rare circumstance where queue
	 *         is too full to accept any more requests
	 */
	long incr(String key, int by, long def, int exp, long timeout);
	/**
	 * Decrement the given counter, returning the new value.
	 *
	 * @param key the key
	 * @param by the amount to decrement
	 * @param def the default value (if the counter does not exist)
	 * @param exp the expiration of this object
	 * @return the new value, or -1 if we were unable to decrement or add
	 * @throws OperationTimeoutException if the global operation timeout is
	 *		   exceeded
	 * @throws IllegalStateException in the rare circumstance where queue
	 *         is too full to accept any more requests
	 */
	@Override
	long decr(String key, int by, long def, int exp);
	/**
	 * Decrement the given counter, returning the new value.
	 *
	 * @param key the key
	 * @param by the amount to decrement
	 * @param def the default value (if the counter does not exist)
	 * @param exp the expiration of this object
	 * @param timeout operationTimeout (msec)
	 * @return the new value, or -1 if we were unable to decrement or add
	 * @throws OperationTimeoutException if the global operation timeout is
	 *		   exceeded
	 * @throws IllegalStateException in the rare circumstance where queue
	 *         is too full to accept any more requests
	 */
	long decr(String key, int by, long def, int exp, long timeout);
	/**
	 * Asychronous increment.
	 *
	 * @return a future with the incremented value, or -1 if the
	 *		   increment failed.
	 * @throws IllegalStateException in the rare circumstance where queue
	 *         is too full to accept any more requests
	 */
	@Override
	Future<Long> asyncIncr(String key, int by);
	/**
	 * Asychronous increment.
	 *
	 * @param timeout operationTimeout (msec)
	 * @return a future with the incremented value, or -1 if the
	 *		   increment failed.
	 * @throws IllegalStateException in the rare circumstance where queue
	 *         is too full to accept any more requests
	 */
	Future<Long> asyncIncr(String key, int by, long timeout);
	/**
	 * Asynchronous decrement.
	 *
	 * @return a future with the decremented value, or -1 if the
	 *		   increment failed.
	 * @throws IllegalStateException in the rare circumstance where queue
	 *         is too full to accept any more requests
	 */
	@Override
	Future<Long> asyncDecr(String key, int by);
	/**
	 * Asynchronous decrement.
	 *
	 * @param timeout operationTimeout (msec)
	 * @return a future with the decremented value, or -1 if the
	 *		   increment failed.
	 * @throws IllegalStateException in the rare circumstance where queue
	 *         is too full to accept any more requests
	 */
	Future<Long> asyncDecr(String key, int by, long timeout);
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
	@Override
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
	@Override
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
	 *
	 * @deprecated Hold values are no longer honored.
	 */
	@Deprecated
	Future<Boolean> delete(String key, int hold);
	/**
	 * Delete the given key from the cache.
	 *
	 * @param key the key to delete
	 * @throws IllegalStateException in the rare circumstance where queue
	 *         is too full to accept any more requests
	 */
	@Override
	Future<Boolean> delete(String key);
	/**
	 * Delete the given key from the cache.
	 *
	 * @param key the key to delete
	 * @param timeout operationTimeout (msec)
	 * @throws IllegalStateException in the rare circumstance where queue
	 *         is too full to accept any more requests
	 */
	Future<Boolean> delete(String key, long timeout);
	/**
	 * Flush all caches from all servers with a delay of application.
	 * @throws IllegalStateException in the rare circumstance where queue
	 *         is too full to accept any more requests
	 */
	@Override
	Future<Boolean> flush(final int delay);
	/**
	 * Flush all caches from all servers with a delay of application.
	 * 
	 * @param timeout operationTimeout (msec)
	 * @throws IllegalStateException in the rare circumstance where queue
	 *         is too full to accept any more requests
	 */
	Future<Boolean> flush(final int delay, long timeout);
	/**
	 * Flush all caches from all servers immediately.
	 * @throws IllegalStateException in the rare circumstance where queue
	 *         is too full to accept any more requests
	 */
	@Override
	Future<Boolean> flush();
	/**
	 * Flush all caches from all servers immediately.
	 * 
	 * @param timeout operationTimeout (msec)
	 * @throws IllegalStateException in the rare circumstance where queue
	 *         is too full to accept any more requests
	 */
	Future<Boolean> flush(long timeout);

	/**
	 * Shut down immediately.
	 */
	@Override
	void shutdown();
	/**
	 * Shut down this client gracefully.
	 */
	@Override
	boolean shutdown(long timeout, TimeUnit unit);
	/**
	 * Wait for the queues to die down.
	 *
	 * @throws IllegalStateException in the rare circumstance where queue
	 *         is too full to accept any more requests
	 */
	@Override
	boolean waitForQueues(long timeout, TimeUnit unit);
	/**
	 * Add a connection observer.
	 *
	 * @return true if the observer was added.
	 */
	@Override
	boolean addObserver(ConnectionObserver obs);
	/**
	 * Remove a connection observer.
	 *
	 * @return true if the observer existed, but no longer does
	 */
	@Override
	boolean removeObserver(ConnectionObserver obs);
}
