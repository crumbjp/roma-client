#include "rakuten/rmcc/rmcc_connection.h"

#include "rakuten/rmcc/rmcc_command.h"
#include "rakuten/exception.h"

#include <stdio.h>
#include <unistd.h>
#include <stdlib.h>

#include <sys/socket.h>
#include <netdb.h>
//#include <arpa/inet.h>
#include <sys/ioctl.h>
#include <sys/types.h>
#include <sys/time.h>
#include <arpa/inet.h>
#include <errno.h>

#include <sstream>
#include <iostream>

#include <typeinfo>
#include <cxxabi.h>

#include <openssl/sha.h>

namespace rakuten {
  namespace rmcc {

    unsigned long sum_timeval(struct timeval &start ,struct timeval &end){
      long msec = (end.tv_sec - start.tv_sec) * 1000;
      long usec = end.tv_usec - start.tv_usec;
      return  msec + usec/1000;
    }

    Node::Node(const char * node_info="")
      :node_info(node_info),sock(INVALID_FD)
    {
      size_t s = this->node_info.find_first_of('_');
      this->host = this->node_info.substr(0,s);
      this->port = strtoul(this->node_info.substr(s+1).c_str(),0,0);
    }
    // @TEST For the compile. (The STL-container requires no-argument constructor.)
    Node::Node()
      : host(""),port(0),sock(INVALID_FD){}

    Node::~Node() {
    }

    void Node::init() {
      WARN_LOG("Initialize [%s]",this->node_info.c_str());
      struct hostent hostent_ret;
      char hostent_buffer[BUFSIZ];
      int hostent_errno;
      struct hostent *host;
      gethostbyname_r(this->host.c_str() , &hostent_ret, hostent_buffer, sizeof(hostent_buffer), &host , &hostent_errno);
      if( host == NULL){
        char work[BUFSIZ];
        char * msg = strerror_r(hostent_errno, work, sizeof(work));
        Exception::throw_exception(0, EXP_PRE_MSG,"gethostbyname_r() failed. [%s]", msg );
      }
      memset(&this->addr,0,sizeof(sockaddr_in));
      this->addr.sin_family 	= PF_INET;
      this->addr.sin_addr	= *(in_addr*)host->h_addr;
      this->addr.sin_port	= htons(this->port);
    }

    bool Node::is_connect() {
      if ( this->sock != INVALID_FD ) {
        if ( ! this->wait_recv(0) ) {
          return true;
        }
        // Disconnect or remain data exists!
        //  Should close and reconnect.
        // @TEST The case of unstabilized connection.
        this->disconnect();
      }
      return false;
    }
    void Node::connect(){
      if ( ! this->is_connect() ) {
        WARN_LOG("Connect to [%s]",this->node_info.c_str());
        this->sock = ::socket(PF_INET,SOCK_STREAM,0);
        if(this->sock == INVALID_FD){
          // @TEST It's very rare case. Maybe it'll be caused by the discriptor-limits.
          Exception::throw_exception(0, EXP_PRE_MSG,"socket() returned -1 ");
        }
        int ret = ::connect(this->sock,(struct sockaddr *)&this->addr,sizeof(sockaddr));
        if(ret == -1){
          char work[BUFSIZ];
          char * msg = ::strerror_r(errno , work , sizeof(work));
          this->disconnect();
          Exception::throw_exception(0, EXP_PRE_MSG,"::connect failed ! : %s",msg);
        }
      }
    }
    void Node::disconnect(){
      WARN_LOG("Disconnect from [%s]",this->node_info.c_str());
      ::shutdown(this->sock,SHUT_RDWR);
      ::close(this->sock);
      this->sock = INVALID_FD;
    }
    void Node::send(const char *data,long len){
      this->connect();
      ssize_t ssize = ::send(this->sock,data,len,MSG_NOSIGNAL);
      if(ssize < 0 ){
        // @TEST Maybe it'll be caused by the problem of the connection. (or fatal bugs !!)
        char   work[BUFSIZ];
        char * msg = ::strerror_r(errno , work , sizeof(work));
        Exception::throw_exception(0, EXP_PRE_MSG,"send error ! send(len:%d) : %s ",len,msg);
        this->disconnect();
      }
      if(ssize != len){
        // @TEST Maybe it'll never causes in blocking socket mode. (or fatal bugs !!)
        this->disconnect();
        Exception::throw_exception(0, EXP_PRE_MSG,"send error (remain) ! send(len:%d) : %d ",len,ssize);
      }
    }      

