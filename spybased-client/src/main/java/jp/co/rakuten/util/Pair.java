package jp.co.rakuten.util;
/**
 * How come jdk isn't equipped with basic classes like This?
 * @author hiroaki.kubota@mail.rakuten.co.jp
 *
 * @param <FIRST>
 * @param <SECOND>
 */
public class Pair<FIRST,SECOND> {
	public FIRST first;
	public SECOND second;
	public Pair( FIRST first , SECOND second) {
		this.first = first;
		this.second = second;
	}
}
