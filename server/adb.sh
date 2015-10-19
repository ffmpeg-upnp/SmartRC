#!/bin/sh

#if [ $# != 1 ] || [ $# != 2 ];then
#	echo "usage:adb.sh [app] [ip(172.17.1.?)]"
#	exit 1;
#fi

appName="/home/wangbo/sld8_PF/master/jb/out/target/product/sld8/system/app/Smart_Server.apk"
packageName="com.casky.sLD8_smart_rc_server"
IP="172.17.1.126"
if [ $# = 1 ];then
	IP="$1"
fi

#appName="GetIP.apk"
#packageName="com.example.getip"

	echo "using default ip addr: $IP"
	echo "using default app: $appName"
	echo "using default packge: $packageName"
	echo "disconnecting"
	adb disconnect
	echo "connecting to $IP"
	adb connect "$IP"
	if [ $? = 1 ];then
		exit 1
	fi
	echo "remounting"
	adb remount
	if [ $? = 1 ];then
		exit 1
	fi
	echo "uninstalling"
	adb uninstall "$packageName"
	echo "installing"
	adb install "$appName"
#fi


