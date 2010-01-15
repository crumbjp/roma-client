package jp.co.rakuten.roma.client;

import net.spy.memcached.MemcachedNode;

// @@@ コネクションでマップ管理すれば、文字列では無くてインスタンス比較で対応できるかも。そしたら要らない
public interface RomaMemcachedNode extends MemcachedNode{
	String getName();
}