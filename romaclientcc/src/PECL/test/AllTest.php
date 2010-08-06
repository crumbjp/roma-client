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
        $suite->addTestSuite('RomaClientUnitTest0');
        $suite->addTestSuite('RomaClientUnitTest1');
        return $suite;
    }
}

?>