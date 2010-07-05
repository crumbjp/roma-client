#include "rakuten/rmcc/rmcc.h"

#include "rakuten/exception.h"

namespace rakuten{
  namespace rmcc {

    RomaValue::RomaValue(const char * data,long length,cas_t  cas)
      : data(data),length(length),cas(cas){
    }

    RomaConnection::node_info_list_t & RomaClient::get_nodelist(){
      return this->node_info_list;
    }
    const char * RomaClient::get_lasterror()const {
      return this->lasterr.str().c_str();
    }
    void RomaClient::init(routing_mode_t routing_mode){
      try {
        this->conn.init(this->node_info_list,routing_mode);
      }catch(const Exception & ex ) {
        this->lasterr << ex.get_func() << ":" << ex.get_line() << ":" << ex.get_msg();
        throw ex;
      }
    }
    void RomaClient::term(){
      try {
        this->conn.term();
      }catch(const Exception & ex ) {
        this->lasterr << ex.get_func() << ":" << ex.get_line() << ":" << ex.get_msg();
        throw ex;
      }
    }
    rmc_ret_t RomaClient::cmd_store(const char *key, RomaValue value, long exptime){
      try {
        CmdSet cmd(key,0,exptime,value.data,value.length);
        conn.command(cmd);
        return cmd.roma_ret;
      }catch(const Exception & ex ) {
        this->lasterr << ex.get_func() << ":" << ex.get_line() << ":" << ex.get_msg();
        throw ex;
      }
    }
    RomaValue RomaClient::cmd_get(const char *key){
      try {
        CmdGet cmd(key);
        this->conn.command(cmd);
        return cmd.value;
      }catch(const Exception & ex ) {
        this->lasterr << ex.get_func() << ":" << ex.get_line() << ":" << ex.get_msg();
        throw ex;
      }
    }
    rmc_ret_t RomaClient::cmd_alist_sized_insert(const char *key, long size, RomaValue value){
      try {
        CmdAlistSizedInsert cmd(key,size,value.data,value.length);
        conn.command(cmd);
        return cmd.roma_ret;
      }catch(const Exception & ex ) {
        this->lasterr << ex.get_func() << ":" << ex.get_line() << ":" << ex.get_msg();
        throw ex;
      }
    }
    RomaValue RomaClient::cmd_alist_join(const char *key,const char * sep){
      try {
        CmdAlistJoin cmd(key,",");
        this->conn.command(cmd);
        return cmd.value;
      }catch(const Exception & ex ) {
        this->lasterr << ex.get_func() << ":" << ex.get_line() << ":" << ex.get_msg();
        throw ex;
      }
    }
  }
}
