package jp.co.rakuten.roma.client.protocol.ascii;

import java.nio.ByteBuffer;

import jp.co.rakuten.roma.client.ops.RomaExtensionOperation;

import net.spy.memcached.KeyUtil;
import net.spy.memcached.ops.OperationCallback;
import net.spy.memcached.ops.OperationState;
import net.spy.memcached.ops.OperationStatus;

public class RomaAlistLengthOpImpl extends OperationImpl
	implements RomaExtensionOperation{

	private static final int OVERHEAD = 32;
	private static final OperationStatus END = 	new OperationStatus(true, "END");
	private static final String cmd = "alist_length ";
	private static final Integer cmdsize=cmd.length();
	private final String key;

	public RomaAlistLengthOpImpl(OperationCallback cb, String k) {
		super(cb);
		key=k;
	}

	public final void handleLine(String line) {
		getLogger().debug("Got line %s", line);
		((RomaExtensionOperation.Callback)getCallback()).gotData(0,line.getBytes());
		getCallback().receivedStatus(END);
		transitionState(OperationState.COMPLETE);
		getLogger().debug(cmd+" complete!");
	}
	public final void handleRead(ByteBuffer b) {
	}

	@Override
	public final void initialize() {
		ByteBuffer b=ByteBuffer.allocate( cmdsize + KeyUtil.getKeyBytes(key).length + OVERHEAD);
		b.put(cmd.getBytes());
		b.put(key.getBytes());
		b.put("\r\n".getBytes());
		b.flip();
		setBuffer(b);
	}

	@Override
	protected final void wasCancelled() {
		getCallback().receivedStatus(CANCELLED);
	}
}
