#!/bin/sh
KABEJA_HOME=`dirname $0`
java -jar  $KABEJA_HOME/kabeja-inkscape-dxf2svg.jar "$@"

