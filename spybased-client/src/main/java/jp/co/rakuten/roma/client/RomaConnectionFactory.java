package jp.co.rakuten.roma.client;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import net.spy.memcached.ConnectionObserver;
import net.spy.memcached.FailureMode;
import net.spy.memcached.MemcachedNode;
import net.spy.memcached.ops.Operation;
import net.spy.memcached.transcoders.Transcoder;

/**
 * Factory for creating instances of MemcachedConnection.
 * This is used to provide more fine-grained configuration of connections.
 */
public interface RomaConnectionFactory {

	/**
	 * Create a MemcachedConnection for the given SocketAddresses.
	 *
	 * @param addrs the addresses of the memcached servers
	 * @return a new MemcachedConnection connected to those addresses
	 * @throws IOException for problems initializing the memcached connections
	 * @throws TimeoutException 
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	RomaConnection createConnection(List<String> names)
		throws IOException, InterruptedException, ExecutionException, TimeoutException;

	/**
	 * Create a new memcached node.
	 */
	MemcachedNode createMemcachedNode(String name,SocketAddress sa,
			SocketChannel c, int bufSize);

	/**
	 * Create a BlockingQueue for operations for a connection.
	 */
	BlockingQueue<Operation> createOperationQueue();

	/**
	 * Create a BlockingQueue for the operations currently expecting to read
	 * responses from memcached.
	 */
	BlockingQueue<Operation> createReadOperationQueue();

	/**
	 * Create a BlockingQueue for the operations currently expecting to write
	 * requests to memcached.
	 */
	BlockingQueue<Operation> createWriteOperationQueue();

	/**
	 * Create a NodeLocator instance for the given list of nodes.
	 */
	RomaNodeLocator createLocator();

	/**
	 * Get the operation factory for connections built by this connection
	 * factory.
	 */
	RomaOperationFactory getOperationFactory();

	/**
	 * Get the operation timeout used by this connection.
	 */
	long getOperationTimeout();

	/**
	 * If true, the IO thread should be a daemon thread.
	 */
	boolean isDaemon();

	/**
	 * If true, the nagle algorithm will be used on connected sockets.
	 *
	 * <p>
	 * See {@link java.net.Socket#setTcpNoDelay(boolean)} for more information.
	 * </p>
	 */
	boolean useNagleAlgorithm();

	/**
	 * Observers that should be established at the time of connection
	 * instantiation.
	 *
	 * These observers will see the first connection established.
	 */
	Collection<ConnectionObserver> getInitialObservers();

	/**
	 * Get the default failure mode for the underlying connection.
	 */
	FailureMode getFailureMode();

	/**
	 * Get the default transcoder to be used in connections created by this
	 * factory.
	 */
	Transcoder<Object> getDefaultTranscoder();

	/**
	 * If true, low-level optimization is in effect.
	 */
	boolean shouldOptimize();

	/*
	 * Get the read buffer size set at construct time.
	 */
	int getReadBufSize();

	/**
	 * Maximum number of milliseconds to wait between reconnect attempts.
	 */
	long getMaxReconnectDelay();
}
