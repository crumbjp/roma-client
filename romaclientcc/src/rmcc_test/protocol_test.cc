/*
 * protocol_test.cc - ????
 *  
 *   Copyright (C) 2010 rakuten 
 *     by hiroaki.kubota <hiroaki.kubota@rakuten.co.jp> 
 *     Date : 2010/07/21
 */

#include <cppunit/TestCaller.h>
#include <cppunit/Asserter.h>
#include <cppunit/TestAssert.h>
#include <cppunit/SourceLine.h>

#include "protocol_test.h"
#include <iostream>

static const long TIMEOUT = 1000;

void ProtocolTest::setUp() {
  set_loglv(0);
  client.get_nodelist().push_back("localhost_11211");
  client.init(ROUTING_MODE_USE);
}
void ProtocolTest::tearDown() {
  client.term();
}
void ProtocolTest::testGetNull() {
  RomaValue v = client.cmd_get("CMD_NULL",TIMEOUT);
}
void ProtocolTest::testGetValue() {
  RomaValue v = client.cmd_get("CMD_VALUE",TIMEOUT);
  CPPUNIT_ASSERT_EQUAL(string("FOOBAR"),string(v.data));
}
void ProtocolTest::testGetServerError() {
  try {
    RomaValue v = client.cmd_get("CMD_SERVER_ERROR",TIMEOUT);
    CPPUNIT_FAIL("Should throw !");
  }catch(const Exception & ex){
  }
}
void ProtocolTest::testGetError() {
  try {
    RomaValue v = client.cmd_get("CMD_ERROR",TIMEOUT);
    CPPUNIT_FAIL("Should throw !");
  }catch(const Exception & ex){
  }
}
void ProtocolTest::testGetTimeout() {
  try {
    RomaValue v = client.cmd_get("TO_10",TIMEOUT);
    CPPUNIT_FAIL("Should throw !");
  }catch(const Exception & ex){
  }
}
CppUnit::TestSuite * ProtocolTest::getSuite(){
  CppUnit::TestSuite *suite = new CppUnit::TestSuite();
  suite->addTest(new CppUnit::TestCaller<ProtocolTest>("testGetNull",&ProtocolTest::testGetNull));
  suite->addTest(new CppUnit::TestCaller<ProtocolTest>("testGetValue",&ProtocolTest::testGetValue));
  suite->addTest(new CppUnit::TestCaller<ProtocolTest>("testGetServerError",&ProtocolTest::testGetServerError));
  suite->addTest(new CppUnit::TestCaller<ProtocolTest>("testGetError",&ProtocolTest::testGetError));
  suite->addTest(new CppUnit::TestCaller<ProtocolTest>("testGetTimeout",&ProtocolTest::testGetTimeout));
  return suite;
}
