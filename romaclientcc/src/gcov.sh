#/usr/bin/env bash

if [ "$#" == "0" ];then
    $0 clean build gen
    exit $?;
fi
while true;
do
    case $1 in
	clean)
	    echo '###############'
	    echo '# CLEAN'
	    find . -name '*.gcno' | xargs -n 1 rm -rf
	    find . -name '*.gcda' | xargs -n 1 rm -rf
	    rm -rf ./gcov
	    mkdir -p ./gcov
	    ;;
	build)
	    echo '###############'
	    echo '# BUILD'
	    export LD_LIBRARY_PATY=`pwd`/lib:$LD_LIBRARY_PATH
	    make clean bin/rmcc_test DEBUG=1 GCOV=1 ;
	    ./bin/rmcc_testdc
	    ;;
	gen)
	    echo '###############'
	    echo '# GEN-GCOV'
	    rm -rf ./gcov
	    mkdir -p ./gcov
	    cd ./gcov
	    for cc in `find .. -name '*.cc' ` ; do ln -sf $cc . ; done
	    #for gcno in `find .. -name '*.gcno' ` ; do  gcov -b -c -f -u -o `echo $gcno |  sed -e 's/\/[^\/]*$//'` $gcno > /dev/null  ; done
	    for gcno in `find .. -name '*.gcno' ` ; do  gcov -f -o `echo $gcno |  sed -e 's/\/[^\/]*$//'` $gcno > /dev/null  ; done
	    for gcov in `find . -name '*.gcov' ` ; do mv $gcov $gcov.txt ; done
	    ;;
	*)
	    exit 0;
	    ;;
    esac
    shift
done