/*
 * nomal_test.h - ????
 *
 *   Copyright (C) 2010 rakuten 
 *     by hiroaki.kubota <hiroaki.kubota@rakuten.co.jp> 
 *     Date : 2010/07/21
 */
#ifndef NOMAL_TEST_H
#define NOMAL_TEST_H

#include <cppunit/TestFixture.h>
#include <cppunit/TestSuite.h>
#include "rakuten/rmcc/rmcc.h"

using namespace std;
using namespace rakuten;
using namespace rmcc;

class NomalTest : public CppUnit::TestFixture {
public:
  RomaClient client;
  virtual void setUp();
  virtual void tearDown();
  void testUnknownServer();
  void testConnectFailed();

  void testSet();
  void testSetLargeKey();
  void testSetLargeVal();
  void testSetInvKey();
  void testSetEmpKey();
  void testSetEmpValue();

  void testDelete();
  void testDeleteInvKey();
  void testDeleteEmpKey();

  void testGet();
  void testGetNotfound();
  void testGetInvKey();
  void testGetEmpKey();

  void testSizedInsert();
  void testSizedInsertNegasize();
  void testSizedInsertZerosize();
  void testSizedInsertError();
  void testSizedInsertInvKey();
  void testSizedInsertEmpKey();
  void testSizedInsertEmpVal();

  void testJoin();
  void testJoinNotfound();
  void testJoinInvKey();
  void testJoinEmpKey();
  void testJoinEmpSep();

  void testAlistDelete();
  void testAlistDeleteNotfoundError();
  void testAlistDeleteValNotfoundError();
  void testAlistDeleteInvKey();
  void testAlistDeleteEmpKey();
  void testAlistDeleteEmpVal();

  void testAlistDeleteAt();
  void testAlistDeleteAtNegapos();
  void testAlistDeleteAtOutrangeError();
  void testAlistDeleteAtNotfoundError();
  void testAlistDeleteAtInvKey();
  void testAlistDeleteAtEmpKey();
  static CppUnit::TestSuite * getSuite();
};

#endif
