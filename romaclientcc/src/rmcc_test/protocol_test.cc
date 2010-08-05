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

static const long TIMEOUT = 2000;

void ProtocolTest0::setUp() {
  set_loglv(0);
  set_logfp(stderr);
  client.get_nodelist().push_back("localhost_11219");
  client.init(0);
  client.init(0);
}
void ProtocolTest1::setUp() {
  set_loglv(0);
  set_logfp(stderr);
  client.get_nodelist().push_back("localhost_11219");
  client.init(ROUTING_MODE_USE);
  client.init(ROUTING_MODE_USE);
}

void ProtocolTest::tearDown() {
  client.term();
}

void ProtocolTest::testNumValidConnection() {
  cerr << "*TEST* " << __PRETTY_FUNCTION__ << " (" << typeid(*this).name() << ")" << endl;
  int n = client.num_valid_connection();
  CPPUNIT_ASSERT_EQUAL(1,n);
}


void ProtocolTest::testSetStored() {
  cerr << "*TEST* " << __PRETTY_FUNCTION__ << " (" << typeid(*this).name() << ")" << endl;
  rmc_ret_t ret = client.cmd_store("CMD_STORED",RomaValue("bbb",3),100,TIMEOUT);
  CPPUNIT_ASSERT_EQUAL(0,ret);
}


void ProtocolTest::testSetStoredVal() {
  cerr << "*TEST* " << __PRETTY_FUNCTION__ << " (" << typeid(*this).name() << ")" << endl;
  rmc_ret_t ret = client.cmd_store("CMPV_bbbBBbb",RomaValue("bbbBBbb",7),100,TIMEOUT);
  CPPUNIT_ASSERT_EQUAL(0,ret);
}

void ProtocolTest::testSetStoredExp() {
  cerr << "*TEST* " << __PRETTY_FUNCTION__ << " (" << typeid(*this).name() << ")" << endl;
  rmc_ret_t ret = client.cmd_store("CMP3_99",RomaValue("bbb",3),99,TIMEOUT);
  CPPUNIT_ASSERT_EQUAL(0,ret);
}

void ProtocolTest::testSetNotStored() {
  cerr << "*TEST* " << __PRETTY_FUNCTION__ << " (" << typeid(*this).name() << ")" << endl;
  rmc_ret_t ret = client.cmd_store("CMD_NOT_STORED",RomaValue("bbb",3),100,TIMEOUT);
  CPPUNIT_ASSERT_EQUAL(1,ret);
}

void ProtocolTest::testSetServerError() {
  cerr << "*TEST* " << __PRETTY_FUNCTION__ << " (" << typeid(*this).name() << ")" << endl;
  try{
    rmc_ret_t ret = client.cmd_store("CMD_SERVER_ERROR",RomaValue("bbb",3),100,TIMEOUT);
    (void)ret;
    CPPUNIT_FAIL("Should throw !");
  }catch(const Exception & ex){
    CPPUNIT_ASSERT(client.get_lasterror() != NULL);
  }
}


void ProtocolTest::testSetError() {
  cerr << "*TEST* " << __PRETTY_FUNCTION__ << " (" << typeid(*this).name() << ")" << endl;
  try{
    rmc_ret_t ret = client.cmd_store("CMD_ERROR",RomaValue("bbb",3),100,TIMEOUT);
    (void)ret;
    CPPUNIT_FAIL("Should throw !");
  }catch(const Exception & ex){
    CPPUNIT_ASSERT(client.get_lasterror() != NULL);
  }
}


void ProtocolTest::testSetTimeout() {
  cerr << "*TEST* " << __PRETTY_FUNCTION__ << " (" << typeid(*this).name() << ")" << endl;
  try {
    rmc_ret_t ret = client.cmd_store("TO_10",RomaValue("bbb",3),100,TIMEOUT);
    (void)ret;
    CPPUNIT_FAIL("Should throw !");
  }catch(const Exception & ex){
  }
}


void ProtocolTest::testSetClose() {
  cerr << "*TEST* " << __PRETTY_FUNCTION__ << " (" << typeid(*this).name() << ")" << endl;
  try{
    rmc_ret_t ret = client.cmd_store("CLOSE_",RomaValue("bbb",3),100,TIMEOUT);
    (void)ret;
    CPPUNIT_FAIL("Should throw !");
  }catch(const Exception & ex){
    CPPUNIT_ASSERT(client.get_lasterror() != NULL);
  }
}



