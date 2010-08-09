/*
 * main.cc - ????
 *  
 *   Copyright (C) 2010 rakuten 
 *     by hiroaki.kubota <hiroaki.kubota@rakuten.co.jp> 
 *     Date : 2010/06/11
 */

#include <cppunit/TestFixture.h>
#include <cppunit/TestSuite.h>
#include <cppunit/TestCaller.h>
#include <cppunit/Asserter.h>
#include <cppunit/TestAssert.h>
#include <cppunit/SourceLine.h>

#include "normal_test.h"
#include "loop_test.h"
#include "loop_monkey_test.h"
#include "protocol_test.h"
#include "loop_connection_test.h"

using namespace std;

#include <cppunit/TestSuite.h>
#include <cppunit/TestCaller.h>
#include <cppunit/CompilerOutputter.h>
#include <cppunit/TestResult.h>
#include <cppunit/TestResultCollector.h>
#include <cppunit/TestRunner.h>

int main ( int argc , char * argv[]  ){
  CppUnit::TestResultCollector collector;
  CppUnit::TestResult result;
  result.addListener(&collector);
  CppUnit::TestSuite suite;
#ifdef WITH_NORMAL_TEST
  suite.addTest(NomalTest0::getSuite());
  suite.addTest(NomalTest1::getSuite());
#endif
#ifdef WITH_PROTOCOL_TEST
  suite.addTest(ProtocolTest0::getSuite());
  suite.addTest(ProtocolTest1::getSuite());
#endif
#ifdef WITH_LOOP_TEST
  suite.addTest(LoopTest::getSuite());
#endif
#ifdef WITH_LOOP_MONKEY_TEST
  suite.addTest(LoopMonkeyTest::getSuite());
#endif
#ifdef WITH_LOOP_CONN_TEST
  suite.addTest(LoopConnectionTest0::getSuite());
  suite.addTest(LoopConnectionTest1::getSuite());
#endif
  suite.run(&result);
  CppUnit::CompilerOutputter outputter(&collector, CppUnit::stdCOut());
  outputter.write();
  return collector.wasSuccessful() ? 0 : 1;
}
// int main ( int argc , char * argv[]  ){
//   ProtocolTest test;
//   test.setUp();
//   test.testSetStored();
// }