    bool Node::wait_recv(long timeout){
      struct timeval tv;
      tv.tv_sec = timeout/1000;
      tv.tv_usec= (timeout%1000)*1000;
      fd_set rfd;
      FD_ZERO(&rfd);
      FD_SET(sock,&rfd);
      int n = ::select(FD_SETSIZE,&rfd,NULL,NULL,&tv);
      if(n == -1){
        // @TEST Maybe it'll never causes. (or fatal bugs !!)
        char work[BUFSIZ];
        char * msg = ::strerror_r(errno , work , sizeof(work));
        WARN_LOG("select() error. [%s] : %s",this->node_info.c_str(),msg);
        return false;
      }else if(n == 0){
        return false;
      }
      return true;
    }
    void Node::recv(string_vbuffer &buf,long num, long timeout) {
      if ( ! wait_recv(timeout) ) {
        this->disconnect();
        Exception::throw_exception(0, EXP_PRE_MSG,"Command timeout [%s]",this->node_info.c_str() );
      }
      // recv
      int offset = buf.length();
      buf.relength(offset+num+1); // recv buffer
      ssize_t rsize = ::recv(this->sock,(buf.pointer() + offset),num,0);
      *(buf.pointer()+offset+rsize) = 0; 
      if(rsize == -1){
        // @TEST Maybe it'll never causes. (or fatal bugs !!)
        char work[BUFSIZ];
        char * msg = ::strerror_r(errno , work , sizeof(work));
        this->disconnect();
        Exception::throw_exception(0, EXP_PRE_MSG,"recv() error [%s] : ",this->node_info.c_str(),msg );
      }
      if(rsize == 0){ 	// refused
        this->disconnect();
        Exception::throw_exception(0, EXP_PRE_MSG,"recv() refused [%s]",this->node_info.c_str() );
      }
      buf.relength(offset+rsize); // fit
    }
    //RomaConnection::RomaConnection() : seed(345678) {
    RomaConnection::RomaConnection() : seed(time(0)) {
      memset(mklhash,0,sizeof(mklhash));
      memset(&tv_last_check,0,sizeof(tv_last_check));
    }
    size_t RomaConnection::num_valid() const {
      return this->nodelist.size();
    }
    void RomaConnection::init(const node_info_list_t &nl,routing_mode_t routing_mode,unsigned long check_threshold){
      // save list
      {
        this->inited_list_buf.relength(0);
        this->inited_list.clear();
        for ( node_info_list_t::const_iterator it(nl.begin()),itend(nl.end());
              it != itend;
              it++ ){
          size_t s = strlen(*it)+1;
          this->inited_list_buf.append((*it),s);
          char * p = this->inited_list_buf.get(s);
          TRACE_LOG("Specialized node : %s",p);
          this->inited_list.push_back(p);
        }
      }
      this->routing_mode = routing_mode;
      this->check_threshold = check_threshold;

      if ( ! this->num_valid() ) {
        INFO_LOG("Init nodes (%d)",routing_mode);
        prepare_nodes(this->inited_list);
        routing_table();
      }
    }
    void RomaConnection::prepare_nodes(const node_info_list_t &info_list){
      for ( node_info_list_t::const_iterator it(info_list.begin()),itend(info_list.end());
            it != itend;
            it++ ) {
        this->prepare_node(*it);
      }
    }
    Node * RomaConnection::prepare_node(const char * node_info) {
      node_list_t::iterator it = this->nodelist.find(node_info);
      if ( it == this->nodelist.end() ) {
        try {
          Node node(node_info);
          node.init();
          node.connect();
          this->nodelist.insert(node_list_t::value_type(node_info,node));
          it = this->nodelist.find(node_info);
        }catch(const Exception & ex){
          ERR_LOG("Cannot connect [%s]",node_info);
          return 0;
        }
      }else{
        try {
          it->second.connect();
        }catch(const Exception & ex){
          // @TEST The case of unstabilized connection.
          ERR_LOG("Cannot connect [%s]",node_info);
          return 0;
        }
      }
      return &it->second;
    }

