#!/bin/sh

if [ -z "$CATALINA_HOME" ]; then
        echo "Set CATALINA_HOME before running this script"
        echo "See comments in conf/server.xml for details"
        exit 1
fi


DIRNAME=`dirname $0`

export CATALINA_BASE=`cd $DIRNAME/..; pwd`

${CATALINA_HOME}/bin/shutdown.sh

NOW=`date +"%Y-%m-%d:%H:%M:%S"`
mv ${CATALINA_BASE}/logs/catalina.out ${CATALINA_BASE}/logs/catalina.out.${NOW}
