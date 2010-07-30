#include "rakuten/rmcc/rmcc_command.h"
#include "rakuten/exception.h"

#include <stdlib.h>

namespace rakuten {
  namespace rmcc {

    #ifdef SMALL_BUF_TEST
    static const size_t NRCVDEF  = 5;
    static const size_t NRCVMKLH = 5;
    #else
    static const size_t NRCVDEF  = 32768;
    static const size_t NRCVMKLH = 42;
    #endif
    Command::Command(op_t op,size_t nrcv,long timeout)
      :op(op),nrcv(nrcv),timeout(timeout),parse_mode(LINE_MODE),roma_ret(RMC_RET_ERROR)
    {
    }
    const Command::op_t Command::get_op()const { return op; }
    callback_ret_t Command::recv(string_vbuffer &rbuf) {
      for (;;){
        if ( this->parse_mode == LINE_MODE ) {
          char * line = rbuf.get_to_token("\r\n");
          if ( ! line ) {
            return RECV_MORE;
          }
          if (strcmp("END",line) == 0 ) {
            return RECV_OVER;
          }
          if ( this->recv_callback_line(line) == RECV_OVER ) {
            return RECV_OVER;
          }
        } else if ( this->parse_mode == BIN_MODE ) {
          if ( this->recv_callback_bin(rbuf) == RECV_OVER ) {
            return RECV_OVER;
          }
          if ( this->parse_mode == BIN_MODE ) {
            return RECV_MORE;
          }
        } else if ( this->parse_mode == POST_BIN_MODE ) {
          if ( rbuf.length() >= CRLF_SIZE ) {
            if ( *rbuf.pointer() =='\r' && *(rbuf.pointer()+1) =='\n' ){
              rbuf.get_to_token("\r\n");
              this->parse_mode = LINE_MODE;
            }else {
              Exception::throw_exception(0, EXP_PRE_MSG,"Unexpected data." );
            }
          }else{
            return RECV_MORE;
          }
        } else {
          // @TEST There is no route to reach here.( or fatal bug !!)
          Exception::throw_exception(0, EXP_PRE_MSG,"Unexpected mode." ); 
        }
      }
    }

    // @TEST There is no route to reach here.( or fatal bug !!) : CmdMklHash is not keyed command.
    const char * CmdMklHash::get_key()const { return 0;}

    string_vbuffer CmdMklHash::sbuf("mklhash 0\r\n");
    CmdMklHash::CmdMklHash(long timeout)
      :Command(Command::RANDOM,NRCVMKLH,timeout),mklhash(0){
      parse_mode = BIN_MODE;
    }
    string_vbuffer & CmdMklHash::send_callback(){
      TRACE_LOG("%s",__PRETTY_FUNCTION__);
      return sbuf;
    }
    void CmdMklHash::prepare(){
    }
    callback_ret_t CmdMklHash::recv_callback_bin(string_vbuffer &rbuf){
      mklhash = rbuf.get_to_token("\r\n");
      if ( mklhash ) {
        this->roma_ret = RMC_RET_OK;
        return RECV_OVER;
      }
      return RECV_MORE;
    }
    // @TEST There is no route to reach here.( or fatal bug !!) : CmdMklHash never becomes LINE_MODE.
    callback_ret_t CmdMklHash::recv_callback_line(char *line) {return RECV_OVER;}

    // @TEST There is no route to reach here.( or fatal bug !!) : CmdRoutingDump is not keyed command.
    const char * CmdRoutingDump::get_key()const {return 0;}
    string_vbuffer CmdRoutingDump::sbuf("routingdump json\r\n");
    CmdRoutingDump::CmdRoutingDump(long timeout)
      :Command(Command::RANDOM,NRCVDEF,timeout)
    {
      parse_mode = BIN_MODE;
    }
    void CmdRoutingDump::prepare(){
    }
    string_vbuffer & CmdRoutingDump::send_callback(){
      TRACE_LOG("%s",__PRETTY_FUNCTION__);
      return sbuf;
    }


    inline char* trimq(char * p){
      if ( *p == '"' ){
        char *s1;
        return strtok_r(p,"\"",&s1);
      }
      return p;
    }

