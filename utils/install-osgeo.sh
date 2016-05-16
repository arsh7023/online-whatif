#!/bin/bash
#set -x
# 
# Installation script for Online WhatIf
#
# Online WhatIf is licensed under the MIT license, see:
# https://raw.githubusercontent.com/AURIN/online-whatif/master/LICENSE
#
# Tested on the following operating systems:
#  * OSGeo 9.5
#
# To get started, install Linux and install git, and clone online-whatif:
#
#   sudo apt-get install git
#   git clone https://github.com/AURIN/online-whatif.git
#
# Change to the online-whatif/utils directory and run the install script as
# root:
#
#   cd online-whatif/utils
#   sudo ./install.sh
#
# Open the workbenchauth page in a web browser and create a new user:
#
#   https://servername/workbenchauth
#
# Your temporary username and password will be emailed to you, or you can find
# it in the log file /var/log/tomcat7/catalina.out. Then log in to Online
# WhatIf:
#
#   https://servername/whatif
#
# More information on installing Online WhatIf is available at:
#
# https://github.com/AURIN/online-whatif/blob/master/INSTALL.md
#
show_help() {
cat << EOF
Usage: ${0##*/} [-hes]
Install AURIN Online WhatIf on OSGeo.  More information on installing Online
WhatIf is available at: https://github.com/AURIN/online-whatif/blob/master/INSTALL.md

    -h    Display this help and exit
    -e    Install optional email dependencies for Workbench Auth.  These
              are necessary for creating new users for Online WhatIf.
    -s    Build war files from source.  Otherwise, install from binary release.

EOF
}

# script requires root as this is expected by the OSGeo install process
# some commands are run as the normal user with uid 1000, mainly for the geoserver jetty container which expects this on OSGeo
if [[ $EUID -ne 0 ]]; then
	echo "This script must be run as root" 1>&2
	echo "" 1>&2
	show_help
	exit 1
fi

email_deps=0
build_source=0

OPTIND=1
while getopts "hes" opt; do
    case "$opt" in
        h)
            show_help
            exit 0
            ;;
        e)  email_deps=1
            ;;
        s)  build_source=1
            ;;
        '?')
            show_help >&2
            exit 1
            ;;
    esac
done
shift "$((OPTIND-1))" # Shift off the options and optional --.

# if there are any missing package dependencies, install them
packages=""
for package in couchdb pwgen
do
	if ! dpkg -s $package 2>/dev/null |grep Status |grep -q installed
        then
                packages="${packages} ${package}"
        fi
done
packages=`echo $packages | awk '{gsub(/^ +| +$/,"")} {print $0}'` # trim leading and trailing whitespace
if [[ $packages != "" ]]
then
	sudo apt-get install -y $packages
fi

# Set all variables and passwords (you may update these to your liking)
export github_release=v1.0.0-alpha.1
export script_dir="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )" # directory that contains the script
export pg_user=whatif
export pg_pass=`pwgen -n 16 -N 1`
export PGPASSWORD=$pg_pass # PGPASSWORD is the variable that pg_dump and pg_restore look for
export database=whatif-development
export schema=wifdemo
export geoserver_master_pw=geoserver
export workspace=whatif
export datastore=whatifStore
export hostname=`hostname`
export namespace_uri="http://$hostname/$workspace"
export auth_user=envision
export auth_pass=`pwgen -n 16 -N 1`
export auth_db=envisiondb
export email_user=admin
export email_pass=`pwgen -n 16 -N 1`
export user1=`getent passwd 1000 |cut -d ':' -f 1` # assume uid 1000 for running geoserver

# if we've run before, get the passwords from the config files
if [ -f /etc/aurin/whatif-combined.properties ]
then
	echo pulling passwords from /etc/aurin/whatif-combined.properties
	pg_pass=`perl -ne 'while(/wif.geo.db.password=(.*)/g){print "$1";}' /etc/aurin/whatif-combined.properties`
	PGPASSWORD=$pg_pass # PGPASSWORD is the variable that pg_dump and pg_restore look for
