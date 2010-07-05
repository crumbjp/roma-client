<?php
/**
 * @file RomaClient.php
 * @brief Roma PHP client
 * <pre>
 * 
 *  </pre>
 * @example test.php
 * @author hiroaki.kubota@mail.rakuten.co.jp
 * @date 2010-06-15
 * @version $Id: RomaClient.php,v 1.0 2010/06/15 09:00:00 hiroaki.kubota Exp $
 */
#extension_loaded('phprmcc') || dl('phprmcc.so');

class RomaClient {
    private $client_id = "";

    /**
     * @deprecated 
     */
    const SERVER_ERROR = -1;
    /**
     * @deprecated 
     */
    const NOT_STORED   = -2;
    /**
     * @deprecated 
     */
    const NOT_FOUND    = -3;
    /**
     * @deprecated 
     */
    const NOT_CLEARED  = -4;
    /**
     * @deprecated 
     */
    const ALIST_NULL   = -5;
    /**
     * @deprecated 
     */
    const STORED       =  1;
    /**
     * @deprecated 
     */
    const DELETED      =  2;
    /**
     * @deprecated 
     */
    const CLEARED      =  3;
    /**
     * @deprecated 
     */
    const ALIST_TRUE   =  5;
    /**
     * @deprecated 
     */
    const ALIST_FALSE  =  6;
    
    
    const RMC_RET_ERROR = 1;

    /**
     * @brief Constructor 
     * 
     * @param cid Name of PECL's.
     */
    private function __construct($cid) {
      $this->client_id = $cid;
    }
    
    /**
     * @brief Get roma-client instance.
     * <pre>
     *   When the value of '-d' is contained in this array, it'll not use routing-dump.
     * </pre>
     * @param hosts Host array. It's formated by <hostname>_<port>.
     */
    public static function getInstance($hosts) {
      $ROUTING_MODE = 1;
      foreach ($hosts as &$value) {        
        if ( $value == "-d") {
          // Without route
          $ROUTING_MODE = 0;
          break;
        }
      }
      $client_id = rmc_init($hosts,$ROUTING_MODE);
      if ( $client_id == RomaClient::RMC_RET_ERROR ) {
        return NULL;
      }
      return new RomaClient($client_id);
    }
    
    /**
     * @brief Get value.(Issue 'get' command).
     * @param key 
     * @return value 
     */
    public function get($key) {
      $result = rmc_get($this->client_id, $key);
      if ( $result == RomaClient::RMC_RET_ERROR ) {
        return RomaClient::SERVER_ERROR;
      }
      if ( $result[1] < 0 ) {
        return null;
      }
      return $result[0];
    }
    
    /**
     * @brief Set value.(Issue 'store' command).
     * @param key             
     * @param value           
     * @param exptime Specify the value of expire second.
     * @return Returns True if success.
     */
    public function set($key, $value, $exptime) {
      $result = rmc_set($this->client_id,$key, $value, $exptime);
      if ( $result == RomaClient::RMC_RET_ERROR ) {
        return False;
      }
      return True;
    }
    

    /**
     * destructor.
     * 
     */
    public function __destruct() {
      // Instance cache.
      // rmc_term($this->client_id);
    }
    /**
     * @brief ALIST operation. (Issue 'alist_sized_insert' command)
     * @param key
     * @param size
     * @param value
     * @return Returns True if success.
     */
    public function alist_sized_insert($key, $size, $value) {
      $result = rmc_alist_sized_insert($this->client_id,$key, $size, $value);
      if ( $result == RomaClient::RMC_RET_ERROR ) {
        return False;
      }
      return True;
    }

