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
  void testNumValidConnection();

  void testSetStoredVal();
  void testSetStored();
  void testSetStoredExp();
  void testSetNotStored();
  void testSetServerError();
  void testSetError();
  void testSetTimeout();
  void testSetClose();

  void testGetNull();
  void testGetValue();
  void testGetServerError();
  void testGetError();
  void testGetTimeout();
  void testGetClose();

  void testAlistSizedInsertStored();
  void testAlistSizedInsertStoredVal();
  void testAlistSizedInsertStoredSize();
  void testAlistSizedInsertNotStored();
  void testAlistSizedInsertServerError();
  void testAlistSizedInsertError();
  void testAlistSizedInsertTimeout();
  void testAlistSizedInsertClose();

  void testAlistJoinNull();
  void testAlistJoinSep();
  void testAlistJoinValue();
  void testAlistJoinServerError();
  void testAlistJoinError();
  void testAlistJoinTimeout();
  void testAlistJoinClose();

  void testAlistDeleteVal();
  void testAlistDeleteDeleted();
  void testAlistDeleteNotDeleted();
  void testAlistDeleteNotFound();
  void testAlistDeleteServerError();
  void testAlistDeleteError();
  void testAlistDeleteTimeout();
  void testAlistDeleteClose();

  void testAlistDeleteAtPos();
  void testAlistDeleteAtDeleted();
  void testAlistDeleteAtNotDeleted();
  void testAlistDeleteAtNotFound();
  void testAlistDeleteAtServerError();
  void testAlistDeleteAtError();
  void testAlistDeleteAtTimeout();
  void testAlistDeleteAtClose();

  static CppUnit::TestSuite * getSuite();
};

#endif