void ProtocolTest::testGetNull() {
  cerr << "*TEST* " << __PRETTY_FUNCTION__ << " (" << typeid(*this).name() << ")" << endl;
  RomaValue v = client.cmd_get("CMD_NULL",TIMEOUT);
  CPPUNIT_ASSERT_EQUAL((const char *)0,v.data);
  CPPUNIT_ASSERT_EQUAL((long)-1,v.length);
}


void ProtocolTest::testGetValue() {
  cerr << "*TEST* " << __PRETTY_FUNCTION__ << " (" << typeid(*this).name() << ")" << endl;
  RomaValue v = client.cmd_get("CMD_VALUE",TIMEOUT);
  CPPUNIT_ASSERT_EQUAL(string("FOOBAR"),string(v.data));
}


void ProtocolTest::testGetValueError1() {
  cerr << "*TEST* " << __PRETTY_FUNCTION__ << " (" << typeid(*this).name() << ")" << endl;
  try {
    RomaValue v = client.cmd_get("CMD_VALUEERR1",TIMEOUT);
    (void)v;
    CPPUNIT_FAIL("Should throw !");
  }catch(const Exception & ex){
  }
}

void ProtocolTest::testGetValueError2() {
  cerr << "*TEST* " << __PRETTY_FUNCTION__ << " (" << typeid(*this).name() << ")" << endl;
  try {
    RomaValue v = client.cmd_get("CMD_VALUEERR2",TIMEOUT);
    (void)v;
    CPPUNIT_FAIL("Should throw !");
  }catch(const Exception & ex){
  }
}

void ProtocolTest::testGetValueError3() {
  cerr << "*TEST* " << __PRETTY_FUNCTION__ << " (" << typeid(*this).name() << ")" << endl;
  try {
    RomaValue v = client.cmd_get("CMD_VALUEERR3",TIMEOUT);
    (void)v;
    CPPUNIT_FAIL("Should throw !");
  }catch(const Exception & ex){
  }
}

void ProtocolTest::testGetValueError4() {
  cerr << "*TEST* " << __PRETTY_FUNCTION__ << " (" << typeid(*this).name() << ")" << endl;
  try {
    RomaValue v = client.cmd_get("CMD_VALUEERR4",TIMEOUT);
    (void)v;
    CPPUNIT_FAIL("Should throw !");
  }catch(const Exception & ex){
  }
}


void ProtocolTest::testGetLarge() {
  cerr << "*TEST* " << __PRETTY_FUNCTION__ << " (" << typeid(*this).name() << ")" << endl;
  RomaValue v = client.cmd_get("CMD_LARGE",TIMEOUT);
  CPPUNIT_ASSERT_EQUAL((long)100000,v.length);
  CPPUNIT_ASSERT_EQUAL((size_t)100000,strlen(v.data));
}


void ProtocolTest::testGetServerError() {
  cerr << "*TEST* " << __PRETTY_FUNCTION__ << " (" << typeid(*this).name() << ")" << endl;
  try {
    RomaValue v = client.cmd_get("CMD_SERVER_ERROR",TIMEOUT);
    CPPUNIT_FAIL("Should throw !");
  }catch(const Exception & ex){
  }
}


void ProtocolTest::testGetError() {
  cerr << "*TEST* " << __PRETTY_FUNCTION__ << " (" << typeid(*this).name() << ")" << endl;
  try {
    RomaValue v = client.cmd_get("CMD_ERROR",TIMEOUT);
    (void)v;
    CPPUNIT_FAIL("Should throw !");
  }catch(const Exception & ex){
  }
}


void ProtocolTest::testGetTimeout() {
  cerr << "*TEST* " << __PRETTY_FUNCTION__ << " (" << typeid(*this).name() << ")" << endl;
  try {
    RomaValue v = client.cmd_get("TO_10",TIMEOUT);
    (void)v;
    CPPUNIT_FAIL("Should throw !");
  }catch(const Exception & ex){
  }
}


