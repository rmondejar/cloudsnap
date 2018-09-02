#!/bin/sh

JAVA_COMMAND=../../jdk1.6.0_05/bin/java
#JAVA_COMMAND=java
AW_HOME=..

if [ -z "$CLASSPATH" ]; then
	CLASSPATH="."
fi

AW_LIBS=$AW_HOME/lib/dom4j-1.4.jar:$AW_HOME/lib/qdox-1.4.jar:$AW_HOME/lib/concurrent-1.3.1.jar:$AW_HOME/lib/trove-1.0.2.jar:$AW_HOME/lib/jrexx-1.1.1.jar:$AW_HOME/lib/sbbi-upnplib-1.0.4.jar
AW_PATH=$AW_HOME/lib/aspectwerkz-jdk5-2.0.jar
AW_BOOTPATH=$AW_HOME/lib/aspectwerkz-extensions-2.0.jar:$AW_HOME/lib/aspectwerkz-core-2.0.jar:$AW_HOME/lib/aspectwerkz-2.0.jar:$AW_HOME/lib/aspectwerkz-jdk5-2.0.jar:$AW_LIBS:$AW_HOME/lib/piccolo-1.03.jar

SNAP_LIB=:$AW_HOME/dist/snap.jar:$AW_HOME/lib/damon.jar:$AW_HOME/lib/asm-all-3.1.jar:$AW_HOME/lib/pastry.jar:$AW_HOME/lib/bunshin.jar:$AW_HOME/lib/junit.jar:$AW_HOME/lib/jdom.jar:$AW_HOME/lib/xstream.jar:$AW_HOME/lib/sbbi-upnplib-1.0.4.jar:$AW_HOME/lib/jetty.jar:$AW_HOME/lib/jetty-util.jar:$AW_HOME/lib/servlet.jar:$AW_HOME/lib/ant-1.6.5.jar:$AW_HOME/lib/core-3.1.1.jar:$AW_HOME/lib/jsp-2.1.jar:$AW_HOME/lib/jsp-api-2.1.jar:$AW_HOME/lib/cos.jar

$JAVA_COMMAND -javaagent:$AW_PATH -Xbootclasspath/p:"$AW_BOOTPATH" -Daspectwerkz.home="$AW_HOME" -cp $SNAP_LIB: org.objectweb.snap.Starter "$@"
