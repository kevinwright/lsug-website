#!/bin/sh

if [ -z "$CATALINA_HOME" ]; then
	echo "Set CATALINA_HOME before running this script"
	echo "See comments in conf/server.xml for details"
	exit 1
fi

DIRNAME=`dirname $0`

export CATALINA_BASE=`cd $DIRNAME/..; pwd`

export CATALINA_OPTS="-Xms128m -Xmx256m -Dlog.dir=${CATALINA_BASE}/logs -verbosegc"

find ${CATALINA_BASE}/work -name SESSIONS.ser | xargs rm
${CATALINA_HOME}/bin/startup.sh