void ProtocolTest::testGetClose() {
  cerr << "*TEST* " << __PRETTY_FUNCTION__ << " (" << typeid(*this).name() << ")" << endl;
  try {
    RomaValue v = client.cmd_get("CLOSE_",TIMEOUT);
    (void)v;
    CPPUNIT_FAIL("Should throw !");
  }catch(const Exception & ex){
  }
}
void ProtocolTest::testGetClose2() {
  cerr << "*TEST* " << __PRETTY_FUNCTION__ << " (" << typeid(*this).name() << ")" << endl;
  RomaValue v = client.cmd_get("CLOSE2_",TIMEOUT);
  CPPUNIT_ASSERT_EQUAL(string("FOOBAR"),string(v.data));
  v = client.cmd_get("CLOSE2_",TIMEOUT);
  CPPUNIT_ASSERT_EQUAL(string("FOOBAR"),string(v.data));
}


void ProtocolTest::testDeleteDeleted() {
  cerr << "*TEST* " << __PRETTY_FUNCTION__ << " (" << typeid(*this).name() << ")" << endl;
  rmc_ret_t ret = client.cmd_delete("CMD_DELETED",TIMEOUT);
  CPPUNIT_ASSERT_EQUAL(0,ret);
}


void ProtocolTest::testDeleteNotDeleted() {
  cerr << "*TEST* " << __PRETTY_FUNCTION__ << " (" << typeid(*this).name() << ")" << endl;
  rmc_ret_t ret = client.cmd_delete("CMD_NOT_DELETED",TIMEOUT);
  CPPUNIT_ASSERT_EQUAL(1,ret);
}


void ProtocolTest::testDeleteNotFound() {
  cerr << "*TEST* " << __PRETTY_FUNCTION__ << " (" << typeid(*this).name() << ")" << endl;
  rmc_ret_t ret = client.cmd_delete("CMD_NOT_FOUND",TIMEOUT);
  CPPUNIT_ASSERT_EQUAL(0,ret);
}


void ProtocolTest::testDeleteServerError() {
  cerr << "*TEST* " << __PRETTY_FUNCTION__ << " (" << typeid(*this).name() << ")" << endl;
  try{
    rmc_ret_t ret = client.cmd_delete("CMD_SERVER_ERROR",TIMEOUT);
    (void)ret;
    CPPUNIT_FAIL("Should throw !");
  }catch(const Exception & ex){
    CPPUNIT_ASSERT(client.get_lasterror() != NULL);
  }
}

void ProtocolTest::testDeleteError() {
  cerr << "*TEST* " << __PRETTY_FUNCTION__ << " (" << typeid(*this).name() << ")" << endl;
  try{
    rmc_ret_t ret = client.cmd_delete("CMD_ERROR",TIMEOUT);
    (void)ret;
    CPPUNIT_FAIL("Should throw !");
  }catch(const Exception & ex){
    CPPUNIT_ASSERT(client.get_lasterror() != NULL);
  }
}


void ProtocolTest::testDeleteTimeout() {
  cerr << "*TEST* " << __PRETTY_FUNCTION__ << " (" << typeid(*this).name() << ")" << endl;
  try {
    rmc_ret_t ret = client.cmd_delete("TO_10",TIMEOUT);
    (void)ret;
    CPPUNIT_FAIL("Should throw !");
  }catch(const Exception & ex){
  }
}


void ProtocolTest::testDeleteClose() {
  cerr << "*TEST* " << __PRETTY_FUNCTION__ << " (" << typeid(*this).name() << ")" << endl;
  try{
    rmc_ret_t ret = client.cmd_delete("CLOSE_",TIMEOUT);
    (void)ret;
    CPPUNIT_FAIL("Should throw !");
  }catch(const Exception & ex){
    CPPUNIT_ASSERT(client.get_lasterror() != NULL);
  }
}


void ProtocolTest::testAlistSizedInsertStored() {
  cerr << "*TEST* " << __PRETTY_FUNCTION__ << " (" << typeid(*this).name() << ")" << endl;
  rmc_ret_t ret = client.cmd_alist_sized_insert("CMD_STORED",2,RomaValue("bbb",3),TIMEOUT);
  CPPUNIT_ASSERT_EQUAL(0,ret);
}


void ProtocolTest::testAlistSizedInsertStoredVal() {
  cerr << "*TEST* " << __PRETTY_FUNCTION__ << " (" << typeid(*this).name() << ")" << endl;
  rmc_ret_t ret = client.cmd_alist_sized_insert("CMPV_bbbBBbb",2,RomaValue("bbbBBbb",7),TIMEOUT);
  CPPUNIT_ASSERT_EQUAL(0,ret);
}


