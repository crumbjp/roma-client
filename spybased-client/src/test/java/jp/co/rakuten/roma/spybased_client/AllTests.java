package jp.co.rakuten.roma.spybased_client;
import junit.framework.Test;
import junit.framework.TestSuite;


public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for default package");
		//$JUnit-BEGIN$
		suite.addTestSuite(RomaTest.class);
		suite.addTestSuite(AlistTest.class);
		//$JUnit-END$
		return suite;
	}
}