fi
if [ -f /etc/aurin/envision-combined.properties ]
then
	echo pulling passwords from /etc/aurin/envision-combined.properties
	auth_pass=`perl -ne 'while(/env.geo.db.password=(.*)/g){print "$1";}' /etc/aurin/envision-combined.properties`
	email_pass=`perl -ne 'while(/env.mail.password=(.*)/g){print "$1";}' /etc/aurin/envision-combined.properties`
fi

# whatif expects /usr/local/bin/pgsql2shp but Ubuntu has /usr/bin/pgsql2shp
if [ ! -e /usr/local/bin/pgsql2shp ]
then
	( cd /usr/local/bin && sudo ln -s /usr/bin/pgsql2shp )
fi

# Update JAVA_OPTS
if ! grep -q "^JAVA_OPTS.*/etc/aurin" /etc/default/tomcat7
then
	sudo sed -i 's/^JAVA_OPTS=.*/JAVA_OPTS="-Djava.awt.headless=true -XX:+UseConcMarkSweepGC -Dfile.encoding=UTF-8 -Xmx2G -XX:PermSize=512M -Daurin.dir=\/etc\/aurin"/' /etc/default/tomcat7
fi

# Bind couchdb to all network interfaces
if ! grep -q "^bind_address.*0.0.0.0" /etc/couchdb/default.ini
then
	sudo sed -i 's/^bind_address.*/bind_address = 0.0.0.0/' /etc/couchdb/default.ini
fi

# Create database
if ! sudo -u postgres psql -lqt | cut -d \| -f 1 | grep -w whatif-development > /dev/null
then
	sudo -u postgres createuser -S -D -R $pg_user
	sudo -u postgres psql -q -c "ALTER USER $pg_user WITH PASSWORD '$pg_pass';"
	sudo -u postgres createdb -O $pg_user $database
	sudo -u postgres psql -q $database << EOF
create extension postgis;
ALTER USER $pg_user WITH SUPERUSER;
CREATE SCHEMA IF NOT EXISTS $schema AUTHORIZATION $pg_user;
\q
EOF
fi

# restore sample database.  Password is supplied in PGPASSWORD variable
if ! sudo -u postgres psql << EOF | cut -d \| -f 1 |grep wifdemo > /dev/null
\c whatif-development
\dt *.*
EOF
then
	pg_restore -Fc -U $pg_user -i -h localhost -p 5432 -d whatif-development $script_dir/../db/wanneroodump
fi

# allow the system-provided geoserver to handle long urls
if ! grep -q "<Set name=\"headerBufferSize\">64000</Set>" /usr/local/lib/geoserver-2.8.2/etc/jetty.xml
then
	sudo sed -i "s/<Set name=\"confidentialPort\">8443<\/Set>/<Set name=\"confidentialPort\">8443<\/Set>\n            <Set name=\"headerBufferSize\">64000<\/Set>/" /usr/local/lib/geoserver-2.8.2/etc/jetty.xml
fi

# (re)start system-provided geoserver
echo "restarting system-provided geoserver"
sudo -u $user1 /usr/local/lib/geoserver-2.8.2/bin/shutdown.sh > /dev/null 2>&1
sudo -u $user1 /usr/local/lib/geoserver-2.8.2/bin/startup.sh > /dev/null 2>&1 &

echo -n "waiting for geoserver to start"
until curl -s "http://localhost:8082/geoserver/web/" |grep "<title>GeoServer: Welcome</title>"
do 
	sleep 2
done
echo "."

# create a new workspace
if ! curl -s -u "admin:$geoserver_master_pw" -XGET -H "Accept: text/xml" -H "Content-type: text/xml" http://localhost:8082/geoserver/rest/workspaces.xml |grep -q "<name>$workspace</name>"
then
	xml="<?xml version=\"1.0\" encoding=\"UTF-8\"?>
<namespace>
  <prefix>$workspace</prefix>
  <uri>$namespace_uri</uri>
</namespace>"
	curl -s -v -u "admin:$geoserver_master_pw" -XPOST -H "Content-type: text/xml" -d "$xml" http://localhost:8082/geoserver/rest/namespaces
