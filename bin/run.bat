@echo off
set ASPECTWERKZ_HOME=..
aspectwerkz -cp ../dist/snap.jar;../lib/damon.jar;../lib/easypastry.jar;../lib/log4j.jar;../lib/asm-all-3.1.jar;../lib/pastry.jar;../lib/bunshin.jar;../lib/junit.jar;../lib/jdom.jar;../lib/xstream.jar;../lib/sbbi-upnplib-1.0.4.jar;../lib/jetty.jar;../lib/jetty-util.jar;../lib/servlet.jar;../lib/ant-1.6.5.jar;../lib/core-3.1.1.jar;../lib/jsp-2.1.jar;../lib/jsp-api-2.1.jar;../lib/cos.jar; org.objectweb.snap.Starter %*
