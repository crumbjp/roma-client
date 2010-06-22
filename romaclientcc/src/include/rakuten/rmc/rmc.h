/**
 * @file rmc.h
 * @brief Roma C client
 * <pre>
 *
 *  </pre>
 * @author hiroaki.kubota@mail.rakuten.co.jp
 * @date 2010-06-15
 * @version $Id: roma_client.h,v 1.0 2010/06/15 09:00:00 hiroaki.kubota Exp $
 * 
 * Copyright (C) 2010 hiroaki.kubota All rights reserved.
 */
#ifndef RMC_H
#define RMC_H

#include <inttypes.h>
#include <unistd.h>

#ifdef	__cplusplus
extern "C" {
#endif
  //----------------------------------
  // Definitions
  //----------------------------------
  /**
   * @brief RMC object.
   */
  typedef void* rmc_t;
  /**
   * @brief RMC return code.
   */
  typedef int rmc_return;
  /**
   * @brief rmc_return : OK
   */
# define RMC_OK    (0)
  /**
   * @brief rmc_return : NG
   */
# define RMC_ERROR (-1)
  /**
   * @brief Type of CAS.
   */
  typedef int32_t cas_t;
  /**
   * @brief Roma value structure
   */
  typedef struct rmc_value_info {
    /**
     * @brief Pointing value.(Don't free this !!)
     */
    uint8_t *value;
    /**
     * @brief Value length.
     */
    size_t  length;
    /**
     * @brief Cas ID.
     */
    cas_t  cas;
  } rmc_value_info_t;
  /**
   * @brief Roma value list structure
   */
  typedef struct rmc_value_list {
    /**
     * @brief Pointing value info.(Don't free this !!)
     */
    rmc_value_info_t *value;
    /**
     * @brief List length.
     */
    size_t  length;
  } rmc_value_list_t;

  //----------------------------------
  // Basic I/F
  //----------------------------------
  /**
   * @brief Get RMC object;
   */
  rmc_t * get_rmc(const char * id);
  /**
   * @brief Get last error message.
   *
   * @return RMC object or NULL.
   */
  char * rmc_geterr(rmc_t * rmc);
  /**
   * @brief Call befor rmc_init() to specify a host to connect at the first.(Able to call repeatly)
   *
   * @param rmc RMC object
   * @param hostname Specify the Name or IP
   * @param port Specify the port
   *
   * @return On success RMC_OK returnd, On error, RMC_ERROR returnd, and call rmc_geterr() to get message.
   */
  rmc_return rmc_add_host(rmc_t * rmc,const char *hostname, const int & port);
  /**
   * @brief Call onece to initialize this library.
   *
   * @param rmc RMC object
   *
   * @return On success RMC_OK returnd, On error, RMC_ERROR returnd, and call rmc_geterr() to get message.
   */
  rmc_return rmc_init(rmc_t * rmc);
  /**
   * @brief Call onece to terminate, It'll disconnect from all hosts and free all resources.
   *
   * @param rmc RMC object
   *
   * @return On success RMC_OK returnd, On error, RMC_ERROR returnd, and call rmc_geterr() to get message.
   */
  rmc_return rmc_term(rmc_t * rmc);

  /**
   * @brief Issue STORE command.
   *
   * @param rmc RMC object
   * @param key Specify the key.
   * @param valinfo Specify the value structure.
   * @param exptime Specify the expires. 0 means infinity.
   *
   * @return On success RMC_OK returnd, On error, RMC_ERROR returnd, and call rmc_geterr() to get message.
   */
  rmc_return rmc_set(rmc_t * rmc, const char *key, const rmc_value_info & valinfo, const long exptime);
  /**
   * @brief Issue ADD command.
   *
   * @param rmc RMC object
   * @param key Specify the key.
   * @param valinfo Specify the value structure.
   * @param exptime Specify the expires. 0 means infinity.
   *
   * @return On success RMC_OK returnd, On error, RMC_ERROR returnd, and call rmc_geterr() to get message.
   */
  rmc_return rmc_add(rmc_t * rmc, const char *key, const rmc_value_info & valinfo, const long exptime);
  /**
   * @brief Issue REPLACE command.
   *
   * @param rmc RMC object
   * @param key Specify the key.
   * @param valinfo Specify the value structure.
   * @param exptime Specify the expires. 0 means infinity.
   *
   * @return On success RMC_OK returnd, On error, RMC_ERROR returnd, and call rmc_geterr() to get message.
   */
  rmc_return rmc_replace(rmc_t * rmc, const char *key, const rmc_value_info & valinfo, const long exptime);
  /**
   * @brief Issue APPEND command.
   *
   * @param rmc RMC object
   * @param key Specify the key.
   * @param valinfo Specify the value structure.
   * @param exptime Specify the expires. 0 means infinity.
   *
   * @return On success RMC_OK returnd, On error, RMC_ERROR returnd, and call rmc_geterr() to get message.
   */
  rmc_return rmc_append(rmc_t * rmc, const char *key, const rmc_value_info & valinfo, const long exptime);
  /**
   * @brief Issue PREPEND command.
   *
   * @param rmc RMC object
   * @param key Specify the key.
   * @param valinfo Specify the value structure.
   * @param exptime Specify the expires. 0 means infinity.
   *
   * @return On success RMC_OK returnd, On error, RMC_ERROR returnd, and call rmc_geterr() to get message.
   */
  rmc_return rmc_prepend(rmc_t * rmc, const char *key, const rmc_value_info & valinfo, const long exptime);
  /**
   * @brief Issue CAS command.
   *
   * @param rmc RMC object
   * @param key Specify the key.
   * @param valinfo Specify the value structure.
   * @param exptime Specify the expires. 0 means infinity.
   *
   * @return On success RMC_OK returnd, On error, RMC_ERROR returnd, and call rmc_geterr() to get message.
   */
  rmc_return rmc_cas(rmc_t * rmc, const char *key, const rmc_value_info & valinfo, const long exptime);
  /**
   * @brief Issue DELETE command.
   *
   * @param rmc RMC object
   * @param key Specify the key.
   *
   * @return On success RMC_OK returnd, On error, RMC_ERROR returnd, and call rmc_geterr() to get message.
   */
  rmc_return rmc_delete(rmc_t * rmc, const char *key);
  /**
   * @brief Issue GET command.
   *
   * @param rmc RMC object
   * @param valinfo Output value structure.
   * @param key Specify the key.
   *
   * @return On success RMC_OK returnd, On error, RMC_ERROR returnd, and call rmc_geterr() to get message.
   */
  rmc_return rmc_get(rmc_t * rmc, rmc_value_info & valinfo,const char *key);
  /**
   * @brief Issue GETS command.
   *
   * @param rmc RMC object
   * @param valinfo Output value structure (including cas).
   * @param key Specify the key.
   *
   * @return On success RMC_OK returnd, On error, RMC_ERROR returnd, and call rmc_geterr() to get message.
   */
  rmc_return rmc_gets(rmc_t * rmc, rmc_value_info & valinfo,const char *key);
  /**
   * @brief Add key to list, It'll process at rmc_getbulk_exec()
   *
   * @param rmc RMC object
   * @param key Specify the key.
   *
   * @return when return without RMC_OK, Call rmc_geterr() and it'll get message.
   */
  rmc_return rmc_getbulk_addkey(rmc_t * rmc, const char * key);
  /**
   * @brief Issue GETS command.(It surely resets all of added keys.)
   *
   * @param rmc RMC object
   * @param vallist Output value list structure.
   *
   * @return On success RMC_OK returnd, On error, RMC_ERROR returnd, and call rmc_geterr() to get message.
   */
  rmc_return rmc_getbulk_exec(rmc_t * rmc, rmc_value_list & vallist);
  //----------------------------------
  // ALIST
  //----------------------------------

  /**
   * @brief Issue ALIST_AT command.
   *
   * @param rmc RMC object
   * @param valinfo Output value structure.
   * @param key Specify the key.
   * @param index Specify the potision.
   *
   * @return On success RMC_OK returnd, On error, RMC_ERROR returnd, and call rmc_geterr() to get message.
   */
  rmc_return rmc_alist_at(rmc_t * rmc, rmc_value_info & valinfo,const char *key, const int index);
  /**
   * @brief Issue ALIST_CLEAR command.
   *
   * @param key Specify the key.
   *
   * @return On success RMC_OK returnd, On error, RMC_ERROR returnd, and call rmc_geterr() to get message.
   */
  rmc_return rmc_alist_clear(rmc_t * rmc, const char *key);
  /**
   * @brief Issue ALIST_DELETE command.
   *
   * @param rmc RMC object
   * @param key Specify the key.
   * @param valinfo Specify the value in the ALIST.
   *
   * @return On success RMC_OK returnd, On error, RMC_ERROR returnd, and call rmc_geterr() to get message.
   */
  rmc_return rmc_alist_delete(rmc_t * rmc, const char *key, const rmc_value_info & valinfo);
  /**
   * @brief Issue ALIST_AT command.
   *
   * @param rmc RMC object
   * @param key Specify the key.
   * @param index Specify the potision.
   *
   * @return On success RMC_OK returnd, On error, RMC_ERROR returnd, and call rmc_geterr() to get message.
   */
  rmc_return rmc_alist_delete_at(rmc_t * rmc, const char *key, const int index);
  // rmc_return rmc_alist_empty(const char *key);
  // rmc_return rmc_alist_first(rmc_value_info & valinfo,const char *key);
  // rmc_return rmc_alist_include(const char *key, const rmc_value_info & valinfo);
  /**
   * @brief Issue ALIST_INDEX command.
   *
   * @param rmc RMC object
   * @param key Specify the key.
   * @param valinfo Specify the value in the ALIST.
   *
   * @return Returns position of specified value. On error, RMC_ERROR is returnd, and call rmc_geterr() to get message.
   */
  rmc_return rmc_alist_index(rmc_t * rmc, const char *key, const rmc_value_info & valinfo);
  /**
   * @brief Issue ALIST_INSERT command.
   *
   * @param rmc RMC object
   * @param key Specify the key.
   * @param valinfo Specify the value in the ALIST.
   *
   * @return On success RMC_OK returnd, On error, RMC_ERROR returnd, and call rmc_geterr() to get message.
   */
  rmc_return rmc_alist_insert(rmc_t * rmc, const char *key, const int index, const rmc_value_info & valinfo);
  /**
   * @brief Issue ALIST_SIZED_INSERT command.
   *
   * @param rmc RMC object
   * @param key Specify the key.
   * @param size Specify the limit size of the ALIST.
   * @param valinfo Specify the value in the ALIST.
   *
   * @return On success RMC_OK returnd, On error, RMC_ERROR returnd, and call rmc_geterr() to get message.
   */
  rmc_return rmc_alist_sized_insert(rmc_t * rmc, const char *key, const int size, const rmc_value_info & valinfo);
  /**
   * @brief Issue ALIST_JOIN command.
   *
   * @param rmc RMC object
   * @param valinfo Output value structure, The value is a string that is all datas in the ALIST separated by specifing charctors.
   * @param key Specify the key.
   * @param seperator Specify the delimeter.
   *
   * @return On success RMC_OK returnd, On error, RMC_ERROR returnd, and call rmc_geterr() to get message.
   */
  rmc_return rmc_alist_join(rmc_t * rmc, rmc_value_info & valinfo,const char *key, const char *separator);
  //rmc_return rmc_alist_to_json(rmc_value_info & valinfo,const char * key);
  //rmc_return rmc_alist_last(rmc_value_info & valinfo,const char *key);
  /**
   * @brief Issue ALIST_LENGTH command.
   *
   * @param rmc RMC object
   * @param key Specify the key.
   * @param valinfo Specify the value in the ALIST.
   *
   * @return Returns length of the ALIST. On error, RMC_ERROR is returnd, and call rmc_geterr() to get message.
   */
  rmc_return rmc_alist_length(rmc_t * rmc, const char *key);
  /**
   * @brief Issue ALIST_POP command.
   *
   * @param rmc RMC object
   * @param valinfo Output value structure.
   * @param key Specify the key.
   *
   * @return On success RMC_OK returnd, On error, RMC_ERROR returnd, and call rmc_geterr() to get message.
   */
  rmc_return rmc_alist_pop(rmc_t * rmc, rmc_value_info & valinfo,const char *key);
  /**
   * @brief Issue ALIST_PUSH command.
   *
   * @param rmc RMC object
   * @param key Specify the key.
   * @param valinfo Specify the value in the ALIST.
   *
   * @return On success RMC_OK returnd, On error, RMC_ERROR returnd, and call rmc_geterr() to get message.
   */
  rmc_return rmc_alist_push(rmc_t * rmc, const char *key, const rmc_value_info & valinfo);
  /**
   * @brief Issue ALIST_SHIFT command.
   *
   * @param rmc RMC object
   * @param valinfo Output value structure.
   * @param key Specify the key.
   *
   * @return On success RMC_OK returnd, On error, RMC_ERROR returnd, and call rmc_geterr() to get message.
   */
  rmc_return rmc_alist_shift(rmc_t * rmc, rmc_value_info & valinfo,const char *key);
  //rmc_return rmc_alist_tostr(rmc_value_info & valinfo,const char *key);

  /**
   * @brief Issue ALIST_GETS command.
   *
   * @param rmc RMC object
   * @param vallist Output value list structure.
   * @param key Specify the key.
   *
   * @return On success RMC_OK returnd, On error, RMC_ERROR returnd, and call rmc_geterr() to get message.
   */
  rmc_return rmc_alist_gets(rmc_t * rmc, rmc_value_list & valinfo,const char *key);

#ifdef	__cplusplus
}
#endif

#endif


// ========================================================
//  
// ========================================================
// #define RMC_SERVER_ERROR   -1
// #define RMC_NOT_STORED     -2
// #define RMC_NOT_FOUND      -3
// #define RMC_NOT_CLEARED    -4
// #define RMC_ALIST_NULL     -5

// #define RMC_STORED          1
// #define RMC_DELETED         2
// #define RMC_CLEARED         3

// #define RMC_ALIST_TRUE      5
// #define RMC_ALIST_FALSE     6

// #define RMC_TIMEOUT         3

