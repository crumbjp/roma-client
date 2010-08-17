include ./_make_/MakePre.def

# Excutable modules.
EXEC_TARGETS = bin/rmcc_test
SO_TARGETS = rmcc rmc
O_TARGETS  = rmcc_test

rmc : rmcc
rmcc_test: rmcc
bin/rmcc_test : rmcc_test

FINAL_LINK_FLGS = -pthread

ifdef M64
  override EXT_LIB_DIRS 		+= /usr/local/lib $(EXT_DIR)m64/lib/
else
  override EXT_LIB_DIRS 		+= /usr/local/lib $(EXT_DIR)lib/
endif

CXX=g++
CC=gcc
MAKE= make

EXE_LINK = $(CXX)

include ./_make_/Make.def
