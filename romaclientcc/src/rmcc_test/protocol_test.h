/*
 * protocol_test.h - ????
 *
 *   Copyright (C) 2010 rakuten 
 *     by hiroaki.kubota <hiroaki.kubota@rakuten.co.jp> 
 *     Date : 2010/07/21
 */
#ifndef PROTOCOL_TEST_H
#define PROTOCOL_TEST_H

#include <cppunit/TestFixture.h>
#include <cppunit/TestSuite.h>
#include "rakuten/rmcc/rmcc.h"

using namespace std;
using namespace rakuten;
using namespace rmcc;

class ProtocolTest : public CppUnit::TestFixture {
public:
  RomaClient client;
  virtual void setUp();
  virtual void tearDown();
  void testGetNull();
  void testGetValue();
  void testGetServerError();
  void testGetError();
  void testGetTimeout();
  static CppUnit::TestSuite * getSuite();
};

#endif
