package jp.co.rakuten.roma.client;

public abstract class RomaExtensionAlist implements RomaExtension {
	final String key;
	public RomaExtensionAlist(String k) {
		key = k;
	}
	@Override
	public String getKey() {
		return key;
	}
	@Override
	public OperationType getType() {
		return RomaExtension.OperationType.KEY;
	}
}