void ProtocolTest::testAlistSizedInsertStoredSize() {
  cerr << "*TEST* " << __PRETTY_FUNCTION__ << " (" << typeid(*this).name() << ")" << endl;
  rmc_ret_t ret = client.cmd_alist_sized_insert("CMP2_99",99,RomaValue("bbb",3),TIMEOUT);
  CPPUNIT_ASSERT_EQUAL(0,ret);
}


void ProtocolTest::testAlistSizedInsertNotStored() {
  cerr << "*TEST* " << __PRETTY_FUNCTION__ << " (" << typeid(*this).name() << ")" << endl;
  rmc_ret_t ret = client.cmd_alist_sized_insert("CMD_NOT_STORED",2,RomaValue("bbb",3),TIMEOUT);
  CPPUNIT_ASSERT_EQUAL(1,ret);
}


void ProtocolTest::testAlistSizedInsertServerError() {
  cerr << "*TEST* " << __PRETTY_FUNCTION__ << " (" << typeid(*this).name() << ")" << endl;
  try{
    rmc_ret_t ret = client.cmd_alist_sized_insert("CMD_SERVER_ERROR",2,RomaValue("bbb",3),TIMEOUT);
    (void)ret;
    CPPUNIT_FAIL("Should throw !");
  }catch(const Exception & ex){
    CPPUNIT_ASSERT(client.get_lasterror() != NULL);
  }
}


void ProtocolTest::testAlistSizedInsertError() {
  cerr << "*TEST* " << __PRETTY_FUNCTION__ << " (" << typeid(*this).name() << ")" << endl;
  try{
    rmc_ret_t ret = client.cmd_alist_sized_insert("CMD_ERROR",2,RomaValue("bbb",3),TIMEOUT);
    (void)ret;
    CPPUNIT_FAIL("Should throw !");
  }catch(const Exception & ex){
    CPPUNIT_ASSERT(client.get_lasterror() != NULL);
  }
}


void ProtocolTest::testAlistSizedInsertTimeout() {
  cerr << "*TEST* " << __PRETTY_FUNCTION__ << " (" << typeid(*this).name() << ")" << endl;
  try {
    rmc_ret_t ret = client.cmd_alist_sized_insert("TO_10",2,RomaValue("bbb",3),TIMEOUT);
    (void)ret;
    CPPUNIT_FAIL("Should throw !");
  }catch(const Exception & ex){
  }
}


void ProtocolTest::testAlistSizedInsertClose() {
  cerr << "*TEST* " << __PRETTY_FUNCTION__ << " (" << typeid(*this).name() << ")" << endl;
  try{
    rmc_ret_t ret = client.cmd_alist_sized_insert("CLOSE_",2,RomaValue("bbb",3),TIMEOUT);
    (void)ret;
    CPPUNIT_FAIL("Should throw !");
  }catch(const Exception & ex){
    CPPUNIT_ASSERT(client.get_lasterror() != NULL);
  }
}



void ProtocolTest::testAlistJoinNull() {
  cerr << "*TEST* " << __PRETTY_FUNCTION__ << " (" << typeid(*this).name() << ")" << endl;
  RomaValue v = client.cmd_alist_join("CMD_NULL",",",TIMEOUT);
  CPPUNIT_ASSERT_EQUAL((const char *)0,v.data);
  CPPUNIT_ASSERT_EQUAL((long)-1,v.length);
}


void ProtocolTest::testAlistJoinSep() {
  cerr << "*TEST* " << __PRETTY_FUNCTION__ << " (" << typeid(*this).name() << ")" << endl;
  RomaValue v = client.cmd_alist_join("CMPV_X","X",TIMEOUT);
  CPPUNIT_ASSERT_EQUAL((const char *)0,v.data);
  CPPUNIT_ASSERT_EQUAL((long)-1,v.length);
}


void ProtocolTest::testAlistJoinValue() {
  cerr << "*TEST* " << __PRETTY_FUNCTION__ << " (" << typeid(*this).name() << ")" << endl;
  RomaValue v = client.cmd_alist_join("CMD_VALUE",",",TIMEOUT);
  CPPUNIT_ASSERT_EQUAL(string("FOO,BAR,BAZ"),string(v.data));
}


