/*
 * loop_test.cc - ????
 *  
 *   Copyright (C) 2010 rakuten 
 *     by hiroaki.kubota <hiroaki.kubota@rakuten.co.jp> 
 *     Date : 2010/07/21
 */

#include <cppunit/TestCaller.h>
#include <cppunit/Asserter.h>
#include <cppunit/TestAssert.h>
#include <cppunit/SourceLine.h>

#include "loop_test.h"
#include <iostream>

static const long TIMEOUT = 1000;

void LoopTest::setUp() {
  set_loglv(0);
  client.get_nodelist().push_back("localhost_11211");
  client.get_nodelist().push_back("localhost_11212");
  client.init(ROUTING_MODE_USE);
}
void LoopTest::tearDown() {
  client.term();
}
void LoopTest::testLoop() {
  cerr << __PRETTY_FUNCTION__ << endl;
  for (int i=0 ;i<180;i++ ) {
    client.cmd_store("AAAA",RomaValue("aaaa",4),0,TIMEOUT);
    client.cmd_store("BBBB",RomaValue("bbbb",4),0,TIMEOUT);
    client.cmd_store("CCCC",RomaValue("cccc",4),0,TIMEOUT);
    client.cmd_store("DDDD",RomaValue("dddd",4),0,TIMEOUT);
    client.cmd_store("EEEE",RomaValue("eeee",4),0,TIMEOUT);
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
CppUnit::TestSuite * LoopTest::getSuite(){
  CppUnit::TestSuite *suite = new CppUnit::TestSuite();
  suite->addTest(new CppUnit::TestCaller<LoopTest>("testLoop",&LoopTest::testLoop));
  return suite;
}
