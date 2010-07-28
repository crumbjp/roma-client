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
  client.get_nodelist().push_back("localhost_11219");
  client.init(ROUTING_MODE_USE);
}
void ProtocolTest::tearDown() {
  client.term();
}
void ProtocolTest::testNumValidConnection() {
  int n = client.num_valid_connection();
  CPPUNIT_ASSERT_EQUAL(1,n);
}
void ProtocolTest::testSetStored() {
  rmc_ret_t ret = client.cmd_store("CMD_STORED",RomaValue("bbb",3),100,TIMEOUT);
  CPPUNIT_ASSERT_EQUAL(0,ret);
}
void ProtocolTest::testSetStoredVal() {
  rmc_ret_t ret = client.cmd_store("CMPV_bbbBBbb",RomaValue("bbbBBbb",7),100,TIMEOUT);
  CPPUNIT_ASSERT_EQUAL(0,ret);
}
void ProtocolTest::testSetStoredExp() {
  rmc_ret_t ret = client.cmd_store("CMP3_99",RomaValue("bbb",3),99,TIMEOUT);
  CPPUNIT_ASSERT_EQUAL(0,ret);
}
void ProtocolTest::testSetNotStored() {
  rmc_ret_t ret = client.cmd_store("CMD_NOT_STORED",RomaValue("bbb",3),100,TIMEOUT);
  CPPUNIT_ASSERT_EQUAL(1,ret);
}
void ProtocolTest::testSetServerError() {
  try{
    rmc_ret_t ret = client.cmd_store("CMD_SERVER_ERROR",RomaValue("bbb",3),100,TIMEOUT);
    (void)ret;
    CPPUNIT_FAIL("Should throw !");
  }catch(const Exception & ex){
    CPPUNIT_ASSERT(client.get_lasterror() != NULL);
  }
}
void ProtocolTest::testSetError() {
  try{
    rmc_ret_t ret = client.cmd_store("CMD_ERROR",RomaValue("bbb",3),100,TIMEOUT);
    (void)ret;
    CPPUNIT_FAIL("Should throw !");
  }catch(const Exception & ex){
    CPPUNIT_ASSERT(client.get_lasterror() != NULL);
  }
}
void ProtocolTest::testSetTimeout() {
  try {
    rmc_ret_t ret = client.cmd_store("TO_10",RomaValue("bbb",3),100,TIMEOUT);
    (void)ret;
    CPPUNIT_FAIL("Should throw !");
  }catch(const Exception & ex){
  }
}
void ProtocolTest::testSetClose() {
  try{
    rmc_ret_t ret = client.cmd_store("CLOSE_",RomaValue("bbb",3),100,TIMEOUT);
    (void)ret;
    CPPUNIT_FAIL("Should throw !");
  }catch(const Exception & ex){
    CPPUNIT_ASSERT(client.get_lasterror() != NULL);
  }
}