void ProtocolTest::testAlistJoinLarge() {
  cerr << "*TEST* " << __PRETTY_FUNCTION__ << " (" << typeid(*this).name() << ")" << endl;
  RomaValue v = client.cmd_alist_join("CMD_LARGE",",",TIMEOUT);
  CPPUNIT_ASSERT_EQUAL((long)100000,v.length);
  CPPUNIT_ASSERT_EQUAL((size_t)100000,strlen(v.data));
}


void ProtocolTest::testAlistJoinServerError() {
  cerr << "*TEST* " << __PRETTY_FUNCTION__ << " (" << typeid(*this).name() << ")" << endl;
  try {
    RomaValue v = client.cmd_alist_join("CMD_SERVER_ERROR",",",TIMEOUT);
    CPPUNIT_FAIL("Should throw !");
  }catch(const Exception & ex){
  }
}


void ProtocolTest::testAlistJoinError() {
  cerr << "*TEST* " << __PRETTY_FUNCTION__ << " (" << typeid(*this).name() << ")" << endl;
  try {
    RomaValue v = client.cmd_alist_join("CMD_ERROR",",",TIMEOUT);
    (void)v;
    CPPUNIT_FAIL("Should throw !");
  }catch(const Exception & ex){
  }
}


void ProtocolTest::testAlistJoinTimeout() {
  cerr << "*TEST* " << __PRETTY_FUNCTION__ << " (" << typeid(*this).name() << ")" << endl;
  try {
    RomaValue v = client.cmd_alist_join("TO_10",",",TIMEOUT);
    (void)v;
    CPPUNIT_FAIL("Should throw !");
  }catch(const Exception & ex){
  }
}


void ProtocolTest::testAlistJoinClose() {
  cerr << "*TEST* " << __PRETTY_FUNCTION__ << " (" << typeid(*this).name() << ")" << endl;
  try {
    RomaValue v = client.cmd_alist_join("CLOSE_",",",TIMEOUT);
    (void)v;
    CPPUNIT_FAIL("Should throw !");
  }catch(const Exception & ex){
  }
}



void ProtocolTest::testAlistDeleteVal() {
  cerr << "*TEST* " << __PRETTY_FUNCTION__ << " (" << typeid(*this).name() << ")" << endl;
  rmc_ret_t ret = client.cmd_alist_delete("CMPV_bbbBBbb",RomaValue("bbbBBbb",7),TIMEOUT);
  CPPUNIT_ASSERT_EQUAL(0,ret);
}


void ProtocolTest::testAlistDeleteDeleted() {
  cerr << "*TEST* " << __PRETTY_FUNCTION__ << " (" << typeid(*this).name() << ")" << endl;
  rmc_ret_t ret = client.cmd_alist_delete("CMD_DELETED",RomaValue("bbb",3),TIMEOUT);
  CPPUNIT_ASSERT_EQUAL(0,ret);
}


void ProtocolTest::testAlistDeleteNotDeleted() {
  cerr << "*TEST* " << __PRETTY_FUNCTION__ << " (" << typeid(*this).name() << ")" << endl;
  rmc_ret_t ret = client.cmd_alist_delete("CMD_NOT_DELETED",RomaValue("bbb",3),TIMEOUT);
  CPPUNIT_ASSERT_EQUAL(1,ret);
}


void ProtocolTest::testAlistDeleteNotFound() {
  cerr << "*TEST* " << __PRETTY_FUNCTION__ << " (" << typeid(*this).name() << ")" << endl;
  rmc_ret_t ret = client.cmd_alist_delete("CMD_NOT_FOUND",RomaValue("bbb",3),TIMEOUT);
  CPPUNIT_ASSERT_EQUAL(1,ret);
}


void ProtocolTest::testAlistDeleteServerError() {
  cerr << "*TEST* " << __PRETTY_FUNCTION__ << " (" << typeid(*this).name() << ")" << endl;
  try{
    rmc_ret_t ret = client.cmd_alist_delete("CMD_SERVER_ERROR",RomaValue("bbb",3),TIMEOUT);
    (void)ret;
    CPPUNIT_FAIL("Should throw !");
  }catch(const Exception & ex){
    CPPUNIT_ASSERT(client.get_lasterror() != NULL);
  }
}


void ProtocolTest::testAlistDeleteError() {
  cerr << "*TEST* " << __PRETTY_FUNCTION__ << " (" << typeid(*this).name() << ")" << endl;
  try{
    rmc_ret_t ret = client.cmd_alist_delete("CMD_ERROR",RomaValue("bbb",3),TIMEOUT);
    (void)ret;
    CPPUNIT_FAIL("Should throw !");
  }catch(const Exception & ex){
    CPPUNIT_ASSERT(client.get_lasterror() != NULL);
  }
}