    template <typename callback_t>
    struct hash_parse {
      typedef std::map<char*,typename callback_t::value_t> hash_table_t;
      void operator()(char * p , hash_table_t & map , callback_t callback ) const {
        char *s1;
        for (char *k=strtok_r(p,":",&s1); k ;k=strtok_r(0,":",&s1)){
          k = trimq(k);
          typename callback_t::value_t v = callback(s1);
          map.insert(typename hash_table_t::value_type(k,v));
        }
      }
    };
    struct value_callback{
      typedef char* value_t;
      value_t operator()(char * &p)const {
        return trimq(strtok_r(0,",",&p));
      }
    };
    struct array_parse {
      typedef std::vector<char*> array_t; 
      void operator()(char * p,array_t & array ) const {
        char *s1;
        for (char *r = strtok_r(p,",",&s1); r ;r=strtok_r(0,",",&s1)){
          r = trimq(r);
          array.push_back(r);
        }
      }
    };
    struct array_callback{
      typedef array_parse::array_t value_t;
      value_t operator()(char * &p)const {
        if ( *p == '[' )
          p++;
        else
          strtok_r(0,"[",&p);  // @TEST There is no padding in the string what The Roma returns.
        char *arr = strtok_r(0,"]",&p);
        array_parse::array_t array;
        array_parse()(arr,array);
        if ( *p == ',' )
          p++;
        else
          strtok_r(0,",",&p);
        return array;  // @TEST I don't have a good reason about why this line is never run. But I'm certain that it's a magic of the inline-expansion of the template.
      }
    };

    callback_ret_t CmdRoutingDump::recv_callback_bin(string_vbuffer &rbuf){
      struct ccount {
        std::pair<int,int> operator()(const char * d , long l,char c ) const {
          int last  = 0;
          int count = 0;
          for ( int i = 0 ; i < l ; i++ )
            if ( *(d+i) == c ) {
              count++;
              last = i;
            }
          return std::pair<int,int>(count,last);
        }
      };
      std::pair<int,int> bp = ccount()(rbuf.pointer(),rbuf.length(),'[');
      std::pair<int,int> ep = ccount()(rbuf.pointer(),rbuf.length(),']');
      if ( bp.first > ep.first ) {
        return RECV_MORE;
      }
      //------------
      // Parse

      { // ALL
        rbuf.get_to_token("[");
        { // CAPABILITY
          rbuf.get_to_token("{");
          char * capability = rbuf.get_to_token("}");
          hash_parse<value_callback>()(capability,this->cap,value_callback());
        }
        rbuf.get_to_token(",");
        { // NODES
          rbuf.get_to_token("[");
          char * nodes = rbuf.get_to_token("]");
          array_parse::array_t array;
          array_parse()(nodes,array);
          for ( array_parse::array_t::iterator it(array.begin()),itend(array.end());
                it != itend;
                it++){
            this->nl.push_back(*it);
          }
        }
        rbuf.get_to_token(",");
        { // HASH
          rbuf.get_to_token("{");
          char * hash = rbuf.get_to_token("}");
          hash_parse<array_callback>()(hash,this->ht,array_callback());
        }
        rbuf.get_to_token("]");
      }

      this->roma_ret = RMC_RET_OK;
      this->parse_mode = POST_BIN_MODE;
      return RECV_MORE;
    }
    // @TEST There is no route to reach here.( or fatal bug !!) : CmdRoutingDump never becomes LINE_MODE.
    callback_ret_t CmdRoutingDump::recv_callback_line(char * line) {return RECV_OVER;}

    CmdKeyed::CmdKeyed(size_t nrcv,long timeout,const char * key)
      :Command(Command::KEYED,nrcv,timeout), key(key) {
    }
    void CmdKeyed::prepare(){
      size_t l = strlen(key);
      const char * p = strpbrk(key,"\r\n\e ");
      if( ! l ) {
	Exception::throw_exception(0, EXP_PRE_MSG,"Key is empty !");
      } else if ( p ) {
	Exception::throw_exception(0, EXP_PRE_MSG,"Key is invalid ! Included(0x%08x)",p[0]);
      }
    }
    const char * CmdKeyed::get_key()const {
      return this->key;
    }
    CmdKeyedOne::CmdKeyedOne(size_t nrcv,long timeout,const char * key)
      :Command(Command::KEYEDONE,nrcv,timeout), key(key) {
    }
    void CmdKeyedOne::prepare(){
      size_t l = strlen(key);
      const char * p = strpbrk(key,"\r\n\e ");
      if( ! l ) {
	Exception::throw_exception(0, EXP_PRE_MSG,"Key is empty !");
      } else if ( p ) {
	Exception::throw_exception(0, EXP_PRE_MSG,"Key is invalid ! Included(0x%08x)",p[0]);
      }
    }
    const char * CmdKeyedOne::get_key()const {
      return this->key;
    }

