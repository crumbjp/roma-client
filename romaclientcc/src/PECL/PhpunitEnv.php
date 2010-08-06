<?php
require_once 'PHPUnit/Framework.php';

class PhpunitEnvTest extends PHPUnit_Framework_TestCase
{
    protected function setUp()
    {
    }

    protected function tearDown()
    {
    }

    public function testGetInstance()
    {
      $fp = fopen("php.env","w");
      fwrite($fp,"export PHP_EXT_DIR=");
      fwrite($fp,ini_get('extension_dir'));
      fwrite($fp,"\n");
      fclose($fp);

      $fp = fopen("php.ini","w");
      fwrite($fp,"include_path=");
      fwrite($fp,ini_get('include_path'));
      fwrite($fp,"\n");
      fclose($fp);
    }
}
?>