    /**
     * @brief ALIST operation. (Issue 'alist_join' command)
     * @param key
     * @param separator Be careful to never conflict with values.
     * @return Return as the array.
     */
    public function alist_join($key, $separator) {
      // @@@ Todo: Should use alist_gets
      $result = rmc_alist_join($this->client_id,$key, $separator);
      if ( $result == RomaClient::RMC_RET_ERROR ) {
        return RomaClient::SERVER_ERROR;
      }
      if ( $result[1] < 0 ) {
        return null;
      }
      $token = strtok($result[0], $separator);
      $array = array();
      while($token) {
        array_push($array, ltrim($token, "\r\n"));
        $token = strtok($separator);
      }
      return $array;
    }

}

    /**
     * add value.
     * @param key             (string)
     * @param value           (string)
     * @param expire time     (int)
     * @return [success|fail] (bool)
     */
    /* public function add($key, $value, $exptime) { */
    /*     /\* $result = rmc_add($key, $value, $exptime); *\/ */
    /*     /\* return ($result == RomaClient::STORED ? True : False); *\/ */
    /*   throw new Exception("Not implements !"); */
    /* } */
    
    /**
     * replace value.
     * @param key             (string)
     * @param value           (string)
     * @param expire time     (int)
     * @return [success|fail] (bool)
     */
    /* public function replace($key, $value, $exptime) { */
    /*     /\* $result = rmc_replace($key, $value, $exptime); *\/ */
    /*     /\* return ($result == RomaClient::STORED ? True : False); *\/ */
    /*   throw new Exception("Not implements !"); */
    /* } */
    
    /**
     * append value.
     * @param key             (string)
     * @param value           (string)
     * @param expire time     (int)
     * @return [success|fail] (bool)
     */
    /* public function append($key, $value, $exptime) { */
    /*     /\* $result = rmc_append($key, $value, $exptime); *\/ */
    /*     /\* return ($result == RomaClient::STORED ? True : False); *\/ */
    /*   throw new Exception("Not implements !"); */
    /* } */
    
    /**
     * prepend value.
     * @param key             (string)
     * @param value           (string)
     * @param expire time     (int)
     * @return [success|fail] (bool)
     */
    /* public function prepend($key, $value, $exptime) { */
    /*     /\* $result = rmc_prepend($key, $value, $exptime); *\/ */
    /*     /\* return ($result == RomaClient::STORED ? True : False); *\/ */
    /*   throw new Exception("Not implements !"); */
    /* } */
    
    /**
     * delete value.
     * @param key             (string)
     * @return [success|fail] (bool)
     */
    /* public function delete($key) { */
    /*     /\* $result = rmc_delete($key); *\/ */
    /*     /\* return ($result == RomaClient::DELETED ? True : False); *\/ */
    /*   throw new Exception("Not implements !"); */
    /* } */
    
    //===== plugin - alist =====//
    /**
     * alist at.
     * @param key   (string)
     * @param index (int)
     * @return value
     */
    /* public function alist_at($key, $index) { */
    /*     /\* $result = rmc_alist_at($key, $index); *\/ */
    /*     /\* return $result; *\/ */
    /*   throw new Exception("Not implements !"); */
    /* } */

    /**
     * alist clear.
     * @param key (string)
     * @return status
     */
    /* public function alist_clear($key) { */
    /*     /\* $result = rmc_alist_clear($key); *\/ */
    /*     /\* return ($result == RomaClient::CLEARED ? True : False); *\/ */
    /*   throw new Exception("Not implements !"); */
    /* } */

    /**
     * alist delete.
     * @param key   (string)
     * @param value (string)
     * @return status
     */
    /* public function alist_delete($key, $value) { */
    /*     /\* $result = rmc_alist_delete($key, $value); *\/ */
    /*     /\* return ($result == RomaClient::DELETED ? True : False); *\/ */
    /*   throw new Exception("Not implements !"); */
    /* } */

    /**
     * alist delete at.
     * @param key   (string)
     * @param index (int)
     * @return status
     */
    /* public function alist_delete_at($key, $index) { */
    /*     /\* $result = rmc_alist_delete_at($key, $index); *\/ */
    /*     /\* return ($result == RomaClient::DELETED ? True : False); *\/ */
    /*   throw new Exception("Not implements !"); */
    /* } */

    /**
     * alist empty ?
     * @param key (string)
     * @return status
     */
    /* public function alist_empty($key) { */
    /*     /\* $result = rmc_alist_empty($key); *\/ */
    /*     /\* return ($result == RomaClient::ALIST_TRUE ? True : False); *\/ */
    /*   throw new Exception("Not implements !"); */
    /* } */

    /**
     * alist first.
     * @param key   (string)
     * @return value
     */
    /* public function alist_first($key) { */
    /*     /\* $result = rmc_alist_first($key); *\/ */
    /*     /\* return $result; *\/ */
    /*   throw new Exception("Not implements !"); */
    /* } */

    /**
     * alist include ?
     * @param key   (string)
     * @param value (string)
     * @return status
     */
    /* public function alist_include($key, $value) { */
    /*     /\* $result = rmc_alist_include($key, $value); *\/ */
    /*     /\* return ($result == RomaClient::ALIST_TRUE ? True : False); *\/ */
    /*   throw new Exception("Not implements !"); */
    /* } */

    /**
     * alist index.
     * @param key   (string)
     * @param value (int)
     * @return index/status
     */
    /* public function alist_index($key, $value) { */
    /*     /\* $result = rmc_alist_index($key, $value); *\/ */
    /*     /\* return $result; *\/ */
    /*   throw new Exception("Not implements !"); */
    /* } */

    /**
     * alist insert.
     * @param key   (string)
     * @param index (int)
     * @param value (string)
     * @return status
     */
    /* public function alist_insert($key, $index, $value) { */
    /*     /\* $result = rmc_alist_insert($key, $index, $value); *\/ */
    /*     /\* return ($result == RomaClient::STORED ? True : False); *\/ */
    /*   throw new Exception("Not implements !"); */
    /* } */

    /**
     * alist to json.
     * @param key (string)
     * @return value - json.
     */
    /* public function alist_to_json($key) { */
    /*     /\* $reuslt = rmc_alist_to_json($key); *\/ */
    /*     /\* return $result; *\/ */
    /*   throw new Exception("Not implements !"); */
    /* } */

    /**
     * alist last.
     * @param key (string)
     * @return value
     */
    /* public function alist_last($key) { */
    /*     /\* $result = rmc_alist_last($key); *\/ */
    /*     /\* return $result; *\/ */
    /*   throw new Exception("Not implements !"); */
    /* } */

    /**
     * alist length.
     * @param key (string)
     * @return length/status
     */
    /* public function alist_length($key) { */
    /*     /\* $result = rmc_alist_length($key); *\/ */
    /*     /\* return $result; *\/ */
    /*   throw new Exception("Not implements !"); */
    /* } */

    /**
     * alist pop.
     * @param key (string)
     * @return value
     */
    /* public function alist_pop($key) { */
    /*     /\* $result = rmc_alist_pop($key); *\/ */
    /*     /\* return $result; *\/ */
    /*   throw new Exception("Not implements !"); */
    /* } */

    /**
     * alist push.
     * @param key   (string)
     * @param value (string)
     * @return status
     */
    /* public function alist_push($key, $value) { */
    /*     /\* $result = rmc_alist_push($key, $value); *\/ */
    /*     /\* return ($result == RomaClient::STORED ? True : False); *\/ */
    /*   throw new Exception("Not implements !"); */
    /* } */

    /**
     * alist shift.
     * @param key (string)
     * @return value
     */
    /* public function alist_shift($key) { */
    /*     /\* $result = rmc_alist_shift($key); *\/ */
    /*     /\* return $result; *\/ */
    /*   throw new Exception("Not implements !"); */
    /* } */

    /**
     * alist to string.
     * @param key (string)
     * @return value
     */
    /* public function alist_to_str($key) { */
    /*     /\* $result = rmc_alist_to_str($key); *\/ */
    /*     /\* return $result; *\/ */
    /*   throw new Exception("Not implements !"); */
    /* } */

?> 
