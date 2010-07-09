#ifndef EXCEPTION_H
#define EXCEPTION_H

#include "rakuten/vbuffer.h"

#define EXP_PRE_MSG __PRETTY_FUNCTION__,__FILE__,__LINE__
namespace rakuten {
  class Exception{
  protected:
    enum{
      K_WARN		= 1,
      K_ERROR		= 2,
      K_FATAL		= 3,
      K_SHUTDOWN	= 0xffffffff,
    };
    int 					kind;
    const char *			pref_func; // reteral reference . (__PRETTY_FUNCTION__)
    const char *			pref_file; // reteral reference . (__PRETTY_FUNCTION__)
    int 					pref_line;
    string_vbuffer msg;
  protected:
    Exception(int k, const char *pf,const char *f, int pl, const char *m);
    virtual ~Exception();
  public:
    const char *get_msg() const ;
    const char *get_func() const ;
    int  get_line() const;
    static void throw_exception(int k,const char *pref_func,const char *pref_file,int pref_line,const char *fmt_str,...) __attribute__((noreturn));
  };
  static const int LOGLV_TRACE = 0;
  static const int LOGLV_INFO  = 1;
  static const int LOGLV_WARN  = 2;
  static const int LOGLV_ERR   = 3;
  static const int LOGLV_FATAL = 4;
  static const int LOGLV_NONE = 100;
  void set_loglv(int lv);
  void set_logfp(FILE *fp);
  void log(int lv,const char * pf,const char * f,const int l,const char * fmt,...);
}

#if DEBUG
#  ifdef GNU_THREE_DOTS_MACRO    // gnu style
#    define TRACE_LOG(va_args...) rakuten::log(LOGLV_TRACE,"","",0,va_args);
#  else
#    define TRACE_LOG(...) rakuten::log(LOGLV_TRACE,"","",0,__VA_ARGS__);
#  endif
#else
#    define TRACE_LOG(...)
#endif

#ifdef GNU_THREE_DOTS_MACRO    // gnu style
#  define LOG(lv,pf,f,l,va_args...) rakuten::log(lv,pf,f,l,va_args);
#  define INFO_LOG(va_args...) LOG(LOGLV_INFO,EXP_PRE_MSG,va_args);
#  define WARN_LOG(va_args...) LOG(LOGLV_WARN,EXP_PRE_MSG,va_args);
#  define ERR_LOG(va_args...)  LOG(LOGLV_ERR,EXP_PRE_MSG,va_args);
#else
#  define LOG(lv,pre,...) rakuten::log(lv,pre,__VA_ARGS__);
#  define INFO_LOG(...) LOG(LOGLV_INFO,EXP_PRE_MSG,__VA_ARGS__);
#  define WARN_LOG(...) LOG(LOGLV_WARN,EXP_PRE_MSG,__VA_ARGS__);
#  define ERR_LOG(...)  LOG(LOGLV_ERR,EXP_PRE_MSG,__VA_ARGS__);
#endif


#endif