void ProtocolTest::testGetNull() {
  RomaValue v = client.cmd_get("CMD_NULL",TIMEOUT);
  CPPUNIT_ASSERT_EQUAL((const char *)0,v.data);
  CPPUNIT_ASSERT_EQUAL((long)-1,v.length);
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
void ProtocolTest::testGetClose() {
  try {
    RomaValue v = client.cmd_get("CLOSE_",TIMEOUT);
    CPPUNIT_FAIL("Should throw !");
  }catch(const Exception & ex){
  }
}

void ProtocolTest::testAlistSizedInsertStored() {
  rmc_ret_t ret = client.cmd_alist_sized_insert("CMD_STORED",2,RomaValue("bbb",3),TIMEOUT);
  CPPUNIT_ASSERT_EQUAL(0,ret);
}
void ProtocolTest::testAlistSizedInsertStoredVal() {
  rmc_ret_t ret = client.cmd_alist_sized_insert("CMPV_bbbBBbb",2,RomaValue("bbbBBbb",7),TIMEOUT);
  CPPUNIT_ASSERT_EQUAL(0,ret);
}
void ProtocolTest::testAlistSizedInsertStoredSize() {
  rmc_ret_t ret = client.cmd_alist_sized_insert("CMP2_99",99,RomaValue("bbb",3),TIMEOUT);
  CPPUNIT_ASSERT_EQUAL(0,ret);
}
void ProtocolTest::testAlistSizedInsertNotStored() {
  rmc_ret_t ret = client.cmd_alist_sized_insert("CMD_NOT_STORED",2,RomaValue("bbb",3),TIMEOUT);
  CPPUNIT_ASSERT_EQUAL(1,ret);
}
void ProtocolTest::testAlistSizedInsertServerError() {
  try{
    rmc_ret_t ret = client.cmd_alist_sized_insert("CMD_SERVER_ERROR",2,RomaValue("bbb",3),TIMEOUT);
    (void)ret;
    CPPUNIT_FAIL("Should throw !");
  }catch(const Exception & ex){
    CPPUNIT_ASSERT(client.get_lasterror() != NULL);
  }
}
void ProtocolTest::testAlistSizedInsertError() {
  try{
    rmc_ret_t ret = client.cmd_alist_sized_insert("CMD_ERROR",2,RomaValue("bbb",3),TIMEOUT);
    (void)ret;
    CPPUNIT_FAIL("Should throw !");
  }catch(const Exception & ex){
    CPPUNIT_ASSERT(client.get_lasterror() != NULL);
  }
}
void ProtocolTest::testAlistSizedInsertTimeout() {
  try {
    rmc_ret_t ret = client.cmd_alist_sized_insert("TO_10",2,RomaValue("bbb",3),TIMEOUT);
    (void)ret;
    CPPUNIT_FAIL("Should throw !");
  }catch(const Exception & ex){
  }
}
void ProtocolTest::testAlistSizedInsertClose() {
  try{
    rmc_ret_t ret = client.cmd_alist_sized_insert("CLOSE_",2,RomaValue("bbb",3),TIMEOUT);
    (void)ret;
    CPPUNIT_FAIL("Should throw !");
  }catch(const Exception & ex){
    CPPUNIT_ASSERT(client.get_lasterror() != NULL);
  }
}

void ProtocolTest::testAlistJoinNull() {
  RomaValue v = client.cmd_alist_join("CMD_NULL",",",TIMEOUT);
  CPPUNIT_ASSERT_EQUAL((const char *)0,v.data);
  CPPUNIT_ASSERT_EQUAL((long)-1,v.length);
}
void ProtocolTest::testAlistJoinSep() {
  RomaValue v = client.cmd_alist_join("CMPV_X","X",TIMEOUT);
  CPPUNIT_ASSERT_EQUAL((const char *)0,v.data);
  CPPUNIT_ASSERT_EQUAL((long)-1,v.length);
}
void ProtocolTest::testAlistJoinValue() {
  RomaValue v = client.cmd_alist_join("CMD_VALUE",",",TIMEOUT);
  CPPUNIT_ASSERT_EQUAL(string("FOO,BAR,BAZ"),string(v.data));
}
void ProtocolTest::testAlistJoinServerError() {
  try {
    RomaValue v = client.cmd_alist_join("CMD_SERVER_ERROR",",",TIMEOUT);
    CPPUNIT_FAIL("Should throw !");
  }catch(const Exception & ex){
  }
}
void ProtocolTest::testAlistJoinError() {
  try {
    RomaValue v = client.cmd_alist_join("CMD_ERROR",",",TIMEOUT);
    CPPUNIT_FAIL("Should throw !");
  }catch(const Exception & ex){
  }
}
void ProtocolTest::testAlistJoinTimeout() {
  try {
    RomaValue v = client.cmd_alist_join("TO_10",",",TIMEOUT);
    CPPUNIT_FAIL("Should throw !");
  }catch(const Exception & ex){
  }
}
void ProtocolTest::testAlistJoinClose() {
  try {
    RomaValue v = client.cmd_alist_join("CLOSE_",",",TIMEOUT);
    CPPUNIT_FAIL("Should throw !");
  }catch(const Exception & ex){
  }
}

void ProtocolTest::testAlistDeleteVal() {
  rmc_ret_t ret = client.cmd_alist_delete("CMPV_bbbBBbb",RomaValue("bbbBBbb",7),TIMEOUT);
  CPPUNIT_ASSERT_EQUAL(0,ret);
}
void ProtocolTest::testAlistDeleteDeleted() {
  rmc_ret_t ret = client.cmd_alist_delete("CMD_DELETED",RomaValue("bbb",3),TIMEOUT);
  CPPUNIT_ASSERT_EQUAL(0,ret);
}
void ProtocolTest::testAlistDeleteNotDeleted() {
  rmc_ret_t ret = client.cmd_alist_delete("CMD_NOT_DELETED",RomaValue("bbb",3),TIMEOUT);
  CPPUNIT_ASSERT_EQUAL(1,ret);
}
void ProtocolTest::testAlistDeleteNotFound() {
  rmc_ret_t ret = client.cmd_alist_delete("CMD_NOT_FOUND",RomaValue("bbb",3),TIMEOUT);
  CPPUNIT_ASSERT_EQUAL(1,ret);
}
void ProtocolTest::testAlistDeleteServerError() {
  try{
    rmc_ret_t ret = client.cmd_alist_delete("CMD_SERVER_ERROR",RomaValue("bbb",3),TIMEOUT);
    (void)ret;
    CPPUNIT_FAIL("Should throw !");
  }catch(const Exception & ex){
    CPPUNIT_ASSERT(client.get_lasterror() != NULL);
  }
}
void ProtocolTest::testAlistDeleteError() {
  try{
    rmc_ret_t ret = client.cmd_alist_delete("CMD_ERROR",RomaValue("bbb",3),TIMEOUT);
    (void)ret;
    CPPUNIT_FAIL("Should throw !");
  }catch(const Exception & ex){
    CPPUNIT_ASSERT(client.get_lasterror() != NULL);
  }
}
void ProtocolTest::testAlistDeleteTimeout() {
  try {
    rmc_ret_t ret = client.cmd_alist_delete("TO_10",RomaValue("bbb",3),TIMEOUT);
    (void)ret;
    CPPUNIT_FAIL("Should throw !");
  }catch(const Exception & ex){
  }
}
void ProtocolTest::testAlistDeleteClose() {
  try{
    rmc_ret_t ret = client.cmd_alist_delete("CLOSE_",RomaValue("bbb",3),TIMEOUT);
    (void)ret;
    CPPUNIT_FAIL("Should throw !");
  }catch(const Exception & ex){
    CPPUNIT_ASSERT(client.get_lasterror() != NULL);
  }
}

void ProtocolTest::testAlistDeleteAtPos() {
  rmc_ret_t ret = client.cmd_alist_delete_at("CMP2_3",3,TIMEOUT);
  CPPUNIT_ASSERT_EQUAL(0,ret);
}
void ProtocolTest::testAlistDeleteAtDeleted() {
  rmc_ret_t ret = client.cmd_alist_delete_at("CMD_DELETED",1,TIMEOUT);
  CPPUNIT_ASSERT_EQUAL(0,ret);
}
void ProtocolTest::testAlistDeleteAtNotDeleted() {
  rmc_ret_t ret = client.cmd_alist_delete_at("CMD_NOT_DELETED",1,TIMEOUT);
  CPPUNIT_ASSERT_EQUAL(1,ret);
}
void ProtocolTest::testAlistDeleteAtNotFound() {
  rmc_ret_t ret = client.cmd_alist_delete_at("CMD_NOT_FOUND",1,TIMEOUT);
  CPPUNIT_ASSERT_EQUAL(1,ret);
}
void ProtocolTest::testAlistDeleteAtServerError() {
  try{
    rmc_ret_t ret = client.cmd_alist_delete_at("CMD_SERVER_ERROR",1,TIMEOUT);
    (void)ret;
    CPPUNIT_FAIL("Should throw !");
  }catch(const Exception & ex){
    CPPUNIT_ASSERT(client.get_lasterror() != NULL);
  }
}
void ProtocolTest::testAlistDeleteAtError() {
  try{
    rmc_ret_t ret = client.cmd_alist_delete_at("CMD_ERROR",1,TIMEOUT);
    (void)ret;
    CPPUNIT_FAIL("Should throw !");
  }catch(const Exception & ex){
    CPPUNIT_ASSERT(client.get_lasterror() != NULL);
  }
}
void ProtocolTest::testAlistDeleteAtTimeout() {
  try {
    rmc_ret_t ret = client.cmd_alist_delete_at("TO_10",1,TIMEOUT);
    (void)ret;
    CPPUNIT_FAIL("Should throw !");
  }catch(const Exception & ex){
  }
}
void ProtocolTest::testAlistDeleteAtClose() {
  try{
    rmc_ret_t ret = client.cmd_alist_delete_at("CLOSE_",1,TIMEOUT);
    (void)ret;
    CPPUNIT_FAIL("Should throw !");
  }catch(const Exception & ex){
    CPPUNIT_ASSERT(client.get_lasterror() != NULL);
  }
}


CppUnit::TestSuite * ProtocolTest::getSuite(){
  CppUnit::TestSuite *suite = new CppUnit::TestSuite();
  suite->addTest(new CppUnit::TestCaller<ProtocolTest>("testNumValidConnection",&ProtocolTest::testNumValidConnection));

  suite->addTest(new CppUnit::TestCaller<ProtocolTest>("testSetStoredVal",&ProtocolTest::testSetStoredVal));
  suite->addTest(new CppUnit::TestCaller<ProtocolTest>("testSetStored",&ProtocolTest::testSetStored));
  suite->addTest(new CppUnit::TestCaller<ProtocolTest>("testSetStoredExp",&ProtocolTest::testSetStoredExp));
  suite->addTest(new CppUnit::TestCaller<ProtocolTest>("testSetNotStored",&ProtocolTest::testSetNotStored));
  suite->addTest(new CppUnit::TestCaller<ProtocolTest>("testSetServerError",&ProtocolTest::testSetServerError));
  suite->addTest(new CppUnit::TestCaller<ProtocolTest>("testSetError",&ProtocolTest::testSetError));
  suite->addTest(new CppUnit::TestCaller<ProtocolTest>("testSetTimeout",&ProtocolTest::testSetTimeout));
  suite->addTest(new CppUnit::TestCaller<ProtocolTest>("testSetClose",&ProtocolTest::testSetClose));

  suite->addTest(new CppUnit::TestCaller<ProtocolTest>("testGetNull",&ProtocolTest::testGetNull));
  suite->addTest(new CppUnit::TestCaller<ProtocolTest>("testGetValue",&ProtocolTest::testGetValue));
  suite->addTest(new CppUnit::TestCaller<ProtocolTest>("testGetServerError",&ProtocolTest::testGetServerError));
  suite->addTest(new CppUnit::TestCaller<ProtocolTest>("testGetError",&ProtocolTest::testGetError));
  suite->addTest(new CppUnit::TestCaller<ProtocolTest>("testGetTimeout",&ProtocolTest::testGetTimeout));
  suite->addTest(new CppUnit::TestCaller<ProtocolTest>("testGetClose",&ProtocolTest::testGetClose));

  suite->addTest(new CppUnit::TestCaller<ProtocolTest>("testAlistSizedInsertStored",&ProtocolTest::testAlistSizedInsertStored));
  suite->addTest(new CppUnit::TestCaller<ProtocolTest>("testAlistSizedInsertStoredVal",&ProtocolTest::testAlistSizedInsertStoredVal));
  suite->addTest(new CppUnit::TestCaller<ProtocolTest>("testAlistSizedInsertStoredSize",&ProtocolTest::testAlistSizedInsertStoredSize));
  suite->addTest(new CppUnit::TestCaller<ProtocolTest>("testAlistSizedInsertNotStored",&ProtocolTest::testAlistSizedInsertNotStored));
  suite->addTest(new CppUnit::TestCaller<ProtocolTest>("testAlistSizedInsertServerError",&ProtocolTest::testAlistSizedInsertServerError));
  suite->addTest(new CppUnit::TestCaller<ProtocolTest>("testAlistSizedInsertError",&ProtocolTest::testAlistSizedInsertError));
  suite->addTest(new CppUnit::TestCaller<ProtocolTest>("testAlistSizedInsertTimeout",&ProtocolTest::testAlistSizedInsertTimeout));
  suite->addTest(new CppUnit::TestCaller<ProtocolTest>("testAlistSizedInsertClose",&ProtocolTest::testAlistSizedInsertClose));

  suite->addTest(new CppUnit::TestCaller<ProtocolTest>("testAlistJoinNull",&ProtocolTest::testAlistJoinNull));
  suite->addTest(new CppUnit::TestCaller<ProtocolTest>("testAlistJoinSep",&ProtocolTest::testAlistJoinSep));
  suite->addTest(new CppUnit::TestCaller<ProtocolTest>("testAlistJoinValue",&ProtocolTest::testAlistJoinValue));
  suite->addTest(new CppUnit::TestCaller<ProtocolTest>("testAlistJoinServerError",&ProtocolTest::testAlistJoinServerError));
  suite->addTest(new CppUnit::TestCaller<ProtocolTest>("testAlistJoinError",&ProtocolTest::testAlistJoinError));
  suite->addTest(new CppUnit::TestCaller<ProtocolTest>("testAlistJoinTimeout",&ProtocolTest::testAlistJoinTimeout));
  suite->addTest(new CppUnit::TestCaller<ProtocolTest>("testAlistJoinClose",&ProtocolTest::testAlistJoinClose));

  suite->addTest(new CppUnit::TestCaller<ProtocolTest>("testAlistDeleteVal",&ProtocolTest::testAlistDeleteVal));
  suite->addTest(new CppUnit::TestCaller<ProtocolTest>("testAlistDeleteDeleted",&ProtocolTest::testAlistDeleteDeleted));
  suite->addTest(new CppUnit::TestCaller<ProtocolTest>("testAlistDeleteNotDeleted",&ProtocolTest::testAlistDeleteNotDeleted));
  suite->addTest(new CppUnit::TestCaller<ProtocolTest>("testAlistDeleteNotFound",&ProtocolTest::testAlistDeleteNotFound));
  suite->addTest(new CppUnit::TestCaller<ProtocolTest>("testAlistDeleteServerError",&ProtocolTest::testAlistDeleteServerError));
  suite->addTest(new CppUnit::TestCaller<ProtocolTest>("testAlistDeleteError",&ProtocolTest::testAlistDeleteError));
  suite->addTest(new CppUnit::TestCaller<ProtocolTest>("testAlistDeleteTimeout",&ProtocolTest::testAlistDeleteTimeout));
  suite->addTest(new CppUnit::TestCaller<ProtocolTest>("testAlistDeleteClose",&ProtocolTest::testAlistDeleteClose));

  suite->addTest(new CppUnit::TestCaller<ProtocolTest>("testAlistDeleteAtPos",&ProtocolTest::testAlistDeleteAtPos));
  suite->addTest(new CppUnit::TestCaller<ProtocolTest>("testAlistDeleteAtDeleted",&ProtocolTest::testAlistDeleteAtDeleted));
  suite->addTest(new CppUnit::TestCaller<ProtocolTest>("testAlistDeleteAtNotDeleted",&ProtocolTest::testAlistDeleteAtNotDeleted));
  suite->addTest(new CppUnit::TestCaller<ProtocolTest>("testAlistDeleteAtNotFound",&ProtocolTest::testAlistDeleteAtNotFound));
  suite->addTest(new CppUnit::TestCaller<ProtocolTest>("testAlistDeleteAtServerError",&ProtocolTest::testAlistDeleteAtServerError));
  suite->addTest(new CppUnit::TestCaller<ProtocolTest>("testAlistDeleteAtError",&ProtocolTest::testAlistDeleteAtError));
  suite->addTest(new CppUnit::TestCaller<ProtocolTest>("testAlistDeleteAtTimeout",&ProtocolTest::testAlistDeleteAtTimeout));
  suite->addTest(new CppUnit::TestCaller<ProtocolTest>("testAlistDeleteAtClose",&ProtocolTest::testAlistDeleteAtClose));

  return suite;
}
