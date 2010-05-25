package jp.co.rakuten.roma.client;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import net.spy.memcached.CASResponse;
import net.spy.memcached.CASValue;
import net.spy.memcached.transcoders.Transcoder;

public class SimpleRomaClientImpl implements SimpleRomaClient {
	final RomaClientImpl impl;
	public SimpleRomaClientImpl(RomaClientImpl c) {
		impl = c;
	}
	/**
	 * Get a memcache client operating on the specified memcached locations.
	 *
	 * @param ia the memcached locations
	 * @throws IOException if connections cannot be established
	 * @throws TimeoutException 
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	public SimpleRomaClientImpl(String... names) throws IOException, InterruptedException, ExecutionException, TimeoutException {
		this(new RomaClientImpl(Arrays.asList(names)));
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
	public SimpleRomaClientImpl(List<String> names)
		throws IOException, InterruptedException, ExecutionException, TimeoutException {
		this(new RomaClientImpl(names));
	}

	@Override
	public Transcoder<Object> getTranscoder() {
		return impl.getTranscoder();
	}
	@Override
	public Boolean add(String key, int exp, Object o){
		try {
			return impl.add(key, exp, o).get();
		} catch (InterruptedException e) {
			throw new RuntimeException("Interrupted waiting for value", e);
		} catch (ExecutionException e) {
			throw new RuntimeException("Exception waiting for value", e);
		}
	}

	@Override
	public Boolean add(String key, int exp, Object o, long timeout){
		try {
			return impl.add(key, exp, o, timeout).get();
		} catch (InterruptedException e) {
			throw new RuntimeException("Interrupted waiting for value", e);
		} catch (ExecutionException e) {
			throw new RuntimeException("Exception waiting for value", e);
		}
	}

	@Override
	public Boolean append(long cas, String key, Object val) {
		try {
			return impl.append(cas, key, val).get();
		} catch (InterruptedException e) {
			throw new RuntimeException("Interrupted waiting for value", e);
		} catch (ExecutionException e) {
			throw new RuntimeException("Exception waiting for value", e);
		}
	}

	@Override
	public Boolean append(long cas, String key, Object val, long timeout) {
		try {
			return impl.append(cas, key, val, timeout).get();
		} catch (InterruptedException e) {
			throw new RuntimeException("Interrupted waiting for value", e);
		} catch (ExecutionException e) {
			throw new RuntimeException("Exception waiting for value", e);
		}
	}

	@Override
	public CASResponse cas(String key, long casId, Object value) {
		return impl.cas(key, casId, value);
	}

	@Override
	public CASResponse cas(String key, long casId, Object value, long timeout) {
		return impl.cas(key, casId, value, timeout);
	}

	@Override
	public long decr(String key, int by) {
		return impl.decr(key, by);
	}

	@Override
	public long decr(String key, int by, long def) {
		return impl.decr(key, by, def);
	}

	@Override
	public long decr(String key, int by, long def, long timeout) {
		return impl.decr(key, by, def, timeout);
	}

	@Override
	public Boolean delete(String key, int hold)  {
		try {
			return impl.delete(key, hold).get();
		} catch (InterruptedException e) {
			throw new RuntimeException("Interrupted waiting for value", e);
		} catch (ExecutionException e) {
			throw new RuntimeException("Exception waiting for value", e);
		}
	}

	@Override
	public Boolean delete(String key) {
		try {
			return impl.delete(key).get();
		} catch (InterruptedException e) {
			throw new RuntimeException("Interrupted waiting for value", e);
		} catch (ExecutionException e) {
			throw new RuntimeException("Exception waiting for value", e);
		}
	}

	@Override
	public Boolean delete(String key, long timeout) {
		try {
			return impl.delete(key,timeout).get();
		} catch (InterruptedException e) {
			throw new RuntimeException("Interrupted waiting for value", e);
		} catch (ExecutionException e) {
			throw new RuntimeException("Exception waiting for value", e);
		}
	}

	@Override
	public Object extension(RomaExtension extention) {
		return impl.extension(extention);
	}

	@Override
	public Boolean flush(int delay) {
		try {
			return impl.flush(delay).get();
		} catch (InterruptedException e) {
			throw new RuntimeException("Interrupted waiting for value", e);
		} catch (ExecutionException e) {
			throw new RuntimeException("Exception waiting for value", e);
		}
	}

	@Override
	public Boolean flush(int delay, long timeout) {
		try {
			return impl.flush(delay, timeout).get();
		} catch (InterruptedException e) {
			throw new RuntimeException("Interrupted waiting for value", e);
		} catch (ExecutionException e) {
			throw new RuntimeException("Exception waiting for value", e);
		}
	}

	@Override
	public Boolean flush() {
		try {
			return impl.flush().get();
		} catch (InterruptedException e) {
			throw new RuntimeException("Interrupted waiting for value", e);
		} catch (ExecutionException e) {
			throw new RuntimeException("Exception waiting for value", e);
		}
	}

	@Override
	public Boolean flush(long timeout) {
		try {
			return impl.flush(timeout).get();
		} catch (InterruptedException e) {
			throw new RuntimeException("Interrupted waiting for value", e);
		} catch (ExecutionException e) {
			throw new RuntimeException("Exception waiting for value", e);
		}
	}

	@Override
	public Object get(String key) {
		return impl.get(key);
	}

	@Override
	public Object get(String key, long timeout) {
		return impl.get(key, timeout);
	}

	@Override
	public Map<String, Object> getBulk(Collection<String> keys) {
		return impl.getBulk(keys);
	}

	@Override
	public Map<String, Object> getBulk(Collection<String> keys, long timeout) {
		return impl.getBulk(keys, timeout);
	}

	@Override
	public Map<String, Object> getBulk(String... keys) {
		return impl.getBulk(Arrays.asList(keys));
	}

	@Override
	public CASValue<Object> gets(String key) {
		return impl.gets(key);
	}

	@Override
	public CASValue<Object> gets(String key, long timeout) {
		return impl.gets(key, timeout);
	}

	@Override
	public long incr(String key, int by) {
		return impl.incr(key, by);
	}

	@Override
	public long incr(String key, int by, long def) {
		return impl.incr(key, by, def);
	}

	@Override
	public long incr(String key, int by, long def, long timeout) {
		return impl.incr(key, by, def, timeout);
	}

	@Override
	public String mklhash() {
		return impl.mklhash(); 
	}

	@Override
	public String mklhash(long timeout) {
		return impl.mklhash(timeout);
	}

	@Override
	public Boolean prepend(long cas, String key, Object val) {
		try {
			return impl.prepend(cas, key, val).get();
		} catch (InterruptedException e) {
			throw new RuntimeException("Interrupted waiting for value", e);
		} catch (ExecutionException e) {
			throw new RuntimeException("Exception waiting for value", e);
		}
	}

	@Override
	public Boolean prepend(long cas, String key, Object val, long timeout) {
		try {
			return impl.prepend(cas, key, val, timeout).get();
		} catch (InterruptedException e) {
			throw new RuntimeException("Interrupted waiting for value", e);
		} catch (ExecutionException e) {
			throw new RuntimeException("Exception waiting for value", e);
		}
	}

	@Override
	public void reconstruction() {
		impl.reconstruction();
	}

	@Override
	public void reconstruction(long timeout) {
		impl.reconstruction(timeout);
	}

	@Override
	public Boolean replace(String key, int exp, Object o) {
		try {
			return impl.replace(key, exp, o).get();
		} catch (InterruptedException e) {
			throw new RuntimeException("Interrupted waiting for value", e);
		} catch (ExecutionException e) {
			throw new RuntimeException("Exception waiting for value", e);
		}
	}

	@Override
	public Boolean replace(String key, int exp, Object o, long timeout) {
		try {
			return impl.replace(key, exp, o, timeout).get();
		} catch (InterruptedException e) {
			throw new RuntimeException("Interrupted waiting for value", e);
		} catch (ExecutionException e) {
			throw new RuntimeException("Exception waiting for value", e);
		}
	}

	@Override
	public String routingdump() {
		return impl.routingdump();
	}

	@Override
	public String routingdump(long timeout) {
		return impl.routingdump(timeout);
	}

	@Override
	public Boolean set(String key, int exp, Object o) {
		try {
			return impl.set(key, exp, o).get();
		} catch (InterruptedException e) {
			throw new RuntimeException("Interrupted waiting for value", e);
		} catch (ExecutionException e) {
			throw new RuntimeException("Exception waiting for value", e);
		}
	}

	@Override
	public Boolean set(String key, int exp, Object o, long timeout) {
		try {
			return impl.set(key, exp, o, timeout).get();
		} catch (InterruptedException e) {
			throw new RuntimeException("Interrupted waiting for value", e);
		} catch (ExecutionException e) {
			throw new RuntimeException("Exception waiting for value", e);
		}
	}

	@Override
	public void shutdown() {
		impl.shutdown();
	}

	@Override
	public void shutdown(long timeout) {
		impl.shutdown(timeout, TimeUnit.MICROSECONDS);
	}
}
