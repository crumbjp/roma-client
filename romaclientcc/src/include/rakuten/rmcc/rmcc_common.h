/*
 * roma_common.h - ????
 *
 *   Copyright (C) 2010 rakuten 
 *     by hiroaki.kubota <hiroaki.kubota@rakuten.co.jp> 
 *     Date : 2010/06/16
 */
#ifndef RMCC_COMMON_H
#define RMCC_COMMON_H


namespace rakuten {
  namespace rmcc {
    typedef int rmc_ret_t;
    static const rmc_ret_t RMC_RET_OK = 0;
    static const rmc_ret_t RMC_RET_ERROR = 1;
    static const rmc_ret_t RMC_RET_FALSE = 2;
    
    typedef int routing_mode_t;
    static const routing_mode_t  ROUTING_MODE_USE = 1; 
    typedef int cas_t;
    class RomaValue {
    public:
      const char * data;
      long   length;
      cas_t  cas;
      RomaValue(const char * data =0,long length=-1,cas_t cas=-1);
    };
  }
}

#endif
