#!/bin/sh
#MONO wrapper script
KABEJA_HOME=`dirname $0`
mono  $KABEJA_HOME/kabeja-dxf2svg.exe "$1"

