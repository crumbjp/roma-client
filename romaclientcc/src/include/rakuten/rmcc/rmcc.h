/**
 * @file rmcc.h
 * @brief Roma C++ client
 * <pre>
 *
 *  </pre>
 * @author hiroaki.kubota@mail.rakuten.co.jp
 * @date 2010-06-15
 * @version $Id: roma_client.h,v 1.0 2010/06/15 09:00:00 hiroaki.kubota Exp $
 * 
 * Copyright (C) 2010 hiroaki.kubota All rights reserved.
 */
#ifndef RMCC_H
#define RMCC_H

#include "rakuten/rmcc/rmcc_common.h"
#include "rakuten/rmcc/rmcc_connection.h"
#include "rakuten/exception.h"
#include <sstream>
#include <iostream>
namespace rakuten {
  namespace rmcc {
    class RomaClient {
      RomaConnection conn;
      RomaConnection::node_info_list_t node_info_list;
      std::ostringstream lasterr;
    public:
      /**
       * @brief Returns nodelist reference.
       *
       * @return Returns nodelist reference.
       */
      RomaConnection::node_info_list_t & get_nodelist();
      /**
       * @brief Returns last error message.
       *
       * @return Returns last error message.( When be issued Exception.)
       */
      const char * get_lasterror()const;
      /**
       * @brief Try to connect to the all-nodes of the nodelist.(Able to call repeatly)
       *
       * @throws Exception Around network error. It'll be set the error message.
       */
      void init(routing_mode_t routing_mode);
      /**
       * @brief Get the number of valid connection. 
       *
       * @return Returns the number of valid connection. 
       */
      int num_valid_connection() const;
      /**
       * @brief Try to disconnect to the all-nodes.
       *
       * @param routing_mode ROUTING_MODE_USE meams using routingdump.
       *
       * @throws Exception Around network error. It'll be set the error message.
       */
      void term();
      /**
       * @brief Issue STORE command.
       *
       * @param key Specify the key.
       * @param value Specify the value structure.
       * @param exptime Specify the expires. 0 means infinity.
       *
       * @return On success RMC_RET_OK returnd.
       * @throws Exception Around network error or SERVER_ERROR returns. It'll be set the error message.
       */
      rmc_ret_t cmd_store(const char *key, RomaValue value, long exptime,long timeout);
      /**
       * @brief Issue GET command.
       *
       * @param key Specify the key.
       *
       * @return Returns value-structure. This buffer is temporary. It'll be cleared when the next command is issued.
       * @throws Exception Around network error or SERVER_ERROR returns. It'll be set the error message.
       */
      RomaValue cmd_get(const char *key,long timeout);
      /**
       * @brief Issue ALIST_SIZED_INSERT command.
       *
       * @param key Specify the key.
       * @param size Specify the limit size of the ALIST.
       * @param value Specify the value in the ALIST.
       *
       * @return On success RMC_RET_OK returnd, On error, RMC_RET_ERROR returnd, and call rmc_geterr() to get message.
       * @throws Exception Around network error or SERVER_ERROR returns. It'll be set the error message.
       */
      rmc_ret_t cmd_alist_sized_insert(const char *key, long size, RomaValue value,long timeout);
      /**
       * @brief Issue ALIST_JOIN command.
       *
       * @param key Specify the key.
       * @param sep Specify the delimeter.
       *
       * @return Returns value-structure. This buffer is temporary. It'll be cleared when the next command is issued.
       * @throws Exception Around network error or SERVER_ERROR returns. It'll be set the error message.
       */
      RomaValue cmd_alist_join(const char *key,const char * sep,long timeout);
    };
  }
}

#endif
