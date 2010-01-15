package jp.co.rakuten.roma.client.protocol.ascii;

import java.nio.ByteBuffer;

import jp.co.rakuten.roma.client.ops.RomaRoutingdumpOperation;

import net.spy.memcached.ops.OperationCallback;
import net.spy.memcached.ops.OperationState;
import net.spy.memcached.ops.OperationStatus;

class RomaRoutingdumpImpl extends OperationImpl 
	implements RomaRoutingdumpOperation {

	private static final OperationStatus END = new OperationStatus(true, "END");
	private final String cmd = "routingdump json";
	private StringBuffer data = null;
	public RomaRoutingdumpImpl(OperationCallback cb){
		super(cb);
	}
	@Override
	public final void handleLine(String line) {
		if(line.equals("END")) {
			getLogger().debug("Got line %s", line);
			((RomaRoutingdumpOperation.Callback)getCallback()).gotData(data.toString());
			getCallback().receivedStatus(END);
			getLogger().debug("mklhash complete!");
			transitionState(OperationState.COMPLETE);
			data = null;
		} else {
			if ( data == null ) {
				data = new StringBuffer();
			}
			data.append(line);
		}
	}

	@Override
	public final void handleRead(ByteBuffer b) {
	}

	@Override
	public final void initialize() {
		// Figure out the length of the request
		int size=18; // Enough for routingdump json\r\n
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