fi

# create a new datastore
if ! curl -s -v -u "admin:$geoserver_master_pw" -XGET -H "Accept: text/xml" -H "Content-type: text/xml" http://localhost:8082/geoserver/rest/workspaces/$workspace/datastores.xml |grep -q "<name>$datastore</name>"
then
	xml="<?xml version=\"1.0\" encoding=\"UTF-8\"?>
<dataStore>
  <name>$datastore</name>
  <type>PostGIS</type>
  <enabled>true</enabled>
  <workspace>
    <name>whatif</name>
  </workspace>
  <connectionParameters>
    <entry key=\"port\">5432</entry>
    <entry key=\"passwd\">$pg_pass</entry>
    <entry key=\"dbtype\">postgis</entry>
    <entry key=\"encode functions\">false</entry>
    <entry key=\"Evictor run periodicity\">300</entry>
    <entry key=\"namespace\">http://$hostname/whatif</entry>
    <entry key=\"schema\">wifdemo</entry>
    <entry key=\"create database\">false</entry>
    <entry key=\"fetch size\">1000</entry>
    <entry key=\"preparedStatements\">false</entry>
    <entry key=\"min connections\">1</entry>
    <entry key=\"host\">localhost</entry>
    <entry key=\"Evictor tests per run\">3</entry>
    <entry key=\"validate connections\">true</entry>
    <entry key=\"max connections\">10</entry>
    <entry key=\"Support on the fly geometry simplification\">false</entry>
    <entry key=\"database\">$database</entry>
    <entry key=\"Max connection idle time\">300</entry>
    <entry key=\"Test while idle\">true</entry>
    <entry key=\"Loose bbox\">true</entry>
    <entry key=\"Expose primary keys\">false</entry>
    <entry key=\"Max open prepared statements\">50</entry>
    <entry key=\"Estimated extends\">false</entry>
    <entry key=\"user\">$pg_user</entry>
  </connectionParameters>
</dataStore>"
	curl -s -v -u "admin:$geoserver_master_pw" -XPOST -H "Content-type: text/xml" -d "$xml" "http://localhost:8082/geoserver/rest/workspaces/$workspace/datastores"
fi

# Configure the apache reverse proxy
sudo a2ensite default-ssl > /dev/null
sudo a2enmod proxy_ajp > /dev/null
sudo a2enmod proxy_http > /dev/null
sudo a2enmod ssl > /dev/null
sudo a2enmod rewrite > /dev/null

# feels like sed might be the wrong tool for this job, though it works
# 000-default
if ! grep -q "ProxyPass /geoserver" /etc/apache2/sites-enabled/000-default.conf
then
	sudo sed -i "s/<VirtualHost \*:80>/<VirtualHost \*:80>\n\tServerName $hostname/" /etc/apache2/sites-enabled/000-default.conf
	conf="	ProxyPreserveHost On

	RewriteEngine  On
	RewriteRule     ^/$             https://$hostname/whatif/      [R]

	ProxyPass /geoserver http://localhost:8082/geoserver
	ProxyPassReverse /geoserver http://localhost:8082/geoserver

	# Allow long GET requests for what-if
	LimitRequestLine 64000"
	conf=$(echo "$conf" | sed -e 's/[]\/$*.^|[]/\\&/g') # encode config file for sed
	conf=$(echo "$conf" | sed -e 's/$/\\/g') # escape newlines
	sudo sed -i "s/<\/VirtualHost>/$conf \n<\/VirtualHost>/" /etc/apache2/sites-enabled/000-default.conf
fi

