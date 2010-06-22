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

#include "rakuten/rmcc/rmcc.h"

using namespace std;
using namespace rakuten;
using namespace rmcc;
class RomaClientTest : public CppUnit::TestFixture {
public:
  RomaClient client;
  virtual void setUp() {
    char localhost1[] = "localhost_11211";
    char localhost2[] = "localhost_11212";
    client.get_nodelist().push_back(localhost1);
//    client.get_nodelist().push_back(localhost2);
    client.init();
    client.init();
    client.init();
    client.cmd_store("foo4",RomaValue("aaaa",4),100);
    client.cmd_store("foo3",RomaValue("aaa",3),100);
    client.cmd_store("bar4",RomaValue("bbbb",4),100);
    client.cmd_store("bar3",RomaValue("bbb",3),100);
    client.cmd_alist_sized_insert("FOO",1,RomaValue("aaa",3));
    client.cmd_alist_sized_insert("FOO",2,RomaValue("AAA",3));
    client.cmd_alist_sized_insert("BAR",1,RomaValue("bbb",3));
    client.cmd_alist_sized_insert("BAR",2,RomaValue("BBB",3));
  }
  virtual void tearDown() {
    client.term();
  }
  void testGet() {
    cerr << __PRETTY_FUNCTION__ << endl;
    {
      RomaValue v = client.cmd_get("foo4");
      CPPUNIT_ASSERT_EQUAL((long)4,v.length);
      CPPUNIT_ASSERT_EQUAL(string("aaaa"),string(v.data));
    }
    {
      RomaValue v = client.cmd_get("foo3");
      CPPUNIT_ASSERT_EQUAL((long)3,v.length);
      CPPUNIT_ASSERT_EQUAL(string("aaa"),string(v.data));
    }
    {
      RomaValue v = client.cmd_get("bar4");
      CPPUNIT_ASSERT_EQUAL((long)4,v.length);
      CPPUNIT_ASSERT_EQUAL(string("bbbb"),string(v.data));
    }
    {
      RomaValue v = client.cmd_get("bar3");
      CPPUNIT_ASSERT_EQUAL((long)3,v.length);
      CPPUNIT_ASSERT_EQUAL(string("bbb"),string(v.data));
    }

  }
  void testSizedInsert() {
    cerr << __PRETTY_FUNCTION__ << endl;
    try{
    {
      rmc_ret_t ret = client.cmd_alist_sized_insert("FOO",3,RomaValue("###",3));
      CPPUNIT_ASSERT_EQUAL(RMC_RET_OK,ret);
    }
    {
      rmc_ret_t ret = client.cmd_alist_sized_insert("FOO",3,RomaValue("###",3));
      CPPUNIT_ASSERT_EQUAL(RMC_RET_OK,ret);
    }
    {
      RomaValue v = client.cmd_alist_join("FOO",",");
      CPPUNIT_ASSERT_EQUAL(string("###,###,AAA"),string(v.data));
    }
    }catch(const Exception & ex){
      CPPUNIT_FAIL(ex.get_msg());
    }
  }
  void testSizedInsertError() {
    cerr << __PRETTY_FUNCTION__ << endl;
    try{
      rmc_ret_t ret = client.cmd_alist_sized_insert("foo3",3,RomaValue("###",3));
      (void)ret;
      CPPUNIT_FAIL("Should throw !");
    }catch(const Exception & ex){
    }
  }
  static CppUnit::TestSuite * getSuite(){
    CppUnit::TestSuite *suite = new CppUnit::TestSuite();
    suite->addTest(new CppUnit::TestCaller<RomaClientTest>("testGet",&RomaClientTest::testGet));
    suite->addTest(new CppUnit::TestCaller<RomaClientTest>("testSizedInsert",&RomaClientTest::testSizedInsert));
    suite->addTest(new CppUnit::TestCaller<RomaClientTest>("testSizedInsertError",&RomaClientTest::testSizedInsertError));
    return suite;
  }
};

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
  suite.addTest(RomaClientTest::getSuite());
  suite.run(&result);
  CppUnit::CompilerOutputter outputter(&collector, CppUnit::stdCOut());
  outputter.write();
  return collector.wasSuccessful() ? 0 : 1;
}
