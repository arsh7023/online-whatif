# Installing Online WhatIf

It is assumed that you are proficient with Linux, including installing custom server softare.  This document will guide you through installing all of the required components on a single virtual machine.

## Service dependencies

We run WhatIf on the following infrustructure, which means it's well tested and known to run well on it.  If you know what you're doing, you can of course swap out any of these pieces for their equivalents.
* A Linux OS, currently we test on Ubuntu 14.04 server but most other current distributions should work.
* PostgreSQL 9.1 or higher with PostGIS extentions
* Java and Tomcat application server
* CouchDB 1.2 or higher
* GeoServer 2.2 or higher
* Apache HTTPD
* [Workbenchauth](https://github.com/AURIN/workbenchauth)
* Postfix MTA

A valid SSL certificate and matching domain, while not required, make things less confusing for users as they won't have to click past any browser warnings

## Basic configuration

Install and setup the OS.  Make sure that you can access port 22, as we'll be using SSH to install and configure.

### Installing Ubuntu from ISO

If you're installing on a cloud instance, skip ahead to the next section.  If you're installing on VirtualBox or similar, make sure you have a way to ssh to the machine.  On VirtualBox a host-only network will work. First, add a new host-only network:

* File -> Preferences -> Network -> Host-only Networks -> Add host only network

Then attach your VM to it

* Right click on your VM -> Settings -> Network -> Adapter 2 -> Enable Network Adapter, Attached to: Host-only Adapter

Download and install from the ubuntu-server iso.  When installing Ubuntu from an iso file, set things up like this:

* Configure the location and keyboard settings to your liking.
* Set a host name (I'll use "whatif-demo" for demonstration purposes).
* Add a new user of your choice (I'll use "whatif" for demonstration purposes) and set a password.
* Continue with the default choices; I don't use LVM, and choose to automatically install security updates, but these choices shouldn't matter too much.
* At the Software Selection screen, make sure you select the OpenSSH server for installation.
* If you're on VirtualBox, set up the Host-only network by logging in on the console and adding an entry to /etc/network/interfaces:

	auto eth1
	iface eth1 inet dhcp

Then bring up the interface:

	sudo ifup eth1
	ifconfig # note the IP address assigned to eth1

### Installing on a cloud instance

If you're installing on a cloud instace, ssh in as the ubuntu user, then add the whatif user:

	sudo adduser whatif
	sudo adduser whatif adm
	sudo adduser whatif sudo

### Common installation steps

#### SSH hardening

Optionally, you can set up public key logins on your host.  For testing purposes this shouldn't be necessary, so you can safely skip this, but it's good practice on a server that's exposed to the internet.

On your local machine, create a new ssh public/private key pair (if you don't have one).  These instructions are for linux, but you can do something similar for [PuTTY on Windows](https://www.howtoforge.com/how-to-configure-ssh-keys-authentication-with-putty-and-linux-server-in-5-quick-steps).

	ssh-keygen # save the key in the default location and set a password on it
	cat ~/.ssh/id_rsa.pub # this is the public key text that you'll have to copy into the authorized_keys file on the host (see below)

Copy the public key to the host

	sudo su -
	cd /home/whatif
	mkdir -p .ssh && cd .ssh
	vim authorized_keys # copy your SSH public key into this file
	cd ..
	chown -R whatif:whatif .ssh
	chmod go-rwx .ssh
	vim /etc/ssh/sshd_config # disable password login by setting "PasswordAuthentication no"
	service ssh restart

#### Install OS upgrades, and restart the new instance

	apt-get update && apt-get dist-upgrade -y
	shutdown -r now

## Domain name resolution

In order for the system to work, you'll need some way for the various components to find and talk to each other.  This can be achieved in one of a few ways:

* Using hosts files
* Using IP addresses throughout
* Using DNS entries

Using hosts files is the approach we will use in this guide.  It is fragile in the instance of IP address changes however, as all hosts files need to be updated.  Using IP addresses throughout is the simplest approach, but is rather ugly as it involves typing IP address numbers everywhere.  Using DNS entries is probably best suited to a more permanent infrastructure.

So, you need to add the IP address to the [hosts file](https://en.wikipedia.org/wiki/Hosts\_%28file%29) on your desktop computer.  In our example case, the IP address is 192.168.56.101, so my hosts file now looks like this:

	127.0.0.1	localhost
	192.168.56.101	whatif-demo

We should add the same whatif-demo entry to the hosts file on the virtual machine as well.  This will allow us to access the system through the url [https://whatif-demo/](https://whatif-demo/).  If you're planning to use a different URL, you'll have to update the config files in /etc/aurin to reflect this.

## Dependencies

### Install distribution packages for dependencies

Now that we have a fully updated ubuntu machine, let's get started installing stuff.

	sudo apt-get install tomcat7 postgresql postgis postgresql-9.3-postgis-2.1 couchdb apache2 unzip
	cd /usr/local/bin && sudo ln -s /usr/bin/pgsql2shp # whatif expects /usr/local/bin/pgsql2shp
	sudo vim /etc/default/tomcat7

Update the JAVA\_OPTS line:

	JAVA_OPTS="-Djava.awt.headless=true -XX:+UseConcMarkSweepGC -Dfile.encoding=UTF-8 -Xmx2G -XX:PermSize=512M -Daurin.dir=/etc/aurin"

### Bind couchdb to the public interface

We only need to do this so we can access the web interface.  You'll probably want to firewall this appropriately.

	sudo vim /etc/couchdb/default.ini

Update the bind\_address:

	[httpd]
	bind_address = 0.0.0.0

Restart:

	sudo service couchdb restart

Now set up an admin user by logging in to the web interface at [http://whatif-demo:5984/\_utils/](http://whatif-demo:5984/_utils/).  For this demo, I'll use the username "whatif".  Take a note of the password you set.  Create a new database by clicking "Create Database..."; for this demo I'm going to call it dev-whatif.


### Create the whatif-development database with wifdemo schema

	sudo -u postgres -i
	createuser -S -D -R -P whatif # create and note down a password for database access
	createdb -O whatif whatif-development
	psql whatif-development

	create extension postgis;
	ALTER USER whatif WITH SUPERUSER;
	CREATE SCHEMA IF NOT EXISTS wifdemo AUTHORIZATION whatif;
	\q

	exit

### Deploy geoserver on Tomcat

Update the links below to get the latest stable version from http://geoserver.org/

	cd
	wget -O geoserver-2.7.1-war.zip "http://downloads.sourceforge.net/project/geoserver/GeoServer/2.7.1/geoserver-2.7.1-war.zip?r=http%3A%2F%2Fgeoserver.org%2Frelease%2Fstable%2F&ts=1430895622&use_mirror=aarnet"
	mkdir geoserver-2.7.1-war && cd geoserver-2.7.1-war
	unzip ../geoserver-2.7.1-war.zip
	sudo mv geoserver.war /var/lib/tomcat7/webapps/
	sudo chown root:root /var/lib/tomcat7/webapps/geoserver.war 
	cd ..
	rm -r geoserver-2.7.1-war

### Configure the geoserver through its web interface

Visit [http://whatif-demo:8080/geoserver/](http://whatif-demo:8080/geoserver/) in a browser. Log in as "admin" with default password "geoserver". Change the admin password by going to Security->Users/Groups/Roles, Users/Groups tab, and clicking on the admin user. Set the password and click save. This updates the following file:

	/var/lib/tomcat7/webapps/geoserver/data/security/usergroup/default/users.xml

* Create a new Workspace called "whatif" with the namespace URI "https://whatif-demo/whatif"
* Create a new Store called "whatifStore":
	* Datastore Type: PostGIS
	* Workspace: whatif
	* Host: localhost
	* Port: 5432
	* Database: whatif-development
	* Schema: wifdemo
	* User: whatif
	* Password: (password from above)
	* Expose Primary Keys: unticked
	* Estimated Extends: unticked
	* Support on the fly geometry simplification: unticked

Other options are left at default settings

### Configure apache reverse proxy

	sudo a2ensite default-ssl
	sudo a2enmod proxy_ajp
	sudo a2enmod proxy_http
	sudo a2enmod ssl
	sudo a2enmod rewrite
	cd /etc/apache2/sites-enabled/

proxy for http:

	sudo vim 000-default.conf

Add the following lines:

	ServerName whatif-demo
	
	ProxyPreserveHost On

       	RewriteEngine  On
       	RewriteRule     ^/$             https://whatif-demo/whatif/      [R]
	
       	ProxyPass /geoserver ajp://localhost:8009/geoserver
       	ProxyPassReverse /geoserver ajp://localhost:8009/geoserver
	
       	# Allow long GET requests for what-if
       	LimitRequestLine 21000

proxy for https:

	sudo vim default-ssl.conf

Add the following lines:

	ServerName whatif-demo
	
       	# Allow long GET requests for what-if
       	LimitRequestLine 21000
	
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
       	ProxyPass /geoserver http://localhost:8009/geoserver
       	ProxyPassReverse /geoserver http://localhost:8009/geoserver
	
       	ProxyPass /workbenchauth ajp://localhost:8009/workbenchauth
       	ProxyPassReverse /workbenchauth ajp://localhost:8009/workbenchauth

When restarting Apache, the following warning can safely be ignored:

	whatif@whatif-demo:~$ sudo service apache2 restart
	 * Restarting web server apache2
        AH00558: apache2: Could not reliably determine the server's fully qualified domain name, using 127.0.1.1. Set the 'ServerName' directive globally to suppress this message

However, it can be suppressed by adding the following line to /etc/apache2/apache2.conf:

	ServerName whatif-demo

### Optional: Set up a valid SSL certificate

	A valid SSL certificate will allow you to avoid SSL certificate warnings in your browser.  WhatIf will work without this for testing purposes.  By default, ubuntu installations of Apache2 will provide a self-signed certificate.

	sudo vim /etc/ssl/certs/aurin.crt # substitute this with your SSL certificate (if you have one)
	sudo vim /etc/ssl/certs/aurin_chain.crt # substitute this with your SSL chain file (if you have one)
	sudo vim /etc/ssl/private/aurin.key # substitute this with your SSL key (if you have one)
	sudo chmod go-rwx /etc/ssl/private/aurin.key
	sudo vim /etc/apache2/sites-enabled/default-ssl # update the SSLCertificateFile, SSLCertificateKeyFile and SSLCertificateChainFile entries to point to the new files
	sudo service apache2 restart

### A note on Tomcat and Self Signed Certificates

If you're relying on self-signed certificates, some care is necessary in order to make sure that all of the components trust the certificate that you create.  If you follow the installation instructions in this document, this should all be taken care of for you, and you can safely skip this section.  If you need to generate your own self-signed certificate then this section might be useful to you.

To generate a self-signed certificate, refer to the [Ubuntu Server Guide](https://help.ubuntu.com/lts/serverguide/certificates-and-security.html), and keep these points in mind:

* The Common Name (CN) in the certificate must match the server's DNS name.  In our example this is: whatif-demo.
* Make sure that you save your public key in /etc/ssl/certs with a .pem file extension.

Once this is done, update the /etc/apache2/sites-enabled/default-ssl.conf to point to your new certificate:

	SSLCertificateFile      /etc/ssl/certs/ssl-cert-snakeoil.pem
	SSLCertificateKeyFile /etc/ssl/private/ssl-cert-snakeoil.key

Next we need to import the public key into the java keystore.  This happens automatically the first time you install the ca-certificates-java package (when you install java).  To the java keystore to include your new public key, run the refresh-java-keystore.sh script from the utils directory:

	sudo refresh-java-keystore.sh

Possible reasons you might want to generate your own public key:

* If whatif can't authenticate against the workbenchauth service
* If your server's hostname (/etc/hostname) does not match the DNS name that you're using

Here's an example of the error message you'd see in the tomcat log (/var/log/tomcat7/catalina.out) if the java keystore does not contain your SSL public key:

	15:39:09.147 [ajp-bio-8009-exec-1] INFO  a.o.aurin.dispatcher.RestController - org.springframework.web.client.ResourceAccessException: I/O error: sun.security.validator.ValidatorException: PKIX path building failed: sun.security.provider.certpath.SunCertPathBuilderException: unable to find valid certification path to requested target; nested exception is javax.net.ssl.SSLHandshakeException: sun.security.validator.ValidatorException: PKIX path building failed: sun.security.provider.certpath.SunCertPathBuilderException: unable to find valid certification path to requested target

In this case, running the above-mentioned refresh-java-keystore.sh script and restarting tomcat should fix the problem, e.g.:

	root@whatif-demo:/home/whatif/wd/online-whatif/utils# ./refresh-java-keystore.sh 
	Replacing debian:Starfield_Class_2_CA.pem
	Replacing debian:Juur-SK.pem
	[snip]
	Replacing debian:Verisign_Class_2_Public_Primary_Certification_Authority_-_G2.pem
	Replacing debian:TC_TrustCenter_Class_3_CA_II.pem
	Adding debian:whatif-demo.pem
	Replacing debian:GlobalSign_Root_CA.pem
	Replacing debian:Thawte_Premium_Server_CA.pem
	[snip]
	done.

### Configure Tomcat

	sudo vim /etc/tomcat7/server.xml 

Enable ajp connector:

	<Connector port="8009" protocol="AJP/1.3" redirectPort="8443" />

Restart:

	service tomcat7 restart

### Create WhatIf configuration file

	sudo mkdir -p /etc/aurin
	sudo vim /etc/aurin/whatif-combined.properties

An example configuration file follows. Fill in the passwords that you set previously.

	# wif.geo
	wif.geo.db.password=(password)
	wif.geo.db.port=5432
	wif.geo.db.user=whatif
	wif.geo.db.type=postgis
	wif.geo.db.name=whatif-development
	wif.geo.db.validateConnection=true
	wif.geo.db.host=localhost
	wif.geo.db.schema=wifdemo
	#datasource
	wif.datasource.driverclassname=org.postgresql.Driver
	wif.datasource.url=jdbc:postgresql://localhost/whatif-development
	wif.datasource.username=whatif
	wif.datasource.password=(password)
	#geoserver
	wif.geoserver.workspace=whatif
	wif.geoserver.storeName=whatifStore
	geoserver.rest-url=https://whatif-demo/geoserver/
	geoserver.proxy-url=https://whatif-demo/geoserver/
	geoserver.restpub-url=https://whatif-demo/geoserver/
	wif.config.serverWMSURL=https://whatif-demo/geoserver/
	wif.config.restClientTargetURL=https://whatif-demo/geoserver/
	#geoserverPublisher
	geoserver.username=admin
	geoserver.password=(password)
	#couchdb
	wif.couchdb.repoURL=http://localhost:5984
	wif.couchdb.wifDB=dev-whatif
	wif.couchdb.login=whatif
	wif.couchdb.password=(password)
	wif.couchdb.loginRequired=true
	#ui.appBase/restURL
	wif.ui.appBase=https://whatif-demo/whatif/
	wif.ui.endpoint=https://whatif-demo/aurin-wif/
	wif.ui.authURL=https://whatif-demo/workbenchauth/
	wif.ui.authpub_URL=https://whatif-demo/workbenchauth/
	wif.ui.useTrustingHttpClient=true
	wif.ui.trusted.hosts="whatif-demo","192.168.56.101","localhost","127.0.0.1"
	wif.ui.trusted.paths="/node","/geoinfo","/data_registration/datasets","/data_registration/dataset","/dataregistration/datasets","/datastore","/aurin-data-provider","/dataprovider","/mservices","/middleware","/R/call","/workflow-api","/ui","/aurin-wif","/what-if","/temp","/whatif"
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

## Install and configure authentication system

WhatIf uses a separate RESTful authentication service.  This is made up of a
postgresql database, a java web application, and requires an SMTP daemon in
order to send emails to new users.

### Create the database

Create a new user for auth database, create envision DB and spatially enable the database.

	sudo -u postgres createuser -S -D -R -P envision
	sudo -u postgres createdb -O envision envisiondb
	sudo -u postgres psql envisiondb
	create extension postgis;
	\q
	sudo -u postgres psql envisiondb -f db/structure.sql

Note down the password for the envision database user.

### Set up an SMTP daemon (if necessary)

If you already have an SMTP daemon that you can use, you can skip this section and just configure it in envision-combined.properties, below.  Otherwise, we're going to install postfix; you can of course install another different server if you'd prefer.

	sudo apt-get install postfix dovecot-core

Set up postfix as internet site, and set the system mail name to whatever DNS name you're using for the machine.  In our demo we're using "whatif-demo".  Install a self-signed certificate in dovecot.

Enable unencrypted SMTP on port 587 for testing.  This assumes that we're going to be submitting email only the local machine so we can skip SSL support for now.

	sudo vim /etc/postfix/master.cf

Enable the submission service:

	submission     inet  n       -       -       -       -       smtpd
	  -o syslog_name=postfix/smtps
	  -o smtpd_tls_wrappermode=no
	  -o smtpd_sasl_auth_enable=yes
	  -o smtpd_client_restrictions=permit_sasl_authenticated,reject
	  -o milter_macro_daemon_name=ORIGINATING
	  -o smtpd_reject_unlisted_sender=yes

Set up dovecot to allow user authentication within postfix.

	sudo vim /etc/dovecot/conf.d/10-master.conf

Add a new unix\_listener within the service auth section:

	# Postfix smtp-auth
	unix_listener /var/spool/postfix/private/auth {
	  mode = 0660
	  user = postfix
	  group = postfix
	}


	sudo vim /etc/dovecot/conf.d/10-auth.conf

Set up the auth:

	disable_plaintext_auth = yes
	auth_username_format = %n
	auth_mechanisms = plain


Connect postfix and dovecot:

	sudo vim /etc/postfix/main.cf


	smtpd_sasl_auth_enable = yes
	smtpd_sasl_type = dovecot
	smtpd_sasl_path = private/auth
	smtpd_sasl_authenticated_header = no
	smtpd_sasl_security_options = noanonymous
	smtpd_sasl_local_domain =
	broken_sasl_auth_clients = yes

Restart the services:

	sudo service postfix restart
	sudo service dovecot restart

Create a linux user to send/receive email:

	sudo adduser admin

Give the user the full name of "User Administraton" or similar.  Note down the user's password.

### Create config file for auth system:

	sudo vim /etc/aurin/envision-combined.properties

Add the following contents, replacing the passwords with those you created above.

	#Properties
	env.ui.authURL=http://localhost/workbenchauth/
	env.ui.authpub_URL=https\://localhost/workbenchauth/
	env.auth.adminUsername=aurin
	env.auth.adminPassword=demoPassword
	
	env.geo.db.host=localhost
	env.geo.db.name=envisiondb
	env.geo.db.port=5432
	env.geo.db.user=envision
	env.geo.db.password=(password)
	env.geo.db.type=postgis
	env.geo.db.validateConnection=true
	env.geo.db.schema=susip
	
	env.datasource.driverclassname=org.postgresql.Driver
	env.datasource.url=jdbc:postgresql://localhost/envisiondb
	env.datasource.username=envision
	env.datasource.password=(password)
	
	#other
	aurin.dir=/etc/aurin

	#email
	env.mail.host=localhost
	env.mail.port=587
	env.mail.starttls=false
	env.mail.auth=true
	env.mail.username=admin
	env.mail.password=(password)
	env.mail.from=admin@whatif-demo
	env.mail.url=https://whatif-demo/workbenchauth/

We need to install the war file for the auth system before we can use it, so continue on with the rest of the instructions and we'll create users later.

## Build and deploy the war files

This can be done on a workstation and the war files copied to the server.

### Install the build dependencies

	sudo apt-get install git tig maven default-jdk

### Authorize the machine on github

Allow the dev machine to be able to talk to github account.  If you're cloning from the https:// style url, you can probably skip this

	ssh-keygen
	cat .ssh/id_rsa.pub # add to github acct

### Clone the source and build

	mkdir wd && cd wd
	git clone git@github.com:AURIN/workbenchauth.git
	git clone git@github.com:AURIN/online-whatif.git
	git clone git@github.com:AURIN/online-whatif-ui.git
	cd workbenchauth
	export AURIN_DIR="/etc/aurin"
	export JAVA_HOME=/usr/lib/jvm/java-7-openjdk-amd64/jre
	mvn clean package -Ddeployment=development -Dsystem=ali-dev -Daurin.dir=$AURIN_DIR
	cd ../online-whatif
	mvn clean package -Ddeployment=development -Dsystem=ali-dev -Daurin.dir=$AURIN_DIR
	cd ../online-whatif-ui
	mvn clean package -Ddeployment=development -Dsystem=ali-dev -Daurin.dir=$AURIN_DIR

If you have problem with the tests you can use -Dmaven.test.skip=true.

### Deploy the war files

If you're building on a different machine, copy these to the what-if machine first, then deploy them in the Tomcat application container:

	sudo cp ~/wd/workbenchauth/target/workbenchauth-1.0.0.war /var/lib/tomcat7/webapps/workbenchauth.war
	sudo cp ~/wd/online-whatif/target/aurin-wif-1.0.war /var/lib/tomcat7/webapps/aurin-wif.war
	sudo cp ~/wd/online-whatif-ui/target/whatif-1.0.war /var/lib/tomcat7/webapps/whatif.war

Restart the services:

	sudo service apache2 restart
	sudo service tomcat7 restart

Check logs for errors

	tail -f /var/log/apache2/access.log /var/log/apache2/error.log
	tail -f /var/log/tomcat7/catalina.out

### Create users

Log in to the [user admin area](https://whatif-demo/workbenchauth/login), and create a user:

Login with the username and password from /etc/aurin/envision-combined.properties, defined by these values:
* env.auth.adminUsername
* env.auth.adminPassword

Click the "register" link and add a new user.  Ensure that you add the user to the organsiations that you want to use, Maroondah and Canning being the examples.  Add the user to the Whatif application and User role.  The user will be emailed a temporary password, it's probably best to reset this to something that is easier to remember (and that hasn't been emailed over the internet).

## Log in to WhatIf

You should now be able to [log in to whatif](https://whatif-demo/whatif/login).  Please see the User Manual for more information on how to use WhatIf.


## Local Development Setup

Note that this documentation is only required if you wish to contribute to WhatIf.  It may also be out of date.

### Javadocs

The WhatIf application has extensive Javadoc-based documentation available.  To build this, clone the online-whatif repository and import it into Eclipse (The Spring Tool Suite version of eclipse is probably best).  Then import:

* File -> Import...
* Maven -> Existing Maven Projects
* Next
* Browse... Navigate to the location where you checked out online-whatif with git.
* Finish

Now build the Javadocs:

* Project -> Generate Javadoc...
* Finish

### Testing

For testing whatif REST services we use the Rest Console plugin in Chrome and Add X-AURIN-USER-ID: aurin in Custom Headers. 
