/*
 * loop_monkey_test.cc - ????
 *  
 *   Copyright (C) 2010 rakuten 
 *     by hiroaki.kubota <hiroaki.kubota@rakuten.co.jp> 
 *     Date : 2010/07/21
 */

#include <cppunit/TestCaller.h>
#include <cppunit/Asserter.h>
#include <cppunit/TestAssert.h>
#include <cppunit/SourceLine.h>

#include "loop_monkey_test.h"
#include <iostream>

static const long TIMEOUT = 1000;

void LoopMonkeyTest::setUp() {
  set_loglv(1);
  client.get_nodelist().push_back("localhost_11211");
  client.init(1);
}
void LoopMonkeyTest::tearDown() {
  client.term();
}
void LoopMonkeyTest::testLoopWithAssert() {
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
void LoopMonkeyTest::testLoopNoAssertAllNodeDown() {
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
CppUnit::TestSuite * LoopMonkeyTest::getSuite(){
  CppUnit::TestSuite *suite = new CppUnit::TestSuite();
  // [NodeA - NodeB] -> [NodeA] -> [NodeA - NodeB] -> [NodeB]
  suite->addTest(new CppUnit::TestCaller<LoopMonkeyTest>("testLoopWithAssert",&LoopMonkeyTest::testLoopWithAssert));
  // [NodeA - NodeB] -> [] -> [NodeA] -> [NodeB] -> [NodeA - NodeB]
  suite->addTest(new CppUnit::TestCaller<LoopMonkeyTest>("testLoopNoAssertAllNodeDown",&LoopMonkeyTest::testLoopNoAssertAllNodeDown));
  return suite;
}