    CmdSet::CmdSet(const char * key,int flags, long exp, const char *data, long length,long timeout)
      :CmdKeyedOne(NRCVDEF,timeout,key),
       flags(flags),exp(exp),data(data),length(length)
    {
    }
    void CmdSet::prepare(){
      this->CmdKeyedOne::prepare();
      sbuf.append_sprintf("set %s %d %ld %ld\r\n",key,flags,exp,length);
      sbuf.append(data,length);
      sbuf.append("\r\n",2);
    }
    string_vbuffer & CmdSet::send_callback(){
      TRACE_LOG("%s",__PRETTY_FUNCTION__);
      return sbuf;
    }
    // @TEST There is no route to reach here.( or fatal bug !!) : CmdSet never becomes BIN_MODE.
    callback_ret_t CmdSet::recv_callback_bin(string_vbuffer &rbuf){return RECV_OVER;}
    callback_ret_t CmdSet::recv_callback_line(char * line) {
      if ( strcmp("STORED",line) == 0 ) {
        this->roma_ret = RMC_RET_OK;
      }else if ( strcmp("NOT_STORED",line) == 0 ) {
      }else {
        Exception::throw_exception(0, EXP_PRE_MSG,"%s",line);
      }
      return RECV_OVER;
    }

    CmdDelete::CmdDelete(const char *key,long timeout)
      :CmdKeyedOne(NRCVDEF,timeout,key){
    }
    void CmdDelete::prepare(){
      this->CmdKeyedOne::prepare();
      sbuf.append_sprintf("delete %s\r\n",key);
    }
    string_vbuffer & CmdDelete::send_callback(){
      TRACE_LOG("%s",__PRETTY_FUNCTION__);
      return sbuf;
    }
    // @TEST There is no route to reach here.( or fatal bug !!) : CmdDelete never becomes BIN_MODE.
    callback_ret_t CmdDelete::recv_callback_bin(string_vbuffer &rbuf){return RECV_OVER;}
    callback_ret_t CmdDelete::recv_callback_line(char * line) {
      if ( strcmp("DELETED",line) == 0 ){
	this->roma_ret = RMC_RET_OK;
      }else if( strcmp("NOT_FOUND",line) == 0){
	this->roma_ret = RMC_RET_OK;
      }else if( strcmp("NOT_DELETED",line) == 0){
      }else{
	Exception::throw_exception(0, EXP_PRE_MSG,"%s",line);
      }
      return RECV_OVER;
    }
    

    CmdBaseGet::CmdBaseGet(size_t nrcv,long timeout,const char *key):CmdKeyed(nrcv,timeout,key){}
    RomaValue CmdBaseGet::parse_value_line(char * line){
      RomaValue ret;
      char *s1;
      const char * val = strtok_r(line," ",&s1);
      if ( strcmp("VALUE",val) ) {
        Exception::throw_exception(0, EXP_PRE_MSG,"Unexpected data. :%s",line );
      }
      const char * key = strtok_r(0," ",&s1);
      if ( ! key ) {
        Exception::throw_exception(0, EXP_PRE_MSG,"Unexpected data." );
      }
      const char * flg = strtok_r(0," ",&s1);
      if ( ! flg ) {
        Exception::throw_exception(0, EXP_PRE_MSG,"Unexpected data." );
      }
      const char * len = strtok_r(0," ",&s1);
      if ( ! len ) {
        Exception::throw_exception(0, EXP_PRE_MSG,"Unexpected data." );
      }
      ret.length = strtoul(len,0,0);
      const char * clk = strtok_r(0," ",&s1);
      if ( clk ) {
        // @TEST Cas command is not supported yet.
        ret.cas = strtoul(clk,0,0);
      }
      this->parse_mode = BIN_MODE;
      return ret;
    }

    CmdGet::CmdGet(const char * key,long timeout)
      :CmdBaseGet(NRCVDEF,timeout,key)
    {
    }
    void CmdGet::prepare(){
      this->CmdKeyed::prepare();
      sbuf.append_sprintf("get %s\r\n",key);
    }
    string_vbuffer & CmdGet::send_callback(){
      TRACE_LOG("%s",__PRETTY_FUNCTION__);
      return sbuf;
    }
    callback_ret_t CmdGet::recv_callback_bin(string_vbuffer &rbuf){
      this->value.data = rbuf.get(this->value.length);
      if ( this->value.data ) {
        this->roma_ret = RMC_RET_OK;
        this->parse_mode = POST_BIN_MODE;
      }
      return RECV_MORE;
    }
    callback_ret_t CmdGet::recv_callback_line(char * line) {
      this->value = this->parse_value_line(line);
      return RECV_MORE;
    }

    CmdAlistSizedInsert::CmdAlistSizedInsert(const char * key,long size,const char *data, long length,long timeout)
      :CmdKeyedOne(NRCVDEF,timeout,key),
       size(size),data(data),length(length)
    {
    }
    void CmdAlistSizedInsert::prepare(){
      this->CmdKeyedOne::prepare();
      sbuf.append_sprintf("alist_sized_insert %s %ld %ld\r\n",key,size,length);
      sbuf.append(data,length);
      sbuf.append("\r\n",2);
    }

