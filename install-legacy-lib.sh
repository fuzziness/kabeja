#!/bin/sh
mvn install:install-file -DgroupId=de.miethxml  \
-DartifactId=miethxml-ui \
-Dversion=0.0.1  \
-Dfile=legacy-lib/miethxml-ui.jar  \
-Dpackaging=jar \
-DgeneratePom=true
