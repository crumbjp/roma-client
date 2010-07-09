/*
 * roma_connection.h - ????
 *
 *   Copyright (C) 2010 rakuten 
 *     by hiroaki.kubota <hiroaki.kubota@rakuten.co.jp> 
 *     Date : 2010/06/11
 */
#ifndef RMCC_CONNECTION_H
#define RMCC_CONNECTION_H

#include "rakuten/rmcc/rmcc_common.h"
#include "rakuten/rmcc/rmcc_command.h"

#include "rakuten/vbuffer.h"
#include <netinet/in.h>

#include <vector>
#include <map>
#include <sys/time.h>

namespace rakuten {
  namespace rmcc {
    static const int INVALID_FD = -1;
    typedef uint32_t hash_t;

    long sum_timeval(struct timeval *start ,struct timeval *end);

    class Node {
    public:
      std::string node_info;
      std::string host;
      int         port;
      struct sockaddr_in addr;
      bool isconnect;
      int sock;
      Node(const char * node_info);
      Node();
      ~Node();
      std::string to_string()const;
      void init();
      bool is_connect();
      void connect();
      void send(const char *data,long len);
      void recv(string_vbuffer &buf,long num, long timeout );
      void disconnect();
    protected:
      bool wait_recv(long timeout);
    };

    class RomaConnection {
      typedef std::map<std::string,Node> node_list_t;
      node_list_t nodelist;
      typedef std::map<hash_t,std::vector<std::string> > routing_t;
      routing_t routing;
      string_vbuffer rbuf;
      unsigned int seed;
      char mklhash[41];
      timeval tv_last_mklhash;
      unsigned long mklhash_threshold;
      int dgst_bits;
      int div_bits;
      int rn;
      routing_mode_t routing_mode;
    public:
      typedef std::vector<const char*> node_info_list_t;
      RomaConnection();
      void init(const node_info_list_t &info_list,routing_mode_t routing_mode);
      int  num_valid()const;
      void command(Command & cmd);
      void term();
      ~RomaConnection();
    protected:
      void prepare_nodes(const node_info_list_t &info_list);
      Node * prepare_node(const char * node_info);
      Node * get_node_random();
      std::vector<std::string> & get_node_key(const char * key);
      hash_t calc_hash(const char * t , long l );
      void routing_table();
      void command(Command & cmd,Node &node);
    };
  }
}
#endif
