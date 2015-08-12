#!/bin/bash
# 
# Installation script for Online WhatIf
#
# Online WhatIf is licensed under the MIT license, see:
# https://raw.githubusercontent.com/AURIN/online-whatif/master/LICENSE
#
# Tested on the following operating systems:
#  * Ubuntu 14.04
#
# To get started, install Linux then, on that machine install git, and clone
# online-whatif with e.g.:
#
# git clone https://github.com/AURIN/online-whatif.git
#
# Change to the online-whatif/utils directory and run the install script as
# root:
#
# cd online-whatif/utils
# sudo ./install.sh
#
# More information on installing Online WhatIf is available at:
#
# https://github.com/AURIN/online-whatif/blob/master/INSTALL.md
initial_pwd="`pwd`"

if [[ $EUID -ne 0 ]]; then
	echo "This script must be run as root" 1>&2
	exit 1
fi

# Upgrade all packages and install dependencies
echo Updating OS packages to latest versions
apt-get update && apt-get dist-upgrade -y
apt-get install -y tomcat7 postgresql postgis postgresql-9.3-postgis-2.1 couchdb apache2 unzip

# whatif expects /usr/local/bin/pgsql2shp but Ubuntu has /usr/bin/pgsql2shp
if [ ! -e /usr/local/bin/pgsql2shp ]
then
	( cd /usr/local/bin && ln -s /usr/bin/pgsql2shp )
fi

# Update JAVA_OPTS
if grep -q "^JAVA_OPTS.*/etc/aurin" /etc/default/tomcat7
then
	sed -i 's/^JAVA_OPTS=.*/JAVA_OPTS="-Djava.awt.headless=true -XX:+UseConcMarkSweepGC -Dfile.encoding=UTF-8 -Xmx2G -XX:PermSize=512M -Daurin.dir=\/etc\/aurin"/' tomcat7
fi

# Bind couchdb to all network interfaces
if grep -q "^bind_address.*0.0.0.0" /etc/couchdb/default.ini
then
	sed -i 's/^bind_address.*/bind_address = 0.0.0.0/' /etc/couchdb/default.ini
fi

# Create the whatif-development database with wifdemo schema

sudo -u postgres createuser -S -D -R -P whatif # create and note down a password for database access
sudo -u postgres createdb -O whatif whatif-development
sudo -u postgres psql whatif-development << EOF
create extension postgis;
ALTER USER whatif WITH SUPERUSER;
CREATE SCHEMA IF NOT EXISTS wifdemo AUTHORIZATION whatif;
\q
EOF

# Deploy geoserver on Tomcat
tempdir="/tmp/`uuidgen -t`"
service tomcat7 stop
mkdir $tempdir && cd $tempdir
wget -q -O geoserver-2.7.1-war.zip "http://downloads.sourceforge.net/project/geoserver/GeoServer/2.7.1/geoserver-2.7.1-war.zip?r=http%3A%2F%2Fgeoserver.org%2Frelease%2Fstable%2F&ts=1430895622&use_mirror=aarnet"
mkdir geoserver-2.7.1-war && cd geoserver-2.7.1-war
unzip -q ../geoserver-2.7.1-war.zip
mv geoserver.war /var/lib/tomcat7/webapps/
chown root:root /var/lib/tomcat7/webapps/geoserver.war
cd $initial_pwd
rm -r $tempdir
service tomcat7 start



# Restart relevant services
sudo service tomcat7 restart
sudo service couchdb restart