    string_vbuffer & CmdAlistSizedInsert::send_callback(){
      TRACE_LOG("%s",__PRETTY_FUNCTION__);
      return sbuf;
    }
    // @TEST There is no route to reach here.( or fatal bug !!) : CmdAlistSizedInsert never becomes BIN_MODE.
    callback_ret_t CmdAlistSizedInsert::recv_callback_bin(string_vbuffer &rbuf){return RECV_OVER;}
    callback_ret_t CmdAlistSizedInsert::recv_callback_line(char * line) {
      if ( strcmp("STORED",line) == 0 ) {
        this->roma_ret = RMC_RET_OK;
      }else if ( strcmp("NOT_STORED",line) == 0 ) {
      }else {
        Exception::throw_exception(0, EXP_PRE_MSG,"%s",line);
      }
      return RECV_OVER;
    }

    CmdAlistJoin::CmdAlistJoin(const char * key,const char *sep,long timeout)
      :CmdBaseGet(NRCVDEF,timeout,key),
       count(0),sep(sep)
    {
    }
    void CmdAlistJoin::prepare(){
      this->CmdKeyed::prepare();
      sbuf.append_sprintf("alist_join %s %d\r\n%s\r\n",key,strlen(sep),sep);
    }
    string_vbuffer & CmdAlistJoin::send_callback(){
      TRACE_LOG("%s",__PRETTY_FUNCTION__);
      return sbuf;
    }
    callback_ret_t CmdAlistJoin::recv_callback_bin(string_vbuffer &rbuf){
      this->value.data = rbuf.get(this->value.length);
      if ( this->value.data ) {
        this->parse_mode = POST_BIN_MODE;
        if ( ! count ){
          count ++;
          return RECV_MORE;
        }
        this->roma_ret = RMC_RET_OK;
      }
      return RECV_MORE;
    }
    callback_ret_t CmdAlistJoin::recv_callback_line(char * line) {
      this->value = this->parse_value_line(line);
      return RECV_MORE;
    }

    CmdAlistDelete::CmdAlistDelete(const char * key,const char *data, long length,long timeout)
      :CmdKeyedOne(NRCVDEF,timeout,key),
       data(data),length(length)
    {
    }

    void CmdAlistDelete::prepare(){
      this->CmdKeyedOne::prepare();
      sbuf.append_sprintf("alist_delete %s %ld\r\n",key,length);
      sbuf.append(data,length);
      sbuf.append("\r\n",2);
    }

    string_vbuffer & CmdAlistDelete::send_callback(){
      TRACE_LOG("%s",__PRETTY_FUNCTION__);
      return sbuf;
    }
    // @TEST There is no route to reach here.( or fatal bug !!) : CmdAlistDelete never becomes BIN_MODE.
    callback_ret_t CmdAlistDelete::recv_callback_bin(string_vbuffer &rbuf){return RECV_OVER;}
    callback_ret_t CmdAlistDelete::recv_callback_line(char * line) {
      if ( strcmp("DELETED",line) == 0 ) {
        this->roma_ret = RMC_RET_OK;
      }else if ( strcmp("NOT_DELETED",line) == 0 ) {
      }else if ( strcmp("NOT_FOUND",line) == 0 ) {
      }else {
        Exception::throw_exception(0, EXP_PRE_MSG,"%s",line);
      }
      return RECV_OVER;
    }

    CmdAlistDeleteAt::CmdAlistDeleteAt(const char * key,int pos,long timeout)
      :CmdKeyedOne(NRCVDEF,timeout,key),
       pos(pos)
    {
    }

    void CmdAlistDeleteAt::prepare(){
      this->CmdKeyedOne::prepare();
      sbuf.append_sprintf("alist_delete_at %s %d\r\n",key,pos);
    }

    string_vbuffer & CmdAlistDeleteAt::send_callback(){
      TRACE_LOG("%s",__PRETTY_FUNCTION__);
      return sbuf;
    }
    // @TEST There is no route to reach here.( or fatal bug !!) : CmdAlistDeleteAt never becomes BIN_MODE.
    callback_ret_t CmdAlistDeleteAt::recv_callback_bin(string_vbuffer &rbuf){return RECV_OVER;}
    callback_ret_t CmdAlistDeleteAt::recv_callback_line(char * line) {
      if ( strcmp("DELETED",line) == 0 ) {
        this->roma_ret = RMC_RET_OK;
      }else if ( strcmp("NOT_DELETED",line) == 0 ) {
      }else if ( strcmp("NOT_FOUND",line) == 0 ) {
      }else {
        Exception::throw_exception(0, EXP_PRE_MSG,"%s",line);
      }
      return RECV_OVER;
    }

  }
}
