// Copyright (c) 2006  Dustin Sallings <dustin@spy.net>

package jp.co.rakuten.roma.client;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.ClosedSelectorException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

import jp.co.rakuten.roma.client.internal.RomaBulkGetFuture;
import jp.co.rakuten.roma.client.ops.RomaExtensionOperation;
import jp.co.rakuten.roma.client.ops.RomaMklhashOperation;
import jp.co.rakuten.roma.client.ops.RomaRoutingdumpOperation;

import net.spy.memcached.BroadcastOpFactory;
import net.spy.memcached.CASResponse;
import net.spy.memcached.CASValue;
import net.spy.memcached.CachedData;
import net.spy.memcached.ConnectionObserver;
import net.spy.memcached.KeyUtil;
import net.spy.memcached.MemcachedClientIF;
import net.spy.memcached.MemcachedNode;
import net.spy.memcached.NodeLocator;
import net.spy.memcached.OperationTimeoutException;
import net.spy.memcached.compat.SpyThread;
import net.spy.memcached.internal.GetFuture;
import net.spy.memcached.internal.OperationFuture;
import net.spy.memcached.ops.CASOperationStatus;
import net.spy.memcached.ops.CancelledOperationStatus;
import net.spy.memcached.ops.ConcatenationType;
import net.spy.memcached.ops.DeleteOperation;
import net.spy.memcached.ops.GetOperation;
import net.spy.memcached.ops.GetsOperation;
import net.spy.memcached.ops.Mutator;
import net.spy.memcached.ops.Operation;
import net.spy.memcached.ops.OperationCallback;
import net.spy.memcached.ops.OperationState;
import net.spy.memcached.ops.OperationStatus;
import net.spy.memcached.ops.StatsOperation;
import net.spy.memcached.ops.StoreType;
import net.spy.memcached.transcoders.TranscodeService;
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
public class RomaClientImpl extends SpyThread implements RomaClient{
	private static final int ROUTING_UPDATE_TIME = 3000;

	private volatile boolean running=true;
	private volatile boolean shuttingDown=false;

	private long operationTimeout;

	private final RomaConnection conn;
	final RomaOperationFactory opFact;

	Transcoder<Object> transcoder;

	final TranscodeService tcService;

