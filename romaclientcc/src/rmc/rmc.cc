#include "rakuten/rmc/rmc.h"
#include "rakuten/rmcc/rmcc.h"

rmc_t * get_rmc(const char * id){
  return 0;
}
char * rmc_geterr(rmc_t * rmc){
  return 0;
}
rmc_return rmc_add_host(rmc_t * rmc,const char *hostname, const int & port){
  return RMC_ERROR;
}
rmc_return rmc_init(rmc_t * rmc){
  return RMC_ERROR;
}
rmc_return rmc_term(rmc_t * rmc){
  return RMC_ERROR;
}
rmc_return rmc_set(rmc_t * rmc, const char *key, const rmc_value_info & valinfo, const long exptime){
  return RMC_ERROR;
}
rmc_return rmc_add(rmc_t * rmc, const char *key, const rmc_value_info & valinfo, const long exptime){
  return RMC_ERROR;
}
rmc_return rmc_replace(rmc_t * rmc, const char *key, const rmc_value_info & valinfo, const long exptime){
  return RMC_ERROR;
}
rmc_return rmc_append(rmc_t * rmc, const char *key, const rmc_value_info & valinfo, const long exptime){
  return RMC_ERROR;
}
rmc_return rmc_prepend(rmc_t * rmc, const char *key, const rmc_value_info & valinfo, const long exptime){
  return RMC_ERROR;
}
rmc_return rmc_cas(rmc_t * rmc, const char *key, const rmc_value_info & valinfo, const long exptime){
  return RMC_ERROR;
}
rmc_return rmc_delete(rmc_t * rmc, const char *key){
  return RMC_ERROR;
}
rmc_return rmc_get(rmc_t * rmc, rmc_value_info & valinfo,const char *key){
  return RMC_ERROR;
}
rmc_return rmc_gets(rmc_t * rmc, rmc_value_info & valinfo,const char *key){
  return RMC_ERROR;
}
rmc_return rmc_getbulk_addkey(rmc_t * rmc, const char * key){
  return RMC_ERROR;
}
rmc_return rmc_getbulk_exec(rmc_t * rmc, rmc_value_list & vallist){
  return RMC_ERROR;
}
// ALIST
rmc_return rmc_alist_at(rmc_t * rmc, rmc_value_info & valinfo,const char *key, const int index){
  return RMC_ERROR;
}
rmc_return rmc_alist_clear(rmc_t * rmc, const char *key){
  return RMC_ERROR;
}
rmc_return rmc_alist_delete(rmc_t * rmc, const char *key, const rmc_value_info & valinfo){
  return RMC_ERROR;
}
rmc_return rmc_alist_delete_at(rmc_t * rmc, const char *key, const int index){
  return RMC_ERROR;
}
rmc_return rmc_alist_index(rmc_t * rmc, const char *key, const rmc_value_info & valinfo){
  return RMC_ERROR;
}
rmc_return rmc_alist_insert(rmc_t * rmc, const char *key, const int index, const rmc_value_info & valinfo){
  return RMC_ERROR;
}
rmc_return rmc_alist_sized_insert(rmc_t * rmc, const char *key, const int size, const rmc_value_info & valinfo){
  return RMC_ERROR;
}
rmc_return rmc_alist_join(rmc_t * rmc, rmc_value_info & valinfo,const char *key, const char *separator){
  return RMC_ERROR;
}
rmc_return rmc_alist_length(rmc_t * rmc, const char *key){
  return RMC_ERROR;
}
rmc_return rmc_alist_pop(rmc_t * rmc, rmc_value_info & valinfo,const char *key){
  return RMC_ERROR;
}
rmc_return rmc_alist_push(rmc_t * rmc, const char *key, const rmc_value_info & valinfo){
  return RMC_ERROR;
}
rmc_return rmc_alist_shift(rmc_t * rmc, rmc_value_info & valinfo,const char *key){
  return RMC_ERROR;
}
rmc_return rmc_alist_gets(rmc_t * rmc, rmc_value_list & valinfo,const char *key){
  return RMC_ERROR;
}
