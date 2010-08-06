<?php
require_once 'PHPUnit/Framework.php';
require_once 'RomaClient.php';

/**
 * Test class for RomaClient.
 * Generated by PHPUnit on 2010-08-05 at 16:37:31.
 */
class RomaClientTest extends PHPUnit_Framework_TestCase
{
  public $nodes;

  /* public function __construct($arg) { */
  /*   $this->nodes = $arg; */
  /* } */

  protected $roma_client;

  protected static $VALID_KEY = "ValidKey";
  protected static $VALID_VAL = "ValidValue";
  protected static $VALID_ALIST_KEY = "ValidAKey";
  protected static $VALID_ALIST_VAL = "ValidAValue";
  /**
   * Sets up the fixture, for example, opens a network connection.
   * This method is called before a test is executed.
   *
   * @access protected
   */
  protected function setUp()
  {
    $this->roma_client = RomaClient::getInstance($this->nodes);
    $this->roma_client->set(self::$VALID_KEY,self::$VALID_VAL,60);
    $this->roma_client->alist_sized_insert(self::$VALID_ALIST_KEY,1,self::$VALID_ALIST_VAL);
  }
  /**
   * Tears down the fixture, for example, closes a network connection.
   * This method is called after a test is executed.
   *
   * @access protected
   */
  protected function tearDown()
  {
      
  }

  public function testGet()
  {
    $val = $this->roma_client->get(self::$VALID_KEY);
    $this->assertEquals(self::$VALID_VAL,$val);
  }

  public function testDelete()
  {
    $ret = $this->roma_client->delete(self::$VALID_KEY);
    $this->assertTrue($ret);
    $val = $this->roma_client->get(self::$VALID_KEY);
    $this->assertEquals(NULL,$val);
  }

  public function testAlist_join()
  {
    $arr = $this->roma_client->alist_join(self::$VALID_ALIST_KEY,",");
    $this->assertEquals(1,sizeof($arr));
  }

  public function testAlist_delete()
  {
    $ret = $this->roma_client->alist_delete(self::$VALID_ALIST_KEY,self::$VALID_ALIST_VAL);
    $this->assertTrue($ret);
    $arr = $this->roma_client->alist_join(self::$VALID_ALIST_KEY,",");
    $this->assertEquals(0,sizeof($arr));
  }

  public function testSet()
  {
    // Remove the following lines when you implement this test.
    $this->markTestIncomplete(
      'This test has not been implemented yet.'
      );
  }


  /**
   * @todo Implement testAlist_sized_insert().
   */
  public function testAlist_sized_insert()
  {
    // Remove the following lines when you implement this test.
    $this->markTestIncomplete(
      'This test has not been implemented yet.'
      );
  }



  /**
   * @todo Implement testAlist_delete_at().
   */
  public function testAlist_delete_at()
  {
    // Remove the following lines when you implement this test.
    $this->markTestIncomplete(
      'This test has not been implemented yet.'
      );
  }
}
?>
