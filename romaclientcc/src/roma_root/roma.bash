#!/usr/bin/env bash
DIR=`dirname $0`
case $1 in
    start)
	rm localhost_*
	$DIR/roma/ruby/server/bin/mkroute localhost_11211 localhost_11212 localhost_11213 localhost_11214 --enabled_repeathost
	#$DIR/roma/ruby/server/bin/mkroute localhost_11211 localhost_11212 --enabled_repeathost
	$DIR/roma/ruby/server/bin/romad localhost -p 11211 -d --enabled_repeathost
	$DIR/roma/ruby/server/bin/romad localhost -p 11212 -d --enabled_repeathost
	$DIR/roma/ruby/server/bin/romad localhost -p 11213 -d --enabled_repeathost
	$DIR/roma/ruby/server/bin/romad localhost -p 11214 -d --enabled_repeathost
	;;
    prof)
        rm localhost_*
	$DIR/roma/ruby/server/bin/mkroute localhost_11211 localhost_11212 localhost_11213 localhost_11214 --enabled_repeathost
        #$DIR/roma/ruby/server/bin/mkroute localhost_11211 localhost_11212 --enabled_repeathost
        /usr/local/ruby/bin/ruby $DIR/roma/ruby/server/bin/romad localhost -p 11211 -d --enabled_repeathost
        ruby -r profile $DIR/roma/ruby/server/bin/romad localhost -p 11212 -d --enabled_repeathost
	;;
    start1)
	$DIR/roma/ruby/server/bin/romad localhost -p 11211 -d -j localhost_11212 --enabled_repeathost
	nc localhost 11212 <<< "recover"
	;;
    start2)
	$DIR/roma/ruby/server/bin/romad localhost -p 11212 -d -j localhost_11211 --enabled_repeathost
	nc localhost 11211 <<< "recover"
	;;
    start3)
	$DIR/roma/ruby/server/bin/romad localhost -p 11213 -d -j localhost_11211 --enabled_repeathost
	nc localhost 11212 <<< "recover"
	;;
    start4)
	$DIR/roma/ruby/server/bin/romad localhost -p 11214 -d -j localhost_11211 --enabled_repeathost
	nc localhost 11212 <<< "recover"
	;;
    stop)
	nc localhost 11211 <<< rbalse
	nc localhost 11212 <<< rbalse
	nc localhost 11213 <<< rbalse
	nc localhost 11214 <<< rbalse
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

