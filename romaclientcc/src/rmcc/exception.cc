#include "rakuten/exception.h"
int loglv = LOGLV_INFO;
namespace rakuten {
  Exception::Exception(int k, const char *pf, const char *f, int pl, const char *m)
    : kind(k), pref_func(pf), pref_file(f),pref_line(pl),msg(m)
  {
    WARN_LOG(m);
  }
  Exception::~Exception(){
  }
  const char * Exception::get_msg()const{
    return this->msg;
  }
  const char * Exception::get_func()const{
    return this->pref_func;
  }
  int Exception::get_line()const{
    return this->pref_line;
  }
  void Exception::throw_exception( 
    int k,
    const char *pref_func,
    const char *pref_file,
    int pref_line,
    const char *fmt_str,...)
  {
    va_list vl1,vl2;
    va_start(vl1,fmt_str);
    va_start(vl2,fmt_str);
    string_vbuffer m;
    m.append_vsprintf(fmt_str,vl1,vl2);
    va_end(vl2);
    va_end(vl1);
    throw Exception(k,pref_func , pref_file,pref_line , m);
  }
}
