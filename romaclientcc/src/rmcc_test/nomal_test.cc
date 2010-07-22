/*
 * nomal_test.cc - ????
 *  
 *   Copyright (C) 2010 rakuten 
 *     by hiroaki.kubota <hiroaki.kubota@rakuten.co.jp> 
 *     Date : 2010/07/21
 */

#include <cppunit/TestCaller.h>
#include <cppunit/Asserter.h>
#include <cppunit/TestAssert.h>
#include <cppunit/SourceLine.h>

#include "nomal_test.h"
#include <iostream>

static const long TIMEOUT = 1000;

void NomalTest::setUp() {
  set_loglv(0);
  client.get_nodelist().push_back("localhost_11211");
  // client.get_nodelist().push_back("localhost_11212");
  client.num_valid_connection();
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
void NomalTest::tearDown() {
  client.term();
}
void NomalTest::testConnectionRefused() {
  RomaClient c;

  c.get_nodelist().push_back("unknownhost_11211");
  c.init(0);
  try{
    RomaValue v = c.cmd_alist_join("FOO",",",TIMEOUT);
    c.term();
    CPPUNIT_FAIL("Should throw !");
  }catch(const Exception & ex){
  }
}
void NomalTest::testGet() {
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
void NomalTest::testSizedInsert() {
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
void NomalTest::testSizedInsertError() {
  cerr << __PRETTY_FUNCTION__ << endl;
  try{
    rmc_ret_t ret = client.cmd_alist_sized_insert("foo3",3,RomaValue("###",3),TIMEOUT);
    (void)ret;
    CPPUNIT_FAIL("Should throw !");
  }catch(const Exception & ex){
  }
}
void NomalTest::testStoreError() {
  // cerr << __PRETTY_FUNCTION__ << endl;
  // try{
  //   rmc_ret_t ret = client.cmd_store("FOO",RomaValue("bbb",3),100,TIMEOUT);
  //   (void)ret;
  //   CPPUNIT_FAIL("Should throw !");
  // }catch(const Exception & ex){
  // }
}
CppUnit::TestSuite * NomalTest::getSuite(){
  CppUnit::TestSuite *suite = new CppUnit::TestSuite();
  suite->addTest(new CppUnit::TestCaller<NomalTest>("testConnectionRefused",&NomalTest::testConnectionRefused));
  suite->addTest(new CppUnit::TestCaller<NomalTest>("testGet",&NomalTest::testGet));
  suite->addTest(new CppUnit::TestCaller<NomalTest>("testSizedInsert",&NomalTest::testSizedInsert));
  suite->addTest(new CppUnit::TestCaller<NomalTest>("testSizedInsertError",&NomalTest::testSizedInsertError));
  suite->addTest(new CppUnit::TestCaller<NomalTest>("testStoreError",&NomalTest::testStoreError));
  return suite;
}
