#!/usr/bin/env bash
DIR=`dirname $0`
echo cd $DIR
cd $DIR
case $1 in
    start)
	rm localhost_*
	./roma/ruby/server/bin/mkroute localhost_11211 localhost_11212 localhost_11213 localhost_11214 --enabled_repeathost
	#./roma/ruby/server/bin/mkroute localhost_11211 localhost_11212 --enabled_repeathost
	./roma/ruby/server/bin/romad --config ntest_config.rb localhost -p 11211 -d --enabled_repeathost
	./roma/ruby/server/bin/romad --config ntest_config.rb localhost -p 11212 -d --enabled_repeathost
	./roma/ruby/server/bin/romad --config ntest_config.rb localhost -p 11213 -d --enabled_repeathost
	./roma/ruby/server/bin/romad --config ntest_config.rb localhost -p 11214 -d --enabled_repeathost
	./roma/ruby/server/bin/mkroute localhost_11219 -r 1 --enabled_repeathost
	./roma/ruby/server/bin/romad --config ptest_config.rb  localhost -p 11219 -d --enabled_repeathost
	./roma/ruby/server/bin/mkroute localhost_23456 -r 1 --enabled_repeathost
	./roma/ruby/server/bin/romad --config ptest_config.rb  localhost -p 23456 -d --enabled_repeathost
	;;
    prof)
        rm localhost_*
	./roma/ruby/server/bin/mkroute localhost_11211 localhost_11212 localhost_11213 localhost_11214 --enabled_repeathost
        #roma/ruby/server/bin/mkroute localhost_11211 localhost_11212 --enabled_repeathost
        /usr/local/ruby/bin/ruby roma/ruby/server/bin/romad --config ntest_config.rb localhost -p 11211 -d --enabled_repeathost
        ruby -r profile roma/ruby/server/bin/romad --config ntest_config.rb localhost -p 11212 -d --enabled_repeathost
	;;
    start1)
	./roma/ruby/server/bin/romad --config ntest_config.rb localhost -p 11211 -d -j localhost_11212 --enabled_repeathost
	nc localhost 11212 <<< "recover"
	;;
    start2)
	./roma/ruby/server/bin/romad --config ntest_config.rb localhost -p 11212 -d -j localhost_11211 --enabled_repeathost
	nc localhost 11211 <<< "recover"
	;;
    start3)
	./roma/ruby/server/bin/romad --config ntest_config.rb localhost -p 11213 -d -j localhost_11211 --enabled_repeathost
	nc localhost 11212 <<< "recover"
	;;
    start4)
	./roma/ruby/server/bin/romad --config ntest_config.rb localhost -p 11214 -d -j localhost_11211 --enabled_repeathost
	nc localhost 11212 <<< "recover"
	;;
    stop)
	nc localhost 11211 <<< rbalse
	nc localhost 11212 <<< rbalse
	nc localhost 11213 <<< rbalse
	nc localhost 11214 <<< rbalse
	nc localhost 11219 <<< rbalse
	nc localhost 23456 <<< rbalse
	;;
    stop1)
	nc localhost 11211 <<< rbalse
	;;
    stop2)
	nc localhost 11212 <<< rbalse
	;;
    stop3)
	nc localhost 11213 <<< rbalse
	;;
    stop4)
	nc localhost 11214 <<< rbalse
	;;
    restart)
	
	;;
    *)
	
esac

