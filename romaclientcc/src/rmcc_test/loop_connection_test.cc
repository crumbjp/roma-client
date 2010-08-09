/*
 * loop_connection_test.cc - ????
 *  
 *   Copyright (C) 2010 rakuten 
 *     by hiroaki.kubota <hiroaki.kubota@rakuten.co.jp> 
 *     Date : 2010/07/21
 */

#include <cppunit/TestCaller.h>
#include <cppunit/Asserter.h>
#include <cppunit/TestAssert.h>
#include <cppunit/SourceLine.h>

#include "loop_connection_test.h"
#include <iostream>
#include <stdlib.h>

static const long TIMEOUT = 1000;

void LoopConnectionTest0::setUp() {
  set_loglv(0);
  client.get_nodelist().push_back("localhost_11211");
  client.init(0);
}
void LoopConnectionTest1::setUp() {
  set_loglv(0);
  client.get_nodelist().push_back("localhost_11211");
  client.init(ROUTING_MODE_USE);
}
void LoopConnectionTest::tearDown() {
  client.term();
  system("roma.bash start");
  sleep(10);
}
void LoopConnectionTest::testLoopAllNodeDown() {
  cerr << __PRETTY_FUNCTION__ << endl;
  {
    client.cmd_store("AAAA",RomaValue("aaaa",4),0,TIMEOUT);
    RomaValue v = client.cmd_get("AAAA",TIMEOUT);
    CPPUNIT_ASSERT_EQUAL((long)4,v.length);
    CPPUNIT_ASSERT_EQUAL(string("aaaa"),string(v.data));
  }
  // ---------------
  // All node down.
  system("roma.bash stop");
  sleep(1);
  try {
    // So it is expected failure. ( Notice to disconnect. )
    client.cmd_store("AAAA",RomaValue("aaaa",4),0,TIMEOUT);
    CPPUNIT_FAIL("Should throw !");
  }catch(const Exception & ex){
  }
  try {
    // Try to repair, but failure.
    client.cmd_store("AAAA",RomaValue("aaaa",4),0,TIMEOUT);
    CPPUNIT_FAIL("Should throw !");
  }catch(const Exception & ex){
  }
  // Of course, num of connection is zero.
  CPPUNIT_ASSERT_EQUAL(0,client.num_valid_connection());
  // ---------------
  // All node start.
  system("roma.bash start");
  sleep(10);
  try {
    client.cmd_store("AAAA",RomaValue("aaaa",4),0,TIMEOUT);
    RomaValue v = client.cmd_get("AAAA",TIMEOUT);
    CPPUNIT_ASSERT_EQUAL((long)4,v.length);
    CPPUNIT_ASSERT_EQUAL(string("aaaa"),string(v.data));
  }catch(const Exception & ex){
    CPPUNIT_FAIL(string("Unexpected exception ! : ") + ex.get_msg());
  }
}

template<class TEST>
struct GetSuite {
  CppUnit::TestSuite * operator()() const{
    CppUnit::TestSuite *suite = new CppUnit::TestSuite();
    suite->addTest(new CppUnit::TestCaller<TEST>("testLoopAllNodeDown",&TEST::testLoopAllNodeDown));
    return suite;
  }
};

CppUnit::TestSuite * LoopConnectionTest0::getSuite(){
  return GetSuite<LoopConnectionTest0>()();
}
CppUnit::TestSuite * LoopConnectionTest1::getSuite(){
  return GetSuite<LoopConnectionTest1>()();
}
