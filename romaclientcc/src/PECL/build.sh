#!/usr/bin/env sh
export LD_LIBRARY_PATH=$1/../lib:$LD_LIBRARY_PATH
export TZ=Asia/Tokyo 
pecl-gen -f rmcc.xml
cd phprmcc
phpize
if [ $WITH_CONFIGURE == "1" ];then
    ./configure CPPFLAGS="-I`pwd`/../../include"
fi
make clean all test LDFLAGS=-L../../lib
# sudo make install
