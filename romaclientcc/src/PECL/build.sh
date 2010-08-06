#!/usr/bin/env bash
cd `dirname $0`
export LD_LIBRARY_PATH=`pwd`/../lib:$LD_LIBRARY_PATH
export TZ=Asia/Tokyo 
pecl-gen -f rmcc.xml
cd phprmcc
phpize
if [ "$WITH_CONFIGURE" == "1" ];then
    ./configure CPPFLAGS="-I`pwd`/../../include"
fi
make clean all test LDFLAGS=-L../../lib
if [ "$WITH_TEST" == "1" ];then
    `which phpunit` --coverage-html . PhpunitEnv
    source php.env
    cp -f $PHP_EXT_DIR/* `pwd`/phprmcc/modules/
    echo "extension_dir=`pwd`/phprmcc/modules" >> php.ini 
    #echo "extension_dir=/usr/lib/php/modules"
    echo "extension=phprmcc.so" >> php.ini 
    echo "extension=xdebug.so" >> php.ini 
    php -c php.ini `which phpunit` --coverage-html xdebug test/AllTest
fi
# sudo make install
