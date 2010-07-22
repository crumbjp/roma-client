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
  void testConnectionRefused();
  void testGet();
  void testSizedInsert();
  void testSizedInsertError();
  void testStoreError();
  void testDelete();
  void testDeleteAt();
  static CppUnit::TestSuite * getSuite();
};

#endif
