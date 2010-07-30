#ifndef VBUFFER_H
#define VBUFFER_H
#include <iostream>
using namespace std;

#include <stdarg.h>
#include <stdio.h>
#include <memory.h>
#include <memory>
#include <vector>
#include <exception>
#include <stdexcept>
#include <inttypes.h>
namespace rakuten {
#define DEFBLOCKSIZ (1024)
  template < typename data_t = uint8_t>
  struct vbuffer {
  protected:
    const long BLOCKSIZ;
    long                 poffset;
    std::vector<data_t>  vbuf;
    long                 uselen;
  public:
    inline long calc_expand(long len){
      return (((len-1)/BLOCKSIZ)+1) * BLOCKSIZ;
    }
    inline long real_length(){
      return static_cast<long>(vbuf.size()-poffset);
    }
    virtual ~vbuffer(){}
    vbuffer(long BLOCKSIZ=DEFBLOCKSIZ)
      :BLOCKSIZ(BLOCKSIZ),vbuf(BLOCKSIZ)
    {
      uselen  = 0;
      poffset = 0;
    }
    // copy
    vbuffer(const vbuffer &in,long BLOCKSIZ=DEFBLOCKSIZ)
      :BLOCKSIZ(BLOCKSIZ),vbuf(BLOCKSIZ)
    {
      (*this) = in;
    }
    vbuffer(const data_t * data, long len,long BLOCKSIZ=DEFBLOCKSIZ)
      :BLOCKSIZ(BLOCKSIZ),vbuf(BLOCKSIZ)
    {
      reset(data,len);
    }
    vbuffer& operator=(const vbuffer &in)
    {	// init buffer.
      uselen  = 0;
      poffset = 0;
      this->relength(in.length());
      memcpy(this->pointer(),in.const_pointer(),this->uselen*sizeof(data_t));
      return *this;
    }
    virtual void reset(const data_t * data , long len ) {
      uselen  = 0;
      poffset = 0;
      this->relength(len);
      memcpy(this->pointer(),data,len*sizeof(data_t));
    }
    // ctrl
    virtual void relength(long len)
    {	// Buffer insufficient.
      if ( len == 0 ) {
        poffset = 0;
      } else {
        if( real_length() < len){
          this->vbuf.resize(this->calc_expand(len+poffset));
        }
      }
      this->uselen = len;
    }
    virtual inline long length() const{
      return this->uselen;
    }
    // accessa
    operator const data_t*()  const {
      return &this->vbuf[0] + poffset;
    }
    virtual inline const data_t* const_pointer () const{
      return &this->vbuf[0] + poffset;
    }
    virtual inline data_t * pointer(){
      return &this->vbuf[0] + poffset;
    }
    // 
    virtual void append(const data_t * data,long len){
      long use = this->length();
      this->relength(this->length() + len);
      memcpy(this->pointer()+use,data,len*sizeof(data_t));
    }
    virtual data_t * get(long len){
      if ( len <= this->length() ) {
        data_t * ret = this->pointer();
        poffset += len;
        uselen -= len;
        return ret;
      }
      return 0;
    }
  };

  struct string_vbuffer : vbuffer<char> {
    virtual ~string_vbuffer(){}
    string_vbuffer(long BLOCKSIZ=DEFBLOCKSIZ) : vbuffer<char>(BLOCKSIZ){}
    string_vbuffer(const char * in,long BLOCKSIZ=DEFBLOCKSIZ) : vbuffer<char>(BLOCKSIZ){
      (*this) += in;
    }
    string_vbuffer & operator += (const char * in ){
      long len = this->length();
      long slen = strlen(in);
      this->append(in,slen+1);
      this->relength(len+slen);
      return *this;
    }
    char * get_to_token(const char * token){
      long tlen = strlen(token);
      char* p = strstr(this->pointer(),token);
      if ( !p ) {
        return 0;
      }
      char * ret = this->get(p - this->pointer());
      memset(this->pointer(),0,tlen*sizeof(char));
      this->get(tlen);
      return ret;
    }
    void append_sprintf(const char *fmt_str,...) {
      long len    = this->length();
      long remain = this->real_length()-len;
      this->relength(this->real_length());
      
      va_list vl;
      va_start(vl,fmt_str);
      int nlen = ::vsnprintf( this->pointer()+len , remain , fmt_str , vl);
      va_end(vl);
      if(nlen >= remain){
        this->relength( len + nlen + 1 );
        va_start(vl,fmt_str);
        ::vsprintf( this->pointer()+len, fmt_str , vl);
        va_end(vl);
      }
      this->relength( len + nlen );
    }
    void append_vsprintf(const char *fmt_str,va_list vl1,va_list vl2) {
      long len    = this->length();
      long remain = this->real_length()-len;
      this->relength(this->real_length());
      int nlen = ::vsnprintf( this->pointer()+len , remain , fmt_str , vl1);
      if(nlen >= remain){
        this->relength( len + nlen + 1 );
        ::vsprintf( this->pointer()+len, fmt_str , vl2);
      }
      this->relength( len + nlen );
    }
  };
}
#endif
