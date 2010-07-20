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
static const long TIMEOUT = 1000;
class RomaClientTest : public CppUnit::TestFixture {
public:
  RomaClient client;
  virtual void setUp() {
    set_loglv(0);
    client.get_nodelist().push_back("localhost_11211");
    // client.get_nodelist().push_back("localhost_11212");
    client.init(ROUTING_MODE_USE);
    client.init(0);
    client.init(0);
    client.cmd_store("foo4",RomaValue("aaaa",4),100,TIMEOUT);
    client.cmd_store("foo3",RomaValue("aaa",3),100,TIMEOUT);
    client.cmd_store("bar4",RomaValue("bbbb",4),100,TIMEOUT);
    client.cmd_store("bar3",RomaValue("bbb",3),100,TIMEOUT);
    client.cmd_alist_sized_insert("FOO",1,RomaValue("aaa",3),TIMEOUT);
    client.cmd_alist_sized_insert("FOO",2,RomaValue("AAA",3),TIMEOUT);
    client.cmd_alist_sized_insert("BAR",1,RomaValue("bbb",3),TIMEOUT);
    client.cmd_alist_sized_insert("BAR",2,RomaValue("BBB",3),TIMEOUT);
  }
  virtual void tearDown() {
    client.term();
  }
  void testGet() {
    cerr << __PRETTY_FUNCTION__ << endl;
    {
      RomaValue v = client.cmd_get("foo4",TIMEOUT);
      CPPUNIT_ASSERT_EQUAL((long)4,v.length);
      CPPUNIT_ASSERT_EQUAL(string("aaaa"),string(v.data));
    }
    {
      RomaValue v = client.cmd_get("foo3",TIMEOUT);
      CPPUNIT_ASSERT_EQUAL((long)3,v.length);
      CPPUNIT_ASSERT_EQUAL(string("aaa"),string(v.data));
    }
    {
      RomaValue v = client.cmd_get("bar4",TIMEOUT);
      CPPUNIT_ASSERT_EQUAL((long)4,v.length);
      CPPUNIT_ASSERT_EQUAL(string("bbbb"),string(v.data));
    }
    {
      RomaValue v = client.cmd_get("bar3",TIMEOUT);
      CPPUNIT_ASSERT_EQUAL((long)3,v.length);
      CPPUNIT_ASSERT_EQUAL(string("bbb"),string(v.data));
    }

  }
  void testSizedInsert() {
    cerr << __PRETTY_FUNCTION__ << endl;
    try{
    {
      rmc_ret_t ret = client.cmd_alist_sized_insert("FOO",3,RomaValue("###",3),TIMEOUT);
      CPPUNIT_ASSERT_EQUAL(RMC_RET_OK,ret);
    }
    {
      rmc_ret_t ret = client.cmd_alist_sized_insert("FOO",3,RomaValue("###",3),TIMEOUT);
      CPPUNIT_ASSERT_EQUAL(RMC_RET_OK,ret);
    }
    {
      RomaValue v = client.cmd_alist_join("FOO",",",TIMEOUT);
      cerr << v.length << endl;
      CPPUNIT_ASSERT_EQUAL(string("###,###,AAA"),string(v.data));
    }
    }catch(const Exception & ex){
      CPPUNIT_FAIL(ex.get_msg());
    }
  }
  void testSizedInsertError() {
    cerr << __PRETTY_FUNCTION__ << endl;
    try{
      rmc_ret_t ret = client.cmd_alist_sized_insert("foo3",3,RomaValue("###",3),TIMEOUT);
      (void)ret;
      CPPUNIT_FAIL("Should throw !");
    }catch(const Exception & ex){
    }
  }
  void testStoreError() {
    cerr << __PRETTY_FUNCTION__ << endl;
    try{
      rmc_ret_t ret = client.cmd_store("FOO",RomaValue("bbb",3),100,TIMEOUT);
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
    suite->addTest(new CppUnit::TestCaller<RomaClientTest>("testStoreError",&RomaClientTest::testStoreError));
    return suite;
  }
};


class RomaClientTestLoop0 : public CppUnit::TestFixture {
public:
  RomaClient client;
  virtual void setUp() {
    set_loglv(0);
    client.get_nodelist().push_back("localhost_11211");
    client.init(0);
  }
  virtual void tearDown() {
    client.term();
  }
  void testLoop() {
    cerr << __PRETTY_FUNCTION__ << endl;
    client.cmd_store("AAAA",RomaValue("aaaa",4),0,TIMEOUT);
    client.cmd_store("BBBB",RomaValue("bbbb",4),0,TIMEOUT);
    client.cmd_store("CCCC",RomaValue("cccc",4),0,TIMEOUT);
    client.cmd_store("DDDD",RomaValue("dddd",4),0,TIMEOUT);
    client.cmd_store("EEEE",RomaValue("eeee",4),0,TIMEOUT);
    for (int i=0 ;i<180;i++ ) {
      {
        RomaValue v = client.cmd_get("AAAA",TIMEOUT);
        CPPUNIT_ASSERT_EQUAL((long)4,v.length);
        CPPUNIT_ASSERT_EQUAL(string("aaaa"),string(v.data));
      }
      {
        RomaValue v = client.cmd_get("BBBB",TIMEOUT);
        CPPUNIT_ASSERT_EQUAL((long)4,v.length);
        CPPUNIT_ASSERT_EQUAL(string("bbbb"),string(v.data));
      }
      {
        RomaValue v = client.cmd_get("CCCC",TIMEOUT);
        CPPUNIT_ASSERT_EQUAL((long)4,v.length);
        CPPUNIT_ASSERT_EQUAL(string("cccc"),string(v.data));
      }
      {
        RomaValue v = client.cmd_get("DDDD",TIMEOUT);
        CPPUNIT_ASSERT_EQUAL((long)4,v.length);
        CPPUNIT_ASSERT_EQUAL(string("dddd"),string(v.data));
      }
      sleep(1);
    }
  }
  static CppUnit::TestSuite * getSuite(){
    CppUnit::TestSuite *suite = new CppUnit::TestSuite();
    suite->addTest(new CppUnit::TestCaller<RomaClientTestLoop0>("testLoop",&RomaClientTestLoop0::testLoop));
    return suite;
  }
};

class RomaClientTestLoop1 : public CppUnit::TestFixture {
public:
  RomaClient client;
  virtual void setUp() {
    set_loglv(0);
    client.get_nodelist().push_back("localhost_11211");
    client.init(1);
  }
  virtual void tearDown() {
    client.term();
  }
  void testLoop1() {
    cerr << __PRETTY_FUNCTION__ << endl;
    client.cmd_store("AAAA",RomaValue("aaaa",4),0,TIMEOUT);
    client.cmd_store("BBBB",RomaValue("bbbb",4),0,TIMEOUT);
    client.cmd_store("CCCC",RomaValue("cccc",4),0,TIMEOUT);
    client.cmd_store("DDDD",RomaValue("dddd",4),0,TIMEOUT);
    client.cmd_store("EEEE",RomaValue("eeee",4),0,TIMEOUT);
    for (int i=0 ;i<180;i++ ) {
      {
        RomaValue v = client.cmd_get("AAAA",TIMEOUT);
        CPPUNIT_ASSERT_EQUAL((long)4,v.length);
        CPPUNIT_ASSERT_EQUAL(string("aaaa"),string(v.data));
      }
      {
        RomaValue v = client.cmd_get("BBBB",TIMEOUT);
        CPPUNIT_ASSERT_EQUAL((long)4,v.length);
        CPPUNIT_ASSERT_EQUAL(string("bbbb"),string(v.data));
      }
      {
        RomaValue v = client.cmd_get("CCCC",TIMEOUT);
        CPPUNIT_ASSERT_EQUAL((long)4,v.length);
        CPPUNIT_ASSERT_EQUAL(string("cccc"),string(v.data));
      }
      {
        RomaValue v = client.cmd_get("DDDD",TIMEOUT);
        CPPUNIT_ASSERT_EQUAL((long)4,v.length);
        CPPUNIT_ASSERT_EQUAL(string("dddd"),string(v.data));
      }
      sleep(1);
    }
  }
  static CppUnit::TestSuite * getSuite(){
    CppUnit::TestSuite *suite = new CppUnit::TestSuite();
    suite->addTest(new CppUnit::TestCaller<RomaClientTestLoop1>("testLoop1",&RomaClientTestLoop1::testLoop1));
    return suite;
  }
};


class RomaClientTestLoop2 : public CppUnit::TestFixture {
public:
  RomaClient client;
  virtual void setUp() {
    set_loglv(0);
    client.get_nodelist().push_back("localhost_11211");
  }
  virtual void tearDown() {
    client.term();
  }
  void testLoop2() {
    cerr << __PRETTY_FUNCTION__ << endl;
    for (int i=0 ;i<100;i++ ) {
      try {
        char host1[] = "localhost_11211";
        char host2[] = "localhost_11212";
        client.get_nodelist().push_back(host1);
        client.get_nodelist().push_back(host2);
        client.init(1);
        client.cmd_store("AAAA",RomaValue("aaaa",4),0,TIMEOUT);
        client.cmd_store("BBBB",RomaValue("bbbb",4),0,TIMEOUT);
        client.cmd_store("CCCC",RomaValue("cccc",4),0,TIMEOUT);
        client.cmd_store("DDDD",RomaValue("dddd",4),0,TIMEOUT);
        client.cmd_store("EEEE",RomaValue("eeee",4),0,TIMEOUT);
        {
          RomaValue v = client.cmd_get("AAAA",TIMEOUT);
        }
        {
          RomaValue v = client.cmd_get("BBBB",TIMEOUT);
        }
        {
          RomaValue v = client.cmd_get("CCCC",TIMEOUT);
        }
        {
          RomaValue v = client.cmd_get("DDDD",TIMEOUT);
        }
      }catch(const Exception & ex){
        cerr << "****************************************" << endl;
      }
    }
  }
  static CppUnit::TestSuite * getSuite(){
    CppUnit::TestSuite *suite = new CppUnit::TestSuite();
    suite->addTest(new CppUnit::TestCaller<RomaClientTestLoop2>("testLoop2",&RomaClientTestLoop2::testLoop2));
    return suite;
  }
};

#include <cppunit/TestSuite.h>
#include <cppunit/TestCaller.h>
#include <cppunit/CompilerOutputter.h>
#include <cppunit/TestResult.h>
#include <cppunit/TestResultCollector.h>
#include <cppunit/TestRunner.h>

// int main ( int argc , char * argv[]  ){
//   CppUnit::TestResultCollector collector;
//   CppUnit::TestResult result;
//   result.addListener(&collector);
//   CppUnit::TestSuite suite;
//   // suite.addTest(RomaClientTest::getSuite());
//   //suite.addTest(RomaClientTestLoop0::getSuite());
//   suite.addTest(RomaClientTestLoop1::getSuite());
//   suite.run(&result);
//   CppUnit::CompilerOutputter outputter(&collector, CppUnit::stdCOut());
//   outputter.write();
//   return collector.wasSuccessful() ? 0 : 1;
// }
int main ( int argc , char * argv[]  ){
  RomaClientTestLoop2 test;
  test.setUp();
  test.testLoop2();
}