    static const long MKLHASH_TIMEOUT = 2000;
    static const long ROUTINGDUMP_TIMEOUT = 5000;
    bool RomaConnection::routing_table(bool force){
      if (this->routing_mode == ROUTING_MODE_USE ) {
        timeval tv_now;
        gettimeofday(&tv_now,0);
        unsigned long diff_check = sum_timeval(this->tv_last_check,tv_now);
        TRACE_LOG("The %lu msec passed from the last mklhash : %lu",diff_check,check_threshold);
        if ( force || diff_check > this->check_threshold ) {
          INFO_LOG("Try to get new mklhash (msec:%ld)",diff_check);
          Node & node = *this->get_node_random();
          try {
            CmdMklHash cmdmklhash(MKLHASH_TIMEOUT);
            cmdmklhash.prepare();
            this->command(cmdmklhash,node);
            if ( memcmp(this->mklhash,cmdmklhash.mklhash,sizeof(mklhash)-1) ) {
              char tmphash[41];
              memcpy(tmphash,cmdmklhash.mklhash,sizeof(mklhash));
              tmphash[40] = 0;
              WARN_LOG("New routing dump [%s] : mklhash(old)=%s : mklhash=%s",node.node_info.c_str(),this->mklhash,cmdmklhash.mklhash);
              // Init datas
              this->dgst_bits = 0;
              this->div_bits  = 0;
              this->rn        = 0;
              this->routing.clear();
              CmdRoutingDump cmdroutingdump(ROUTINGDUMP_TIMEOUT);
              cmdroutingdump.prepare();
              this->command(cmdroutingdump,node);
              for ( std::map<char*,char*>::iterator it(cmdroutingdump.cap.begin()),itend(cmdroutingdump.cap.end());
                    it!=itend;
                    it++){
                if ( 0==strcmp(it->first,"dgst_bits") ) {
                  this->dgst_bits = strtoul(it->second,0,0);
                } else if ( 0==strcmp(it->first,"div_bits") ) {
                  this->div_bits = strtoul(it->second,0,0);
                } else if ( 0==strcmp(it->first,"rn") ) {
                  this->rn = strtoul(it->second,0,0);
                }
              }
              for ( std::map<char*,std::vector<char*> >::iterator it(cmdroutingdump.ht.begin()),itend(cmdroutingdump.ht.end());
                    it!=itend;
                    it++){
                hash_t hash = strtoul(it->first,0,0);
                for (std::vector<char*>::iterator vit(it->second.begin()),vitend(it->second.end());
                     vit!=vitend;
                     vit++){
                  routing[hash].push_back(*vit);
                }
              }
              this->prepare_nodes(cmdroutingdump.nl);
              memcpy(this->mklhash,tmphash,sizeof(mklhash));
              this->mklhash[40] = 0;
              gettimeofday(&tv_last_check,0);
              return true;
            }
            gettimeofday(&tv_last_check,0);
          }catch(const Exception & ex ) {
            // @TEST The case of unstabilized connection.
            ERR_LOG("New RoutingTable error [%s]",node.node_info.c_str());
            return routing_table();
          }
        }
      }
      return false;
    }

    Node * RomaConnection::get_node_random(){
      int num_node = this->nodelist.size();
      if ( ! num_node ) {
        Exception::throw_exception(0, EXP_PRE_MSG,"No more valid node !" );
      }
      int index = (1+rand_r(&this->seed))*(static_cast<double>(num_node)/RAND_MAX);
      node_list_t::iterator it = nodelist.begin();
      for( int i= 0;i<index;i++){
        // @TEST For the torture test
        it++;
      }
      try {
        it->second.connect();
        return &it->second;
      }catch(const Exception & ex ) {
        // @TEST The case of unstabilized connection.
        ERR_LOG("Node check error [%s] : %s",it->second.node_info.c_str(),ex.get_msg());
        this->nodelist.erase(it);
        return this->get_node_random();
      }
    }

    hash_t RomaConnection::calc_hash(const char * t , long l ) {
      unsigned char buf[SHA_DIGEST_LENGTH];
      SHA1(reinterpret_cast<const unsigned char*>(t),l,buf);
      hash_t ret;
      memcpy(&ret,buf+SHA_DIGEST_LENGTH-sizeof(hash_t),sizeof(hash_t));
      ret = hash_bswap(ret);
      //ret = ntohl(ret);
      ret = ret << (sizeof(hash_t)*8 - this->dgst_bits);
      ret = ret >> (sizeof(hash_t)*8 - this->div_bits);
      ret = ret << (this->dgst_bits  - this->div_bits);
      // fprintf(stderr,"%s:%016llx\n",t,ret);
      return ret;
    }

