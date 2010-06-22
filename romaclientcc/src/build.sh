#/usr/bin/env bash

# Test-Build & Test
export LD_LIBRARY_PATY=`pwd`/lib:$LD_LIBRARY_PATH
make clean bin/rmcc_test DEBUG=1
./bin/rmcc_testd
# Build
make bin/rmcc_test 