# default-ssl
if ! grep -q "ProxyPass /whatif/" /etc/apache2/sites-enabled/default-ssl.conf
then
	sudo sed -i "s/<VirtualHost _default_:443>/<VirtualHost _default_:443>\n\t\tServerName $hostname/" /etc/apache2/sites-enabled/default-ssl.conf
	conf="	# Allow long GET requests for what-if
		LimitRequestLine 64000

		ProxyPreserveHost On
		ProxyPassReverseCookiePath /whatif /whatif

		RewriteEngine  On
		RewriteRule     ^/$             /whatif/      [R]
		RewriteRule     ^/whatif$       /whatif/      [R]

		ProxyPass /whatif/ ajp://localhost:8009/whatif/
		ProxyPassReverse /whatif/ ajp://localhost:8009/whatif/

		ProxyPass /aurin-wif/ ajp://localhost:8009/aurin-wif/
		ProxyPassReverse /aurin-wif/ ajp://localhost:8009/aurin-wif/

		# HTTP proxy as AJP doesn't have sufficiently large GET request support
		ProxyPass /geoserver http://localhost:8082/geoserver
		ProxyPassReverse /geoserver http://localhost:8082/geoserver

		ProxyPass /workbenchauth ajp://localhost:8009/workbenchauth
		ProxyPassReverse /workbenchauth ajp://localhost:8009/workbenchauth"
	conf=$(echo "$conf" | sed -e 's/[]\/$*.^|[]/\\&/g') # encode config file for sed
	conf=$(echo "$conf" | sed -e 's/$/\\/g') # escape newlines
	sudo sed -i "s/<\/VirtualHost>/${conf} \n\t<\/VirtualHost>/" /etc/apache2/sites-enabled/default-ssl.conf
fi

# apache2.conf
if ! grep -q "Servername" /etc/apache2/apache2.conf
then
	sudo sed -i "1iServername ${hostname}" /etc/apache2/apache2.conf
fi

# Configure tomcat, enabling AJP connector
sudo perl -0777 -i -pe 's/    <!-- Define an AJP 1.3 Connector on port 8009 -->\n    <!--\n    <Connector port="8009" protocol="AJP\/1.3" redirectPort="8443" \/>\n    -->/    <!-- Define an AJP 1.3 Connector on port 8009 -->\n    <Connector port="8009" protocol="AJP\/1.3" redirectPort="8443" \/>/' /etc/tomcat7/server.xml

# create whatif configuration file
if [ ! -e /etc/aurin/whatif-combined.properties ]
then
	sudo mkdir -p /etc/aurin
	sudo bash -c "cat > /etc/aurin/whatif-combined.properties << EOF
# wif.geo
wif.geo.db.password=$pg_pass
wif.geo.db.port=5432
wif.geo.db.user=$pg_user
wif.geo.db.type=postgis
wif.geo.db.name=$database
wif.geo.db.validateConnection=true
wif.geo.db.host=localhost
wif.geo.db.schema=$schema
#datasource
wif.datasource.driverclassname=org.postgresql.Driver
wif.datasource.url=jdbc:postgresql://localhost/$database
wif.datasource.username=$pg_user
wif.datasource.password=$pg_pass
#geoserver
wif.geoserver.workspace=$workspace
wif.geoserver.storeName=$datastore
geoserver.rest-url=https://$hostname/geoserver/
geoserver.proxy-url=https://$hostname/geoserver/
geoserver.restpub-url=https://$hostname/geoserver/
wif.config.serverWMSURL=https://$hostname/geoserver/
wif.config.restClientTargetURL=https://$hostname/geoserver/
#geoserverPublisher
geoserver.username=admin
geoserver.password=$geoserver_master_pw
#couchdb
wif.couchdb.repoURL=http://localhost:5984
wif.couchdb.wifDB=dev-whatif
wif.couchdb.login=whatif
wif.couchdb.password=
wif.couchdb.loginRequired=false
#ui.appBase/restURL
wif.ui.appBase=https://$hostname/whatif/
wif.ui.endpoint=https://$hostname/aurin-wif/
wif.ui.authURL=https://$hostname/workbenchauth/
wif.ui.authpub_URL=https://$hostname/workbenchauth/
wif.ui.useTrustingHttpClient=true
wif.ui.trusted.hosts=\"$hostname\",\"192.168.56.101\",\"localhost\",\"127.0.0.1\"
wif.ui.trusted.paths=\"/node\",\"/geoinfo\",\"/data_registration/datasets\",\"/data_registration/dataset\",\"/dataregistration/datasets\",\"/datastore\",\"/aurin-data-provider\",\"/dataprovider\",\"/mservices\",\"/middleware\",\"/R/call\",\"/workflow-api\",\"/ui\",\"/aurin-wif\",\"/what-if\",\"/temp\",\"/whatif\"
#other
wif.config.productionLevel=false
wif.config.testAllocationArea=true
wif.config.standAlone=true
aurin.dir=/etc/aurin
#these are presently unused but must be present
dev=https://api-dev.aurin.org.au
wif.ui.mservices=https://api-dev.aurin.org.au/middleware/
wif.config.middlewareService=https://api-dev.aurin.org.au/middleware/
aurin.data-store-service.uri=https://api-dev.aurin.org.au/data_store/
# printing - deprecated but required for now
wif.ui.mapprintURL=http://localhost:3000/print/
EOF"
fi

