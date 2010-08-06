<?php
require_once 'PHPUnit/Framework.php';
require_once 'PHPUnit/TextUI/TestRunner.php';
require_once 'RomaClientUnitTest0.php';
require_once 'RomaClientUnitTest1.php';
require_once 'RomaClientUnitTest2.php';
class AllTest extends PHPUnit_Framework_TestSuite {
    public static function suite() {
      //return new AllTest('RomaClientTest');
        $suite  = new PHPUnit_Framework_TestSuite();
        //$suite->addTestSuite('RomaClientTest');
        $suite->addTestSuite('RomaClientUnitTest0');
        $suite->addTestSuite('RomaClientUnitTest1');
        $suite->addTestSuite('RomaClientUnitTest2');
        return $suite;
    }
}

?>