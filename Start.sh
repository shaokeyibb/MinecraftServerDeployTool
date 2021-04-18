#!/bin/bash
# Song-Xin233
export LANG=en_US.UTF-8
export LANGUAGE=en_US:en

# Install Java Start
if [ -f "/usr/bin/yum" ] && [ -f "/etc/yum.conf" ]; then
	sudo yum install -y java-1.8.0-openjdk-devel.x86_64 wget
elif [ -f "/usr/bin/apt-get" ] && [ -f "/usr/bin/dpkg" ]; then
	sudo apt-get install openjdk-8-jdk wget
fi
java -version
echo "Install Java End"
# Install Java End

# Download Project Start
mkdir NewServer && cd NewServer
wget -c https://github.com/shaokeyibb/MinecraftServerDeployTool/releases/download/2.0-SNAPSHOT/MinecraftServerDeployTool-2.0-SNAPSHOT-all.jar -O MinecraftServerDeployTool.jar
# Download Project End

# Run Project
java -jar ./MinecraftServerDeployTool.jar nogui
