#ifndef HASH_H
#define HASH_H

#include <ext/hash_fun.h>
#include <string>
#include <inttypes.h>

namespace rakuten {
  struct string_hasher : ::std::unary_function<size_t,std::string>{
    const size_t operator()(const std::string &str) const{
      return __gnu_cxx::__stl_hash_string(str.c_str());
    }
  };
  struct string_equal_to : ::std::binary_function< ::std::string , ::std::string , bool> {
    bool operator()(const ::std::string & x, const ::std::string & y ) const{
      return ( x.compare(y) == 0 );
    }
  };


  struct pointer_hasher : ::std::unary_function<size_t,void *>{
    const size_t operator()(const void *p) const{
      return reinterpret_cast<size_t>(p);
    }
  };
  struct int64_hasher : ::std::unary_function<size_t,int64_t>{
    const size_t operator()(const int64_t &i) const{
      return static_cast<size_t>(i);
    }
  };
}
#endif
