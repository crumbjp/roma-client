/*
 * loop_connection_test.h - ????
 *
 *   Copyright (C) 2010 rakuten 
 *     by hiroaki.kubota <hiroaki.kubota@rakuten.co.jp> 
 *     Date : 2010/07/21
 */
#ifndef LOOP_CONNECTION_TEST_H
#define LOOP_CONNECTION_TEST_H

#include <cppunit/TestFixture.h>
#include <cppunit/TestSuite.h>
#include "rakuten/rmcc/rmcc.h"

using namespace std;
using namespace rakuten;
using namespace rmcc;

class LoopConnectionTest : public CppUnit::TestFixture {
public:
  RomaClient client;
  virtual void setUp()=0;
  virtual void tearDown();
  void testLoopAllNodeDown();
  static CppUnit::TestSuite * getSuite();
};
class LoopConnectionTest0 : public LoopConnectionTest {
public:
  virtual void setUp();
  static CppUnit::TestSuite * getSuite();
};
class LoopConnectionTest1 : public LoopConnectionTest {
public:
  virtual void setUp();
  static CppUnit::TestSuite * getSuite();
};

#endif