    std::vector<std::string> &  RomaConnection::get_node_key(const char * key){
      int num_node = this->nodelist.size();
      if ( num_node ) {
        this->routing_table();
      }
      hash_t hash = calc_hash(key,strlen(key));
      TRACE_LOG("HASH:%" HASH_FMT "u KEY:%s",hash,key);
      routing_t::iterator it = this->routing.find(hash);
      if ( it != this->routing.end() ) {
        return it->second;
      }
      // @TEST The case of the Roma's bug.
      Exception::throw_exception(0, EXP_PRE_MSG,"Routing table broken !!!" );
    }

    void RomaConnection::command(Command & cmd){
      Node *node = 0;
      if ( this->routing_mode != ROUTING_MODE_USE ){
        if ( this->num_valid() < this->inited_list.size() ) {
          timeval tv_now;
          gettimeofday(&tv_now,0);
          unsigned long diff_check = sum_timeval(this->tv_last_check,tv_now);
          if (  diff_check > this->check_threshold ) {
            INFO_LOG("Try to repair connection (msec:%ld)",diff_check);
            prepare_nodes(this->inited_list);
            gettimeofday(&tv_last_check,0);
          }
        }
        node = this->get_node_random();
      } else if ( cmd.get_op() == Command::RANDOM ){
        node = this->get_node_random();
      } else if( cmd.get_op() == Command::KEYED ){
        std::vector<std::string> & nodes = this->get_node_key(cmd.get_key());
        for ( std::vector<std::string>::iterator it(nodes.begin()),itend(nodes.end());
              it != itend;
              it++){
          node = prepare_node((*it).c_str());
          if ( node ) 
            break;
        }
      } else if( cmd.get_op() == Command::KEYEDONE ){
        std::vector<std::string> & nodes = this->get_node_key(cmd.get_key());
        std::vector<std::string>::iterator it = nodes.begin();
        if ( it != nodes.end() ) {
          node = prepare_node((*it).c_str());
          if ( ! node ) {
            // @TEST The case of unstabilized connection.
            WARN_LOG("Primary node down ! So try to get new routing %s",typeid(cmd).name());
            if ( routing_table(true) ) {
              return this->command(cmd);
            }
            Exception::throw_exception(0, EXP_PRE_MSG,"Command failure(No-P-nodes) %s",typeid(cmd).name());
          }
        }
      }
      // char cmdname[BUFSIZ];
      // size_t cmdname_len = sizeof(cmdname);
      // int status;
      // __cxxabiv1::__cxa_demangle(typeid(cmd).name(), cmdname,&cmdname_len,&status);
      // INFO_LOG("Command to [%s] %s",node.node_info.c_str(),cmdname);
      if ( node ) {
        INFO_LOG("Command to [%s] %s",node->node_info.c_str(),typeid(cmd).name());
        this->command(cmd,*node);
      }else {
        // @TEST The case of unstabilized connection.
        Exception::throw_exception(0, EXP_PRE_MSG,"Command failure(No-nodes) %s",typeid(cmd).name());
      }
    }
    void RomaConnection::command(Command & cmd,Node & node){
      try { 
        //
        long &timeout = cmd.timeout;
        struct timeval tv_start,tv_now;
        gettimeofday(&tv_start,0);
        // send
        string_vbuffer & b = cmd.send_callback();
        node.send(b.pointer(),b.length());
        // recv
        rbuf.relength(0);
        for (;;) {
          // calc timeout
          gettimeofday(&tv_now,0);
          timeout -= sum_timeval(tv_start,tv_now);
          tv_start = tv_now;
          if ( timeout < 0 ) { 
            // @TEST Hashed recv(). But very rare. It'll always be detected as recv() timeout.
            Exception::throw_exception(0, EXP_PRE_MSG,"Command timeout ." );
          }
          node.recv(rbuf,cmd.nrcv,timeout);
          if ( cmd.recv(rbuf) == RECV_OVER ) {
            break;
          }
        }
      }catch( const Exception & ex ) {
        ERR_LOG("Command failure from [%s] : %s",node.node_info.c_str(),ex.get_msg());
        node.disconnect();
        this->nodelist.erase(node.node_info);
        throw;
      }
    }
    void RomaConnection::term(){
    }

    RomaConnection::~RomaConnection(){
    }
  }
}
