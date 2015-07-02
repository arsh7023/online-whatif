#!/bin/bash
# Refresh the java keystore with all ssl certificates /etc/ssl/certs/*.pem
# based on the postinst script of the ca-certificates-java package

JAR=/usr/share/ca-certificates-java/ca-certificates-java.jar
storepass='changeit'

if [ -e /etc/ssl/certs/java/cacerts ]; then
    cp -f /etc/ssl/certs/java/cacerts /etc/ssl/certs/java/cacerts.dpkg-old
fi

find /etc/ssl/certs -name \*.pem | \
while read filename; do
alias=$(basename $filename .pem | tr A-Z a-z | tr -cs a-z0-9 _)
alias=${alias%*_}
    echo "-${alias}"
    echo "-${alias}_pem"
    echo "+${filename}"
done | \
java -jar $JAR -storepass "$storepass"
echo "done."