# XXX need to check for db presence first
if ! sudo -u postgres psql -lqt | cut -d \| -f 1 | grep -w "$auth_db" > /dev/null
then
	sudo -u postgres createuser -S -D -R $auth_user
	sudo -u postgres createdb -O $auth_user $auth_db
	sudo -u postgres psql -q $auth_db << EOF
ALTER USER $auth_user WITH PASSWORD '$auth_pass';
create extension postgis;
\q
EOF
	sudo -u postgres psql $auth_db -f ../db/structure.sql
	sed "s/##user##/$user1/; s/##hostname##/$hostname/" ../db/users_osgeo.sql | sudo -u postgres psql $auth_db
fi
	
# Install the email dependencies for the Workbenchauth authentication system
function install_email_deps {
	# Set up SMTP daemon for sending email to new users
	if ! dpkg -s postfix 2>/dev/null |grep Status |grep -q installed
	then
		sudo bash -c "debconf-set-selections <<< \"postfix postfix/mailname string $hostname\"
		debconf-set-selections <<< \"postfix postfix/main_mailer_type string 'Internet Site'\""
		sudo apt-get install -y postfix
	fi
	#XXX need to make it not prompt about self-signed cert
	if ! dpkg -s dovecot-core 2>/dev/null |grep Status |grep -q installed
	then
		sudo bash -c "debconf-set-selections <<< \"dovecot-core dovecot-core/create-ssl-cert boolean true\"
		debconf-set-selections <<< \"dovecot-core dovecot-core/ssl-cert-name string $hostname\"
		debconf-set-selections <<< \"ssl-cert make-ssl-cert/hostname string $hostname\""
		sudo apt-get install -y dovecot-core
	fi
	
	# Enable the submission service
	if ! grep -q "^submission" /etc/postfix/master.cf
	then
		conf="submission     inet  n       -       -       -       -       smtpd
  -o syslog_name=postfix/smtps
  -o smtpd_tls_wrappermode=no
  -o smtpd_sasl_auth_enable=yes
  -o smtpd_client_restrictions=permit_sasl_authenticated,reject
  -o milter_macro_daemon_name=ORIGINATING
  -o smtpd_reject_unlisted_sender=yes"
		conf=$(echo "$conf" | sed -e 's/[]\/$*.^|[]/\\&/g') # encode config file for sed
		conf=$(echo "$conf" | sed -e 's/$/\\/g') # escape newlines
		sudo sed -i "s/#submission/${conf} \n#submission/" /etc/postfix/master.cf
	fi
	
	#Set up dovecot to allow user authentication within postfix.
	if ! grep -q "[^#]unix_listener /var/spool/postfix/private/auth" /etc/dovecot/conf.d/10-master.conf
	then
		conf="  # Postfix smtp-auth
  unix_listener /var/spool/postfix/private/auth {
    mode = 0660
    user = postfix
    group = postfix
  }"
		conf=$(echo "$conf" | sed -e 's/[]\/$*.^|[]/\\&/g') # encode config file for sed
		conf=$(echo "$conf" | sed -e 's/$/\\/g') # escape newlines
		sudo sed -i "s/  # Auth process is run as this user./${conf} \n  # Auth process is run as this user./" /etc/dovecot/conf.d/10-master.conf
	fi
	
	# Set up email auth
	sudo sed -i "s/#disable_plaintext_auth = yes/disable_plaintext_auth = yes/" /etc/dovecot/conf.d/10-auth.conf
	sudo sed -i "s/#auth_username_format = %Lu/auth_username_format = %n/" /etc/dovecot/conf.d/10-auth.conf
	sudo sed -i "s/^auth_mechanisms.*/auth_mechanisms = plain/" /etc/dovecot/conf.d/10-auth.conf
	
	# Connect postfix and dovecot
	if ! grep -q "smtpd_sasl_auth_enable = yes" /etc/postfix/main.cf
	then
		conf="smtpd_sasl_auth_enable = yes
smtpd_sasl_type = dovecot
smtpd_sasl_path = private/auth
smtpd_sasl_authenticated_header = no
smtpd_sasl_security_options = noanonymous
smtpd_sasl_local_domain =
broken_sasl_auth_clients = yes"
		conf=$(echo "$conf" | sed -e 's/[]\/$*.^|[]/\\&/g') # encode config file for sed
		conf=$(echo "$conf" | sed -e 's/$/\\/g') # escape newlines
		sudo sed -i "s/inet_protocols = all/inet_protocols = all\n${conf} /" /etc/postfix/main.cf
	fi
	
	# Create a linux user to send/receive email:
	if ! getent passwd $email_user 2>&1 >/dev/null
	then
		sudo adduser $email_user --gecos "User Administration" --disabled-password
		sudo bash -c "chpasswd << END
$email_user:$email_pass
END"
	fi
}

