#/usr/bin/env bash

# Test-Build & Test
export LD_LIBRARY_PATY=`pwd`/lib:$LD_LIBRARY_PATH
make clean
if [ WITH_TEST == "1" ];then
    make bin/rmcc_test DEBUG=1
    ./bin/rmcc_testd
fi
# Build
make rmcc
# valgrind -v --leak-check=yes --track-origins=yes ./bin/rmcc_testd