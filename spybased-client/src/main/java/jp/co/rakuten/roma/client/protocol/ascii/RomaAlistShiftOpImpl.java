package jp.co.rakuten.roma.client.protocol.ascii;

import java.nio.ByteBuffer;

import jp.co.rakuten.roma.client.ops.RomaExtensionOperation;

import net.spy.memcached.KeyUtil;
import net.spy.memcached.ops.OperationCallback;

public class RomaAlistShiftOpImpl extends RomaAlistBaseGetsOpImpl
	implements RomaExtensionOperation{

	private static final int OVERHEAD = 32;
	private static final String cmd = "alist_shift ";
	private static final Integer cmdsize=cmd.length();
	private final String key;

	public RomaAlistShiftOpImpl(OperationCallback cb, String k) {
		super(cb);
		key = k;
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
}
