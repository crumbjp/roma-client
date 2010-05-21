package jp.co.rakuten.roma.client.protocol.ascii;

import java.nio.ByteBuffer;

import jp.co.rakuten.roma.client.ops.RomaExtensionOperation;

import net.spy.memcached.ops.OperationCallback;
import net.spy.memcached.ops.OperationState;
import net.spy.memcached.ops.OperationStatus;

public abstract class RomaAlistBaseGetsOpImpl extends OperationImpl
	implements RomaExtensionOperation{

	private static final OperationStatus END = 	new OperationStatus(true, "END");
	// private int alist_size = 0;
	private int flg;
	private int readOffset = 0;
	private byte[] data = null;
	private int ndata = 0;
	private byte lookingFor = '\0';

	public RomaAlistBaseGetsOpImpl(OperationCallback cb) {
		super(cb);
	}

	public final void handleLine(String line) {
		if(line.equals("END")) {
			getLogger().debug("Get complete!");
			getCallback().receivedStatus(END);
			transitionState(OperationState.COMPLETE);
			data=null;
			getLogger().debug("alist_gets(base) complete!");
		} else if(line.startsWith("VALUE ")) {
			getLogger().debug("Got line %s", line);
			String[] stuff=line.split(" ");
			assert stuff[0].equals("VALUE");
			ndata = Integer.parseInt(stuff[3]);
			flg = Integer.parseInt(stuff[2]);
				data=new byte[ndata];
				getLogger().debug("Set read type to data");
				setReadType(OperationReadType.DATA);
		} else {
			assert false : "Unknown line type: " + line;
		}
	}
		
	public final void handleRead(ByteBuffer b) {
		assert data != null;
		// This will be the case, because we'll clear them when it's not.
		assert readOffset <= data.length
		: "readOffset is " + readOffset + " data.length is " + data.length;
		getLogger().debug("readOffset: %d, length: %d",
				readOffset, data.length);
		// If we're not looking for termination, we're still looking for data
		if(lookingFor == '\0') {
			int toRead=data.length - readOffset;
			int available=b.remaining();
			toRead=Math.min(toRead, available);
			getLogger().debug("Reading %d bytes", toRead);
			b.get(data, readOffset, toRead);
			readOffset+=toRead;
		}
		// Transition us into a ``looking for \r\n'' kind of state if we've
		// read enough and are still in a data state.
		if(readOffset == data.length && lookingFor == '\0') {
			// The callback is most likely a get callback.  If it's not, then
			// it's a gets callback.
			RomaExtensionOperation.Callback gcb=(RomaExtensionOperation.Callback)getCallback();
			gcb.gotData(flg,data);
			lookingFor='\r';
		}
		// If we're looking for an ending byte, let's go find it.
		if(lookingFor != '\0' && b.hasRemaining()) {
			do {
				byte tmp=b.get();
				assert tmp == lookingFor : "Expecting " + lookingFor + ", got "
					+ (char)tmp;
				switch(lookingFor) {
					case '\r': lookingFor='\n'; break;
					case '\n': lookingFor='\0'; break;
					default:
						assert false: "Looking for unexpected char: "
							+ (char)lookingFor;
				}
			} while(lookingFor != '\0' && b.hasRemaining());
			// Completed the read, reset stuff.
			if(lookingFor == '\0') {
				data=null;
				readOffset=0;
				getLogger().debug("Setting read type back to line.");
				setReadType(OperationReadType.LINE);
//				parseStage = PARSE_STAGE.ALIST_HEAD;
			}
		}
	}

	@Override
	protected final void wasCancelled() {
		getCallback().receivedStatus(CANCELLED);
	}
}
