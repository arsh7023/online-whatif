#!/bin/bash
STAT=`sudo service tomcat7 status | grep pid`
if [ -z "$STAT" ] ; then
    sudo service tomcat7 start
    (sleep 2; echo "25"; sleep 2; echo "50"; sleep 2; echo "75"; sleep 2; echo "100") 	   | zenity --progress --auto-close --text "Online WhatIf starting"
fi
/usr/local/bin/geoserver_start.sh &
firefox "https://##hostname##/whatif"
