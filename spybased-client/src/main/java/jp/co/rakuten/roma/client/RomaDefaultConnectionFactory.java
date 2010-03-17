package jp.co.rakuten.roma.client;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeoutException;

import jp.co.rakuten.roma.client.protocol.ascii.RomaAsciiOperationFactory;

import net.spy.memcached.ConnectionObserver;
import net.spy.memcached.FailureMode;
import net.spy.memcached.MemcachedNode;
import net.spy.memcached.OperationFactory;
import net.spy.memcached.compat.SpyObject;
import net.spy.memcached.ops.Operation;
import net.spy.memcached.protocol.ascii.AsciiMemcachedNodeImpl;
import net.spy.memcached.transcoders.SerializingTranscoder;
import net.spy.memcached.transcoders.Transcoder;

/**
 * Default implementation of ConnectionFactory.
 *
 * <p>
 * This implementation creates connections where the operation queue is an
 * ArrayBlockingQueue and the read and write queues are unbounded
 * LinkedBlockingQueues.  The <code>Redistribute</code> FailureMode is used
 * by default.
 * </p>
 */
public class RomaDefaultConnectionFactory extends SpyObject
	implements RomaConnectionFactory {

	/**
	 * Default failure mode.
	 */
	public static final FailureMode DEFAULT_FAILURE_MODE =
		FailureMode.Redistribute;


	/**
	 * Maximum length of the operation queue returned by this connection
	 * factory.
	 */
	public static final int DEFAULT_OP_QUEUE_LEN=16384;

	/**
	 * The read buffer size for each server connection from this factory.
	 */
	public static final int DEFAULT_READ_BUFFER_SIZE=16384;

    /**
     * Default operation timeout in milliseconds.
     */
    public static final long DEFAULT_OPERATION_TIMEOUT = 1000;

    /**
     * Maximum amount of time (in seconds) to wait between reconnect attempts.
     */
    public static final long DEFAULT_MAX_RECONNECT_DELAY = 30;

	private final int opQueueLen;
	private final int readBufSize;

	/**
	 * Construct a DefaultConnectionFactory with the given parameters.
	 *
	 * @param qLen the queue length.
	 * @param bufSize the buffer size
	 */
	public RomaDefaultConnectionFactory(int qLen, int bufSize) {
		super();
		opQueueLen=qLen;
		readBufSize=bufSize;
	}


	/**
	 * Create a DefaultConnectionFactory with the default parameters.
	 */
	public RomaDefaultConnectionFactory() {
		this(DEFAULT_OP_QUEUE_LEN, DEFAULT_READ_BUFFER_SIZE);
	}

	public MemcachedNode createMemcachedNode(String name, SocketAddress sa,
			SocketChannel c, int bufSize) {

		OperationFactory of = getOperationFactory();
		if(of instanceof RomaOperationFactory ) {
			return new AsciiMemcachedNodeImpl(sa, c, bufSize,
					createReadOperationQueue(),
					createWriteOperationQueue(),
					createOperationQueue());
		} else {
			throw new IllegalStateException(
				"Unhandled operation factory type " + of);
		}
	}

	/* (non-Javadoc)
	 * @see net.spy.memcached.ConnectionFactory#createConnection(java.util.List)
	 */
	public RomaConnection createConnection(List<String> names)
		throws IOException{
		return new RomaConnection(getReadBufSize(), this, names,
			getInitialObservers(), getFailureMode(), getOperationFactory());
	}

	/* (non-Javadoc)
	 * @see net.spy.memcached.ConnectionFactory#getFailureMode()
	 */
	public FailureMode getFailureMode() {
		return DEFAULT_FAILURE_MODE;
	}

	/* (non-Javadoc)
	 * @see net.spy.memcached.ConnectionFactory#createOperationQueue()
	 */
	public BlockingQueue<Operation> createOperationQueue() {
		return new ArrayBlockingQueue<Operation>(getOpQueueLen());
	}

	/* (non-Javadoc)
	 * @see net.spy.memcached.ConnectionFactory#createReadOperationQueue()
	 */
	public BlockingQueue<Operation> createReadOperationQueue() {
		return new LinkedBlockingQueue<Operation>();
	}

	/* (non-Javadoc)
	 * @see net.spy.memcached.ConnectionFactory#createWriteOperationQueue()
	 */
	public BlockingQueue<Operation> createWriteOperationQueue() {
		return new LinkedBlockingQueue<Operation>();
	}

	/* (non-Javadoc)
	 * @see net.spy.memcached.ConnectionFactory#createLocator(java.util.List)
	 */
	public RomaNodeLocator createLocator() {
		return new RomaNodeLocator();
	}

	/**
	 * Get the op queue length set at construct time.
	 */
	public int getOpQueueLen() {
		return opQueueLen;
	}

	/* (non-Javadoc)
	 * @see net.spy.memcached.ConnectionFactory#getReadBufSize()
	 */
	public int getReadBufSize() {
		return readBufSize;
	}

	/* (non-Javadoc)
	 * @see net.spy.memcached.ConnectionFactory#getOperationFactory()
	 */
	public RomaOperationFactory getOperationFactory() {
		return new RomaAsciiOperationFactory();
	}

	/* (non-Javadoc)
	 * @see net.spy.memcached.ConnectionFactory#getOperationTimeout()
	 */
	public long getOperationTimeout() {
		return DEFAULT_OPERATION_TIMEOUT;
	}

	/* (non-Javadoc)
	 * @see net.spy.memcached.ConnectionFactory#isDaemon()
	 */
	public boolean isDaemon() {
		return false;
	}

	/* (non-Javadoc)
	 * @see net.spy.memcached.ConnectionFactory#getInitialObservers()
	 */
	public Collection<ConnectionObserver> getInitialObservers() {
		return Collections.emptyList();
	}

	/* (non-Javadoc)
	 * @see net.spy.memcached.ConnectionFactory#getDefaultTranscoder()
	 */
	public Transcoder<Object> getDefaultTranscoder() {
		return new SerializingTranscoder();
	}

	/* (non-Javadoc)
	 * @see net.spy.memcached.ConnectionFactory#useNagleAlgorithm()
	 */
	public boolean useNagleAlgorithm() {
		return false;
	}

	/* (non-Javadoc)
	 * @see net.spy.memcached.ConnectionFactory#shouldOptimize()
	 */
	public boolean shouldOptimize() {
		return true;
	}

	/* (non-Javadoc)
	 * @see net.spy.memcached.ConnectionFactory#getMaxReconnectDelay()
	 */
	public long getMaxReconnectDelay() {
		return DEFAULT_MAX_RECONNECT_DELAY;
	}

}