void ProtocolTest::testAlistDeleteTimeout() {
  cerr << "*TEST* " << __PRETTY_FUNCTION__ << " (" << typeid(*this).name() << ")" << endl;
  try {
    rmc_ret_t ret = client.cmd_alist_delete("TO_10",RomaValue("bbb",3),TIMEOUT);
    (void)ret;
    CPPUNIT_FAIL("Should throw !");
  }catch(const Exception & ex){
  }
}


void ProtocolTest::testAlistDeleteClose() {
  cerr << "*TEST* " << __PRETTY_FUNCTION__ << " (" << typeid(*this).name() << ")" << endl;
  try{
    rmc_ret_t ret = client.cmd_alist_delete("CLOSE_",RomaValue("bbb",3),TIMEOUT);
    (void)ret;
    CPPUNIT_FAIL("Should throw !");
  }catch(const Exception & ex){
    CPPUNIT_ASSERT(client.get_lasterror() != NULL);
  }
}



void ProtocolTest::testAlistDeleteAtPos() {
  cerr << "*TEST* " << __PRETTY_FUNCTION__ << " (" << typeid(*this).name() << ")" << endl;
  rmc_ret_t ret = client.cmd_alist_delete_at("CMP2_3",3,TIMEOUT);
  CPPUNIT_ASSERT_EQUAL(0,ret);
}


void ProtocolTest::testAlistDeleteAtDeleted() {
  cerr << "*TEST* " << __PRETTY_FUNCTION__ << " (" << typeid(*this).name() << ")" << endl;
  rmc_ret_t ret = client.cmd_alist_delete_at("CMD_DELETED",1,TIMEOUT);
  CPPUNIT_ASSERT_EQUAL(0,ret);
}


void ProtocolTest::testAlistDeleteAtNotDeleted() {
  cerr << "*TEST* " << __PRETTY_FUNCTION__ << " (" << typeid(*this).name() << ")" << endl;
  rmc_ret_t ret = client.cmd_alist_delete_at("CMD_NOT_DELETED",1,TIMEOUT);
  CPPUNIT_ASSERT_EQUAL(1,ret);
}


void ProtocolTest::testAlistDeleteAtNotFound() {
  cerr << "*TEST* " << __PRETTY_FUNCTION__ << " (" << typeid(*this).name() << ")" << endl;
  rmc_ret_t ret = client.cmd_alist_delete_at("CMD_NOT_FOUND",1,TIMEOUT);
  CPPUNIT_ASSERT_EQUAL(1,ret);
}


void ProtocolTest::testAlistDeleteAtServerError() {
  cerr << "*TEST* " << __PRETTY_FUNCTION__ << " (" << typeid(*this).name() << ")" << endl;
  try{
    rmc_ret_t ret = client.cmd_alist_delete_at("CMD_SERVER_ERROR",1,TIMEOUT);
    (void)ret;
    CPPUNIT_FAIL("Should throw !");
  }catch(const Exception & ex){
    CPPUNIT_ASSERT(client.get_lasterror() != NULL);
  }
}


void ProtocolTest::testAlistDeleteAtError() {
  cerr << "*TEST* " << __PRETTY_FUNCTION__ << " (" << typeid(*this).name() << ")" << endl;
  try{
    rmc_ret_t ret = client.cmd_alist_delete_at("CMD_ERROR",1,TIMEOUT);
    (void)ret;
    CPPUNIT_FAIL("Should throw !");
  }catch(const Exception & ex){
    CPPUNIT_ASSERT(client.get_lasterror() != NULL);
  }
}


void ProtocolTest::testAlistDeleteAtTimeout() {
  cerr << "*TEST* " << __PRETTY_FUNCTION__ << " (" << typeid(*this).name() << ")" << endl;
  try {
    rmc_ret_t ret = client.cmd_alist_delete_at("TO_10",1,TIMEOUT);
    (void)ret;
    CPPUNIT_FAIL("Should throw !");
  }catch(const Exception & ex){
  }
}


