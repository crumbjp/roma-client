<?php
require_once 'PHPUnit/Framework.php';
require_once 'PHPUnit/TextUI/TestRunner.php';
require_once 'RomaClientTest0.php';
require_once 'RomaClientTest1.php';
class AllTest extends PHPUnit_Framework_TestSuite {
    public static function suite() {
      //return new AllTest('RomaClientTest');
        $suite  = new PHPUnit_Framework_TestSuite();
        //$suite->addTestSuite('RomaClientTest');
        $suite->addTestSuite('RomaClientTest0');
        $suite->addTestSuite('RomaClientTest1');
        return $suite;
    }
}

?>