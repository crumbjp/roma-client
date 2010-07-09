<?php
require_once("RomaClient.php");
$no_conn = RomaClient::getInstance(array("-d","localhost_12345"));
if ( !$no_conn ) {
  print "NO-CONN FALSE\n";
}
$without_route = RomaClient::getInstance(array("-d","localhost_11211","localhost_11212"));
if ( $without_route ) {
  $res =  $without_route->set("rc","*@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@*",100);
  for ( $i = 0 ; $i < 10 ; $i++ ) {
    $val = $without_route->get("rc");
    print "V:$val\n";
  }
}
$use_route = RomaClient::getInstance(array("localhost_11211"));
if ( $use_route ) {
  $use_route->set("rc2","**************************************",100);
  for ( $i = 0 ; $i < 10 ; $i++ ) {
    $v2 = $use_route->get("rc2");
    print "V:$v2\n";
  }
  $use_route->alist_sized_insert("rc3",10,"aaaa");
  $use_route->alist_sized_insert("rc3",10,"bbbb");
  $use_route->alist_sized_insert("rc3",10,"cccc");
  $use_route->alist_sized_insert("rc3",10,"dddd");
  $ls = $use_route->alist_join("rc3","-");
  foreach ($ls as &$l) {        
    print "$l\n";
  }
  // EXP
  try {
    $val =   $use_route->alist_sized_insert("rc2",10,"aaaa");
  }catch(Exception $ex ) {
    print "$ex\n";
    print "SUCCESS\n";
  }
}


?>