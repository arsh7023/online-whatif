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

if [[ $EUID -ne 0 ]]; then
   echo "This script must be run as root" 1>&2
   exit 1
fi

apt-get update && apt-get dist-upgrade -y