# install email dependencies for Workbenchauth if requested
if [[ email_deps -eq 1 ]]
then
	install_email_deps
fi

#XXX Create whatif users

#Create config file for auth system
if [ ! -e /etc/aurin/envision-combined.properties ]
then
	sudo bash -c "cat > /etc/aurin/envision-combined.properties << EOF
#Properties
env.ui.authURL=http://localhost/workbenchauth/
env.ui.authpub_URL=https://localhost/workbenchauth/
env.auth.adminUsername=aurin
env.auth.adminPassword=demoPassword

env.geo.db.host=localhost
env.geo.db.name=$auth_db
env.geo.db.port=5432
env.geo.db.user=$auth_user
env.geo.db.password=$auth_pass
env.geo.db.type=postgis
env.geo.db.validateConnection=true
env.geo.db.schema=susip

env.datasource.driverclassname=org.postgresql.Driver
env.datasource.url=jdbc:postgresql://localhost/$auth_db
env.datasource.username=$auth_user
env.datasource.password=$auth_pass

#other
aurin.dir=/etc/aurin

#email
env.mail.host=localhost
env.mail.port=587
env.mail.starttls=false
env.mail.auth=true
env.mail.username=$email_user
env.mail.password=$email_pass
env.mail.from=$email_user@$hostname
env.mail.url=https://$hostname/workbenchauth/
EOF"
fi

