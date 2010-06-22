#!/usr/bin/env sh
export LD_LIBRARY_PATH=$1/../lib:$LD_LIBRARY_PATH
/usr/local/pear/bin/pecl-gen -f rmcc.xml
cd phprmcc
phpize
./configure
make clean all test LDFLAGS=-L../../lib
# sudo make install
