package jp.co.rakuten.roma.client.protocol.ascii;

import java.nio.ByteBuffer;

import jp.co.rakuten.roma.client.ops.RomaMklhashOperation;

import net.spy.memcached.ops.OperationCallback;
import net.spy.memcached.ops.OperationState;
import net.spy.memcached.ops.OperationStatus;

class RomaMklhashOpImpl extends OperationImpl
	implements RomaMklhashOperation {

	private static final OperationStatus END = new OperationStatus(true, "END");
	private final String cmd = "mklhash 0";

	public RomaMklhashOpImpl(OperationCallback cb){
		super(cb);
	}
	public final void handleLine(String line) {
		getLogger().debug("Got line %s", line);
		((RomaMklhashOperation.Callback)getCallback()).gotData(line);
		getCallback().receivedStatus(END);
		getLogger().debug("mklhash complete!");
		transitionState(OperationState.COMPLETE);
	}
	public final void handleRead(ByteBuffer b) {
	}

	@Override
	public final void initialize() {
		// Figure out the length of the request
		int size=11; // Enough for mklhash 0\r\n
		ByteBuffer b=ByteBuffer.allocate(size);
		b.put(cmd.getBytes());
		b.put("\r\n".getBytes());
		b.flip();
		setBuffer(b);
	}

	@Override
	protected final void wasCancelled() {
		getCallback().receivedStatus(CANCELLED);
	}

}