# Build and deploy the war files
function build_war_files {
	# if there are any missing package dependencies, install them
	packages=""
	for package in git tig maven default-jdk
	do
		if ! dpkg -s $package 2>/dev/null |grep Status |grep -q installed
	        then
	                packages="${packages} ${package}"
	        fi
	done
	packages=`echo $packages | awk '{gsub(/^ +| +$/,"")} {print $0}'` # trim leading and trailing whitespace
	if [[ $packages != "" ]]
	then
		sudo apt-get install -y $packages
	fi

	# Clone the source and build
	sudo -u $user1 mkdir -p dependencies && cd dependencies
	if [ ! -e online-whatif-ui ]
	then
		sudo -u $user1 git clone https://github.com/AURIN/online-whatif-ui.git
	fi
	if [ ! -e workbenchauth ]
	then
		sudo -u $user1 git clone https://github.com/AURIN/workbenchauth.git
	fi
	export AURIN_DIR="/etc/aurin"
	export JAVA_HOME=/usr/lib/jvm/java-7-openjdk-amd64/jre
	if [ ! -f workbenchauth/target/workbenchauth-1.0.0.war ]
	then
		cd workbenchauth
		sudo -u $user1 mvn clean package -Ddeployment=development -Dsystem=ali-dev -Daurin.dir=$AURIN_DIR
		cd ..
	fi
	if [ ! -f online-whatif-ui/target/whatif-1.0.war ]
	then
		cd online-whatif-ui
		sudo -u $user1 mvn clean package -Ddeployment=development -Dsystem=ali-dev -Daurin.dir=$AURIN_DIR
		cd ..
	fi
	if [ ! -f ${script_dir}/../target/aurin-wif-1.0.war ]
	then
		cd "$script_dir/.."
		sudo -u $user1 mvn clean package -Ddeployment=development -Dsystem=ali-dev -Daurin.dir=$AURIN_DIR
		cd "$script_dir"
	fi

	# Deploy the war files
	cd "$script_dir"
	sudo cp dependencies/workbenchauth/target/workbenchauth-1.0.0.war /var/lib/tomcat7/webapps/workbenchauth.war
	sudo cp dependencies/online-whatif-ui/target/whatif-1.0.war /var/lib/tomcat7/webapps/whatif.war
	sudo cp ../target/aurin-wif-1.0.war /var/lib/tomcat7/webapps/aurin-wif.war
}

if [[ build_source -eq 1 ]]
then
	build_war_files
else
	# download and install war files from github release tag
	sudo -u $user1 mkdir -p "$script_dir"/download
	for file in aurin-wif-1.0.war whatif-1.0.war workbenchauth-1.0.0.war
	do
		if [[ ! -f "$script_dir/download/$file" ]]
		then
			sudo -u $user1 curl -s -o "$script_dir/download/$file" "https://github.com/AURIN/online-whatif/releases/download/$github_release/$file"
		fi
	done

	sudo cp "$script_dir"/download/workbenchauth-1.0.0.war /var/lib/tomcat7/webapps/workbenchauth.war
	sudo cp "$script_dir"/download/whatif-1.0.war /var/lib/tomcat7/webapps/whatif.war
	sudo cp "$script_dir"/download/aurin-wif-1.0.war /var/lib/tomcat7/webapps/aurin-wif.war
fi

# Ensure that the self-signed SSL certificate is trusted by java
sudo "$script_dir"/refresh-java-keystore.sh

# Add the password to the passwords.txt file
if ! grep -q OnlineWhatIf /home/$user1/Desktop/passwords.txt
then
	sed -i '/^\s*$/d' /home/$user1/Desktop/passwords.txt # delete blank lines
	echo "OnlineWhatIf,$user1@$hostname,aurin" >> /home/$user1/Desktop/passwords.txt
fi

# Add icon to the main menu
if [[ ! -f /usr/local/share/applications/osgeo-onlinewhatif.desktop ]]
then
	cp "$script_dir"/../resources/AURIN_Workbench.png /usr/local/share/icons/
	cp "$script_dir"/../resources/OnlineWhatIf-{start,stop}.desktop /usr/local/share/applications/
	cp "$script_dir"/../resources/osgeo-onlinewhatif.desktop /usr/local/share/applications/
	sed -i "s/##hostname##/$hostname/" /usr/local/share/applications/OnlineWhatIf-{start,stop}.desktop
	sed -i "s/##hostname##/$hostname/" /usr/local/share/applications/osgeo-onlinewhatif.desktop
	cp "$script_dir"/../resources/OnlineWhatIf-{start,stop}.sh /usr/local/bin/
	sed -i "s/##hostname##/$hostname/" /usr/local/bin/OnlineWhatIf-start.sh
	cp "$script_dir"/../resources/OnlineWhatIf.menu /etc/xdg/menus/applications-merged/
	cp "$script_dir"/../resources/OnlineWhatIf.directory /usr/share/desktop-directories/
	lxpanelctl restart
fi

# Restart relevant services
services="dovecot postfix postgresql couchdb tomcat7 apache2"
for service in $services
do
	if dpkg -l |awk '{ print $2 }' |grep "^${service}" > /dev/null
	then
		sudo service $service restart
	fi
done
