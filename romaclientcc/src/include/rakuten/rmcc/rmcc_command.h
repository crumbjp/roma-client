/*
 * roma_command.h - ????
 *
 *   Copyright (C) 2010 rakuten 
 *     by hiroaki.kubota <hiroaki.kubota@rakuten.co.jp> 
 *     Date : 2010/06/16
 */
#ifndef RMCC_COMMAND_H
#define RMCC_COMMAND_H

#include "rakuten/rmcc/rmcc_common.h"
#include "rakuten/vbuffer.h"
#include <vector>
#include <map>

namespace rakuten {
  namespace rmcc {
    static const int CRLF_SIZE = 2;
    typedef int callback_ret_t;
    static const callback_ret_t RECV_MORE = 1;
    static const callback_ret_t RECV_OVER  = 2;
    typedef int parse_mode_t;
    static const parse_mode_t LINE_MODE = 1;
    static const parse_mode_t BIN_MODE = 2;
    static const parse_mode_t POST_BIN_MODE = 3;
    class Command {
    public:
      typedef enum operation{
        RANDOM,
        KEYEDONE,
        KEYED,
      }op_t;
      const op_t op;
      size_t nrcv;
      long timeout; 
      parse_mode_t parse_mode;
      rmc_ret_t roma_ret;
      Command(op_t op,size_t nrcv,long timeout);
      virtual const op_t get_op()const;
      virtual const char * get_key()const = 0;
      virtual string_vbuffer & send_callback() = 0;
      virtual callback_ret_t recv(string_vbuffer &rbuf);
      virtual callback_ret_t recv_callback_line(char *line) = 0;
      virtual callback_ret_t recv_callback_bin(string_vbuffer &rbuf) = 0;
    };

    class CmdMklHash: public Command {
      static string_vbuffer sbuf;
    public:
      char * mklhash;
    public:
      CmdMklHash(long timeout);
      virtual const char * get_key()const;
      virtual string_vbuffer & send_callback();
      virtual callback_ret_t recv_callback_line(char *line);
      virtual callback_ret_t recv_callback_bin(string_vbuffer &rbuf);
    };
    class CmdRoutingDump: public Command {
      static string_vbuffer sbuf;
    public:
      std::map<char*,char*> cap;
      std::vector<const char*>    nl;
      std::map<char*,std::vector<char*> > ht;
    public:
      CmdRoutingDump(long timeout);
      virtual const char * get_key()const;
      virtual string_vbuffer & send_callback();
      virtual callback_ret_t recv_callback_line(char *line);
      virtual callback_ret_t recv_callback_bin(string_vbuffer &rbuf);
    };

    class CmdKeyed: public Command {
    protected:
      const char * key;
    public:
      CmdKeyed(size_t nrcv,long timeout,const char *key);
      virtual const char * get_key()const;
    };
    class CmdKeyedOne: public Command {
    protected:
      const char * key;
    public:
      CmdKeyedOne(size_t nrcv,long timeout,const char *key);
      virtual const char * get_key()const;
    };

    class CmdSet: public CmdKeyedOne {
      string_vbuffer sbuf;
    public:
      CmdSet(const char * key,int flags, long exp, const char *data, long length,long timeout);
      virtual string_vbuffer & send_callback();
      virtual callback_ret_t recv_callback_line(char *line);
      virtual callback_ret_t recv_callback_bin(string_vbuffer &rbuf);
    };

    class CmdBaseGet: public CmdKeyed {
    public:
      CmdBaseGet(size_t nrcv,long timeout,const char * key);
      RomaValue parse_value_line(char * line);
    };

    class CmdGet: public CmdBaseGet {
      string_vbuffer sbuf;
    public:
      RomaValue value;
      CmdGet(const char * key,long timeout);
      virtual string_vbuffer & send_callback();
      virtual callback_ret_t recv_callback_line(char *line);
      virtual callback_ret_t recv_callback_bin(string_vbuffer &rbuf);
    };

    class CmdAlistSizedInsert: public CmdKeyedOne {
      string_vbuffer sbuf;
    public:
      CmdAlistSizedInsert(const char * key,long size,const char *data, long length,long timeout);
      virtual string_vbuffer & send_callback();
      virtual callback_ret_t recv_callback_line(char *line);
      virtual callback_ret_t recv_callback_bin(string_vbuffer &rbuf);
    };

    class CmdAlistJoin: public CmdBaseGet {
      string_vbuffer sbuf;
    public:
      RomaValue value;
      int count;
      CmdAlistJoin(const char * key,const char *sep,long timeout);
      virtual string_vbuffer & send_callback();
      virtual callback_ret_t recv_callback_line(char *line);
      virtual callback_ret_t recv_callback_bin(string_vbuffer &rbuf);
    };

  }
}

#endif
