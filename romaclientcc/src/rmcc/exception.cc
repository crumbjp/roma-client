#include "rakuten/exception.h"
#include <stdio.h>
#include <sys/types.h>
#include <unistd.h>

namespace rakuten {
  Exception::Exception(int k, const char *pf, const char *f, int pl, const char *m)
    : kind(k), pref_func(pf), pref_file(f),pref_line(pl),msg(m)
  {
    LOG(LOGLV_WARN,pf,f,pl,m);
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

  int loglv = LOGLV_WARN;
  FILE *logfp  = stderr;
  void set_loglv(int lv) {
    loglv = lv;
  }
  void set_logfp(FILE *fp) {
    logfp = fp;
  }
  static pid_t pid = 0;
  void log(int lv,const char * pf,const char * f,const int l,const char * fmt,...) {
    if ( ! pid ) {
      pid = getpid();
    }
    if ( lv >= loglv ){
      fprintf(logfp,"[%s:%u](%d) ",f,l,pid);
      va_list vl;
      va_start(vl,fmt);
      vfprintf(logfp,fmt,vl);
      va_end(vl);
      fprintf(logfp,"\n");
    }
  }
  
}
