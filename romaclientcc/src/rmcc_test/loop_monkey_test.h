/*
 * loop_monkey_test.h - ????
 *
 *   Copyright (C) 2010 rakuten 
 *     by hiroaki.kubota <hiroaki.kubota@rakuten.co.jp> 
 *     Date : 2010/07/21
 */
#ifndef LOOP_MONKEY_TEST_H
#define LOOP_MONKEY_TEST_H

#include <cppunit/TestFixture.h>
#include <cppunit/TestSuite.h>
#include "rakuten/rmcc/rmcc.h"

using namespace std;
using namespace rakuten;
using namespace rmcc;

class LoopMonkeyTest : public CppUnit::TestFixture {
public:
  RomaClient client;
  virtual void setUp();
  virtual void tearDown();
  void testLoopWithAssert();
  void testLoopNoAssertAllNodeDown();
  static CppUnit::TestSuite * getSuite();
};

#endif
