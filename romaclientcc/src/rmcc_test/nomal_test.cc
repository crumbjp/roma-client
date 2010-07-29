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
void NomalTest::testSet() {
  cerr << __PRETTY_FUNCTION__ << endl;

  {
    client.cmd_store("foo4",RomaValue("",0),100,TIMEOUT);
    RomaValue v = client.cmd_get("foo4",TIMEOUT);
    CPPUNIT_ASSERT_EQUAL((long)0,v.length);
  }
  {
    client.cmd_store("foo4",RomaValue(NULL,0),100,TIMEOUT);
    RomaValue v = client.cmd_get("foo4",TIMEOUT);
    CPPUNIT_ASSERT_EQUAL((long)0,v.length);
  }

  try{
    client.cmd_store(NULL,RomaValue("aaaa",4),100,TIMEOUT);
    CPPUNIT_FAIL("Should throw !");
  }catch(const Exception & ex){
  }

  try{
    client.cmd_store("",RomaValue("aaaa",4),100,TIMEOUT);
    CPPUNIT_FAIL("Should throw !");
  }catch(const Exception & ex){
  }
}

void NomalTest::testDelete() {
  cerr << __PRETTY_FUNCTION__ << endl;
  {
    RomaValue v = client.cmd_get("foo4",TIMEOUT);
    CPPUNIT_ASSERT_EQUAL((long)4,v.length);
    CPPUNIT_ASSERT_EQUAL(string("aaaa"),string(v.data));

    rmc_ret_t ret = client.cmd_delete("foo4",TIMEOUT); // DELETED
    CPPUNIT_ASSERT_EQUAL(RMC_RET_OK,ret);

    v = client.cmd_get("foo4",TIMEOUT);
    CPPUNIT_ASSERT_EQUAL((long)-1,v.length);
  }
  {
    rmc_ret_t ret = client.cmd_delete("foo4",TIMEOUT); // NOT_FOUND
    CPPUNIT_ASSERT_EQUAL(RMC_RET_OK,ret);
  }
  try{
    client.cmd_delete(NULL,TIMEOUT);
    CPPUNIT_FAIL("Should throw !");
  }catch(const Exception & ex){
  }
  try{
    client.cmd_delete("",TIMEOUT);
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
  {
    RomaValue v = client.cmd_get("not_found",TIMEOUT);
    CPPUNIT_ASSERT_EQUAL((long)-1,v.length);
  }  
  try{
    RomaValue v = client.cmd_get("",TIMEOUT);
    CPPUNIT_FAIL("Should throw !");
  }catch(const Exception & ex){
  }
  try{
    RomaValue v = client.cmd_get(NULL,TIMEOUT);
    CPPUNIT_FAIL("Should throw !");
  }catch(const Exception & ex){
  }
}
void NomalTest::testSizedInsert() {
  cerr << __PRETTY_FUNCTION__ << endl;
  {
    rmc_ret_t ret = client.cmd_alist_sized_insert("FOO",3,RomaValue("##1",3),TIMEOUT);
    CPPUNIT_ASSERT_EQUAL(RMC_RET_OK,ret);
  }
  {
    rmc_ret_t ret = client.cmd_alist_sized_insert("FOO",3,RomaValue("##2",3),TIMEOUT);
    CPPUNIT_ASSERT_EQUAL(RMC_RET_OK,ret);
  }
  {
    RomaValue v = client.cmd_alist_join("FOO",",",TIMEOUT);
    CPPUNIT_ASSERT_EQUAL(string("##2,##1,AAA"),string(v.data));
  }
  {
    rmc_ret_t ret = client.cmd_alist_sized_insert("FOO",3,RomaValue(NULL,0),TIMEOUT);
    CPPUNIT_ASSERT_EQUAL(RMC_RET_OK,ret);
    RomaValue v = client.cmd_alist_join("FOO",",",TIMEOUT);
    CPPUNIT_ASSERT_EQUAL(string(",##2,##1"),string(v.data));
  }
  {
    rmc_ret_t ret = client.cmd_alist_sized_insert("FOO",3,RomaValue("",0),TIMEOUT);
    CPPUNIT_ASSERT_EQUAL(RMC_RET_OK,ret);
    RomaValue v = client.cmd_alist_join("FOO",",",TIMEOUT);
    CPPUNIT_ASSERT_EQUAL(string(",,##2"),string(v.data));
  }
  {
    rmc_ret_t ret = client.cmd_alist_sized_insert("FOO",-1,RomaValue("##3",3),TIMEOUT);
    CPPUNIT_ASSERT_EQUAL(RMC_RET_OK,ret);
    RomaValue v = client.cmd_alist_join("FOO",",",TIMEOUT);
    CPPUNIT_ASSERT_EQUAL(string("##3,,"),string(v.data));
  }
  {
    rmc_ret_t ret = client.cmd_alist_sized_insert("FOO",0,RomaValue("##4",3),TIMEOUT);
    CPPUNIT_ASSERT_EQUAL(RMC_RET_OK,ret);
    RomaValue v = client.cmd_alist_join("FOO",",",TIMEOUT);
    CPPUNIT_ASSERT_EQUAL(string("##4,##3,,"),string(v.data));
  }
  try{
    client.cmd_alist_sized_insert(NULL,3,RomaValue("###",3),TIMEOUT);
    CPPUNIT_FAIL("Should throw !");
  }catch(const Exception & ex){
  }
  try{
    client.cmd_alist_sized_insert("",3,RomaValue("###",3),TIMEOUT);
    CPPUNIT_FAIL("Should throw !");
  }catch(const Exception & ex){
  }
}
void NomalTest::testJoin() {
  cerr << __PRETTY_FUNCTION__ << endl;
  {
    {
      rmc_ret_t ret = client.cmd_alist_sized_insert("FOO",3,RomaValue("###",3),TIMEOUT);
      CPPUNIT_ASSERT_EQUAL(RMC_RET_OK,ret);
    }
    {
      rmc_ret_t ret = client.cmd_alist_sized_insert("FOO",3,RomaValue("###",3),TIMEOUT);
      CPPUNIT_ASSERT_EQUAL(RMC_RET_OK,ret);
    }
    {
      RomaValue v = client.cmd_alist_join("FOO","",TIMEOUT);
      CPPUNIT_ASSERT_EQUAL(string("######AAA"),string(v.data));
    }
    try{
      RomaValue v = client.cmd_alist_join(NULL,",",TIMEOUT);
      CPPUNIT_FAIL("Should throw !");
    }catch(const Exception & ex){
    }
    try{
      RomaValue v = client.cmd_alist_join("",",",TIMEOUT);
      CPPUNIT_FAIL("Should throw !");
    }catch(const Exception & ex){
    }
    try{
      RomaValue v = client.cmd_alist_join("",NULL,TIMEOUT);
      CPPUNIT_FAIL("Should throw !");
    }catch(const Exception & ex){
    }
    {
      RomaValue v = client.cmd_alist_join("NOT_FOUND",",",TIMEOUT);
      CPPUNIT_ASSERT_EQUAL((long)-1,v.length);
    }

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
void NomalTest::testAlistDelete() {
  cerr << __PRETTY_FUNCTION__ << endl;
  {
    rmc_ret_t ret = client.cmd_alist_delete("FOO",RomaValue("AAA",3),TIMEOUT);
    CPPUNIT_ASSERT_EQUAL(RMC_RET_OK,ret);
    RomaValue v = client.cmd_alist_join("FOO",",",TIMEOUT);
    CPPUNIT_ASSERT_EQUAL(string("aaa"),string(v.data));
  }
  {
    rmc_ret_t ret = client.cmd_alist_delete("FOO",RomaValue("NOT_FOUND",9),TIMEOUT);
    CPPUNIT_ASSERT_EQUAL(RMC_RET_ERROR,ret);
  }
  {
    rmc_ret_t ret = client.cmd_alist_delete("FOO",RomaValue(NULL,0),TIMEOUT);
    CPPUNIT_ASSERT_EQUAL(RMC_RET_ERROR,ret);
  }
  {
    rmc_ret_t ret = client.cmd_alist_delete("FOO",RomaValue("",0),TIMEOUT);
    CPPUNIT_ASSERT_EQUAL(RMC_RET_ERROR,ret);
  }
  {
    rmc_ret_t ret = client.cmd_alist_sized_insert("FOO",3,RomaValue(NULL,0),TIMEOUT);
    CPPUNIT_ASSERT_EQUAL(RMC_RET_OK,ret);
    ret = client.cmd_alist_delete("FOO",RomaValue(NULL,0),TIMEOUT);
    CPPUNIT_ASSERT_EQUAL(RMC_RET_OK,ret);
  }
  {
    rmc_ret_t ret = client.cmd_alist_sized_insert("FOO",3,RomaValue(NULL,0),TIMEOUT);
    CPPUNIT_ASSERT_EQUAL(RMC_RET_OK,ret);
    ret = client.cmd_alist_delete("FOO",RomaValue("",0),TIMEOUT);
    CPPUNIT_ASSERT_EQUAL(RMC_RET_OK,ret);
  }
  {
    rmc_ret_t ret = client.cmd_alist_delete("NOT_FOUND",RomaValue("AAA",3),TIMEOUT);
    CPPUNIT_ASSERT_EQUAL(RMC_RET_ERROR,ret);
  }
  {
    try{
      client.cmd_alist_delete(NULL,RomaValue("AAA",3),TIMEOUT);
      CPPUNIT_FAIL("Should throw !");
    }catch(const Exception & ex){
    }
  }
  {
    try{
      client.cmd_alist_delete("",RomaValue("AAA",3),TIMEOUT);
      CPPUNIT_FAIL("Should throw !");
    }catch(const Exception & ex){
    }
  }

}
void NomalTest::testAlistDeleteAt() {
  cerr << __PRETTY_FUNCTION__ << endl;
  {
    rmc_ret_t ret = client.cmd_alist_delete_at("FOO",1,TIMEOUT);
    CPPUNIT_ASSERT_EQUAL(RMC_RET_OK,ret);
    RomaValue v = client.cmd_alist_join("FOO",",",TIMEOUT);
    CPPUNIT_ASSERT_EQUAL(string("AAA"),string(v.data));
  }
  {
    rmc_ret_t ret = client.cmd_alist_delete_at("FOO",10,TIMEOUT);
    CPPUNIT_ASSERT_EQUAL(RMC_RET_ERROR,ret);
  }
  {
    rmc_ret_t ret = client.cmd_alist_delete_at("FOO",-1,TIMEOUT);
    CPPUNIT_ASSERT_EQUAL(RMC_RET_OK,ret);
  }
  {
    rmc_ret_t ret = client.cmd_alist_delete_at("NOT_FOUND",2,TIMEOUT);
    CPPUNIT_ASSERT_EQUAL(RMC_RET_ERROR,ret);
  }
  try{
    client.cmd_alist_delete_at(NULL,0,TIMEOUT);
    CPPUNIT_FAIL("Should throw !");
  }catch(const Exception & ex){
  }
  try{
    client.cmd_alist_delete_at("",0,TIMEOUT);
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
  suite->addTest(new CppUnit::TestCaller<NomalTest>("testSet",&NomalTest::testSet));
  suite->addTest(new CppUnit::TestCaller<NomalTest>("testDelete",&NomalTest::testDelete));
  suite->addTest(new CppUnit::TestCaller<NomalTest>("testGet",&NomalTest::testGet));
  suite->addTest(new CppUnit::TestCaller<NomalTest>("testSizedInsert",&NomalTest::testSizedInsert));
  suite->addTest(new CppUnit::TestCaller<NomalTest>("testJoin",&NomalTest::testJoin));
  suite->addTest(new CppUnit::TestCaller<NomalTest>("testSizedInsertError",&NomalTest::testSizedInsertError));
  suite->addTest(new CppUnit::TestCaller<NomalTest>("testStoreError",&NomalTest::testStoreError));
  suite->addTest(new CppUnit::TestCaller<NomalTest>("testAlistDelete",&NomalTest::testAlistDelete));
  suite->addTest(new CppUnit::TestCaller<NomalTest>("testAlistDeleteAt",&NomalTest::testAlistDeleteAt));
  return suite;
}
