#!/usr/bin/env bash
function auto {
    touch NEWS README AUTHORS ChangeLog COPYING
    libtoolize --force --copy
    aclocal
    autoheader
    automake -a -c
    autoconf
}
case $1 in
    tar)
	auto
	cd `dirname $0`/..
	V=$2
	if [ "$V" == "" ];then
	    V=0.0.1
	fi
	ln -s romaclient rmcc-${V}
	tar czvhf  rmcc-${V}.tar.gz \
	    rmcc-${V}/AUTHORS  \
	    rmcc-${V}/COPYING \
	    rmcc-${V}/ChangeLog \
	    rmcc-${V}/INSTALL \
	    rmcc-${V}/Makefile.am \
	    rmcc-${V}/Makefile.in \
	    rmcc-${V}/NEWS \
	    rmcc-${V}/README \
	    rmcc-${V}/aclocal.m4 \
	    rmcc-${V}/autom4te.cache \
	    rmcc-${V}/config.guess \
	    rmcc-${V}/config.h.in \
	    rmcc-${V}/config.sub \
	    rmcc-${V}/configure \
	    rmcc-${V}/configure.in \
	    rmcc-${V}/depcomp \
	    rmcc-${V}/install-sh \
	    rmcc-${V}/ltmain.sh \
	    rmcc-${V}/m4 \
	    rmcc-${V}/missing \
	    rmcc-${V}/release.sh \
	    rmcc-${V}/src/INSTALL \
	    rmcc-${V}/src/Makefile.am \
	    rmcc-${V}/src/Makefile.in \
	    rmcc-${V}/src/build.sh \
	    rmcc-${V}/src/include \
	    rmcc-${V}/src/rmcc

	rm rmcc-${V}
	;;  
    clean)
	rm -rf .deps \
	AUTHORS    \
	INSTALL \
	Makefile.in \
	aclocal.m4 \
	confdefs.h \
        config.h.in \
	config.in.h \
	config.sub \
	configure.scan \
	libtool \
	missing \
	COPYING \
	Makefile \
	NEWS \
        autom4te.cache \
	config.guess \
	config.log \
	configure \
	depcomp \
        ltmain.sh \
	ChangeLog \
	README \
	autoscan.log \
	config.h \
	config.status \
	m4 \
        stamp-h1 \
	src/.deps \
	src/Makefile.in \
	src/Makefile \
	src/include/Makefile.in \
	src/include/Makefile
	;;
    all)
	./build.sh clean
	./build.sh
	./configure --with-openssl-header=/usr/local/openssl/include --with-openssl-lib=/usr/local/openssl/lib
	make
	;;
    *)
	auto
	;;
esac
