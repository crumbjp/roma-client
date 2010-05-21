package jp.co.rakuten.roma.client.protocol.ascii;

import java.nio.ByteBuffer;

import jp.co.rakuten.roma.client.ops.RomaExtensionOperation;

import net.spy.memcached.KeyUtil;
import net.spy.memcached.ops.OperationCallback;
import net.spy.memcached.ops.OperationState;
import net.spy.memcached.ops.OperationStatus;

public class RomaAlistDeleteOpImpl extends OperationImpl
	implements RomaExtensionOperation{

	private static final int OVERHEAD = 32;
	private static final OperationStatus DELETED = 	new OperationStatus(true, "DELETED");
	private static final OperationStatus NOT_FOUND = 	new OperationStatus(true, "NOT_FOUND");
	private static final OperationStatus NOT_DELETED = 	new OperationStatus(true, "NOT_DELETED");
	private static final String cmd = "alist_delete ";
	private static final Integer cmdsize=cmd.length();
	private final String key;
	protected final byte[] data;

	public RomaAlistDeleteOpImpl(OperationCallback cb, String k,byte [] d) {
		super(cb);
		key=k;
		data=d;
	}

	public final void handleLine(String line) {
		getLogger().debug("Got line %s", line);
		getCallback().receivedStatus(matchStatus(line, DELETED,NOT_FOUND,NOT_DELETED));
		transitionState(OperationState.COMPLETE);
		getLogger().debug(cmd+" complete!");
	}
	public final void handleRead(ByteBuffer b) {
	}

	@Override
	public final void initialize() {
		ByteBuffer b=ByteBuffer.allocate( cmdsize + data.length + KeyUtil.getKeyBytes(key).length + OVERHEAD);
		b.put(cmd.getBytes());
		b.put(key.getBytes());
		b.put(" ".getBytes());
		b.put(String.valueOf(data.length).getBytes());
		b.put("\r\n".getBytes());
		b.put(data);
		b.put("\r\n".getBytes());
		b.flip();
		setBuffer(b);
	}

	@Override
	protected final void wasCancelled() {
		getCallback().receivedStatus(CANCELLED);
	}
}