void ProtocolTest::testAlistDeleteAtClose() {
  cerr << "*TEST* " << __PRETTY_FUNCTION__ << " (" << typeid(*this).name() << ")" << endl;
  try{
    rmc_ret_t ret = client.cmd_alist_delete_at("CLOSE_",1,TIMEOUT);
    (void)ret;
    CPPUNIT_FAIL("Should throw !");
  }catch(const Exception & ex){
    CPPUNIT_ASSERT(client.get_lasterror() != NULL);
  }
}


template<class TEST>
struct GetSuite {
  CppUnit::TestSuite * operator()() const{
    CppUnit::TestSuite *suite = new CppUnit::TestSuite();
    suite->addTest(new CppUnit::TestCaller<TEST >("testNumValidConnection",&TEST::testNumValidConnection));

    suite->addTest(new CppUnit::TestCaller<TEST >("testSetStoredVal",&TEST::testSetStoredVal));
    suite->addTest(new CppUnit::TestCaller<TEST >("testSetStored",&TEST::testSetStored));
    suite->addTest(new CppUnit::TestCaller<TEST >("testSetStoredExp",&TEST::testSetStoredExp));
    suite->addTest(new CppUnit::TestCaller<TEST >("testSetNotStored",&TEST::testSetNotStored));
    suite->addTest(new CppUnit::TestCaller<TEST >("testSetServerError",&TEST::testSetServerError));
    suite->addTest(new CppUnit::TestCaller<TEST >("testSetError",&TEST::testSetError));
    suite->addTest(new CppUnit::TestCaller<TEST >("testSetTimeout",&TEST::testSetTimeout));
    suite->addTest(new CppUnit::TestCaller<TEST >("testSetClose",&TEST::testSetClose));

    suite->addTest(new CppUnit::TestCaller<TEST >("testGetNull",&TEST::testGetNull));
    suite->addTest(new CppUnit::TestCaller<TEST >("testGetValue",&TEST::testGetValue));
    suite->addTest(new CppUnit::TestCaller<TEST >("testGetValueError1",&TEST::testGetValueError1));
    suite->addTest(new CppUnit::TestCaller<TEST >("testGetValueError2",&TEST::testGetValueError2));
    suite->addTest(new CppUnit::TestCaller<TEST >("testGetValueError3",&TEST::testGetValueError3));
    suite->addTest(new CppUnit::TestCaller<TEST >("testGetValueError4",&TEST::testGetValueError4));
    suite->addTest(new CppUnit::TestCaller<TEST >("testGetLarge",&TEST::testGetLarge));
    suite->addTest(new CppUnit::TestCaller<TEST >("testGetServerError",&TEST::testGetServerError));
    suite->addTest(new CppUnit::TestCaller<TEST >("testGetError",&TEST::testGetError));
    suite->addTest(new CppUnit::TestCaller<TEST >("testGetTimeout",&TEST::testGetTimeout));
    suite->addTest(new CppUnit::TestCaller<TEST >("testGetClose",&TEST::testGetClose));
    suite->addTest(new CppUnit::TestCaller<TEST >("testGetClose2",&TEST::testGetClose2));

    suite->addTest(new CppUnit::TestCaller<TEST >("testDeleteDeleted",&TEST::testDeleteDeleted));
    suite->addTest(new CppUnit::TestCaller<TEST >("testDeleteNotDeleted",&TEST::testDeleteNotDeleted));
    suite->addTest(new CppUnit::TestCaller<TEST >("testDeleteNotFound",&TEST::testDeleteNotFound));
    suite->addTest(new CppUnit::TestCaller<TEST >("testDeleteServerError",&TEST::testDeleteServerError));
    suite->addTest(new CppUnit::TestCaller<TEST >("testDeleteError",&TEST::testDeleteError));
    suite->addTest(new CppUnit::TestCaller<TEST >("testDeleteTimeout",&TEST::testDeleteTimeout));
    suite->addTest(new CppUnit::TestCaller<TEST >("testDeleteClose",&TEST::testDeleteClose));

    suite->addTest(new CppUnit::TestCaller<TEST >("testAlistSizedInsertStored",&TEST::testAlistSizedInsertStored));
    suite->addTest(new CppUnit::TestCaller<TEST >("testAlistSizedInsertStoredVal",&TEST::testAlistSizedInsertStoredVal));
    suite->addTest(new CppUnit::TestCaller<TEST >("testAlistSizedInsertStoredSize",&TEST::testAlistSizedInsertStoredSize));
    suite->addTest(new CppUnit::TestCaller<TEST >("testAlistSizedInsertNotStored",&TEST::testAlistSizedInsertNotStored));
    suite->addTest(new CppUnit::TestCaller<TEST >("testAlistSizedInsertServerError",&TEST::testAlistSizedInsertServerError));
    suite->addTest(new CppUnit::TestCaller<TEST >("testAlistSizedInsertError",&TEST::testAlistSizedInsertError));
    suite->addTest(new CppUnit::TestCaller<TEST >("testAlistSizedInsertTimeout",&TEST::testAlistSizedInsertTimeout));
    suite->addTest(new CppUnit::TestCaller<TEST >("testAlistSizedInsertClose",&TEST::testAlistSizedInsertClose));

    suite->addTest(new CppUnit::TestCaller<TEST >("testAlistJoinNull",&TEST::testAlistJoinNull));
    suite->addTest(new CppUnit::TestCaller<TEST >("testAlistJoinSep",&TEST::testAlistJoinSep));
    suite->addTest(new CppUnit::TestCaller<TEST >("testAlistJoinValue",&TEST::testAlistJoinValue));
    suite->addTest(new CppUnit::TestCaller<TEST >("testAlistJoinLarge",&TEST::testAlistJoinLarge));
    suite->addTest(new CppUnit::TestCaller<TEST >("testAlistJoinServerError",&TEST::testAlistJoinServerError));
    suite->addTest(new CppUnit::TestCaller<TEST >("testAlistJoinError",&TEST::testAlistJoinError));
    suite->addTest(new CppUnit::TestCaller<TEST >("testAlistJoinTimeout",&TEST::testAlistJoinTimeout));
    suite->addTest(new CppUnit::TestCaller<TEST >("testAlistJoinClose",&TEST::testAlistJoinClose));

    suite->addTest(new CppUnit::TestCaller<TEST >("testAlistDeleteVal",&TEST::testAlistDeleteVal));
    suite->addTest(new CppUnit::TestCaller<TEST >("testAlistDeleteDeleted",&TEST::testAlistDeleteDeleted));
    suite->addTest(new CppUnit::TestCaller<TEST >("testAlistDeleteNotDeleted",&TEST::testAlistDeleteNotDeleted));
    suite->addTest(new CppUnit::TestCaller<TEST >("testAlistDeleteNotFound",&TEST::testAlistDeleteNotFound));
    suite->addTest(new CppUnit::TestCaller<TEST >("testAlistDeleteServerError",&TEST::testAlistDeleteServerError));
    suite->addTest(new CppUnit::TestCaller<TEST >("testAlistDeleteError",&TEST::testAlistDeleteError));
    suite->addTest(new CppUnit::TestCaller<TEST >("testAlistDeleteTimeout",&TEST::testAlistDeleteTimeout));
    suite->addTest(new CppUnit::TestCaller<TEST >("testAlistDeleteClose",&TEST::testAlistDeleteClose));

    suite->addTest(new CppUnit::TestCaller<TEST >("testAlistDeleteAtPos",&TEST::testAlistDeleteAtPos));
    suite->addTest(new CppUnit::TestCaller<TEST >("testAlistDeleteAtDeleted",&TEST::testAlistDeleteAtDeleted));
    suite->addTest(new CppUnit::TestCaller<TEST >("testAlistDeleteAtNotDeleted",&TEST::testAlistDeleteAtNotDeleted));
    suite->addTest(new CppUnit::TestCaller<TEST >("testAlistDeleteAtNotFound",&TEST::testAlistDeleteAtNotFound));
    suite->addTest(new CppUnit::TestCaller<TEST >("testAlistDeleteAtServerError",&TEST::testAlistDeleteAtServerError));
    suite->addTest(new CppUnit::TestCaller<TEST >("testAlistDeleteAtError",&TEST::testAlistDeleteAtError));
    suite->addTest(new CppUnit::TestCaller<TEST >("testAlistDeleteAtTimeout",&TEST::testAlistDeleteAtTimeout));
    suite->addTest(new CppUnit::TestCaller<TEST >("testAlistDeleteAtClose",&TEST::testAlistDeleteAtClose));

    return suite;
  }
};

CppUnit::TestSuite * ProtocolTest0::getSuite(){
  return GetSuite<ProtocolTest0>()();
}
CppUnit::TestSuite * ProtocolTest1::getSuite(){
  return GetSuite<ProtocolTest1>()();
}
