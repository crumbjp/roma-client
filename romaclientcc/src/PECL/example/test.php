<?php
require_once("RomaClient.php");

$without_route = RomaClient::getInstance(array("-d","localhost_11211","localhost_11212"));
if ( $without_route != NULL ) {
  $res =  $without_route->set("rc","*@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@*",100);
  if ( $res == False ) {
    print "FALSE\n";
  } else {
    print "TRUE\n";
  }
  for ( $i = 0 ; $i < 10 ; $i++ ) {
    $val = $without_route->get("rc");
    print "V:$val\n";
  }
}
$use_route = RomaClient::getInstance(array("localhost_11211"));
if ( $use_route != NULL ) {
  $use_route->set("rc2","**************************************",100);
  for ( $i = 0 ; $i < 10 ; $i++ ) {
    $v2 = $use_route->get("rc2");
    print "V:$v2\n";
  }
}

?>