	/**
	 * Get a memcache client operating on the specified memcached locations.
	 *
	 * @param ia the memcached locations
	 * @throws IOException if connections cannot be established
	 * @throws TimeoutException 
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	public RomaClientImpl(String... names) throws IOException, InterruptedException, ExecutionException, TimeoutException {
		this(new RomaDefaultConnectionFactory(), Arrays.asList(names));
	}

	/**
	 * Get a memcache client over the specified memcached locations.
	 *
	 * @param addrs the socket addrs
	 * @throws IOException if connections cannot be established
	 * @throws TimeoutException 
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	public RomaClientImpl(List<String> names)
		throws IOException, InterruptedException, ExecutionException, TimeoutException {
		this(new RomaDefaultConnectionFactory(), names);
	}

	/**
	 * Get a memcache client over the specified memcached locations.
	 *
	 * @param cf the connection factory to configure connections for this client
	 * @param addrs the socket addresses
	 * @throws IOException if connections cannot be established
	 * @throws TimeoutException 
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	public RomaClientImpl(RomaConnectionFactory cf, List<String> names)
		throws IOException, InterruptedException, ExecutionException, TimeoutException {
		if(cf == null) {
			throw new NullPointerException("Connection factory required");
		}
		if(names == null) {
			throw new NullPointerException("Server list required");
		}
		if(names.isEmpty()) {
			throw new IllegalArgumentException(
				"You must have at least one server to connect to");
		}
		if(cf.getOperationTimeout() <= 0) {
			throw new IllegalArgumentException(
				"Operation timeout must be positive.");
		}
		tcService = new TranscodeService();
		transcoder=cf.getDefaultTranscoder();
		opFact=cf.getOperationFactory();
		assert opFact != null : "Connection factory failed to make op factory";
		conn=cf.createConnection(names);
		assert conn != null : "Connection factory failed to make a connection";
		operationTimeout = cf.getOperationTimeout();
		setName("Memcached IO over " + conn);
		setDaemon(cf.isDaemon());
		start();
	long start = System.currentTimeMillis();
	System.err.println("BEGIN-FIRST");
		conn.awaitStartup(operationTimeout);
	long end = System.currentTimeMillis();
	System.err.println("BEGIN-END ("+(end-start)+")");
	}
	/**
	 * Observer
	 */
	@Override
	public boolean addObserver(ConnectionObserver obs) {
		return conn.addObserver(obs);
	}
	@Override
	public boolean removeObserver(ConnectionObserver obs) {
		return conn.removeObserver(obs);
	}
	/**
	 * OperationTimeout
	 */
	@Override
	public void setOperationTimeout(long operationTimeout) {
		this.operationTimeout = operationTimeout;
	}
	@Override
	public long getOperationTimeout() {
		return operationTimeout;
	}
	/**
	 * Servers
	 */
	@Override
	public Collection<SocketAddress> getAvailableServers() {
		Collection<SocketAddress> rv=new ArrayList<SocketAddress>();
		for(MemcachedNode node : conn.getLocator().getAll()) {
			if(node.isActive()) {
				rv.add(node.getSocketAddress());
			}
		}
		return rv;
	}
	@Override
	public Collection<SocketAddress> getUnavailableServers() {
		Collection<SocketAddress> rv=new ArrayList<SocketAddress>();
		for(MemcachedNode node : conn.getLocator().getAll()) {
			if(!node.isActive()) {
				rv.add(node.getSocketAddress());
			}
		}
		return rv;
	}
	@Override
	public NodeLocator getNodeLocator() {
		return conn.getLocator().getReadonlyCopy();
	}
	/*
	 * Transcoder
	 */
	@Override
	public Transcoder<Object> getTranscoder() {
		return transcoder;
	}
	@Override
	public void setTranscoder(Transcoder<Object> tc) {
		this.transcoder = tc;
	}
	/**
	 * append
	 */
	@Override
	public Future<Boolean> append(long cas, String key, Object val) {
		return append(cas, key, val, transcoder,operationTimeout);
	}
	@Override
	public <T> Future<Boolean> append(long cas, String key, T val,Transcoder<T> tc) {
		return append(cas, key, val, tc,operationTimeout);
	}
	@Override
	public Future<Boolean> append(long cas, String key, Object val, long timeout) {
		return append(cas, key, val, transcoder,timeout);
	}
	@Override
	public <T> Future<Boolean> append(long cas, String key, T val, Transcoder<T> tc, long timeout) {
		return asyncCat(ConcatenationType.append, cas, key, val, tc,timeout);
	}
	/**
	 * prepend
	 */
	@Override
	public Future<Boolean> prepend(long cas, String key, Object val) {
		return prepend(cas, key, val, transcoder,operationTimeout);
	}
	@Override
	public Future<Boolean> prepend(long cas, String key, Object val,long timeout) {
		return prepend(cas, key, val, transcoder, timeout);
	}
	@Override
	public <T> Future<Boolean> prepend(long cas, String key, T val ,Transcoder<T> tc) {
		return prepend(cas, key, val, tc,operationTimeout);
	}
	@Override
	public <T> Future<Boolean> prepend(long cas, String key, T val,Transcoder<T> tc,long timeout) {
		return asyncCat(ConcatenationType.prepend, cas, key, val, tc,timeout);
	}
	/**
	 * cas
	 */
	@Override
	public Future<CASResponse> asyncCAS(String key, long casId, Object value,	long timeout) {
		return asyncCAS(key, casId, value,transcoder,timeout);
	}
	@Override
	public <T> Future<CASResponse> asyncCAS(String key, long casId, T value,Transcoder<T> tc) {
		return asyncCAS(key, casId, value,tc,operationTimeout);
	}
	@Override
	public <T> Future<CASResponse> asyncCAS(String key, long casId, int exp, T value,Transcoder<T> tc) {
        return asyncCAS(key, casId, exp, value, tc,operationTimeout);
	}
	@Override
    public <T> Future<CASResponse> asyncCAS(String key, long casId, T value,Transcoder<T> tc,long timeout) {
        return asyncCAS(key, casId, 0, value, tc,timeout);
	}
	@Override
	public Future<CASResponse> asyncCAS(String key, long casId, Object value) {
		return asyncCAS(key, casId, value, transcoder,operationTimeout);
	}
	@Override
	public <T> Future<CASResponse> asyncCAS(String key, long casId, int exp, T value,Transcoder<T> tc,long timeout) {
		CachedData co=tc.encode(value);
		final CountDownLatch latch=new CountDownLatch(1);
		final OperationFuture<CASResponse> rv=new OperationFuture<CASResponse>(latch, timeout);
		Operation op=opFact.cas(StoreType.set, key, casId, co.getFlags(), exp,
				co.getData(), new OperationCallback() {
					@Override
					public void receivedStatus(OperationStatus val) {
						if(val instanceof CASOperationStatus) {
							rv.set(((CASOperationStatus)val).getCASResponse());
						} else if(val instanceof CancelledOperationStatus) {
							// Cancelled, ignore and let it float up
						} else {
							throw new RuntimeException(
								"Unhandled state: " + val);
						}
					}
					@Override
					public void complete() {
						latch.countDown();
					}});
		rv.setOperation(op);
		addOp(key, op);
		return rv;
	}
	@Override
	public <T> CASResponse cas(String key, long casId, T value,Transcoder<T> tc) {
        return cas(key, casId, 0, value, tc);
    }
	@Override
	public CASResponse cas(String key, long casId, Object value) {
		return cas(key, casId, 0, value, transcoder,operationTimeout);
	}
	@Override
	public <T> CASResponse cas(String key, long casId, int exp, T value, net.spy.memcached.transcoders.Transcoder<T> tc) {
		return cas(key, casId, exp, value, tc,operationTimeout);
	}
	@Override
	public CASResponse cas(String key, long casId, Object value, long timeout) {
		return cas(key, casId, 0, value, transcoder,timeout);
	}
	@Override
	public <T> CASResponse cas(String key, long casId, T value, net.spy.memcached.transcoders.Transcoder<T> tc, long timeout) {
		return cas(key, casId, 0, value, tc,timeout);
	}
	@Override
	public <T> CASResponse cas(String key, long casId, int exp, T value,Transcoder<T> tc,long timeout) {
		try {
			return asyncCAS(key, casId, exp, value, tc,timeout).get(timeout,TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			throw new RuntimeException("Interrupted waiting for value", e);
		} catch (ExecutionException e) {
			throw new RuntimeException("Exception waiting for value", e);
		} catch (TimeoutException e) {
			throw new OperationTimeoutException("Timeout waiting for value", e);
		}
	}
	/**
	 * add
	 */
	@Override
	public <T> Future<Boolean> add(String key, int exp, T o, Transcoder<T> tc,long timeout) {
		return asyncStore(StoreType.add, key, exp, o, tc,timeout);
	}
	@Override
	public Future<Boolean> add(String key, int exp, Object o) {
		return asyncStore(StoreType.add, key, exp, o, transcoder,operationTimeout);
	}
	@Override
	public Future<Boolean> add(String key, int exp, Object o, long timeout) {
		return asyncStore(StoreType.add, key, exp, o, transcoder,timeout);
	}
	@Override
	public <T> java.util.concurrent.Future<Boolean> add(String key, int exp, T o, net.spy.memcached.transcoders.Transcoder<T> tc) {
		return asyncStore(StoreType.add, key, exp, o, tc,operationTimeout);
	}
	/**
	 * set
	 */
	@Override
	public <T> Future<Boolean> set(String key, int exp, T o, Transcoder<T> tc,long timeout) {
		return asyncStore(StoreType.set, key, exp, o, tc,timeout);
	}
	@Override
	public <T> Future<Boolean> set(String key, int exp, T o, Transcoder<T> tc) {
		return asyncStore(StoreType.set, key, exp, o, tc,operationTimeout);
	}
	@Override
	public Future<Boolean> set(String key, int exp, Object o) {
		return asyncStore(StoreType.set, key, exp, o, transcoder,operationTimeout);
	}
	@Override
	public Future<Boolean> set(String key, int exp, Object o, long timeout) {
		return asyncStore(StoreType.set, key, exp, o, transcoder,timeout);
	}
	/**
	 * replace
	 */
	@Override
	public <T> Future<Boolean> replace(String key, int exp, T o,Transcoder<T> tc,long timeout) {
		return asyncStore(StoreType.replace, key, exp, o, tc,timeout);
	}

	@Override
	public Future<Boolean> replace(String key, int exp, Object o) {
		return asyncStore(StoreType.replace, key, exp, o, transcoder,operationTimeout);
	}
	@Override
	public Future<Boolean> replace(String key, int exp, Object o, long timeout) {
		return asyncStore(StoreType.replace, key, exp, o, transcoder,timeout);
	}
	@Override
	public <T> java.util.concurrent.Future<Boolean> replace(String key, int exp, T o, net.spy.memcached.transcoders.Transcoder<T> tc) {
		return asyncStore(StoreType.replace, key, exp, o, tc,operationTimeout);
	}
	/**
	 * get
	 */
	@Override
	public <T> Future<T> asyncGet(final String key, final Transcoder<T> tc,long timeout) {
		final CountDownLatch latch=new CountDownLatch(1);
		final GetFuture<T> rv=new GetFuture<T>(latch, timeout);
		Operation op=opFact.get(key,new GetOperation.Callback() {
			private Future<T> val=null;
			@Override
			public void receivedStatus(OperationStatus status) {
				rv.set(val);
			}
			@Override
			public void gotData(String key, int flags, byte[] data) {
				val=tcService.decode(tc,
						new CachedData(flags, data, tc.getMaxSize()));
			}
			@Override
			public void complete() {
				latch.countDown();
			}});

		rv.setOperation(op);
		addOp(key, op);
		return rv;
	}
	@Override
	public Future<Object> asyncGet(String key, long timeout) {
		return asyncGet(key, transcoder,timeout);
	}
	@Override
	public <T> Future<T> asyncGet(String key, Transcoder<T> tc) {
		return asyncGet(key, tc,operationTimeout);
	}
	@Override
	public Future<Object> asyncGet(final String key) {
		return asyncGet(key, transcoder,operationTimeout);
	}
	@Override
	public <T> T get(String key, Transcoder<T> tc,long timeout) {
		try {
			return asyncGet(key, tc, timeout).get(timeout, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			throw new RuntimeException("Interrupted waiting for value", e);
		} catch (ExecutionException e) {
			throw new RuntimeException("Exception waiting for value", e);
		} catch (TimeoutException e) {
			throw new OperationTimeoutException("Timeout waiting for value", e);
		}
	}
	@Override
	public <T> T get(String key, Transcoder<T> tc) {
		return get(key, tc,operationTimeout);
	}
	@Override
	public Object get(String key) {
		return get(key, transcoder,operationTimeout);
	}
	@Override
	public Object get(String key, long timeout) {
		return get(key, transcoder,timeout);
	}
	/**
	 * gets
	 */
	@Override
	public <T> Future<CASValue<T>> asyncGets(final String key,final Transcoder<T> tc,long timeout) {

		final CountDownLatch latch=new CountDownLatch(1);
		final OperationFuture<CASValue<T>> rv= new OperationFuture<CASValue<T>>(latch, timeout);
		Operation op=opFact.gets(key,
				new GetsOperation.Callback() {
			private CASValue<T> val=null;
			@Override
			public void receivedStatus(OperationStatus status) {
				rv.set(val);
			}
			@Override
			public void gotData(String k, int flags, long cas, byte[] data) {
				assert key.equals(k) : "Wrong key returned";
				assert cas > 0 : "CAS was less than zero:  " + cas;
				val=new CASValue<T>(cas, tc.decode(
					new CachedData(flags, data, tc.getMaxSize())));
			}
			@Override
			public void complete() {
				latch.countDown();
			}});
		rv.setOperation(op);
		addOp(key, op);
		return rv;
	}
	@Override
	public <T> Future<CASValue<T>> asyncGets(final String key,final Transcoder<T> tc) {
		return asyncGets(key, tc, operationTimeout);
	}
	@Override
	public Future<CASValue<Object>> asyncGets(final String key) {
		return asyncGets(key, transcoder, operationTimeout);
	}
	@Override
	public Future<CASValue<Object>> asyncGets(String key, long timeout) {
		return asyncGets(key, transcoder, timeout);
	}
	@Override
	public <T> CASValue<T> gets(String key, Transcoder<T> tc,long timeout) {
		try {
			return asyncGets(key, tc,timeout).get(timeout, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			throw new RuntimeException("Interrupted waiting for value", e);
		} catch (ExecutionException e) {
			throw new RuntimeException("Exception waiting for value", e);
		} catch (TimeoutException e) {
			throw new OperationTimeoutException("Timeout waiting for value", e);
		}
	}
	@Override
	public CASValue<Object> gets(String key) {
		return gets(key, transcoder, operationTimeout);
	}
	@Override
	public CASValue<Object> gets(String key, long timeout) {
		return gets(key, transcoder, timeout);
	}
	@Override
	public <T> CASValue<T> gets(String key, Transcoder<T> tc) {
		return gets(key, tc, operationTimeout);
	}
	/**
	 * getBulk
	 */
	@Override
	public <T> Future<Map<String, T>> asyncGetBulk(Collection<String> keys,final Transcoder<T> tc,long timeout) {
		final Map<String, Future<T>> m=new ConcurrentHashMap<String, Future<T>>();
		// Break the gets down into groups by key
		final Map<MemcachedNode, Collection<String>> chunks
			=new HashMap<MemcachedNode, Collection<String>>();
		final NodeLocator locator=conn.getLocator();
		for(String key : keys) {
			validateKey(key);
			final MemcachedNode primaryNode=locator.getPrimary(key);
			MemcachedNode node=null;
			if(primaryNode.isActive()) {
				node=primaryNode;
			} else {
				for(Iterator<MemcachedNode> i=locator.getSequence(key);
					node == null && i.hasNext();) {
					MemcachedNode n=i.next();
					if(n.isActive()) {
						node=n;
					}
				}
				if(node == null) {
					node=primaryNode;
				}
			}
			assert node != null : "Didn't find a node for " + key;
			Collection<String> ks=chunks.get(node);
			if(ks == null) {
				ks=new ArrayList<String>();
				chunks.put(node, ks);
			}
			ks.add(key);
		}

		final CountDownLatch latch=new CountDownLatch(chunks.size());
		final Collection<Operation> ops=new ArrayList<Operation>();

		GetOperation.Callback cb=new GetOperation.Callback() {
				@Override
				public void receivedStatus(OperationStatus status) {
					if(!status.isSuccess()) {
						getLogger().warn("Unsuccessful get:  %s", status);
					}
				}
				@Override
				public void gotData(String k, int flags, byte[] data) {
					m.put(k, tcService.decode(tc,
							new CachedData(flags, data, tc.getMaxSize())));
				}
				@Override
				public void complete() {
					latch.countDown();
				}
		};

		// Now that we know how many servers it breaks down into, and the latch
		// is all set up, convert all of these strings collections to operations
		final Map<MemcachedNode, Operation> mops=
			new HashMap<MemcachedNode, Operation>();

		for(Map.Entry<MemcachedNode, Collection<String>> me
				: chunks.entrySet()) {
			Operation op=opFact.get(me.getValue(), cb);
			mops.put(me.getKey(), op);
			ops.add(op);
		}
		assert mops.size() == chunks.size();
		checkState();
		conn.addOperations(mops);
//		return new BulkGetFuture<T>(m, ops, latch);
		return new RomaBulkGetFuture<T>(m, ops, latch,timeout);
	}

	@Override
	public Future<Map<String, Object>> asyncGetBulk(Collection<String> keys) {
		return asyncGetBulk(keys, transcoder,operationTimeout);
	}
	@Override
	public Future<Map<String, Object>> asyncGetBulk(Collection<String> keys,	long timeout) {
		return asyncGetBulk(keys, transcoder,timeout);
	}
	@Override
	public <T> Future<Map<String, T>> asyncGetBulk(Collection<String> keys,Transcoder<T> tc) {
		return asyncGetBulk(keys, tc,operationTimeout);
	}
	@Override
	public <T> Future<Map<String, T>> asyncGetBulk(Transcoder<T> tc,String... keys) {
		return asyncGetBulk(Arrays.asList(keys), tc);
	}
	@Override
	public Future<Map<String, Object>> asyncGetBulk(String... keys) {
		return asyncGetBulk(Arrays.asList(keys), transcoder);
	}
	@Override
	public <T> Map<String, T> getBulk(Collection<String> keys,Transcoder<T> tc,long timeout) {
		try {
			return asyncGetBulk(keys, tc).get(timeout, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			throw new RuntimeException("Interrupted getting bulk values", e);
		} catch (ExecutionException e) {
			throw new RuntimeException("Failed getting bulk values", e);
		} catch (TimeoutException e) {
			throw new OperationTimeoutException(
				"Timeout waiting for bulkvalues", e);
		}
	}
	@Override
	public Map<String, Object> getBulk(Collection<String> keys) {
		return getBulk(keys, transcoder,operationTimeout);
	}
	@Override
	public Map<String, Object> getBulk(Collection<String> keys, long timeout) {
		return getBulk(keys, transcoder,timeout);
	}
	@Override
	public <T> Map<String, T> getBulk(Collection<String> keys, Transcoder<T> tc) {
		return getBulk(keys, tc,operationTimeout);
	}
	@Override
	public <T> Map<String, T> getBulk(Transcoder<T> tc, String... keys) {
		return getBulk(Arrays.asList(keys), tc,operationTimeout);
	}
	@Override
	public Map<String, Object> getBulk(String... keys) {
		return getBulk(Arrays.asList(keys), transcoder,operationTimeout);
	}
	/**
	 * version
	 */
	@Override
	public Map<SocketAddress, String> getVersions(long timeout) {
		final Map<SocketAddress, String>rv=
			new ConcurrentHashMap<SocketAddress, String>();

		CountDownLatch blatch = broadcastOp(new BroadcastOpFactory(){
			@Override
			public Operation newOp(final MemcachedNode n,
					final CountDownLatch latch) {
				final SocketAddress sa=n.getSocketAddress();
				return opFact.version(
						new OperationCallback() {
							@Override
							public void receivedStatus(OperationStatus s) {
								rv.put(sa, s.getMessage());
							}
							@Override
							public void complete() {
								latch.countDown();
							}
						});
			}});
		try {
			blatch.await(timeout, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			throw new RuntimeException("Interrupted waiting for versions", e);
		}
		return rv;
	}
	@Override
	public Map<SocketAddress, String> getVersions() {
		return getVersions(operationTimeout);
	}
	/**
	 * stats 
	 */
	@Override
	public Map<SocketAddress, Map<String, String>> getStats(final String arg,long timeout) {
		final Map<SocketAddress, Map<String, String>> rv
			=new HashMap<SocketAddress, Map<String, String>>();

		CountDownLatch blatch = broadcastOp(new BroadcastOpFactory(){
			@Override
			public Operation newOp(final MemcachedNode n,
				final CountDownLatch latch) {
				final SocketAddress sa=n.getSocketAddress();
				rv.put(sa, new HashMap<String, String>());
				return opFact.stats(arg,
						new StatsOperation.Callback() {
					@Override
					public void gotStat(String name, String val) {
						rv.get(sa).put(name, val);
					}
					@Override
					public void receivedStatus(OperationStatus status) {
						if(!status.isSuccess()) {
							getLogger().warn("Unsuccessful stat fetch:	%s",
									status);
						}
					}
					@Override
					public void complete() {
						latch.countDown();
					}});
			}});
		try {
			blatch.await(timeout, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			throw new RuntimeException("Interrupted waiting for stats", e);
		}
		return rv;
	}
	@Override
	public Map<SocketAddress, Map<String, String>> getStats() {
		return getStats(null,operationTimeout);
	}
	@Override
	public Map<SocketAddress, Map<String, String>> getStats(long timeout) {
		return getStats(null,timeout);
	}
	@Override
	public Map<SocketAddress, Map<String, String>> getStats(String arg) {
		return getStats(arg,operationTimeout);
	}
	/**
	 * incr
	 */
	@Override
	public long incr(String key, int by) {
		return mutate(Mutator.incr, key, by, 0, -1,operationTimeout);
	}
	@Override
	public long incr(String key, int by, long def) {
		return mutateWithDefault(Mutator.incr, key, by, def, 0,operationTimeout);
	}
	@Override
	public long incr(String key, int by, long def, int exp) {
		return mutateWithDefault(Mutator.incr, key, by, def, exp,operationTimeout);
	}
	@Override
	public long incr(String key, int by, long def, long timeout) {
		return mutateWithDefault(Mutator.incr, key, by, def, 0,timeout);
	}
	@Override
	public long incr(String key, int by, long def, int exp, long timeout) {
		return mutateWithDefault(Mutator.incr, key, by, def, exp,timeout);
	}
	@Override
	public Future<Long> asyncIncr(String key, int by , long timeout) {
		return asyncMutate(Mutator.incr, key, by, 0, -1,timeout);
	}
	@Override
	public Future<Long> asyncIncr(String key, int by) {
		return asyncMutate(Mutator.incr, key, by, 0, -1,operationTimeout);
	}
	/**
	 * decr
	 */
	@Override
	public long decr(String key, int by) {
		return mutate(Mutator.decr, key, by, 0, -1,operationTimeout);
	}
	@Override
	public long decr(String key, int by, long def) {
		return mutateWithDefault(Mutator.decr, key, by, def, 0,operationTimeout);
	}
	@Override
	public long decr(String key, int by, long def, int exp) {
		return mutateWithDefault(Mutator.decr, key, by, def, exp,operationTimeout);
	}
	@Override
	public long decr(String key, int by, long def, long timeout) {
		return mutateWithDefault(Mutator.decr, key, by, def, 0,timeout);
	}
	@Override
	public long decr(String key, int by, long def, int exp, long timeout) {
		return mutateWithDefault(Mutator.decr, key, by, def, exp,timeout);
	}
	@Override
	public Future<Long> asyncDecr(String key, int by,long timeout) {
		return asyncMutate(Mutator.decr, key, by, 0, -1,timeout);
	}
	@Override
	public Future<Long> asyncDecr(String key, int by) {
		return asyncMutate(Mutator.decr, key, by, 0, -1,operationTimeout);
	}
	/**
	 * delete
	 */
	@Override
	public Future<Boolean> delete(String key,long timeout) {
		final CountDownLatch latch=new CountDownLatch(1);
		final OperationFuture<Boolean> rv=new OperationFuture<Boolean>(latch,	timeout);
		DeleteOperation op=opFact.delete(key,
				new OperationCallback() {
					@Override
					public void receivedStatus(OperationStatus s) {
						rv.set(s.isSuccess());
					}
					@Override
					public void complete() {
						latch.countDown();
					}});
		rv.setOperation(op);
		addOp(key, op);
		return rv;
	}
	@Override
	@Deprecated
	public Future<Boolean> delete(String key, int hold) {
		return delete(key);
	}
	@Override
	public Future<Boolean> delete(String key) {
		return delete(key,operationTimeout);
	}	
	/**
	 * flush
	 */
	@Override
	public Future<Boolean> flush(final int delay,long timeout) {
		final AtomicReference<Boolean> flushResult= new AtomicReference<Boolean>(null);
		final ConcurrentLinkedQueue<Operation> ops=new ConcurrentLinkedQueue<Operation>();
		CountDownLatch blatch = broadcastOp(new BroadcastOpFactory(){
			@Override
			public Operation newOp(final MemcachedNode n,	final CountDownLatch latch) {
				Operation op=opFact.flush(delay, new OperationCallback(){
					@Override
					public void receivedStatus(OperationStatus s) {
						flushResult.set(s.isSuccess());
					}
					@Override
					public void complete() {
						latch.countDown();
					}});
				ops.add(op);
				return op;
			}});
		return new OperationFuture<Boolean>(blatch, flushResult,timeout) {
			@Override
			public boolean cancel(boolean ign) {
				boolean rv=false;
				for(Operation op : ops) {
					op.cancel();
					rv |= op.getState() == OperationState.WRITING;
				}
				return rv;
			}
			@Override
			public boolean isCancelled() {
				boolean rv=false;
				for(Operation op : ops) {
					rv |= op.isCancelled();
				}
				return rv;
			}
			@Override
			public boolean isDone() {
				boolean rv=true;
				for(Operation op : ops) {
					rv &= op.getState() == OperationState.COMPLETE;
				}
				return rv || isCancelled();
			}
		};
	}
	@Override
	public Future<Boolean> flush(long timeout) {
		return flush(-1, timeout);
	}
	@Override
	public Future<Boolean> flush(int delay) {
		return flush(delay,operationTimeout);
	}
	@Override
	public Future<Boolean> flush() {
		return flush(-1,operationTimeout);
	}
	/**
	 * threads
	 */
	@Override
	public void run() {
		long lastUpdate = 0;
		while(running) {
			try {
				long now=System.currentTimeMillis();
				conn.handleIO();
				if ( (now - lastUpdate) > ROUTING_UPDATE_TIME) {
					lastUpdate = now;
					conn.asyncReconstruction();
				}
			} catch(IOException e) {
				logRunException(e);
			} catch(CancelledKeyException e) {
				logRunException(e);
			} catch(ClosedSelectorException e) {
				logRunException(e);
			} catch(IllegalStateException e) {
				logRunException(e);
			}
		}
		getLogger().info("Shut down memcached client");
	}
	/**
	 * shutdown
	 */
	@Override
	public void shutdown() {
		shutdown(-1, TimeUnit.MILLISECONDS);
	}
	@Override
	public boolean shutdown(long timeout, TimeUnit unit) {
		// Guard against double shutdowns (bug 8).
		if(shuttingDown) {
			getLogger().info("Suppressing duplicate attempt to shut down");
			return false;
		}
		shuttingDown=true;
		String baseName=getName();
		setName(baseName + " - SHUTTING DOWN");
		boolean rv=false;
		try {
			// Conditionally wait
			if(timeout > 0) {
				setName(baseName + " - SHUTTING DOWN (waiting)");
				rv=waitForQueues(timeout, unit);
			}
		} finally {
			// But always begin the shutdown sequence
			try {
				setName(baseName + " - SHUTTING DOWN (telling client)");
				running=false;
				conn.shutdown();
				setName(baseName + " - SHUTTING DOWN (informed client)");
				tcService.shutdown();
			} catch (IOException e) {
				getLogger().warn("exception while shutting down", e);
			}
		}
		return rv;
	}
	/**
	 * 
	 */
	@Override
	public boolean waitForQueues(long timeout, TimeUnit unit) {
		CountDownLatch blatch = broadcastOp(new BroadcastOpFactory(){
			@Override
			public Operation newOp(final MemcachedNode n,
					final CountDownLatch latch) {
				return opFact.noop(
						new OperationCallback() {
							@Override
							public void complete() {
								latch.countDown();
							}
							@Override
							public void receivedStatus(OperationStatus s) {
								// Nothing special when receiving status, only
								// necessary to complete the interface
							}
						});
			}}, false);
		try {
			// XXX:  Perhaps IllegalStateException should be caught here
			// and the check retried.
			return blatch.await(timeout, unit);
		} catch (InterruptedException e) {
			throw new RuntimeException("Interrupted waiting for queues", e);
		}
	}

	
	/**
	 * extension
	 */
	@Override
	public Future<Object> asyncExtension(long timeout,RomaExtension ex) {
		Pair<RomaExtensionOperation, OperationFuture<Object>> pair = ex.getOperation(timeout);
		Operation op = pair.first;
		OperationFuture<Object> rv = pair.second;
		rv.setOperation(op);
		if ( ex.getType() == RomaExtension.OperationType.KEY ) {
			addOp(ex.getKey(),op);
		}else if( ex.getType() == RomaExtension.OperationType.RANDOM ) {
			randOp(op);
		}
		return rv;
	}
	@Override
	public Future<Object> asyncExtension(RomaExtension ex) {
		return asyncExtension(operationTimeout, ex);
	}
	@Override
	public Object extension(long timeout,RomaExtension ex) {
		try {
			return asyncExtension(timeout, ex).get(timeout, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			throw new RuntimeException("Interrupted waiting for extension", e);
		} catch (ExecutionException e) {
			throw new RuntimeException("Exception waiting for extension", e);
		} catch (TimeoutException e) {
			throw new OperationTimeoutException("Timeout waiting for extension", e);
		}
	}
	@Override
	public Object extension(RomaExtension ex) {
		return extension(operationTimeout,ex);
	}

	/**
	 * mklhash
	 */
	@Override
	public Future<String> asyncMklhash(long timeout) {
		final CountDownLatch latch=new CountDownLatch(1);
		final OperationFuture<String> rv=new OperationFuture<String>(latch, timeout);

		Operation op=opFact.mklhash(
				new RomaMklhashOperation.Callback() {
					private String val=null;
					@Override
					public void receivedStatus(OperationStatus status) {
						rv.set(val);
					}
					@Override
					public void complete() {
						latch.countDown();
					}
					@Override
					public void gotData(String data) {
						val= data;
					}
				});
		rv.setOperation(op);
		randOp(op);
		return rv;
	}
	@Override
	public Future<String> asyncMklhash() {
		return asyncMklhash(operationTimeout);
	}
	@Override
	public String mklhash(long timeout) {
		try {
			return asyncMklhash().get(timeout, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			throw new RuntimeException("Interrupted waiting for mklhash", e);
		} catch (ExecutionException e) {
			throw new RuntimeException("Exception waiting for mklhash", e);
		} catch (TimeoutException e) {
			throw new OperationTimeoutException("Timeout waiting for mklhash", e);
		}
	}
	@Override
	public String mklhash() {
		return mklhash(operationTimeout);
	}
	/**
	 * routingdump
	 */
	@Override
	public Future<String> asyncRoutingdump(long timeout) {
		final CountDownLatch latch=new CountDownLatch(1);
		final OperationFuture<String> rv=new OperationFuture<String>(latch, timeout);
		Operation op=opFact.routingdump(
				new RomaRoutingdumpOperation.Callback() {
					private String val=null;
					@Override
					public void receivedStatus(OperationStatus status) {
						rv.set(val);
					}
					@Override
					public void complete() {
						latch.countDown();
					}
					@Override
					public void gotData(String data) {
						val= data;
					}
				});
		rv.setOperation(op);
		randOp(op);
		return rv;
	}
	@Override
	public Future<String> asyncRoutingdump() {
		return asyncRoutingdump(operationTimeout);
	}
	@Override
	public String routingdump(long timeout) {
		try {
			return asyncRoutingdump().get(timeout, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			throw new RuntimeException("Interrupted waiting for routingdump", e);
		} catch (ExecutionException e) {
			throw new RuntimeException("Exception waiting for routingdump", e);
		} catch (TimeoutException e) {
			throw new OperationTimeoutException("Timeout waiting for routingdump", e);
		}
	}
	@Override
	public String routingdump() {
		return routingdump(operationTimeout);
	}
	/**
	 * reconstruction
	 */
	@Override
	public Future<Boolean> asyncReconstruction(long timeout) {
		return conn.asyncReconstruction();
	}
	@Override
	public Future<Boolean> asyncReconstruction() {
		return asyncReconstruction(operationTimeout);
	}
	@Override
	public void reconstruction(long timeout) {
		try {
			asyncReconstruction(timeout).get(timeout, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			throw new RuntimeException("Interrupted waiting for node-reconstruction", e);
		} catch (ExecutionException e) {
			throw new RuntimeException("Exception waiting for node-reconstruction", e);
		} catch (TimeoutException e) {
			throw new OperationTimeoutException("Timeout waiting for node-reconstruction", e);
		}
	}
	@Override
	public void reconstruction() {
		reconstruction(operationTimeout);
	}
	
	/**
	 *  
	 */
	private void logRunException(Exception e) {
		if(shuttingDown) {
			// There are a couple types of errors that occur during the
			// shutdown sequence that are considered OK.  Log at debug.
			getLogger().debug("Exception occurred during shutdown", e);
		} else {
			getLogger().warn("Problem handling memcached IO", e);
		}
	}
	private void checkState() {
		if(shuttingDown) {
			throw new IllegalStateException("Shutting down");
		}
		assert isAlive() : "IO Thread is not running.";
	}
	private void validateKey(String key) {
		byte[] keyBytes=KeyUtil.getKeyBytes(key);
		if(keyBytes.length > MemcachedClientIF.MAX_KEY_LENGTH) {
			throw new IllegalArgumentException("Key is too long (maxlen = "
					+ MemcachedClientIF.MAX_KEY_LENGTH + ")");
		}
		if(keyBytes.length == 0) {
			throw new IllegalArgumentException(
				"Key must contain at least one character.");
		}
		// Validate the key
		for(byte b : keyBytes) {
			if(b == ' ' || b == '\n' || b == '\r' || b == 0) {
				throw new IllegalArgumentException(
					"Key contains invalid characters:  ``" + key + "''");
			}
		}
	}
	/**
	 * Shared backend function.
	 */
	private <T> Future<Boolean> asyncStore(StoreType storeType, String key,int exp, T value, Transcoder<T> tc,long timeout) {
		CachedData co=tc.encode(value);
		final CountDownLatch latch=new CountDownLatch(1);
		final OperationFuture<Boolean> rv=new OperationFuture<Boolean>(latch,timeout);
		Operation op=opFact.store(storeType, key, co.getFlags(),
				exp, co.getData(), new OperationCallback() {
					@Override
					public void receivedStatus(OperationStatus val) {
						rv.set(val.isSuccess());
					}
					@Override
					public void complete() {
						latch.countDown();
					}});
		rv.setOperation(op);
		addOp(key, op);
		return rv;
	}

	private Future<Boolean> asyncStore(StoreType storeType,String key, int exp, Object value,long timeout) {
		return asyncStore(storeType, key, exp, value, transcoder,timeout);
	}
	private <T> Future<Boolean> asyncCat(ConcatenationType catType, long cas, String key,T value, Transcoder<T> tc,long timeout) {
		CachedData co=tc.encode(value);
		final CountDownLatch latch=new CountDownLatch(1);
		final OperationFuture<Boolean> rv=new OperationFuture<Boolean>(latch,timeout);
		Operation op=opFact.cat(catType, cas, key, co.getData(),
				new OperationCallback() {
			@Override
			public void receivedStatus(OperationStatus val) {
				rv.set(val.isSuccess());
			}
			@Override
			public void complete() {
				latch.countDown();
			}});
		rv.setOperation(op);
		addOp(key, op);
		return rv;
	}
	private long mutateWithDefault(Mutator t, String key,int by, long def, int exp,long timeout) {
		long rv=mutate(t, key, by, def, exp,timeout);
		// The ascii protocol doesn't support defaults, so I added them
		// manually here.
		if(rv == -1) {
			Future<Boolean> f=asyncStore(StoreType.add,key, exp, String.valueOf(def),timeout);
			try {
				if(f.get(timeout, TimeUnit.MILLISECONDS)) {
					rv=def;
				} else {
					rv=mutate(t, key, by, 0, exp,timeout);
					assert rv != -1 : "Failed to mutate or init value";
				}
			} catch (InterruptedException e) {
				throw new RuntimeException("Interrupted waiting for store", e);
			} catch (ExecutionException e) {
				throw new RuntimeException("Failed waiting for store", e);
			} catch (TimeoutException e) {
				throw new OperationTimeoutException(
					"Timeout waiting to mutate or init value", e);
			}
		}
		return rv;
	}

	private Future<Long> asyncMutate(Mutator m, String key, int by, long def,	int exp,long timeout) {
		final CountDownLatch latch = new CountDownLatch(1);
		final OperationFuture<Long> rv = new OperationFuture<Long>(	latch, timeout);
		Operation op = addOp(key, opFact.mutate(m, key, by, def, exp,
				new OperationCallback() {
			@Override
			public void receivedStatus(OperationStatus s) {
				// XXX:  Potential abstraction leak.
				// The handling of incr/decr in the binary protocol
				// Allows us to avoid string processing.
				rv.set(new Long(s.isSuccess() ? s.getMessage() : "-1"));
			}
			@Override
			public void complete() {
				latch.countDown();
			}
		}));
		rv.setOperation(op);
		return rv;
	}
	private long mutate(Mutator m, String key, int by, long def, int exp,long timeout) {
		try {
			return asyncMutate(m, key, by, def, exp, timeout).get(timeout,TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			throw new RuntimeException("Interrupted", e);
		} catch (ExecutionException e) {
			throw new RuntimeException("Failed mutate", e);
		} catch (TimeoutException e) {
			throw new OperationTimeoutException("Timeout waiting for mutate", e);
		}
	}
/*	
	private long mutate(Mutator m, String key, int by, long def, int exp,long timeout) {
		final AtomicLong rv=new AtomicLong();
		final CountDownLatch latch=new CountDownLatch(1);
		addOp(key, opFact.mutate(m, key, by, def, exp, new OperationCallback() {
					@Override
					public void receivedStatus(OperationStatus s) {
						// XXX:  Potential abstraction leak.
						// The handling of incr/decr in the binary protocol
						// Allows us to avoid string processing.
						rv.set(new Long(s.isSuccess()?s.getMessage():"-1"));
					}
					@Override
					public void complete() {
						latch.countDown();
					}}));
		try {
			if (!latch.await(timeout, TimeUnit.MILLISECONDS)) {
				throw new OperationTimeoutException(
					"Mutate operation timed out, unable to modify counter ["
						+ key + "]");
			}
		} catch (InterruptedException e) {
			throw new RuntimeException("Interrupted", e);
		}
		getLogger().debug("Mutation returned %s", rv);
		return rv.get();
	}
*/
	/**
	 *  Operation queing functions.
	 */
	private Operation randOp(final Operation op) {
		checkState();
		conn.randOperation(op);
		return op;
	}
	private Operation addOp(final String key, final Operation op) {
		validateKey(key);
		checkState();
		conn.addOperation(key, op);
		return op;
	}

	private CountDownLatch broadcastOp(final BroadcastOpFactory of) {
		return broadcastOp(of, true);
	}

	private CountDownLatch broadcastOp(BroadcastOpFactory of,boolean checkShuttingDown) {
		if(checkShuttingDown && shuttingDown) {
			throw new IllegalStateException("Shutting down");
		}
		return conn.broadcastOperation(of);
	}
}
